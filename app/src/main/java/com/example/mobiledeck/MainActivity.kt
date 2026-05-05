package com.example.mobiledeck

import android.os.Build
import android.os.Bundle
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mobiledeck.ui.theme.MobileDeckTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MobileDeckTheme {
                MobileDeckApp()
            }
        }
    }
}

private enum class DeckActionType(val label: String) {
    MediaKey("Media key"),
    Hotkey("Hotkey"),
    Text("Text"),
    AppCommand("App command")
}

private data class DeckButton(
    val id: Int,
    val title: String,
    val subtitle: String,
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
    var deckButtons by remember { mutableStateOf(defaultButtons()) }
    var editingButton by remember { mutableStateOf<DeckButton?>(null) }
    var logs by remember { mutableStateOf(emptyList<ActivityLog>()) }

    DisposableEffect(hidManager) {
        onDispose { hidManager.stop() }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Header(
                    status = hidStatus,
                    onStart = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            permissionLauncher.launch(HidKeyboardManager.REQUIRED_BLUETOOTH_PERMISSIONS)
                        } else {
                            hidManager.start()
                        }
                        pairedHosts = hidManager.pairedHosts()
                    },
                    onStop = { hidManager.stop() },
                    onMakeDiscoverable = {
                        discoverableLauncher.launch(
                            Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                                putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
                            }
                        )
                    },
                    pairedHosts = pairedHosts,
                    onRefreshHosts = { pairedHosts = hidManager.pairedHosts() },
                    onConnectHost = { host ->
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
                )
            }

            item {
                DiagnosticsPanel(logs = logs)
            }

            item {
                DeckGrid(
                    buttons = deckButtons,
                    onButtonPressed = { button ->
                        val delivered = when (button.actionType) {
                            DeckActionType.MediaKey -> hidManager.sendMediaKey(button.payload)
                            DeckActionType.Hotkey -> hidManager.sendHotkey(button.payload)
                            DeckActionType.Text -> hidManager.sendText(button.payload)
                            DeckActionType.AppCommand -> false
                        }
                        val note = when {
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
                    },
                    onButtonEdit = { editingButton = it }
                )
            }
        }
    }

    editingButton?.let { button ->
        EditButtonDialog(
            button = button,
            onDismiss = { editingButton = null },
            onSave = { updated ->
                deckButtons = deckButtons.map { if (it.id == updated.id) updated else it }
                editingButton = null
            }
        )
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
private fun DeckGrid(
    buttons: List<DeckButton>,
    onButtonPressed: (DeckButton) -> Unit,
    onButtonEdit: (DeckButton) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Deck",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${buttons.size} keys",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .height(560.dp),
            contentPadding = PaddingValues(0.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            userScrollEnabled = false
        ) {
            items(buttons, key = { it.id }) { button ->
                DeckKey(
                    button = button,
                    enabled = true,
                    onPressed = { onButtonPressed(button) },
                    onEdit = { onButtonEdit(button) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DeckKey(
    button: DeckButton,
    enabled: Boolean,
    onPressed: () -> Unit,
    onEdit: () -> Unit
) {
    val containerColor = if (enabled) button.color else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (enabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = Modifier
            .aspectRatio(1f)
            .combinedClickable(
                onClick = onPressed,
                onLongClick = onEdit
            ),
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 2.dp,
        color = containerColor
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = button.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Column {
                Text(
                    text = button.subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor.copy(alpha = 0.82f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Bluetooth HID -> ${button.actionType.label}",
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.72f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
    onDismiss: () -> Unit,
    onSave: (DeckButton) -> Unit
) {
    var title by remember(button.id) { mutableStateOf(button.title) }
    var subtitle by remember(button.id) { mutableStateOf(button.subtitle) }
    var payload by remember(button.id) { mutableStateOf(button.payload) }
    var actionType by remember(button.id) { mutableStateOf(button.actionType) }
    var menuExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit key") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Subtitle") },
                    singleLine = true
                )
                ExposedDropdownMenuBox(
                    expanded = menuExpanded,
                    onExpandedChange = { menuExpanded = !menuExpanded }
                ) {
                    OutlinedTextField(
                        modifier = Modifier.menuAnchor(),
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
                        DeckActionType.values().forEach { item ->
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
                OutlinedTextField(
                    value = payload,
                    onValueChange = { payload = it },
                    label = { Text("Payload") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank() && payload.isNotBlank(),
                onClick = {
                    onSave(
                        button.copy(
                            title = title.trim(),
                            subtitle = subtitle.trim(),
                            actionType = actionType,
                            payload = payload.trim()
                        )
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private fun defaultButtons(): List<DeckButton> {
    val colors = listOf(
        Color(0xFF005A9C),
        Color(0xFF6A4C93),
        Color(0xFF006D77),
        Color(0xFF9D4E15),
        Color(0xFF4F772D),
        Color(0xFF8A1C1C)
    )

    return listOf(
        DeckButton(1, "Test A", "Type", DeckActionType.Text, "a", colors[2]),
        DeckButton(2, "Mute", "Media", DeckActionType.MediaKey, "MUTE", colors[0]),
        DeckButton(3, "Play", "Pause", DeckActionType.MediaKey, "PLAY_PAUSE", colors[1]),
        DeckButton(4, "Stop", "Media", DeckActionType.MediaKey, "STOP", colors[5]),
        DeckButton(5, "Prev", "Track", DeckActionType.MediaKey, "PREVIOUS", colors[3]),
        DeckButton(6, "Next", "Track", DeckActionType.MediaKey, "NEXT", colors[4]),
        DeckButton(7, "Vol -", "Media", DeckActionType.MediaKey, "VOLUME_DOWN", colors[0]),
        DeckButton(8, "Vol +", "Media", DeckActionType.MediaKey, "VOLUME_UP", colors[2]),
        DeckButton(9, "Share", "Screen", DeckActionType.Hotkey, "CTRL+SHIFT+E", colors[1]),
        DeckButton(10, "Record", "Clip", DeckActionType.Hotkey, "WIN+ALT+R", colors[2]),
        DeckButton(11, "BRB", "Chat", DeckActionType.Text, "Be right back", colors[1]),
        DeckButton(12, "Thanks", "Chat", DeckActionType.Text, "Thanks checking now", colors[2])
    )
}

@Preview(showBackground = true)
@Composable
private fun MobileDeckPreview() {
    MobileDeckTheme {
        MobileDeckApp()
    }
}
