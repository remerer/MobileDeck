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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Keyboard
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mobiledeck.ui.theme.MobileDeckTheme
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.abs
import kotlin.math.roundToInt

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
    PreviousPage("Previous page"),
    NextPage("Next page"),
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

private enum class PageSwipeAxis(val label: String) {
    Horizontal("Horizontal pages"),
    Vertical("Vertical pages")
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
    val color: Color,
    val position: Int = 0
)

private data class DeckPageConfig(
    val id: Int,
    val name: String,
    val buttons: List<DeckButton>
)

private data class ActivityLog(
    val buttonTitle: String,
    val payload: String,
    val delivered: Boolean,
    val note: String
)

private enum class AppPage {
    Deck,
    LayoutEditor,
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
    var deckPages by remember { mutableStateOf(loadDeckPages(context)) }
    var activeDeckPageId by remember { mutableStateOf(deckPages.first().id) }
    var deckColumns by remember { mutableStateOf(loadDeckColumns(context)) }
    var deckRows by remember { mutableStateOf(loadDeckRows(context)) }
    var pageSwipeAxis by remember { mutableStateOf(loadPageSwipeAxis(context)) }
    var multiTouchPageSwipe by remember { mutableStateOf(loadMultiTouchPageSwipe(context)) }
    var editingButton by remember { mutableStateOf<DeckButton?>(null) }
    var logs by remember { mutableStateOf(emptyList<ActivityLog>()) }
    var page by remember { mutableStateOf(AppPage.Deck) }
    val activeDeckPage = deckPages.firstOrNull { it.id == activeDeckPageId } ?: deckPages.first()
    val deckButtons = activeDeckPage.buttons

