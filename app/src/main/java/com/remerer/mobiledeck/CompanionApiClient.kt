package com.remerer.mobiledeck

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import android.content.Context
import android.os.Build
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import java.util.UUID
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import java.net.URI

data class CompanionApiResult(
    val ok: Boolean,
    val message: String,
    val data: JSONObject = JSONObject(),
    val errorCode: String? = null
)

internal data class CompanionRequestMetadata(
    val requestId: String,
    val pairingToken: String,
    val deviceId: String,
    val deviceName: String
)

class CompanionApiClient(context: Context) {
    private val appContext = context.applicationContext
    private val deviceId by lazy { mobileDeckDeviceId(appContext) }
    private val deviceName by lazy { mobileDeckDeviceName() }
    private val client = OkHttpClient.Builder()
        .connectTimeout(900, TimeUnit.MILLISECONDS)
        .readTimeout(2, TimeUnit.SECONDS)
        .writeTimeout(900, TimeUnit.MILLISECONDS)
        .build()

    suspend fun status(settings: CompanionSettings): CompanionApiResult {
        return send(settings, JSONObject().put("type", "status.ping"))
    }

    suspend fun open(settings: CompanionSettings, target: String, mode: String = "normal"): CompanionApiResult {
        return send(
            settings,
            JSONObject()
                .put("type", "open")
                .put("target", target)
                .put("mode", mode)
        )
    }

    suspend fun text(settings: CompanionSettings, value: String, delivery: String = "clipboard-paste"): CompanionApiResult {
        return send(
            settings,
            JSONObject()
                .put("type", "text")
                .put("value", value)
                .put("delivery", delivery)
        )
    }

    suspend fun inputCommand(settings: CompanionSettings, actionType: String, payload: String): CompanionApiResult {
        return send(
            settings,
            JSONObject()
                .put("type", "input.command")
                .put("actionType", actionType)
                .put("payload", payload)
        )
    }

    suspend fun programCommand(settings: CompanionSettings, payload: String): CompanionApiResult {
        val parsed = parseProgramPayload(payload)
        return send(
            settings,
            JSONObject()
                .put("type", "program.command")
                .put("programId", parsed.programId)
                .put("command", parsed.command)
                .put("args", parsed.args)
        )
    }

    suspend fun submitCodexJob(
        settings: CompanionSettings,
        binding: CodexButtonBindingPayload
    ): CodexJobApiResult {
        if (!settings.isConfigured()) return CodexJobApiResult(reconnectRequired = true)
        return sendCodexJobRequest(
            settings = settings,
            payloadFactory = { metadata -> buildCodexSubmitRequestJson(metadata, binding) },
            responseProjector = { ok, errorCode, dataJson ->
                projectCodexSubmitResponse(ok, errorCode, dataJson, binding)
            }
        )
    }

    suspend fun codexJobStatus(
        settings: CompanionSettings,
        jobId: String,
        bindingId: String
    ): CodexJobApiResult {
        if (!settings.isConfigured()) return CodexJobApiResult(reconnectRequired = true)
        if (!CompanionReleaseRoutePolicy.allowsCodexStatus(settings, jobId, bindingId)) {
            return CodexJobApiResult(failureCode = "invalid_request")
        }
        return sendCodexJobRequest(
            settings = settings,
            payloadFactory = { metadata -> buildCodexStatusRequestJson(metadata, jobId, bindingId) },
            responseProjector = { ok, errorCode, dataJson ->
                projectCodexStatusResponse(ok, errorCode, dataJson, jobId, bindingId)
            }
        )
    }

    suspend fun controlUpdate(settings: CompanionSettings, source: String, value: Any): CompanionApiResult {
        return send(
            settings,
            JSONObject()
                .put("type", "companion.control.update")
                .put("source", source)
                .put("value", value)
        )
    }

    suspend fun updateMobileDeckBundle(settings: CompanionSettings, bundle: JSONObject): CompanionApiResult {
        return send(
            settings,
            JSONObject()
                .put("type", "mobiledeck.bundle.update")
                .put("bundle", bundle)
        )
    }

