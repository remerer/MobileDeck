package com.remerer.mobiledeck

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SliderState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import com.remerer.mobiledeck.ui.theme.MobileDeckTheme
import com.remerer.mobiledeck.ui.theme.MobileDeckThemeStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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
    Utility(R.string.action_utility),
    AppCommand(R.string.action_app_command)
}

private enum class EditActionPanel(@StringRes val labelRes: Int) {
    AppCommand(R.string.app_command_target),
    KeyboardInput(R.string.action_keyboard_input),
    Widget(R.string.action_widget),
    MediaKey(R.string.action_media_key),
    RunCommand(R.string.action_run_command),
    Utility(R.string.action_utility)
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

private enum class PageSwipeMode(@StringRes val labelRes: Int) {
    Disabled(R.string.page_swipe_mode_disabled),
    SingleTouch(R.string.page_swipe_mode_single),
    MultiTouch(R.string.page_swipe_mode_multi);

    fun next(): PageSwipeMode {
        val values = values()
        return values[(ordinal + 1) % values.size]
    }
}

private enum class DeckUiMode(@StringRes val labelRes: Int) {
    Classic(R.string.deck_ui_classic),
    Console(R.string.deck_ui_console)
}

private fun DeckUiMode.toThemeStyle(): MobileDeckThemeStyle {
    return when (this) {
        DeckUiMode.Classic -> MobileDeckThemeStyle.Classic
        DeckUiMode.Console -> MobileDeckThemeStyle.Console
    }
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
    val position: Int = 0,
    val spanColumns: Int = 1,
    val spanRows: Int = 1,
    val appWidgetId: Int = INVALID_APP_WIDGET_ID,
    val appWidgetTouchable: Boolean = true
)

private data class UtilityChoice(
    val payload: String,
    @StringRes val labelRes: Int
)

private data class DeckPageConfig(
    val id: Int,
    val name: String,
    val buttons: List<DeckButton>
)

private data class ConsoleLayoutConfig(
    val rows: List<List<Int>>
)

private data class ConsolePanelOptions(
    val showConnection: Boolean = true,
    val showMessage: Boolean = true,
    val showClock: Boolean = true,
    val showDate: Boolean = true
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

private data class LaunchableAppChoice(
    val label: String,
    val packageName: String,
    val icon: ImageBitmap?
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

private data class DeckThemeColors(
    val backgroundGradient: List<Color>,
    val sidebarBackground: Color,
    val sidebarBorder: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val toggleBackground: Color,
    val actionStart: Color,
    val actionEnd: Color,
    val neutralIconBackground: Color,
    val consoleSidebar: Color,
    val consolePreviewBackground: Color,
    val consoleButtonDefault: Color,
    val consoleButtonFeatured: Color,
    val consoleButtonSystem: Color
)

private val DefaultDeckThemeColors = DeckThemeColors(
    backgroundGradient = listOf(Color(0xFF0F141A), Color(0xFF151B22), Color(0xFF10151B)),
    sidebarBackground = Color(0xFF10171F).copy(alpha = 0.94f),
    sidebarBorder = Color.White.copy(alpha = 0.08f),
    cardBackground = Color(0xFF18212B).copy(alpha = 0.86f),
    cardBorder = Color.White.copy(alpha = 0.08f),
    textPrimary = Color.White,
    textSecondary = Color.White.copy(alpha = 0.64f),
    textMuted = Color.White.copy(alpha = 0.38f),
    toggleBackground = Color(0xFF151D26),
    actionStart = Color(0xFF18212B),
    actionEnd = Color(0xFF131B24),
    neutralIconBackground = Color(0xFF263342),
    consoleSidebar = Color(0xFF17212B).copy(alpha = 0.86f),
    consolePreviewBackground = Color(0xFF101820),
    consoleButtonDefault = Color(0xFF24313D),
    consoleButtonFeatured = Color(0xFF245B9D),
    consoleButtonSystem = Color(0xFF1F5DAD)
)

private val LocalDeckThemeColors = staticCompositionLocalOf { DefaultDeckThemeColors }

private val ClassicLayoutAccent = Color(0xFF9B5DE5)
private val ClassicLayoutSecondaryAccent = Color(0xFF5F2AA0)
private val ClassicButtonAccent = Color(0xFFE47B17)
private val ClassicButtonSecondaryAccent = Color(0xFFB85B00)

private enum class AppPage {
    Deck,
    LayoutEditor,
    ConsoleLayoutEditor,
    Settings
}

private enum class BluetoothPermissionAction {
    RegisterHid,
    MakeDiscoverable
}

private enum class ButtonVibrationLevel(
    @StringRes val labelRes: Int,
    @StringRes val shortLabelRes: Int,
    val durationMillis: Long,
    val amplitude: Int
) {
    Off(R.string.button_vibration_off, R.string.button_vibration_off_short, 0L, 0),
    Weak(R.string.button_vibration_weak, R.string.button_vibration_weak_short, 10L, 55),
    Medium(R.string.button_vibration_medium, R.string.button_vibration_medium_short, 14L, 115),
    Strong(R.string.button_vibration_strong, R.string.button_vibration_strong_short, 18L, 190);

    fun next(): ButtonVibrationLevel {
        val values = values()
        return values[(ordinal + 1) % values.size]
    }
}

@Composable
private fun deckThemeColors(mode: DeckUiMode = DeckUiMode.Classic): DeckThemeColors {
    val dark = isSystemInDarkTheme()
    return when {
        mode == DeckUiMode.Console && dark -> DeckThemeColors(
            backgroundGradient = listOf(Color(0xFF050A10), Color(0xFF0D1721), Color(0xFF06111A)),
            sidebarBackground = Color(0xFF071018).copy(alpha = 0.92f),
            sidebarBorder = Color.White.copy(alpha = 0.08f),
            cardBackground = Color(0xFF111D27).copy(alpha = 0.86f),
            cardBorder = Color.White.copy(alpha = 0.08f),
            textPrimary = Color.White,
            textSecondary = Color.White.copy(alpha = 0.64f),
            textMuted = Color.White.copy(alpha = 0.38f),
            toggleBackground = Color(0xFF101B25),
            actionStart = Color(0xFF111D27),
            actionEnd = Color(0xFF0D1720),
            neutralIconBackground = Color(0xFF233342),
            consoleSidebar = Color(0xFF17212B).copy(alpha = 0.86f),
            consolePreviewBackground = Color(0xFF08131D),
            consoleButtonDefault = Color(0xFF1B2630),
            consoleButtonFeatured = Color(0xFF245B9D),
            consoleButtonSystem = Color(0xFF1F5DAD)
        )
        mode == DeckUiMode.Console -> DeckThemeColors(
            backgroundGradient = listOf(Color(0xFFF2F8FC), Color(0xFFE6F2FA), Color(0xFFDCEAF4)),
            sidebarBackground = Color(0xFFF8FCFF).copy(alpha = 0.96f),
            sidebarBorder = Color(0xFFB8D2E4),
            cardBackground = Color.White.copy(alpha = 0.88f),
            cardBorder = Color(0xFFC8DCE8),
            textPrimary = Color(0xFF10202B),
            textSecondary = Color(0xFF51616D),
            textMuted = Color(0xFF7A8A95),
            toggleBackground = Color(0xFFE4F0F8),
            actionStart = Color(0xFFF8FCFF),
            actionEnd = Color(0xFFEAF5FB),
            neutralIconBackground = Color(0xFFD7E8F3),
            consoleSidebar = Color(0xFFFFFFFF).copy(alpha = 0.9f),
            consolePreviewBackground = Color(0xFFEAF5FB),
            consoleButtonDefault = Color(0xFFDBE8F1),
            consoleButtonFeatured = Color(0xFF1976B7),
            consoleButtonSystem = Color(0xFF1D82BE)
        )
        dark -> DeckThemeColors(
            backgroundGradient = listOf(Color(0xFF0F141A), Color(0xFF151B22), Color(0xFF10151B)),
            sidebarBackground = Color(0xFF10171F).copy(alpha = 0.94f),
            sidebarBorder = Color.White.copy(alpha = 0.08f),
            cardBackground = Color(0xFF18212B).copy(alpha = 0.86f),
            cardBorder = Color.White.copy(alpha = 0.08f),
            textPrimary = Color.White,
            textSecondary = Color.White.copy(alpha = 0.64f),
            textMuted = Color.White.copy(alpha = 0.38f),
            toggleBackground = Color(0xFF151D26),
            actionStart = Color(0xFF18212B),
            actionEnd = Color(0xFF131B24),
            neutralIconBackground = Color(0xFF263342),
            consoleSidebar = Color(0xFF17212B).copy(alpha = 0.86f),
            consolePreviewBackground = Color(0xFF101820),
            consoleButtonDefault = Color(0xFF24313D),
            consoleButtonFeatured = Color(0xFF245B9D),
            consoleButtonSystem = Color(0xFF1F5DAD)
        )
        else -> DeckThemeColors(
            backgroundGradient = listOf(Color(0xFFF7F9FC), Color(0xFFEFF4FA), Color(0xFFE6EDF5)),
            sidebarBackground = Color.White.copy(alpha = 0.95f),
            sidebarBorder = Color(0xFFD0DAE5),
            cardBackground = Color.White.copy(alpha = 0.9f),
            cardBorder = Color(0xFFD7E0EA),
            textPrimary = Color(0xFF17202A),
            textSecondary = Color(0xFF56616D),
            textMuted = Color(0xFF838E99),
            toggleBackground = Color(0xFFE8EEF5),
            actionStart = Color(0xFFFFFFFF),
            actionEnd = Color(0xFFF0F5FA),
            neutralIconBackground = Color(0xFFDDE7F2),
            consoleSidebar = Color.White.copy(alpha = 0.9f),
            consolePreviewBackground = Color(0xFFEFF4FA),
            consoleButtonDefault = Color(0xFFE1E8F0),
            consoleButtonFeatured = Color(0xFF276DB4),
            consoleButtonSystem = Color(0xFF2369B0)
        )
    }
}

@Composable
private fun MobileDeckApp() {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val appWidgetManager = remember(context, isPreview) {
        if (isPreview) null else AppWidgetManager.getInstance(context.applicationContext)
    }
    val appWidgetHost = remember(context, isPreview) {
        if (isPreview) null else AppWidgetHost(context.applicationContext, APP_WIDGET_HOST_ID)
    }
    var hidStatus by remember { mutableStateOf(HidStatus()) }
    val hidManager = remember {
        HidKeyboardManager(context.applicationContext) { status ->
            hidStatus = status
        }
    }
    var pairedHosts by remember { mutableStateOf(emptyList<PairedHidHost>()) }
    var pendingBluetoothPermissionAction by remember { mutableStateOf<BluetoothPermissionAction?>(null) }
    var pairingDiscoverable by remember { mutableStateOf(false) }
    var pairingDiscoverableUntilMillis by remember { mutableStateOf<Long?>(null) }
    var discoverableFinishedMessage by remember { mutableStateOf("Discoverable request finished.") }
    val discoverableLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val durationSeconds = result.resultCode.takeIf { it > 0 } ?: 0
        if (durationSeconds > 0) {
            pairingDiscoverableUntilMillis = System.currentTimeMillis() + durationSeconds * 1000L
        } else {
            pairingDiscoverable = false
            pairingDiscoverableUntilMillis = null
        }
        hidStatus = hidStatus.copy(
            message = discoverableFinishedMessage
        )
    }

    fun launchDiscoverableRequest(
        finishedMessage: String = "Discoverable request finished."
    ) {
        pairingDiscoverable = true
        discoverableFinishedMessage = finishedMessage
        runCatching {
            discoverableLauncher.launch(
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
                    putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
                }
            )
        }.onFailure { error ->
            pairingDiscoverable = false
            pairingDiscoverableUntilMillis = null
            hidStatus = HidStatus(
                HidConnectionState.Error,
                error.message ?: "Could not request Bluetooth discoverable mode"
            )
            Log.e("MobileDeck", "Failed to request discoverable mode", error)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { grants ->
        fun allGranted(requiredPermissions: Array<String>): Boolean {
            return requiredPermissions.all { permission ->
                grants[permission] == true ||
                    ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
            }
        }

        val granted = when (pendingBluetoothPermissionAction) {
            BluetoothPermissionAction.MakeDiscoverable -> allGranted(HidKeyboardManager.DISCOVERABLE_BLUETOOTH_PERMISSIONS)
            BluetoothPermissionAction.RegisterHid,
            null -> allGranted(HidKeyboardManager.HID_BLUETOOTH_PERMISSIONS)
        }

        if (granted) {
            when (pendingBluetoothPermissionAction) {
                BluetoothPermissionAction.MakeDiscoverable -> launchDiscoverableRequest()
                BluetoothPermissionAction.RegisterHid,
                null -> {
                    hidManager.start()
                    pairedHosts = hidManager.pairedHosts()
                }
            }
        } else {
            pairingDiscoverable = false
            pairingDiscoverableUntilMillis = null
            hidStatus = HidStatus(
                HidConnectionState.PermissionMissing,
                "Bluetooth permissions were denied"
            )
        }
        pendingBluetoothPermissionAction = null
    }
    var deckPages by remember { mutableStateOf(loadDeckPages(context)) }
    var activeDeckPageId by remember { mutableStateOf(deckPages.first().id) }
    var deckColumns by remember { mutableStateOf(loadDeckColumns(context)) }
    var deckRows by remember { mutableStateOf(loadDeckRows(context)) }
    var deckSpacing by remember { mutableStateOf(loadDeckSpacing(context)) }
    var pageSwipeAxis by remember { mutableStateOf(loadPageSwipeAxis(context)) }
    var pageSwipeMode by remember { mutableStateOf(loadPageSwipeMode(context)) }
    var pageSwipeAnimation by remember { mutableStateOf(loadPageSwipeAnimation(context)) }
    var infinitePageSwipe by remember { mutableStateOf(loadInfinitePageSwipe(context)) }
    var buttonVibrationLevel by remember { mutableStateOf(loadButtonVibrationLevel(context)) }
    var deckUiMode by remember { mutableStateOf(loadDeckUiMode(context)) }
    var consoleLayout by remember { mutableStateOf(loadConsoleLayout(context)) }
    var consolePanelOptions by remember { mutableStateOf(loadConsolePanelOptions(context)) }
    var lastPageDelta by remember { mutableStateOf(1) }
    var pageAnimationSequence by remember { mutableStateOf(0) }
    var editingButton by remember { mutableStateOf<DeckButton?>(null) }
    var consoleButtonPickerRow by remember { mutableStateOf<Int?>(null) }
    var pendingWidgetButtonId by remember { mutableStateOf<Int?>(null) }
    var pendingWidgetId by remember { mutableStateOf<Int?>(null) }
    var logs by remember { mutableStateOf(emptyList<ActivityLog>()) }
    var page by remember { mutableStateOf(AppPage.Deck) }
    var confirmSettingsButtonRestore by remember { mutableStateOf(false) }
    val activeDeckPage = deckPages.firstOrNull { it.id == activeDeckPageId } ?: deckPages.first()
    val deckButtons = activeDeckPage.buttons

    LaunchedEffect(pairingDiscoverableUntilMillis) {
        val until = pairingDiscoverableUntilMillis ?: return@LaunchedEffect
        delay((until - System.currentTimeMillis()).coerceAtLeast(0L))
        if (pairingDiscoverableUntilMillis == until) {
            pairingDiscoverable = false
            pairingDiscoverableUntilMillis = null
        }
    }

    fun updateButtonEverywhere(button: DeckButton) {
        val updatedPages = updateDeckButton(deckPages, button)
        deckPages = updatedPages
        saveDeckPages(context, updatedPages)
    }

    fun assignWidgetToButton(buttonId: Int, widgetId: Int) {
        val info = appWidgetManager?.getAppWidgetInfo(widgetId)
        val updated = deckPages.flatMap { it.buttons }
            .firstOrNull { it.id == buttonId }
            ?.copy(
                title = info?.label?.takeIf { label -> label.isNotBlank() } ?: "Widget",
                subtitle = "Android widget",
                icon = "W",
                iconImageUri = "",
                displayMode = DeckDisplayMode.IconOnly,
                appWidgetId = widgetId,
                appWidgetTouchable = true,
                spanColumns = 2,
                spanRows = 2
            ) ?: return
        updateButtonEverywhere(updated)
    }

    val configureWidgetLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val buttonId = pendingWidgetButtonId
        val widgetId = pendingWidgetId
        if (result.resultCode == Activity.RESULT_OK && buttonId != null && widgetId != null) {
            assignWidgetToButton(buttonId, widgetId)
        } else if (widgetId != null) {
            appWidgetHost?.deleteAppWidgetId(widgetId)
        }
        pendingWidgetButtonId = null
        pendingWidgetId = null
    }

    val pickWidgetLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val buttonId = pendingWidgetButtonId
        val widgetId = result.data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pendingWidgetId ?: INVALID_APP_WIDGET_ID)
            ?: pendingWidgetId
        if (result.resultCode != Activity.RESULT_OK || buttonId == null || widgetId == null || widgetId == INVALID_APP_WIDGET_ID) {
            if (widgetId != null && widgetId != INVALID_APP_WIDGET_ID) appWidgetHost?.deleteAppWidgetId(widgetId)
            pendingWidgetButtonId = null
            pendingWidgetId = null
            return@rememberLauncherForActivityResult
        }

        val info = appWidgetManager?.getAppWidgetInfo(widgetId)
        if (info?.configure != null) {
            pendingWidgetId = widgetId
            configureWidgetLauncher.launch(
                Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).apply {
                    component = info.configure
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                }
            )
        } else {
            assignWidgetToButton(buttonId, widgetId)
            pendingWidgetButtonId = null
            pendingWidgetId = null
        }
    }

    DisposableEffect(appWidgetHost) {
        if (appWidgetHost != null) {
            appWidgetHost.startListening()
        }
        onDispose {
            if (appWidgetHost != null) {
                appWidgetHost.stopListening()
            }
        }
    }

    fun startHid() {
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.P -> {
                hidStatus = HidStatus(
                    HidConnectionState.Unsupported,
                    "Android ${Build.VERSION.RELEASE} can be discoverable for pairing, but Bluetooth HID input requires Android 9 or newer."
                )
                launchDiscoverableRequest(
                    finishedMessage = "Pairing mode finished. Bluetooth HID input still requires Android 9 or newer."
                )
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !hidManager.hasRequiredPermissions() -> {
                pendingBluetoothPermissionAction = BluetoothPermissionAction.RegisterHid
                permissionLauncher.launch(HidKeyboardManager.HID_BLUETOOTH_PERMISSIONS)
            }

            else -> hidManager.start()
        }
        pairedHosts = hidManager.pairedHosts()
    }

    fun makeDiscoverable() {
        if (pairingDiscoverable) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !hidManager.hasDiscoverablePermissions()) {
            pairingDiscoverable = true
            pendingBluetoothPermissionAction = BluetoothPermissionAction.MakeDiscoverable
            permissionLauncher.launch(HidKeyboardManager.DISCOVERABLE_BLUETOOTH_PERMISSIONS)
        } else {
            launchDiscoverableRequest()
        }
    }

    fun cancelDiscoverable() {
        pairingDiscoverable = false
        pairingDiscoverableUntilMillis = null
        if (pendingBluetoothPermissionAction == BluetoothPermissionAction.MakeDiscoverable) {
            pendingBluetoothPermissionAction = null
        }
        hidStatus = hidStatus.copy(
            message = "Discoverable request canceled in MobileDeck."
        )
    }

    fun addDeckButton(position: Int? = null, editAfterCreate: Boolean = false) {
        val colors = defaultDeckColors()
        val buttonCapacity = pageButtonCapacity(activeDeckPage.id, deckPages, deckColumns, deckRows)
        val showTitle = activeDeckPage.id == deckPages.firstOrNull()?.id
        val targetPosition = position ?: nextOpenPosition(deckButtons, deckColumns, deckRows, showTitle)
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
        if (position == null && targetPosition >= buttonCapacity && deckPages.size >= MAX_PAGES) return
        val updatedPages = if (position == null && targetPosition >= buttonCapacity) {
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

    fun finishLayoutEditor() {
        if (hasSettingsButton(deckPages)) {
            page = AppPage.Settings
        } else {
            confirmSettingsButtonRestore = true
        }
    }

    fun restoreSettingsButtonAndFinish() {
        val updatedPages = restoreSettingsButton(deckPages, deckColumns, deckRows)
        deckPages = updatedPages
        activeDeckPageId = updatedPages.first().id
        saveDeckPages(context, updatedPages)
        confirmSettingsButtonRestore = false
        page = AppPage.Settings
    }

    fun pickWidgetForButton(button: DeckButton) {
        updateButtonEverywhere(button)
        val oldWidgetId = button.appWidgetId
        val widgetId = appWidgetHost?.allocateAppWidgetId() ?: return
        pendingWidgetButtonId = button.id
        pendingWidgetId = widgetId
        if (oldWidgetId != INVALID_APP_WIDGET_ID) {
            appWidgetHost.deleteAppWidgetId(oldWidgetId)
        }
        pickWidgetLauncher.launch(
            Intent(AppWidgetManager.ACTION_APPWIDGET_PICK).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            }
        )
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
            DeckActionType.Utility -> runUtilityAction(context, button.payload)
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
            button.actionType == DeckActionType.Utility -> "utility unavailable"
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
        val movedButton = button.copy(position = targetPosition)
        val showTitle = activeDeckPage.id == deckPages.firstOrNull()?.id
        if (!canPlaceButton(movedButton, deckButtons.filterNot { it.id == button.id }, deckColumns, deckRows, showTitle)) {
            return
        }
        val updatedPages = updateDeckPage(deckPages, activeDeckPage.id) { deckPage ->
            deckPage.buttons.map { existing ->
                if (existing.id == button.id) movedButton else existing
            }
        }
        deckPages = updatedPages
        saveDeckPages(context, updatedPages)
    }

    DisposableEffect(hidManager) {
        onDispose { hidManager.stop() }
    }

    val appColors = deckThemeColors(deckUiMode)
    MobileDeckTheme(style = deckUiMode.toThemeStyle()) {
    CompositionLocalProvider(LocalDeckThemeColors provides appColors) {
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
                spacing = deckSpacing.dp,
                status = hidStatus,
                appWidgetHost = appWidgetHost,
                appWidgetManager = appWidgetManager,
                uiMode = deckUiMode,
                consoleLayout = consoleLayout,
                consolePanelOptions = consolePanelOptions,
                previewMode = false,
                pageSwipeAxis = pageSwipeAxis,
                pageSwipeMode = pageSwipeMode,
                pageSwipeAnimation = pageSwipeAnimation,
                pageSwipeDelta = lastPageDelta,
                pageAnimationSequence = pageAnimationSequence,
                onPageSwipe = ::switchDeckPage,
                onAddPage = ::addDeckPage,
                onButtonPressed = ::pressDeckButton,
                onButtonTouchStarted = { context.applicationContext.vibrateButtonPress(buttonVibrationLevel) },
                onButtonTouchEnded = { context.applicationContext.vibrateButtonPress(buttonVibrationLevel) },
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
                spacing = deckSpacing.dp,
                status = hidStatus,
                appWidgetHost = appWidgetHost,
                appWidgetManager = appWidgetManager,
                pageSwipeAxis = pageSwipeAxis,
                pageSwipeMode = pageSwipeMode,
                pageSwipeAnimation = pageSwipeAnimation,
                pageSwipeDelta = lastPageDelta,
                pageAnimationSequence = pageAnimationSequence,
                onBack = ::finishLayoutEditor,
                onPageSwipe = ::switchDeckPage,
                onAddPage = ::addDeckPage,
                onDeletePage = ::deleteActiveDeckPage,
                onResetPage = ::resetFirstDeckPage,
                onButtonEdit = { editingButton = it },
                onButtonMoved = ::moveDeckButtonToSlot,
                onEmptySlotPressed = { slot -> addDeckButton(slot, editAfterCreate = true) }
            )

            AppPage.ConsoleLayoutEditor -> ConsoleLayoutEditorPage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(10.dp),
                buttons = deckButtons,
                layout = consoleLayout,
                onBack = { page = AppPage.Settings },
                onAddRow = {
                    val updated = consoleLayout.copy(rows = consoleLayout.rows + emptyList())
                    consoleLayout = updated
                    saveConsoleLayout(context, updated)
                },
                onReset = {
                    val updated = defaultConsoleLayout(deckButtons)
                    consoleLayout = updated
                    saveConsoleLayout(context, updated)
                },
                onPickButton = { rowIndex -> consoleButtonPickerRow = rowIndex },
                onRemoveButton = { rowIndex, buttonId ->
                    val updated = consoleLayout.copy(
                        rows = consoleLayout.rows.mapIndexed { index, row ->
                            if (index == rowIndex) row.filterNot { it == buttonId } else row
                        }
                    )
                    consoleLayout = updated
                    saveConsoleLayout(context, updated)
                },
                onMoveButton = { rowIndex, fromIndex, delta ->
                    val targetIndex = (fromIndex + delta).coerceIn(consoleLayout.rows[rowIndex].indices)
                    if (targetIndex != fromIndex) {
                        val updatedRows = consoleLayout.rows.mapIndexed { index, row ->
                            if (index != rowIndex) {
                                row
                            } else {
                                row.toMutableList().apply {
                                    val item = removeAt(fromIndex)
                                    add(targetIndex, item)
                                }
                            }
                        }
                        val updated = consoleLayout.copy(rows = updatedRows)
                        consoleLayout = updated
                        saveConsoleLayout(context, updated)
                    }
                },
                onEditButton = { button -> editingButton = button }
            )

            AppPage.Settings -> SettingsPage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                status = hidStatus,
                logs = logs,
                columns = deckColumns,
                rows = deckRows,
                spacing = deckSpacing,
                pageName = activeDeckPage.name,
                pageCount = deckPages.size,
                pairedHosts = pairedHosts,
                onBack = {
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    page = AppPage.Deck
                },
                deckPages = deckPages,
                activePageId = activeDeckPage.id,
                pageSwipeAxis = pageSwipeAxis,
                pageSwipeMode = pageSwipeMode,
                pageSwipeAnimation = pageSwipeAnimation,
                infinitePageSwipe = infinitePageSwipe,
                buttonVibrationLevel = buttonVibrationLevel,
                deckUiMode = deckUiMode,
                consolePanelOptions = consolePanelOptions,
                pairingDiscoverable = pairingDiscoverable,
                onLayoutEditor = {
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    page = AppPage.LayoutEditor
                },
                onConsoleLayoutEditor = {
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    page = AppPage.ConsoleLayoutEditor
                },
                onPageSwipeAxisChange = { axis ->
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    pageSwipeAxis = axis
                    savePageSwipeAxis(context, axis)
                },
                onPageSwipeModeChange = { mode ->
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    pageSwipeMode = mode
                    savePageSwipeMode(context, mode)
                },
                onPageSwipeAnimationChange = { enabled ->
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    pageSwipeAnimation = enabled
                    savePageSwipeAnimation(context, enabled)
                },
                onInfinitePageSwipeChange = { enabled ->
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    infinitePageSwipe = enabled
                    saveInfinitePageSwipe(context, enabled)
                },
                onButtonVibrationLevelChange = { level ->
                    buttonVibrationLevel = level
                    saveButtonVibrationLevel(context, level)
                    context.applicationContext.vibrateButtonPress(level)
                },
                onDeckUiModeChange = { mode ->
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    deckUiMode = mode
                    saveDeckUiMode(context, mode)
                },
                onConsolePanelOptionsChange = { options ->
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    consolePanelOptions = options
                    saveConsolePanelOptions(context, options)
                },
                onStart = {
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    startHid()
                },
                onStop = {
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    hidManager.stop()
                },
                onMakeDiscoverable = {
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    makeDiscoverable()
                },
                onCancelDiscoverable = {
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    cancelDiscoverable()
                },
                onRefreshHosts = {
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    pairedHosts = hidManager.pairedHosts()
                },
                onConnectHost = { host ->
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    connectHost(host)
                },
                onColumnsChange = { columns ->
                    deckColumns = columns
                    saveDeckColumns(context, columns)
                },
                onRowsChange = { rows ->
                    deckRows = rows
                    saveDeckRows(context, rows)
                },
                onSpacingChange = { spacing ->
                    deckSpacing = spacing
                    saveDeckSpacing(context, spacing)
                },
                onAddButton = { addDeckButton() },
                onAddPage = {
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    addDeckPage()
                },
            )
        }
    }

    consoleButtonPickerRow?.let { rowIndex ->
        ConsoleButtonPickerDialog(
            buttons = deckButtons.filterNot { buttonAppAction(it) == DeckActionType.Settings },
            assignedIds = consoleLayout.rows.flatten().toSet(),
            onDismiss = { consoleButtonPickerRow = null },
            onSelect = { button ->
                val rows = consoleLayout.rows.ifEmpty { listOf(emptyList()) }
                val safeRowIndex = rowIndex.coerceIn(rows.indices)
                val updated = ConsoleLayoutConfig(
                    rows = rows.mapIndexed { index, row ->
                        if (index == safeRowIndex) row + button.id else row
                    }
                )
                consoleLayout = updated
                saveConsoleLayout(context, updated)
                consoleButtonPickerRow = null
            }
        )
    }

    editingButton?.let { button ->
        EditButtonDialog(
            button = button,
            status = hidStatus,
            onDismiss = { editingButton = null },
            onSave = { updated ->
                if (button.appWidgetId != INVALID_APP_WIDGET_ID && button.appWidgetId != updated.appWidgetId) {
                    if (appWidgetHost != null) {
                        appWidgetHost.deleteAppWidgetId(button.appWidgetId)
                    }
                }
                val showTitle = activeDeckPage.id == deckPages.firstOrNull()?.id
                val adjustedButton = shrinkButtonToAvailable(
                    updated,
                    activeDeckPage.buttons.filterNot { it.id == updated.id },
                    deckColumns,
                    deckRows,
                    showTitle
                )
                val updatedPages = updateDeckButton(deckPages, adjustedButton)
                deckPages = updatedPages
                saveDeckPages(context, updatedPages)
                editingButton = null
            },
            onPickWidget = { updated ->
                editingButton = null
                pickWidgetForButton(updated)
            },
            onDelete = {
                if (button.appWidgetId != INVALID_APP_WIDGET_ID) {
                    if (appWidgetHost != null) {
                        appWidgetHost.deleteAppWidgetId(button.appWidgetId)
                    }
                }
                val updatedPages = deckPages.map { pageConfig ->
                    pageConfig.copy(buttons = pageConfig.buttons.filterNot { existing -> existing.id == button.id })
                }
                deckPages = updatedPages
                saveDeckPages(context, updatedPages)
                editingButton = null
            }
        )
    }

    if (confirmSettingsButtonRestore) {
        AlertDialog(
            onDismissRequest = { confirmSettingsButtonRestore = false },
            title = { Text(stringResource(R.string.restore_settings_button_title)) },
            text = { Text(stringResource(R.string.restore_settings_button_message)) },
            confirmButton = {
                TextButton(onClick = ::restoreSettingsButtonAndFinish) {
                    Text(stringResource(R.string.restore_settings_button_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmSettingsButtonRestore = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    }
    }
}

@Composable
private fun SettingsPage(
    modifier: Modifier = Modifier,
    status: HidStatus,
    logs: List<ActivityLog>,
    columns: Int,
    rows: Int,
    spacing: Int,
    deckPages: List<DeckPageConfig>,
    activePageId: Int,
    pageSwipeAxis: PageSwipeAxis,
    pageSwipeMode: PageSwipeMode,
    pageSwipeAnimation: Boolean,
    infinitePageSwipe: Boolean,
    buttonVibrationLevel: ButtonVibrationLevel,
    deckUiMode: DeckUiMode,
    consolePanelOptions: ConsolePanelOptions,
    pairingDiscoverable: Boolean,
    pageName: String,
    pageCount: Int,
    pairedHosts: List<PairedHidHost>,
    onBack: () -> Unit,
    onLayoutEditor: () -> Unit,
    onConsoleLayoutEditor: () -> Unit,
    onPageSwipeAxisChange: (PageSwipeAxis) -> Unit,
    onPageSwipeModeChange: (PageSwipeMode) -> Unit,
    onPageSwipeAnimationChange: (Boolean) -> Unit,
    onInfinitePageSwipeChange: (Boolean) -> Unit,
    onButtonVibrationLevelChange: (ButtonVibrationLevel) -> Unit,
    onDeckUiModeChange: (DeckUiMode) -> Unit,
    onConsolePanelOptionsChange: (ConsolePanelOptions) -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onMakeDiscoverable: () -> Unit,
    onCancelDiscoverable: () -> Unit,
    onRefreshHosts: () -> Unit,
    onConnectHost: (PairedHidHost) -> Unit,
    onColumnsChange: (Int) -> Unit,
    onRowsChange: (Int) -> Unit,
    onSpacingChange: (Int) -> Unit,
    onAddButton: () -> Unit,
    onAddPage: () -> Unit
) {
    val colors = deckThemeColors(deckUiMode)
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(colors.backgroundGradient)
            )
    ) {
        SettingsSidebar(
            modifier = Modifier
                .width(300.dp)
                .fillMaxHeight(),
            status = status,
            deckUiMode = deckUiMode,
            pairedHosts = pairedHosts,
            pairingDiscoverable = pairingDiscoverable,
            onBack = onBack,
            onDeckUiModeChange = onDeckUiModeChange,
            onStart = onStart,
            onStop = onStop,
            onMakeDiscoverable = onMakeDiscoverable,
            onCancelDiscoverable = onCancelDiscoverable,
            onRefreshHosts = onRefreshHosts,
            onConnectHost = onConnectHost
        )
        AnimatedContent(
            targetState = deckUiMode,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            transitionSpec = {
                val direction = if (targetState == DeckUiMode.Console) 1 else -1
                slideInHorizontally { width -> direction * width } togetherWith
                    slideOutHorizontally { width -> -direction * width }
            },
            label = "settingsMode"
        ) { mode ->
            if (mode == DeckUiMode.Console) {
                ConsoleSettingsContent(
                    deckPages = deckPages,
                    activePageId = activePageId,
                    pageSwipeMode = pageSwipeMode,
                    pageSwipeAnimation = pageSwipeAnimation,
                    infinitePageSwipe = infinitePageSwipe,
                    buttonVibrationLevel = buttonVibrationLevel,
                    consolePanelOptions = consolePanelOptions,
                    pageName = pageName,
                    pageCount = pageCount,
                    logs = logs,
                    onPageSwipeModeChange = onPageSwipeModeChange,
                    onPageSwipeAnimationChange = onPageSwipeAnimationChange,
                    onInfinitePageSwipeChange = onInfinitePageSwipeChange,
                    onButtonVibrationLevelChange = onButtonVibrationLevelChange,
                    onConsolePanelOptionsChange = onConsolePanelOptionsChange,
                    onConsoleLayoutEditor = onConsoleLayoutEditor,
                    onAddPage = onAddPage
                )
            } else {
                ClassicSettingsContent(
                    deckPages = deckPages,
                    activePageId = activePageId,
                    columns = columns,
                    rows = rows,
                    spacing = spacing,
                    pageSwipeAxis = pageSwipeAxis,
                    pageSwipeMode = pageSwipeMode,
                    pageSwipeAnimation = pageSwipeAnimation,
                    infinitePageSwipe = infinitePageSwipe,
                    buttonVibrationLevel = buttonVibrationLevel,
                    pageName = pageName,
                    pageCount = pageCount,
                    logs = logs,
                    onPageSwipeAxisChange = onPageSwipeAxisChange,
                    onPageSwipeModeChange = onPageSwipeModeChange,
                    onPageSwipeAnimationChange = onPageSwipeAnimationChange,
                    onInfinitePageSwipeChange = onInfinitePageSwipeChange,
                    onButtonVibrationLevelChange = onButtonVibrationLevelChange,
                    onLayoutEditor = onLayoutEditor,
                    onColumnsChange = onColumnsChange,
                    onRowsChange = onRowsChange,
                    onSpacingChange = onSpacingChange,
                    onAddPage = onAddPage
                )
            }
        }
    }
}

@Composable
private fun SettingsSidebar(
    modifier: Modifier = Modifier,
    status: HidStatus,
    deckUiMode: DeckUiMode,
    pairedHosts: List<PairedHidHost>,
    pairingDiscoverable: Boolean,
    onBack: () -> Unit,
    onDeckUiModeChange: (DeckUiMode) -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onMakeDiscoverable: () -> Unit,
    onCancelDiscoverable: () -> Unit,
    onRefreshHosts: () -> Unit,
    onConnectHost: (PairedHidHost) -> Unit
) {
    val colors = deckThemeColors(deckUiMode)
    Column(
        modifier = modifier
            .background(colors.sidebarBackground)
            .border(1.dp, colors.sidebarBorder)
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.settings_back_to_deck),
                    tint = colors.textPrimary
                )
            }
        }
        UiModeToggle(
            deckUiMode = deckUiMode,
            onDeckUiModeChange = onDeckUiModeChange
        )
        AnimatedContent(
            targetState = deckUiMode,
            transitionSpec = {
                val direction = if (targetState == DeckUiMode.Console) 1 else -1
                slideInHorizontally { width -> direction * width } togetherWith
                    slideOutHorizontally { width -> -direction * width }
            },
            label = "bluetoothSettingsMode"
        ) { mode ->
            BluetoothManagementBox(
                status = status,
                deckUiMode = mode,
                pairedHosts = pairedHosts,
                pairingDiscoverable = pairingDiscoverable,
                onStart = onStart,
                onStop = onStop,
                onMakeDiscoverable = onMakeDiscoverable,
                onCancelDiscoverable = onCancelDiscoverable,
                onRefreshHosts = onRefreshHosts,
                onConnectHost = onConnectHost
            )
        }
    }
}