    fun startHid() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionLauncher.launch(HidKeyboardManager.REQUIRED_BLUETOOTH_PERMISSIONS)
        } else {
            hidManager.start()
        }
        pairedHosts = hidManager.pairedHosts()
    }

    fun addDeckButton(position: Int? = null, editAfterCreate: Boolean = false) {
        val colors = defaultDeckColors()
        val buttonCapacity = deckColumns * deckRows - 1
        val targetPosition = position ?: nextOpenPosition(deckButtons, buttonCapacity)
        val newButton = DeckButton(
            id = nextDeckButtonId(deckPages.flatMap { it.buttons }),
            title = "New key",
            subtitle = "Custom",
            icon = "+",
            iconImageUri = "",
            displayMode = DeckDisplayMode.IconAndText,
            actionType = DeckActionType.Hotkey,
            payload = "CTRL+F9",
            color = colors[deckButtons.size % colors.size],
            position = targetPosition
        )
        if (position == null && deckButtons.size >= buttonCapacity && deckPages.size >= MAX_PAGES) return
        val updatedPages = if (position == null && deckButtons.size >= buttonCapacity) {
            val newPage = DeckPageConfig(
                id = nextDeckPageId(deckPages),
                name = "Page ${deckPages.size + 1}",
                buttons = listOf(newButton.copy(position = 0))
            )
            activeDeckPageId = newPage.id
            deckPages + newPage
        } else {
            updateDeckPage(deckPages, activeDeckPage.id) { it.buttons + newButton }
        }
        deckPages = updatedPages
        saveDeckPages(context, updatedPages)
        if (editAfterCreate) {
            editingButton = newButton
        }
    }

    fun addBluetoothStatusButton() {
        val colors = defaultDeckColors()
        val newButton = DeckButton(
            id = nextDeckButtonId(deckPages.flatMap { it.buttons }),
            title = "Bluetooth",
            subtitle = "Status",
            icon = "BT",
            iconImageUri = "",
            displayMode = DeckDisplayMode.IconAndText,
            actionType = DeckActionType.BluetoothStatus,
            payload = "",
            color = colors[deckButtons.size % colors.size],
            position = nextOpenPosition(deckButtons, deckColumns * deckRows - 1)
        )
        val updatedPages = updateDeckPage(deckPages, activeDeckPage.id) { it.buttons + newButton }
        deckPages = updatedPages
        saveDeckPages(context, updatedPages)
    }

    fun addDeckPage() {
        if (deckPages.size >= MAX_PAGES) return
        val nextPageNumber = deckPages.size + 1
        val newPage = DeckPageConfig(
            id = nextDeckPageId(deckPages),
            name = "Page $nextPageNumber",
            buttons = emptyList()
        )
        val updatedPages = deckPages + newPage
        deckPages = updatedPages
        activeDeckPageId = newPage.id
        saveDeckPages(context, updatedPages)
    }

    fun switchDeckPage(delta: Int) {
        val currentIndex = deckPages.indexOfFirst { it.id == activeDeckPage.id }
        val target = (currentIndex + delta).coerceIn(deckPages.indices)
        activeDeckPageId = deckPages[target].id
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
            DeckActionType.PreviousPage -> {
                switchDeckPage(-1)
                true
            }
            DeckActionType.NextPage -> {
                switchDeckPage(1)
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
            button.actionType == DeckActionType.PreviousPage -> "previous page"
            button.actionType == DeckActionType.NextPage -> "next page"
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

    fun moveDeckButtonToSlot(button: DeckButton, targetPosition: Int) {
        val pageCapacity = deckColumns * deckRows - 1
        if (targetPosition !in 0 until pageCapacity || button.position == targetPosition) return
        val targetButton = deckButtons.firstOrNull { it.position == targetPosition }
        val updatedPages = updateDeckPage(deckPages, activeDeckPage.id) { deckPage ->
            deckPage.buttons.map { existing ->
                when (existing.id) {
                    button.id -> existing.copy(position = targetPosition)
                    targetButton?.id -> existing.copy(position = button.position)
                    else -> existing
                }
            }
        }
        deckPages = updatedPages
        saveDeckPages(context, updatedPages)
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
                deckPages = deckPages,
                activePageId = activeDeckPage.id,
                columns = deckColumns,
                rows = deckRows,
                status = hidStatus,
                previewMode = false,
                pageSwipeAxis = pageSwipeAxis,
                multiTouchPageSwipe = multiTouchPageSwipe,
                onPageSwipe = ::switchDeckPage,
                onAddPage = ::addDeckPage,
                onButtonPressed = ::pressDeckButton,
                onButtonEdit = {},
                onButtonMoved = ::moveDeckButtonToSlot,
                onEmptySlotLongPressed = { slot -> addDeckButton(slot, editAfterCreate = true) }
            )

            AppPage.LayoutEditor -> LayoutEditorPage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(10.dp),
                buttons = deckButtons,
                deckPages = deckPages,
                activePageId = activeDeckPage.id,
                columns = deckColumns,
                rows = deckRows,
                status = hidStatus,
                pageSwipeAxis = pageSwipeAxis,
                multiTouchPageSwipe = multiTouchPageSwipe,
                onBack = { page = AppPage.Settings },
                onColumnsChange = { columns ->
                    deckColumns = columns
                    saveDeckColumns(context, columns)
                },
                onRowsChange = { rows ->
                    deckRows = rows
                    saveDeckRows(context, rows)
                },
                onPageSwipe = ::switchDeckPage,
                onAddPage = ::addDeckPage,
                onButtonEdit = { editingButton = it },
                onButtonMoved = ::moveDeckButtonToSlot,
                onEmptySlotLongPressed = { slot -> addDeckButton(slot, editAfterCreate = true) }
            )

            AppPage.Settings -> SettingsPage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                status = hidStatus,
                logs = logs,
                columns = deckColumns,
                rows = deckRows,
                pageName = activeDeckPage.name,
                pageCount = deckPages.size,
                pairedHosts = pairedHosts,
                onBack = { page = AppPage.Deck },
                deckPages = deckPages,
                activePageId = activeDeckPage.id,
                pageSwipeAxis = pageSwipeAxis,
                multiTouchPageSwipe = multiTouchPageSwipe,
                onLayoutEditor = { page = AppPage.LayoutEditor },
                onPageSwipeAxisChange = { axis ->
                    pageSwipeAxis = axis
                    savePageSwipeAxis(context, axis)
                },
                onMultiTouchPageSwipeChange = { enabled ->
                    multiTouchPageSwipe = enabled
                    saveMultiTouchPageSwipe(context, enabled)
                },
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
                onRowsChange = { rows ->
                    deckRows = rows
                    saveDeckRows(context, rows)
                },
                onAddButton = { addDeckButton() },
                canAddBluetoothStatus = deckPages.none { page -> page.buttons.any { it.actionType == DeckActionType.BluetoothStatus } },
                onAddBluetoothStatus = ::addBluetoothStatusButton,
                onAddPage = ::addDeckPage
            )
        }
    }

    editingButton?.let { button ->
        EditButtonDialog(
            button = button,
            status = hidStatus,
            onDismiss = { editingButton = null },
            onSave = { updated ->
                val updatedPages = updateDeckButton(deckPages, updated)
                deckPages = updatedPages
                saveDeckPages(context, updatedPages)
                editingButton = null
            },
            onDelete = {
                if (button.actionType != DeckActionType.Settings) {
                    val updatedPages = updateDeckPage(deckPages, activeDeckPage.id) {
                        it.buttons.filterNot { existing -> existing.id == button.id }
                    }
                    deckPages = updatedPages
                    saveDeckPages(context, updatedPages)
                }
                editingButton = null
            },
            onMoveEarlier = {
                val updatedPages = updateDeckPage(deckPages, activeDeckPage.id) {
                    moveDeckButton(it.buttons, button.id, -1)
                }
                deckPages = updatedPages
                saveDeckPages(context, updatedPages)
                editingButton = null
            },
            onMoveLater = {
                val updatedPages = updateDeckPage(deckPages, activeDeckPage.id) {
                    moveDeckButton(it.buttons, button.id, 1)
                }
                deckPages = updatedPages
                saveDeckPages(context, updatedPages)
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
    rows: Int,
    deckPages: List<DeckPageConfig>,
    activePageId: Int,
    pageSwipeAxis: PageSwipeAxis,
    multiTouchPageSwipe: Boolean,
    pageName: String,
    pageCount: Int,
    pairedHosts: List<PairedHidHost>,
    onBack: () -> Unit,
    onLayoutEditor: () -> Unit,
    onPageSwipeAxisChange: (PageSwipeAxis) -> Unit,
    onMultiTouchPageSwipeChange: (Boolean) -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onMakeDiscoverable: () -> Unit,
    onRefreshHosts: () -> Unit,
    onConnectHost: (PairedHidHost) -> Unit,
    onColumnsChange: (Int) -> Unit,
    onRowsChange: (Int) -> Unit,
    onAddButton: () -> Unit,
    canAddBluetoothStatus: Boolean,
    onAddBluetoothStatus: () -> Unit,
    onAddPage: () -> Unit
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
                    text = "Connection, pages, and diagnostics",
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
                OutlinedButton(
                    shape = RoundedCornerShape(8.dp),
                    onClick = onLayoutEditor
                ) {
                    Text("Layout editor")
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
                deckPages = deckPages,
                activePageId = activePageId,
                pageSwipeAxis = pageSwipeAxis,
                multiTouchPageSwipe = multiTouchPageSwipe,
                pageName = pageName,
                pageCount = pageCount,
                onPageSwipeAxisChange = onPageSwipeAxisChange,
                onMultiTouchPageSwipeChange = onMultiTouchPageSwipeChange,
                canAddBluetoothStatus = canAddBluetoothStatus,
                onAddBluetoothStatus = onAddBluetoothStatus,
                onAddPage = onAddPage
            )
        }

        if (BuildConfig.DEBUG) {
            item {
                DiagnosticsPanel(logs = logs)
            }
        }
    }
}

