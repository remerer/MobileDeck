package com.example.mobiledeck

import android.os.Build
import android.os.Bundle
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntSize
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
        WindowCompat.setDecorFitsSystemWindows(window, false)
        hideSystemBars()
        setContent {
            MobileDeckTheme {
                MobileDeckApp()
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemBars()
        }
    }

    private fun hideSystemBars() {
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}

private enum class DeckActionType(@StringRes val labelRes: Int) {
    Settings(R.string.action_settings),
    BluetoothStatus(R.string.action_bluetooth_status),
    PreviousPage(R.string.action_previous_page),
    NextPage(R.string.action_next_page),
    MediaKey(R.string.action_media_key),
    Hotkey(R.string.action_hotkey),
    Text(R.string.action_text),
    RunCommand(R.string.action_run_command),
    AppCommand(R.string.action_app_command)
}

private enum class DeckDisplayMode(@StringRes val labelRes: Int) {
    IconOnly(R.string.display_icon_only),
    IconAndText(R.string.display_icon_and_text),
    KeywordOnly(R.string.display_keyword_only)
}

private enum class PageSwipeAxis(@StringRes val labelRes: Int, @StringRes val shortLabelRes: Int) {
    Horizontal(R.string.page_axis_horizontal, R.string.page_axis_horizontal_short),
    Vertical(R.string.page_axis_vertical, R.string.page_axis_vertical_short)
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

private data class IconChoice(
    val key: String,
    @StringRes val labelRes: Int
)

private data class MediaKeyChoice(
    val payload: String,
    @StringRes val labelRes: Int
)

private data class PageAnimationTarget(
    val pageId: Int,
    val delta: Int,
    val sequence: Int
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
    var pageSwipeAnimation by remember { mutableStateOf(loadPageSwipeAnimation(context)) }
    var infinitePageSwipe by remember { mutableStateOf(loadInfinitePageSwipe(context)) }
    var lastPageDelta by remember { mutableStateOf(1) }
    var pageAnimationSequence by remember { mutableStateOf(0) }
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
        val buttonCapacity = pageButtonCapacity(activeDeckPage.id, deckPages, deckColumns, deckRows)
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

    fun deleteActiveDeckPage() {
        if (deckPages.size <= 1 || activeDeckPage.id == deckPages.first().id) return
        val currentIndex = deckPages.indexOfFirst { it.id == activeDeckPage.id }.coerceAtLeast(0)
        val updatedPages = deckPages.filterNot { it.id == activeDeckPage.id }
        val nextIndex = currentIndex.coerceAtMost(updatedPages.lastIndex)
        deckPages = updatedPages
        activeDeckPageId = updatedPages[nextIndex].id
        saveDeckPages(context, updatedPages)
    }

    fun resetFirstDeckPage() {
        val firstPage = deckPages.firstOrNull() ?: return
        val settingsButtons = firstPage.buttons
            .filter { buttonAppAction(it) == DeckActionType.Settings }
            .ifEmpty { normalizeDeckButtons(emptyList()) }
            .mapIndexed { index, button -> button.copy(position = index) }
        val updatedPages = deckPages.map { page ->
            if (page.id == firstPage.id) page.copy(buttons = settingsButtons) else page
        }
        deckPages = updatedPages
        activeDeckPageId = firstPage.id
        saveDeckPages(context, updatedPages)
    }

    fun switchDeckPage(delta: Int) {
        if (deckPages.isEmpty()) return
        val currentIndex = deckPages.indexOfFirst { it.id == activeDeckPageId }.let { index ->
            if (index >= 0) index else 0
        }
        val target = if (infinitePageSwipe) {
            wrapIndex(currentIndex + delta, deckPages.size)
        } else {
            (currentIndex + delta).coerceIn(deckPages.indices)
        }
        Log.d(
            "MobileDeckGesture",
            "switchPage current=$currentIndex target=$target delta=$delta activeId=$activeDeckPageId targetId=${deckPages[target].id} pages=${deckPages.size}"
        )
        if (target != currentIndex) {
            lastPageDelta = delta
            pageAnimationSequence += 1
            activeDeckPageId = deckPages[target].id
        }
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
            DeckActionType.AppCommand -> when (appCommandAction(button.payload)) {
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
                else -> false
            }
        }
        val note = when {
            buttonAppAction(button) == DeckActionType.Settings -> "opened settings"
            buttonAppAction(button) == DeckActionType.BluetoothStatus -> "opened connection"
            buttonAppAction(button) == DeckActionType.PreviousPage -> "previous page"
            buttonAppAction(button) == DeckActionType.NextPage -> "next page"
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
        val pageCapacity = pageButtonCapacity(activeDeckPage.id, deckPages, deckColumns, deckRows)
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
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
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
                pageSwipeAnimation = pageSwipeAnimation,
                pageSwipeDelta = lastPageDelta,
                pageAnimationSequence = pageAnimationSequence,
                onPageSwipe = ::switchDeckPage,
                onAddPage = ::addDeckPage,
                onButtonPressed = ::pressDeckButton,
                onButtonEdit = {},
                onButtonMoved = ::moveDeckButtonToSlot,
                onEmptySlotPressed = { slot -> addDeckButton(slot, editAfterCreate = true) }
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
                pageSwipeAnimation = pageSwipeAnimation,
                pageSwipeDelta = lastPageDelta,
                pageAnimationSequence = pageAnimationSequence,
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
                onDeletePage = ::deleteActiveDeckPage,
                onResetPage = ::resetFirstDeckPage,
                onButtonEdit = { editingButton = it },
                onButtonMoved = ::moveDeckButtonToSlot,
                onEmptySlotPressed = { slot -> addDeckButton(slot, editAfterCreate = true) }
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
                pageSwipeAnimation = pageSwipeAnimation,
                infinitePageSwipe = infinitePageSwipe,
                onLayoutEditor = { page = AppPage.LayoutEditor },
                onPageSwipeAxisChange = { axis ->
                    pageSwipeAxis = axis
                    savePageSwipeAxis(context, axis)
                },
                onMultiTouchPageSwipeChange = { enabled ->
                    multiTouchPageSwipe = enabled
                    saveMultiTouchPageSwipe(context, enabled)
                },
                onPageSwipeAnimationChange = { enabled ->
                    pageSwipeAnimation = enabled
                    savePageSwipeAnimation(context, enabled)
                },
                onInfinitePageSwipeChange = { enabled ->
                    infinitePageSwipe = enabled
                    saveInfinitePageSwipe(context, enabled)
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
                onAddPage = ::addDeckPage,
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
    pageSwipeAnimation: Boolean,
    infinitePageSwipe: Boolean,
    pageName: String,
    pageCount: Int,
    pairedHosts: List<PairedHidHost>,
    onBack: () -> Unit,
    onLayoutEditor: () -> Unit,
    onPageSwipeAxisChange: (PageSwipeAxis) -> Unit,
    onMultiTouchPageSwipeChange: (Boolean) -> Unit,
    onPageSwipeAnimationChange: (Boolean) -> Unit,
    onInfinitePageSwipeChange: (Boolean) -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onMakeDiscoverable: () -> Unit,
    onRefreshHosts: () -> Unit,
    onConnectHost: (PairedHidHost) -> Unit,
    onColumnsChange: (Int) -> Unit,
    onRowsChange: (Int) -> Unit,
    onAddButton: () -> Unit,
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
                        text = stringResource(R.string.settings_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.settings_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OutlinedButton(
                    shape = RoundedCornerShape(8.dp),
                    onClick = onBack
                ) {
                    Text(stringResource(R.string.deck))
                }
                OutlinedButton(
                    shape = RoundedCornerShape(8.dp),
                    onClick = onLayoutEditor
                ) {
                    Text(stringResource(R.string.layout_editor))
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
                pageSwipeAnimation = pageSwipeAnimation,
                infinitePageSwipe = infinitePageSwipe,
                pageName = pageName,
                pageCount = pageCount,
                onPageSwipeAxisChange = onPageSwipeAxisChange,
                onMultiTouchPageSwipeChange = onMultiTouchPageSwipeChange,
                onPageSwipeAnimationChange = onPageSwipeAnimationChange,
                onInfinitePageSwipeChange = onInfinitePageSwipeChange,
                onAddPage = onAddPage,
            )
        }

        if (BuildConfig.DEBUG) {
            item {
                DiagnosticsPanel(logs = logs)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeckSettingsPanel(
    deckPages: List<DeckPageConfig>,
    activePageId: Int,
    pageSwipeAxis: PageSwipeAxis,
    multiTouchPageSwipe: Boolean,
    pageSwipeAnimation: Boolean,
    infinitePageSwipe: Boolean,
    pageName: String,
    pageCount: Int,
    onPageSwipeAxisChange: (PageSwipeAxis) -> Unit,
    onMultiTouchPageSwipeChange: (Boolean) -> Unit,
    onPageSwipeAnimationChange: (Boolean) -> Unit,
    onInfinitePageSwipeChange: (Boolean) -> Unit,
    onAddPage: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(R.string.deck_layout),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(R.string.page_count_summary, pageName, pageCount),
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
                        Text(stringResource(if (pageSwipeAxis == axis) axis.labelRes else axis.shortLabelRes))
                    }
                }
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                onClick = { onMultiTouchPageSwipeChange(!multiTouchPageSwipe) }
            ) {
                Text(stringResource(if (multiTouchPageSwipe) R.string.multi_touch_swipe_on else R.string.multi_touch_swipe_off))
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                onClick = { onInfinitePageSwipeChange(!infinitePageSwipe) }
            ) {
                Text(stringResource(if (infinitePageSwipe) R.string.page_wrap_on else R.string.page_wrap_off))
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                onClick = { onPageSwipeAnimationChange(!pageSwipeAnimation) }
            ) {
                Text(stringResource(if (pageSwipeAnimation) R.string.page_swipe_animation_on else R.string.page_swipe_animation_off))
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                enabled = pageCount < MAX_PAGES,
                onClick = onAddPage
            ) {
                Text(stringResource(R.string.add_page_count, pageCount, MAX_PAGES))
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
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.app_tagline),
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
                        Text(stringResource(R.string.register_hid))
                    }
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        onClick = onStop
                    ) {
                        Text(stringResource(R.string.stop))
                    }
                }

