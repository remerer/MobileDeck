package com.remerer.mobiledeck

import java.util.GregorianCalendar
import java.util.TimeZone

const val CODEX_CONTRACT_VERSION = 1
const val CODEX_JOB_POLL_INTERVAL_MILLIS = 750L
const val CODEX_TERMINAL_DISPLAY_MILLIS = 5_000L

val CODEX_JOB_ERROR_CODES: Set<String> = setOf(
    "invalid_contract_version",
    "invalid_request",
    "binding_not_found",
    "preset_not_found",
    "binding_preset_mismatch",
    "execution_disabled",
    "execution_mode_insufficient",
    "workspace_not_allowlisted",
    "workspace_mismatch",
    "workspace_unavailable",
    "workspace_write_expired",
    "job_not_found",
    "job_not_active",
    "codex_cli_unavailable",
    "codex_auth_required",
    "approval_required",
    "spawn_failed",
    "output_parse_failed",
    "exec_failed",
    "timed_out",
    "cancel_failed",
    "companion_restarted",
    "journal_io_failed",
    "internal_error"
)

fun projectSafeCodexFailureCode(errorCode: String?): String? {
    return errorCode?.takeIf(CODEX_JOB_ERROR_CODES::contains)
}

data class CodexButtonBindingPayload(
    val presetId: String,
    val bindingId: String
) {
    val contractVersion: Int = CODEX_CONTRACT_VERSION

    init {
        require(isValidCodexIdentifier(presetId))
        require(isValidCodexIdentifier(bindingId))
    }

    companion object {
        fun parse(payload: String): CodexButtonBindingPayload? {
            val root = parseStrictJsonObject(payload) ?: return null
            if (!root.hasExactly("programId", "command", "args")) return null
            if (root.string("programId") != "codex") return null
            if (root.string("command") != "exec.submit") return null
            return parseCodexBindingArgs(root.objectValue("args") ?: return null)
        }

        internal fun parseArgs(argsJson: String): CodexButtonBindingPayload? {
            return parseStrictJsonObject(argsJson)?.let(::parseCodexBindingArgs)
        }
    }
}

enum class CodexJobStatus {
    Queued,
    Running,
    Completed,
    Failed,
    Cancelled;

    val isActive: Boolean
        get() = this == Queued || this == Running

    val isTerminal: Boolean
        get() = !isActive

    companion object {
        internal fun parse(value: String): CodexJobStatus? {
            return when (value) {
                "queued" -> Queued
                "running" -> Running
                "completed" -> Completed
                "failed" -> Failed
                "cancelled" -> Cancelled
                else -> null
            }
        }
    }
}

class CodexJobSnapshot private constructor(
    val contractVersion: Int,
    val jobId: String,
    val bindingId: String,
    val presetId: String,
    val status: CodexJobStatus,
    val duplicate: Boolean,
    val acceptedAt: String,
    val updatedAt: String,
    val startedAt: String?,
    val finishedAt: String?,
    val elapsedMs: Long,
    val cancelRequested: Boolean,
    val errorCode: String?,
    val message: String
) {
    companion object {
        fun parse(payload: String): CodexJobSnapshot? {
            val root = parseStrictJsonObject(payload) ?: return null
            if (!root.hasExactly(*SNAPSHOT_FIELDS)) return null
            if (root.long("contractVersion") != CODEX_CONTRACT_VERSION.toLong()) return null

            val jobId = root.string("jobId")?.takeIf(::isValidCodexIdentifier) ?: return null
            val bindingId = root.string("bindingId")?.takeIf(::isValidCodexIdentifier) ?: return null
            val presetId = root.string("presetId")?.takeIf(::isValidCodexIdentifier) ?: return null
            val status = root.string("status")?.let(CodexJobStatus::parse) ?: return null
            val duplicate = root.boolean("duplicate") ?: return null
            val acceptedAt = root.string("acceptedAt")?.takeIf(::isRfc3339Timestamp) ?: return null
            val updatedAt = root.string("updatedAt")?.takeIf(::isRfc3339Timestamp) ?: return null
            val startedAt = root.nullableTimestamp("startedAt") ?: return null
            val finishedAt = root.nullableTimestamp("finishedAt") ?: return null
            val elapsedMs = root.long("elapsedMs")?.takeIf { it >= 0L } ?: return null
            val cancelRequested = root.boolean("cancelRequested") ?: return null
            val errorCode = root.nullableString("errorCode") ?: return null
            val message = root.string("message") ?: return null

            if (root["summary"] !== JsonValue.NullValue) return null
            if (!isValidSnapshotMessage(status, cancelRequested, message)) return null
            if (errorCode.value != null && projectSafeCodexFailureCode(errorCode.value) == null) return null
            if (status != CodexJobStatus.Failed && errorCode.value != null) return null

            return CodexJobSnapshot(
                contractVersion = CODEX_CONTRACT_VERSION,
                jobId = jobId,
                bindingId = bindingId,
                presetId = presetId,
                status = status,
                duplicate = duplicate,
                acceptedAt = acceptedAt,
                updatedAt = updatedAt,
                startedAt = startedAt.value,
                finishedAt = finishedAt.value,
                elapsedMs = elapsedMs,
                cancelRequested = cancelRequested,
                errorCode = errorCode.value,
                message = message
            )
        }
    }
}