@Composable
private fun UiModeToggle(
    deckUiMode: DeckUiMode,
    onDeckUiModeChange: (DeckUiMode) -> Unit
) {
    val colors = deckThemeColors(deckUiMode)
    val selectedStart by animateColorAsState(
        targetValue = if (deckUiMode == DeckUiMode.Console) Color(0xFF006BAC) else Color(0xFF0B63D1),
        label = "settingsToggleStart"
    )
    val selectedEnd by animateColorAsState(
        targetValue = if (deckUiMode == DeckUiMode.Console) Color(0xFF11B9FF) else Color(0xFF228BFF),
        label = "settingsToggleEnd"
    )
    val selectedBorder by animateColorAsState(
        targetValue = if (deckUiMode == DeckUiMode.Console) Color(0xFF6DDBFF) else Color(0xFF72B8FF),
        label = "settingsToggleBorder"
    )
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.toggleBackground)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(8.dp))
    ) {
        val selectedIndex = if (deckUiMode == DeckUiMode.Console) 1f else 0f
        val offsetIndex by animateFloatAsState(selectedIndex, label = "settingsModeToggle")
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .offset { IntOffset((constraints.maxWidth * 0.5f * offsetIndex).roundToInt(), 0) }
                .padding(3.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(
                    Brush.linearGradient(
                        listOf(selectedStart, selectedEnd)
                    )
                )
                .border(1.dp, selectedBorder, RoundedCornerShape(7.dp))
        )
        Row(Modifier.fillMaxSize()) {
            DeckUiMode.values().forEach { mode ->
                Surface(
                    modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                color = Color.Transparent,
                contentColor = colors.textPrimary,
                onClick = { onDeckUiModeChange(mode) }
            ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(mode.labelRes),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = if (deckUiMode == mode) Color.White else colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BluetoothManagementBox(
    status: HidStatus,
    deckUiMode: DeckUiMode,
    pairedHosts: List<PairedHidHost>,
    pairingDiscoverable: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onMakeDiscoverable: () -> Unit,
    onCancelDiscoverable: () -> Unit,
    onRefreshHosts: () -> Unit,
    onConnectHost: (PairedHidHost) -> Unit
) {
    val colors = deckThemeColors(deckUiMode)
    val accent = settingsModeAccent(deckUiMode)
    val secondaryAccent = settingsModeSecondaryAccent(deckUiMode)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.cardBackground)
            .border(1.dp, accent.copy(alpha = 0.48f), RoundedCornerShape(8.dp)),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            accent.copy(alpha = if (isSystemInDarkTheme()) 0.62f else 0.16f),
                            secondaryAccent.copy(alpha = if (isSystemInDarkTheme()) 0.42f else 0.08f)
                        )
                    )
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Bluetooth,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = stringResource(R.string.settings_hid_management),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            SettingsStatusBadge(status.state)
        }
        Column(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = localizedStatusMessage(status.message),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SidebarCompactActionButton(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Bluetooth,
                    title = stringResource(R.string.register_hid),
                    accent = accent,
                    highlighted = true,
                    deckUiMode = deckUiMode,
                    onClick = onStart
                )
                SidebarCompactActionButton(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Stop,
                    title = stringResource(R.string.stop),
                    accent = colors.neutralIconBackground,
                    highlighted = false,
                    deckUiMode = deckUiMode,
                    onClick = onStop
                )
            }
            SidebarDiscoverableRow(
                deckUiMode = deckUiMode,
                pairingDiscoverable = pairingDiscoverable,
                onMakeDiscoverable = onMakeDiscoverable,
                onCancelDiscoverable = onCancelDiscoverable
            )
            PairedHostsInlineSection(
                pairedHosts = pairedHosts,
                deckUiMode = deckUiMode,
                onRefreshHosts = onRefreshHosts,
                onConnectHost = onConnectHost
            )
        }
    }
}

