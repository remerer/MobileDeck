package com.remerer.mobiledeck

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Density
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import android.content.res.Configuration
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
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class CodexButtonStatusInstrumentedTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun actualPrimaryPresentationReplacesRegularContentForEveryTransientState() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val statuses = listOf(
            codexButtonVisualStatus(taskState(snapshot(CodexJobStatus.Queued)), null, false, true, true),
            codexButtonVisualStatus(taskState(snapshot(CodexJobStatus.Running, elapsedMs = 65_000L)), null, false, true, true),
            codexButtonVisualStatus(taskState(snapshot(CodexJobStatus.Completed), 10L), null, false, true, true),
            codexButtonVisualStatus(taskState(snapshot(CodexJobStatus.Failed, errorCode = "exec_failed")), null, false, true, true),
            codexButtonVisualStatus(taskState(snapshot(CodexJobStatus.Cancelled), 10L), null, false, true, true),
            codexButtonVisualStatus(taskState(snapshot(CodexJobStatus.Queued)).copy(reconnecting = true), null, false, true, false),
            codexButtonVisualStatus(null, null, false, false, false),
            codexButtonVisualStatus(null, null, false, true, false)
        ).map(::requireNotNull)

        composeRule.setContent {
            Column {
                statuses.chunked(4).forEachIndexed { rowIndex, rowStatuses ->
                    Row {
                        rowStatuses.forEachIndexed { columnIndex, status ->
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .testTag("fixed-button-$rowIndex-$columnIndex")
                            ) {
                                DeckButtonPrimaryPresentation(
                                    modifier = Modifier.fillMaxSize(),
                                    status = status,
                                    isConsole = columnIndex % 2 == 1
                                ) {
                                    Text("regular", Modifier.testTag("regular-$rowIndex-$columnIndex"))
                                }
                            }
                        }
                    }
                }
            }
        }

        statuses.forEachIndexed { index, _ ->
            val tag = "fixed-button-${index / 4}-${index % 4}"
            composeRule.onNodeWithTag(tag)
                .assertWidthIsEqualTo(72.dp)
                .assertHeightIsEqualTo(72.dp)
            composeRule.onNodeWithTag("regular-${index / 4}-${index % 4}").assertDoesNotExist()
        }
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
    fun longestAllowlistedCodeFitsEnglishAtDefaultFontScale() {
        assertLongestStatusFits(Locale.ENGLISH, fontScale = 1f)
    }

    @Test
    fun longestAllowlistedCodeFitsEnglishAtAccessibilityFontScale() {
        assertLongestStatusFits(Locale.ENGLISH, fontScale = 2.5f)
    }

    @Test
    fun longestAllowlistedCodeFitsKoreanAtDefaultFontScale() {
        assertLongestStatusFits(Locale.KOREAN, fontScale = 1f)
    }

    @Test
    fun longestAllowlistedCodeFitsKoreanAtAccessibilityFontScale() {
        assertLongestStatusFits(Locale.KOREAN, fontScale = 2.5f)
    }

    @Test
    fun statusLabelsPreserveFullFailureCodeInEnglishAndKorean() {
        val base = InstrumentationRegistry.getInstrumentation().targetContext
        val failed = CodexButtonVisualStatus(
            phase = CodexButtonVisualPhase.Failed,
            safeFailureCode = "execution_mode_insufficient"
        )
        val english = localizedResources(base.resources.configuration, Locale.ENGLISH)
        val korean = localizedResources(base.resources.configuration, Locale.KOREAN)

        assertEquals(
            "Failed: execution_mode_insufficient",
            codexButtonStatusLabel(base.createConfigurationContext(english).resources, failed)
        )
        assertEquals(
            "실패: execution_mode_insufficient",
            codexButtonStatusLabel(base.createConfigurationContext(korean).resources, failed)
        )
    }

    @Test
    fun cleanReleaseConfigurationSurfaceEnablesStrictRouteAndExposesNoDebugActions() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        var configured = CompanionSettings()
        composeRule.setContent {
            var settings by remember { mutableStateOf(CompanionSettings()) }
            CompanionReleaseConfigurationContent(
                modifier = Modifier.fillMaxSize(),
                settings = settings,
                status = CompanionConnectionStatus(connected = true, message = "Connected"),
                onSettingsChange = { updated ->
                    settings = updated
                    configured = updated
                }
            )
        }

        composeRule.onNodeWithTag(CompanionReleaseSettingsTag).assertExists()
        composeRule.onNodeWithTag(CompanionEnabledSettingTag).assertExists()
        composeRule.onNodeWithTag(CompanionEndpointSettingTag).assertExists()
        composeRule.onNodeWithTag(CompanionPairingTokenSettingTag).assertExists()
        composeRule.onNodeWithTag(CompanionPairingTokenSettingTag).assert(
            SemanticsMatcher.keyIsDefined(SemanticsProperties.Password)
        )
        composeRule.onNodeWithTag(CompanionConnectionStatusTag).assertExists()
        composeRule.onNodeWithText(context.getString(R.string.companion_scan_qr)).assertDoesNotExist()
        composeRule.onNodeWithText(context.getString(R.string.companion_test_connection)).assertDoesNotExist()
        composeRule.onNodeWithText(context.getString(R.string.companion_send_deck)).assertDoesNotExist()
        composeRule.onNodeWithText(context.getString(R.string.companion_apply_deck)).assertDoesNotExist()
        composeRule.onNodeWithTag(CompanionEndpointSettingTag).performTextInput(SETTINGS.endpoint)
        composeRule.onNodeWithTag(CompanionPairingTokenSettingTag).performTextInput(SETTINGS.pairingToken)
        composeRule.onAllNodes(hasText(SETTINGS.pairingToken)).assertCountEquals(0)
        composeRule.onNode(isToggleable()).performClick()
        composeRule.runOnIdle {
            assertEquals(SETTINGS, configured)
            assertTrue(
                CompanionReleaseRoutePolicy.allowsCodexSubmit(
                    configured,
                    bindingPayload(BINDING)
                )
            )
        }
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
        val taskStates = mutableMapOf<CodexButtonOwnerKey, CodexButtonTaskState>()
        val failureCodes = mutableMapOf<CodexButtonOwnerKey, String>()
        val submitting = mutableMapOf<CodexButtonOwnerKey, Boolean>()
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

        assertTrue(coordinator.submitOnce(OWNER, SETTINGS))
        assertFalse(coordinator.submitOnce(OWNER, SETTINGS))
        yield()
        assertEquals(1, submitCalls)
        assertEquals(true, submitting[OWNER])

        submitGate.complete(CodexJobApiResult(snapshot = snapshot(CodexJobStatus.Queued, duplicate = true)))
        yield()
        assertEquals(true, taskStates[OWNER]?.snapshot?.duplicate)
        assertEquals(CodexJobStatus.Queued, taskStates[OWNER]?.snapshot?.status)
        assertFalse(coordinator.submitOnce(OWNER, SETTINGS))
        coordinator.clear()
    }

    @Test
    fun pollsAt750AndRetriesOneTwoFourWhileRetainingLastSafeState() = runBlocking {
        val delays = ManualDelay()
        val taskStates = mutableMapOf<CodexButtonOwnerKey, CodexButtonTaskState>()
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

        assertTrue(coordinator.submitOnce(OWNER, SETTINGS))
        yield()
        assertEquals(CODEX_JOB_POLL_INTERVAL_MILLIS, delays.next().also { it.resume() }.millis)

        statusResults.send(CodexJobApiResult(reconnectRequired = true))
        yield()
        assertEquals(CodexJobStatus.Queued, taskStates[OWNER]?.snapshot?.status)
        assertEquals(true, taskStates[OWNER]?.reconnecting)
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
        assertEquals(false, taskStates[OWNER]?.reconnecting)
        assertEquals(CodexJobStatus.Running, taskStates[OWNER]?.snapshot?.status)
        assertEquals(CODEX_JOB_POLL_INTERVAL_MILLIS, delays.next().millis)
        coordinator.clear()
    }

    @Test
    fun completedAndCancelledExpireAtExactFiveSecondsAndCancelledSuppressesTap() = runBlocking {
        val delays = ManualDelay()
        val taskStates = mutableMapOf<CodexButtonOwnerKey, CodexButtonTaskState>()
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
        assertTrue(coordinator.submitOnce(OWNER, SETTINGS))
        yield()
        assertFalse(coordinator.submitOnce(OWNER, SETTINGS))
        val cancelledExpiry = delays.next()
        assertEquals(CODEX_TERMINAL_DISPLAY_MILLIS, cancelledExpiry.millis)
        nowMillis += CODEX_TERMINAL_DISPLAY_MILLIS
        cancelledExpiry.resume()
        yield()
        assertNull(taskStates[OWNER])

        terminal.send(snapshot(CodexJobStatus.Completed))
        assertTrue(coordinator.submitOnce(OWNER, SETTINGS))
        yield()
        val completedExpiry = delays.next()
        assertEquals(CODEX_TERMINAL_DISPLAY_MILLIS, completedExpiry.millis)
        nowMillis += CODEX_TERMINAL_DISPLAY_MILLIS
        completedExpiry.resume()
        yield()
        assertNull(taskStates[OWNER])
        coordinator.clear()
    }

    @Test
    fun failedPersistsUntilNextTapAndLifecycleCleanupBoundsEveryMap() = runBlocking {
        val taskStates = mutableMapOf<CodexButtonOwnerKey, CodexButtonTaskState>()
        val failureCodes = mutableMapOf<CodexButtonOwnerKey, String>()
        val submitting = mutableMapOf<CodexButtonOwnerKey, Boolean>()
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

        assertTrue(coordinator.submitOnce(OWNER, SETTINGS))
        yield()
        assertEquals("execution_disabled", failureCodes[OWNER])
        assertTrue(coordinator.submitOnce(OWNER, SETTINGS))
        yield()
        assertNull(failureCodes[OWNER])
        assertEquals(2, submitCalls)

        coordinator.reconcileOwners(emptyList())
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

    private fun assertLongestStatusFits(locale: Locale, fontScale: Float) {
        val base = InstrumentationRegistry.getInstrumentation().targetContext
        val localizedContext = base.createConfigurationContext(
            localizedResources(base.resources.configuration, locale)
        )
        val code = "execution_mode_insufficient"
        val label = localizedContext.getString(R.string.codex_status_failed_code, code)
        val failed = codexButtonVisualStatus(null, code, false, true, true)
        val density = Density(base.resources.displayMetrics.density, fontScale)

        composeRule.setContent {
            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalDensity provides density
            ) {
                Row {
                    listOf("classic" to 96.dp, "console" to 58.dp).forEach { (name, size) ->
                        Box(
                            modifier = Modifier
                                .size(size)
                                .testTag("$name-button")
                        ) {
                            DeckButtonPrimaryPresentation(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("$name-presentation"),
                                status = failed,
                                isConsole = name == "console"
                            ) {
                                Text("regular title", Modifier.testTag("$name-regular"))
                            }
                        }
                    }
                }
            }
        }

        composeRule.onNodeWithTag("classic-button")
            .assertWidthIsEqualTo(96.dp)
            .assertHeightIsEqualTo(96.dp)
        composeRule.onNodeWithTag("console-button")
            .assertWidthIsEqualTo(58.dp)
            .assertHeightIsEqualTo(58.dp)
        composeRule.onNodeWithTag("classic-presentation")
            .assertWidthIsEqualTo(96.dp)
            .assertHeightIsEqualTo(96.dp)
        composeRule.onNodeWithTag("console-presentation")
            .assertWidthIsEqualTo(58.dp)
            .assertHeightIsEqualTo(58.dp)
        composeRule.onNodeWithTag("classic-regular").assertDoesNotExist()
        composeRule.onNodeWithTag("console-regular").assertDoesNotExist()
        composeRule.onAllNodesWithContentDescription(label).assertCountEquals(2)
        composeRule.onAllNodes(
            SemanticsMatcher.expectValue(CodexStatusTextFitsKey, true)
        ).assertCountEquals(2)
        composeRule.onAllNodes(
            SemanticsMatcher.expectValue(CodexStatusExactLabelKey, label)
        ).assertCountEquals(2)
        assertNodeInside("classic-button", label, nodeIndex = 0)
        assertNodeInside("console-button", label, nodeIndex = 1)
        listOf(CodexStatusContainerTag, CodexStatusIconTag, CodexStatusTextTag).forEach { childTag ->
            assertTaggedNodeInside("classic-button", childTag, nodeIndex = 0)
            assertTaggedNodeInside("console-button", childTag, nodeIndex = 1)
        }
        assertTaggedNodesDoNotOverlap(CodexStatusIconTag, CodexStatusTextTag, nodeIndex = 0)
        assertTaggedNodesDoNotOverlap(CodexStatusIconTag, CodexStatusTextTag, nodeIndex = 1)
    }

    private fun assertNodeInside(parentTag: String, contentDescription: String, nodeIndex: Int) {
        val parentBounds = composeRule.onNodeWithTag(parentTag).fetchSemanticsNode().boundsInRoot
        val childBounds = composeRule
            .onAllNodesWithContentDescription(contentDescription)[nodeIndex]
            .fetchSemanticsNode()
            .boundsInRoot
        assertTrue("left bound", childBounds.left >= parentBounds.left)
        assertTrue("top bound", childBounds.top >= parentBounds.top)
        assertTrue("right bound", childBounds.right <= parentBounds.right)
        assertTrue("bottom bound", childBounds.bottom <= parentBounds.bottom)
    }

    private fun assertTaggedNodeInside(parentTag: String, childTag: String, nodeIndex: Int) {
        val parentBounds = composeRule.onNodeWithTag(parentTag).fetchSemanticsNode().boundsInRoot
        val childBounds = composeRule
            .onAllNodesWithTag(childTag)[nodeIndex]
            .fetchSemanticsNode()
            .boundsInRoot
        assertTrue("$childTag left bound", childBounds.left >= parentBounds.left)
        assertTrue("$childTag top bound", childBounds.top >= parentBounds.top)
        assertTrue("$childTag right bound", childBounds.right <= parentBounds.right)
        assertTrue("$childTag bottom bound", childBounds.bottom <= parentBounds.bottom)
    }

    private fun assertTaggedNodesDoNotOverlap(firstTag: String, secondTag: String, nodeIndex: Int) {
        val firstBounds = composeRule.onAllNodesWithTag(firstTag)[nodeIndex]
            .fetchSemanticsNode()
            .boundsInRoot
        val secondBounds = composeRule.onAllNodesWithTag(secondTag)[nodeIndex]
            .fetchSemanticsNode()
            .boundsInRoot
        assertTrue(
            "$firstTag and $secondTag overlap",
            firstBounds.bottom <= secondBounds.top || secondBounds.bottom <= firstBounds.top ||
                firstBounds.right <= secondBounds.left || secondBounds.right <= firstBounds.left
        )
    }

    private fun localizedResources(base: Configuration, locale: Locale): Configuration {
        return Configuration(base).apply { setLocale(locale) }
    }

    private fun coordinator(
        scope: CoroutineScope,
        taskStates: MutableMap<CodexButtonOwnerKey, CodexButtonTaskState> = mutableMapOf(),
        failureCodes: MutableMap<CodexButtonOwnerKey, String> = mutableMapOf(),
        submitting: MutableMap<CodexButtonOwnerKey, Boolean> = mutableMapOf(),
        delayMillis: suspend (Long) -> Unit = { CompletableDeferred<Unit>().await() },
        nowMillis: () -> Long = { 1_000L },
        submit: suspend (CompanionSettings, CodexButtonBindingPayload) -> CodexJobApiResult,
        status: suspend (CompanionSettings, String, String) -> CodexJobApiResult = { _, _, _ ->
            CompletableDeferred<CodexJobApiResult>().await()
        }
    ): CodexButtonTaskCoordinator {
        val registry = CodexButtonOwnerRegistry()
        val coordinator = CodexButtonTaskCoordinator(
            scope = scope,
            taskStates = taskStates,
            commandFailureCodes = failureCodes,
            submittingButtons = submitting,
            ownerRegistry = registry,
            submitJob = submit,
            pollJob = status,
            delayMillis = delayMillis,
            nowMillis = nowMillis
        )
        val ownerButton = button(bindingPayload(BINDING))
        coordinator.reconcileOwners(
            listOf(
                DeckPageConfig(
                    id = OWNER.pageId,
                    name = "Page",
                    buttons = listOf(ownerButton),
                    classicButtons = listOf(ownerButton),
                    consoleButtons = emptyList()
                )
            )
        )
        check(coordinator.ownerFor(OWNER.pageId, OWNER.presentation, ownerButton) == OWNER)
        return coordinator
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

    private fun bindingPayload(binding: CodexButtonBindingPayload): String {
        return """{"programId":"codex","command":"exec.submit","args":{"contractVersion":1,"presetId":"${binding.presetId}","bindingId":"${binding.bindingId}"}}"""
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
        private val OWNER = CodexButtonOwnerKey(
            pageId = 7,
            presentation = DeckUiMode.Classic,
            buttonId = BUTTON_ID,
            presetId = BINDING.presetId,
            bindingId = BINDING.bindingId,
            generation = 1L
        )
    }
}