data class CodexButtonTaskState(
    val snapshot: CodexJobSnapshot,
    val reconnecting: Boolean = false,
    val reconnectAttempt: Int = 0,
    val terminalObservedAtMillis: Long? = null
) {
    init {
        require(reconnectAttempt >= 0)
    }

    val suppressesDuplicateSubmit: Boolean
        get() = snapshot.status.isActive

    val safeFailureCode: String?
        get() = if (snapshot.status == CodexJobStatus.Failed) {
            projectSafeCodexFailureCode(snapshot.errorCode)
        } else {
            null
        }

    fun isTerminalDisplayExpired(nowMillis: Long): Boolean {
        if (snapshot.status != CodexJobStatus.Completed && snapshot.status != CodexJobStatus.Cancelled) {
            return false
        }
        val observedAt = terminalObservedAtMillis ?: return false
        return nowMillis >= observedAt && nowMillis - observedAt >= CODEX_TERMINAL_DISPLAY_MILLIS
    }

    companion object {
        @JvmStatic
        fun reconnectDelayMillis(attempt: Int): Long {
            require(attempt >= 0)
            return when (attempt) {
                0 -> 1_000L
                1 -> 2_000L
                else -> 4_000L
            }
        }
    }
}

object CompanionReleaseRoutePolicy {
    fun allowsRequestType(settings: CompanionSettings, type: String): Boolean {
        return settings.isConfigured() && type == "status.ping"
    }

    fun allowsCodexSubmit(settings: CompanionSettings, payload: String): Boolean {
        return bindingForSubmit(settings, payload) != null
    }

    fun bindingForSubmit(
        settings: CompanionSettings,
        payload: String
    ): CodexButtonBindingPayload? {
        if (!settings.isConfigured()) return null
        return CodexButtonBindingPayload.parse(payload)
    }

    fun allowsCodexStatus(
        settings: CompanionSettings,
        jobId: String,
        bindingId: String
    ): Boolean {
        return settings.isConfigured() &&
            isValidCodexIdentifier(jobId) &&
            isValidCodexIdentifier(bindingId)
    }

    fun allowsProgramCommand(
        settings: CompanionSettings,
        programId: String,
        command: String,
        argsJson: String
    ): Boolean {
        if (!settings.isConfigured() || programId != "codex") return false
        return when (command) {
            "exec.submit" -> CodexButtonBindingPayload.parseArgs(argsJson) != null
            "exec.status" -> parseCodexStatusArgs(argsJson) != null
            else -> false
        }
    }
}

data class CodexJobApiResult(
    val snapshot: CodexJobSnapshot? = null,
    val failureCode: String? = null,
    val reconnectRequired: Boolean = false
) {
    companion object {
        fun fromCompanionProjection(
            ok: Boolean,
            errorCode: String?,
            dataJson: String
        ): CodexJobApiResult {
            if (ok) {
                val snapshot = CodexJobSnapshot.parse(dataJson)
                return if (snapshot != null) {
                    CodexJobApiResult(snapshot = snapshot)
                } else {
                    CodexJobApiResult(failureCode = "internal_error")
                }
            }

            val safeCode = projectSafeCodexFailureCode(errorCode)
            return when {
                safeCode != null -> CodexJobApiResult(failureCode = safeCode)
                errorCode == null || errorCode == "unauthorized" -> {
                    CodexJobApiResult(reconnectRequired = true)
                }
                else -> CodexJobApiResult(failureCode = "internal_error")
            }
        }
    }
}