@Composable
private fun SidebarCompactActionButton(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    accent: Color,
    highlighted: Boolean,
    deckUiMode: DeckUiMode,
    onClick: () -> Unit
) {
    val colors = deckThemeColors(deckUiMode)
    val background = if (highlighted) {
        Brush.linearGradient(listOf(settingsModeAccent(deckUiMode), Color(0xFF0B7FE8)))
    } else {
        Brush.linearGradient(listOf(colors.actionStart, colors.actionEnd))
    }
    Surface(
        modifier = modifier,
        color = Color.Transparent,
        contentColor = colors.textPrimary,
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .background(background)
                .border(1.dp, accent.copy(alpha = 0.42f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (highlighted) Color.White else colors.textPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(7.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (highlighted) Color.White else colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SidebarDiscoverableRow(
    deckUiMode: DeckUiMode,
    pairingDiscoverable: Boolean,
    onMakeDiscoverable: () -> Unit,
    onCancelDiscoverable: () -> Unit
) {
    val colors = deckThemeColors(deckUiMode)
    val accent = settingsModeAccent(deckUiMode)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colors.toggleBackground.copy(alpha = 0.58f),
        contentColor = colors.textPrimary,
        shape = RoundedCornerShape(8.dp),
        onClick = {
            if (pairingDiscoverable) onCancelDiscoverable() else onMakeDiscoverable()
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SettingsIconTile(Icons.Filled.Search, accent)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stringResource(R.string.make_discoverable),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.settings_discoverable_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            SettingsSwitch(
                checked = pairingDiscoverable,
                accent = accent,
                onCheckedChange = { enabled ->
                    if (enabled) onMakeDiscoverable() else onCancelDiscoverable()
                }
            )
        }
    }
}

@Composable
private fun PairedHostsInlineSection(
    pairedHosts: List<PairedHidHost>,
    deckUiMode: DeckUiMode,
    onRefreshHosts: () -> Unit,
    onConnectHost: (PairedHidHost) -> Unit
) {
    val colors = deckThemeColors(deckUiMode)
    val accent = settingsModeAccent(deckUiMode)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.toggleBackground.copy(alpha = 0.42f))
            .border(1.dp, colors.cardBorder.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.paired_hosts),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            TextButton(
                onClick = onRefreshHosts,
                colors = ButtonDefaults.textButtonColors(contentColor = accent)
            ) {
                Text(stringResource(R.string.refresh), color = accent)
            }
        }
        if (pairedHosts.isEmpty()) {
            Text(
                text = stringResource(R.string.no_paired_hosts),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        } else {
            pairedHosts.take(3).forEach { host ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = colors.cardBackground.copy(alpha = 0.68f),
                    shape = RoundedCornerShape(6.dp),
                    onClick = { onConnectHost(host) }
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        text = host.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsStatusBadge(state: HidConnectionState) {
    val color = statusDotColor(state)
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = 0.18f))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = stringResource(state.labelRes()),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            maxLines = 1
        )
    }
}

@Composable
private fun ClassicSettingsContent(
    deckPages: List<DeckPageConfig>,
    activePageId: Int,
    columns: Int,
    rows: Int,
    spacing: Int,
    pageSwipeAxis: PageSwipeAxis,
    pageSwipeMode: PageSwipeMode,
    pageSwipeAnimation: Boolean,
    infinitePageSwipe: Boolean,
    buttonVibrationLevel: ButtonVibrationLevel,
    pageName: String,
    pageCount: Int,
    logs: List<ActivityLog>,
    onPageSwipeAxisChange: (PageSwipeAxis) -> Unit,
    onPageSwipeModeChange: (PageSwipeMode) -> Unit,
    onPageSwipeAnimationChange: (Boolean) -> Unit,
    onInfinitePageSwipeChange: (Boolean) -> Unit,
    onButtonVibrationLevelChange: (ButtonVibrationLevel) -> Unit,
    onLayoutEditor: () -> Unit,
    onColumnsChange: (Int) -> Unit,
    onRowsChange: (Int) -> Unit,
    onSpacingChange: (Int) -> Unit,
    onAddPage: () -> Unit
) {
    SettingsDetailContent(
        mode = DeckUiMode.Classic,
        accent = ClassicLayoutAccent,
        icon = Icons.Filled.GridView,
        title = stringResource(R.string.settings_classic_header),
        subtitle = stringResource(R.string.settings_classic_subtitle)
    ) {
        item {
            ClassicLayoutControlsCard(
                columns = columns,
                rows = rows,
                spacing = spacing,
                deckPages = deckPages,
                activePageId = activePageId,
                pageName = pageName,
                pageCount = pageCount,
                pageSwipeAxis = pageSwipeAxis,
                pageSwipeMode = pageSwipeMode,
                pageSwipeAnimation = pageSwipeAnimation,
                infinitePageSwipe = infinitePageSwipe,
                onColumnsChange = onColumnsChange,
                onRowsChange = onRowsChange,
                onSpacingChange = onSpacingChange,
                onPageSwipeAxisChange = onPageSwipeAxisChange,
                onPageSwipeModeChange = onPageSwipeModeChange,
                onPageSwipeAnimationChange = onPageSwipeAnimationChange,
                onInfinitePageSwipeChange = onInfinitePageSwipeChange,
                onAddPage = onAddPage
            )
        }
        item {
            ClassicButtonSettingsCard(
                onLayoutEditor = onLayoutEditor,
                buttonVibrationLevel = buttonVibrationLevel,
                onButtonVibrationLevelChange = onButtonVibrationLevelChange
            )
        }
        item {
            SettingsDiagnosticsCard(logs)
        }
        item {
            SettingsAppInfoRow(mode = DeckUiMode.Classic)
        }
    }
}

@Composable
private fun ConsoleSettingsContent(
    deckPages: List<DeckPageConfig>,
    activePageId: Int,
    pageSwipeMode: PageSwipeMode,
    pageSwipeAnimation: Boolean,
    infinitePageSwipe: Boolean,
    buttonVibrationLevel: ButtonVibrationLevel,
    consolePanelOptions: ConsolePanelOptions,
    pageName: String,
    pageCount: Int,
    logs: List<ActivityLog>,
    onPageSwipeModeChange: (PageSwipeMode) -> Unit,
    onPageSwipeAnimationChange: (Boolean) -> Unit,
    onInfinitePageSwipeChange: (Boolean) -> Unit,
    onButtonVibrationLevelChange: (ButtonVibrationLevel) -> Unit,
    onConsolePanelOptionsChange: (ConsolePanelOptions) -> Unit,
    onConsoleLayoutEditor: () -> Unit,
    onAddPage: () -> Unit
) {
    SettingsDetailContent(
        mode = DeckUiMode.Console,
        accent = Color(0xFF00A6E7),
        icon = Icons.Filled.Settings,
        title = stringResource(R.string.console_settings),
        subtitle = stringResource(R.string.console_settings_subtitle)
    ) {
        item {
            ConsolePreviewCard {
                ConsoleSettingsPreview(panelOptions = consolePanelOptions)
            }
        }
        item {
            ConsoleSettingRow(
                icon = Icons.Filled.GridView,
                iconColor = Color(0xFF00A6E7),
                title = stringResource(R.string.console_layout_editor),
                subtitle = stringResource(R.string.settings_console_layout_desc),
                trailing = {
                    ConsolePillButton(text = stringResource(R.string.console_layout_editor), onClick = onConsoleLayoutEditor)
                }
            )
        }
        item {
            ConsolePanelOptionsCard(
                options = consolePanelOptions,
                onOptionsChange = onConsolePanelOptionsChange
            )
        }
        item {
            ConsoleSettingRow(
                icon = Icons.Filled.SwapHoriz,
                iconColor = Color(0xFF00A6E7),
                title = stringResource(R.string.settings_page_direction),
                subtitle = stringResource(R.string.settings_console_horizontal_desc),
                trailing = {
                    SettingsValuePill(text = stringResource(R.string.page_axis_horizontal_short))
                }
            )
        }
        item {
            PageSwipeModeSettingRow(
                icon = Icons.Filled.TouchApp,
                iconColor = Color(0xFF00B8A9),
                pageSwipeMode = pageSwipeMode,
                onPageSwipeModeChange = onPageSwipeModeChange
            )
        }
        item {
            ConsoleSwitchRow(
                icon = Icons.Filled.Refresh,
                iconColor = Color(0xFF78B83B),
                title = stringResource(R.string.settings_page_wrap),
                subtitle = stringResource(R.string.settings_page_wrap_desc),
                checked = infinitePageSwipe,
                onCheckedChange = onInfinitePageSwipeChange
            )
        }
        item {
            ConsoleSwitchRow(
                icon = Icons.Filled.PlayArrow,
                iconColor = Color(0xFFE47B17),
                title = stringResource(R.string.settings_page_animation),
                subtitle = stringResource(R.string.settings_page_animation_desc),
                checked = pageSwipeAnimation,
                onCheckedChange = onPageSwipeAnimationChange
            )
        }
        item {
            VibrationSettingRow(
                buttonVibrationLevel = buttonVibrationLevel,
                onButtonVibrationLevelChange = onButtonVibrationLevelChange
            )
        }
        item {
            PageSummaryRow(
                pageName = pageName,
                pageCount = pageCount,
                activeIndex = deckPages.indexOfFirst { it.id == activePageId }.coerceAtLeast(0),
                columns = null,
                rows = null,
                onAddPage = onAddPage
            )
        }
        item {
            SettingsDiagnosticsCard(logs)
        }
        item {
            SettingsAppInfoRow(mode = DeckUiMode.Console)
        }
    }
}

@Composable
private fun SettingsDetailContent(
    mode: DeckUiMode,
    accent: Color,
    icon: ImageVector,
    title: String,
    subtitle: String,
    content: LazyListScope.() -> Unit
) {
    val colors = deckThemeColors(mode)
    val listState = rememberLazyListState()
    var showDragHint by remember(mode) { mutableStateOf(true) }
    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            showDragHint = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 14.dp, top = 14.dp, end = 14.dp, bottom = 34.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SettingsIconTile(icon, accent)
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                }
            }
            content()
        }
        AnimatedVisibility(
            visible = showDragHint,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            enter = fadeIn(animationSpec = tween(160)) + slideInVertically { it / 2 },
            exit = fadeOut(animationSpec = tween(160)) + slideOutVertically { it },
            label = "settingsDragHint"
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                color = colors.cardBackground.copy(alpha = if (isSystemInDarkTheme()) 0.78f else 0.86f),
                contentColor = colors.textSecondary,
                shape = RoundedCornerShape(999.dp),
                tonalElevation = 0.dp,
                shadowElevation = 6.dp
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    text = stringResource(R.string.settings_drag_more),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun SettingsAppInfoRow(mode: DeckUiMode) {
    val colors = deckThemeColors(mode)
    val accent = settingsModeAccent(mode)
    SettingsCard(accent = accent, themeColors = colors) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SettingsIconTile(Icons.Filled.Info, accent)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stringResource(R.string.settings_app_info),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Text(
                    text = "${stringResource(R.string.app_name)} ${BuildConfig.VERSION_NAME}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun SettingsPreviewCard(
    title: String,
    content: @Composable () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    SettingsCard {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary
        )
        content()
        PageIndicator(
            modifier = Modifier.fillMaxWidth(),
            pageCount = 2,
            activeIndex = 0
        )
    }
}

@Composable
private fun ClassicLayoutControlsCard(
    columns: Int,
    rows: Int,
    spacing: Int,
    deckPages: List<DeckPageConfig>,
    activePageId: Int,
    pageName: String,
    pageCount: Int,
    pageSwipeAxis: PageSwipeAxis,
    pageSwipeMode: PageSwipeMode,
    pageSwipeAnimation: Boolean,
    infinitePageSwipe: Boolean,
    onColumnsChange: (Int) -> Unit,
    onRowsChange: (Int) -> Unit,
    onSpacingChange: (Int) -> Unit,
    onPageSwipeAxisChange: (PageSwipeAxis) -> Unit,
    onPageSwipeModeChange: (PageSwipeMode) -> Unit,
    onPageSwipeAnimationChange: (Boolean) -> Unit,
    onInfinitePageSwipeChange: (Boolean) -> Unit,
    onAddPage: () -> Unit
) {
    var editing by remember { mutableStateOf(false) }
    ClassicConceptSectionCard(
        icon = Icons.Filled.GridView,
        title = stringResource(R.string.deck_layout),
        subtitle = "${stringResource(R.string.settings_layout_preview)} - ${columns}x$rows - ${stringResource(R.string.spacing)} $spacing",
        accent = ClassicLayoutAccent,
        secondaryAccent = ClassicLayoutSecondaryAccent,
        trailing = {
            SettingsCycleButton(
                text = stringResource(if (editing) R.string.save else R.string.edit),
                accent = ClassicLayoutAccent,
                onClick = { editing = !editing }
            )
        }
    ) {
        ClassicDeviceLayoutPreview(
            columns = columns,
            rows = rows,
            spacing = spacing,
            editing = editing,
            onColumnsChange = onColumnsChange,
            onRowsChange = onRowsChange,
            onSpacingChange = onSpacingChange
        )
        ClassicSettingsControlRow(
            icon = Icons.Filled.SwapHoriz,
            iconColor = ClassicLayoutAccent,
            title = stringResource(R.string.settings_page_direction),
            subtitle = stringResource(R.string.settings_page_direction_desc),
            trailing = {
                SettingsSegmentedControl(
                    options = PageSwipeAxis.values().toList(),
                    selected = pageSwipeAxis,
                    label = { stringResource(it.shortLabelRes) },
                    accent = ClassicLayoutAccent,
                    onSelected = onPageSwipeAxisChange
                )
            }
        )
        ClassicSettingsControlRow(
            icon = Icons.Filled.TouchApp,
            iconColor = ClassicLayoutAccent,
            title = stringResource(R.string.settings_page_swipe_mode),
            subtitle = stringResource(R.string.settings_page_swipe_mode_desc),
            trailing = {
                SettingsSegmentedControl(
                    options = PageSwipeMode.values().toList(),
                    selected = pageSwipeMode,
                    label = { stringResource(it.labelRes) },
                    accent = ClassicLayoutAccent,
                    onSelected = onPageSwipeModeChange
                )
            }
        )
        ClassicSettingsControlRow(
            icon = Icons.Filled.Refresh,
            iconColor = ClassicLayoutAccent,
            title = stringResource(R.string.settings_page_wrap),
            subtitle = stringResource(R.string.settings_page_wrap_desc),
            trailing = {
                SettingsSwitch(
                    checked = infinitePageSwipe,
                    accent = ClassicLayoutAccent,
                    onCheckedChange = onInfinitePageSwipeChange
                )
            }
        )
        ClassicSettingsControlRow(
            icon = Icons.Filled.PlayArrow,
            iconColor = ClassicLayoutAccent,
            title = stringResource(R.string.settings_page_animation),
            subtitle = stringResource(R.string.settings_page_animation_desc),
            trailing = {
                SettingsSwitch(
                    checked = pageSwipeAnimation,
                    accent = ClassicLayoutAccent,
                    onCheckedChange = onPageSwipeAnimationChange
                )
            }
        )
        ClassicSettingsControlRow(
            icon = Icons.Filled.Apps,
            iconColor = ClassicLayoutAccent,
            title = stringResource(R.string.add_page_count, pageCount, MAX_PAGES),
            subtitle = pageLayoutSummary(
                pageName = pageName,
                pageCount = pageCount,
                columns = columns,
                rows = rows
            ),
            trailing = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PageIndicator(
                        pageCount = pageCount,
                        activeIndex = deckPages.indexOfFirst { it.id == activePageId }.coerceAtLeast(0)
                    )
                    Button(
                        enabled = pageCount < MAX_PAGES,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ClassicLayoutAccent,
                            contentColor = Color.White
                        ),
                        onClick = onAddPage
                    ) {
                        Text(stringResource(R.string.add_page))
                    }
                }
            }
        )
    }
}