                OutlinedButton(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    onClick = onMakeDiscoverable
                ) {
                    Text(stringResource(R.string.make_discoverable))
                }

                Text(
                    text = localizedStatusMessage(status.message),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = stringResource(R.string.pairing_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.paired_hosts),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(onClick = onRefreshHosts) {
                        Text(stringResource(R.string.refresh))
                    }
                }

                if (pairedHosts.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_paired_hosts),
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
                                    text = stringResource(R.string.connect_host, host.name),
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
            text = stringResource(state.labelRes()),
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
    pageSwipeAnimation: Boolean,
    pageSwipeDelta: Int,
    pageAnimationSequence: Int,
    onBack: () -> Unit,
    onColumnsChange: (Int) -> Unit,
    onRowsChange: (Int) -> Unit,
    onPageSwipe: (Int) -> Unit,
    onAddPage: () -> Unit,
    onDeletePage: () -> Unit,
    onResetPage: () -> Unit,
    onButtonEdit: (DeckButton) -> Unit,
    onButtonMoved: (DeckButton, Int) -> Unit,
    onEmptySlotPressed: (Int) -> Unit
) {
    var confirmPageAction by remember { mutableStateOf(false) }
    val isFirstPage = activePageId == deckPages.firstOrNull()?.id

    if (confirmPageAction) {
        AlertDialog(
            onDismissRequest = { confirmPageAction = false },
            title = { Text(stringResource(if (isFirstPage) R.string.reset_first_page else R.string.delete_current_page)) },
            text = { Text(stringResource(if (isFirstPage) R.string.confirm_reset_first_page else R.string.confirm_delete_page)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (isFirstPage) onResetPage() else onDeletePage()
                        confirmPageAction = false
                    }
                ) {
                    Text(stringResource(if (isFirstPage) R.string.reset else R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmPageAction = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

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
                Text(stringResource(R.string.settings_title))
            }
            LayoutSlider(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.columns),
                value = columns,
                range = MIN_COLUMNS..MAX_COLUMNS,
                onValueChange = onColumnsChange
            )
            OutlinedButton(
                shape = RoundedCornerShape(8.dp),
                enabled = isFirstPage || deckPages.size > 1,
                onClick = { confirmPageAction = true }
            ) {
                Text(stringResource(if (isFirstPage) R.string.reset_first_page else R.string.delete_current_page))
            }
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
                pageSwipeAnimation = pageSwipeAnimation,
                pageSwipeDelta = pageSwipeDelta,
                pageAnimationSequence = pageAnimationSequence,
                onPageSwipe = onPageSwipe,
                onAddPage = onAddPage,
                onButtonPressed = onButtonEdit,
                onButtonEdit = onButtonEdit,
                onButtonMoved = onButtonMoved,
                onEmptySlotPressed = onEmptySlotPressed
            )
            VerticalLayoutSlider(
                modifier = Modifier
                    .width(34.dp)
                    .fillMaxHeight(),
                label = stringResource(R.string.rows),
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
        CompactSlider(
            modifier = Modifier.weight(1f),
            value = value.toFloat(),
            onValueChange = { next ->
                onValueChange(next.roundToInt().coerceIn(range.first, range.last))
            },
            valueRange = range.first.toFloat()..range.last.toFloat()
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
        CompactSlider(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            value = value.toFloat(),
            onValueChange = { next ->
                onValueChange(next.roundToInt().coerceIn(range.first, range.last))
            },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            vertical = true
        )
    }
}

@Composable
private fun CompactSlider(
    modifier: Modifier = Modifier,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    vertical: Boolean = false
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.28f)
    var size by remember { mutableStateOf(IntSize.Zero) }
    val fraction = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start))
        .coerceIn(0f, 1f)

    fun updateFromPosition(position: Offset) {
        val nextFraction = if (vertical) {
            1f - (position.y / size.height.coerceAtLeast(1)).coerceIn(0f, 1f)
        } else {
            (position.x / size.width.coerceAtLeast(1)).coerceIn(0f, 1f)
        }
        val nextValue = valueRange.start + nextFraction * (valueRange.endInclusive - valueRange.start)
        onValueChange(nextValue)
    }

    Canvas(
        modifier = modifier
            .onSizeChanged { size = it }
            .pointerInput(valueRange, vertical, size) {
                detectTapGestures(
                    onPress = { offset ->
                        updateFromPosition(offset)
                        tryAwaitRelease()
                    }
                )
            }
            .pointerInput(valueRange, vertical, size) {
                detectDragGestures { change, _ ->
                    change.consume()
                    updateFromPosition(change.position)
                }
            }
    ) {
        val trackStroke = 4.dp.toPx()
        val thumbRadius = 7.dp.toPx()
        if (vertical) {
            val x = this.size.width / 2f
            val top = thumbRadius
            val bottom = this.size.height - thumbRadius
            val y = bottom - (bottom - top) * fraction
            drawLine(inactiveColor, Offset(x, top), Offset(x, bottom), trackStroke, StrokeCap.Round)
            drawLine(activeColor, Offset(x, y), Offset(x, bottom), trackStroke, StrokeCap.Round)
            drawCircle(activeColor, thumbRadius, Offset(x, y))
        } else {
            val y = this.size.height / 2f
            val start = thumbRadius
            val end = this.size.width - thumbRadius
            val x = start + (end - start) * fraction
            drawLine(inactiveColor, Offset(start, y), Offset(end, y), trackStroke, StrokeCap.Round)
            drawLine(activeColor, Offset(start, y), Offset(x, y), trackStroke, StrokeCap.Round)
            drawCircle(activeColor, thumbRadius, Offset(x, y))
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
    pageSwipeAnimation: Boolean,
    pageSwipeDelta: Int,
    pageAnimationSequence: Int,
    onPageSwipe: (Int) -> Unit,
    onAddPage: () -> Unit,
    onButtonPressed: (DeckButton) -> Unit,
    onButtonEdit: (DeckButton) -> Unit,
    onButtonMoved: (DeckButton, Int) -> Unit,
    onEmptySlotPressed: (Int) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val safeColumns = columns.coerceIn(MIN_COLUMNS, MAX_COLUMNS)
        val safeRows = rows.coerceIn(MIN_ROWS, MAX_ROWS)
        val density = LocalDensity.current
        val spacing = 8.dp
        val indicatorPadding = PaddingValues(
            start = if (pageSwipeAxis == PageSwipeAxis.Vertical) 12.dp else 0.dp,
            bottom = if (pageSwipeAxis == PageSwipeAxis.Horizontal) 12.dp else 0.dp
        )
        val swipeModifier = Modifier.multiTouchPageSwipe(
            enabled = multiTouchPageSwipe,
            axis = pageSwipeAxis,
            onPageSwipe = onPageSwipe
        )
        val cellSize = with(density) {
            val spacingPx = spacing.toPx()
            val reservedWidth = if (pageSwipeAxis == PageSwipeAxis.Vertical) 12.dp.toPx() else 0f
            val reservedHeight = if (pageSwipeAxis == PageSwipeAxis.Horizontal) 12.dp.toPx() else 0f
            val maxCellWidth = (constraints.maxWidth - reservedWidth - spacingPx * (safeColumns - 1)) / safeColumns
            val maxCellHeight = (constraints.maxHeight - reservedHeight - spacingPx * (safeRows - 1)) / safeRows
            minOf(maxCellWidth, maxCellHeight).toDp()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(swipeModifier)
        ) {
            AnimatedContent(
                targetState = PageAnimationTarget(activePageId, pageSwipeDelta, pageAnimationSequence),
                modifier = Modifier.fillMaxSize(),
                transitionSpec = {
                    if (!pageSwipeAnimation) {
                        fadeIn(tween(0)) togetherWith fadeOut(tween(0))
                    } else if (pageSwipeAxis == PageSwipeAxis.Horizontal) {
                        slideInHorizontally { width -> targetState.delta.signOrOne() * width } togetherWith
                            slideOutHorizontally { width -> -targetState.delta.signOrOne() * width }
                    } else {
                        slideInVertically { height -> targetState.delta.signOrOne() * height } togetherWith
                            slideOutVertically { height -> -targetState.delta.signOrOne() * height }
                    }
                },
                label = "pageSwipe"
            ) { target ->
                val targetButtons = deckPages.firstOrNull { it.id == target.pageId }?.buttons.orEmpty()
                ButtonGrid(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = indicatorPadding,
                    buttons = targetButtons,
                    columns = safeColumns,
                    rows = safeRows,
                    cellSize = cellSize,
                    spacing = spacing,
                    status = status,
                    previewMode = previewMode,
                    showTitle = target.pageId == deckPages.firstOrNull()?.id,
                    onButtonPressed = onButtonPressed,
                    onButtonEdit = onButtonEdit,
                    onButtonMoved = onButtonMoved,
                    onEmptySlotPressed = onEmptySlotPressed
                )
            }
            PageIndicator(
                modifier = Modifier
                    .align(
                        if (pageSwipeAxis == PageSwipeAxis.Horizontal) {
                            Alignment.BottomCenter
                        } else {
                            Alignment.CenterStart
                        }
                    )
                    .padding(4.dp),
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
            var multiTouchActive = false

            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                val pressed = event.changes.filter { it.pressed }
                if (pressed.isNotEmpty()) {
                    maxPointerCount = maxOf(maxPointerCount, pressed.size)
                    if (pressed.size >= 2 || multiTouchActive) {
                        multiTouchActive = true
                        event.changes.forEach { if (it.pressed) it.consume() }
                    }
                    val centroid = pressed
                        .map { it.position }
                        .reduce { acc, offset -> acc + offset } / pressed.size.toFloat()
                    if (!tracking) {
                        tracking = true
                        previousCentroid = centroid
                        totalDrag = Offset.Zero
                    } else {
                        previousCentroid?.let { totalDrag += centroid - it }
                        previousCentroid = centroid
                    }
                } else if (tracking) {
                    if (multiTouchActive && maxPointerCount in 2..3) {
                        event.changes.forEach { it.consume() }
                        val threshold = 80f
                        var pageDelta = 0
                        when (axis) {
                            PageSwipeAxis.Horizontal -> {
                                if (abs(totalDrag.x) > threshold && abs(totalDrag.x) > abs(totalDrag.y)) {
                                    pageDelta = if (totalDrag.x < 0f) 1 else -1
                                }
                            }
                            PageSwipeAxis.Vertical -> {
                                if (abs(totalDrag.y) > threshold && abs(totalDrag.y) > abs(totalDrag.x)) {
                                    pageDelta = if (totalDrag.y < 0f) 1 else -1
                                }
                            }
                        }
                        Log.d(
                            "MobileDeckGesture",
                            "pageSwipe axis=$axis pointers=$maxPointerCount drag=${totalDrag.x},${totalDrag.y} delta=$pageDelta"
                        )
                        if (pageDelta != 0) onPageSwipe(pageDelta)
                    }
                    tracking = false
                    previousCentroid = null
                    totalDrag = Offset.Zero
                    maxPointerCount = 0
                    multiTouchActive = false
                }
            }
        }
    }
}

private fun Int.signOrOne(): Int = if (this < 0) -1 else 1

private fun wrapIndex(value: Int, size: Int): Int = ((value % size) + size) % size

private fun slotToButtonPosition(slot: Int, showTitle: Boolean): Int = if (showTitle) slot - 1 else slot

@Composable
private fun ButtonGrid(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    buttons: List<DeckButton>,
    columns: Int,
    rows: Int,
    cellSize: Dp,
    spacing: Dp,
    status: HidStatus,
    previewMode: Boolean,
    showTitle: Boolean,
    onButtonPressed: (DeckButton) -> Unit,
    onButtonEdit: (DeckButton) -> Unit,
    onButtonMoved: (DeckButton, Int) -> Unit,
    onEmptySlotPressed: (Int) -> Unit
) {
    val safeColumns = columns.coerceAtLeast(1)
    val slotCount = safeColumns * rows.coerceAtLeast(1)
    val slots = List(slotCount) { slot ->
        if (showTitle && slot == 0) null else buttons.firstOrNull { it.position == slotToButtonPosition(slot, showTitle) }
    }
    Box(
        modifier = modifier.padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(spacing)) {
            slots.chunked(safeColumns).forEachIndexed { rowIndex, rowButtons ->
                Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
                    rowButtons.forEachIndexed { columnIndex, button ->
                        val slot = rowIndex * safeColumns + columnIndex
                        if (showTitle && slot == 0) {
                            TitleDeckSlot(
                                modifier = Modifier.size(cellSize),
                                status = status
                            )
                        } else if (button == null) {
                        EmptyDeckSlot(
                            modifier = Modifier.size(cellSize),
                            showAddIcon = previewMode,
                            createOnClick = previewMode,
                            onCreate = { onEmptySlotPressed(slotToButtonPosition(slot, showTitle)) }
                        )
                        } else {
                            val buttonPosition = slotToButtonPosition(slot, showTitle)
                            val maxButtonPosition = slotCount - if (showTitle) 2 else 1
                            DeckKey(
                                modifier = Modifier.size(cellSize),
                                button = button,
                                status = status,
                                enabled = true,
                                previewMode = previewMode,
                                columns = safeColumns,
                                slot = buttonPosition,
                                cellSize = cellSize,
                                spacing = spacing,
                                onPressed = { onButtonPressed(button) },
                                onEdit = { onButtonEdit(button) },
                                onMove = { targetSlot -> onButtonMoved(button, targetSlot.coerceIn(0, maxButtonPosition)) }
                            )
                        }
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
                text = stringResource(R.string.deck_title),
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
    showAddIcon: Boolean,
    createOnClick: Boolean,
    onCreate: () -> Unit
) {
    Surface(
        modifier = modifier.combinedClickable(
            onClick = { if (createOnClick) onCreate() },
            onLongClick = { if (!createOnClick) onCreate() }
        ),
        shape = RoundedCornerShape(8.dp),
        tonalElevation = 0.dp,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.36f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (showAddIcon) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
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
            verticalArrangement = if (button.displayMode != DeckDisplayMode.IconAndText) {
                Arrangement.Center
            } else {
                Arrangement.SpaceBetween
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (button.displayMode != DeckDisplayMode.KeywordOnly) {
                    DeckButtonIcon(
                        button = button,
                        tint = contentColor,
                        large = button.displayMode == DeckDisplayMode.IconOnly
                    )
                }
                if (button.displayMode == DeckDisplayMode.IconAndText) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        if (buttonAppAction(button) == DeckActionType.BluetoothStatus) {
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
                } else if (button.displayMode == DeckDisplayMode.KeywordOnly) {
                    Text(
                        text = button.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = buttonSubtitle(button, status),
                        style = MaterialTheme.typography.labelMedium,
                        color = contentColor.copy(alpha = 0.82f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
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
                    if (buttonAppAction(button) != DeckActionType.BluetoothStatus) {
                        Text(
                            text = stringResource(button.actionType.labelRes),
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
}

@Composable
private fun buttonSubtitle(button: DeckButton, status: HidStatus): String {
    return if (buttonAppAction(button) == DeckActionType.BluetoothStatus) {
        stringResource(status.state.labelRes())
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
    iconVectorForKey(button.icon)?.let { return it }
    return when (buttonAppAction(button) ?: button.actionType) {
        DeckActionType.Settings -> Icons.Filled.Settings
        DeckActionType.BluetoothStatus -> Icons.Filled.Bluetooth
        DeckActionType.PreviousPage -> Icons.Filled.SkipPrevious
        DeckActionType.NextPage -> Icons.Filled.SkipNext
        DeckActionType.MediaKey -> mediaIconForPayload(button.payload)
        DeckActionType.Hotkey -> when (button.icon.uppercase()) {
            "REC" -> Icons.Filled.Videocam
            else -> Icons.Filled.Keyboard
        }
        DeckActionType.Text -> Icons.Filled.TextFields
        DeckActionType.RunCommand -> Icons.Filled.Apps
        DeckActionType.AppCommand -> Icons.Filled.Code
    }
}

private fun mediaIconForPayload(payload: String): ImageVector? {
    return when (payload.uppercase()) {
        MEDIA_MUTE -> Icons.Filled.VolumeOff
        MEDIA_VOLUME_UP, "VOLUMEUP" -> Icons.Filled.VolumeUp
        MEDIA_VOLUME_DOWN, "VOLUMEDOWN" -> Icons.Filled.VolumeDown
        MEDIA_PLAY_PAUSE, "PLAYPAUSE", "PLAY", "PAUSE" -> Icons.Filled.PlayArrow
        MEDIA_STOP -> Icons.Filled.Stop
        MEDIA_NEXT, "NEXT_TRACK" -> Icons.Filled.SkipNext
        MEDIA_PREVIOUS, "PREV", "PREVIOUS_TRACK" -> Icons.Filled.SkipPrevious
        else -> null
    }
}

private fun iconVectorForKey(key: String): ImageVector? {
    return when (key) {
        ICON_SETTINGS -> Icons.Filled.Settings
        ICON_BLUETOOTH -> Icons.Filled.Bluetooth
        ICON_KEYBOARD -> Icons.Filled.Keyboard
        ICON_APPS -> Icons.Filled.Apps
        ICON_CODE -> Icons.Filled.Code
        ICON_TEXT -> Icons.Filled.TextFields
        ICON_PLAY -> Icons.Filled.PlayArrow
        ICON_STOP -> Icons.Filled.Stop
        ICON_PREVIOUS -> Icons.Filled.SkipPrevious
        ICON_NEXT -> Icons.Filled.SkipNext
        ICON_VOLUME_OFF -> Icons.Filled.VolumeOff
        ICON_VOLUME_DOWN -> Icons.Filled.VolumeDown
        ICON_VOLUME_UP -> Icons.Filled.VolumeUp
        else -> null
    }
}

private fun iconChoices(): List<IconChoice> {
    return listOf(
        IconChoice(ICON_AUTO, R.string.icon_auto),
        IconChoice(ICON_SETTINGS, R.string.icon_settings),
        IconChoice(ICON_BLUETOOTH, R.string.icon_bluetooth),
        IconChoice(ICON_KEYBOARD, R.string.icon_keyboard),
        IconChoice(ICON_APPS, R.string.icon_apps),
        IconChoice(ICON_CODE, R.string.icon_code),
        IconChoice(ICON_TEXT, R.string.icon_text_fields),
        IconChoice(ICON_PLAY, R.string.icon_play),
        IconChoice(ICON_STOP, R.string.icon_stop),
        IconChoice(ICON_PREVIOUS, R.string.icon_previous),
        IconChoice(ICON_NEXT, R.string.icon_next),
        IconChoice(ICON_VOLUME_OFF, R.string.icon_volume_off),
        IconChoice(ICON_VOLUME_DOWN, R.string.icon_volume_down),
        IconChoice(ICON_VOLUME_UP, R.string.icon_volume_up)
    )
}

private fun selectedIconChoice(key: String): IconChoice {
    return iconChoices().firstOrNull { it.key == key } ?: iconChoices().first()
}

private fun mediaKeyChoices(): List<MediaKeyChoice> {
    return listOf(
        MediaKeyChoice(MEDIA_MUTE, R.string.media_mute),
        MediaKeyChoice(MEDIA_PLAY_PAUSE, R.string.media_play_pause),
        MediaKeyChoice(MEDIA_STOP, R.string.media_stop),
        MediaKeyChoice(MEDIA_PREVIOUS, R.string.media_previous),
        MediaKeyChoice(MEDIA_NEXT, R.string.media_next),
        MediaKeyChoice(MEDIA_VOLUME_DOWN, R.string.media_volume_down),
        MediaKeyChoice(MEDIA_VOLUME_UP, R.string.media_volume_up)
    )
}

private fun mediaKeyChoice(payload: String): MediaKeyChoice? {
    val normalizedPayload = payload.uppercase()
    val canonicalPayload = when (normalizedPayload) {
        "VOLUMEUP" -> MEDIA_VOLUME_UP
        "VOLUMEDOWN" -> MEDIA_VOLUME_DOWN
        "PLAYPAUSE", "PLAY", "PAUSE" -> MEDIA_PLAY_PAUSE
        "NEXT_TRACK" -> MEDIA_NEXT
        "PREV", "PREVIOUS_TRACK" -> MEDIA_PREVIOUS
        else -> normalizedPayload
    }
    return mediaKeyChoices().firstOrNull { it.payload == canonicalPayload }
}

private fun selectedMediaKeyChoice(payload: String): MediaKeyChoice {
    return mediaKeyChoice(payload) ?: mediaKeyChoices().first()
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

@StringRes
private fun HidConnectionState.labelRes(): Int {
    return when (this) {
        HidConnectionState.Disconnected -> R.string.status_disconnected
        HidConnectionState.Registering -> R.string.status_registering
        HidConnectionState.Registered -> R.string.status_registered
        HidConnectionState.Connected -> R.string.status_connected
        HidConnectionState.Unsupported -> R.string.status_unsupported
        HidConnectionState.PermissionMissing -> R.string.status_permission_needed
        HidConnectionState.Error -> R.string.status_error
    }
}

@Composable
private fun localizedStatusMessage(message: String): String {
    return when (message) {
        "Bluetooth permissions were denied" -> stringResource(R.string.status_message_permissions_denied)
        "Discoverable request finished. Pair from the PC while HID is registered." -> stringResource(R.string.status_message_discoverable_finished)
        else -> message
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
                text = stringResource(R.string.diagnostics),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (logs.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_actions_yet),
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
    onDelete: () -> Unit
) {
    var title by remember(button.id) { mutableStateOf(button.title) }
    var subtitle by remember(button.id) { mutableStateOf(button.subtitle) }
    var icon by remember(button.id) { mutableStateOf(button.icon) }
    var iconImageUri by remember(button.id) { mutableStateOf(button.iconImageUri) }
    var displayMode by remember(button.id) { mutableStateOf(button.displayMode) }
    var payload by remember(button.id) { mutableStateOf(button.payload) }
    var actionType by remember(button.id) { mutableStateOf(button.actionType) }
    var menuExpanded by remember { mutableStateOf(false) }
    var iconMenuExpanded by remember { mutableStateOf(false) }
    var mediaMenuExpanded by remember { mutableStateOf(false) }
    var appCommandMenuExpanded by remember { mutableStateOf(false) }
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
    val appCommandActions = listOf(
        DeckActionType.BluetoothStatus,
        DeckActionType.PreviousPage,
        DeckActionType.NextPage,
        DeckActionType.Settings
    )
    val selectedAppCommand = appCommandAction(payload) ?: DeckActionType.BluetoothStatus
    val selectedIcon = selectedIconChoice(icon)
    val selectedMedia = selectedMediaKeyChoice(payload)
    val canDelete = buttonAppAction(button) != DeckActionType.Settings
    val actionLocked = buttonAppAction(button) == DeckActionType.Settings
    val canSave = title.isNotBlank() && (!payloadRequired(actionType) || payload.isNotBlank())

    AlertDialog(
        modifier = Modifier.fillMaxWidth(0.86f),
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.edit_key)) },
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
                        label = { Text(stringResource(R.string.title)) },
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = subtitle,
                        onValueChange = { subtitle = it },
                        label = { Text(stringResource(R.string.subtitle)) },
                        singleLine = true
                    )
                }
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ExposedDropdownMenuBox(
                            expanded = iconMenuExpanded,
                            onExpandedChange = { iconMenuExpanded = !iconMenuExpanded }
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                value = stringResource(selectedIcon.labelRes),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.icon)) },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = iconMenuExpanded)
                                }
                            )
                            ExposedDropdownMenu(
                                expanded = iconMenuExpanded,
                                onDismissRequest = { iconMenuExpanded = false }
                            ) {
                                iconChoices().forEach { item ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                iconVectorForKey(item.key)?.let { vector ->
                                                    Icon(
                                                        imageVector = vector,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(22.dp)
                                                    )
                                                } ?: Box(modifier = Modifier.size(22.dp))
                                                Text(stringResource(item.labelRes))
                                            }
                                        },
                                        onClick = {
                                            icon = item.key
                                            iconMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            DeckDisplayMode.values().forEach { mode ->
                                OutlinedButton(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    onClick = { displayMode = mode }
                                ) {
                                    Text(stringResource(mode.labelRes))
                                }
                            }
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
                            Text(stringResource(if (iconImageUri.isBlank()) R.string.pick_image else R.string.change_image))
                        }
                        OutlinedButton(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            enabled = iconImageUri.isNotBlank(),
                            onClick = { iconImageUri = "" }
                        ) {
                            Text(stringResource(R.string.clear_image))
                        }
                    }
                }
                item {
                    if (actionLocked) {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = stringResource(actionType.labelRes),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(stringResource(R.string.action)) },
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
                                value = stringResource(actionType.labelRes),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.action)) },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuExpanded)
                                }
                            )
                            ExposedDropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                DeckActionType.values()
                                    .filterNot { it in appCommandActions }
                                    .forEach { item ->
                                        DropdownMenuItem(
                                            text = { Text(stringResource(item.labelRes)) },
                                            onClick = {
                                                actionType = item
                                                if (item == DeckActionType.MediaKey && mediaKeyChoice(payload) == null) {
                                                    payload = MEDIA_MUTE
                                                    icon = ICON_AUTO
                                                } else if (item == DeckActionType.AppCommand && appCommandAction(payload) == null) {
                                                    payload = DeckActionType.BluetoothStatus.name
                                                } else if (!payloadRequired(item)) {
                                                    payload = ""
                                                }
                                                menuExpanded = false
                                            }
                                        )
                                    }
                            }
                        }
                    }
                }
                if (actionType == DeckActionType.MediaKey) {
                    item {
                        ExposedDropdownMenuBox(
                            expanded = mediaMenuExpanded,
                            onExpandedChange = { mediaMenuExpanded = !mediaMenuExpanded }
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                value = stringResource(selectedMedia.labelRes),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.media_key_target)) },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = mediaMenuExpanded)
                                }
                            )
                            ExposedDropdownMenu(
                                expanded = mediaMenuExpanded,
                                onDismissRequest = { mediaMenuExpanded = false }
                            ) {
                                mediaKeyChoices().forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(stringResource(item.labelRes)) },
                                        onClick = {
                                            payload = item.payload
                                            icon = ICON_AUTO
                                            mediaMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                if (actionType == DeckActionType.AppCommand) {
                    item {
                        ExposedDropdownMenuBox(
                            expanded = appCommandMenuExpanded,
                            onExpandedChange = { appCommandMenuExpanded = !appCommandMenuExpanded }
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                value = stringResource(selectedAppCommand.labelRes),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(R.string.app_command_target)) },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = appCommandMenuExpanded)
                                }
                            )
                            ExposedDropdownMenu(
                                expanded = appCommandMenuExpanded,
                                onDismissRequest = { appCommandMenuExpanded = false }
                            ) {
                                appCommandActions.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(stringResource(item.labelRes)) },
                                        onClick = {
                                            payload = item.name
                                            appCommandMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                if (buttonAppAction(actionType, payload) == DeckActionType.BluetoothStatus) {
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
                                text = stringResource(status.state.labelRes()),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                item {
                    if (actionType != DeckActionType.AppCommand && actionType != DeckActionType.MediaKey) {
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = payload,
                            onValueChange = { payload = it },
                            label = { Text(stringResource(R.string.payload)) },
                            enabled = payloadRequired(actionType),
                            singleLine = true
                        )
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
                            payload = if (actionType == DeckActionType.AppCommand) {
                                selectedAppCommand.name
                            } else if (actionType == DeckActionType.MediaKey) {
                                selectedMedia.payload
                            } else if (payloadRequired(actionType)) {
                                payload.trim()
                            } else {
                                ""
                            }
                        )
                    )
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    enabled = canDelete,
                    onClick = onDelete
                ) {
                    Text(stringResource(R.string.delete))
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
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
        DeckButton(5, "Vol -", "Media", "-", "", DeckDisplayMode.IconOnly, DeckActionType.MediaKey, "VOLUME_DOWN", colors[0]),
        DeckButton(6, "Vol +", "Media", "+", "", DeckDisplayMode.IconOnly, DeckActionType.MediaKey, "VOLUME_UP", colors[2]),
        DeckButton(7, "Previous", "Page", "<<", "", DeckDisplayMode.IconOnly, DeckActionType.PreviousPage, "", colors[3]),
        DeckButton(8, "Next", "Page", ">>", "", DeckDisplayMode.IconOnly, DeckActionType.NextPage, "", colors[4]),
        DeckButton(9, "Desktop", "Win+D", "DES", "", DeckDisplayMode.IconAndText, DeckActionType.Hotkey, "WIN+D", colors[4]),
        DeckButton(10, "Explorer", "Win+E", "EXP", "", DeckDisplayMode.IconAndText, DeckActionType.Hotkey, "WIN+E", colors[2]),
        DeckButton(11, "Task View", "Win+Tab", "TAB", "", DeckDisplayMode.IconAndText, DeckActionType.Hotkey, "WIN+TAB", colors[1]),
        DeckButton(12, "Run", "Win+R", "RUN", "", DeckDisplayMode.IconAndText, DeckActionType.Hotkey, "WIN+R", colors[3])
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
        ?: return defaultDeckPages(loadDeckButtons(context))
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
            defaultDeckPages()
        }
    }.getOrDefault(defaultDeckPages())
}