internal fun isValidCodexIdentifier(value: String): Boolean {
    return value.length in 1..128 && CODEX_IDENTIFIER.matches(value)
}

private fun parseCodexBindingArgs(root: Map<String, JsonValue>): CodexButtonBindingPayload? {
    if (!root.hasExactly("contractVersion", "presetId", "bindingId")) return null
    if (root.long("contractVersion") != CODEX_CONTRACT_VERSION.toLong()) return null
    val presetId = root.string("presetId")?.takeIf(::isValidCodexIdentifier) ?: return null
    val bindingId = root.string("bindingId")?.takeIf(::isValidCodexIdentifier) ?: return null
    return CodexButtonBindingPayload(presetId = presetId, bindingId = bindingId)
}

private fun parseCodexStatusArgs(argsJson: String): Pair<String, String>? {
    val root = parseStrictJsonObject(argsJson) ?: return null
    if (!root.hasExactly("contractVersion", "jobId", "bindingId")) return null
    if (root.long("contractVersion") != CODEX_CONTRACT_VERSION.toLong()) return null
    val jobId = root.string("jobId")?.takeIf(::isValidCodexIdentifier) ?: return null
    val bindingId = root.string("bindingId")?.takeIf(::isValidCodexIdentifier) ?: return null
    return jobId to bindingId
}

private fun isValidSnapshotMessage(
    status: CodexJobStatus,
    cancelRequested: Boolean,
    message: String
): Boolean {
    val expected = if (cancelRequested && status.isActive) {
        "Cancellation requested"
    } else {
        when (status) {
            CodexJobStatus.Queued -> "Queued"
            CodexJobStatus.Running -> "Running"
            CodexJobStatus.Completed -> "Completed"
            CodexJobStatus.Failed -> "Codex job failed"
            CodexJobStatus.Cancelled -> "Cancelled"
        }
    }
    return message == expected
}

private fun isRfc3339Timestamp(value: String): Boolean {
    val match = RFC_3339.matchEntire(value) ?: return false
    val year = match.groupValues[1].toIntOrNull()?.takeIf { it > 0 } ?: return false
    val month = match.groupValues[2].toIntOrNull() ?: return false
    val day = match.groupValues[3].toIntOrNull() ?: return false
    val hour = match.groupValues[4].toIntOrNull()?.takeIf { it in 0..23 } ?: return false
    val minute = match.groupValues[5].toIntOrNull()?.takeIf { it in 0..59 } ?: return false
    val second = match.groupValues[6].toIntOrNull()?.takeIf { it in 0..59 } ?: return false
    val offset = match.groupValues[8]
    if (offset != "Z") {
        offset.substring(1, 3).toIntOrNull()?.takeIf { it in 0..23 } ?: return false
        offset.substring(4, 6).toIntOrNull()?.takeIf { it in 0..59 } ?: return false
    }
    return runCatching {
        GregorianCalendar(TimeZone.getTimeZone("UTC")).apply {
            isLenient = false
            clear()
            set(year, month - 1, day, hour, minute, second)
        }.time
    }.isSuccess
}

private sealed interface JsonValue {
    data class ObjectValue(val value: Map<String, JsonValue>) : JsonValue
    data class StringValue(val value: String) : JsonValue
    data class LongValue(val value: Long) : JsonValue
    data class BooleanValue(val value: Boolean) : JsonValue
    data object NullValue : JsonValue
}

private data class NullableString(val value: String?)

private fun parseStrictJsonObject(raw: String): Map<String, JsonValue>? {
    return runCatching { StrictJsonParser(raw).parseObjectDocument() }.getOrNull()
}

private class StrictJsonParser(private val raw: String) {
    private var index = 0

    fun parseObjectDocument(): Map<String, JsonValue> {
        skipWhitespace()
        val value = parseObject()
        skipWhitespace()
        require(index == raw.length)
        return value
    }

    private fun parseValue(): JsonValue {
        skipWhitespace()
        require(index < raw.length)
        return when (raw[index]) {
            '{' -> JsonValue.ObjectValue(parseObject())
            '"' -> JsonValue.StringValue(parseString())
            't' -> parseLiteral("true", JsonValue.BooleanValue(true))
            'f' -> parseLiteral("false", JsonValue.BooleanValue(false))
            'n' -> parseLiteral("null", JsonValue.NullValue)
            '-', in '0'..'9' -> JsonValue.LongValue(parseLong())
            else -> error("Unsupported JSON value")
        }
    }