    suspend fun getMobileDeckBundle(settings: CompanionSettings): CompanionApiResult {
        return send(
            settings,
            JSONObject()
                .put("type", "mobiledeck.bundle.get")
        )
    }

    suspend fun syncPending(settings: CompanionSettings): CompanionApiResult {
        return send(
            settings,
            JSONObject()
                .put("type", "mobiledeck.sync.pending")
        )
    }

    suspend fun updateMobileDeckView(
        settings: CompanionSettings,
        activePageId: Int,
        deckUiMode: DeckUiMode
    ): CompanionApiResult {
        return send(
            settings,
            JSONObject()
                .put("type", "mobiledeck.view.update")
                .put(
                    "viewState",
                    JSONObject()
                        .put("activePageId", activePageId)
                        .put("deckUiMode", deckUiMode.name)
                )
        )
    }

    private suspend fun send(
        settings: CompanionSettings,
        frame: JSONObject
    ): CompanionApiResult {
        val requestType = frame.optString("type", "unknown")
        return send(settings, requestType) { metadata ->
            frame
                .put("requestId", metadata.requestId)
                .put("pairingToken", metadata.pairingToken)
                .put("deviceId", metadata.deviceId)
                .put("deviceName", metadata.deviceName)
                .toString()
        }
    }

    private suspend fun sendCodexJobRequest(
        settings: CompanionSettings,
        payloadFactory: (CompanionRequestMetadata) -> String,
        responseProjector: (Boolean, String?, String) -> CodexJobApiResult
    ): CodexJobApiResult {
        val result = runCatching {
            send(settings, "program.command", payloadFactory)
        }.getOrElse {
            return CodexJobApiResult(reconnectRequired = true)
        }
        return responseProjector(result.ok, result.errorCode, result.data.toString())
    }

    private suspend fun send(
        settings: CompanionSettings,
        requestType: String,
        payloadFactory: (CompanionRequestMetadata) -> String
    ): CompanionApiResult {
        if (!settings.isConfigured()) {
            return CompanionApiResult(false, "Companion endpoint or pairing token is empty", errorCode = "not_configured")
        }
        val endpoint = normalizeCompanionEndpoint(settings.endpoint)
        companionEndpointValidationMessage(endpoint)?.let { message ->
            return CompanionApiResult(false, message, errorCode = "invalid_endpoint")
        }
        val metadata = CompanionRequestMetadata(
            requestId = "android-${UUID.randomUUID()}",
            pairingToken = settings.pairingToken.trim(),
            deviceId = deviceId,
            deviceName = deviceName
        )
        val requestId = metadata.requestId
        val payload = payloadFactory(metadata)
        debugLog(
            "send type=$requestType requestId=$requestId endpoint=$endpoint tokenLength=${settings.pairingToken.trim().length}"
        )
        return withTimeout(COMPANION_REQUEST_TIMEOUT_MILLIS) {
            suspendCancellableCoroutine { continuation ->
                val completed = AtomicBoolean(false)
                var webSocket: WebSocket? = null
                fun complete(result: CompanionApiResult) {
                    if (completed.compareAndSet(false, true)) {
                        webSocket?.close(1000, null)
                        continuation.resume(result)
                    }
                }

                val request = Request.Builder()
                    .url(endpoint)
                    .build()
                val listener = object : WebSocketListener() {
                    override fun onOpen(webSocket: WebSocket, response: Response) {
                        debugLog("onOpen type=$requestType requestId=$requestId")
                        webSocket.send(payload)
                    }

                    override fun onMessage(webSocket: WebSocket, text: String) {
                        debugLog("onMessage type=$requestType requestId=$requestId bytes=${text.length}")
                        val response = runCatching { JSONObject(text) }.getOrElse { error ->
                            debugLog("parseFailure type=$requestType requestId=$requestId message=${error.message}")
                            complete(CompanionApiResult(false, error.message ?: "Invalid Companion response"))
                            return
                        }
                        if (response.optString("requestId") != requestId) return
                        debugLog(
                            "parsed type=$requestType requestId=$requestId ok=${response.optBoolean("ok", false)} errorCode=${response.optString("errorCode")}"
                        )
                        complete(
                            CompanionApiResult(
                                ok = response.optBoolean("ok", false),
                                message = response.optString("message"),
                                data = response.optJSONObject("data") ?: JSONObject(),
                                errorCode = response.optString("errorCode").takeIf { it.isNotBlank() && it != "null" }
                            )
                        )
                    }

                    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                        if (completed.get()) return
                        debugLog(
                            "onFailure type=$requestType requestId=$requestId exception=${t::class.java.simpleName} message=${t.message} httpCode=${response?.code}"
                        )
                        complete(CompanionApiResult(false, t.message ?: "Companion transport failed"))
                    }

                    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                        debugLog("onClosed type=$requestType requestId=$requestId code=$code reason=$reason")
                        if (!completed.get()) {
                            complete(CompanionApiResult(false, reason.ifBlank { "Companion socket closed" }))
                        }
                    }
                }
                webSocket = client.newWebSocket(request, listener)
                continuation.invokeOnCancellation {
                    webSocket?.cancel()
                }
            }
        }
    }

    fun close() {
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
    }
}