@Composable
private fun DeckSettingsPanel(
    deckPages: List<DeckPageConfig>,
    activePageId: Int,
    pageSwipeAxis: PageSwipeAxis,
    multiTouchPageSwipe: Boolean,
    pageName: String,
    pageCount: Int,
    onPageSwipeAxisChange: (PageSwipeAxis) -> Unit,
    onMultiTouchPageSwipeChange: (Boolean) -> Unit,
    canAddBluetoothStatus: Boolean,
    onAddBluetoothStatus: () -> Unit,
    onAddPage: () -> Unit
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
            Text(
                text = "$pageName - $pageCount pages",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            PageIndicator(
                modifier = Modifier.fillMaxWidth(),
                pageCount = deckPages.size,
                activeIndex = deckPages.indexOfFirst { it.id == activePageId }.coerceAtLeast(0)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PageSwipeAxis.values().forEach { axis ->
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        onClick = { onPageSwipeAxisChange(axis) }
                    ) {
                        Text(if (pageSwipeAxis == axis) axis.label else axis.label.removeSuffix(" pages"))
                    }
                }
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                onClick = { onMultiTouchPageSwipeChange(!multiTouchPageSwipe) }
            ) {
                Text(if (multiTouchPageSwipe) "Multi-touch swipe on" else "Multi-touch swipe off")
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                enabled = pageCount < MAX_PAGES,
                onClick = onAddPage
            ) {
                Text("Add page ($pageCount/$MAX_PAGES)")
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
private fun LayoutEditorPage(
    modifier: Modifier = Modifier,
    buttons: List<DeckButton>,
    deckPages: List<DeckPageConfig>,
    activePageId: Int,
    columns: Int,
    rows: Int,
    status: HidStatus,
    pageSwipeAxis: PageSwipeAxis,
    multiTouchPageSwipe: Boolean,
    onBack: () -> Unit,
    onColumnsChange: (Int) -> Unit,
    onRowsChange: (Int) -> Unit,
    onPageSwipe: (Int) -> Unit,
    onAddPage: () -> Unit,
    onButtonEdit: (DeckButton) -> Unit,
    onButtonMoved: (DeckButton, Int) -> Unit,
    onEmptySlotLongPressed: (Int) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                shape = RoundedCornerShape(8.dp),
                onClick = onBack
            ) {
                Text("Settings")
            }
            LayoutSlider(
                modifier = Modifier.weight(1f),
                label = "Columns",
                value = columns,
                range = MIN_COLUMNS..MAX_COLUMNS,
                onValueChange = onColumnsChange
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DeckPage(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                buttons = buttons,
                deckPages = deckPages,
                activePageId = activePageId,
                columns = columns,
                rows = rows,
                status = status,
                previewMode = true,
                pageSwipeAxis = pageSwipeAxis,
                multiTouchPageSwipe = multiTouchPageSwipe,
                onPageSwipe = onPageSwipe,
                onAddPage = onAddPage,
                onButtonPressed = onButtonEdit,
                onButtonEdit = onButtonEdit,
                onButtonMoved = onButtonMoved,
                onEmptySlotLongPressed = onEmptySlotLongPressed
            )
            VerticalLayoutSlider(
                modifier = Modifier
                    .width(46.dp)
                    .fillMaxHeight(),
                label = "Rows",
                value = rows,
                range = MIN_ROWS..MAX_ROWS,
                onValueChange = onRowsChange
            )
        }
    }
}

