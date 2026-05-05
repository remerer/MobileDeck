package com.example.mobiledeck

import android.os.Build
import android.os.Bundle
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Web
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobiledeck.ui.theme.MobileDeckTheme
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        enableEdgeToEdge()
        setContent {
            MobileDeckTheme {
                MobileDeckApp()
            }
        }
    }
}

private enum class DeckActionType(val label: String) {
    Settings("Settings"),
    BluetoothStatus("Bluetooth status"),
    MediaKey("Media key"),
    Hotkey("Hotkey"),
    Text("Text"),
    RunCommand("Run command"),
    AppCommand("App command")
}

private enum class DeckDisplayMode(val label: String) {
    IconOnly("Icon only"),
    IconAndText("Icon + text")
}

private data class DeckButton(
    val id: Int,
    val title: String,
    val subtitle: String,
    val icon: String,
    val iconImageUri: String,
    val displayMode: DeckDisplayMode,
    val actionType: DeckActionType,
    val payload: String,
    val color: Color
)

private data class ActivityLog(
    val buttonTitle: String,
    val payload: String,
    val delivered: Boolean,
    val note: String
)

private enum class AppPage {
    Deck,
    Settings
}

@Composable
private fun MobileDeckApp() {
    val context = LocalContext.current
    var hidStatus by remember { mutableStateOf(HidStatus()) }
    val hidManager = remember {
        HidKeyboardManager(context.applicationContext) { status ->
            hidStatus = status
        }
    }
    var pairedHosts by remember { mutableStateOf(emptyList<PairedHidHost>()) }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        if (grants.values.all { it }) {
            hidManager.start()
            pairedHosts = hidManager.pairedHosts()
        } else {
            hidStatus = HidStatus(
                HidConnectionState.PermissionMissing,
                "Bluetooth permissions were denied"
            )
        }
    }
    val discoverableLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        hidStatus = hidStatus.copy(
            message = "Discoverable request finished. Pair from the PC while HID is registered."
        )
    }
    var deckButtons by remember { mutableStateOf(loadDeckButtons(context)) }
    var deckColumns by remember { mutableStateOf(loadDeckColumns(context)) }
    var editingButton by remember { mutableStateOf<DeckButton?>(null) }
    var logs by remember { mutableStateOf(emptyList<ActivityLog>()) }
    var page by remember { mutableStateOf(AppPage.Deck) }

    fun startHid() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(HidKeyboardManager.REQUIRED_BLUETOOTH_PERMISSIONS)
        } else {
            hidManager.start()
        }
        pairedHosts = hidManager.pairedHosts()
    }

    fun addDeckButton() {
        val colors = defaultDeckColors()
        val newButton = DeckButton(
            id = nextDeckButtonId(deckButtons),
            title = "New key",
            subtitle = "Custom",
            icon = "+",
            iconImageUri = "",
            displayMode = DeckDisplayMode.IconAndText,
            actionType = DeckActionType.Hotkey,
            payload = "CTRL+F9",
            color = colors[deckButtons.size % colors.size]
        )
        val updatedButtons = deckButtons + newButton
        deckButtons = updatedButtons
        saveDeckButtons(context, updatedButtons)
    }

    fun addBluetoothStatusButton() {
        val colors = defaultDeckColors()
        val newButton = DeckButton(
            id = nextDeckButtonId(deckButtons),
            title = "Bluetooth",
            subtitle = "Status",
            icon = "BT",
            iconImageUri = "",
            displayMode = DeckDisplayMode.IconAndText,
            actionType = DeckActionType.BluetoothStatus,
            payload = "",
            color = colors[deckButtons.size % colors.size]
        )
        val updatedButtons = deckButtons + newButton
        deckButtons = updatedButtons
        saveDeckButtons(context, updatedButtons)
    }

    fun pressDeckButton(button: DeckButton) {
        val delivered = when (button.actionType) {
            DeckActionType.Settings -> {
                page = AppPage.Settings
                true
            }
            DeckActionType.BluetoothStatus -> {
                page = AppPage.Settings
                true
            }
            DeckActionType.MediaKey -> hidManager.sendMediaKey(button.payload)
            DeckActionType.Hotkey -> hidManager.sendHotkey(button.payload)
            DeckActionType.Text -> hidManager.sendText(button.payload)
            DeckActionType.RunCommand -> hidManager.runWindowsCommand(button.payload)
            DeckActionType.AppCommand -> false
        }
        val note = when {
            button.actionType == DeckActionType.Settings -> "opened settings"
            button.actionType == DeckActionType.BluetoothStatus -> "opened connection"
            delivered -> "sent"
            button.actionType == DeckActionType.AppCommand -> "not supported by keyboard HID"
            hidStatus.state != HidConnectionState.Connected -> "no connected PC"
            else -> "unsupported payload"
        }
        logs = listOf(
            ActivityLog(
                buttonTitle = button.title,
                payload = button.payload,
                delivered = delivered,
                note = note
            )
        ) + logs.take(9)
    }

    DisposableEffect(hidManager) {
        onDispose { hidManager.stop() }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        val connectHost: (PairedHidHost) -> Unit = { host ->
            val started = hidManager.connect(host.address)
            logs = listOf(
                ActivityLog(
                    buttonTitle = "Connect",
                    payload = host.name,
                    delivered = started,
                    note = if (started) "connection requested" else "connection failed"
                )
            ) + logs.take(9)
        }

        when (page) {
            AppPage.Deck -> DeckPage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(10.dp),
                buttons = deckButtons,
                columns = deckColumns,
                status = hidStatus,
                onButtonPressed = ::pressDeckButton,
                onButtonEdit = { editingButton = it }
            )

            AppPage.Settings -> SettingsPage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                status = hidStatus,
                logs = logs,
                columns = deckColumns,
                pairedHosts = pairedHosts,
                onBack = { page = AppPage.Deck },
                onStart = ::startHid,
                onStop = { hidManager.stop() },
                onMakeDiscoverable = {
                    discoverableLauncher.launch(
                        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
                        }
                    )
                },
                onRefreshHosts = { pairedHosts = hidManager.pairedHosts() },
                onConnectHost = connectHost,
                onColumnsChange = { columns ->
                    deckColumns = columns
                    saveDeckColumns(context, columns)
                },
                onAddButton = ::addDeckButton,
                canAddBluetoothStatus = deckButtons.none { it.actionType == DeckActionType.BluetoothStatus },
                onAddBluetoothStatus = ::addBluetoothStatusButton
            )
        }
    }

    editingButton?.let { button ->
        EditButtonDialog(
            button = button,
            status = hidStatus,
            onDismiss = { editingButton = null },
            onSave = { updated ->
                val updatedButtons = deckButtons.map { if (it.id == updated.id) updated else it }
                deckButtons = updatedButtons
                saveDeckButtons(context, updatedButtons)
                editingButton = null
            },
            onDelete = {
                if (button.actionType != DeckActionType.Settings) {
                    val updatedButtons = deckButtons.filterNot { it.id == button.id }
                    deckButtons = updatedButtons
                    saveDeckButtons(context, updatedButtons)
                }
                editingButton = null
            },
            onMoveEarlier = {
                val updatedButtons = moveDeckButton(deckButtons, button.id, -1)
                deckButtons = updatedButtons
                saveDeckButtons(context, updatedButtons)
                editingButton = null
            },
            onMoveLater = {
                val updatedButtons = moveDeckButton(deckButtons, button.id, 1)
                deckButtons = updatedButtons
                saveDeckButtons(context, updatedButtons)
                editingButton = null
            }
        )
    }
}

