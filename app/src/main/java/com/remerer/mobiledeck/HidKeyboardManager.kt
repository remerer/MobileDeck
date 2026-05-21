package com.remerer.mobiledeck

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppSdpSettings
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import java.util.Locale
import java.util.concurrent.Executors

data class HidStatus(
    val state: HidConnectionState = HidConnectionState.Disconnected,
    val message: String = "Ready to register Bluetooth HID keyboard"
)

data class PairedHidHost(
    val name: String,
    val address: String
)

enum class HidConnectionState(val label: String) {
    Disconnected("Disconnected"),
    Registering("Registering"),
    Registered("Registered"),
    Connected("Connected"),
    Unsupported("Unsupported"),
    PermissionMissing("Permission needed"),
    Error("Error")
}

class HidKeyboardManager(
    private val context: Context,
    private val onStatusChanged: (HidStatus) -> Unit
) {
    private val adapter: BluetoothAdapter? by lazy {
        try {
            BluetoothAdapter.getDefaultAdapter()
        } catch (e: Throwable) {
            null
        }
    }
    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())
    private var hidDevice: BluetoothHidDevice? = null
    private var host: BluetoothDevice? = null
    private var previousAdapterName: String? = null

    private val hidDescriptor = intArrayOf(
        0x05, 0x01,
        0x09, 0x06,
        0xA1, 0x01,
        0x85, KEYBOARD_REPORT_ID,
        0x05, 0x07,
        0x19, 0xE0,
        0x29, 0xE7,
        0x15, 0x00,
        0x25, 0x01,
        0x75, 0x01,
        0x95, 0x08,
        0x81, 0x02,
        0x95, 0x01,
        0x75, 0x08,
        0x81, 0x03,
        0x95, 0x06,
        0x75, 0x08,
        0x15, 0x00,
        0x25, 0x65,
        0x05, 0x07,
        0x19, 0x00,
        0x29, 0x65,
        0x81, 0x00,
        0x95, 0x05,
        0x75, 0x01,
        0x05, 0x08,
        0x19, 0x01,
        0x29, 0x05,
        0x91, 0x02,
        0x95, 0x01,
        0x75, 0x03,
        0x91, 0x03,
        0xC0,
        0x05, 0x0C,
        0x09, 0x01,
        0xA1, 0x01,
        0x85, MEDIA_REPORT_ID,
        0x19, 0x00,
        0x2A, 0xFF, 0x03,
        0x15, 0x00,
        0x26, 0xFF, 0x03,
        0x75, 0x10,
        0x95, 0x01,
        0x81, 0x00,
        0xC0
    ).map { it.toByte() }.toByteArray()

    private val callback by lazy {
        object : BluetoothHidDevice.Callback() {
            override fun onAppStatusChanged(pluggedDevice: BluetoothDevice?, registered: Boolean) {
                Log.d(TAG, "onAppStatusChanged registered=$registered plugged=${pluggedDevice?.safeName()}")
                if (registered) {
                    host = pluggedDevice
                    publish(
                        HidConnectionState.Registered,
                        "Registered as Android HID Keyboard. Pair this phone from the PC Bluetooth settings."
                    )
                } else {
                    host = null
                    publish(HidConnectionState.Disconnected, "Bluetooth HID keyboard unregistered")
                }
            }

            override fun onConnectionStateChanged(device: BluetoothDevice, state: Int) {
                Log.d(TAG, "onConnectionStateChanged device=${device.safeName()} state=$state")
                when (state) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        host = device
                        publish(HidConnectionState.Connected, "Connected to ${device.safeName()}")
                    }

                    BluetoothProfile.STATE_CONNECTING -> {
                        publish(HidConnectionState.Registering, "Connecting to ${device.safeName()}")
                    }

                    else -> {
                        if (host == device) host = null
                        publish(HidConnectionState.Registered, "Registered. Waiting for PC connection.")
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun start() {
        val adapter = adapter
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            Log.w(TAG, "HID Device unsupported: sdk=${Build.VERSION.SDK_INT}")
            publish(HidConnectionState.Unsupported, "Bluetooth HID Device requires Android 9 or newer")
            return
        }
        if (adapter == null) {
            Log.w(TAG, "Bluetooth adapter is null")
            publish(HidConnectionState.Unsupported, "Bluetooth adapter is not available")
            return
        }
        if (!hasRequiredPermissions()) {
            Log.w(TAG, "Bluetooth permissions missing")
            publish(HidConnectionState.PermissionMissing, "Bluetooth permission approval is required")
            return
        }

        hidDevice?.unregisterApp()
        hidDevice = null
        host = null

        renameAdapterForPairing()
        Log.d(TAG, "Requesting HID_DEVICE profile proxy")
        publish(HidConnectionState.Registering, "Registering Bluetooth HID keyboard")
        adapter.getProfileProxy(
            context,
            object : BluetoothProfile.ServiceListener {
                override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
                    if (profile != BluetoothProfile.HID_DEVICE) return
                    hidDevice = proxy as BluetoothHidDevice

                    val sdp = BluetoothHidDeviceAppSdpSettings(
                        "MobileDeck Keyboard",
                        "Android Bluetooth HID keyboard",
                        "MobileDeck",
                        BluetoothHidDevice.SUBCLASS1_KEYBOARD,
                        hidDescriptor
                    )

                    val registered = hidDevice?.registerApp(
                        sdp,
                        null,
                        null,
                        executor,
                        callback
                    ) == true

                    Log.d(TAG, "registerApp returned $registered")
                    if (!registered) {
                        publish(HidConnectionState.Error, "Bluetooth HID registration failed")
                    }
                }

                override fun onServiceDisconnected(profile: Int) {
                    if (profile == BluetoothProfile.HID_DEVICE) {
                        hidDevice = null
                        host = null
                        publish(HidConnectionState.Disconnected, "Bluetooth HID service disconnected")
                    }
                }
            },
            BluetoothProfile.HID_DEVICE
        )
    }

    @SuppressLint("MissingPermission")
    fun stop() {
        hidDevice?.unregisterApp()
        hidDevice = null
        host = null
        restoreAdapterName()
        publish(HidConnectionState.Disconnected, "Bluetooth HID keyboard stopped")
    }

    @SuppressLint("MissingPermission")
    fun pairedHosts(): List<PairedHidHost> {
        val adapter = adapter
        if (adapter == null || !hasRequiredPermissions()) return emptyList()
        return adapter.bondedDevices
            .map { device ->
                PairedHidHost(
                    name = device.name ?: "Unknown Bluetooth device",
                    address = device.address
                )
            }
            .sortedBy { it.name.lowercase(Locale.US) }
    }

    @SuppressLint("MissingPermission")
    fun connect(address: String): Boolean {
        val hid = hidDevice
        if (hid == null) {
            publish(HidConnectionState.Disconnected, "Register HID before connecting to a PC")
            return false
        }
        if (!hasRequiredPermissions()) {
            publish(HidConnectionState.PermissionMissing, "Bluetooth permission approval is required")
            return false
        }
        val device = adapter?.bondedDevices?.firstOrNull { it.address == address }
        if (device == null) {
            publish(HidConnectionState.Error, "Paired device was not found")
            return false
        }
        val started = hid.connect(device)
        Log.d(TAG, "connect(${device.safeName()}) returned $started")
        publish(
            HidConnectionState.Registering,
            if (started) "Connecting HID profile to ${device.safeName()}" else "Could not start HID connection"
        )
        return started
    }

    @SuppressLint("MissingPermission")
    fun sendHotkey(payload: String): Boolean {
        val device = host ?: return false
        val hid = hidDevice ?: return false
        val report = hotkeyReport(payload) ?: return false
        return sendReportTap(hid, device, KEYBOARD_REPORT_ID, report, KEYBOARD_REPORT_SIZE)
    }

    @SuppressLint("MissingPermission")
    fun sendMediaKey(payload: String): Boolean {
        val device = host ?: return false
        val hid = hidDevice ?: return false
        val report = mediaReport(payload) ?: return false
        return sendReportTap(hid, device, MEDIA_REPORT_ID, report, MEDIA_REPORT_SIZE)
    }

    @SuppressLint("MissingPermission")
    fun sendText(text: String): Boolean {
        val device = host ?: return false
        val hid = hidDevice ?: return false
        var sentAny = false
        text.forEach { char ->
            val report = charReport(char) ?: return@forEach
            sentAny = sendReportTap(hid, device, KEYBOARD_REPORT_ID, report, KEYBOARD_REPORT_SIZE) || sentAny
        }
        return sentAny
    }

    @SuppressLint("MissingPermission")
    fun runWindowsCommand(command: String): Boolean {
        val device = host ?: return false
        val hid = hidDevice ?: return false
        val openRunDialog = hotkeyReport("WIN+R") ?: return false
        var sent = sendReportTap(hid, device, KEYBOARD_REPORT_ID, openRunDialog, KEYBOARD_REPORT_SIZE)
        Thread.sleep(250)
        command.forEach { char ->
            val report = charReport(char) ?: return@forEach
            sent = sendReportTap(hid, device, KEYBOARD_REPORT_ID, report, KEYBOARD_REPORT_SIZE) && sent
        }
        val enter = hotkeyReport("ENTER") ?: return false
        return sendReportTap(hid, device, KEYBOARD_REPORT_ID, enter, KEYBOARD_REPORT_SIZE) && sent
    }

    fun hasRequiredPermissions(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            HID_BLUETOOTH_PERMISSIONS.all { permission ->
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            }
    }

    fun hasDiscoverablePermissions(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            DISCOVERABLE_BLUETOOTH_PERMISSIONS.all { permission ->
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            }
    }

    private fun publish(state: HidConnectionState, message: String) {
        mainHandler.post {
            onStatusChanged(HidStatus(state, message))
        }
    }

    @SuppressLint("MissingPermission")
    private fun renameAdapterForPairing() {
        val adapter = adapter
        if (adapter == null || !hasRequiredPermissions()) return
        val currentName = adapter.name
        if (currentName != DEVICE_NAME) {
            previousAdapterName = currentName
            val renamed = adapter.setName(DEVICE_NAME)
            Log.d(TAG, "setName($DEVICE_NAME) returned $renamed previous=$currentName")
        }
    }

    @SuppressLint("MissingPermission")
    private fun restoreAdapterName() {
        val oldName = previousAdapterName ?: return
        val adapter = adapter
        if (adapter != null && hasRequiredPermissions()) {
            val restored = adapter.setName(oldName)
            Log.d(TAG, "restoreName($oldName) returned $restored")
        }
        previousAdapterName = null
    }

    private fun sendReportTap(
        hid: BluetoothHidDevice,
        device: BluetoothDevice,
        reportId: Int,
        report: ByteArray,
        releaseSize: Int
    ): Boolean {
        val pressed = hid.sendReport(device, reportId, report)
        Thread.sleep(30)
        val released = hid.sendReport(device, reportId, ByteArray(releaseSize))
        Log.d(
            TAG,
            "sendReportTap host=${device.safeName()} id=$reportId pressed=$pressed released=$released data=${report.toHex()}"
        )
        return pressed && released
    }

    private fun ByteArray.toHex(): String {
        return joinToString(" ") { byte -> "%02X".format(byte) }
    }

    private fun BluetoothDevice.safeName(): String {
        return if (hasRequiredPermissions()) name ?: address else "paired host"
    }

    companion object {
        const val KEYBOARD_REPORT_ID = 1
        const val MEDIA_REPORT_ID = 2
        private const val TAG = "MobileDeckHid"
        private const val DEVICE_NAME = "MobileDeck Keyboard"
        private const val KEYBOARD_REPORT_SIZE = 8
        private const val MEDIA_REPORT_SIZE = 2
        private const val MOD_LEFT_CTRL = 0x01
        private const val MOD_LEFT_SHIFT = 0x02
        private const val MOD_LEFT_ALT = 0x04
        private const val MOD_LEFT_GUI = 0x08

        val HID_BLUETOOTH_PERMISSIONS = arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE
        )

        val DISCOVERABLE_BLUETOOTH_PERMISSIONS = arrayOf(
            Manifest.permission.BLUETOOTH_ADVERTISE
        )

        val HOST_LIST_BLUETOOTH_PERMISSIONS = arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT
        )

        fun hotkeyReport(payload: String): ByteArray? {
            var modifier = 0
            val keys = mutableListOf<Int>()
            payload.split("+")
                .map { it.trim().uppercase(Locale.US) }
                .filter { it.isNotEmpty() }
                .forEach { token ->
                    when (token) {
                        "CTRL", "CONTROL" -> modifier = modifier or MOD_LEFT_CTRL
                        "SHIFT" -> modifier = modifier or MOD_LEFT_SHIFT
                        "ALT", "OPTION" -> modifier = modifier or MOD_LEFT_ALT
                        "WIN", "WINDOWS", "CMD", "META", "GUI" -> modifier = modifier or MOD_LEFT_GUI
                        else -> hidKeyCode(token)?.let { keys += it }
                    }
                }

            if (modifier == 0 && keys.isEmpty()) return null
            return keyboardReport(modifier, keys)
        }

        fun charReport(char: Char): ByteArray? {
            val mapped = charKey(char) ?: return null
            val modifier = if (mapped.shift) MOD_LEFT_SHIFT else 0
            return keyboardReport(modifier, listOf(mapped.keyCode))
        }

        private fun charKey(char: Char): CharKey? {
            val token = char.uppercaseChar().toString()
            if (char in 'a'..'z' || char in 'A'..'Z') {
                return CharKey(hidKeyCode(token) ?: return null, char.isUpperCase())
            }
            if (char in '0'..'9') return CharKey(hidKeyCode(token) ?: return null, false)
            val shiftedDigits = mapOf(
                '!' to '1',
                '@' to '2',
                '#' to '3',
                '$' to '4',
                '%' to '5',
                '^' to '6',
                '&' to '7',
                '*' to '8',
                '(' to '9',
                ')' to '0'
            )
            shiftedDigits[char]?.let { digit ->
                return CharKey(hidKeyCode(digit.toString()) ?: return null, true)
            }
            val punctuation = when (char) {
                ' ' -> CharKey(0x2C, false)
                '-' -> CharKey(0x2D, false)
                '_' -> CharKey(0x2D, true)
                '=' -> CharKey(0x2E, false)
                '+' -> CharKey(0x2E, true)
                '[' -> CharKey(0x2F, false)
                '{' -> CharKey(0x2F, true)
                ']' -> CharKey(0x30, false)
                '}' -> CharKey(0x30, true)
                '\\' -> CharKey(0x31, false)
                '|' -> CharKey(0x31, true)
                ';' -> CharKey(0x33, false)
                ':' -> CharKey(0x33, true)
                '\'' -> CharKey(0x34, false)
                '"' -> CharKey(0x34, true)
                '`' -> CharKey(0x35, false)
                '~' -> CharKey(0x35, true)
                ',' -> CharKey(0x36, false)
                '<' -> CharKey(0x36, true)
                '.' -> CharKey(0x37, false)
                '>' -> CharKey(0x37, true)
                '/' -> CharKey(0x38, false)
                '?' -> CharKey(0x38, true)
                else -> null
            }
            return punctuation
        }

        private data class CharKey(
            val keyCode: Int,
            val shift: Boolean
        )

        fun mediaReport(payload: String): ByteArray? {
            val usage = when (payload.trim().uppercase(Locale.US)) {
                "MUTE" -> 0x00E2
                "VOLUME_UP", "VOLUMEUP" -> 0x00E9
                "VOLUME_DOWN", "VOLUMEDOWN" -> 0x00EA
                "PLAY_PAUSE", "PLAYPAUSE", "PLAY", "PAUSE" -> 0x00CD
                "STOP" -> 0x00B7
                "NEXT", "NEXT_TRACK" -> 0x00B5
                "PREVIOUS", "PREV", "PREVIOUS_TRACK" -> 0x00B6
                "EJECT" -> 0x00B8
                else -> return null
            }
            return byteArrayOf(
                (usage and 0xFF).toByte(),
                ((usage shr 8) and 0xFF).toByte()
            )
        }

        private fun keyboardReport(modifier: Int, keys: List<Int>): ByteArray {
            val report = ByteArray(KEYBOARD_REPORT_SIZE)
            report[0] = modifier.toByte()
            keys.take(6).forEachIndexed { index, keyCode ->
                report[index + 2] = keyCode.toByte()
            }
            return report
        }

        private fun hidKeyCode(token: String): Int? {
            if (token.length == 1 && token[0] in 'A'..'Z') return token[0] - 'A' + 0x04
            if (token.length == 1 && token[0] in '1'..'9') return token[0] - '1' + 0x1E
            if (token == "0") return 0x27
            return when (token) {
                "ENTER" -> 0x28
                "ESC", "ESCAPE" -> 0x29
                "BACKSPACE" -> 0x2A
                "TAB" -> 0x2B
                "SPACE" -> 0x2C
                "F1" -> 0x3A
                "F2" -> 0x3B
                "F3" -> 0x3C
                "F4" -> 0x3D
                "F5" -> 0x3E
                "F6" -> 0x3F
                "F7" -> 0x40
                "F8" -> 0x41
                "F9" -> 0x42
                "F10" -> 0x43
                "F11" -> 0x44
                "F12" -> 0x45
                else -> null
            }
        }
    }
}