internal fun projectCodexSubmitResponse(
    ok: Boolean,
    errorCode: String?,
    dataJson: String,
    binding: CodexButtonBindingPayload
): CodexJobApiResult {
    return CodexJobApiResult.fromCompanionProjection(
        ok = ok,
        errorCode = errorCode,
        dataJson = dataJson,
        expectedIdentity = CodexJobExpectedIdentity.Submit(
            bindingId = binding.bindingId,
            presetId = binding.presetId
        )
    )
}

internal fun projectCodexStatusResponse(
    ok: Boolean,
    errorCode: String?,
    dataJson: String,
    jobId: String,
    bindingId: String
): CodexJobApiResult {
    return CodexJobApiResult.fromCompanionProjection(
        ok = ok,
        errorCode = errorCode,
        dataJson = dataJson,
        expectedIdentity = CodexJobExpectedIdentity.Status(jobId = jobId, bindingId = bindingId)
    )
}

internal fun buildCodexSubmitRequestJson(
    metadata: CompanionRequestMetadata,
    binding: CodexButtonBindingPayload
): String {
    val args = "{" +
        "\"contractVersion\":$CODEX_CONTRACT_VERSION," +
        "\"presetId\":${binding.presetId.toJsonString()}," +
        "\"bindingId\":${binding.bindingId.toJsonString()}" +
        "}"
    return buildCodexProgramCommandRequestJson(metadata, "exec.submit", args)
}

internal fun buildCodexStatusRequestJson(
    metadata: CompanionRequestMetadata,
    jobId: String,
    bindingId: String
): String {
    require(isValidCodexIdentifier(jobId))
    require(isValidCodexIdentifier(bindingId))
    val args = "{" +
        "\"contractVersion\":$CODEX_CONTRACT_VERSION," +
        "\"jobId\":${jobId.toJsonString()}," +
        "\"bindingId\":${bindingId.toJsonString()}" +
        "}"
    return buildCodexProgramCommandRequestJson(metadata, "exec.status", args)
}

private fun buildCodexProgramCommandRequestJson(
    metadata: CompanionRequestMetadata,
    command: String,
    argsJson: String
): String {
    return "{" +
        "\"type\":\"program.command\"," +
        "\"requestId\":${metadata.requestId.toJsonString()}," +
        "\"pairingToken\":${metadata.pairingToken.toJsonString()}," +
        "\"deviceId\":${metadata.deviceId.toJsonString()}," +
        "\"deviceName\":${metadata.deviceName.toJsonString()}," +
        "\"programId\":\"codex\"," +
        "\"command\":${command.toJsonString()}," +
        "\"args\":$argsJson" +
        "}"
}

private fun String.toJsonString(): String {
    val result = StringBuilder(length + 2).append('"')
    forEach { character ->
        when (character) {
            '"' -> result.append("\\\"")
            '\\' -> result.append("\\\\")
            '\b' -> result.append("\\b")
            '\u000C' -> result.append("\\f")
            '\n' -> result.append("\\n")
            '\r' -> result.append("\\r")
            '\t' -> result.append("\\t")
            else -> if (character.code < 0x20) {
                result.append("\\u").append(character.code.toString(16).padStart(4, '0'))
            } else {
                result.append(character)
            }
        }
    }
    return result.append('"').toString()
}