@Composable
private fun SettingsPage(
    modifier: Modifier = Modifier,
    status: HidStatus,
    logs: List<ActivityLog>,
    columns: Int,
    pairedHosts: List<PairedHidHost>,
    onBack: () -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onMakeDiscoverable: () -> Unit,
    onRefreshHosts: () -> Unit,
    onConnectHost: (PairedHidHost) -> Unit,
    onColumnsChange: (Int) -> Unit,
    onAddButton: () -> Unit,
    canAddBluetoothStatus: Boolean,
    onAddBluetoothStatus: () -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Connection, layout, and diagnostics",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedButton(
                    shape = RoundedCornerShape(8.dp),
                    onClick = onBack
                ) {
                    Text("Deck")
                }
            }
        }

        item {
            Header(
                status = status,
                onStart = onStart,
                onStop = onStop,
                onMakeDiscoverable = onMakeDiscoverable,
                pairedHosts = pairedHosts,
                onRefreshHosts = onRefreshHosts,
                onConnectHost = onConnectHost
            )
        }

        item {
            DeckSettingsPanel(
                columns = columns,
                onColumnsChange = onColumnsChange,
                onAddButton = onAddButton,
                canAddBluetoothStatus = canAddBluetoothStatus,
                onAddBluetoothStatus = onAddBluetoothStatus
            )
        }

        item {
            DiagnosticsPanel(logs = logs)
        }
    }
}