    private fun parseObject(): Map<String, JsonValue> {
        expect('{')
        skipWhitespace()
        if (takeIfPresent('}')) return emptyMap()

        val values = linkedMapOf<String, JsonValue>()
        while (true) {
            skipWhitespace()
            val key = parseString()
            require(key !in values)
            skipWhitespace()
            expect(':')
            values[key] = parseValue()
            skipWhitespace()
            if (takeIfPresent('}')) return values
            expect(',')
        }
    }

    private fun parseString(): String {
        expect('"')
        val value = StringBuilder()
        while (index < raw.length) {
            val char = raw[index++]
            when {
                char == '"' -> return value.toString()
                char == '\\' -> value.append(parseEscape())
                char.code < 0x20 -> error("Unescaped control character")
                else -> value.append(char)
            }
        }
        error("Unterminated JSON string")
    }

    private fun parseEscape(): Char {
        require(index < raw.length)
        return when (val escaped = raw[index++]) {
            '"', '\\', '/' -> escaped
            'b' -> '\b'
            'f' -> '\u000C'
            'n' -> '\n'
            'r' -> '\r'
            't' -> '\t'
            'u' -> {
                require(index + 4 <= raw.length)
                val code = raw.substring(index, index + 4).toIntOrNull(16)
                    ?: error("Invalid unicode escape")
                index += 4
                code.toChar()
            }
            else -> error("Invalid JSON escape")
        }
    }

    private fun parseLong(): Long {
        val start = index
        if (raw[index] == '-') index++
        require(index < raw.length)
        if (raw[index] == '0') {
            index++
            require(index == raw.length || !raw[index].isDigit())
        } else {
            require(raw[index] in '1'..'9')
            while (index < raw.length && raw[index].isDigit()) index++
        }
        return raw.substring(start, index).toLongOrNull() ?: error("Invalid JSON integer")
    }

    private fun <T : JsonValue> parseLiteral(literal: String, value: T): T {
        require(raw.regionMatches(index, literal, 0, literal.length))
        index += literal.length
        return value
    }

    private fun skipWhitespace() {
        while (index < raw.length && raw[index] in JSON_WHITESPACE) index++
    }

    private fun expect(expected: Char) {
        require(index < raw.length && raw[index] == expected)
        index++
    }

    private fun takeIfPresent(expected: Char): Boolean {
        if (index >= raw.length || raw[index] != expected) return false
        index++
        return true
    }
}

private fun Map<String, JsonValue>.hasExactly(vararg names: String): Boolean {
    return keys == names.toSet()
}

private fun Map<String, JsonValue>.string(name: String): String? {
    return (get(name) as? JsonValue.StringValue)?.value
}

private fun Map<String, JsonValue>.long(name: String): Long? {
    return (get(name) as? JsonValue.LongValue)?.value
}

private fun Map<String, JsonValue>.boolean(name: String): Boolean? {
    return (get(name) as? JsonValue.BooleanValue)?.value
}

private fun Map<String, JsonValue>.objectValue(name: String): Map<String, JsonValue>? {
    return (get(name) as? JsonValue.ObjectValue)?.value
}

private fun Map<String, JsonValue>.nullableString(name: String): NullableString? {
    return when (val value = get(name)) {
        JsonValue.NullValue -> NullableString(null)
        is JsonValue.StringValue -> NullableString(value.value)
        else -> null
    }
}

private fun Map<String, JsonValue>.nullableTimestamp(name: String): NullableString? {
    val value = nullableString(name) ?: return null
    if (value.value != null && !isRfc3339Timestamp(value.value)) return null
    return value
}

private val CODEX_IDENTIFIER = Regex("[A-Za-z0-9][A-Za-z0-9._-]{0,127}")
private val RFC_3339 = Regex(
    """(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})(\.\d{1,9})?(Z|[+-]\d{2}:\d{2})"""
)
private val JSON_WHITESPACE = setOf(' ', '\t', '\n', '\r')
private val SNAPSHOT_FIELDS = arrayOf(
    "contractVersion",
    "jobId",
    "bindingId",
    "presetId",
    "status",
    "duplicate",
    "acceptedAt",
    "updatedAt",
    "startedAt",
    "finishedAt",
    "elapsedMs",
    "cancelRequested",
    "errorCode",
    "message",
    "summary"
)