@Composable
private fun LayoutSlider(
    modifier: Modifier = Modifier,
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "$label $value",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
        Slider(
            modifier = Modifier.weight(1f),
            value = value.toFloat(),
            onValueChange = { next ->
                onValueChange(next.roundToInt().coerceIn(range.first, range.last))
            },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = (range.last - range.first - 1).coerceAtLeast(0)
        )
    }
}

@Composable
private fun VerticalLayoutSlider(
    modifier: Modifier = Modifier,
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$label $value",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Slider(
                modifier = Modifier
                    .width(320.dp)
                    .graphicsLayer(rotationZ = -90f),
                value = value.toFloat(),
                onValueChange = { next ->
                    onValueChange(next.roundToInt().coerceIn(range.first, range.last))
                },
                valueRange = range.first.toFloat()..range.last.toFloat(),
                steps = (range.last - range.first - 1).coerceAtLeast(0)
            )
        }
    }
}

@Composable
private fun DeckPage(
    modifier: Modifier = Modifier,
    buttons: List<DeckButton>,
    deckPages: List<DeckPageConfig>,
    activePageId: Int,
    columns: Int,
    rows: Int,
    status: HidStatus,
    previewMode: Boolean,
    pageSwipeAxis: PageSwipeAxis,
    multiTouchPageSwipe: Boolean,
    onPageSwipe: (Int) -> Unit,
    onAddPage: () -> Unit,
    onButtonPressed: (DeckButton) -> Unit,
    onButtonEdit: (DeckButton) -> Unit,
    onButtonMoved: (DeckButton, Int) -> Unit,
    onEmptySlotLongPressed: (Int) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val safeColumns = columns.coerceIn(MIN_COLUMNS, MAX_COLUMNS)
        val safeRows = rows.coerceIn(MIN_ROWS, MAX_ROWS)
        val density = LocalDensity.current
        val spacing = 8.dp
        val swipeModifier = Modifier.multiTouchPageSwipe(
            enabled = multiTouchPageSwipe,
            axis = pageSwipeAxis,
            onPageSwipe = onPageSwipe
        )
        val cellSize = with(density) {
            val spacingPx = spacing.toPx()
            val maxCellWidth = (constraints.maxWidth - spacingPx * (safeColumns - 1)) / safeColumns
            val maxCellHeight = (constraints.maxHeight - spacingPx * (safeRows - 1)) / safeRows
            minOf(maxCellWidth, maxCellHeight).toDp()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(swipeModifier)
        ) {
            ButtonGrid(
                modifier = Modifier
                    .fillMaxSize(),
                buttons = buttons,
                columns = safeColumns,
                rows = safeRows,
                cellSize = cellSize,
                spacing = spacing,
                status = status,
                previewMode = previewMode,
                onButtonPressed = onButtonPressed,
                onButtonEdit = onButtonEdit,
                onButtonMoved = onButtonMoved,
                onEmptySlotLongPressed = onEmptySlotLongPressed
            )
            PageIndicator(
                modifier = Modifier
                    .align(
                        if (pageSwipeAxis == PageSwipeAxis.Horizontal) {
                            Alignment.BottomCenter
                        } else {
                            Alignment.CenterStart
                        }
                    )
                    .padding(6.dp),
                pageCount = deckPages.size,
                activeIndex = deckPages.indexOfFirst { it.id == activePageId }.coerceAtLeast(0),
                axis = pageSwipeAxis
            )
        }
    }
}