@Composable
private fun DeckSettingsPanel(
    columns: Int,
    onColumnsChange: (Int) -> Unit,
    onAddButton: () -> Unit,
    canAddBluetoothStatus: Boolean,
    onAddBluetoothStatus: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Deck layout",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(4, 5, 6).forEach { option ->
                    val selected = columns == option
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        onClick = { onColumnsChange(option) }
                    ) {
                        Text(if (selected) "$option columns" else option.toString())
                    }
                }
                Button(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    onClick = onAddButton
                ) {
                    Text("Add key")
                }
            }
            if (canAddBluetoothStatus) {
                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    onClick = onAddBluetoothStatus
                ) {
                    Text("Add Bluetooth status key")
                }
            }
        }
    }
}

@Composable
private fun Header(
    status: HidStatus,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onMakeDiscoverable: () -> Unit,
    pairedHosts: List<PairedHidHost>,
    onRefreshHosts: () -> Unit,
    onConnectHost: (PairedHidHost) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "MobileDeck",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Phone-powered macro pad",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            StatusPill(status.state)
        }

        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        enabled = status.state != HidConnectionState.Registering,
                        onClick = onStart
                    ) {
                        Text("Register HID")
                    }
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        onClick = onStop
                    ) {
                        Text("Stop")
                    }
                }

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    onClick = onMakeDiscoverable
                ) {
                    Text("Make discoverable for pairing")
                }

                Text(
                    text = status.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "After registration, pair \"MobileDeck Keyboard\" from the PC Bluetooth settings.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Paired hosts",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = onRefreshHosts) {
                        Text("Refresh")
                    }
                }

                if (pairedHosts.isEmpty()) {
                    Text(
                        text = "No paired Bluetooth devices found.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        pairedHosts.take(3).forEach { host ->
                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                onClick = { onConnectHost(host) }
                            ) {
                                Text(
                                    text = "Connect ${host.name}",
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusPill(state: HidConnectionState) {
    val color = when (state) {
        HidConnectionState.Disconnected -> MaterialTheme.colorScheme.error
        HidConnectionState.Registering -> Color(0xFFB26A00)
        HidConnectionState.Registered -> Color(0xFF005A9C)
        HidConnectionState.Connected -> Color(0xFF2E7D32)
        HidConnectionState.Unsupported,
        HidConnectionState.PermissionMissing,
        HidConnectionState.Error -> MaterialTheme.colorScheme.error
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.14f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(50))
                .background(color)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = state.label,
            style = MaterialTheme.typography.labelMedium,
            color = color
        )
    }
}

@Composable
private fun DeckPage(
    modifier: Modifier = Modifier,
    buttons: List<DeckButton>,
    columns: Int,
    status: HidStatus,
    onButtonPressed: (DeckButton) -> Unit,
    onButtonEdit: (DeckButton) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(116.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "MobileDeck",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(statusDotColor(status.state))
                )
            }
        }

        ButtonGrid(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            buttons = buttons,
            columns = columns,
            status = status,
            onButtonPressed = onButtonPressed,
            onButtonEdit = onButtonEdit
        )
    }
}