private fun defaultDeckPages(firstPageButtons: List<DeckButton> = defaultButtons()): List<DeckPageConfig> {
    return listOf(
        DeckPageConfig(1, "Page 1", firstPageButtons),
        DeckPageConfig(2, "Page 2", emptyList())
    )
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
        DeckActionType.Hotkey,
        DeckActionType.Text,
        DeckActionType.RunCommand -> true
        DeckActionType.MediaKey,
        DeckActionType.AppCommand -> false
    }
}

private fun appCommandAction(payload: String): DeckActionType? {
    return runCatching {
        DeckActionType.valueOf(payload)
    }.getOrNull()?.takeIf {
        it == DeckActionType.Settings ||
            it == DeckActionType.BluetoothStatus ||
            it == DeckActionType.PreviousPage ||
            it == DeckActionType.NextPage
    }
}

private fun buttonAppAction(button: DeckButton): DeckActionType? {
    return buttonAppAction(button.actionType, button.payload)
}

private fun buttonAppAction(actionType: DeckActionType, payload: String): DeckActionType? {
    return when (actionType) {
        DeckActionType.Settings,
        DeckActionType.BluetoothStatus,
        DeckActionType.PreviousPage,
        DeckActionType.NextPage -> actionType
        DeckActionType.AppCommand -> appCommandAction(payload)
        else -> null
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

private fun loadPageSwipeAnimation(context: Context): Boolean {
    return context.deckPrefs().getBoolean(PREF_PAGE_SWIPE_ANIMATION, true)
}

private fun savePageSwipeAnimation(context: Context, enabled: Boolean) {
    context.deckPrefs().edit().putBoolean(PREF_PAGE_SWIPE_ANIMATION, enabled).apply()
}

private fun loadInfinitePageSwipe(context: Context): Boolean {
    return context.deckPrefs().getBoolean(PREF_INFINITE_PAGE_SWIPE, true)
}

private fun saveInfinitePageSwipe(context: Context, enabled: Boolean) {
    context.deckPrefs().edit().putBoolean(PREF_INFINITE_PAGE_SWIPE, enabled).apply()
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

private fun pageButtonCapacity(pageId: Int, pages: List<DeckPageConfig>, columns: Int, rows: Int): Int {
    val slotCount = columns * rows
    return if (pageId == pages.firstOrNull()?.id) slotCount - 1 else slotCount
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

private fun Context.deckPrefs() = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

private const val PREFS_NAME = "mobile_deck"
private const val PREF_PAGES = "pages"
private const val PREF_BUTTONS = "buttons"
private const val PREF_COLUMNS = "columns"
private const val PREF_ROWS = "rows"
private const val PREF_PAGE_SWIPE_AXIS = "page_swipe_axis"
private const val PREF_MULTI_TOUCH_PAGE_SWIPE = "multi_touch_page_swipe"
private const val PREF_PAGE_SWIPE_ANIMATION = "page_swipe_animation"
private const val PREF_INFINITE_PAGE_SWIPE = "infinite_page_swipe"
private const val MAX_PAGES = 5
private const val MIN_COLUMNS = 4
private const val MAX_COLUMNS = 9
private const val DEFAULT_COLUMNS = 6
private const val MIN_ROWS = 2
private const val MAX_ROWS = 6
private const val DEFAULT_ROWS = 3
private const val ICON_AUTO = "AUTO"
private const val ICON_SETTINGS = "ICON_SETTINGS"
private const val ICON_BLUETOOTH = "ICON_BLUETOOTH"
private const val ICON_KEYBOARD = "ICON_KEYBOARD"
private const val ICON_APPS = "ICON_APPS"
private const val ICON_CODE = "ICON_CODE"
private const val ICON_TEXT = "ICON_TEXT"
private const val ICON_PLAY = "ICON_PLAY"
private const val ICON_STOP = "ICON_STOP"
private const val ICON_PREVIOUS = "ICON_PREVIOUS"
private const val ICON_NEXT = "ICON_NEXT"
private const val ICON_VOLUME_OFF = "ICON_VOLUME_OFF"
private const val ICON_VOLUME_DOWN = "ICON_VOLUME_DOWN"
private const val ICON_VOLUME_UP = "ICON_VOLUME_UP"
private const val MEDIA_MUTE = "MUTE"
private const val MEDIA_PLAY_PAUSE = "PLAY_PAUSE"
private const val MEDIA_STOP = "STOP"
private const val MEDIA_PREVIOUS = "PREVIOUS"
private const val MEDIA_NEXT = "NEXT"
private const val MEDIA_VOLUME_DOWN = "VOLUME_DOWN"
private const val MEDIA_VOLUME_UP = "VOLUME_UP"

@Preview(showBackground = true)
@Composable
private fun MobileDeckPreview() {
    MobileDeckTheme {
        MobileDeckApp()
    }
}
