package com.remerer.mobiledeck

import java.io.File
import java.lang.reflect.Modifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CodexJobContractTest {
    @Test
    fun exactButtonBindingPayloadParses() {
        val binding = CodexButtonBindingPayload.parse(BINDING_PAYLOAD)

        assertNotNull(binding)
        assertEquals(1, binding?.contractVersion)
        assertEquals(PRESET_ID, binding?.presetId)
        assertEquals(BINDING_ID, binding?.bindingId)
    }

    @Test
    fun bindingPayloadRejectsMissingExtraMistypedAndMalformedFields() {
        val rejected = listOf(
            """{"programId":"codex","command":"exec.submit","args":{"contractVersion":1,"presetId":"$PRESET_ID"}}""",
            """{"programId":"codex","command":"exec.submit","args":{"contractVersion":1,"presetId":"$PRESET_ID","bindingId":"$BINDING_ID"},"extra":true}""",
            """{"programId":"codex","command":"exec.submit","args":{"contractVersion":"1","presetId":"$PRESET_ID","bindingId":"$BINDING_ID"}}""",
            """{"programId":"codex","command":"exec.submit","args":{"contractVersion":1,"presetId":"","bindingId":"$BINDING_ID"}}""",
            """{"programId":"codex","command":"exec.submit","args":{"contractVersion":1,"presetId":"../private","bindingId":"$BINDING_ID"}}""",
            """{"programId":"codex","command":"exec.submit","args":{"contractVersion":1,"presetId":"$PRESET_ID","bindingId":"binding id"}}""",
            """{"programId":"codex","command":"exec.submit","args":{"contractVersion":1,"presetId":"$PRESET_ID","bindingId":"$BINDING_ID","bindingId":"other"}}""",
            "not-json"
        )

        rejected.forEach { payload ->
            assertNull(payload, CodexButtonBindingPayload.parse(payload))
        }
    }

    @Test
    fun bindingPayloadRejectsUnknownVersionAndEveryPcOwnedOverride() {
        val unknownVersion = BINDING_PAYLOAD.replace("\"contractVersion\":1", "\"contractVersion\":2")
        assertNull(CodexButtonBindingPayload.parse(unknownVersion))

        listOf(
            "prompt",
            "model",
            "sandbox",
            "workspacePath",
            "workspace",
            "approval",
            "timeout",
            "output",
            "override"
        ).forEach { field ->
            val payload = BINDING_PAYLOAD.replace(
                "\"bindingId\":\"$BINDING_ID\"",
                "\"bindingId\":\"$BINDING_ID\",\"$field\":\"unsafe\""
            )
            assertNull(field, CodexButtonBindingPayload.parse(payload))
        }
    }

    @Test
    fun exactSnapshotParsesAndDropsNullSummary() {
        val snapshot = parseSnapshot()

        assertEquals(1, snapshot.contractVersion)
        assertEquals(JOB_ID, snapshot.jobId)
        assertEquals(BINDING_ID, snapshot.bindingId)
        assertEquals(PRESET_ID, snapshot.presetId)
        assertEquals(CodexJobStatus.Queued, snapshot.status)
        assertFalse(snapshot.duplicate)
        assertNull(snapshot.startedAt)
        assertNull(snapshot.finishedAt)
        assertEquals(0L, snapshot.elapsedMs)
        assertFalse(snapshot.cancelRequested)
        assertNull(snapshot.errorCode)
        assertEquals("Queued", snapshot.message)
        assertFalse(CodexJobSnapshot::class.java.declaredFields.any { it.name == "summary" })
    }

    @Test
    fun snapshotAcceptsSafeOptionalTimestampAndFailureFields() {
        val snapshot = parseSnapshot(
            status = "failed",
            duplicate = true,
            startedAt = "\"2026-07-17T12:00:00.125Z\"",
            finishedAt = "\"2026-07-17T12:00:03+00:00\"",
            elapsedMs = 3000,
            errorCode = "\"exec_failed\"",
            message = "Codex job failed"
        )

        assertEquals(CodexJobStatus.Failed, snapshot.status)
        assertTrue(snapshot.duplicate)
        assertEquals("2026-07-17T12:00:00.125Z", snapshot.startedAt)
        assertEquals("2026-07-17T12:00:03+00:00", snapshot.finishedAt)
        assertEquals(3000L, snapshot.elapsedMs)
        assertEquals("exec_failed", snapshot.errorCode)
    }

    @Test
    fun failedSnapshotMayOmitTheNullableErrorCode() {
        val snapshot = parseSnapshot(
            status = "failed",
            errorCode = "null",
            message = "Codex job failed"
        )

        assertEquals(CodexJobStatus.Failed, snapshot.status)
        assertNull(snapshot.errorCode)
        assertNull(CodexButtonTaskState(snapshot).safeFailureCode)
    }

    @Test
    fun snapshotRejectsUnknownVersionStatusErrorSummaryAndRawFields() {
        val rejected = listOf(
            snapshotJson().replace("\"contractVersion\":1", "\"contractVersion\":2"),
            snapshotJson(status = "idle", message = "Idle"),
            snapshotJson(status = "failed", errorCode = "\"codex_cli_incompatible\"", message = "Codex job failed"),
            snapshotJson().replace("\"summary\":null", "\"summary\":\"agent output\""),
            snapshotJson().replace("\"summary\":null", "\"summary\":null,\"stdout\":\"secret\""),
            snapshotJson().replace("\"summary\":null", "\"summary\":null,\"prompt\":\"secret\""),
            snapshotJson().replace("\"jobId\":\"$JOB_ID\"", "\"jobId\":\"../job\""),
            snapshotJson().replace("\"elapsedMs\":0", "\"elapsedMs\":-1"),
            snapshotJson().replace("\"acceptedAt\":\"2026-07-17T12:00:00Z\"", "\"acceptedAt\":\"yesterday\""),
            snapshotJson().replace("\"message\":\"Queued\"", "\"message\":\"raw arbitrary text\"")
        )

        rejected.forEach { json ->
            assertNull(json, CodexJobSnapshot.parse(json))
        }
    }

    @Test
    fun statusesClassifyActiveAndTerminalExactly() {
        assertTrue(CodexJobStatus.Queued.isActive)
        assertTrue(CodexJobStatus.Running.isActive)
        assertFalse(CodexJobStatus.Completed.isActive)
        assertFalse(CodexJobStatus.Failed.isActive)
        assertFalse(CodexJobStatus.Cancelled.isActive)

        assertFalse(CodexJobStatus.Queued.isTerminal)
        assertFalse(CodexJobStatus.Running.isTerminal)
        assertTrue(CodexJobStatus.Completed.isTerminal)
        assertTrue(CodexJobStatus.Failed.isTerminal)
        assertTrue(CodexJobStatus.Cancelled.isTerminal)
    }

    @Test
    fun localActiveAndPcDuplicateSnapshotsSuppressAnotherSubmit() {
        val queued = CodexButtonTaskState(parseSnapshot())
        val duplicateRunning = CodexButtonTaskState(
            parseSnapshot(status = "running", duplicate = true, message = "Running")
        )
        val completed = CodexButtonTaskState(
            parseSnapshot(status = "completed", message = "Completed")
        )

        assertTrue(queued.suppressesDuplicateSubmit)
        assertTrue(duplicateRunning.suppressesDuplicateSubmit)
        assertFalse(completed.suppressesDuplicateSubmit)
    }

    @Test
    fun completedAndCancelledExpireAtFiveSecondsWhileFailedIsRetained() {
        val completed = CodexButtonTaskState(
            snapshot = parseSnapshot(status = "completed", message = "Completed"),
            terminalObservedAtMillis = 10_000L
        )
        val cancelled = CodexButtonTaskState(
            snapshot = parseSnapshot(status = "cancelled", message = "Cancelled"),
            terminalObservedAtMillis = 10_000L
        )
        val failed = CodexButtonTaskState(
            snapshot = parseSnapshot(
                status = "failed",
                errorCode = "\"exec_failed\"",
                message = "Codex job failed"
            ),
            terminalObservedAtMillis = 10_000L
        )

        assertFalse(completed.isTerminalDisplayExpired(14_999L))
        assertTrue(completed.isTerminalDisplayExpired(15_000L))
        assertTrue(cancelled.isTerminalDisplayExpired(15_000L))
        assertFalse(failed.isTerminalDisplayExpired(Long.MAX_VALUE))
        assertEquals("exec_failed", failed.safeFailureCode)
        assertNull(completed.safeFailureCode)
    }

    @Test
    fun pollExpiryAndReconnectTimingAreFrozen() {
        assertEquals(750L, CODEX_JOB_POLL_INTERVAL_MILLIS)
        assertEquals(5_000L, CODEX_TERMINAL_DISPLAY_MILLIS)
        assertEquals(2_200L, COMPANION_REQUEST_TIMEOUT_MILLIS)
        assertEquals(
            listOf(1_000L, 2_000L, 4_000L, 4_000L, 4_000L),
            (0..4).map(CodexButtonTaskState::reconnectDelayMillis)
        )
    }

    @Test
    fun stableFailureProjectionUsesOnlyTheFrozenAllowlist() {
        assertEquals(24, CODEX_JOB_ERROR_CODES.size)
        assertTrue("exec_failed" in CODEX_JOB_ERROR_CODES)
        assertFalse("codex_cli_incompatible" in CODEX_JOB_ERROR_CODES)
        assertEquals("exec_failed", projectSafeCodexFailureCode("exec_failed"))
        assertNull(projectSafeCodexFailureCode("unauthorized"))
        assertNull(projectSafeCodexFailureCode("raw_failure"))
    }

    @Test
    fun releaseAllowsOnlyConfiguredPingAndExactCodexSubmitStatus() {
        val settings = configuredSettings()
        val statusArgs = """{"contractVersion":1,"jobId":"$JOB_ID","bindingId":"$BINDING_ID"}"""

        assertTrue(CompanionReleaseRoutePolicy.allowsRequestType(settings, "status.ping"))
        assertTrue(CompanionReleaseRoutePolicy.allowsCodexSubmit(settings, BINDING_PAYLOAD))
        assertTrue(
            CompanionReleaseRoutePolicy.allowsProgramCommand(
                settings = settings,
                programId = "codex",
                command = "exec.submit",
                argsJson = BINDING_ARGS
            )
        )
        assertTrue(
            CompanionReleaseRoutePolicy.allowsProgramCommand(
                settings = settings,
                programId = "codex",
                command = "exec.status",
                argsJson = statusArgs
            )
        )
    }

    @Test
    fun releaseRejectsUnconfiguredGenericObsCancelOverridesAndDeveloperRoutes() {
        val settings = configuredSettings()
        val invalidSettings = listOf(
            settings.copy(enabled = false),
            settings.copy(endpoint = ""),
            settings.copy(pairingToken = "")
        )
        invalidSettings.forEach { invalid ->
            assertFalse(CompanionReleaseRoutePolicy.allowsRequestType(invalid, "status.ping"))
            assertFalse(CompanionReleaseRoutePolicy.allowsCodexSubmit(invalid, BINDING_PAYLOAD))
        }

        listOf("status.check", "actions.list", "open", "text", "input.command", "mobiledeck.bundle.update").forEach { type ->
            assertFalse(type, CompanionReleaseRoutePolicy.allowsRequestType(settings, type))
        }

        val rejectedCommands = listOf(
            Triple("obs", "scene.next", "{}"),
            Triple("program", "command", "{}"),
            Triple("codex", "exec.cancel", """{"contractVersion":1,"jobId":"$JOB_ID"}"""),
            Triple("codex", "exec.submit", """{"contractVersion":1,"presetId":"$PRESET_ID","bindingId":"$BINDING_ID","prompt":"raw"}"""),
            Triple("codex", "exec.submit", """{"contractVersion":1,"presetId":"$PRESET_ID","bindingId":"$BINDING_ID","model":"override"}"""),
            Triple("codex", "exec.submit", """{"contractVersion":1,"presetId":"$PRESET_ID","bindingId":"$BINDING_ID","sandbox":"workspace-write"}"""),
            Triple("codex", "exec.submit", """{"contractVersion":1,"presetId":"$PRESET_ID","bindingId":"$BINDING_ID","workspacePath":"C:/private"}"""),
            Triple("codex", "exec.submit", """{"contractVersion":1,"presetId":"$PRESET_ID","bindingId":"$BINDING_ID","approval":"never"}"""),
            Triple("codex", "exec.submit", """{"contractVersion":1,"presetId":"$PRESET_ID","bindingId":"$BINDING_ID","timeout":2200}"""),
            Triple("codex", "exec.status", """{"contractVersion":1,"jobId":"$JOB_ID","bindingId":"$BINDING_ID","output":true}""")
        )
        rejectedCommands.forEach { (programId, command, args) ->
            assertFalse(
                "$programId:$command",
                CompanionReleaseRoutePolicy.allowsProgramCommand(settings, programId, command, args)
            )
        }
    }

    @Test
    fun submitAndStatusRequestsMatchTheFrozenJsonExactly() {
        val metadata = CompanionRequestMetadata(
            requestId = "android-uuid",
            pairingToken = "token",
            deviceId = "device-uuid",
            deviceName = "Pixel"
        )
        val binding = requireNotNull(CodexButtonBindingPayload.parse(BINDING_PAYLOAD))

        assertEquals(
            """{"type":"program.command","requestId":"android-uuid","pairingToken":"token","deviceId":"device-uuid","deviceName":"Pixel","programId":"codex","command":"exec.submit","args":{"contractVersion":1,"presetId":"$PRESET_ID","bindingId":"$BINDING_ID"}}""",
            buildCodexSubmitRequestJson(metadata, binding)
        )
        assertEquals(
            """{"type":"program.command","requestId":"android-uuid","pairingToken":"token","deviceId":"device-uuid","deviceName":"Pixel","programId":"codex","command":"exec.status","args":{"contractVersion":1,"jobId":"$JOB_ID","bindingId":"$BINDING_ID"}}""",
            buildCodexStatusRequestJson(metadata, JOB_ID, BINDING_ID)
        )
    }

    @Test
    fun typedApiProjectionRejectsUnsafeDataAndSeparatesTransportAuth() {
        val accepted = CodexJobApiResult.fromCompanionProjection(
            ok = true,
            errorCode = null,
            dataJson = snapshotJson()
        )
        val unsafe = CodexJobApiResult.fromCompanionProjection(
            ok = true,
            errorCode = null,
            dataJson = snapshotJson().replace("\"summary\":null", "\"summary\":\"raw\"")
        )
        val failed = CodexJobApiResult.fromCompanionProjection(
            ok = false,
            errorCode = "exec_failed",
            dataJson = "{}"
        )
        val unauthorized = CodexJobApiResult.fromCompanionProjection(
            ok = false,
            errorCode = "unauthorized",
            dataJson = "{}"
        )
        val unknownError = CodexJobApiResult.fromCompanionProjection(
            ok = false,
            errorCode = "raw_unknown_error",
            dataJson = "{}"
        )

        assertEquals(JOB_ID, accepted.snapshot?.jobId)
        assertNull(accepted.failureCode)
        assertEquals("internal_error", unsafe.failureCode)
        assertEquals("exec_failed", failed.failureCode)
        assertFalse(failed.reconnectRequired)
        assertNull(unauthorized.failureCode)
        assertTrue(unauthorized.reconnectRequired)
        assertEquals("internal_error", unknownError.failureCode)
        assertFalse(unknownError.reconnectRequired)
    }

    @Test
    fun deckBundleAndPersistedButtonSchemaRemainVersionTwoWithoutJobFields() {
        val persistedFields = DeckButton::class.java.declaredFields
            .filterNot { Modifier.isStatic(it.modifiers) || it.isSynthetic }
            .map { it.name }
            .toSet()
        assertEquals(
            setOf(
                "id",
                "title",
                "subtitle",
                "icon",
                "iconImageUri",
                "displayMode",
                "actionType",
                "payload",
                "color",
                "position",
                "spanColumns",
                "spanRows",
                "appWidgetId",
                "appWidgetTouchable",
                "controlStyle",
                "controlStyleRaw",
                "companionControl"
            ),
            persistedFields
        )
        assertTrue(findAppSource("DeckBundleTransfer.kt").readText().contains("private const val BUNDLE_VERSION = 2"))
    }

    private fun configuredSettings() = CompanionSettings(
        enabled = true,
        endpoint = "wss://companion.example",
        pairingToken = "pairing-token"
    )

    private fun parseSnapshot(
        status: String = "queued",
        duplicate: Boolean = false,
        startedAt: String = "null",
        finishedAt: String = "null",
        elapsedMs: Long = 0L,
        errorCode: String = "null",
        message: String = "Queued"
    ): CodexJobSnapshot {
        return requireNotNull(
            CodexJobSnapshot.parse(
                snapshotJson(status, duplicate, startedAt, finishedAt, elapsedMs, errorCode, message)
            )
        )
    }

    private fun snapshotJson(
        status: String = "queued",
        duplicate: Boolean = false,
        startedAt: String = "null",
        finishedAt: String = "null",
        elapsedMs: Long = 0L,
        errorCode: String = "null",
        message: String = "Queued"
    ): String {
        return """{"contractVersion":1,"jobId":"$JOB_ID","bindingId":"$BINDING_ID","presetId":"$PRESET_ID","status":"$status","duplicate":$duplicate,"acceptedAt":"2026-07-17T12:00:00Z","updatedAt":"2026-07-17T12:00:00Z","startedAt":$startedAt,"finishedAt":$finishedAt,"elapsedMs":$elapsedMs,"cancelRequested":false,"errorCode":$errorCode,"message":"$message","summary":null}"""
    }

    private fun findAppSource(fileName: String): File {
        return sequenceOf(
            File("app/src/main/java/com/remerer/mobiledeck/$fileName"),
            File("src/main/java/com/remerer/mobiledeck/$fileName")
        ).first { it.isFile }
    }

    private companion object {
        const val PRESET_ID = "readonly-qa"
        const val BINDING_ID = "binding-7f1a5f45d82e4d63a6a238db1f69a211"
        const val JOB_ID = "job-9d34d23d4efb4da0aadb8d65c0861f52"
        const val BINDING_ARGS =
            """{"contractVersion":1,"presetId":"$PRESET_ID","bindingId":"$BINDING_ID"}"""
        const val BINDING_PAYLOAD =
            """{"programId":"codex","command":"exec.submit","args":$BINDING_ARGS}"""
    }
}