@Composable
private fun ButtonGrid(
    modifier: Modifier = Modifier,
    buttons: List<DeckButton>,
    columns: Int,
    status: HidStatus,
    onButtonPressed: (DeckButton) -> Unit,
    onButtonEdit: (DeckButton) -> Unit
) {
    val safeColumns = columns.coerceAtLeast(1)
    val rows = buttons.chunked(safeColumns)
    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val spacing = 8.dp
        val cellSize = with(density) {
            val spacingPx = spacing.toPx()
            val maxCellWidth = (constraints.maxWidth - spacingPx * (safeColumns - 1)) / safeColumns
            val maxCellHeight = if (rows.isEmpty()) {
                maxCellWidth
            } else {
                (constraints.maxHeight - spacingPx * (rows.size - 1)) / rows.size
            }
            minOf(maxCellWidth, maxCellHeight).toDp()
        }
        Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
            rows.forEach { rowButtons ->
                Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                    rowButtons.forEach { button ->
                        DeckKey(
                            modifier = Modifier.size(cellSize),
                            button = button,
                            status = status,
                            enabled = true,
                            onPressed = { onButtonPressed(button) },
                            onEdit = { onButtonEdit(button) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DeckKey(
    modifier: Modifier = Modifier,
    button: DeckButton,
    status: HidStatus,
    enabled: Boolean,
    onPressed: () -> Unit,
    onEdit: () -> Unit
) {
    val containerColor = if (enabled) button.color else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (enabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = modifier
            .combinedClickable(
                onClick = onPressed,
                onLongClick = onEdit
            ),
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 2.dp,
        color = containerColor
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                DeckButtonIcon(
                    button = button,
                    tint = contentColor
                )
                if (button.displayMode == DeckDisplayMode.IconAndText) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        if (button.actionType == DeckActionType.BluetoothStatus) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(statusDotColor(status.state))
                            )
                        }
                        Text(
                            text = button.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = contentColor,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            if (button.displayMode == DeckDisplayMode.IconAndText) {
                Column {
                    Text(
                        text = buttonSubtitle(button, status),
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor.copy(alpha = 0.82f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = button.actionType.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor.copy(alpha = 0.72f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private fun buttonSubtitle(button: DeckButton, status: HidStatus): String {
    return if (button.actionType == DeckActionType.BluetoothStatus) {
        status.state.label
    } else {
        button.subtitle
    }
}

@Composable
private fun DeckButtonIcon(
    button: DeckButton,
    tint: Color
) {
    val image = rememberImageBitmap(button.iconImageUri)
    when {
        image != null -> {
            Image(
                bitmap = image,
                contentDescription = button.title,
                modifier = Modifier.size(30.dp)
            )
        }

        materialIconFor(button) != null -> {
            Icon(
                imageVector = materialIconFor(button)!!,
                contentDescription = button.title,
                modifier = Modifier.size(30.dp),
                tint = tint
            )
        }

        else -> {
            Text(
                text = button.icon.ifBlank { button.title.take(1).uppercase() },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = tint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun rememberImageBitmap(uriString: String): ImageBitmap? {
    val context = LocalContext.current
    var image by remember(uriString) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(uriString) {
        image = if (uriString.isBlank()) {
            null
        } else {
            runCatching {
                context.contentResolver.openInputStream(Uri.parse(uriString))?.use { input ->
                    BitmapFactory.decodeStream(input)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }
    return image
}

private fun materialIconFor(button: DeckButton): ImageVector? {
    return when (button.actionType) {
        DeckActionType.Settings -> Icons.Filled.Settings
        DeckActionType.BluetoothStatus -> Icons.Filled.Bluetooth
        DeckActionType.MediaKey -> when (button.payload.uppercase()) {
            "MUTE" -> Icons.Filled.VolumeOff
            "VOLUME_UP", "VOLUMEUP" -> Icons.Filled.VolumeUp
            "VOLUME_DOWN", "VOLUMEDOWN" -> Icons.Filled.VolumeDown
            "PLAY_PAUSE", "PLAYPAUSE", "PLAY", "PAUSE" -> Icons.Filled.PlayArrow
            "STOP" -> Icons.Filled.Stop
            "NEXT", "NEXT_TRACK" -> Icons.Filled.SkipNext
            "PREVIOUS", "PREV", "PREVIOUS_TRACK" -> Icons.Filled.SkipPrevious
            else -> null
        }
        DeckActionType.Hotkey -> when (button.icon.uppercase()) {
            "REC" -> Icons.Filled.Videocam
            else -> Icons.Filled.Keyboard
        }
        DeckActionType.Text -> Icons.Filled.TextFields
        DeckActionType.RunCommand -> Icons.Filled.Apps
        DeckActionType.AppCommand -> Icons.Filled.Code
    }
}

private fun statusDotColor(state: HidConnectionState): Color {
    return when (state) {
        HidConnectionState.Disconnected -> Color(0xFFD32F2F)
        HidConnectionState.Registering -> Color(0xFFB26A00)
        HidConnectionState.Registered -> Color(0xFF005A9C)
        HidConnectionState.Connected -> Color(0xFF2E7D32)
        HidConnectionState.Unsupported,
        HidConnectionState.PermissionMissing,
        HidConnectionState.Error -> Color(0xFFD32F2F)
    }
}

@Composable
private fun IconPresetPicker(
    selectedIcon: String,
    onIconSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Icon preset",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        val rows = iconPresets().chunked(6)
        rows.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                row.forEach { icon ->
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        onClick = { onIconSelected(icon) }
                    ) {
                        Text(
                            text = if (selectedIcon == icon) "[$icon]" else icon,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                repeat(6 - row.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun DiagnosticsPanel(logs: List<ActivityLog>) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Diagnostics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (logs.isEmpty()) {
                Text(
                    text = "No actions yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    modifier = Modifier.height(180.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(logs) { log ->
                        Text(
                            text = "${log.buttonTitle} ${log.note}: ${log.payload}",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditButtonDialog(
    button: DeckButton,
    status: HidStatus,
    onDismiss: () -> Unit,
    onSave: (DeckButton) -> Unit,
    onDelete: () -> Unit,
    onMoveEarlier: () -> Unit,
    onMoveLater: () -> Unit
) {
    var title by remember(button.id) { mutableStateOf(button.title) }
    var subtitle by remember(button.id) { mutableStateOf(button.subtitle) }
    var icon by remember(button.id) { mutableStateOf(button.icon) }
    var iconImageUri by remember(button.id) { mutableStateOf(button.iconImageUri) }
    var displayMode by remember(button.id) { mutableStateOf(button.displayMode) }
    var payload by remember(button.id) { mutableStateOf(button.payload) }
    var actionType by remember(button.id) { mutableStateOf(button.actionType) }
    var menuExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            iconImageUri = uri.toString()
        }
    }
    val canDelete = button.actionType != DeckActionType.Settings
    val actionLocked = button.actionType == DeckActionType.Settings
    val canSave = title.isNotBlank() && (!payloadRequired(actionType) || payload.isNotBlank())

    AlertDialog(
        modifier = Modifier.fillMaxWidth(0.86f),
        onDismissRequest = onDismiss,
        title = { Text("Edit key") },
        text = {
            LazyColumn(
                modifier = Modifier.height(360.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = subtitle,
                        onValueChange = { subtitle = it },
                        label = { Text("Subtitle") },
                        singleLine = true
                    )
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = icon,
                            onValueChange = { icon = it.take(3) },
                            label = { Text("Icon text") },
                            singleLine = true
                        )
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            onClick = {
                                displayMode = if (displayMode == DeckDisplayMode.IconOnly) {
                                    DeckDisplayMode.IconAndText
                                } else {
                                    DeckDisplayMode.IconOnly
                                }
                            }
                        ) {
                            Text(displayMode.label)
                        }
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            onClick = { imagePicker.launch(arrayOf("image/*")) }
                        ) {
                            Text(if (iconImageUri.isBlank()) "Pick image" else "Change image")
                        }
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            enabled = iconImageUri.isNotBlank(),
                            onClick = { iconImageUri = "" }
                        ) {
                            Text("Clear image")
                        }
                    }
                }
                item {
                    IconPresetPicker(
                        selectedIcon = icon,
                        onIconSelected = { icon = it }
                    )
                }
                item {
                    if (actionLocked) {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = actionType.label,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Action") },
                            singleLine = true
                        )
                    } else {
                        ExposedDropdownMenuBox(
                            expanded = menuExpanded,
                            onExpandedChange = { menuExpanded = !menuExpanded }
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                value = actionType.label,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Action") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpanded)
                                }
                            )
                            ExposedDropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DeckActionType.values()
                                    .filterNot { it == DeckActionType.Settings }
                                    .forEach { item ->
                                        DropdownMenuItem(
                                            text = { Text(item.label) },
                                            onClick = {
                                                actionType = item
                                                menuExpanded = false
                                            }
                                        )
                                    }
                            }
                        }
                    }
                }
                if (actionType == DeckActionType.BluetoothStatus) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(statusDotColor(status.state))
                            )
                            Text(
                                text = status.state.label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = payload,
                        onValueChange = { payload = it },
                        label = { Text("Payload") },
                        enabled = payloadRequired(actionType),
                        singleLine = true
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            onClick = onMoveEarlier
                        ) {
                            Text("Earlier")
                        }
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            onClick = onMoveLater
                        ) {
                            Text("Later")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = canSave,
                onClick = {
                    onSave(
                        button.copy(
                            title = title.trim(),
                            subtitle = subtitle.trim(),
                            icon = icon.trim(),
                            iconImageUri = iconImageUri,
                            displayMode = displayMode,
                            actionType = actionType,
                            payload = if (payloadRequired(actionType)) payload.trim() else ""
                        )
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    enabled = canDelete,
                    onClick = onDelete
                ) {
                    Text("Delete")
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

private fun defaultDeckColors(): List<Color> {
    return listOf(
        Color(0xFF005A9C),
        Color(0xFF6A4C93),
        Color(0xFF006D77),
        Color(0xFF9D4E15),
        Color(0xFF4F772D),
        Color(0xFF8A1C1C)
    )
}

private fun iconPresets(): List<String> {
    return listOf(
        "SET",
        "BT",
        "A",
        "M",
        "P",
        "S",
        "<<",
        ">>",
        "-",
        "+",
        "SH",
        "REC",
        "RUN",
        "TY",
        "WEB",
        "APP",
        "TXT",
        "CMD",
        "F1",
        "OK"
    )
}

private fun defaultButtons(): List<DeckButton> {
    val colors = defaultDeckColors()

    return listOf(
        DeckButton(1, "Settings", "Deck", "SET", "", DeckDisplayMode.IconAndText, DeckActionType.Settings, "", colors[4]),
        DeckButton(2, "Bluetooth", "Status", "BT", "", DeckDisplayMode.IconAndText, DeckActionType.BluetoothStatus, "", colors[0]),
        DeckButton(3, "Mute", "Media", "M", "", DeckDisplayMode.IconOnly, DeckActionType.MediaKey, "MUTE", colors[0]),
        DeckButton(4, "Play", "Pause", "P", "", DeckDisplayMode.IconOnly, DeckActionType.MediaKey, "PLAY_PAUSE", colors[1]),
        DeckButton(5, "Stop", "Media", "S", "", DeckDisplayMode.IconOnly, DeckActionType.MediaKey, "STOP", colors[5]),
        DeckButton(6, "Prev", "Track", "<<", "", DeckDisplayMode.IconOnly, DeckActionType.MediaKey, "PREVIOUS", colors[3]),
        DeckButton(7, "Next", "Track", ">>", "", DeckDisplayMode.IconOnly, DeckActionType.MediaKey, "NEXT", colors[4]),
        DeckButton(8, "Vol -", "Media", "-", "", DeckDisplayMode.IconOnly, DeckActionType.MediaKey, "VOLUME_DOWN", colors[0]),
        DeckButton(9, "Vol +", "Media", "+", "", DeckDisplayMode.IconOnly, DeckActionType.MediaKey, "VOLUME_UP", colors[2]),
        DeckButton(10, "Record", "Clip", "REC", "", DeckDisplayMode.IconAndText, DeckActionType.Hotkey, "WIN+ALT+R", colors[2]),
        DeckButton(11, "Run", "Path", "RUN", "", DeckDisplayMode.IconAndText, DeckActionType.RunCommand, "C:\\", colors[3]),
        DeckButton(12, "Thanks", "Chat", "TY", "", DeckDisplayMode.IconAndText, DeckActionType.Text, "Thanks checking now", colors[2])
    )
}

private fun loadDeckButtons(context: Context): List<DeckButton> {
    val raw = context.deckPrefs().getString(PREF_BUTTONS, null) ?: return defaultButtons()
    return runCatching {
        val array = JSONArray(raw)
        List(array.length()) { index ->
            val item = array.getJSONObject(index)
            DeckButton(
                id = item.getInt("id"),
                title = item.getString("title"),
                subtitle = item.optString("subtitle"),
                icon = item.optString("icon"),
                iconImageUri = item.optString("iconImageUri"),
                displayMode = runCatching {
                    DeckDisplayMode.valueOf(item.optString("displayMode"))
                }.getOrDefault(DeckDisplayMode.IconAndText),
                actionType = runCatching {
                    DeckActionType.valueOf(item.getString("actionType"))
                }.getOrDefault(DeckActionType.Hotkey),
                payload = item.getString("payload"),
                color = Color(item.getInt("color"))
            )
        }
    }.map { normalizeDeckButtons(it) }.getOrDefault(defaultButtons())
}

private fun saveDeckButtons(context: Context, buttons: List<DeckButton>) {
    val array = JSONArray()
    buttons.forEach { button ->
        array.put(
            JSONObject()
                .put("id", button.id)
                .put("title", button.title)
                .put("subtitle", button.subtitle)
                .put("icon", button.icon)
                .put("iconImageUri", button.iconImageUri)
                .put("displayMode", button.displayMode.name)
                .put("actionType", button.actionType.name)
                .put("payload", button.payload)
                .put("color", button.color.toArgb())
        )
    }
    context.deckPrefs().edit().putString(PREF_BUTTONS, array.toString()).apply()
}

private fun normalizeDeckButtons(buttons: List<DeckButton>): List<DeckButton> {
    if (buttons.any { it.actionType == DeckActionType.Settings }) return buttons
    val colors = defaultDeckColors()
    val settingsButton = DeckButton(
        id = nextDeckButtonId(buttons),
        title = "Settings",
        subtitle = "Deck",
        icon = "SET",
        iconImageUri = "",
        displayMode = DeckDisplayMode.IconAndText,
        actionType = DeckActionType.Settings,
        payload = "",
        color = colors[4]
    )
    return listOf(settingsButton) + buttons
}

private fun payloadRequired(actionType: DeckActionType): Boolean {
    return when (actionType) {
        DeckActionType.Settings,
        DeckActionType.BluetoothStatus -> false
        DeckActionType.MediaKey,
        DeckActionType.Hotkey,
        DeckActionType.Text,
        DeckActionType.RunCommand,
        DeckActionType.AppCommand -> true
    }
}

private fun loadDeckColumns(context: Context): Int {
    return context.deckPrefs().getInt(PREF_COLUMNS, DEFAULT_COLUMNS).coerceIn(MIN_COLUMNS, MAX_COLUMNS)
}

private fun saveDeckColumns(context: Context, columns: Int) {
    context.deckPrefs().edit().putInt(PREF_COLUMNS, columns.coerceIn(MIN_COLUMNS, MAX_COLUMNS)).apply()
}

private fun nextDeckButtonId(buttons: List<DeckButton>): Int {
    return (buttons.maxOfOrNull { it.id } ?: 0) + 1
}

private fun moveDeckButton(
    buttons: List<DeckButton>,
    id: Int,
    delta: Int
): List<DeckButton> {
    val from = buttons.indexOfFirst { it.id == id }
    if (from < 0) return buttons
    val to = (from + delta).coerceIn(buttons.indices)
    if (from == to) return buttons
    return buttons.toMutableList().apply {
        add(to, removeAt(from))
    }
}

private fun Context.deckPrefs() = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

private const val PREFS_NAME = "mobile_deck"
private const val PREF_BUTTONS = "buttons"
private const val PREF_COLUMNS = "columns"
private const val MIN_COLUMNS = 4
private const val MAX_COLUMNS = 6
private const val DEFAULT_COLUMNS = 6

@Preview(showBackground = true)
@Composable
private fun MobileDeckPreview() {
    MobileDeckTheme {
        MobileDeckApp()
    }
}
