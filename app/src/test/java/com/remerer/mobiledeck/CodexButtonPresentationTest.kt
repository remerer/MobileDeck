package com.remerer.mobiledeck

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CodexButtonPresentationTest {
    @Test
    fun cleanReleaseCanPersistConfigurationAndEnableStrictCodexRouteOnly() {
        val policy = CompanionSettingsAccessPolicy.forBuild(debugBuild = false)
        val configured = policy.configurationUpdate(
            current = CompanionSettings(),
            proposed = SETTINGS
        )

        assertEquals(SETTINGS, configured)
        assertTrue(configured.isConfigured())
        assertTrue(CompanionReleaseRoutePolicy.allowsCodexSubmit(configured, bindingJson(BINDING_A)))
        assertTrue(policy.canAccess(CompanionSettingsFeature.Enabled))
        assertTrue(policy.canAccess(CompanionSettingsFeature.Endpoint))
        assertTrue(policy.canAccess(CompanionSettingsFeature.PairingToken))
        assertTrue(policy.canAccess(CompanionSettingsFeature.ConnectionStatus))
        CompanionSettingsFeature.debugOnly.forEach { feature ->
            assertFalse(feature.name, policy.canAccess(feature))
        }
    }

    @Test
    fun sameNumericIdInClassicAndConsoleGetsIndependentBindingOwners() {
        val classic = button(BUTTON_ID, "Classic Codex", BINDING_A)
        val console = button(BUTTON_ID, "Console Codex", BINDING_B)
        val registry = CodexButtonOwnerRegistry()

        val owners = registry.reconcile(
            listOf(page(classicButtons = listOf(classic), consoleButtons = listOf(console)))
        )

        val classicOwner = requireNotNull(registry.ownerFor(PAGE_ID, DeckUiMode.Classic, classic))
        val consoleOwner = requireNotNull(registry.ownerFor(PAGE_ID, DeckUiMode.Console, console))
        assertNotEquals(classicOwner, consoleOwner)
        assertEquals(DeckUiMode.Classic, classicOwner.presentation)
        assertEquals(DeckUiMode.Console, consoleOwner.presentation)
        assertEquals(BINDING_A, owners[classicOwner])
        assertEquals(BINDING_B, owners[consoleOwner])
    }

    @Test
    fun sharedModelInstanceStillGetsDifferentClassicAndConsoleOwners() {
        val shared = button(BUTTON_ID, "Shared Codex", BINDING_A)
        val registry = CodexButtonOwnerRegistry()

        registry.reconcile(pageList(shared, shared))

        val classicOwner = requireNotNull(registry.ownerFor(PAGE_ID, DeckUiMode.Classic, shared))
        val consoleOwner = requireNotNull(registry.ownerFor(PAGE_ID, DeckUiMode.Console, shared))
        assertNotEquals(classicOwner, consoleOwner)
        assertEquals(DeckUiMode.Classic, classicOwner.presentation)
        assertEquals(DeckUiMode.Console, consoleOwner.presentation)
    }

    @Test
    fun replacementAndIdReuseAdvanceGenerationWithoutRekeyingUnchangedPresentation() {
        val classic = button(BUTTON_ID, "Classic Codex", BINDING_A)
        val console = button(BUTTON_ID, "Console Codex", BINDING_B)
        val registry = CodexButtonOwnerRegistry()
        registry.reconcile(pageList(classic, console))
        val firstClassicOwner = requireNotNull(registry.ownerFor(PAGE_ID, DeckUiMode.Classic, classic))
        val firstConsoleOwner = requireNotNull(registry.ownerFor(PAGE_ID, DeckUiMode.Console, console))

        val replacement = button(BUTTON_ID, "Classic replacement", BINDING_B)
        registry.reconcile(pageList(replacement, console))
        val replacementOwner = requireNotNull(registry.ownerFor(PAGE_ID, DeckUiMode.Classic, replacement))

        assertNotEquals(firstClassicOwner, replacementOwner)
        assertTrue(replacementOwner.generation > firstClassicOwner.generation)
        assertEquals(firstConsoleOwner, registry.ownerFor(PAGE_ID, DeckUiMode.Console, console))
        assertNull(registry.ownerFor(PAGE_ID, DeckUiMode.Classic, classic))

        registry.reconcile(emptyList())
        registry.reconcile(pageList(replacement, console))
        assertTrue(
            requireNotNull(registry.ownerFor(PAGE_ID, DeckUiMode.Classic, replacement)).generation >
                replacementOwner.generation
        )
    }

    @Test
    fun simultaneousSameIdOwnersSubmitIndependently() = runBlocking {
        val taskStates = mutableMapOf<CodexButtonOwnerKey, CodexButtonTaskState>()
        val failures = mutableMapOf<CodexButtonOwnerKey, String>()
        val submitting = mutableMapOf<CodexButtonOwnerKey, Boolean>()
        val gates = mapOf(
            BINDING_A.bindingId to CompletableDeferred<CodexJobApiResult>(),
            BINDING_B.bindingId to CompletableDeferred<CodexJobApiResult>()
        )
        val submitted = mutableListOf<String>()
        val coordinator = CodexButtonTaskCoordinator(
            scope = this,
            taskStates = taskStates,
            commandFailureCodes = failures,
            submittingButtons = submitting,
            submitJob = { _, binding ->
                submitted += binding.bindingId
                requireNotNull(gates[binding.bindingId]).await()
            },
            pollJob = { _, _, _ -> CompletableDeferred<CodexJobApiResult>().await() },
            nowMillis = { 1_000L }
        )
        val classicOwner = owner(DeckUiMode.Classic, BINDING_A, generation = 1)
        val consoleOwner = owner(DeckUiMode.Console, BINDING_B, generation = 2)

        assertTrue(coordinator.submitOnce(classicOwner, SETTINGS, BINDING_A))
        assertTrue(coordinator.submitOnce(consoleOwner, SETTINGS, BINDING_B))
        assertFalse(coordinator.submitOnce(classicOwner, SETTINGS, BINDING_A))
        yield()

        assertEquals(setOf(BINDING_A.bindingId, BINDING_B.bindingId), submitted.toSet())
        assertEquals(setOf(classicOwner, consoleOwner), submitting.keys)
        coordinator.clear()
    }

    @Test
    fun staleUncooperativeResponseCannotRestoreRemovedGeneration() = runBlocking {
        val taskStates = mutableMapOf<CodexButtonOwnerKey, CodexButtonTaskState>()
        val failures = mutableMapOf<CodexButtonOwnerKey, String>()
        val submitting = mutableMapOf<CodexButtonOwnerKey, Boolean>()
        val response = CompletableDeferred<CodexJobApiResult>()
        val coordinator = CodexButtonTaskCoordinator(
            scope = this,
            taskStates = taskStates,
            commandFailureCodes = failures,
            submittingButtons = submitting,
            submitJob = { _, _ -> withContext(NonCancellable) { response.await() } },
            pollJob = { _, _, _ -> CompletableDeferred<CodexJobApiResult>().await() },
            nowMillis = { 1_000L }
        )
        val staleOwner = owner(DeckUiMode.Classic, BINDING_A, generation = 1)
        val replacementOwner = owner(DeckUiMode.Classic, BINDING_B, generation = 2)

        assertTrue(coordinator.submitOnce(staleOwner, SETTINGS, BINDING_A))
        yield()
        coordinator.retainBindings(mapOf(replacementOwner to BINDING_B))
        response.complete(CodexJobApiResult(snapshot = snapshot(BINDING_A)))
        yield()

        assertTrue(taskStates.isEmpty())
        assertTrue(failures.isEmpty())
        assertTrue(submitting.isEmpty())
        coordinator.clear()
    }

    @Test
    fun retentionAndClearBoundAllOwnerMapsAcrossGenerations() = runBlocking {
        val taskStates = mutableMapOf<CodexButtonOwnerKey, CodexButtonTaskState>()
        val failures = mutableMapOf<CodexButtonOwnerKey, String>()
        val submitting = mutableMapOf<CodexButtonOwnerKey, Boolean>()
        val coordinator = CodexButtonTaskCoordinator(
            scope = this,
            taskStates = taskStates,
            commandFailureCodes = failures,
            submittingButtons = submitting,
            submitJob = { _, _ -> CodexJobApiResult(failureCode = "execution_disabled") },
            pollJob = { _, _, _ -> CompletableDeferred<CodexJobApiResult>().await() },
            nowMillis = { 1_000L }
        )
        val owners = (1L..24L).map { generation ->
            owner(DeckUiMode.Classic, BINDING_A, generation)
        }

        owners.forEach { owner ->
            assertTrue(coordinator.submitOnce(owner, SETTINGS, BINDING_A))
            yield()
        }
        assertEquals(24, failures.size)

        val retained = owners.last()
        coordinator.retainBindings(mapOf(retained to BINDING_A))
        assertEquals(setOf(retained), failures.keys)
        assertTrue(taskStates.isEmpty())
        assertTrue(submitting.isEmpty())

        coordinator.clear()
        assertTrue(taskStates.isEmpty())
        assertTrue(failures.isEmpty())
        assertTrue(submitting.isEmpty())
    }

    private fun pageList(classic: DeckButton, console: DeckButton): List<DeckPageConfig> {
        return listOf(page(listOf(classic), listOf(console)))
    }

    private fun page(
        classicButtons: List<DeckButton>,
        consoleButtons: List<DeckButton>
    ): DeckPageConfig {
        return DeckPageConfig(
            id = PAGE_ID,
            name = "Page",
            buttons = classicButtons,
            classicButtons = classicButtons,
            consoleButtons = consoleButtons
        )
    }

    private fun button(id: Int, title: String, binding: CodexButtonBindingPayload): DeckButton {
        return DeckButton(
            id = id,
            title = title,
            subtitle = "",
            icon = "",
            iconImageUri = "",
            displayMode = DeckDisplayMode.IconAndText,
            actionType = DeckActionType.CompanionCommand,
            payload = bindingJson(binding),
            color = Color.Black
        )
    }

    private fun owner(
        presentation: DeckUiMode,
        binding: CodexButtonBindingPayload,
        generation: Long
    ): CodexButtonOwnerKey {
        return CodexButtonOwnerKey(
            pageId = PAGE_ID,
            presentation = presentation,
            buttonId = BUTTON_ID,
            presetId = binding.presetId,
            bindingId = binding.bindingId,
            generation = generation
        )
    }

    private fun snapshot(binding: CodexButtonBindingPayload): CodexJobSnapshot {
        return requireNotNull(
            CodexJobSnapshot.parse(
                """{"contractVersion":1,"jobId":"job-1","bindingId":"${binding.bindingId}","presetId":"${binding.presetId}","status":"queued","duplicate":false,"acceptedAt":"2026-07-17T00:00:00Z","updatedAt":"2026-07-17T00:00:00Z","startedAt":null,"finishedAt":null,"elapsedMs":0,"cancelRequested":false,"errorCode":null,"message":"Queued","summary":null}"""
            )
        )
    }

    private fun bindingJson(binding: CodexButtonBindingPayload): String {
        return """{"programId":"codex","command":"exec.submit","args":{"contractVersion":1,"presetId":"${binding.presetId}","bindingId":"${binding.bindingId}"}}"""
    }

    companion object {
        private const val PAGE_ID = 7
        private const val BUTTON_ID = 42
        private val SETTINGS = CompanionSettings(
            enabled = true,
            endpoint = "ws://127.0.0.1:8765",
            pairingToken = "paired-token"
        )
        private val BINDING_A = CodexButtonBindingPayload(
            presetId = "preset-classic",
            bindingId = "binding-classic"
        )
        private val BINDING_B = CodexButtonBindingPayload(
            presetId = "preset-console",
            bindingId = "binding-console"
        )
    }
}
