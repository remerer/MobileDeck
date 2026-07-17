package com.remerer.mobiledeck

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    fun statusLayoutUsesBoundedRenderedTypeAndCompactAccessibilityTier() {
        val normalConsole = codexStatusLayoutSpec(isConsole = true, fontScale = 1f)
        val largeConsole = codexStatusLayoutSpec(isConsole = true, fontScale = 2.5f)
        val largeClassic = codexStatusLayoutSpec(isConsole = false, fontScale = 2.5f)

        assertEquals(10.dp, normalConsole.iconSize)
        assertEquals(3.dp, normalConsole.padding)
        assertEquals(1.dp, normalConsole.spacing)
        assertEquals(7f, normalConsole.fontSize.value * 1f, 0.001f)
        assertEquals(8f, normalConsole.lineHeight.value * 1f, 0.001f)

        assertEquals(8.dp, largeConsole.iconSize)
        assertEquals(1.dp, largeConsole.padding)
        assertEquals(0.dp, largeConsole.spacing)
        assertEquals(8f, largeConsole.fontSize.value * 2.5f, 0.001f)
        assertEquals(8.5f, largeConsole.lineHeight.value * 2.5f, 0.001f)
        assertEquals(5, largeConsole.maxLines)

        assertEquals(12.dp, largeClassic.iconSize)
        assertEquals(3.dp, largeClassic.padding)
        assertEquals(2.dp, largeClassic.spacing)
        assertEquals(11f, largeClassic.fontSize.value * 2.5f, 0.001f)
        assertEquals(12f, largeClassic.lineHeight.value * 2.5f, 0.001f)
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
        val registry = CodexButtonOwnerRegistry()
        val coordinator = CodexButtonTaskCoordinator(
            scope = this,
            taskStates = taskStates,
            commandFailureCodes = failures,
            submittingButtons = submitting,
            ownerRegistry = registry,
            submitJob = { _, binding ->
                submitted += binding.bindingId
                requireNotNull(gates[binding.bindingId]).await()
            },
            pollJob = { _, _, _ -> CompletableDeferred<CodexJobApiResult>().await() },
            nowMillis = { 1_000L }
        )
        val classic = button(BUTTON_ID, "Classic Codex", BINDING_A)
        val console = button(BUTTON_ID, "Console Codex", BINDING_B)
        coordinator.reconcileOwners(pageList(classic, console))
        val classicOwner = requireNotNull(coordinator.ownerFor(PAGE_ID, DeckUiMode.Classic, classic))
        val consoleOwner = requireNotNull(coordinator.ownerFor(PAGE_ID, DeckUiMode.Console, console))

        assertTrue(coordinator.submitOnce(classicOwner, SETTINGS))
        assertTrue(coordinator.submitOnce(consoleOwner, SETTINGS))
        assertFalse(coordinator.submitOnce(classicOwner, SETTINGS))
        yield()

        assertEquals(setOf(BINDING_A.bindingId, BINDING_B.bindingId), submitted.toSet())
        assertEquals(setOf(classicOwner, consoleOwner), submitting.keys)
        coordinator.clear()
    }

    @Test
    fun submitRejectsOwnerAndBindingIdentityMismatchWithoutInvokingClient() = runBlocking {
        var submitCalls = 0
        val registry = CodexButtonOwnerRegistry()
        val coordinator = CodexButtonTaskCoordinator(
            scope = this,
            taskStates = mutableMapOf(),
            commandFailureCodes = mutableMapOf(),
            submittingButtons = mutableMapOf(),
            ownerRegistry = registry,
            submitJob = { _, _ ->
                submitCalls += 1
                CodexJobApiResult(failureCode = "execution_disabled")
            },
            pollJob = { _, _, _ -> CompletableDeferred<CodexJobApiResult>().await() }
        )
        val button = button(BUTTON_ID, "Codex", BINDING_A)
        coordinator.reconcileOwners(
            listOf(page(classicButtons = listOf(button), consoleButtons = emptyList()))
        )
        val owner = requireNotNull(coordinator.ownerFor(PAGE_ID, DeckUiMode.Classic, button))
        val mismatchedOwner = owner.copy(
            presetId = BINDING_B.presetId,
            bindingId = BINDING_B.bindingId
        )

        assertFalse(coordinator.submitOnce(mismatchedOwner, SETTINGS))
        yield()

        assertEquals(0, submitCalls)
        coordinator.clear()
    }

    @Test
    fun retainRejectsMismatchedOwnerAndBindingWithoutRegisteringSubmitIdentity() = runBlocking {
        var submitCalls = 0
        val registry = CodexButtonOwnerRegistry()
        val coordinator = CodexButtonTaskCoordinator(
            scope = this,
            taskStates = mutableMapOf(),
            commandFailureCodes = mutableMapOf(),
            submittingButtons = mutableMapOf(),
            ownerRegistry = registry,
            submitJob = { _, _ ->
                submitCalls += 1
                CodexJobApiResult(failureCode = "execution_disabled")
            },
            pollJob = { _, _, _ -> CompletableDeferred<CodexJobApiResult>().await() }
        )
        val first = button(BUTTON_ID, "Codex A", BINDING_A)
        coordinator.reconcileOwners(
            listOf(page(classicButtons = listOf(first), consoleButtons = emptyList()))
        )
        val staleOwner = requireNotNull(coordinator.ownerFor(PAGE_ID, DeckUiMode.Classic, first))
        val replacement = button(BUTTON_ID, "Codex B", BINDING_B)

        coordinator.reconcileOwners(
            listOf(page(classicButtons = listOf(replacement), consoleButtons = emptyList()))
        )
        assertFalse(coordinator.submitOnce(staleOwner, SETTINGS))
        yield()

        assertEquals(0, submitCalls)
        coordinator.clear()
    }

    @Test
    fun clearInvalidatesStructurallyIdenticalOwnerBeforeReuse() = runBlocking {
        val button = button(BUTTON_ID, "Codex", BINDING_A)
        val pages = pageList(button, button)
        val registry = CodexButtonOwnerRegistry()
        val coordinator = CodexButtonTaskCoordinator(
            scope = this,
            taskStates = mutableMapOf(),
            commandFailureCodes = mutableMapOf(),
            submittingButtons = mutableMapOf(),
            ownerRegistry = registry,
            submitJob = { _, _ -> CodexJobApiResult(failureCode = "execution_disabled") },
            pollJob = { _, _, _ -> CodexJobApiResult(failureCode = "execution_disabled") }
        )
        coordinator.reconcileOwners(pages)
        val firstOwner = requireNotNull(coordinator.ownerFor(PAGE_ID, DeckUiMode.Classic, button))

        coordinator.clear()
        coordinator.reconcileOwners(pages)
        val reusedOwner = requireNotNull(coordinator.ownerFor(PAGE_ID, DeckUiMode.Classic, button))

        assertNotEquals(firstOwner, reusedOwner)
    }

    @Test
    fun generationExhaustionFailsClosedWithoutWrappingOrReusingIdentity() {
        val registry = CodexButtonOwnerRegistry(
            generationFactory = BoundedCodexOwnerGenerationFactory(Long.MAX_VALUE)
        )
        val classic = button(BUTTON_ID, "Classic Codex", BINDING_A)
        val console = button(BUTTON_ID, "Console Codex", BINDING_B)

        val owners = registry.reconcile(pageList(classic, console))

        assertEquals(1, owners.size)
        assertEquals(Long.MAX_VALUE, owners.keys.single().generation)
        registry.reconcile(emptyList())
        assertTrue(registry.reconcile(pageList(classic.copy(), console.copy())).isEmpty())
    }

    @Test
    fun boundedGenerationFactoryIsDeterministicAtLongBoundary() {
        val factory = BoundedCodexOwnerGenerationFactory(Long.MAX_VALUE - 1L)

        assertEquals(Long.MAX_VALUE - 1L, factory.nextGeneration())
        assertEquals(Long.MAX_VALUE, factory.nextGeneration())
        assertNull(factory.nextGeneration())
        assertNull(factory.nextGeneration())
    }

    @Test
    fun clearThenSameOwnerReuseRejectsDeferredOldCompletion() = runBlocking {
        val taskStates = mutableMapOf<CodexButtonOwnerKey, CodexButtonTaskState>()
        val failures = mutableMapOf<CodexButtonOwnerKey, String>()
        val submitting = mutableMapOf<CodexButtonOwnerKey, Boolean>()
        val firstResponse = CompletableDeferred<CodexJobApiResult>()
        val secondResponse = CompletableDeferred<CodexJobApiResult>()
        var submitCalls = 0
        val coordinator = CodexButtonTaskCoordinator(
            scope = this,
            taskStates = taskStates,
            commandFailureCodes = failures,
            submittingButtons = submitting,
            ownerRegistry = CodexButtonOwnerRegistry(),
            submitJob = { _, _ ->
                submitCalls += 1
                if (submitCalls == 1) {
                    withContext(NonCancellable) { firstResponse.await() }
                } else {
                    secondResponse.await()
                }
            },
            pollJob = { _, _, _ -> CompletableDeferred<CodexJobApiResult>().await() },
            nowMillis = { 1_000L }
        )
        val button = button(BUTTON_ID, "Codex", BINDING_A)
        val pages = listOf(page(classicButtons = listOf(button), consoleButtons = emptyList()))
        coordinator.reconcileOwners(pages)
        val firstOwner = requireNotNull(coordinator.ownerFor(PAGE_ID, DeckUiMode.Classic, button))

        assertTrue(coordinator.submitOnce(firstOwner, SETTINGS))
        yield()
        coordinator.resetOwners(pages)
        val secondOwner = requireNotNull(coordinator.ownerFor(PAGE_ID, DeckUiMode.Classic, button))
        assertNotEquals(firstOwner, secondOwner)
        assertTrue(coordinator.submitOnce(secondOwner, SETTINGS))
        yield()

        firstResponse.complete(CodexJobApiResult(snapshot = snapshot(BINDING_A)))
        yield()
        assertFalse(taskStates.containsKey(firstOwner))
        assertNull(taskStates[secondOwner])
        assertEquals(true, submitting[secondOwner])

        secondResponse.complete(CodexJobApiResult(failureCode = "execution_disabled"))
        yield()
        assertEquals("execution_disabled", failures[secondOwner])
        coordinator.clear()
    }

    @Test
    fun resetOwnersRekeysStructurallyEqualDeckReplacement() = runBlocking {
        val coordinator = CodexButtonTaskCoordinator(
            scope = this,
            taskStates = mutableMapOf(),
            commandFailureCodes = mutableMapOf(),
            submittingButtons = mutableMapOf(),
            ownerRegistry = CodexButtonOwnerRegistry(),
            submitJob = { _, _ -> CodexJobApiResult(failureCode = "execution_disabled") },
            pollJob = { _, _, _ -> CodexJobApiResult(failureCode = "execution_disabled") }
        )
        val original = button(BUTTON_ID, "Codex", BINDING_A)
        val originalPages = listOf(
            page(classicButtons = listOf(original), consoleButtons = emptyList())
        )
        coordinator.reconcileOwners(originalPages)
        val firstOwner = requireNotNull(coordinator.ownerFor(PAGE_ID, DeckUiMode.Classic, original))
        val replacement = original.copy()
        val replacementPages = listOf(
            page(classicButtons = listOf(replacement), consoleButtons = emptyList())
        )
        assertEquals(originalPages, replacementPages)

        coordinator.resetOwners(replacementPages)
        val replacementOwner = requireNotNull(
            coordinator.ownerFor(PAGE_ID, DeckUiMode.Classic, replacement)
        )

        assertNotEquals(firstOwner, replacementOwner)
        assertNull(coordinator.ownerFor(PAGE_ID, DeckUiMode.Classic, original))
        coordinator.clear()
    }

    @Test
    fun staleUncooperativeResponseCannotRestoreRemovedGeneration() = runBlocking {
        val taskStates = mutableMapOf<CodexButtonOwnerKey, CodexButtonTaskState>()
        val failures = mutableMapOf<CodexButtonOwnerKey, String>()
        val submitting = mutableMapOf<CodexButtonOwnerKey, Boolean>()
        val response = CompletableDeferred<CodexJobApiResult>()
        val registry = CodexButtonOwnerRegistry()
        val coordinator = CodexButtonTaskCoordinator(
            scope = this,
            taskStates = taskStates,
            commandFailureCodes = failures,
            submittingButtons = submitting,
            ownerRegistry = registry,
            submitJob = { _, _ -> withContext(NonCancellable) { response.await() } },
            pollJob = { _, _, _ -> CompletableDeferred<CodexJobApiResult>().await() },
            nowMillis = { 1_000L }
        )
        val staleButton = button(BUTTON_ID, "Codex A", BINDING_A)
        coordinator.reconcileOwners(
            listOf(page(classicButtons = listOf(staleButton), consoleButtons = emptyList()))
        )
        val staleOwner = requireNotNull(coordinator.ownerFor(PAGE_ID, DeckUiMode.Classic, staleButton))

        assertTrue(coordinator.submitOnce(staleOwner, SETTINGS))
        yield()
        val replacement = button(BUTTON_ID, "Codex B", BINDING_B)
        coordinator.reconcileOwners(
            listOf(page(classicButtons = listOf(replacement), consoleButtons = emptyList()))
        )
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
            ownerRegistry = CodexButtonOwnerRegistry(),
            submitJob = { _, _ -> CodexJobApiResult(failureCode = "execution_disabled") },
            pollJob = { _, _, _ -> CompletableDeferred<CodexJobApiResult>().await() },
            nowMillis = { 1_000L }
        )
        val buttons = (1..24).map { id -> button(id, "Codex $id", BINDING_A) }
        coordinator.reconcileOwners(
            listOf(page(classicButtons = buttons, consoleButtons = emptyList()))
        )
        val owners = buttons.map { button ->
            requireNotNull(coordinator.ownerFor(PAGE_ID, DeckUiMode.Classic, button))
        }

        owners.forEach { owner ->
            assertTrue(coordinator.submitOnce(owner, SETTINGS))
            yield()
        }
        assertEquals(24, failures.size)

        val retained = owners.last()
        coordinator.reconcileOwners(
            listOf(page(classicButtons = listOf(buttons.last()), consoleButtons = emptyList()))
        )
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
