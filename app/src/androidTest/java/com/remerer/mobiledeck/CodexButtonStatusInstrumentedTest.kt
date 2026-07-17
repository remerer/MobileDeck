package com.remerer.mobiledeck

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CodexButtonStatusInstrumentedTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun rendersAllTransientStatesInsideFixedButtonBoundsWithoutPercentage() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val queued = taskState(snapshot(CodexJobStatus.Queued))
        val running = taskState(snapshot(CodexJobStatus.Running, elapsedMs = 65_000L))
        val completed = taskState(snapshot(CodexJobStatus.Completed), terminalObservedAtMillis = 10L)
        val failed = taskState(snapshot(CodexJobStatus.Failed, errorCode = "exec_failed"))
        val cancelled = taskState(snapshot(CodexJobStatus.Cancelled), terminalObservedAtMillis = 10L)
        val reconnecting = queued.copy(reconnecting = true, reconnectAttempt = 2)

        composeRule.setContent {
            Box(
                modifier = Modifier
                    .size(width = 120.dp, height = 80.dp)
                    .testTag("fixed-button")
            ) {
                listOf(
                    codexButtonVisualStatus(queued, null, false, true, true),
                    codexButtonVisualStatus(running, null, false, true, true),
                    codexButtonVisualStatus(completed, null, false, true, true),
                    codexButtonVisualStatus(failed, null, false, true, true),
                    codexButtonVisualStatus(cancelled, null, false, true, true),
                    codexButtonVisualStatus(reconnecting, null, false, true, false),
                    codexButtonVisualStatus(null, null, false, false, false),
                    codexButtonVisualStatus(null, null, false, true, false)
                ).forEach { status ->
                    CodexButtonStatusOverlay(status = status)
                }
            }
        }

        composeRule.onNodeWithTag("fixed-button")
            .assertWidthIsEqualTo(120.dp)
            .assertHeightIsEqualTo(80.dp)
        composeRule.onNodeWithContentDescription(context.getString(R.string.codex_status_queued)).assertExists()
        composeRule.onNodeWithContentDescription(
            context.getString(R.string.codex_status_running_elapsed, "1:05")
        ).assertExists()
        composeRule.onNodeWithContentDescription(context.getString(R.string.codex_status_completed)).assertExists()
        composeRule.onNodeWithContentDescription(
            context.getString(R.string.codex_status_failed_code, "exec_failed")
        ).assertExists()
        composeRule.onNodeWithContentDescription(context.getString(R.string.codex_status_cancelled)).assertExists()
        composeRule.onNodeWithContentDescription(context.getString(R.string.codex_status_reconnecting)).assertExists()
        composeRule.onNodeWithContentDescription(context.getString(R.string.codex_status_disabled)).assertExists()
        composeRule.onNodeWithContentDescription(context.getString(R.string.codex_status_disconnected)).assertExists()
        composeRule.onAllNodes(hasText("%", substring = true)).assertCountEquals(0)
    }

    @Test
    fun failedDisplayProjectsOnlyAllowlistedCodes() {
        val safe = codexButtonVisualStatus(
            taskState = null,
            commandFailureCode = "approval_required",
            submitting = false,
            configured = true,
            connected = true
        )
        val unknown = codexButtonVisualStatus(
            taskState = null,
            commandFailureCode = "raw server details and path C:\\secret",
            submitting = false,
            configured = true,
            connected = true
        )

        assertEquals(CodexButtonVisualPhase.Failed, safe?.phase)
        assertEquals("approval_required", safe?.safeFailureCode)
        assertEquals(CodexButtonVisualPhase.Failed, unknown?.phase)
        assertEquals("internal_error", unknown?.safeFailureCode)
    }

    @Test
    fun duplicateTapCreatesOneSubmitAndAcceptedDuplicateReconstructsActiveState() = runBlocking {
        val submitGate = CompletableDeferred<CodexJobApiResult>()
        val taskStates = mutableMapOf<Int, CodexButtonTaskState>()
        val failureCodes = mutableMapOf<Int, String>()
        val submitting = mutableMapOf<Int, Boolean>()
        var submitCalls = 0
        val coordinator = coordinator(
            scope = this,
            taskStates = taskStates,
            failureCodes = failureCodes,
            submitting = submitting,
            submit = { _, _ ->
                submitCalls += 1
                submitGate.await()
            }
        )

        assertTrue(coordinator.submitOnce(BUTTON_ID, SETTINGS, BINDING))
        assertFalse(coordinator.submitOnce(BUTTON_ID, SETTINGS, BINDING))
        yield()
        assertEquals(1, submitCalls)
        assertEquals(true, submitting[BUTTON_ID])

        submitGate.complete(CodexJobApiResult(snapshot = snapshot(CodexJobStatus.Queued, duplicate = true)))
        yield()
        assertEquals(true, taskStates[BUTTON_ID]?.snapshot?.duplicate)
        assertEquals(CodexJobStatus.Queued, taskStates[BUTTON_ID]?.snapshot?.status)
        assertFalse(coordinator.submitOnce(BUTTON_ID, SETTINGS, BINDING))
        coordinator.clear()
    }

    @Test
    fun pollsAt750AndRetriesOneTwoFourWhileRetainingLastSafeState() = runBlocking {
        val delays = ManualDelay()
        val taskStates = mutableMapOf<Int, CodexButtonTaskState>()
        val statusResults = Channel<CodexJobApiResult>(Channel.UNLIMITED)
        var statusCalls = 0
        val coordinator = coordinator(
            scope = this,
            taskStates = taskStates,
            delayMillis = delays::await,
            submit = { _, _ -> CodexJobApiResult(snapshot = snapshot(CodexJobStatus.Queued)) },
            status = { _, _, _ ->
                statusCalls += 1
                statusResults.receive()
            }
        )

        assertTrue(coordinator.submitOnce(BUTTON_ID, SETTINGS, BINDING))
        yield()
        assertEquals(CODEX_JOB_POLL_INTERVAL_MILLIS, delays.next().also { it.resume() }.millis)

        statusResults.send(CodexJobApiResult(reconnectRequired = true))
        yield()
        assertEquals(CodexJobStatus.Queued, taskStates[BUTTON_ID]?.snapshot?.status)
        assertEquals(true, taskStates[BUTTON_ID]?.reconnecting)
        assertEquals(1_000L, delays.next().also { it.resume() }.millis)

        statusResults.send(CodexJobApiResult(reconnectRequired = true))
        yield()
        assertEquals(2_000L, delays.next().also { it.resume() }.millis)

        statusResults.send(CodexJobApiResult(reconnectRequired = true))
        yield()
        assertEquals(4_000L, delays.next().also { it.resume() }.millis)

        statusResults.send(CodexJobApiResult(snapshot = snapshot(CodexJobStatus.Running, elapsedMs = 750L)))
        yield()
        assertEquals(4, statusCalls)
        assertEquals(false, taskStates[BUTTON_ID]?.reconnecting)
        assertEquals(CodexJobStatus.Running, taskStates[BUTTON_ID]?.snapshot?.status)
        assertEquals(CODEX_JOB_POLL_INTERVAL_MILLIS, delays.next().millis)
        coordinator.clear()
    }

    @Test
    fun completedAndCancelledExpireAtExactFiveSecondsAndCancelledSuppressesTap() = runBlocking {
        val delays = ManualDelay()
        val taskStates = mutableMapOf<Int, CodexButtonTaskState>()
        val terminal = Channel<CodexJobSnapshot>(Channel.UNLIMITED)
        var nowMillis = 20_000L
        val coordinator = coordinator(
            scope = this,
            taskStates = taskStates,
            delayMillis = delays::await,
            nowMillis = { nowMillis },
            submit = { _, _ -> CodexJobApiResult(snapshot = terminal.receive()) }
        )

        terminal.send(snapshot(CodexJobStatus.Cancelled))
        assertTrue(coordinator.submitOnce(BUTTON_ID, SETTINGS, BINDING))
        yield()
        assertFalse(coordinator.submitOnce(BUTTON_ID, SETTINGS, BINDING))
        val cancelledExpiry = delays.next()
        assertEquals(CODEX_TERMINAL_DISPLAY_MILLIS, cancelledExpiry.millis)
        nowMillis += CODEX_TERMINAL_DISPLAY_MILLIS
        cancelledExpiry.resume()
        yield()
        assertNull(taskStates[BUTTON_ID])

        terminal.send(snapshot(CodexJobStatus.Completed))
        assertTrue(coordinator.submitOnce(BUTTON_ID, SETTINGS, BINDING))
        yield()
        val completedExpiry = delays.next()
        assertEquals(CODEX_TERMINAL_DISPLAY_MILLIS, completedExpiry.millis)
        nowMillis += CODEX_TERMINAL_DISPLAY_MILLIS
        completedExpiry.resume()
        yield()
        assertNull(taskStates[BUTTON_ID])
        coordinator.clear()
    }

    @Test
    fun failedPersistsUntilNextTapAndLifecycleCleanupBoundsEveryMap() = runBlocking {
        val taskStates = mutableMapOf<Int, CodexButtonTaskState>()
        val failureCodes = mutableMapOf<Int, String>()
        val submitting = mutableMapOf<Int, Boolean>()
        val firstSubmit = CompletableDeferred<CodexJobApiResult>()
        var submitCalls = 0
        val coordinator = coordinator(
            scope = this,
            taskStates = taskStates,
            failureCodes = failureCodes,
            submitting = submitting,
            submit = { _, _ ->
                submitCalls += 1
                if (submitCalls == 1) CodexJobApiResult(failureCode = "execution_disabled") else firstSubmit.await()
            }
        )

        assertTrue(coordinator.submitOnce(BUTTON_ID, SETTINGS, BINDING))
        yield()
        assertEquals("execution_disabled", failureCodes[BUTTON_ID])
        assertTrue(coordinator.submitOnce(BUTTON_ID, SETTINGS, BINDING))
        yield()
        assertNull(failureCodes[BUTTON_ID])
        assertEquals(2, submitCalls)

        coordinator.retainBindings(emptyMap())
        assertTrue(taskStates.isEmpty())
        assertTrue(failureCodes.isEmpty())
        assertTrue(submitting.isEmpty())
        coordinator.clear()
        assertTrue(taskStates.isEmpty())
        assertTrue(failureCodes.isEmpty())
        assertTrue(submitting.isEmpty())
    }

    @Test
    fun releaseAllowsOnlyConfiguredStrictCodexRouteWithoutPersistedTaskFields() {
        val validPayload = """{"programId":"codex","command":"exec.submit","args":{"contractVersion":1,"presetId":"preset-1","bindingId":"binding-1"}}"""
        val validButton = button(validPayload)
        val rejectedPayloads = listOf(
            """{"programId":"obs","command":"scene.set","args":{}}""",
            """{"programId":"codex","command":"exec.cancel","args":{"contractVersion":1,"jobId":"job-1"}}""",
            """{"programId":"codex","command":"exec.submit","args":{"contractVersion":1,"presetId":"preset-1","bindingId":"binding-1","prompt":"raw"}}""",
            "raw prompt"
        )

        assertEquals(
            DeckButtonExecutionRoute.CompanionProgramCommand,
            validButton.executionDecision(
                companionAvailable = true,
                debugBuild = false,
                companionSettings = SETTINGS
            ).route
        )
        assertEquals(
            DeckButtonExecutionRoute.Unavailable,
            validButton.executionDecision(
                companionAvailable = true,
                debugBuild = false,
                companionSettings = SETTINGS.copy(enabled = false)
            ).route
        )
        rejectedPayloads.forEach { payload ->
            assertEquals(
                DeckButtonExecutionRoute.Unavailable,
                button(payload).executionDecision(
                    companionAvailable = true,
                    debugBuild = false,
                    companionSettings = SETTINGS
                ).route
            )
        }

        val persistedFields = DeckButton::class.java.declaredFields.map { it.name }.toSet()
        assertFalse(persistedFields.any { it.contains("job", ignoreCase = true) })
        assertFalse(persistedFields.any { it.contains("status", ignoreCase = true) })
        assertFalse(persistedFields.any { it.contains("reconnect", ignoreCase = true) })
    }

    private fun coordinator(
        scope: CoroutineScope,
        taskStates: MutableMap<Int, CodexButtonTaskState> = mutableMapOf(),
        failureCodes: MutableMap<Int, String> = mutableMapOf(),
        submitting: MutableMap<Int, Boolean> = mutableMapOf(),
        delayMillis: suspend (Long) -> Unit = { CompletableDeferred<Unit>().await() },
        nowMillis: () -> Long = { 1_000L },
        submit: suspend (CompanionSettings, CodexButtonBindingPayload) -> CodexJobApiResult,
        status: suspend (CompanionSettings, String, String) -> CodexJobApiResult = { _, _, _ ->
            CompletableDeferred<CodexJobApiResult>().await()
        }
    ): CodexButtonTaskCoordinator {
        return CodexButtonTaskCoordinator(
            scope = scope,
            taskStates = taskStates,
            commandFailureCodes = failureCodes,
            submittingButtons = submitting,
            submitJob = submit,
            pollJob = status,
            delayMillis = delayMillis,
            nowMillis = nowMillis
        )
    }

    private fun taskState(
        snapshot: CodexJobSnapshot,
        terminalObservedAtMillis: Long? = null
    ): CodexButtonTaskState {
        return CodexButtonTaskState(
            snapshot = snapshot,
            terminalObservedAtMillis = terminalObservedAtMillis
        )
    }

    private fun snapshot(
        status: CodexJobStatus,
        duplicate: Boolean = false,
        elapsedMs: Long = 0L,
        errorCode: String? = null
    ): CodexJobSnapshot {
        val statusValue = status.name.lowercase()
        val startedAt = if (status == CodexJobStatus.Queued) "null" else "\"2026-07-17T00:00:01Z\""
        val finishedAt = if (status.isTerminal) "\"2026-07-17T00:00:02Z\"" else "null"
        val message = when (status) {
            CodexJobStatus.Queued -> "Queued"
            CodexJobStatus.Running -> "Running"
            CodexJobStatus.Completed -> "Completed"
            CodexJobStatus.Failed -> "Codex job failed"
            CodexJobStatus.Cancelled -> "Cancelled"
        }
        val error = errorCode?.let { "\"$it\"" } ?: "null"
        return requireNotNull(
            CodexJobSnapshot.parse(
                """{"contractVersion":1,"jobId":"job-1","bindingId":"binding-1","presetId":"preset-1","status":"$statusValue","duplicate":$duplicate,"acceptedAt":"2026-07-17T00:00:00Z","updatedAt":"2026-07-17T00:00:02Z","startedAt":$startedAt,"finishedAt":$finishedAt,"elapsedMs":$elapsedMs,"cancelRequested":false,"errorCode":$error,"message":"$message","summary":null}"""
            )
        )
    }

    private fun button(payload: String): DeckButton {
        return DeckButton(
            id = BUTTON_ID,
            title = "Codex",
            subtitle = "",
            icon = "",
            iconImageUri = "",
            displayMode = DeckDisplayMode.IconAndText,
            actionType = DeckActionType.CompanionCommand,
            payload = payload,
            color = androidx.compose.ui.graphics.Color.Black
        )
    }

    private class ManualDelay {
        private val waits = Channel<Wait>(Channel.UNLIMITED)

        suspend fun await(millis: Long) {
            val gate = CompletableDeferred<Unit>()
            waits.send(Wait(millis, gate))
            gate.await()
        }

        suspend fun next(): Wait = waits.receive()

        data class Wait(
            val millis: Long,
            private val gate: CompletableDeferred<Unit>
        ) {
            fun resume() {
                gate.complete(Unit)
            }
        }
    }

    companion object {
        private const val BUTTON_ID = 42
        private val SETTINGS = CompanionSettings(
            enabled = true,
            endpoint = "ws://127.0.0.1:8765",
            pairingToken = "paired-token"
        )
        private val BINDING = CodexButtonBindingPayload(
            presetId = "preset-1",
            bindingId = "binding-1"
        )
    }
}