@Composable
private fun PageIndicator(
    modifier: Modifier = Modifier,
    pageCount: Int,
    activeIndex: Int,
    axis: PageSwipeAxis = PageSwipeAxis.Horizontal
) {
    val content: @Composable () -> Unit = {
        repeat(pageCount.coerceIn(1, MAX_PAGES)) { index ->
            PageDot(index == activeIndex)
        }
    }
    if (axis == PageSwipeAxis.Horizontal) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) { content() }
    } else {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) { content() }
    }
}

@Composable
private fun PageDot(active: Boolean) {
    Box(
        modifier = Modifier
            .padding(3.dp)
            .size(if (active) 9.dp else 6.dp)
            .clip(RoundedCornerShape(50))
            .background(
                if (active) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                }
            )
    )
}

private fun Modifier.multiTouchPageSwipe(
    enabled: Boolean,
    axis: PageSwipeAxis,
    onPageSwipe: (Int) -> Unit
): Modifier {
    if (!enabled) return this
    return pointerInput(axis) {
        awaitPointerEventScope {
            var tracking = false
            var previousCentroid: Offset? = null
            var totalDrag = Offset.Zero
            var maxPointerCount = 0

            while (true) {
                val event = awaitPointerEvent()
                val pressed = event.changes.filter { it.pressed }
                if (pressed.isNotEmpty()) {
                    val centroid = pressed
                        .map { it.position }
                        .reduce { acc, offset -> acc + offset } / pressed.size.toFloat()
                    if (!tracking) {
                        tracking = true
                        previousCentroid = centroid
                        totalDrag = Offset.Zero
                        maxPointerCount = pressed.size
                    } else {
                        previousCentroid?.let { totalDrag += centroid - it }
                        previousCentroid = centroid
                        maxPointerCount = maxOf(maxPointerCount, pressed.size)
                    }
                } else if (tracking) {
                    if (maxPointerCount in 2..3) {
                        val threshold = 80f
                        when (axis) {
                            PageSwipeAxis.Horizontal -> {
                                if (abs(totalDrag.x) > threshold && abs(totalDrag.x) > abs(totalDrag.y)) {
                                    onPageSwipe(if (totalDrag.x < 0f) 1 else -1)
                                }
                            }
                            PageSwipeAxis.Vertical -> {
                                if (abs(totalDrag.y) > threshold && abs(totalDrag.y) > abs(totalDrag.x)) {
                                    onPageSwipe(if (totalDrag.y < 0f) 1 else -1)
                                }
                            }
                        }
                    }
                    tracking = false
                    previousCentroid = null
                    totalDrag = Offset.Zero
                    maxPointerCount = 0
                }
            }
        }
    }
}

