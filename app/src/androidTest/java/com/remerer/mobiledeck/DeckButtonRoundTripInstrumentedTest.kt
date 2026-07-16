package com.remerer.mobiledeck

import androidx.compose.ui.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeckButtonRoundTripInstrumentedTest {
    @Test
    fun representativeButtonTuplesPreserveCodecSemantics() {
        val buttons = normalDisplayButtons() + listOf(
            button(
                id = 10,
                actionType = DeckActionType.CompanionStatus,
                payload = "system.cpuUsage",
                companionControl = controlJson(
                    "kind" to "Status",
                    "source" to "system.cpuUsage",
                    "value" to 37.5,
                    "unit" to "%"
                )
            ),
            scalarButton(11, DeckControlStyle.TrimSlider, "Slider", "system.volume", 42.0, 0.0, 100.0, 1.0, "%"),
            scalarButton(12, DeckControlStyle.TrimKnob, "Knob", "manual.knob", 0.5, -3.0, 3.0, 0.5, ""),
            scalarButton(13, DeckControlStyle.InfiniteWheel, "Wheel", "manual.wheel", 8.0, 0.0, 24.0, 1.0, "ticks"),
            button(
                id = 14,
                actionType = DeckActionType.CompanionControl,
                controlStyle = DeckControlStyle.CompanionToggle,
                payload = "system.micMute",
                companionControl = controlJson(
                    "kind" to "Toggle",
                    "source" to "system.micMute",
                    "value" to true
                )
            ),
            button(
                id = 15,
                actionType = DeckActionType.Hotkey,
                controlStyle = DeckControlStyle.JoyPad,
                payload = "UP|DOWN|LEFT|RIGHT",
                companionControl = controlJson(
                    "kind" to "DPad",
                    "source" to "keyboard.direction"
                )
            ),
            button(
                id = 16,
                actionType = DeckActionType.CompanionControl,
                controlStyle = DeckControlStyle.AnalogStick,
                payload = "manual.analog",
                companionControl = controlJson(
                    "kind" to "AnalogStick",
                    "source" to "manual.analog",
                    "x" to 0.25,
                    "y" to -0.75,
                    "active" to true,
                    "deadZone" to 0.12
                )
            )
        )

        buttons.forEach { original ->
            val encoded = encodeDeckButton(original)
            val decoded = decodeDeckButton(JSONObject(encoded.toString()), original.position)
            val reEncoded = encodeDeckButton(decoded)

            assertEquals(original.id, decoded.id)
            assertEquals(original.title, decoded.title)
            assertEquals(original.subtitle, decoded.subtitle)
            assertEquals(original.icon, decoded.icon)
            assertEquals(original.iconImageUri, decoded.iconImageUri)
            assertEquals(original.displayMode, decoded.displayMode)
            assertEquals(original.actionType, decoded.actionType)
            assertEquals(original.payload, decoded.payload)
            assertEquals(original.color, decoded.color)
            assertEquals(original.controlStyle, decoded.controlStyle)
            assertEquals(original.controlStyleRaw, decoded.controlStyleRaw)

            assertEquals(encoded.getString("actionType"), reEncoded.getString("actionType"))
            assertEquals(encoded.getString("payload"), reEncoded.getString("payload"))
            assertEquals(encoded.getString("displayMode"), reEncoded.getString("displayMode"))
            assertEquals(encoded.getString("controlStyle"), reEncoded.getString("controlStyle"))
            assertEquals(encoded.getString("title"), reEncoded.getString("title"))
            assertEquals(encoded.getString("subtitle"), reEncoded.getString("subtitle"))
            assertEquals(encoded.getString("icon"), reEncoded.getString("icon"))
            assertEquals(encoded.getString("iconImageUri"), reEncoded.getString("iconImageUri"))
            assertJsonSemantics(
                expected = encoded.optJSONObject("companionControl"),
                actual = reEncoded.optJSONObject("companionControl")
            )
        }
    }

    private fun normalDisplayButtons(): List<DeckButton> {
        return DeckDisplayMode.entries.mapIndexed { index, displayMode ->
            button(
                id = index + 1,
                actionType = DeckActionType.Hotkey,
                payload = "CTRL+${index + 1}",
                displayMode = displayMode
            )
        }
    }

    private fun scalarButton(
        id: Int,
        controlStyle: DeckControlStyle,
        kind: String,
        source: String,
        value: Double,
        min: Double,
        max: Double,
        step: Double,
        unit: String
    ): DeckButton {
        return button(
            id = id,
            actionType = DeckActionType.CompanionControl,
            controlStyle = controlStyle,
            payload = source,
            companionControl = controlJson(
                "kind" to kind,
                "source" to source,
                "value" to value,
                "min" to min,
                "max" to max,
                "step" to step,
                "unit" to unit
            )
        )
    }

    private fun button(
        id: Int,
        actionType: DeckActionType,
        payload: String,
        controlStyle: DeckControlStyle = DeckControlStyle.Button,
        displayMode: DeckDisplayMode = DeckDisplayMode.IconAndText,
        companionControl: String = ""
    ): DeckButton {
        return DeckButton(
            id = id,
            title = "Hidden title $id",
            subtitle = "Hidden subtitle $id",
            icon = "icon_$id",
            iconImageUri = "content://mobiledeck/icon/$id",
            displayMode = displayMode,
            actionType = actionType,
            payload = payload,
            color = Color(0xFF204060),
            position = id,
            spanColumns = 2,
            spanRows = 2,
            controlStyle = controlStyle,
            controlStyleRaw = controlStyle.name,
            companionControl = companionControl
        )
    }

    private fun controlJson(vararg fields: Pair<String, Any>): String {
        return JSONObject().apply {
            fields.forEach { (name, value) -> put(name, value) }
        }.toString()
    }

    private fun assertJsonSemantics(expected: JSONObject?, actual: JSONObject?) {
        if (expected == null) {
            assertTrue(actual == null)
            return
        }
        assertFalse(actual == null)
        actual ?: return
        val expectedKeys = expected.keys().asSequence().toSet()
        val actualKeys = actual.keys().asSequence().toSet()
        assertEquals(expectedKeys, actualKeys)
        expectedKeys.forEach { key ->
            val expectedValue = expected.get(key)
            val actualValue = actual.get(key)
            if (expectedValue is Number && actualValue is Number) {
                assertEquals(key, expectedValue.toDouble(), actualValue.toDouble(), 0.000001)
            } else {
                assertEquals(key, expectedValue, actualValue)
            }
        }
    }
}
