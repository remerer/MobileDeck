package com.remerer.mobiledeck

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class CompanionButtonContractTest {
    @Test
    fun sharedTuplesRemainOnAndroidOrHidRoutes() {
        val sharedButtons = listOf(
            button(DeckActionType.RunCommand),
            button(DeckActionType.Hotkey),
            button(DeckActionType.MediaKey),
            button(DeckActionType.Text),
            button(DeckActionType.Hotkey, DeckControlStyle.JoyPad),
            button(DeckActionType.CompanionControl, DeckControlStyle.AnalogStick)
        )

        sharedButtons.forEach { candidate ->
            assertEquals(candidate.toString(), DeckButtonPlatformAvailability.Shared, candidate.platformAvailability())
            assertEquals(
                candidate.toString(),
                DeckButtonExecutionRoute.AndroidOrHid,
                candidate.executionDecision(companionAvailable = false).route
            )
        }
    }

    @Test
    fun androidActionsClassifyAndroidOnly() {
        val androidOnlyActions = listOf(
            DeckActionType.Settings,
            DeckActionType.BluetoothStatus,
            DeckActionType.PreviousPage,
            DeckActionType.NextPage,
            DeckActionType.Utility,
            DeckActionType.AppCommand
        )

        androidOnlyActions.forEach { action ->
            assertEquals(
                action.name,
                DeckButtonPlatformAvailability.AndroidOnly,
                button(action).platformAvailability()
            )
        }
    }

    @Test
    fun companionCommandsStatusesAndScalarToggleControlsRequireCompanion() {
        val companionRequired = listOf(
            button(DeckActionType.CompanionCommand),
            button(DeckActionType.CompanionStatus),
            button(DeckActionType.CompanionControl, DeckControlStyle.TrimSlider),
            button(DeckActionType.CompanionControl, DeckControlStyle.TrimKnob),
            button(DeckActionType.CompanionControl, DeckControlStyle.InfiniteWheel),
            button(DeckActionType.CompanionControl, DeckControlStyle.CompanionToggle)
        )

        companionRequired.forEach { candidate ->
            assertEquals(
                candidate.toString(),
                DeckButtonPlatformAvailability.CompanionRequired,
                candidate.platformAvailability()
            )
        }
    }

    @Test
    fun unavailableCompanionTuplesNeverSelectAndroidOrHid() {
        val companionRequired = listOf(
            button(DeckActionType.CompanionCommand),
            button(DeckActionType.CompanionStatus),
            button(DeckActionType.CompanionControl, DeckControlStyle.TrimSlider),
            button(DeckActionType.CompanionControl, DeckControlStyle.CompanionToggle)
        )

        companionRequired.forEach { candidate ->
            assertEquals(
                candidate.toString(),
                DeckButtonExecutionRoute.Unavailable,
                candidate.executionDecision(companionAvailable = false).route
            )
        }
    }

    @Test
    fun httpCompanionCommandsUseOpenAndStructuredCommandsUseProgramCommand() {
        val command = button(DeckActionType.CompanionCommand)

        listOf("http://localhost:8080", "https://openai.com/docs").forEach { payload ->
            assertEquals(
                DeckButtonExecutionRoute.CompanionOpen,
                command.executionDecision(companionAvailable = true, payloadOverride = payload).route
            )
        }
        listOf(
            "obs:scene.next",
            "{\"programId\":\"codex\",\"command\":\"thread/resume\"}",
            "program:command"
        ).forEach { payload ->
            assertEquals(
                DeckButtonExecutionRoute.CompanionProgramCommand,
                command.executionDecision(companionAvailable = true, payloadOverride = payload).route
            )
        }
    }

    @Test
    fun releaseRoutesOnlyStrictCodexBindingsWithConfiguredSettings() {
        val command = button(DeckActionType.CompanionCommand)
        val settings = CompanionSettings(
            enabled = true,
            endpoint = "wss://companion.example",
            pairingToken = "pairing-token"
        )
        val payload =
            """{"programId":"codex","command":"exec.submit","args":{"contractVersion":1,"presetId":"readonly-qa","bindingId":"binding-7f1a5f45d82e4d63a6a238db1f69a211"}}"""

        val decision = command.executionDecision(
            companionAvailable = true,
            payloadOverride = payload,
            debugBuild = false,
            companionSettings = settings
        )

        assertEquals(DeckButtonExecutionRoute.CompanionProgramCommand, decision.route)
        assertNotNull(decision.codexBinding)
        assertEquals("readonly-qa", decision.codexBinding?.presetId)
    }

    @Test
    fun releaseRejectsGenericObsOverridesAndUnconfiguredCodexBindings() {
        val command = button(DeckActionType.CompanionCommand)
        val settings = CompanionSettings(
            enabled = true,
            endpoint = "wss://companion.example",
            pairingToken = "pairing-token"
        )
        val validPayload =
            """{"programId":"codex","command":"exec.submit","args":{"contractVersion":1,"presetId":"readonly-qa","bindingId":"binding-7f1a5f45d82e4d63a6a238db1f69a211"}}"""
        val rejectedPayloads = listOf(
            "obs:scene.next",
            "program:command",
            """{"programId":"codex","command":"exec.cancel","args":{"contractVersion":1,"jobId":"job-9d34d23d4efb4da0aadb8d65c0861f52"}}""",
            """{"programId":"codex","command":"exec.submit","args":{"contractVersion":1,"presetId":"readonly-qa","bindingId":"binding-7f1a5f45d82e4d63a6a238db1f69a211","prompt":"raw"}}"""
        )

        rejectedPayloads.forEach { payload ->
            val decision = command.executionDecision(
                companionAvailable = true,
                payloadOverride = payload,
                debugBuild = false,
                companionSettings = settings
            )
            assertEquals(payload, DeckButtonExecutionRoute.Unavailable, decision.route)
            assertNull(payload, decision.codexBinding)
        }

        assertEquals(
            DeckButtonExecutionRoute.Unavailable,
            command.executionDecision(
                companionAvailable = true,
                payloadOverride = validPayload,
                debugBuild = false,
                companionSettings = settings.copy(enabled = false)
            ).route
        )
    }

    @Test
    fun debugRetainsExistingGenericProgramAndObsRouting() {
        val command = button(DeckActionType.CompanionCommand)

        listOf("obs:scene.next", "program:command").forEach { payload ->
            val decision = command.executionDecision(
                companionAvailable = true,
                payloadOverride = payload,
                debugBuild = true
            )
            assertEquals(payload, DeckButtonExecutionRoute.CompanionProgramCommand, decision.route)
            assertNull(payload, decision.codexBinding)
        }
    }

    @Test
    fun companionControlsKeepExactSourceAndTypedValue() {
        val volumeDecision = button(
            DeckActionType.CompanionControl,
            DeckControlStyle.TrimSlider
        ).executionDecision(
            companionAvailable = true,
            companionControlSource = "system.volume",
            companionControlValue = 42.5
        )
        assertEquals(DeckButtonExecutionRoute.CompanionControlUpdate, volumeDecision.route)
        assertEquals("system.volume", volumeDecision.source)
        assertEquals(42.5, volumeDecision.value)

        val muteDecision = button(
            DeckActionType.CompanionControl,
            DeckControlStyle.CompanionToggle
        ).executionDecision(
            companionAvailable = true,
            companionControlSource = "system.micMute",
            companionControlValue = true
        )
        assertEquals(DeckButtonExecutionRoute.CompanionControlUpdate, muteDecision.route)
        assertEquals("system.micMute", muteDecision.source)
        assertEquals(true, muteDecision.value)
    }

    @Test
    fun connectedStatusRemainsReadOnly() {
        val status = button(DeckActionType.CompanionStatus)

        assertEquals(
            DeckButtonExecutionRoute.ReadOnly,
            status.executionDecision(companionAvailable = true).route
        )
    }

    private fun button(
        actionType: DeckActionType,
        controlStyle: DeckControlStyle = DeckControlStyle.Button
    ): DeckButton {
        return DeckButton(
            id = 1,
            title = actionType.name,
            subtitle = "",
            icon = "",
            iconImageUri = "",
            displayMode = DeckDisplayMode.IconAndText,
            actionType = actionType,
            payload = "payload",
            color = Color.Black,
            controlStyle = controlStyle,
            controlStyleRaw = controlStyle.name,
            companionControl = ""
        )
    }
}