private fun debugLog(message: String) {
    if (BuildConfig.DEBUG) {
        Log.d("MobileDeckCompanion", message)
    }
}

private data class ProgramPayload(
    val programId: String,
    val command: String,
    val args: JSONObject
)

private fun parseProgramPayload(payload: String): ProgramPayload {
    val trimmed = payload.trim()
    if (trimmed.startsWith("{")) {
        val root = JSONObject(trimmed)
        return ProgramPayload(
            programId = root.optString("programId"),
            command = root.optString("command"),
            args = root.optJSONObject("args") ?: JSONObject()
        )
    }
    val parts = trimmed.split(":", limit = 2)
    return ProgramPayload(
        programId = parts.getOrElse(0) { "" },
        command = parts.getOrElse(1) { "" },
        args = JSONObject()
    )
}

fun normalizeCompanionEndpoint(rawEndpoint: String): String {
    val trimmed = rawEndpoint.trim()
    return when {
        trimmed.startsWith("ws://") || trimmed.startsWith("wss://") -> trimmed
        else -> "ws://$trimmed"
    }
}

fun companionEndpointValidationMessage(rawEndpoint: String): String? {
    val endpoint = normalizeCompanionEndpoint(rawEndpoint)
    val uri = runCatching { URI(endpoint) }.getOrElse {
        return "Invalid Companion endpoint"
    }
    val scheme = uri.scheme?.lowercase(Locale.US)
    val host = uri.host?.trim()?.takeIf { it.isNotBlank() }
        ?: return "Invalid Companion endpoint host"
    if (scheme != "ws" && scheme != "wss") {
        return "Companion endpoint must start with ws:// or wss://"
    }
    if (scheme == "wss") {
        return null
    }
    return if (isAllowedCleartextCompanionHost(host)) {
        null
    } else {
        "Cleartext ws:// Companion endpoints are only allowed for local/private network hosts"
    }
}

private fun isAllowedCleartextCompanionHost(host: String): Boolean {
    val normalized = host
        .removePrefix("[")
        .removeSuffix("]")
        .lowercase(Locale.US)
        .trimEnd('.')
    if (normalized == "0.0.0.0" || normalized == "::") return false
    if (normalized == "localhost" || normalized == "::1") return true
    if (!normalized.contains(".")) return true
    if (
        normalized.endsWith(".local") ||
        normalized.endsWith(".lan") ||
        normalized.endsWith(".home.arpa") ||
        normalized.endsWith(".ts.net")
    ) {
        return true
    }
    parseIpv4(normalized)?.let { octets ->
        val first = octets[0]
        val second = octets[1]
        return first == 10 ||
            first == 127 ||
            (first == 169 && second == 254) ||
            (first == 172 && second in 16..31) ||
            (first == 192 && second == 168) ||
            (first == 100 && second in 64..127)
    }
    if (normalized.startsWith("fe80:")) return true
    if (normalized.startsWith("fc") || normalized.startsWith("fd")) return true
    return false
}

private fun parseIpv4(host: String): IntArray? {
    val parts = host.split(".")
    if (parts.size != 4) return null
    val octets = parts.map { part ->
        if (part.isEmpty() || part.any { !it.isDigit() }) return null
        part.toIntOrNull()?.takeIf { it in 0..255 } ?: return null
    }
    return octets.toIntArray()
}

fun mobileDeckDeviceName(): String {
    val manufacturer = Build.MANUFACTURER.orEmpty().trim()
    val model = Build.MODEL.orEmpty().trim()
    val normalizedManufacturer = manufacturer.lowercase(Locale.US)
    val normalizedModel = model.lowercase(Locale.US)
    return when {
        model.isBlank() && manufacturer.isBlank() -> "Android device"
        model.isBlank() -> manufacturer
        manufacturer.isBlank() -> model
        normalizedModel.startsWith(normalizedManufacturer) -> model
        else -> "$manufacturer $model"
    }
}

internal const val COMPANION_REQUEST_TIMEOUT_MILLIS = 2200L