@Composable
private fun ClassicButtonSettingsCard(
    onLayoutEditor: () -> Unit,
    buttonVibrationLevel: ButtonVibrationLevel,
    onButtonVibrationLevelChange: (ButtonVibrationLevel) -> Unit
) {
    ClassicConceptSectionCard(
        icon = Icons.Filled.TouchApp,
        title = stringResource(R.string.button_editor),
        subtitle = stringResource(R.string.settings_classic_layout_desc),
        accent = ClassicButtonAccent,
        secondaryAccent = ClassicButtonSecondaryAccent
    ) {
        ClassicSettingsActionRow(
            icon = Icons.Filled.GridView,
            iconColor = ClassicButtonAccent,
            title = stringResource(R.string.button_editor),
            subtitle = stringResource(R.string.settings_classic_layout_desc),
            buttonText = stringResource(R.string.button_editor),
            onClick = onLayoutEditor
        )
        ClassicSettingsControlRow(
            icon = Icons.Filled.Vibration,
            iconColor = ClassicButtonAccent,
            title = stringResource(R.string.settings_vibration),
            subtitle = stringResource(R.string.settings_vibration_desc),
            trailing = {
                SettingsCycleButton(
                    text = stringResource(buttonVibrationLevel.shortLabelRes),
                    accent = ClassicButtonAccent,
                    onClick = { onButtonVibrationLevelChange(buttonVibrationLevel.next()) }
                )
            }
        )
    }
}

@Composable
private fun ClassicConceptSectionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accent: Color,
    secondaryAccent: Color,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = LocalDeckThemeColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.cardBackground)
            .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            accent.copy(alpha = if (isSystemInDarkTheme()) 0.5f else 0.18f),
                            secondaryAccent.copy(alpha = if (isSystemInDarkTheme()) 0.34f else 0.1f)
                        )
                    )
                )
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SettingsIconTile(icon, accent)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            trailing?.invoke()
        }
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = content
        )
    }
}

@Composable
private fun ClassicSettingsActionRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    buttonText: String,
    onClick: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colors.toggleBackground.copy(alpha = 0.54f),
        contentColor = colors.textPrimary,
        shape = RoundedCornerShape(8.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .border(1.dp, iconColor.copy(alpha = 0.22f), RoundedCornerShape(8.dp))
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SettingsIconTile(icon, iconColor)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            SettingsCycleButton(text = buttonText, accent = iconColor, onClick = onClick)
        }
    }
}

@Composable
private fun ClassicSettingsControlRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.toggleBackground.copy(alpha = 0.54f))
            .border(1.dp, iconColor.copy(alpha = 0.22f), RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SettingsIconTile(icon, iconColor)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        trailing()
    }
}

@Composable
private fun ClassicDeviceLayoutPreview(
    columns: Int,
    rows: Int,
    spacing: Int,
    editing: Boolean,
    onColumnsChange: (Int) -> Unit,
    onRowsChange: (Int) -> Unit,
    onSpacingChange: (Int) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val configuration = LocalConfiguration.current
    val rawRatio = configuration.screenWidthDp.toFloat() / configuration.screenHeightDp.coerceAtLeast(1).toFloat()
    val deviceRatio = if (rawRatio >= 1f) rawRatio else 1f / rawRatio
    val columnAccent = Color(0xFF0B7FE8)
    val rowAccent = Color(0xFF00A6A6)
    val spacingAccent = Color(0xFFE47B17)

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val previewHeight = (maxWidth / deviceRatio).coerceIn(150.dp, 260.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(previewHeight)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.consolePreviewBackground)
                .border(1.dp, colors.cardBorder, RoundedCornerShape(8.dp))
                .padding(6.dp)
        ) {
            ClassicPreviewGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = if (editing) 34.dp else 0.dp,
                        end = if (editing) 76.dp else 0.dp
                    ),
                columns = columns,
                rows = rows,
                spacing = spacing
            )
            AnimatedVisibility(
                visible = editing,
                modifier = Modifier.align(Alignment.TopStart),
                enter = fadeIn(tween(140)) + slideInVertically(tween(180)) { -it / 2 },
                exit = fadeOut(tween(100)) + slideOutVertically(tween(140)) { -it / 2 },
                label = "classicLayoutSliders"
            ) {
                MiniHorizontalLayoutSlider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .padding(end = 80.dp),
                    label = stringResource(R.string.columns),
                    value = columns,
                    range = MIN_COLUMNS..MAX_COLUMNS,
                    accent = columnAccent,
                    onValueChange = onColumnsChange
                )
            }
            AnimatedVisibility(
                visible = editing,
                modifier = Modifier.align(Alignment.CenterEnd),
                enter = fadeIn(tween(140)) + slideInHorizontally(tween(180)) { it / 2 },
                exit = fadeOut(tween(100)) + slideOutHorizontally(tween(140)) { it / 2 },
                label = "classicLayoutVerticalSliders"
            ) {
                Row(
                    modifier = Modifier
                        .width(68.dp)
                        .fillMaxHeight()
                        .padding(top = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End)
                ) {
                    MiniVerticalLayoutSlider(
                        modifier = Modifier
                            .width(32.dp)
                            .fillMaxHeight(),
                        label = stringResource(R.string.rows),
                        value = rows,
                        range = MIN_ROWS..MAX_ROWS,
                        accent = rowAccent,
                        onValueChange = onRowsChange
                    )
                    MiniVerticalLayoutSlider(
                        modifier = Modifier
                            .width(32.dp)
                            .fillMaxHeight(),
                        label = stringResource(R.string.spacing),
                        value = spacing,
                        range = MIN_SPACING_DP..MAX_SPACING_DP,
                        accent = spacingAccent,
                        onValueChange = onSpacingChange
                    )
                }
            }
        }
    }
}

