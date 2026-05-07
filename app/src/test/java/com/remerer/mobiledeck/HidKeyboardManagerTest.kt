package com.remerer.mobiledeck

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class HidKeyboardManagerTest {
    @Test
    fun hotkeyReport_usesModifierAndKeyWithoutReportIdInData() {
        val report = HidKeyboardManager.hotkeyReport("CTRL+SHIFT+M")

        assertNotNull(report)
        assertEquals(8, report!!.size)
        assertArrayEquals(
            byteArrayOf(0x03, 0x00, 0x10, 0x00, 0x00, 0x00, 0x00, 0x00),
            report
        )
    }

    @Test
    fun hotkeyReport_mapsFunctionKeys() {
        val report = HidKeyboardManager.hotkeyReport("CTRL+F9")

        assertArrayEquals(
            byteArrayOf(0x01, 0x00, 0x42, 0x00, 0x00, 0x00, 0x00, 0x00),
            report
        )
    }

    @Test
    fun charReport_addsShiftForUppercaseLetters() {
        val report = HidKeyboardManager.charReport('A')

        assertArrayEquals(
            byteArrayOf(0x02, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00),
            report
        )
    }

    @Test
    fun mediaReport_mapsConsumerControlKeys() {
        assertArrayEquals(byteArrayOf(0xE2.toByte(), 0x00), HidKeyboardManager.mediaReport("MUTE"))
        assertArrayEquals(byteArrayOf(0xCD.toByte(), 0x00), HidKeyboardManager.mediaReport("PLAY_PAUSE"))
        assertArrayEquals(byteArrayOf(0xB7.toByte(), 0x00), HidKeyboardManager.mediaReport("STOP"))
    }
}
