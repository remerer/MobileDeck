package com.remerer.mobiledeck

internal data class CodexButtonOwnerKey(
    val pageId: Int,
    val presentation: DeckUiMode,
    val buttonId: Int,
    val presetId: String,
    val bindingId: String,
    val generation: Long
)

internal fun interface CodexOwnerGenerationFactory {
    fun nextGeneration(): Long?
}

internal class BoundedCodexOwnerGenerationFactory(
    initialGeneration: Long = 1L
) : CodexOwnerGenerationFactory {
    private var nextGeneration = initialGeneration.coerceAtLeast(1L)
    private var exhausted = false

    override fun nextGeneration(): Long? {
        if (exhausted) return null
        val generation = nextGeneration
        if (generation == Long.MAX_VALUE) {
            exhausted = true
        } else {
            nextGeneration = generation + 1L
        }
        return generation
    }
}

internal class CodexButtonOwnerRegistry(
    private val generationFactory: CodexOwnerGenerationFactory =
        BoundedCodexOwnerGenerationFactory()
) {
    private data class Slot(
        val pageId: Int,
        val presentation: DeckUiMode,
        val buttonId: Int
    )

    private data class TrackedButton(
        val button: DeckButton,
        val binding: CodexButtonBindingPayload,
        val owner: CodexButtonOwnerKey
    )

    private val trackedBySlot = mutableMapOf<Slot, TrackedButton>()
    private val buttonsByOwner = mutableMapOf<CodexButtonOwnerKey, DeckButton>()
    private val bindingsByOwner = mutableMapOf<CodexButtonOwnerKey, CodexButtonBindingPayload>()

    fun reconcile(pages: List<DeckPageConfig>): Map<CodexButtonOwnerKey, CodexButtonBindingPayload> {
        val nextTracked = mutableMapOf<Slot, TrackedButton>()
        val nextButtonsByOwner = mutableMapOf<CodexButtonOwnerKey, DeckButton>()
        val nextBindingsByOwner = linkedMapOf<CodexButtonOwnerKey, CodexButtonBindingPayload>()

        pages.forEach { page ->
            reconcilePresentation(
                pageId = page.id,
                presentation = DeckUiMode.Classic,
                buttons = page.classicButtons,
                nextTracked = nextTracked,
                nextButtonsByOwner = nextButtonsByOwner,
                bindingsByOwner = nextBindingsByOwner
            )
            reconcilePresentation(
                pageId = page.id,
                presentation = DeckUiMode.Console,
                buttons = page.consoleButtons,
                nextTracked = nextTracked,
                nextButtonsByOwner = nextButtonsByOwner,
                bindingsByOwner = nextBindingsByOwner
            )
        }

        trackedBySlot.clear()
        trackedBySlot.putAll(nextTracked)
        buttonsByOwner.clear()
        buttonsByOwner.putAll(nextButtonsByOwner)
        bindingsByOwner.clear()
        bindingsByOwner.putAll(nextBindingsByOwner)
        return nextBindingsByOwner
    }

    fun ownerFor(
        pageId: Int,
        presentation: DeckUiMode,
        button: DeckButton
    ): CodexButtonOwnerKey? {
        val tracked = trackedBySlot[Slot(pageId, presentation, button.id)] ?: return null
        return tracked.owner.takeIf { tracked.button === button }
    }

    fun titleFor(owner: CodexButtonOwnerKey): String? = buttonsByOwner[owner]?.title

    fun bindingFor(owner: CodexButtonOwnerKey): CodexButtonBindingPayload? {
        return bindingsByOwner[owner]?.takeIf { binding ->
            owner.presetId == binding.presetId && owner.bindingId == binding.bindingId
        }
    }

    fun invalidateAll() {
        trackedBySlot.clear()
        buttonsByOwner.clear()
        bindingsByOwner.clear()
    }

    private fun reconcilePresentation(
        pageId: Int,
        presentation: DeckUiMode,
        buttons: List<DeckButton>,
        nextTracked: MutableMap<Slot, TrackedButton>,
        nextButtonsByOwner: MutableMap<CodexButtonOwnerKey, DeckButton>,
        bindingsByOwner: MutableMap<CodexButtonOwnerKey, CodexButtonBindingPayload>
    ) {
        buttons.forEach { button ->
            if (button.actionType != DeckActionType.CompanionCommand) return@forEach
            val binding = CodexButtonBindingPayload.parse(button.payload) ?: return@forEach
            val slot = Slot(pageId, presentation, button.id)
            val previous = trackedBySlot[slot]
            val owner = if (previous?.button === button && previous.binding == binding) {
                previous.owner
            } else {
                val generation = generationFactory.nextGeneration() ?: return@forEach
                CodexButtonOwnerKey(
                    pageId = pageId,
                    presentation = presentation,
                    buttonId = button.id,
                    presetId = binding.presetId,
                    bindingId = binding.bindingId,
                    generation = generation
                )
            }
            val tracked = TrackedButton(button, binding, owner)
            nextTracked[slot] = tracked
            nextButtonsByOwner[owner] = button
            bindingsByOwner[owner] = binding
        }
    }
}

internal enum class CompanionSettingsFeature {
    Enabled,
    Endpoint,
    PairingToken,
    ConnectionStatus,
    QrIntake,
    DeckSyncAutomation,
    GenericCommands,
    Obs,
    ExecCancel,
    Diagnostics,
    TestConnection,
    Logs,
    DeveloperControls;

    companion object {
        val debugOnly: Set<CompanionSettingsFeature> = setOf(
            QrIntake,
            DeckSyncAutomation,
            GenericCommands,
            Obs,
            ExecCancel,
            Diagnostics,
            TestConnection,
            Logs,
            DeveloperControls
        )
    }
}

internal class CompanionSettingsAccessPolicy private constructor(
    private val debugBuild: Boolean
) {
    fun canAccess(feature: CompanionSettingsFeature): Boolean {
        return feature !in CompanionSettingsFeature.debugOnly || debugBuild
    }

    fun configurationUpdate(
        current: CompanionSettings,
        proposed: CompanionSettings
    ): CompanionSettings {
        return current.copy(
            enabled = proposed.enabled,
            endpoint = proposed.endpoint,
            pairingToken = proposed.pairingToken
        )
    }

    companion object {
        fun forBuild(debugBuild: Boolean): CompanionSettingsAccessPolicy {
            return CompanionSettingsAccessPolicy(debugBuild)
        }
    }
}