@Composable
private fun ButtonGrid(
    modifier: Modifier = Modifier,
    buttons: List<DeckButton>,
    columns: Int,
    rows: Int,
    cellSize: Dp,
    spacing: Dp,
    status: HidStatus,
    previewMode: Boolean,
    onButtonPressed: (DeckButton) -> Unit,
    onButtonEdit: (DeckButton) -> Unit,
    onButtonMoved: (DeckButton, Int) -> Unit,
    onEmptySlotLongPressed: (Int) -> Unit
) {
    val safeColumns = columns.coerceAtLeast(1)
    val slotCount = safeColumns * rows.coerceAtLeast(1)
    val slots = List(slotCount) { slot ->
        if (slot == 0) null else buttons.firstOrNull { it.position == slot - 1 }
    }
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        slots.chunked(safeColumns).forEachIndexed { rowIndex, rowButtons ->
            Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                rowButtons.forEachIndexed { columnIndex, button ->
                    val slot = rowIndex * safeColumns + columnIndex
                    if (slot == 0) {
                        TitleDeckSlot(
                            modifier = Modifier.size(cellSize),
                            status = status
                        )
                    } else if (button == null) {
                        EmptyDeckSlot(
                            modifier = Modifier.size(cellSize),
                            onLongPress = { onEmptySlotLongPressed(slot - 1) }
                        )
                    } else {
                        DeckKey(
                            modifier = Modifier.size(cellSize),
                            button = button,
                            status = status,
                            enabled = true,
                            previewMode = previewMode,
                            columns = safeColumns,
                            slot = slot - 1,
                            cellSize = cellSize,
                            spacing = spacing,
                            onPressed = { onButtonPressed(button) },
                            onEdit = { onButtonEdit(button) },
                            onMove = { targetSlot -> onButtonMoved(button, targetSlot.coerceIn(0, slotCount - 2)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TitleDeckSlot(
    modifier: Modifier = Modifier,
    status: HidStatus
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 0.dp,
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "MobileDeck",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(statusDotColor(status.state))
            )
        }
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun EmptyDeckSlot(
    modifier: Modifier = Modifier,
    onLongPress: () -> Unit
) {
    Surface(
        modifier = modifier.combinedClickable(
            onClick = {},
            onLongClick = onLongPress
        ),
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 0.dp,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.36f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "+",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
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
    previewMode: Boolean,
    columns: Int,
    slot: Int,
    cellSize: Dp,
    spacing: Dp,
    onPressed: () -> Unit,
    onEdit: () -> Unit,
    onMove: (Int) -> Unit
) {
    val containerColor = if (enabled) button.color else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (enabled) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    val density = LocalDensity.current
    var dragOffset by remember(button.id) { mutableStateOf(Offset.Zero) }
    val moveThresholdPx = with(density) { ((cellSize + spacing) * 0.55f).toPx() }
    val dragActive = abs(dragOffset.x) > moveThresholdPx || abs(dragOffset.y) > moveThresholdPx
    val animatedX by animateFloatAsState(
        targetValue = dragOffset.x,
        animationSpec = tween(durationMillis = if (dragOffset == Offset.Zero) 140 else 40),
        label = "keyDragX"
    )
    val animatedY by animateFloatAsState(
        targetValue = dragOffset.y,
        animationSpec = tween(durationMillis = if (dragOffset == Offset.Zero) 140 else 40),
        label = "keyDragY"
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (dragActive) 1.06f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "keyDragScale"
    )
    val dragModifier = if (previewMode) {
        Modifier.pointerInput(button.id, columns, slot, cellSize, spacing) {
            detectDragGestures(
                onDragStart = { dragOffset = Offset.Zero },
                onDrag = { change, dragAmount ->
                    change.consume()
                    dragOffset += dragAmount
                },
                onDragEnd = {
                    val stepPx = with(density) { (cellSize + spacing).toPx() }
                    val columnDelta = (dragOffset.x / stepPx).roundToInt()
                    val rowDelta = (dragOffset.y / stepPx).roundToInt()
                    onMove(slot + columnDelta + rowDelta * columns)
                    dragOffset = Offset.Zero
                },
                onDragCancel = { dragOffset = Offset.Zero }
            )
        }
    } else {
        Modifier
    }

    Surface(
        modifier = modifier
            .then(dragModifier)
            .graphicsLayer {
                translationX = animatedX
                translationY = animatedY
                scaleX = animatedScale
                scaleY = animatedScale
            }
            .combinedClickable(
                onClick = onPressed,
                onLongClick = if (previewMode) onEdit else null
            ),
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 2.dp,
        color = containerColor
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = if (button.displayMode == DeckDisplayMode.IconOnly) {
                Arrangement.Center
            } else {
                Arrangement.SpaceBetween
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                DeckButtonIcon(
                    button = button,
                    tint = contentColor,
                    large = button.displayMode == DeckDisplayMode.IconOnly
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
    tint: Color,
    large: Boolean = false
) {
    val image = rememberImageBitmap(button.iconImageUri)
    val iconSize = if (large) 46.dp else 30.dp
    when {
        image != null -> {
            Image(
                bitmap = image,
                contentDescription = button.title,
                modifier = Modifier.size(iconSize)
            )
        }

        materialIconFor(button) != null -> {
            Icon(
                imageVector = materialIconFor(button)!!,
                contentDescription = button.title,
                modifier = Modifier.size(iconSize),
                tint = tint
            )
        }

        else -> {
            Text(
                text = button.icon.ifBlank { button.title.take(1).uppercase() },
                style = if (large) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleLarge,
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
        DeckActionType.PreviousPage -> Icons.Filled.SkipPrevious
        DeckActionType.NextPage -> Icons.Filled.SkipNext
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
    ).mapIndexed { index, button -> button.copy(position = index) }
}

private fun loadDeckButtons(context: Context): List<DeckButton> {
    val raw = context.deckPrefs().getString(PREF_BUTTONS, null) ?: return defaultButtons()
    return runCatching {
        val array = JSONArray(raw)
        List(array.length().coerceAtMost(MAX_PAGES)) { index ->
            decodeDeckButton(array.getJSONObject(index), index)
        }
    }.map { normalizeDeckButtons(it) }.getOrDefault(defaultButtons())
}

private fun saveDeckButtons(context: Context, buttons: List<DeckButton>) {
    val array = JSONArray()
    buttons.forEach { button ->
        array.put(encodeDeckButton(button))
    }
    context.deckPrefs().edit().putString(PREF_BUTTONS, array.toString()).apply()
}

private fun loadDeckPages(context: Context): List<DeckPageConfig> {
    val raw = context.deckPrefs().getString(PREF_PAGES, null)
        ?: return listOf(DeckPageConfig(1, "Page 1", loadDeckButtons(context)))
    return runCatching {
        val array = JSONArray(raw)
        List(array.length()) { index ->
            val item = array.getJSONObject(index)
            val buttons = item.optJSONArray("buttons") ?: JSONArray()
            DeckPageConfig(
                id = item.getInt("id"),
                name = item.optString("name", "Page ${index + 1}"),
                buttons = normalizeDeckButtons(
                    List(buttons.length()) { buttonIndex ->
                        decodeDeckButton(buttons.getJSONObject(buttonIndex), buttonIndex)
                    }
                )
            )
        }.ifEmpty {
            listOf(DeckPageConfig(1, "Page 1", defaultButtons()))
        }
    }.getOrDefault(listOf(DeckPageConfig(1, "Page 1", defaultButtons())))
}

private fun saveDeckPages(context: Context, pages: List<DeckPageConfig>) {
    val array = JSONArray()
    pages.forEach { page ->
        val buttons = JSONArray()
        page.buttons.forEach { button ->
            buttons.put(encodeDeckButton(button))
        }
        array.put(
            JSONObject()
                .put("id", page.id)
                .put("name", page.name)
                .put("buttons", buttons)
        )
    }
    context.deckPrefs().edit().putString(PREF_PAGES, array.toString()).apply()
}

private fun encodeDeckButton(button: DeckButton): JSONObject {
    return JSONObject()
        .put("id", button.id)
        .put("title", button.title)
        .put("subtitle", button.subtitle)
        .put("icon", button.icon)
        .put("iconImageUri", button.iconImageUri)
        .put("displayMode", button.displayMode.name)
        .put("actionType", button.actionType.name)
        .put("payload", button.payload)
        .put("color", button.color.toArgb())
        .put("position", button.position)
}

private fun decodeDeckButton(item: JSONObject, fallbackPosition: Int): DeckButton {
    return DeckButton(
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
        color = Color(item.getInt("color")),
        position = item.optInt("position", fallbackPosition)
    )
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
        color = colors[4],
        position = nextOpenPosition(buttons, DEFAULT_COLUMNS * DEFAULT_ROWS - 1)
    )
    return listOf(settingsButton) + buttons
}

private fun payloadRequired(actionType: DeckActionType): Boolean {
    return when (actionType) {
        DeckActionType.Settings,
        DeckActionType.BluetoothStatus,
        DeckActionType.PreviousPage,
        DeckActionType.NextPage -> false
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

private fun loadDeckRows(context: Context): Int {
    return context.deckPrefs().getInt(PREF_ROWS, DEFAULT_ROWS).coerceIn(MIN_ROWS, MAX_ROWS)
}

private fun saveDeckRows(context: Context, rows: Int) {
    context.deckPrefs().edit().putInt(PREF_ROWS, rows.coerceIn(MIN_ROWS, MAX_ROWS)).apply()
}

private fun loadPageSwipeAxis(context: Context): PageSwipeAxis {
    return runCatching {
        PageSwipeAxis.valueOf(context.deckPrefs().getString(PREF_PAGE_SWIPE_AXIS, null) ?: PageSwipeAxis.Horizontal.name)
    }.getOrDefault(PageSwipeAxis.Horizontal)
}

private fun savePageSwipeAxis(context: Context, axis: PageSwipeAxis) {
    context.deckPrefs().edit().putString(PREF_PAGE_SWIPE_AXIS, axis.name).apply()
}

private fun loadMultiTouchPageSwipe(context: Context): Boolean {
    return context.deckPrefs().getBoolean(PREF_MULTI_TOUCH_PAGE_SWIPE, true)
}

private fun saveMultiTouchPageSwipe(context: Context, enabled: Boolean) {
    context.deckPrefs().edit().putBoolean(PREF_MULTI_TOUCH_PAGE_SWIPE, enabled).apply()
}

private fun nextDeckButtonId(buttons: List<DeckButton>): Int {
    return (buttons.maxOfOrNull { it.id } ?: 0) + 1
}

private fun nextDeckPageId(pages: List<DeckPageConfig>): Int {
    return (pages.maxOfOrNull { it.id } ?: 0) + 1
}

private fun nextOpenPosition(buttons: List<DeckButton>, capacity: Int): Int {
    val occupied = buttons.map { it.position }.toSet()
    return (0 until capacity).firstOrNull { it !in occupied } ?: capacity
}

private fun updateDeckPage(
    pages: List<DeckPageConfig>,
    pageId: Int,
    update: (DeckPageConfig) -> List<DeckButton>
): List<DeckPageConfig> {
    return pages.map { page ->
        if (page.id == pageId) page.copy(buttons = update(page)) else page
    }
}

private fun updateDeckButton(
    pages: List<DeckPageConfig>,
    button: DeckButton
): List<DeckPageConfig> {
    return pages.map { page ->
        page.copy(buttons = page.buttons.map { if (it.id == button.id) button else it })
    }
}

private fun moveDeckButton(
    buttons: List<DeckButton>,
    id: Int,
    delta: Int
): List<DeckButton> {
    val button = buttons.firstOrNull { it.id == id } ?: return buttons
    val targetPosition = (button.position + delta).coerceAtLeast(0)
    val targetButton = buttons.firstOrNull { it.position == targetPosition }
    return buttons.map { existing ->
        when (existing.id) {
            button.id -> existing.copy(position = targetPosition)
            targetButton?.id -> existing.copy(position = button.position)
            else -> existing
        }
    }
}

private fun Context.deckPrefs() = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

private const val PREFS_NAME = "mobile_deck"
private const val PREF_PAGES = "pages"
private const val PREF_BUTTONS = "buttons"
private const val PREF_COLUMNS = "columns"
private const val PREF_ROWS = "rows"
private const val PREF_PAGE_SWIPE_AXIS = "page_swipe_axis"
private const val PREF_MULTI_TOUCH_PAGE_SWIPE = "multi_touch_page_swipe"
private const val MAX_PAGES = 5
private const val MIN_COLUMNS = 4
private const val MAX_COLUMNS = 6
private const val DEFAULT_COLUMNS = 6
private const val MIN_ROWS = 2
private const val MAX_ROWS = 4
private const val DEFAULT_ROWS = 2

@Preview(showBackground = true)
@Composable
private fun MobileDeckPreview() {
    MobileDeckTheme {
        MobileDeckApp()
    }
}