@Composable
private fun ClassicPreviewGrid(
    modifier: Modifier = Modifier,
    columns: Int,
    rows: Int,
    spacing: Int
) {
    val colors = LocalDeckThemeColors.current
    val safeColumns = columns.coerceIn(MIN_COLUMNS, MAX_COLUMNS)
    val safeRows = rows.coerceIn(MIN_ROWS, MAX_ROWS)
    val previewSpacing = spacing.coerceIn(MIN_SPACING_DP, MAX_SPACING_DP).dp
    val buttonColors = listOf(
        Color(0xFF7B3EB1),
        Color(0xFF00A6A6),
        Color(0xFF6FA833),
        Color(0xFFE47B17),
        Color(0xFF0B7FE8)
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(previewSpacing)
    ) {
        repeat(safeRows) { rowIndex ->
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(previewSpacing)
            ) {
                repeat(safeColumns) { columnIndex ->
                    val cellIndex = rowIndex * safeColumns + columnIndex
                    val isTitleCell = rowIndex == 0 && columnIndex == 0
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isTitleCell) colors.cardBackground.copy(alpha = 0.35f)
                                else buttonColors[cellIndex % buttonColors.size].copy(alpha = 0.88f)
                            )
                            .border(
                                1.dp,
                                if (isTitleCell) colors.cardBorder else Color.White.copy(alpha = 0.14f),
                                RoundedCornerShape(6.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isTitleCell) {
                            Text(
                                text = stringResource(R.string.deck_title),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        } else if (safeColumns <= 8 && safeRows <= 4) {
                            Icon(
                                imageVector = when (cellIndex % 5) {
                                    0 -> Icons.Filled.PlayArrow
                                    1 -> Icons.Filled.SkipPrevious
                                    2 -> Icons.Filled.SkipNext
                                    3 -> Icons.Filled.VolumeUp
                                    else -> Icons.Filled.GridView
                                },
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.86f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniHorizontalLayoutSlider(
    modifier: Modifier = Modifier,
    label: String,
    value: Int,
    range: IntRange,
    accent: Color,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "$label $value",
            style = MaterialTheme.typography.labelSmall,
            color = LocalDeckThemeColors.current.textSecondary,
            maxLines = 1
        )
        ClassicLayoutDiscreteSlider(
            modifier = Modifier
                .weight(1f)
                .height(30.dp),
            value = value,
            range = range,
            accent = accent,
            onValueChange = onValueChange
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MiniVerticalLayoutSlider(
    modifier: Modifier = Modifier,
    label: String,
    value: Int,
    range: IntRange,
    accent: Color,
    onValueChange: (Int) -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        Text(
            text = "${label.take(1)} $value",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.labelSmall,
            color = LocalDeckThemeColors.current.textSecondary,
            textAlign = TextAlign.End,
            maxLines = 1
        )
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            ClassicLayoutVerticalDiscreteSlider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(24.dp),
                value = value,
                range = range,
                accent = accent,
                onValueChange = onValueChange
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClassicLayoutVerticalDiscreteSlider(
    modifier: Modifier = Modifier,
    value: Int,
    range: IntRange,
    accent: Color,
    onValueChange: (Int) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    var size by remember { mutableStateOf(IntSize.Zero) }
    val fraction = ((value - range.first).toFloat() / (range.last - range.first).coerceAtLeast(1))
        .coerceIn(0f, 1f)
    val tickCount = (range.last - range.first + 1).coerceAtLeast(2)

    fun updateFromY(y: Float) {
        val usableHeight = size.height.coerceAtLeast(1)
        val nextFraction = (1f - (y / usableHeight).coerceIn(0f, 1f)).coerceIn(0f, 1f)
        val next = range.first + (nextFraction * (range.last - range.first)).roundToInt()
        onValueChange(next.coerceIn(range.first, range.last))
    }

    Canvas(
        modifier = modifier
            .onSizeChanged { size = it }
            .pointerInput(range, size) {
                detectTapGestures { offset -> updateFromY(offset.y) }
            }
            .pointerInput(range, size) {
                detectDragGestures { change, _ ->
                    change.consume()
                    updateFromY(change.position.y)
                }
            }
    ) {
        val stroke = 10.dp.toPx()
        val tickRadius = 2.dp.toPx()
        val thumbWidth = 22.dp.toPx()
        val thumbHeight = 3.dp.toPx()
        val x = this.size.width / 2f
        val top = stroke / 2f
        val bottom = this.size.height - stroke / 2f
        val activeY = bottom - (bottom - top) * fraction

        drawLine(
            color = colors.textMuted.copy(alpha = 0.44f),
            start = Offset(x, top),
            end = Offset(x, bottom),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = accent.copy(alpha = 0.78f),
            start = Offset(x, activeY),
            end = Offset(x, bottom),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        repeat(tickCount) { index ->
            val tickFraction = if (tickCount <= 1) 0f else index.toFloat() / (tickCount - 1)
            val y = bottom - (bottom - top) * tickFraction
            drawCircle(
                color = if (tickFraction <= fraction) {
                    colors.textPrimary.copy(alpha = 0.76f)
                } else {
                    colors.textPrimary.copy(alpha = 0.44f)
                },
                radius = tickRadius,
                center = Offset(x, y)
            )
        }
        drawLine(
            color = accent,
            start = Offset(x - thumbWidth / 2f, activeY),
            end = Offset(x + thumbWidth / 2f, activeY),
            strokeWidth = thumbHeight,
            cap = StrokeCap.Round
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClassicLayoutDiscreteSlider(
    modifier: Modifier = Modifier,
    value: Int,
    range: IntRange,
    accent: Color,
    onValueChange: (Int) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val sliderColors = SliderDefaults.colors(
        thumbColor = accent,
        activeTrackColor = accent,
        inactiveTrackColor = colors.textMuted.copy(alpha = 0.42f),
        activeTickColor = colors.textPrimary.copy(alpha = 0.72f),
        inactiveTickColor = colors.textPrimary.copy(alpha = 0.46f)
    )
    Slider(
        modifier = modifier,
        value = value.toFloat(),
        onValueChange = { next ->
            onValueChange(next.roundToInt().coerceIn(range.first, range.last))
        },
        valueRange = range.first.toFloat()..range.last.toFloat(),
        steps = (range.last - range.first - 1).coerceAtLeast(0),
        colors = sliderColors,
        interactionSource = interactionSource,
        thumb = {
            SliderDefaults.Thumb(
                interactionSource = interactionSource,
                colors = sliderColors,
                thumbSize = DpSize(width = 3.dp, height = 22.dp)
            )
        },
        track = { sliderState ->
            ClassicLayoutDiscreteTrack(sliderState = sliderState, accent = accent)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClassicLayoutDiscreteTrack(sliderState: SliderState, accent: Color) {
    val colors = LocalDeckThemeColors.current
    val activeColor = accent.copy(alpha = 0.78f)
    val inactiveColor = colors.textMuted.copy(alpha = 0.44f)
    val activeTickColor = colors.textPrimary.copy(alpha = 0.76f)
    val inactiveTickColor = colors.textPrimary.copy(alpha = 0.44f)
    val fraction = ((sliderState.value - sliderState.valueRange.start) /
        (sliderState.valueRange.endInclusive - sliderState.valueRange.start).coerceAtLeast(1f))
        .coerceIn(0f, 1f)
    val tickCount = sliderState.steps + 2

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(22.dp)
    ) {
        val y = size.height / 2f
        val stroke = 10.dp.toPx()
        val tickRadius = 2.dp.toPx()
        val start = stroke / 2f
        val end = size.width - stroke / 2f
        val activeEnd = start + (end - start) * fraction

        drawLine(
            color = inactiveColor,
            start = Offset(start, y),
            end = Offset(end, y),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        drawLine(
            color = activeColor,
            start = Offset(start, y),
            end = Offset(activeEnd, y),
            strokeWidth = stroke,
            cap = StrokeCap.Round
        )
        repeat(tickCount) { index ->
            val tickFraction = if (tickCount <= 1) 0f else index.toFloat() / (tickCount - 1)
            val x = start + (end - start) * tickFraction
            drawCircle(
                color = if (tickFraction <= fraction) activeTickColor else inactiveTickColor,
                radius = tickRadius,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
private fun ConsoleSettingsPreview(
    panelOptions: ConsolePanelOptions
) {
    val colors = LocalDeckThemeColors.current
    val configuration = LocalConfiguration.current
    val rawRatio = configuration.screenWidthDp.toFloat() / configuration.screenHeightDp.coerceAtLeast(1).toFloat()
    val deviceRatio = if (rawRatio >= 1f) rawRatio else 1f / rawRatio

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val previewHeight = (maxWidth / deviceRatio).coerceIn(126.dp, 220.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(previewHeight)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.consolePreviewBackground)
                .border(1.dp, colors.cardBorder, RoundedCornerShape(8.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .width(96.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.sidebarBackground)
                    .border(1.dp, colors.sidebarBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = stringResource(R.string.console_panel),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                val previewItems = listOf(
                    panelOptions.showConnection,
                    panelOptions.showMessage,
                    panelOptions.showClock,
                    panelOptions.showDate
                ).filter { it }
                repeat(previewItems.size.coerceIn(1, 4)) { index ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (index == 1) 24.dp else 18.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(
                                if (index == 1) Color(0xFF00A6E7).copy(alpha = 0.65f)
                                else colors.cardBackground
                            )
                    )
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(5) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(10.dp))
                                .background(consoleButtonPreviewColor(index))
                        )
                    }
                }
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    repeat(4) { index ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(10.dp))
                                .background(consoleButtonPreviewColor(index + 5).copy(alpha = 0.9f))
                        )
                    }
                }
            }
        }
    }
}

private fun consoleButtonPreviewColor(index: Int): Color {
    return when (index % 5) {
        0 -> Color(0xFF145AA8)
        1 -> Color(0xFF1D2936)
        2 -> Color(0xFF294E25)
        3 -> Color(0xFFB85B00)
        else -> Color(0xFF4D2578)
    }
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    SettingsCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingsIconTile(icon, iconColor)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            trailing()
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    SettingRow(
        icon = icon,
        iconColor = iconColor,
        title = title,
        subtitle = subtitle,
        trailing = {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    )
}

@Composable
private fun ConsoleSettingRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    SettingsCard(accent = iconColor) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingsIconTile(icon, iconColor)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            trailing()
        }
    }
}

@Composable
private fun ConsoleSwitchRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ConsoleSettingRow(
        icon = icon,
        iconColor = iconColor,
        title = title,
        subtitle = subtitle,
        trailing = { Switch(checked = checked, onCheckedChange = onCheckedChange) }
    )
}

@Composable
private fun ConsolePillButton(
    text: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = Color(0xFF00A6E7).copy(alpha = 0.22f),
        contentColor = Color(0xFF76DFFF),
        onClick = onClick
    ) {
        Text(
            modifier = Modifier
                .border(1.dp, Color(0xFF22C5FF).copy(alpha = 0.5f), RoundedCornerShape(999.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ConsolePreviewCard(
    content: @Composable () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    SettingsCard(accent = Color(0xFF00A6E7)) {
        Text(
            text = stringResource(R.string.settings_console_preview),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary
        )
        content()
        PageIndicator(
            modifier = Modifier.fillMaxWidth(),
            pageCount = 2,
            activeIndex = 0
        )
    }
}

@Composable
private fun ConsolePanelOptionsCard(
    options: ConsolePanelOptions,
    onOptionsChange: (ConsolePanelOptions) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    SettingsCard(accent = Color(0xFF00A6E7)) {
        Text(
            text = stringResource(R.string.console_panel),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary
        )
        ConsolePanelOptionRow(
            title = stringResource(R.string.console_panel_connection),
            checked = options.showConnection,
            onCheckedChange = { onOptionsChange(options.copy(showConnection = it)) }
        )
        ConsolePanelOptionRow(
            title = stringResource(R.string.console_panel_message),
            checked = options.showMessage,
            onCheckedChange = { onOptionsChange(options.copy(showMessage = it)) }
        )
        ConsolePanelOptionRow(
            title = stringResource(R.string.console_panel_clock),
            checked = options.showClock,
            onCheckedChange = { onOptionsChange(options.copy(showClock = it)) }
        )
        ConsolePanelOptionRow(
            title = stringResource(R.string.console_panel_date),
            checked = options.showDate,
            onCheckedChange = { onOptionsChange(options.copy(showDate = it)) }
        )
    }
}

@Composable
private fun ConsolePanelOptionRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = LocalDeckThemeColors.current.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun PageSwipeModeSettingRow(
    icon: ImageVector,
    iconColor: Color,
    pageSwipeMode: PageSwipeMode,
    onPageSwipeModeChange: (PageSwipeMode) -> Unit
) {
    SettingRow(
        icon = icon,
        iconColor = iconColor,
        title = stringResource(R.string.settings_page_swipe_mode),
        subtitle = stringResource(R.string.settings_page_swipe_mode_desc),
        trailing = {
            SettingsSegmentedControl(
                options = PageSwipeMode.values().toList(),
                selected = pageSwipeMode,
                label = { stringResource(it.labelRes) },
                onSelected = onPageSwipeModeChange
            )
        }
    )
}

@Composable
private fun VibrationSettingRow(
    buttonVibrationLevel: ButtonVibrationLevel,
    onButtonVibrationLevelChange: (ButtonVibrationLevel) -> Unit
) {
    SettingRow(
        icon = Icons.Filled.Vibration,
        iconColor = Color(0xFFE47B17),
        title = stringResource(R.string.settings_vibration),
        subtitle = stringResource(R.string.settings_vibration_desc),
        trailing = {
            SettingsCycleButton(
                text = stringResource(buttonVibrationLevel.shortLabelRes),
                onClick = { onButtonVibrationLevelChange(buttonVibrationLevel.next()) }
            )
        }
    )
}

@Composable
private fun PageSummaryRow(
    pageName: String,
    pageCount: Int,
    activeIndex: Int,
    columns: Int?,
    rows: Int?,
    onAddPage: () -> Unit
) {
    val layoutSummary = pageLayoutSummary(pageName, pageCount, columns, rows)
    SettingRow(
        icon = Icons.Filled.Apps,
        iconColor = Color(0xFF0EA5FF),
        title = stringResource(R.string.add_page_count, pageCount, MAX_PAGES),
        subtitle = layoutSummary,
        trailing = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PageIndicator(pageCount = pageCount, activeIndex = activeIndex)
                Button(
                    enabled = pageCount < MAX_PAGES,
                    shape = RoundedCornerShape(8.dp),
                    onClick = onAddPage
                ) {
                    Text(stringResource(R.string.add_page))
                }
            }
        }
    )
}

@Composable
private fun pageLayoutSummary(
    pageName: String,
    pageCount: Int,
    columns: Int?,
    rows: Int?
): String {
    val pageSummary = stringResource(R.string.page_count_summary, pageName, pageCount)
    return if (columns != null && rows != null) "$pageSummary - ${columns}x$rows" else pageSummary
}

@Composable
private fun SettingsDiagnosticsCard(logs: List<ActivityLog>) {
    val colors = LocalDeckThemeColors.current
    SettingsCard {
        Text(
            text = stringResource(R.string.diagnostics),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary
        )
        if (logs.isEmpty()) {
            Text(
                text = stringResource(R.string.no_actions_yet),
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        } else {
            logs.take(5).forEach { log ->
                Text(
                    text = "${log.buttonTitle} ${log.note}: ${log.payload}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun <T> SettingsSegmentedControl(
    options: List<T>,
    selected: T,
    label: @Composable (T) -> String,
    accent: Color? = null,
    onSelected: (T) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val activeColor = accent ?: MaterialTheme.colorScheme.primary
    Row(
        modifier = Modifier
            .height(38.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(colors.toggleBackground)
            .border(1.dp, colors.cardBorder, RoundedCornerShape(7.dp))
    ) {
        options.forEach { option ->
            val active = option == selected
            Surface(
                color = if (active) activeColor.copy(alpha = if (isSystemInDarkTheme()) 0.86f else 0.78f) else Color.Transparent,
                contentColor = if (active) Color.White else colors.textPrimary,
                onClick = { onSelected(option) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label(option),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (active) Color.White else colors.textSecondary,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsCycleButton(
    text: String,
    accent: Color? = null,
    onClick: () -> Unit
) {
    val activeColor = accent ?: MaterialTheme.colorScheme.primary
    Surface(
        shape = RoundedCornerShape(7.dp),
        color = activeColor.copy(alpha = if (isSystemInDarkTheme()) 0.34f else 0.16f),
        contentColor = activeColor,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .height(38.dp)
                .width(76.dp)
                .border(1.dp, activeColor.copy(alpha = 0.42f), RoundedCornerShape(7.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (isSystemInDarkTheme()) activeColor.copy(alpha = 0.95f) else activeColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SettingsSwitch(
    checked: Boolean,
    accent: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = Color.White,
            checkedTrackColor = accent.copy(alpha = if (isSystemInDarkTheme()) 0.82f else 0.68f),
            checkedBorderColor = accent.copy(alpha = 0.7f),
            uncheckedThumbColor = colors.textSecondary,
            uncheckedTrackColor = colors.toggleBackground,
            uncheckedBorderColor = colors.cardBorder
        )
    )
}

@Composable
private fun SettingsValuePill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Color(0xFF006CAC).copy(alpha = 0.32f))
            .border(1.dp, Color(0xFF22C5FF).copy(alpha = 0.42f), RoundedCornerShape(999.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = if (isSystemInDarkTheme()) Color(0xFF76DFFF) else Color(0xFF005D86)
        )
    }
}

@Composable
private fun SettingsCard(
    modifier: Modifier = Modifier,
    accent: Color = Color(0xFF0EA5FF),
    themeColors: DeckThemeColors? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = themeColors ?: LocalDeckThemeColors.current
    val borderColor by animateColorAsState(
        targetValue = accent.copy(alpha = 0.18f).compositeOver(colors.cardBorder.copy(alpha = 0.5f)),
        label = "settingsCardBorder"
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.cardBackground)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = content
    )
}

@Composable
private fun SettingsIconTile(
    icon: ImageVector,
    color: Color
) {
    val animatedColor by animateColorAsState(color, label = "settingsIconTile")
    val tint = if (animatedColor.luminance() > 0.55f) Color(0xFF153040) else Color.White
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(animatedColor.copy(alpha = 0.9f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(20.dp)
        )
    }
}

private fun settingsModeAccent(mode: DeckUiMode): Color {
    return if (mode == DeckUiMode.Console) Color(0xFF00A6E7) else Color(0xFF0B7FE8)
}

private fun settingsModeSecondaryAccent(mode: DeckUiMode): Color {
    return if (mode == DeckUiMode.Console) Color(0xFF004B78) else Color(0xFF124E91)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeckSettingsPanel(
    deckPages: List<DeckPageConfig>,
    activePageId: Int,
    pageSwipeAxis: PageSwipeAxis,
    pageSwipeMode: PageSwipeMode,
    pageSwipeAnimation: Boolean,
    infinitePageSwipe: Boolean,
    buttonVibrationLevel: ButtonVibrationLevel,
    pageName: String,
    pageCount: Int,
    onPageSwipeAxisChange: (PageSwipeAxis) -> Unit,
    onPageSwipeModeChange: (PageSwipeMode) -> Unit,
    onPageSwipeAnimationChange: (Boolean) -> Unit,
    onInfinitePageSwipeChange: (Boolean) -> Unit,
    onButtonVibrationLevelChange: (ButtonVibrationLevel) -> Unit,
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
                onClick = { onPageSwipeModeChange(pageSwipeMode.next()) }
            ) {
                Text(stringResource(pageSwipeMode.labelRes))
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
                onClick = { onButtonVibrationLevelChange(buttonVibrationLevel.next()) }
            ) {
                Text(stringResource(buttonVibrationLevel.labelRes))
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
private fun ConsoleSettingsPanel(
    deckPages: List<DeckPageConfig>,
    activePageId: Int,
    pageSwipeAxis: PageSwipeAxis,
    pageSwipeMode: PageSwipeMode,
    pageSwipeAnimation: Boolean,
    infinitePageSwipe: Boolean,
    buttonVibrationLevel: ButtonVibrationLevel,
    pageName: String,
    pageCount: Int,
    onPageSwipeAxisChange: (PageSwipeAxis) -> Unit,
    onPageSwipeModeChange: (PageSwipeMode) -> Unit,
    onPageSwipeAnimationChange: (Boolean) -> Unit,
    onInfinitePageSwipeChange: (Boolean) -> Unit,
    onButtonVibrationLevelChange: (ButtonVibrationLevel) -> Unit,
    onConsoleLayoutEditor: () -> Unit,
    onAddPage: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF14202A).copy(alpha = 0.88f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.console_settings),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = stringResource(R.string.page_count_summary, pageName, pageCount),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            PageIndicator(
                modifier = Modifier.fillMaxWidth(),
                pageCount = deckPages.size,
                activeIndex = deckPages.indexOfFirst { it.id == activePageId }.coerceAtLeast(0)
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                onClick = onConsoleLayoutEditor
            ) {
                Text(stringResource(R.string.console_layout_editor))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PageSwipeAxis.values().forEach { axis ->
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        onClick = { onPageSwipeAxisChange(axis) }
                    ) {
                        Text(stringResource(if (pageSwipeAxis == axis) axis.labelRes else axis.shortLabelRes))
                    }
                }
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                onClick = { onPageSwipeModeChange(pageSwipeMode.next()) }
            ) {
                Text(stringResource(pageSwipeMode.labelRes))
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                onClick = { onInfinitePageSwipeChange(!infinitePageSwipe) }
            ) {
                Text(stringResource(if (infinitePageSwipe) R.string.page_wrap_on else R.string.page_wrap_off))
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                onClick = { onPageSwipeAnimationChange(!pageSwipeAnimation) }
            ) {
                Text(stringResource(if (pageSwipeAnimation) R.string.page_swipe_animation_on else R.string.page_swipe_animation_off))
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                onClick = { onButtonVibrationLevelChange(buttonVibrationLevel.next()) }
            ) {
                Text(stringResource(buttonVibrationLevel.labelRes))
            }
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
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
    consoleStyle: Boolean = false,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onMakeDiscoverable: () -> Unit,
    pairedHosts: List<PairedHidHost>,
    onRefreshHosts: () -> Unit,
    onConnectHost: (PairedHidHost) -> Unit
) {
    val primaryText = if (consoleStyle) Color.White else MaterialTheme.colorScheme.onSurface
    val secondaryText = if (consoleStyle) Color.White.copy(alpha = 0.64f) else MaterialTheme.colorScheme.onSurfaceVariant
    val cardColor = if (consoleStyle) Color(0xFF17232D).copy(alpha = 0.9f) else MaterialTheme.colorScheme.surfaceVariant
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
                    fontWeight = FontWeight.Bold,
                    color = primaryText
                )
                Text(
                    text = stringResource(R.string.app_tagline),
                    style = MaterialTheme.typography.bodyMedium,
                    color = secondaryText
                )
            }
            StatusPill(status.state)
        }

        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor)
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
                    color = secondaryText
                )

                Text(
                    text = stringResource(R.string.pairing_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = secondaryText
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.paired_hosts),
                        style = MaterialTheme.typography.labelLarge,
                        color = secondaryText
                    )
                    TextButton(onClick = onRefreshHosts) {
                        Text(stringResource(R.string.refresh))
                    }
                }

                if (pairedHosts.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_paired_hosts),
                        style = MaterialTheme.typography.bodySmall,
                        color = secondaryText
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        pairedHosts.forEach { host ->
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
    spacing: Dp,
    status: HidStatus,
    appWidgetHost: AppWidgetHost?,
    appWidgetManager: AppWidgetManager?,
    pageSwipeAxis: PageSwipeAxis,
    pageSwipeMode: PageSwipeMode,
    pageSwipeAnimation: Boolean,
    pageSwipeDelta: Int,
    pageAnimationSequence: Int,
    onBack: () -> Unit,
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
                colors = buttonEditorOutlinedButtonColors(),
                onClick = onBack
            ) {
                Text(stringResource(R.string.settings_title))
            }
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.button_editor),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            OutlinedButton(
                shape = RoundedCornerShape(8.dp),
                enabled = isFirstPage || deckPages.size > 1,
                colors = buttonEditorOutlinedButtonColors(),
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
                spacing = spacing,
                status = status,
                appWidgetHost = appWidgetHost,
                appWidgetManager = appWidgetManager,
                uiMode = DeckUiMode.Classic,
                consoleLayout = ConsoleLayoutConfig(emptyList()),
                consolePanelOptions = ConsolePanelOptions(),
                previewMode = true,
                pageSwipeAxis = pageSwipeAxis,
                pageSwipeMode = pageSwipeMode,
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
        }
    }
}

@Composable
private fun buttonEditorOutlinedButtonColors() = ButtonDefaults.outlinedButtonColors(
    contentColor = ClassicButtonAccent,
    disabledContentColor = ClassicButtonAccent.copy(alpha = 0.38f)
)

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
            modifier = Modifier
                .weight(1f)
                .height(34.dp),
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
    spacing: Dp = DEFAULT_SPACING_DP.dp,
    status: HidStatus,
    appWidgetHost: AppWidgetHost?,
    appWidgetManager: AppWidgetManager?,
    uiMode: DeckUiMode,
    consoleLayout: ConsoleLayoutConfig,
    consolePanelOptions: ConsolePanelOptions,
    previewMode: Boolean,
    pageSwipeAxis: PageSwipeAxis,
    pageSwipeMode: PageSwipeMode,
    pageSwipeAnimation: Boolean,
    pageSwipeDelta: Int,
    pageAnimationSequence: Int,
    onPageSwipe: (Int) -> Unit,
    onAddPage: () -> Unit,
    onButtonPressed: (DeckButton) -> Unit,
    onButtonTouchStarted: () -> Unit = {},
    onButtonTouchEnded: () -> Unit = {},
    onButtonEdit: (DeckButton) -> Unit,
    onButtonMoved: (DeckButton, Int) -> Unit,
    onEmptySlotPressed: (Int) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val safeColumns = columns.coerceIn(MIN_COLUMNS, MAX_COLUMNS)
        val safeRows = rows.coerceIn(MIN_ROWS, MAX_ROWS)
        val density = LocalDensity.current
        val spacing = spacing.coerceIn(MIN_SPACING_DP.dp, MAX_SPACING_DP.dp)
        val indicatorPadding = PaddingValues(
            start = if (pageSwipeAxis == PageSwipeAxis.Vertical) 12.dp else 0.dp,
            bottom = if (pageSwipeAxis == PageSwipeAxis.Horizontal) 12.dp else 0.dp
        )
        val swipeModifier = Modifier.pageSwipeGesture(
            mode = pageSwipeMode,
            axis = pageSwipeAxis,
            preferChildGestures = previewMode,
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

        if (uiMode == DeckUiMode.Console && !previewMode) {
            ConsoleDeckSurface(
                modifier = Modifier.fillMaxSize(),
                deckPages = deckPages,
                activePageId = activePageId,
                pageSwipeAnimation = pageSwipeAnimation,
                pageSwipeDelta = pageSwipeDelta,
                pageAnimationSequence = pageAnimationSequence,
                pageSwipeMode = pageSwipeMode,
                layout = consoleLayout,
                panelOptions = consolePanelOptions,
                columns = safeColumns,
                rows = safeRows,
                spacing = spacing,
                status = status,
                appWidgetHost = appWidgetHost,
                appWidgetManager = appWidgetManager,
                onPageSwipe = onPageSwipe,
                onButtonPressed = onButtonPressed,
                onButtonTouchStarted = onButtonTouchStarted,
                onButtonTouchEnded = onButtonTouchEnded
            )
            return@BoxWithConstraints
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
                    appWidgetHost = appWidgetHost,
                    appWidgetManager = appWidgetManager,
                    visualMode = DeckUiMode.Classic,
                    previewMode = previewMode,
                    showTitle = target.pageId == deckPages.firstOrNull()?.id,
                    onButtonPressed = onButtonPressed,
                    onButtonTouchStarted = onButtonTouchStarted,
                    onButtonTouchEnded = onButtonTouchEnded,
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
                axis = pageSwipeAxis,
                activeColor = if (previewMode && uiMode == DeckUiMode.Classic) {
                    ClassicButtonAccent
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        }
    }
}

@Composable
private fun PageIndicator(
    modifier: Modifier = Modifier,
    pageCount: Int,
    activeIndex: Int,
    axis: PageSwipeAxis = PageSwipeAxis.Horizontal,
    activeColor: Color = MaterialTheme.colorScheme.primary
) {
    val content: @Composable () -> Unit = {
        repeat(pageCount.coerceIn(1, MAX_PAGES)) { index ->
            PageDot(index == activeIndex, activeColor = activeColor)
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
private fun ConsoleDeckSurface(
    modifier: Modifier = Modifier,
    deckPages: List<DeckPageConfig>,
    activePageId: Int,
    pageSwipeAnimation: Boolean,
    pageSwipeDelta: Int,
    pageAnimationSequence: Int,
    pageSwipeMode: PageSwipeMode,
    layout: ConsoleLayoutConfig,
    panelOptions: ConsolePanelOptions,
    columns: Int,
    rows: Int,
    spacing: Dp,
    status: HidStatus,
    appWidgetHost: AppWidgetHost?,
    appWidgetManager: AppWidgetManager?,
    onPageSwipe: (Int) -> Unit,
    onButtonPressed: (DeckButton) -> Unit,
    onButtonTouchStarted: () -> Unit,
    onButtonTouchEnded: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val settingsButton = remember(deckPages) {
        deckPages.flatMap { it.buttons }.firstOrNull { buttonAppAction(it) == DeckActionType.Settings }
    }
    val swipeModifier = Modifier.pageSwipeGesture(
        mode = pageSwipeMode,
        axis = PageSwipeAxis.Horizontal,
        onPageSwipe = onPageSwipe
    )
    Box(
        modifier = modifier
            .background(
                Brush.linearGradient(colors.backgroundGradient)
            )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ConsoleSidebar(
                modifier = Modifier
                    .width(160.dp)
                    .fillMaxHeight(),
                status = status,
                panelOptions = panelOptions,
                onSettings = { settingsButton?.let(onButtonPressed) }
            )
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .then(swipeModifier)
            ) {
                val density = LocalDensity.current
                val consoleSpacing = maxOf(spacing, 10.dp)
                AnimatedContent(
                    targetState = PageAnimationTarget(activePageId, pageSwipeDelta, pageAnimationSequence),
                    modifier = Modifier.fillMaxSize(),
                    transitionSpec = {
                        if (!pageSwipeAnimation) {
                            fadeIn(tween(0)) togetherWith fadeOut(tween(0))
                        } else {
                            slideInHorizontally { width -> targetState.delta.signOrOne() * width } togetherWith
                                slideOutHorizontally { width -> -targetState.delta.signOrOne() * width }
                        }
                    },
                    label = "consolePageSwipe"
                ) { target ->
                    val targetButtons = deckPages.firstOrNull { it.id == target.pageId }?.buttons.orEmpty()
                    val consoleRows = consoleLayoutRows(
                        layout = layout,
                        buttons = targetButtons.filterNot { buttonAppAction(it) == DeckActionType.Settings },
                        columns = columns,
                        rows = rows
                    )
                    val rowHeight = with(density) {
                        val spacingPx = consoleSpacing.toPx()
                        val rowCount = consoleRows.size.coerceAtLeast(1)
                        ((constraints.maxHeight - 12.dp.toPx() - spacingPx * (rowCount - 1)) / rowCount).toDp()
                    }
                    ConsoleButtonRows(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 12.dp),
                        rows = consoleRows,
                        rowHeight = rowHeight,
                        spacing = consoleSpacing,
                        status = status,
                        appWidgetHost = appWidgetHost,
                        appWidgetManager = appWidgetManager,
                        onButtonPressed = onButtonPressed,
                        onButtonTouchStarted = onButtonTouchStarted,
                        onButtonTouchEnded = onButtonTouchEnded
                    )
                }
                PageIndicator(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 2.dp),
                    pageCount = deckPages.size,
                    activeIndex = deckPages.indexOfFirst { it.id == activePageId }.coerceAtLeast(0),
                    axis = PageSwipeAxis.Horizontal
                )
            }
        }
    }
}

@Composable
private fun ConsoleButtonRows(
    modifier: Modifier = Modifier,
    rows: List<List<DeckButton>>,
    rowHeight: Dp,
    spacing: Dp,
    status: HidStatus,
    appWidgetHost: AppWidgetHost?,
    appWidgetManager: AppWidgetManager?,
    onButtonPressed: (DeckButton) -> Unit,
    onButtonTouchStarted: () -> Unit,
    onButtonTouchEnded: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        rows.forEach { rowButtons ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(rowHeight)
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                rowButtons.forEach { button ->
                    DeckKey(
                        modifier = Modifier
                            .weight(button.spanColumns.coerceAtLeast(1).toFloat())
                            .fillMaxHeight(),
                        button = button,
                        status = status,
                        appWidgetHost = appWidgetHost,
                        appWidgetManager = appWidgetManager,
                        visualMode = DeckUiMode.Console,
                        enabled = true,
                        previewMode = false,
                        columns = rowButtons.size.coerceAtLeast(1),
                        slot = button.position,
                        cellSize = rowHeight,
                        spacing = spacing,
                        onPressed = { onButtonPressed(button) },
                        onPressFeedback = onButtonTouchStarted,
                        onReleaseFeedback = onButtonTouchEnded,
                        onEdit = {},
                        onMove = {}
                    )
                }
            }
        }
    }
}

private fun consoleLayoutRows(
    layout: ConsoleLayoutConfig,
    buttons: List<DeckButton>,
    columns: Int,
    rows: Int
): List<List<DeckButton>> {
    val buttonById = buttons.associateBy { it.id }
    val configuredRows = layout.rows.map { row ->
        row.mapNotNull { buttonById[it] }
    }.filter { it.isNotEmpty() }
    if (configuredRows.isNotEmpty()) return configuredRows

    return defaultConsoleRows(buttons, columns, rows)
}

private fun defaultConsoleLayout(buttons: List<DeckButton>): ConsoleLayoutConfig {
    val buttonIds = buttons.filterNot { buttonAppAction(it) == DeckActionType.Settings }.associateBy { it.id }
    val defaultRows = defaultConsoleRows(buttonIds.values.toList(), DEFAULT_COLUMNS, DEFAULT_ROWS)
    return ConsoleLayoutConfig(defaultRows.map { row -> row.map { it.id } })
}

private fun defaultConsoleRows(
    buttons: List<DeckButton>,
    columns: Int,
    rows: Int
): List<List<DeckButton>> {
    val media = buttons
        .filter {
            it.actionType == DeckActionType.MediaKey ||
                buttonAppAction(it) == DeckActionType.PreviousPage ||
                buttonAppAction(it) == DeckActionType.NextPage
        }
        .sortedBy { consoleMediaOrder(it) }
    val prominent = buttons
        .filter {
            it.spanColumns > 1 ||
                it.appWidgetId != INVALID_APP_WIDGET_ID ||
                it.actionType == DeckActionType.Utility ||
                it.iconImageUri.isNotBlank()
        }
        .sortedBy { it.position }
    val system = buttons
        .filter { buttonAppAction(it) == DeckActionType.BluetoothStatus }
        .sortedBy { it.position }
    val usedIds = (media + prominent + system).map { it.id }.toSet()
    val regular = buttons
        .filterNot { it.id in usedIds }
        .sortedBy { it.position }

    val bottom = (prominent + system).distinctBy { it.id }
    val rowsList = buildList {
        add(media)
        add(regular)
        add(bottom)
    }.filter { it.isNotEmpty() }

    return if (rowsList.isEmpty()) {
        List(rows.coerceAtLeast(1)) { emptyList() }
    } else {
        rowsList
    }
}

private fun consoleMediaOrder(button: DeckButton): Int {
    val appAction = buttonAppAction(button)
    return when {
        button.actionType == DeckActionType.MediaKey && button.payload == MEDIA_PLAY_PAUSE -> 0
        appAction == DeckActionType.PreviousPage || button.payload == MEDIA_PREVIOUS -> 1
        appAction == DeckActionType.NextPage || button.payload == MEDIA_NEXT -> 2
        button.actionType == DeckActionType.MediaKey && button.payload == MEDIA_VOLUME_UP -> 3
        button.actionType == DeckActionType.MediaKey && button.payload == MEDIA_MUTE -> 4
        button.actionType == DeckActionType.MediaKey && button.payload == MEDIA_VOLUME_DOWN -> 5
        else -> 10 + button.position
    }
}

@Composable
private fun ConsoleSidebar(
    modifier: Modifier = Modifier,
    status: HidStatus,
    panelOptions: ConsolePanelOptions,
    onSettings: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = colors.consoleSidebar,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    maxLines = 2
                )
                if (panelOptions.showConnection) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(statusDotColor(status.state))
                        )
                        Text(
                            text = stringResource(status.state.labelRes()),
                            style = MaterialTheme.typography.labelLarge,
                            color = statusDotColor(status.state)
                        )
                    }
                }
                if (panelOptions.showMessage) {
                    Text(
                        text = status.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (panelOptions.showClock) {
                    Text(
                        text = currentTimeText(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }
                if (panelOptions.showDate) {
                    Text(
                        text = currentDateText(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                }
                OutlinedButton(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(0.dp),
                    onClick = onSettings
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = stringResource(R.string.settings_title),
                        tint = colors.textPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun ConsoleLayoutEditorPage(
    modifier: Modifier = Modifier,
    buttons: List<DeckButton>,
    layout: ConsoleLayoutConfig,
    onBack: () -> Unit,
    onAddRow: () -> Unit,
    onReset: () -> Unit,
    onPickButton: (Int) -> Unit,
    onRemoveButton: (Int, Int) -> Unit,
    onMoveButton: (Int, Int, Int) -> Unit,
    onEditButton: (DeckButton) -> Unit
) {
    val buttonById = buttons.associateBy { it.id }
    val colors = LocalDeckThemeColors.current
    LazyColumn(
        modifier = modifier.background(Brush.linearGradient(colors.backgroundGradient)),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = colors.cardBackground,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ConsolePillButton(text = stringResource(R.string.settings_title), onClick = onBack)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.console_layout_editor),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = stringResource(R.string.settings_console_layout_desc),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary
                        )
                    }
                    ConsolePillButton(text = stringResource(R.string.reset), onClick = onReset)
                    ConsolePillButton(text = stringResource(R.string.add_console_row), onClick = onAddRow)
                }
            }
        }

        val rows = layout.rows.ifEmpty { listOf(emptyList()) }
        items(rows.indices.toList()) { rowIndex ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colors.consolePreviewBackground,
                tonalElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier
                        .border(1.dp, colors.cardBorder, RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.console_row, rowIndex + 1),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                        ConsolePillButton(text = stringResource(R.string.add_button), onClick = { onPickButton(rowIndex) })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rows[rowIndex].mapNotNull { buttonById[it] }.forEachIndexed { index, button ->
                            ConsoleEditorButtonItem(
                                modifier = Modifier.weight(1f),
                                button = button,
                                canMoveLeft = index > 0,
                                canMoveRight = index < rows[rowIndex].lastIndex,
                                onEdit = { onEditButton(button) },
                                onRemove = { onRemoveButton(rowIndex, button.id) },
                                onMoveLeft = { onMoveButton(rowIndex, index, -1) },
                                onMoveRight = { onMoveButton(rowIndex, index, 1) }
                            )
                        }
                        if (rows[rowIndex].isEmpty()) {
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF00A6E7).copy(alpha = 0.13f),
                                onClick = { onPickButton(rowIndex) }
                            ) {
                                Text(
                                    modifier = Modifier.padding(18.dp),
                                    text = stringResource(R.string.add_button),
                                    color = Color(0xFF76DFFF),
                                    textAlign = TextAlign.Center
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
private fun ConsoleEditorButtonItem(
    modifier: Modifier = Modifier,
    button: DeckButton,
    canMoveLeft: Boolean,
    canMoveRight: Boolean,
    onEdit: () -> Unit,
    onRemove: () -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = consoleButtonColor(button),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .border(1.dp, colors.cardBorder, RoundedCornerShape(18.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            materialIconFor(button)?.let { icon ->
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = button.title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                ConsoleMiniButton(enabled = canMoveLeft, onClick = onMoveLeft) {
                    Text("<")
                }
                ConsoleMiniButton(onClick = onEdit) {
                    Text(stringResource(R.string.edit_key))
                }
                ConsoleMiniButton(enabled = canMoveRight, onClick = onMoveRight) {
                    Text(">")
                }
            }
            ConsoleMiniButton(onClick = onRemove) {
                Text(stringResource(R.string.delete))
            }
        }
    }
}

@Composable
private fun ConsoleMiniButton(
    enabled: Boolean = true,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (enabled) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.04f),
        contentColor = if (enabled) Color.White else Color.White.copy(alpha = 0.34f),
        onClick = { if (enabled) onClick() }
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
private fun ConsoleButtonPickerDialog(
    buttons: List<DeckButton>,
    assignedIds: Set<Int>,
    onDismiss: () -> Unit,
    onSelect: (DeckButton) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    AlertDialog(
        modifier = Modifier.fillMaxWidth(0.82f),
        onDismissRequest = onDismiss,
        containerColor = colors.cardBackground,
        titleContentColor = colors.textPrimary,
        textContentColor = colors.textPrimary,
        shape = RoundedCornerShape(14.dp),
        title = { Text(stringResource(R.string.add_button)) },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 360.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(buttons.filterNot { it.id in assignedIds }) { button ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(button.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(
                                    text = button.subtitle.ifBlank { button.payload },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        },
                        onClick = { onSelect(button) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun PageDot(
    active: Boolean,
    activeColor: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = Modifier
            .padding(3.dp)
            .size(if (active) 9.dp else 6.dp)
            .clip(RoundedCornerShape(50))
            .background(
                if (active) {
                    activeColor
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                }
            )
    )
}

private fun Modifier.pageSwipeGesture(
    mode: PageSwipeMode,
    axis: PageSwipeAxis,
    preferChildGestures: Boolean = false,
    onPageSwipe: (Int) -> Unit
): Modifier {
    if (mode == PageSwipeMode.Disabled) return this
    return pointerInput(axis, mode, preferChildGestures) {
        awaitPointerEventScope {
            var tracking = false
            var previousCentroid: Offset? = null
            var totalDrag = Offset.Zero
            var maxPointerCount = 0
            var multiTouchActive = false
            val eventPass = if (preferChildGestures) PointerEventPass.Final else PointerEventPass.Initial

            fun resetTracking() {
                tracking = false
                previousCentroid = null
                totalDrag = Offset.Zero
                maxPointerCount = 0
                multiTouchActive = false
            }

            while (true) {
                val event = awaitPointerEvent(eventPass)
                if (preferChildGestures && event.changes.any { it.isConsumed }) {
                    resetTracking()
                    continue
                }
                val pressed = event.changes.filter { it.pressed }
                if (pressed.isNotEmpty()) {
                    maxPointerCount = maxOf(maxPointerCount, pressed.size)
                    if (mode == PageSwipeMode.MultiTouch && (pressed.size >= 2 || multiTouchActive)) {
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
                    val singleTouchSwipeStarted = mode == PageSwipeMode.SingleTouch &&
                        maxPointerCount == 1 &&
                        when (axis) {
                            PageSwipeAxis.Horizontal -> abs(totalDrag.x) > 24f && abs(totalDrag.x) > abs(totalDrag.y)
                            PageSwipeAxis.Vertical -> abs(totalDrag.y) > 24f && abs(totalDrag.y) > abs(totalDrag.x)
                        }
                    if (singleTouchSwipeStarted) {
                        event.changes.forEach { if (it.pressed) it.consume() }
                    }
                } else if (tracking) {
                    val validSwipe = when (mode) {
                        PageSwipeMode.Disabled -> false
                        PageSwipeMode.SingleTouch -> maxPointerCount == 1
                        PageSwipeMode.MultiTouch -> multiTouchActive && maxPointerCount in 2..3
                    }
                    if (validSwipe) {
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
                            "pageSwipe mode=$mode axis=$axis pointers=$maxPointerCount drag=${totalDrag.x},${totalDrag.y} delta=$pageDelta"
                        )
                        if (pageDelta != 0) {
                            event.changes.forEach { it.consume() }
                            onPageSwipe(pageDelta)
                        }
                    }
                    resetTracking()
                }
            }
        }
    }
}

private fun Modifier.deckTapGesture(
    enabled: Boolean,
    onPress: () -> Unit,
    onRelease: () -> Unit,
    onPressedChange: (Boolean) -> Unit,
    onClick: () -> Unit
): Modifier {
    if (!enabled) return this
    return pointerInput(onPress, onRelease, onClick) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            var totalDrag = Offset.Zero
            var canceled = false
            var pressFeedbackSent = false
            onPressedChange(true)

            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Final)
                val pressedCount = event.changes.count { it.pressed }
                if (event.changes.any { it.isConsumed }) {
                    canceled = true
                }
                if (pressedCount > 1) {
                    canceled = true
                }
                val change = event.changes.firstOrNull { it.id == down.id }
                if (change != null) {
                    totalDrag += change.position - change.previousPosition
                    if (abs(totalDrag.x) > 24f || abs(totalDrag.y) > 24f) {
                        canceled = true
                    }
                }
                if (!canceled && !pressFeedbackSent && pressedCount == 1) {
                    onPress()
                    pressFeedbackSent = true
                }
                if (event.changes.all { it.changedToUp() || !it.pressed }) {
                    break
                }
            }

            onPressedChange(false)
            if (!canceled) {
                if (!pressFeedbackSent) {
                    onPress()
                }
                onClick()
                onRelease()
            }
        }
    }
}

private fun Modifier.deckSlotGesture(
    enabled: Boolean,
    onClick: () -> Unit
): Modifier {
    return deckTapGesture(
        enabled = enabled,
        onPress = {},
        onRelease = {},
        onPressedChange = {},
        onClick = onClick
    )
}

private fun Int.signOrOne(): Int = if (this < 0) -1 else 1

private fun wrapIndex(value: Int, size: Int): Int = ((value % size) + size) % size

private fun slotToButtonPosition(slot: Int, showTitle: Boolean): Int = if (showTitle) slot - 1 else slot

private fun buttonToSlot(button: DeckButton, showTitle: Boolean): Int {
    return if (showTitle) button.position + 1 else button.position
}

private fun DeckButton.effectiveSpanColumns(columns: Int, showTitle: Boolean): Int {
    val column = buttonToSlot(this, showTitle).floorMod(columns.coerceAtLeast(1))
    return spanColumns.coerceIn(1, minOf(MAX_BUTTON_SPAN_COLUMNS, columns - column).coerceAtLeast(1))
}

private fun DeckButton.effectiveSpanRows(columns: Int, rows: Int, showTitle: Boolean): Int {
    val slot = buttonToSlot(this, showTitle)
    val row = slot / columns.coerceAtLeast(1)
    return spanRows.coerceIn(1, minOf(MAX_BUTTON_SPAN_ROWS, rows - row).coerceAtLeast(1))
}

private fun occupiedSlotsForButton(
    button: DeckButton,
    columns: Int,
    rows: Int,
    showTitle: Boolean
): List<Int> {
    val safeColumns = columns.coerceAtLeast(1)
    val safeRows = rows.coerceAtLeast(1)
    val anchorSlot = buttonToSlot(button, showTitle)
    if (anchorSlot !in 0 until safeColumns * safeRows) return emptyList()
    val anchorColumn = anchorSlot % safeColumns
    val anchorRow = anchorSlot / safeColumns
    val spanColumns = button.effectiveSpanColumns(safeColumns, showTitle)
    val spanRows = button.effectiveSpanRows(safeColumns, safeRows, showTitle)
    return buildList {
        repeat(spanRows) { rowOffset ->
            repeat(spanColumns) { columnOffset ->
                add((anchorRow + rowOffset) * safeColumns + anchorColumn + columnOffset)
            }
        }
    }
}

private fun Int.floorMod(divisor: Int): Int = ((this % divisor) + divisor) % divisor

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
    appWidgetHost: AppWidgetHost?,
    appWidgetManager: AppWidgetManager?,
    visualMode: DeckUiMode,
    previewMode: Boolean,
    showTitle: Boolean,
    onButtonPressed: (DeckButton) -> Unit,
    onButtonTouchStarted: () -> Unit,
    onButtonTouchEnded: () -> Unit,
    onButtonEdit: (DeckButton) -> Unit,
    onButtonMoved: (DeckButton, Int) -> Unit,
    onEmptySlotPressed: (Int) -> Unit
) {
    val safeColumns = columns.coerceAtLeast(1)
    val safeRows = rows.coerceAtLeast(1)
    val slotCount = safeColumns * safeRows
    val buttonSlots = buttons.associateBy { buttonToSlot(it, showTitle) }
    val occupiedSlots = buttons.flatMap { occupiedSlotsForButton(it, safeColumns, safeRows, showTitle) }.toSet()
    val gridWidth = cellSize * safeColumns.toFloat() + spacing * (safeColumns - 1).coerceAtLeast(0).toFloat()
    val gridHeight = cellSize * safeRows.toFloat() + spacing * (safeRows - 1).coerceAtLeast(0).toFloat()

    Box(
        modifier = modifier.padding(contentPadding),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.size(gridWidth, gridHeight)) {
            repeat(slotCount) { slot ->
                val columnIndex = slot % safeColumns
                val rowIndex = slot / safeColumns
                val slotModifier = Modifier
                    .offset(
                        x = (cellSize + spacing) * columnIndex.toFloat(),
                        y = (cellSize + spacing) * rowIndex.toFloat()
                    )
                val button = buttonSlots[slot]
                if (showTitle && slot == 0) {
                    TitleDeckSlot(
                        modifier = slotModifier.size(cellSize),
                        status = status
                    )
                } else if (button != null) {
                    val buttonPosition = slotToButtonPosition(slot, showTitle)
                    val maxButtonPosition = slotCount - if (showTitle) 2 else 1
                    val spanColumns = button.effectiveSpanColumns(safeColumns, showTitle)
                    val spanRows = button.effectiveSpanRows(safeColumns, safeRows, showTitle)
                    val buttonWidth = cellSize * spanColumns.toFloat() + spacing * (spanColumns - 1).coerceAtLeast(0).toFloat()
                    val buttonHeight = cellSize * spanRows.toFloat() + spacing * (spanRows - 1).coerceAtLeast(0).toFloat()
                    DeckKey(
                        modifier = slotModifier.size(buttonWidth, buttonHeight),
                        button = button,
                        status = status,
                        appWidgetHost = appWidgetHost,
                        appWidgetManager = appWidgetManager,
                        visualMode = visualMode,
                        enabled = true,
                        previewMode = previewMode,
                        columns = safeColumns,
                        slot = buttonPosition,
                        cellSize = cellSize,
                        spacing = spacing,
                        onPressed = { onButtonPressed(button) },
                        onPressFeedback = onButtonTouchStarted,
                        onReleaseFeedback = onButtonTouchEnded,
                        onEdit = { onButtonEdit(button) },
                        onMove = { targetSlot -> onButtonMoved(button, targetSlot.coerceIn(0, maxButtonPosition)) }
                    )
                } else if (slot !in occupiedSlots) {
                        EmptyDeckSlot(
                            modifier = slotModifier.size(cellSize),
                            showAddIcon = previewMode,
                            createOnClick = previewMode,
                            onCreate = { onEmptySlotPressed(slotToButtonPosition(slot, showTitle)) }
                        )
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
            modifier = Modifier.padding(4.dp),
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
                    .clip(CircleShape)
                    .background(statusDotColor(status.state))
            )
        }
    }
}

@Composable
private fun EmptyDeckSlot(
    modifier: Modifier = Modifier,
    showAddIcon: Boolean,
    createOnClick: Boolean,
    onCreate: () -> Unit
) {
    Surface(
        modifier = modifier.deckSlotGesture(
            enabled = true,
            onClick = { if (createOnClick) onCreate() }
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

@Composable
private fun DeckKey(
    modifier: Modifier = Modifier,
    button: DeckButton,
    status: HidStatus,
    appWidgetHost: AppWidgetHost?,
    appWidgetManager: AppWidgetManager?,
    visualMode: DeckUiMode,
    enabled: Boolean,
    previewMode: Boolean,
    columns: Int,
    slot: Int,
    cellSize: Dp,
    spacing: Dp,
    onPressed: () -> Unit,
    onPressFeedback: () -> Unit,
    onReleaseFeedback: () -> Unit,
    onEdit: () -> Unit,
    onMove: (Int) -> Unit
) {
    val isConsole = visualMode == DeckUiMode.Console
    val themeColors = LocalDeckThemeColors.current
    val containerColor = when {
        !enabled -> MaterialTheme.colorScheme.surfaceVariant
        isConsole -> consoleButtonColor(button)
        else -> button.color
    }
    val contentColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurfaceVariant
        isConsole && containerColor.luminance() > 0.5f -> themeColors.textPrimary
        else -> Color.White
    }
    val buttonShape = RoundedCornerShape(if (isConsole) 18.dp else 8.dp)
    val density = LocalDensity.current
    var dragOffset by remember(button.id) { mutableStateOf(Offset.Zero) }
    var touchPressed by remember(button.id) { mutableStateOf(false) }
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
    val displayTitle = buttonDisplayTitle(button)
    val hasWidget = button.appWidgetId != INVALID_APP_WIDGET_ID
    val letWidgetHandleTouch = hasWidget && button.appWidgetTouchable && !previewMode
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
    val clickModifier = when {
        hasWidget || letWidgetHandleTouch -> Modifier
        previewMode -> Modifier.deckSlotGesture(
            enabled = enabled,
            onClick = onPressed
        )
        else -> Modifier.deckTapGesture(
            enabled = enabled,
            onPress = onPressFeedback,
            onRelease = onReleaseFeedback,
            onPressedChange = { touchPressed = it },
            onClick = onPressed
        )
    }
    val surfaceColor = if (touchPressed) {
        Color.White.copy(alpha = if (isConsole) 0.12f else 0.16f).compositeOver(containerColor)
    } else {
        containerColor
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
            .clip(buttonShape)
            .then(clickModifier),
        shape = buttonShape,
        tonalElevation = if (isConsole) 0.dp else 2.dp,
        color = surfaceColor
    ) {
    val showText = if (isConsole) cellSize >= 58.dp else cellSize >= 96.dp
        val showSubtitle = showText
        if (hasWidget) {
            Box(modifier = Modifier.fillMaxSize()) {
                DeckWidgetHost(
                    modifier = Modifier.fillMaxSize(),
                    appWidgetHost = appWidgetHost,
                    appWidgetManager = appWidgetManager,
                    appWidgetId = button.appWidgetId
                )
                if (!button.appWidgetTouchable || previewMode) {
                    val overlayActionModifier = if (previewMode) {
                        Modifier.deckSlotGesture(
                            enabled = enabled,
                            onClick = onEdit
                        )
                    } else {
                        Modifier.deckTapGesture(
                            enabled = enabled,
                            onPress = onPressFeedback,
                            onRelease = onReleaseFeedback,
                            onPressedChange = { touchPressed = it },
                            onClick = onPressed
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(overlayActionModifier),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Text(
                            text = if (button.appWidgetTouchable) button.title else stringResource(R.string.widget_touch_disabled),
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(containerColor.copy(alpha = 0.36f))
                                .padding(horizontal = 5.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = contentColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            return@Surface
        }
        if (!isConsole) {
            ClassicDeckKeyContent(
                button = button,
                status = status,
                contentColor = contentColor,
                cellSize = cellSize
            )
            return@Surface
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isConsole) {
                        Modifier
                            .border(1.dp, themeColors.cardBorder, RoundedCornerShape(18.dp))
                            .padding(10.dp)
                    } else {
                        Modifier.padding(8.dp)
                    }
                ),
            verticalArrangement = if (button.displayMode != DeckDisplayMode.IconAndText || !showText) {
                Arrangement.Center
            } else {
                Arrangement.SpaceBetween
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (button.displayMode != DeckDisplayMode.KeywordOnly || !showText) {
                    DeckButtonIcon(
                        button = button,
                        tint = contentColor,
                        large = button.displayMode == DeckDisplayMode.IconOnly || !showText
                    )
                }
                if ((button.displayMode == DeckDisplayMode.IconAndText || isConsole) && showText) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        if (buttonAppAction(button) == DeckActionType.BluetoothStatus) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(statusDotColor(status.state))
                            )
                        }
                        Text(
                            text = displayTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = contentColor,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else if (button.displayMode == DeckDisplayMode.KeywordOnly && showText) {
                    Text(
                        text = displayTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (showSubtitle) {
                        Text(
                            text = buttonSubtitle(button, status),
                            style = MaterialTheme.typography.labelMedium,
                            color = contentColor.copy(alpha = 0.82f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            if (button.displayMode == DeckDisplayMode.IconAndText && showText && showSubtitle) {
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
private fun ClassicDeckKeyContent(
    button: DeckButton,
    status: HidStatus,
    contentColor: Color,
    cellSize: Dp
) {
    val displayTitle = buttonDisplayTitle(button)
    val showText = cellSize >= 72.dp && button.displayMode != DeckDisplayMode.IconOnly
    val showSubtitle = cellSize >= 86.dp
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp),
        verticalArrangement = if (showText) Arrangement.SpaceBetween else Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.weight(1f, fill = showText),
            contentAlignment = Alignment.Center
        ) {
            if (button.displayMode != DeckDisplayMode.KeywordOnly || !showText) {
                DeckButtonIcon(
                    button = button,
                    tint = contentColor,
                    large = button.displayMode == DeckDisplayMode.IconOnly || !showText
                )
            } else {
                Text(
                    text = displayTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (showText) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (buttonAppAction(button) == DeckActionType.BluetoothStatus) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(statusDotColor(status.state))
                        )
                    }
                    Text(
                        text = displayTitle,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
                if (showSubtitle) {
                    Text(
                        text = buttonSubtitle(button, status),
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor.copy(alpha = 0.82f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun DeckWidgetHost(
    modifier: Modifier = Modifier,
    appWidgetHost: AppWidgetHost?,
    appWidgetManager: AppWidgetManager?,
    appWidgetId: Int
) {
    val context = LocalContext.current
    val providerInfo = remember(appWidgetId, appWidgetManager) { appWidgetManager?.getAppWidgetInfo(appWidgetId) }
    if (providerInfo == null || appWidgetHost == null) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.widget_unavailable),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        return
    }
    AndroidView(
        modifier = modifier,
        factory = {
            appWidgetHost.createView(context, appWidgetId, providerInfo).apply {
                setAppWidget(appWidgetId, providerInfo)
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        update = { hostView ->
            hostView.setAppWidget(appWidgetId, providerInfo)
        }
    )
}

@Composable
private fun buttonDisplayTitle(button: DeckButton): String {
    return when (button.actionType) {
        DeckActionType.Utility -> when (button.payload) {
            UTILITY_TIME -> currentTimeText()
            else -> button.title
        }
        else -> button.title
    }
}

@Composable
private fun buttonSubtitle(button: DeckButton, status: HidStatus): String {
    return when {
        buttonAppAction(button) == DeckActionType.BluetoothStatus -> stringResource(status.state.labelRes())
        button.actionType == DeckActionType.Utility && button.payload == UTILITY_TIME -> currentDateText()
        else -> button.subtitle
    }
}

@Composable
private fun currentTimeText(): String {
    var now by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = Date()
            delay(1000)
        }
    }
    return remember(now) { SimpleDateFormat("HH:mm", Locale.getDefault()).format(now) }
}

@Composable
private fun currentDateText(): String {
    var now by remember { mutableStateOf(Date()) }
    LaunchedEffect(Unit) {
        while (true) {
            now = Date()
            delay(60_000)
        }
    }
    return remember(now) { SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(now) }
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
private fun consoleButtonColor(button: DeckButton): Color {
    val colors = LocalDeckThemeColors.current
    return when {
        button.actionType == DeckActionType.MediaKey &&
            button.payload in setOf(MEDIA_PLAY_PAUSE, MEDIA_VOLUME_UP) -> colors.consoleButtonFeatured
        buttonAppAction(button) == DeckActionType.BluetoothStatus -> colors.consoleButtonSystem
        else -> colors.consoleButtonDefault
    }
}

@Composable
private fun rememberImageBitmap(uriString: String): ImageBitmap? {
    val context = LocalContext.current
    var image by remember(uriString) { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(uriString) {
        image = if (uriString.isBlank()) {
            null
        } else if (uriString.startsWith(APP_ICON_URI_PREFIX)) {
            val packageName = uriString.removePrefix(APP_ICON_URI_PREFIX)
            runCatching {
                context.packageManager.getApplicationIcon(packageName).toBitmap().asImageBitmap()
            }.getOrNull()
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
        DeckActionType.Utility -> Icons.Filled.Apps
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

private fun appIconUri(packageName: String): String = "$APP_ICON_URI_PREFIX$packageName"

private fun loadLaunchableApps(context: Context): List<LaunchableAppChoice> {
    val packageManager = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
    val resolveInfos: List<ResolveInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
    } else {
        @Suppress("DEPRECATION")
        packageManager.queryIntentActivities(intent, 0)
    }
    return resolveInfos
        .mapNotNull { info ->
            val packageName = info.activityInfo?.packageName ?: return@mapNotNull null
            val label = info.loadLabel(packageManager)?.toString()?.takeIf { it.isNotBlank() } ?: packageName
            val icon = runCatching { info.loadIcon(packageManager).toBitmap().asImageBitmap() }.getOrNull()
            LaunchableAppChoice(label, packageName, icon)
        }
        .distinctBy { it.packageName }
        .sortedBy { it.label.lowercase(Locale.getDefault()) }
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

private fun utilityChoices(): List<UtilityChoice> {
    return listOf(
        UtilityChoice(UTILITY_TIME, R.string.utility_time),
        UtilityChoice(UTILITY_WEATHER, R.string.utility_weather)
    )
}

private fun utilityChoice(payload: String): UtilityChoice? {
    return utilityChoices().firstOrNull { it.payload == payload }
}

private fun selectedUtilityChoice(payload: String): UtilityChoice {
    return utilityChoice(payload) ?: utilityChoices().first()
}

private fun runUtilityAction(context: Context, payload: String): Boolean {
    return when (payload) {
        UTILITY_TIME -> true
        UTILITY_WEATHER -> runCatching {
            context.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=weather")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
            true
        }.getOrDefault(false)
        else -> false
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
        "Discoverable request finished." -> stringResource(R.string.status_message_discoverable_finished)
        "Discoverable request canceled in MobileDeck." -> stringResource(R.string.status_message_discoverable_canceled)
        else -> message
    }
}

@Composable
private fun DiagnosticsPanel(
    logs: List<ActivityLog>,
    consoleStyle: Boolean = false
) {
    val primaryText = if (consoleStyle) Color.White else MaterialTheme.colorScheme.onSurface
    val secondaryText = if (consoleStyle) Color.White.copy(alpha = 0.64f) else MaterialTheme.colorScheme.onSurfaceVariant
    val cardColor = if (consoleStyle) Color(0xFF17232D).copy(alpha = 0.9f) else MaterialTheme.colorScheme.surface
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
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
                fontWeight = FontWeight.SemiBold,
                color = primaryText
            )

            if (logs.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_actions_yet),
                    style = MaterialTheme.typography.bodyMedium,
                    color = secondaryText
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
                            color = primaryText,
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
    onPickWidget: (DeckButton) -> Unit,
    onDelete: () -> Unit
) {
    var title by remember(button.id) { mutableStateOf(button.title) }
    var subtitle by remember(button.id) { mutableStateOf(button.subtitle) }
    var icon by remember(button.id) { mutableStateOf(button.icon) }
    var iconImageUri by remember(button.id) { mutableStateOf(button.iconImageUri) }
    var displayMode by remember(button.id) { mutableStateOf(button.displayMode) }
    var payload by remember(button.id) { mutableStateOf(button.payload) }
    var actionType by remember(button.id) { mutableStateOf(button.actionType) }
    var spanColumns by remember(button.id) { mutableStateOf(button.spanColumns) }
    var spanRows by remember(button.id) { mutableStateOf(button.spanRows) }
    var appWidgetId by remember(button.id) { mutableStateOf(button.appWidgetId) }
    var appWidgetTouchable by remember(button.id) { mutableStateOf(button.appWidgetTouchable) }
    var iconMenuExpanded by remember { mutableStateOf(false) }
    var mediaMenuExpanded by remember { mutableStateOf(false) }
    var appCommandMenuExpanded by remember { mutableStateOf(false) }
    var utilityMenuExpanded by remember { mutableStateOf(false) }
    var appPickerVisible by remember { mutableStateOf(false) }
    var actionPanel by remember(button.id) {
        mutableStateOf(editPanelForButton(button))
    }
    val context = LocalContext.current
    val colors = LocalDeckThemeColors.current
    var launchableApps by remember { mutableStateOf<List<LaunchableAppChoice>?>(null) }
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
    LaunchedEffect(appPickerVisible) {
        if (appPickerVisible && launchableApps == null) {
            launchableApps = withContext(Dispatchers.IO) {
                loadLaunchableApps(context.applicationContext)
            }
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
    val selectedUtility = selectedUtilityChoice(payload)
    val actionLocked = buttonAppAction(button) == DeckActionType.Settings
    val canSave = title.isNotBlank() && (!payloadRequired(actionType) || payload.isNotBlank())
    val configuration = LocalConfiguration.current
    val contentMaxHeight = (configuration.screenHeightDp.dp - 152.dp).coerceAtLeast(220.dp)
    val dialogWidthFraction = if (configuration.screenWidthDp >= 900) 0.66f else 0.82f
    fun selectActionPanel(panel: EditActionPanel) {
        actionPanel = panel
        when (panel) {
            EditActionPanel.AppCommand -> {
                actionType = DeckActionType.AppCommand
                if (appCommandAction(payload) == null) payload = DeckActionType.BluetoothStatus.name
            }
            EditActionPanel.KeyboardInput -> {
                actionType = DeckActionType.Hotkey
                if (!payloadRequired(actionType) || payload.isBlank()) payload = "CTRL+F9"
            }
            EditActionPanel.MediaKey -> {
                actionType = DeckActionType.MediaKey
                if (mediaKeyChoice(payload) == null) payload = MEDIA_MUTE
                icon = ICON_AUTO
            }
            EditActionPanel.RunCommand -> {
                actionType = DeckActionType.RunCommand
                if (!payloadRequired(actionType) || payload.isBlank()) payload = "notepad"
            }
            EditActionPanel.Utility -> {
                actionType = DeckActionType.Utility
                if (utilityChoice(payload) == null) payload = UTILITY_TIME
                icon = ICON_AUTO
            }
            EditActionPanel.Widget -> Unit
        }
    }
    fun editedButton(): DeckButton {
        return button.copy(
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
            } else if (actionType == DeckActionType.Utility) {
                selectedUtility.payload
            } else if (payloadRequired(actionType)) {
                payload.trim()
            } else {
                ""
            },
            spanColumns = spanColumns.coerceIn(1, MAX_BUTTON_SPAN_COLUMNS),
            spanRows = spanRows.coerceIn(1, MAX_BUTTON_SPAN_ROWS),
            appWidgetId = appWidgetId,
            appWidgetTouchable = appWidgetTouchable
        )
    }

    if (appPickerVisible) {
        AppIconPickerDialog(
            apps = launchableApps,
            loading = launchableApps == null,
            onDismiss = { appPickerVisible = false },
            onSelect = { app ->
                title = app.label
                subtitle = app.packageName
                icon = ICON_APPS
                iconImageUri = appIconUri(app.packageName)
                appPickerVisible = false
            }
        )
    }

    AlertDialog(
        modifier = Modifier.fillMaxWidth(dialogWidthFraction),
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
        containerColor = colors.cardBackground,
        titleContentColor = colors.textPrimary,
        textContentColor = colors.textPrimary,
        shape = RoundedCornerShape(12.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SettingsIconTile(
                    icon = iconVectorForKey(icon) ?: materialIconFor(button.copy(actionType = actionType, payload = payload)) ?: Icons.Filled.Keyboard,
                    color = ClassicButtonAccent
                )
                Column {
                    Text(
                        text = stringResource(R.string.edit_key),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = title.ifBlank { stringResource(R.string.title) },
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                EditActionPanelSlider(
                    modifier = Modifier.weight(1f),
                    selected = actionPanel,
                    locked = actionLocked,
                    accent = ClassicButtonAccent,
                    onSelected = ::selectActionPanel
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = contentMaxHeight),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    EditDialogSection(title = stringResource(R.string.key_action)) {
                        if (actionPanel == EditActionPanel.MediaKey) {
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
                        if (actionPanel == EditActionPanel.Utility) {
                            ExposedDropdownMenuBox(
                                expanded = utilityMenuExpanded,
                                onExpandedChange = { utilityMenuExpanded = !utilityMenuExpanded }
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    value = stringResource(selectedUtility.labelRes),
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(stringResource(R.string.utility_target)) },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = utilityMenuExpanded)
                                    }
                                )
                                ExposedDropdownMenu(
                                    expanded = utilityMenuExpanded,
                                    onDismissRequest = { utilityMenuExpanded = false }
                                ) {
                                    utilityChoices().forEach { item ->
                                        DropdownMenuItem(
                                            text = { Text(stringResource(item.labelRes)) },
                                            onClick = {
                                                payload = item.payload
                                                icon = ICON_AUTO
                                                utilityMenuExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        if (actionPanel == EditActionPanel.AppCommand) {
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
                        if (buttonAppAction(actionType, payload) == DeckActionType.BluetoothStatus) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(colors.toggleBackground.copy(alpha = 0.5f))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
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
                                    color = colors.textSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        if (actionPanel == EditActionPanel.Widget) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    onClick = { onPickWidget(editedButton()) }
                                ) {
                                    Text(
                                        text = stringResource(if (appWidgetId == INVALID_APP_WIDGET_ID) R.string.pick_widget else R.string.change_widget),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                OutlinedButton(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    enabled = appWidgetId != INVALID_APP_WIDGET_ID,
                                    onClick = { appWidgetId = INVALID_APP_WIDGET_ID }
                                ) {
                                    Text(
                                        text = stringResource(R.string.clear_widget),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                OutlinedButton(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    enabled = appWidgetId != INVALID_APP_WIDGET_ID,
                                    onClick = { appWidgetTouchable = !appWidgetTouchable }
                                ) {
                                    Text(
                                        text = stringResource(if (appWidgetTouchable) R.string.widget_touch_on else R.string.widget_touch_off),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        if (actionPanel != EditActionPanel.AppCommand &&
                            actionPanel != EditActionPanel.MediaKey &&
                            actionPanel != EditActionPanel.Utility &&
                            actionPanel != EditActionPanel.Widget
                        ) {
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
                item {
                    EditDialogSection(title = stringResource(R.string.key_content)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                modifier = Modifier.weight(1f),
                                value = title,
                                onValueChange = { title = it },
                                label = { Text(stringResource(R.string.title)) },
                                singleLine = true
                            )
                            OutlinedTextField(
                                modifier = Modifier.weight(1f),
                                value = subtitle,
                                onValueChange = { subtitle = it },
                                label = { Text(stringResource(R.string.subtitle)) },
                                singleLine = true
                            )
                        }
                    }
                }
                item {
                    EditDialogSection(title = stringResource(R.string.key_appearance)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ExposedDropdownMenuBox(
                                expanded = iconMenuExpanded,
                                onExpandedChange = { iconMenuExpanded = !iconMenuExpanded }
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier
                                        .menuAnchor()
                                        .weight(1f),
                                    value = stringResource(selectedIcon.labelRes),
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(stringResource(R.string.icon)) },
                                    singleLine = true,
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

                            Row(
                                modifier = Modifier.weight(1.2f),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                DeckDisplayMode.values().forEach { mode ->
                                    OutlinedButton(
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        onClick = { displayMode = mode }
                                    ) {
                                        Text(
                                            text = stringResource(mode.labelRes),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SpanStepper(
                                modifier = Modifier.weight(1f),
                                label = stringResource(R.string.span_columns),
                                value = spanColumns,
                                range = 1..MAX_BUTTON_SPAN_COLUMNS,
                                onValueChange = { spanColumns = it }
                            )
                            SpanStepper(
                                modifier = Modifier.weight(1f),
                                label = stringResource(R.string.span_rows),
                                value = spanRows,
                                range = 1..MAX_BUTTON_SPAN_ROWS,
                                onValueChange = { spanRows = it }
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                onClick = { imagePicker.launch(arrayOf("image/*")) }
                            ) {
                                Text(
                                    text = stringResource(if (iconImageUri.isBlank()) R.string.pick_image else R.string.change_image),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            OutlinedButton(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                onClick = {
                                    appPickerVisible = true
                                }
                            ) {
                                Text(
                                    text = stringResource(R.string.pick_app_icon),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                enabled = iconImageUri.isNotBlank(),
                                onClick = { iconImageUri = "" }
                            ) {
                                Text(
                                    text = stringResource(R.string.clear_image),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = canSave,
                onClick = {
                    onSave(editedButton())
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            Row {
                TextButton(
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

@Composable
private fun EditActionPanelSlider(
    modifier: Modifier = Modifier,
    selected: EditActionPanel,
    locked: Boolean,
    accent: Color,
    onSelected: (EditActionPanel) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EditActionPanel.values().forEach { panel ->
            val active = panel == selected
            val enabled = !locked || active
            Surface(
                modifier = Modifier.width(118.dp),
                shape = RoundedCornerShape(8.dp),
                color = if (active) {
                    accent.copy(alpha = if (isSystemInDarkTheme()) 0.36f else 0.18f)
                } else {
                    colors.toggleBackground.copy(alpha = 0.58f)
                },
                contentColor = if (active) accent else colors.textSecondary,
                enabled = enabled,
                onClick = { onSelected(panel) }
            ) {
                Row(
                    modifier = Modifier
                        .border(
                            1.dp,
                            if (active) accent.copy(alpha = 0.56f) else colors.cardBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = editActionPanelIcon(panel),
                        contentDescription = null,
                        tint = if (active) accent else colors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(panel.labelRes),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (active) colors.textPrimary else colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private fun editPanelForButton(button: DeckButton): EditActionPanel {
    if (button.appWidgetId != INVALID_APP_WIDGET_ID) return EditActionPanel.Widget
    return when (buttonAppAction(button) ?: button.actionType) {
        DeckActionType.Settings,
        DeckActionType.BluetoothStatus,
        DeckActionType.PreviousPage,
        DeckActionType.NextPage,
        DeckActionType.AppCommand -> EditActionPanel.AppCommand
        DeckActionType.MediaKey -> EditActionPanel.MediaKey
        DeckActionType.Hotkey,
        DeckActionType.Text -> EditActionPanel.KeyboardInput
        DeckActionType.RunCommand -> EditActionPanel.RunCommand
        DeckActionType.Utility -> EditActionPanel.Utility
    }
}

private fun editActionPanelIcon(panel: EditActionPanel): ImageVector {
    return when (panel) {
        EditActionPanel.AppCommand -> Icons.Filled.Settings
        EditActionPanel.KeyboardInput -> Icons.Filled.Keyboard
        EditActionPanel.Widget -> Icons.Filled.Apps
        EditActionPanel.MediaKey -> Icons.Filled.PlayArrow
        EditActionPanel.RunCommand -> Icons.Filled.Apps
        EditActionPanel.Utility -> Icons.Filled.Apps
    }
}

@Composable
private fun EditDialogSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    SettingsCard(accent = ClassicButtonAccent) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = ClassicButtonAccent
        )
        content()
    }
}

@Composable
private fun SpanStepper(
    modifier: Modifier = Modifier,
    label: String,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "$label $value",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
        OutlinedButton(
            shape = RoundedCornerShape(8.dp),
            enabled = value > range.first,
            onClick = { onValueChange((value - 1).coerceIn(range)) }
        ) {
            Text("-")
        }
        OutlinedButton(
            shape = RoundedCornerShape(8.dp),
            enabled = value < range.last,
            onClick = { onValueChange((value + 1).coerceIn(range)) }
        ) {
            Text("+")
        }
    }
}

@Composable
private fun AppIconPickerDialog(
    apps: List<LaunchableAppChoice>?,
    loading: Boolean,
    onDismiss: () -> Unit,
    onSelect: (LaunchableAppChoice) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    AlertDialog(
        modifier = Modifier.fillMaxWidth(0.84f),
        onDismissRequest = onDismiss,
        containerColor = colors.cardBackground,
        titleContentColor = colors.textPrimary,
        textContentColor = colors.textPrimary,
        shape = RoundedCornerShape(12.dp),
        title = { Text(stringResource(R.string.pick_app_icon)) },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 360.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (loading) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            Text(
                                text = stringResource(R.string.loading_apps),
                                color = colors.textSecondary
                            )
                        }
                    }
                } else if (apps.orEmpty().isEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.no_apps_found),
                            color = colors.textSecondary
                        )
                    }
                } else {
                    items(apps.orEmpty()) { app ->
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    if (app.icon != null) {
                                        Image(
                                            bitmap = app.icon,
                                            contentDescription = null,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    } else {
                                        Box(modifier = Modifier.size(28.dp))
                                    }
                                    Column {
                                        Text(
                                            text = app.label,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = app.packageName,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = colors.textSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            },
                            onClick = { onSelect(app) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
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
        DeckButton(12, "Run", "Win+R", "RUN", "", DeckDisplayMode.IconAndText, DeckActionType.Hotkey, "WIN+R", colors[3]),
        DeckButton(13, "Time", "Android clock", "TIME", "", DeckDisplayMode.KeywordOnly, DeckActionType.Utility, UTILITY_TIME, colors[5], spanColumns = 2),
        DeckButton(14, "Weather", "Open forecast", "WX", "", DeckDisplayMode.IconAndText, DeckActionType.Utility, UTILITY_WEATHER, colors[2], position = 14, spanColumns = 2)
    ).mapIndexed { index, button ->
        if (button.id == 14) button else button.copy(position = index)
    }
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

private fun loadConsoleLayout(context: Context): ConsoleLayoutConfig {
    val raw = context.deckPrefs().getString(PREF_CONSOLE_LAYOUT, null) ?: return ConsoleLayoutConfig(emptyList())
    return runCatching {
        val array = JSONArray(raw)
        ConsoleLayoutConfig(
            rows = List(array.length()) { rowIndex ->
                val row = array.getJSONArray(rowIndex)
                List(row.length()) { index -> row.getInt(index) }
            }
        )
    }.getOrDefault(ConsoleLayoutConfig(emptyList()))
}

private fun saveConsoleLayout(context: Context, layout: ConsoleLayoutConfig) {
    val array = JSONArray()
    layout.rows.forEach { row ->
        val rowArray = JSONArray()
        row.forEach { rowArray.put(it) }
        array.put(rowArray)
    }
    context.deckPrefs().edit().putString(PREF_CONSOLE_LAYOUT, array.toString()).apply()
}

private fun loadConsolePanelOptions(context: Context): ConsolePanelOptions {
    val prefs = context.deckPrefs()
    return ConsolePanelOptions(
        showConnection = prefs.getBoolean(PREF_CONSOLE_PANEL_CONNECTION, true),
        showMessage = prefs.getBoolean(PREF_CONSOLE_PANEL_MESSAGE, true),
        showClock = prefs.getBoolean(PREF_CONSOLE_PANEL_CLOCK, true),
        showDate = prefs.getBoolean(PREF_CONSOLE_PANEL_DATE, true)
    )
}

private fun saveConsolePanelOptions(context: Context, options: ConsolePanelOptions) {
    context.deckPrefs().edit()
        .putBoolean(PREF_CONSOLE_PANEL_CONNECTION, options.showConnection)
        .putBoolean(PREF_CONSOLE_PANEL_MESSAGE, options.showMessage)
        .putBoolean(PREF_CONSOLE_PANEL_CLOCK, options.showClock)
        .putBoolean(PREF_CONSOLE_PANEL_DATE, options.showDate)
        .apply()
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
        .put("spanColumns", button.spanColumns)
        .put("spanRows", button.spanRows)
        .put("appWidgetId", button.appWidgetId)
        .put("appWidgetTouchable", button.appWidgetTouchable)
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
        position = item.optInt("position", fallbackPosition),
        spanColumns = item.optInt("spanColumns", 1).coerceIn(1, MAX_BUTTON_SPAN_COLUMNS),
        spanRows = item.optInt("spanRows", 1).coerceIn(1, MAX_BUTTON_SPAN_ROWS),
        appWidgetId = item.optInt("appWidgetId", INVALID_APP_WIDGET_ID),
        appWidgetTouchable = item.optBoolean("appWidgetTouchable", true)
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

private fun hasSettingsButton(pages: List<DeckPageConfig>): Boolean {
    return pages.any { page -> page.buttons.any { buttonAppAction(it) == DeckActionType.Settings } }
}

private fun restoreSettingsButton(
    pages: List<DeckPageConfig>,
    columns: Int,
    rows: Int
): List<DeckPageConfig> {
    if (hasSettingsButton(pages)) return pages
    val firstPageId = pages.firstOrNull()?.id
    val allButtons = pages.flatMap { it.buttons }

    pages.forEach { page ->
        val showTitle = page.id == firstPageId
        val capacity = pageButtonCapacity(page.id, pages, columns, rows)
        val position = nextOpenPosition(page.buttons, columns, rows, showTitle)
        if (position < capacity) {
            val settingsButton = settingsDeckButton(allButtons, position)
            return pages.map { pageConfig ->
                if (pageConfig.id == page.id) pageConfig.copy(buttons = pageConfig.buttons + settingsButton) else pageConfig
            }
        }
    }

    val lastPage = pages.lastOrNull() ?: return listOf(
        DeckPageConfig(
            id = 1,
            name = "Page 1",
            buttons = listOf(settingsDeckButton(emptyList(), 0))
        )
    )
    val replacementIndex = lastPage.buttons.indices.maxWithOrNull(
        compareBy<Int> { lastPage.buttons[it].position }.thenBy { it }
    )
    val replacementPosition = replacementIndex?.let { lastPage.buttons[it].position } ?: 0
    val settingsButton = settingsDeckButton(allButtons, replacementPosition)
    val replacementButtons = lastPage.buttons.toMutableList().apply {
        if (replacementIndex != null) {
            this[replacementIndex] = settingsButton
        } else {
            add(settingsButton)
        }
    }
    return pages.map { page ->
        if (page.id == lastPage.id) page.copy(buttons = replacementButtons) else page
    }
}

private fun settingsDeckButton(existingButtons: List<DeckButton>, position: Int): DeckButton {
    val colors = defaultDeckColors()
    return DeckButton(
        id = nextDeckButtonId(existingButtons),
        title = "Settings",
        subtitle = "Deck",
        icon = "SET",
        iconImageUri = "",
        displayMode = DeckDisplayMode.IconAndText,
        actionType = DeckActionType.Settings,
        payload = "",
        color = colors[4],
        position = position
    )
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
        DeckActionType.Utility,
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

private fun loadDeckSpacing(context: Context): Int {
    return context.deckPrefs().getInt(PREF_SPACING, DEFAULT_SPACING_DP).coerceIn(MIN_SPACING_DP, MAX_SPACING_DP)
}

private fun saveDeckSpacing(context: Context, spacing: Int) {
    context.deckPrefs().edit().putInt(PREF_SPACING, spacing.coerceIn(MIN_SPACING_DP, MAX_SPACING_DP)).apply()
}

private fun loadPageSwipeAxis(context: Context): PageSwipeAxis {
    return runCatching {
        PageSwipeAxis.valueOf(context.deckPrefs().getString(PREF_PAGE_SWIPE_AXIS, null) ?: PageSwipeAxis.Horizontal.name)
    }.getOrDefault(PageSwipeAxis.Horizontal)
}

private fun savePageSwipeAxis(context: Context, axis: PageSwipeAxis) {
    context.deckPrefs().edit().putString(PREF_PAGE_SWIPE_AXIS, axis.name).apply()
}

private fun loadPageSwipeMode(context: Context): PageSwipeMode {
    return runCatching {
        PageSwipeMode.valueOf(context.deckPrefs().getString(PREF_PAGE_SWIPE_MODE, null) ?: "")
    }.getOrElse {
        if (context.deckPrefs().getBoolean(PREF_MULTI_TOUCH_PAGE_SWIPE, true)) {
            PageSwipeMode.MultiTouch
        } else {
            PageSwipeMode.Disabled
        }
    }
}

private fun savePageSwipeMode(context: Context, mode: PageSwipeMode) {
    context.deckPrefs().edit()
        .putString(PREF_PAGE_SWIPE_MODE, mode.name)
        .putBoolean(PREF_MULTI_TOUCH_PAGE_SWIPE, mode == PageSwipeMode.MultiTouch)
        .apply()
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

private fun loadButtonVibrationLevel(context: Context): ButtonVibrationLevel {
    return runCatching {
        ButtonVibrationLevel.valueOf(
            context.deckPrefs().getString(PREF_BUTTON_VIBRATION_LEVEL, null) ?: ButtonVibrationLevel.Off.name
        )
    }.getOrDefault(ButtonVibrationLevel.Off)
}

private fun saveButtonVibrationLevel(context: Context, level: ButtonVibrationLevel) {
    context.deckPrefs().edit().putString(PREF_BUTTON_VIBRATION_LEVEL, level.name).apply()
}

private fun loadDeckUiMode(context: Context): DeckUiMode {
    return runCatching {
        DeckUiMode.valueOf(context.deckPrefs().getString(PREF_DECK_UI_MODE, null) ?: DeckUiMode.Classic.name)
    }.getOrDefault(DeckUiMode.Classic)
}

private fun saveDeckUiMode(context: Context, mode: DeckUiMode) {
    context.deckPrefs().edit().putString(PREF_DECK_UI_MODE, mode.name).apply()
}

private fun Context.vibrateButtonPress(level: ButtonVibrationLevel) {
    if (level == ButtonVibrationLevel.Off) return
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        getSystemService(VibratorManager::class.java)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        getSystemService(Vibrator::class.java)
    } ?: return
    if (!vibrator.hasVibrator()) return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(level.durationMillis, level.amplitude))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(level.durationMillis)
    }
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

private fun nextOpenPosition(
    buttons: List<DeckButton>,
    columns: Int,
    rows: Int,
    showTitle: Boolean
): Int {
    val capacity = columns * rows - if (showTitle) 1 else 0
    val occupied = buttons.flatMap { occupiedSlotsForButton(it, columns, rows, showTitle) }.toSet()
    return (0 until capacity).firstOrNull { position ->
        val slot = if (showTitle) position + 1 else position
        slot !in occupied
    } ?: capacity
}

private fun canPlaceButton(
    button: DeckButton,
    otherButtons: List<DeckButton>,
    columns: Int,
    rows: Int,
    showTitle: Boolean
): Boolean {
    val slots = occupiedSlotsForButton(button, columns, rows, showTitle).toSet()
    if (slots.isEmpty() || (showTitle && 0 in slots)) return false
    val otherSlots = otherButtons.flatMap { occupiedSlotsForButton(it, columns, rows, showTitle) }.toSet()
    return slots.intersect(otherSlots).isEmpty()
}

private fun shrinkButtonToAvailable(
    button: DeckButton,
    otherButtons: List<DeckButton>,
    columns: Int,
    rows: Int,
    showTitle: Boolean
): DeckButton {
    var candidate = button.copy(
        spanColumns = button.spanColumns.coerceIn(1, MAX_BUTTON_SPAN_COLUMNS),
        spanRows = button.spanRows.coerceIn(1, MAX_BUTTON_SPAN_ROWS)
    )
    candidate = candidate.copy(
        spanColumns = candidate.effectiveSpanColumns(columns, showTitle),
        spanRows = candidate.effectiveSpanRows(columns, rows, showTitle)
    )
    while (!canPlaceButton(candidate, otherButtons, columns, rows, showTitle) &&
        (candidate.spanColumns > 1 || candidate.spanRows > 1)
    ) {
        candidate = if (candidate.spanColumns >= candidate.spanRows && candidate.spanColumns > 1) {
            candidate.copy(spanColumns = candidate.spanColumns - 1)
        } else {
            candidate.copy(spanRows = candidate.spanRows - 1)
        }
    }
    return candidate
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
private const val PREF_SPACING = "spacing"
private const val PREF_PAGE_SWIPE_AXIS = "page_swipe_axis"
private const val PREF_PAGE_SWIPE_MODE = "page_swipe_mode"
private const val PREF_MULTI_TOUCH_PAGE_SWIPE = "multi_touch_page_swipe"
private const val PREF_PAGE_SWIPE_ANIMATION = "page_swipe_animation"
private const val PREF_INFINITE_PAGE_SWIPE = "infinite_page_swipe"
private const val PREF_BUTTON_VIBRATION_LEVEL = "button_vibration_level"
private const val PREF_DECK_UI_MODE = "deck_ui_mode"
private const val PREF_CONSOLE_LAYOUT = "console_layout"
private const val PREF_CONSOLE_PANEL_CONNECTION = "console_panel_connection"
private const val PREF_CONSOLE_PANEL_MESSAGE = "console_panel_message"
private const val PREF_CONSOLE_PANEL_CLOCK = "console_panel_clock"
private const val PREF_CONSOLE_PANEL_DATE = "console_panel_date"
private const val APP_WIDGET_HOST_ID = 4201
private const val INVALID_APP_WIDGET_ID = -1
private const val APP_ICON_URI_PREFIX = "app-icon:"
private const val MAX_PAGES = 5
private const val MIN_COLUMNS = 4
private const val MAX_COLUMNS = 12
private const val DEFAULT_COLUMNS = 6
private const val MIN_ROWS = 2
private const val MAX_ROWS = 6
private const val DEFAULT_ROWS = 3
private const val MAX_BUTTON_SPAN_COLUMNS = 3
private const val MAX_BUTTON_SPAN_ROWS = 2
private const val MIN_SPACING_DP = 2
private const val MAX_SPACING_DP = 16
private const val DEFAULT_SPACING_DP = 8
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
private const val UTILITY_TIME = "TIME"
private const val UTILITY_WEATHER = "WEATHER"

@Preview(showBackground = true)
@Composable
private fun MobileDeckPreview() {
    MobileDeckTheme {
        MobileDeckApp()
    }
}
