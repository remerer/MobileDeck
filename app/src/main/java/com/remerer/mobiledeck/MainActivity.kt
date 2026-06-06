package com.remerer.mobiledeck

import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.BitmapFactory
import android.graphics.Color as AndroidColor
import android.graphics.Movie
import android.graphics.RuntimeShader
import android.graphics.drawable.ColorDrawable
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
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TabletAndroid
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.shadow.Shadow as DropShadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
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
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(ColorDrawable(AndroidColor.BLACK))
        window.decorView.setBackgroundColor(AndroidColor.BLACK)
        enableEdgeToEdge()
        window.statusBarColor = AndroidColor.TRANSPARENT
        window.navigationBarColor = AndroidColor.TRANSPARENT
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

@Composable
private fun MobileDeckApp() {
    val context = LocalContext.current
    val isPreview = LocalInspectionMode.current
    val darkTheme = isSystemInDarkTheme()
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
    var pendingDiscoverableAfterKeyboardRegistration by remember { mutableStateOf(false) }
    var discoverableFinishedMessage by remember { mutableStateOf("Discoverable request finished.") }
    val discoverableLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val durationSeconds = result.resultCode.takeIf { it > 0 } ?: 0
        if (durationSeconds > 0) {
            pairingDiscoverable = true
            pairingDiscoverableUntilMillis = System.currentTimeMillis() + durationSeconds * 1000L
            hidStatus = hidStatus.copy(
                message = "Discoverable mode is active. Pair from the PC while the Bluetooth keyboard is registered."
            )
        } else {
            pairingDiscoverable = false
            pairingDiscoverableUntilMillis = null
            hidStatus = hidStatus.copy(
                message = "Discoverable request was canceled."
            )
        }
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
            BluetoothPermissionAction.MakeDiscoverable -> allGranted(
                (HidKeyboardManager.HID_BLUETOOTH_PERMISSIONS + HidKeyboardManager.DISCOVERABLE_BLUETOOTH_PERMISSIONS)
                    .distinct()
                    .toTypedArray()
            )
            BluetoothPermissionAction.RegisterHid,
            null -> allGranted(HidKeyboardManager.HID_BLUETOOTH_PERMISSIONS)
        }

        if (granted) {
            when (pendingBluetoothPermissionAction) {
                BluetoothPermissionAction.MakeDiscoverable -> {
                    if (hidStatus.state == HidConnectionState.Registered || hidStatus.state == HidConnectionState.Connected) {
                        launchDiscoverableRequest()
                    } else {
                        pendingDiscoverableAfterKeyboardRegistration = true
                        hidManager.start()
                        pairedHosts = hidManager.pairedHosts()
                    }
                }
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
    var classicSolidButtonBackground by remember { mutableStateOf(loadClassicSolidButtonBackground(context)) }
    var classicDeckBackground by remember { mutableStateOf(loadClassicDeckBackground(context)) }
    var deckUiMode by remember { mutableStateOf(loadDeckUiMode(context)) }
    var classicFontSize by remember { mutableStateOf(loadClassicFontSizeOption(context)) }
    var consoleFontSize by remember { mutableStateOf(loadConsoleFontSizeOption(context)) }
    val initialConsoleLayouts = remember { loadConsoleLayouts(context) }
    var consoleLayouts by remember { mutableStateOf(initialConsoleLayouts) }
    var consoleLayout by remember { mutableStateOf(initialConsoleLayouts[activeDeckPageId] ?: loadConsoleLayout(context)) }
    var consolePanelOptions by remember { mutableStateOf(loadConsolePanelOptions(context)) }
    var lastPageDelta by remember { mutableStateOf(1) }
    var pageAnimationSequence by remember { mutableStateOf(0) }
    var editingButton by remember { mutableStateOf<DeckButton?>(null) }
    var pendingNewButtonId by remember { mutableStateOf<Int?>(null) }
    var pendingNewButtonCreatedPageId by remember { mutableStateOf<Int?>(null) }
    var consoleLayoutEditorInitialMode by remember { mutableStateOf(ConsoleLayoutEditMode.Layout) }
    var pendingWidgetButtonId by remember { mutableStateOf<Int?>(null) }
    var pendingWidgetId by remember { mutableStateOf<Int?>(null) }
    var pendingExportJson by remember { mutableStateOf<String?>(null) }
    var logs by remember { mutableStateOf(emptyList<ActivityLog>()) }
    var consoleLayoutDiagnostics by remember { mutableStateOf(emptyList<String>()) }
    var page by remember { mutableStateOf(AppPage.Deck) }
    var showClassicTutorial by remember { mutableStateOf(shouldShowClassicTutorial(context)) }
    var classicTutorialStep by remember { mutableStateOf(SettingsTutorialStep.Bluetooth) }
    var confirmSettingsButtonRestore by remember { mutableStateOf(false) }
    var debugKeepScreenOn by remember { mutableStateOf(false) }
    var confirmEmptyPageDeletePageId by remember { mutableStateOf<Int?>(null) }
    val activeDeckPage = deckPages.firstOrNull { it.id == activeDeckPageId } ?: deckPages.first()
    val deckButtons = activeDeckPage.buttons

    fun addConsoleLayoutDiagnostic(message: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        consoleLayoutDiagnostics = (listOf("$time  $message") + consoleLayoutDiagnostics).take(8)
    }

    LaunchedEffect(pairingDiscoverableUntilMillis) {
        val until = pairingDiscoverableUntilMillis ?: return@LaunchedEffect
        delay((until - System.currentTimeMillis()).coerceAtLeast(0L))
        if (pairingDiscoverableUntilMillis == until) {
            pairingDiscoverable = false
            pairingDiscoverableUntilMillis = null
            hidStatus = hidStatus.copy(message = discoverableFinishedMessage)
        }
    }

    LaunchedEffect(showClassicTutorial, classicTutorialStep, deckPages) {
        if (showClassicTutorial) {
            if (deckUiMode != DeckUiMode.Classic) {
                deckUiMode = DeckUiMode.Classic
                saveDeckUiMode(context, DeckUiMode.Classic)
            }
            if (classicTutorialStep == SettingsTutorialStep.DeckSettingsButton) {
                val settingsPage = deckPages.firstOrNull { pageConfig ->
                    pageConfig.buttons.any { buttonAppAction(it) == DeckActionType.Settings }
                }
                activeDeckPageId = settingsPage?.id ?: deckPages.first().id
                page = AppPage.Deck
            } else {
                page = AppPage.Settings
            }
        }
    }

    LaunchedEffect(hidStatus.state, pendingDiscoverableAfterKeyboardRegistration) {
        if (!pendingDiscoverableAfterKeyboardRegistration) return@LaunchedEffect
        when (hidStatus.state) {
            HidConnectionState.Registered,
            HidConnectionState.Connected -> {
                pendingDiscoverableAfterKeyboardRegistration = false
                launchDiscoverableRequest()
            }
            HidConnectionState.Unsupported,
            HidConnectionState.PermissionMissing,
            HidConnectionState.Error -> {
                pendingDiscoverableAfterKeyboardRegistration = false
            }
            HidConnectionState.Disconnected,
            HidConnectionState.Registering -> Unit
        }
    }

    fun updateButtonEverywhere(button: DeckButton) {
        val updatedPages = updateDeckButton(deckPages, button)
        deckPages = updatedPages
        saveDeckPages(context, updatedPages)
    }

    fun assignWidgetToButton(buttonId: Int, widgetId: Int) {
        val info = appWidgetManager?.getAppWidgetInfo(widgetId)
        val sourcePage = deckPages.firstOrNull { pageConfig ->
            pageConfig.buttons.any { it.id == buttonId }
        }
        val pageButtons = sourcePage?.buttons ?: return
        val sourceButton = pageButtons.firstOrNull { it.id == buttonId }
        val showTitle = sourcePage?.id == deckPages.firstOrNull()?.id
        val (widgetColumns, widgetRows) = widgetSpanForProvider(info, deckColumns, deckRows)
        val updated = sourceButton
            ?.copy(
                title = info?.label?.takeIf { label -> label.isNotBlank() } ?: "Widget",
                subtitle = "Android widget",
                icon = "W",
                iconImageUri = "",
                displayMode = DeckDisplayMode.IconOnly,
                appWidgetId = widgetId,
                appWidgetTouchable = true,
                spanColumns = widgetColumns,
                spanRows = widgetRows
            ) ?: return
        val adjusted = placeFixedSpanButton(
            button = updated,
            otherButtons = pageButtons.filterNot { it.id == updated.id },
            columns = deckColumns,
            rows = deckRows,
            showTitle = showTitle
        )
        updateButtonEverywhere(adjusted)
        editingButton = adjusted
    }

    val classicBackgroundImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val updated = classicDeckBackground.copy(
            type = ClassicDeckBackgroundType.Image,
            imageUri = uri.toString()
        )
        classicDeckBackground = updated
        saveClassicDeckBackground(context, updated)
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
                    "Android ${Build.VERSION.RELEASE} can be discoverable for pairing, but Bluetooth keyboard mode requires Android 9 or newer."
                )
                launchDiscoverableRequest(
                    finishedMessage = "Pairing mode finished. Bluetooth keyboard mode still requires Android 9 or newer."
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
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            hidStatus = HidStatus(
                HidConnectionState.Unsupported,
                "Android ${Build.VERSION.RELEASE} can be discoverable for pairing, but Bluetooth keyboard mode requires Android 9 or newer."
            )
            launchDiscoverableRequest(
                finishedMessage = "Pairing mode finished. Bluetooth keyboard mode still requires Android 9 or newer."
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            (!hidManager.hasRequiredPermissions() || !hidManager.hasDiscoverablePermissions())
        ) {
            pairingDiscoverable = true
            pendingBluetoothPermissionAction = BluetoothPermissionAction.MakeDiscoverable
            permissionLauncher.launch(
                (HidKeyboardManager.HID_BLUETOOTH_PERMISSIONS + HidKeyboardManager.DISCOVERABLE_BLUETOOTH_PERMISSIONS)
                    .distinct()
                    .toTypedArray()
            )
        } else if (hidStatus.state == HidConnectionState.Registered || hidStatus.state == HidConnectionState.Connected) {
            launchDiscoverableRequest()
        } else {
            pendingDiscoverableAfterKeyboardRegistration = true
            hidManager.start()
            pairedHosts = hidManager.pairedHosts()
        }
    }

    fun cancelDiscoverable() {
        pairingDiscoverable = false
        pairingDiscoverableUntilMillis = null
        pendingDiscoverableAfterKeyboardRegistration = false
        if (pendingBluetoothPermissionAction == BluetoothPermissionAction.MakeDiscoverable) {
            pendingBluetoothPermissionAction = null
        }
        hidStatus = hidStatus.copy(
            message = "Discoverable request canceled in MobileDeck."
        )
    }

    fun currentDeckBundleSnapshot(): DeckBundleSnapshot {
        return DeckBundleSnapshot(
            pages = deckPages,
            consoleLayouts = consoleLayouts + (activeDeckPageId to consoleLayout),
            columns = deckColumns,
            rows = deckRows,
            spacing = deckSpacing,
            pageSwipeAxis = pageSwipeAxis,
            pageSwipeMode = pageSwipeMode,
            pageSwipeAnimation = pageSwipeAnimation,
            infinitePageSwipe = infinitePageSwipe,
            buttonVibrationLevel = buttonVibrationLevel,
            classicSolidButtonBackground = classicSolidButtonBackground,
            classicDeckBackground = classicDeckBackground,
            deckUiMode = deckUiMode,
            classicFontSize = classicFontSize,
            consoleFontSize = consoleFontSize,
            consolePanelOptions = consolePanelOptions
        )
    }

    fun applyImportedDeckBundle(imported: ImportedDeckBundle) {
        val importedPages = ensureSettingsButton(imported.pages, darkTheme).ifEmpty {
            defaultDeckPages(darkTheme = darkTheme)
        }
        val importedLayouts = imported.consoleLayouts
            .filterKeys { pageId -> importedPages.any { it.id == pageId } }
        val nextActivePageId = importedPages.first().id
        val nextConsoleLayout = importedLayouts[nextActivePageId] ?: defaultConsoleLayout(importedPages.first().buttons)

        deckPages = importedPages
        activeDeckPageId = nextActivePageId
        deckColumns = imported.columns
        deckRows = imported.rows
        deckSpacing = imported.spacing
        pageSwipeAxis = imported.pageSwipeAxis
        pageSwipeMode = imported.pageSwipeMode
        pageSwipeAnimation = imported.pageSwipeAnimation
        infinitePageSwipe = imported.infinitePageSwipe
        buttonVibrationLevel = imported.buttonVibrationLevel
        classicSolidButtonBackground = imported.classicSolidButtonBackground
        classicDeckBackground = imported.classicDeckBackground
        deckUiMode = imported.deckUiMode
        classicFontSize = imported.classicFontSize
        consoleFontSize = imported.consoleFontSize
        consolePanelOptions = imported.consolePanelOptions
        consoleLayouts = importedLayouts
        consoleLayout = nextConsoleLayout

        saveDeckPages(context, importedPages)
        saveDeckColumns(context, deckColumns)
        saveDeckRows(context, deckRows)
        saveDeckSpacing(context, deckSpacing)
        savePageSwipeAxis(context, pageSwipeAxis)
        savePageSwipeMode(context, pageSwipeMode)
        savePageSwipeAnimation(context, pageSwipeAnimation)
        saveInfinitePageSwipe(context, infinitePageSwipe)
        saveButtonVibrationLevel(context, buttonVibrationLevel)
        saveClassicSolidButtonBackground(context, classicSolidButtonBackground)
        saveClassicDeckBackground(context, classicDeckBackground)
        saveDeckUiMode(context, deckUiMode)
        saveClassicFontSizeOption(context, classicFontSize)
        saveConsoleFontSizeOption(context, consoleFontSize)
        saveConsolePanelOptions(context, consolePanelOptions)
        saveConsoleLayouts(context, importedLayouts)
        saveConsoleLayout(context, nextConsoleLayout)
    }

    val exportDeckBundleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        val json = pendingExportJson
        pendingExportJson = null
        if (uri == null || json == null) return@rememberLauncherForActivityResult
        runCatching {
            context.contentResolver.openOutputStream(uri)?.use { output ->
                output.write(json.toByteArray(Charsets.UTF_8))
            }
        }.onFailure { error ->
            Log.e("MobileDeck", "Failed to export deck bundle", error)
        }
    }

    val importDeckBundleLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        runCatching {
            val raw = context.contentResolver.openInputStream(uri)?.use { input ->
                input.readBytes().toString(Charsets.UTF_8)
            } ?: return@runCatching
            applyImportedDeckBundle(context.importDeckBundleJson(raw))
        }.onFailure { error ->
            Log.e("MobileDeck", "Failed to import deck bundle", error)
        }
    }

    fun addDeckButton(position: Int? = null, editAfterCreate: Boolean = false) {
        val colors = defaultDeckColors(darkTheme)
        val buttonCapacity = pageButtonCapacity(activeDeckPage.id, deckPages, deckColumns, deckRows)
        val showTitle = activeDeckPage.id == deckPages.firstOrNull()?.id
        val targetPosition = position ?: nextOpenPosition(deckButtons, deckColumns, deckRows, showTitle)
        val newButton = DeckButton(
            id = nextDeckButtonId(deckPages.flatMap { it.buttons }),
            title = "Explorer",
            subtitle = "Win+E",
            icon = ICON_AUTO,
            iconImageUri = "",
            displayMode = DeckDisplayMode.IconAndText,
            actionType = DeckActionType.Hotkey,
            payload = "WIN+E",
            color = colors[deckButtons.size % colors.size],
            position = targetPosition
        )
        if (position == null && targetPosition >= buttonCapacity && deckPages.size >= MAX_PAGES) return
        var createdPageId: Int? = null
        val updatedPages = if (position == null && targetPosition >= buttonCapacity) {
            val newPage = DeckPageConfig(
                id = nextDeckPageId(deckPages),
                name = "Page ${deckPages.size + 1}",
                buttons = listOf(newButton.copy(position = 0))
            )
            createdPageId = newPage.id
            activeDeckPageId = newPage.id
            deckPages + newPage
        } else {
            updateDeckPage(deckPages, activeDeckPage.id) { it.buttons + newButton }
        }
        deckPages = updatedPages
        saveDeckPages(context, updatedPages)
        if (editAfterCreate) {
            editingButton = newButton
            pendingNewButtonId = newButton.id
            pendingNewButtonCreatedPageId = createdPageId
        }
    }

    fun addConsoleButton(rowIndex: Int) {
        val visibleRows = consoleLayoutRowIds(
            consoleLayout,
            activeDeckPage.buttons.filterNot { buttonAppAction(it) == DeckActionType.Settings },
            DEFAULT_COLUMNS,
            DEFAULT_ROWS
        ).ifEmpty { listOf(emptyList()) }
        val safeRowIndex = rowIndex.coerceIn(visibleRows.indices)
        val colors = defaultDeckColors(darkTheme)
        val newButton = DeckButton(
            id = nextDeckButtonId(deckPages.flatMap { it.buttons }),
            title = "Explorer",
            subtitle = "Win+E",
            icon = ICON_AUTO,
            iconImageUri = "",
            displayMode = DeckDisplayMode.IconAndText,
            actionType = DeckActionType.Hotkey,
            payload = "WIN+E",
            color = colors[activeDeckPage.buttons.size % colors.size],
            position = (activeDeckPage.buttons.maxOfOrNull { it.position } ?: -1) + 1
        )
        val updatedPages = updateDeckPage(deckPages, activeDeckPage.id) { pageConfig ->
            pageConfig.buttons + newButton
        }
        val updatedLayoutBase = consoleLayout.copy(
            rows = visibleRows.mapIndexed { index, row ->
                if (index == safeRowIndex) row + newButton.id else row
            }
        )
        val updatedLayout = updatedLayoutBase.copy(
            rowWeights = normalizedConsoleRowWeights(updatedLayoutBase, updatedLayoutBase.rows.size)
        )
        deckPages = updatedPages
        saveDeckPages(context, updatedPages)
        val updatedLayouts = consoleLayouts + (activeDeckPage.id to updatedLayout)
        consoleLayouts = updatedLayouts
        consoleLayout = updatedLayout
        saveConsoleLayouts(context, updatedLayouts)
        saveConsoleLayout(context, updatedLayout)
        pendingNewButtonId = newButton.id
        pendingNewButtonCreatedPageId = null
        editingButton = newButton
        addConsoleLayoutDiagnostic("add console button row=${safeRowIndex + 1} id=${newButton.id}")
    }

    fun consolePlaceholderButtons(startId: Int, startPosition: Int, count: Int): List<DeckButton> {
        val colors = defaultDeckColors(darkTheme)
        val icons = listOf(
            ICON_KEYBOARD,
            ICON_APPS,
            ICON_CODE,
            ICON_TEXT,
            ICON_PLAY,
            ICON_IMAGE,
            ICON_LINK,
            ICON_SEARCH,
            ICON_TOUCH
        ).shuffled()
        return List(count) { index ->
            DeckButton(
                id = startId + index,
                title = "",
                subtitle = "",
                icon = icons[index % icons.size],
                iconImageUri = "",
                displayMode = DeckDisplayMode.IconOnly,
                actionType = DeckActionType.AppCommand,
                payload = "",
                color = colors[index % colors.size],
                position = startPosition + index
            )
        }
    }

    fun consoleThreeByThreeLayout(buttons: List<DeckButton>, sidebarFraction: Float = consoleLayout.sidebarFraction): ConsoleLayoutConfig {
        return ConsoleLayoutConfig(
            rows = buttons.take(9).chunked(3).map { row -> row.map { it.id } },
            rowWeights = List(3) { 1f },
            sidebarFraction = sidebarFraction
        )
    }

    fun saveConsoleLayoutForPage(pageId: Int, layout: ConsoleLayoutConfig) {
        val updatedLayouts = consoleLayouts + (pageId to layout)
        consoleLayouts = updatedLayouts
        saveConsoleLayouts(context, updatedLayouts)
        if (pageId == activeDeckPageId) {
            consoleLayout = layout
            saveConsoleLayout(context, layout)
        }
    }

    fun removeConsoleLayoutForPage(pageId: Int) {
        val updatedLayouts = consoleLayouts - pageId
        consoleLayouts = updatedLayouts
        saveConsoleLayouts(context, updatedLayouts)
    }

    fun consoleLayoutForPage(pageConfig: DeckPageConfig): ConsoleLayoutConfig {
        return consoleLayouts[pageConfig.id] ?: defaultConsoleLayout(pageConfig.buttons)
    }

    fun ensureUniqueConsoleLayoutSlots() {
        val visibleButtons = activeDeckPage.buttons.filterNot { buttonAppAction(it) == DeckActionType.Settings }
        val buttonById = visibleButtons.associateBy { it.id }
        val rows = consoleLayoutRowIds(consoleLayout, visibleButtons, DEFAULT_COLUMNS, DEFAULT_ROWS)
        val seenIds = mutableSetOf<Int>()
        var nextId = nextDeckButtonId(deckPages.flatMap { it.buttons })
        var nextPosition = (activeDeckPage.buttons.maxOfOrNull { it.position } ?: -1) + 1
        val clonedButtons = mutableListOf<DeckButton>()
        var changed = false
        val updatedRows = rows.map { row ->
            row.map { buttonId ->
                if (seenIds.add(buttonId)) {
                    buttonId
                } else {
                    val source = buttonById[buttonId] ?: return@map buttonId
                    changed = true
                    val clone = source.copy(id = nextId++, position = nextPosition++)
                    clonedButtons += clone
                    clone.id
                }
            }
        }
        if (!changed) return
        val updatedPages = updateDeckPage(deckPages, activeDeckPage.id) { pageConfig ->
            pageConfig.buttons + clonedButtons
        }
        val updatedLayout = consoleLayout.copy(rows = updatedRows)
        deckPages = updatedPages
        saveDeckPages(context, updatedPages)
        val updatedLayouts = consoleLayouts + (activeDeckPage.id to updatedLayout)
        consoleLayouts = updatedLayouts
        consoleLayout = updatedLayout
        saveConsoleLayouts(context, updatedLayouts)
        saveConsoleLayout(context, updatedLayout)
        addConsoleLayoutDiagnostic("duplicated console slots cloned count=${clonedButtons.size}")
    }

    LaunchedEffect(page, activeDeckPageId, consoleLayout.rows, activeDeckPage.buttons) {
        if (page == AppPage.ConsoleLayoutEditor) {
            ensureUniqueConsoleLayoutSlots()
        }
    }

    fun saveConsoleLayoutForActivePage(updated: ConsoleLayoutConfig, removeEmptyRows: Boolean = false) {
        val activeButtonIds = activeDeckPage.buttons.map { it.id }.toSet()
        val cleanedBase = if (removeEmptyRows) {
            val weights = normalizedConsoleRowWeights(updated, updated.rows.size)
            val rowsWithWeights = updated.rows.mapIndexed { index, row ->
                row.filter { it in activeButtonIds } to weights.getOrElse(index) { 1f }
            }.filter { (row, _) -> row.isNotEmpty() }
            updated.copy(
                rows = rowsWithWeights.map { it.first },
                rowWeights = normalizedConsoleWeightValues(
                    weights = rowsWithWeights.map { it.second },
                    rowCount = rowsWithWeights.size
                )
            )
        } else {
            updated.copy(rowWeights = normalizedConsoleRowWeights(updated, updated.rows.size))
        }
        val cleaned = cleanedBase.copy(rowWeights = normalizedConsoleRowWeights(cleanedBase, cleanedBase.rows.size))
        if (removeEmptyRows && cleaned.rows.isEmpty() && deckPages.size > 1) {
            val currentIndex = deckPages.indexOfFirst { it.id == activeDeckPage.id }.coerceAtLeast(0)
            val remainingPages = ensureSettingsButton(
                deckPages.filterNot { it.id == activeDeckPage.id },
                darkTheme
            )
            val nextIndex = currentIndex.coerceAtMost(remainingPages.lastIndex)
            val nextPage = remainingPages[nextIndex]
            val nextLayout = consoleLayoutForPage(nextPage)
            removeConsoleLayoutForPage(activeDeckPage.id)
            deckPages = remainingPages
            activeDeckPageId = nextPage.id
            consoleLayout = nextLayout
            saveDeckPages(context, remainingPages)
            saveConsoleLayout(context, nextLayout)
            addConsoleLayoutDiagnostic("empty console page deleted; active=${nextPage.name}")
            return
        }
        saveConsoleLayoutForPage(activeDeckPage.id, cleaned)
    }

    fun addDeckPage() {
        if (deckPages.size >= MAX_PAGES) return
        val nextPageNumber = deckPages.size + 1
        val consolePage = deckUiMode == DeckUiMode.Console || page == AppPage.ConsoleLayoutEditor
        val newButtons = if (consolePage) {
            consolePlaceholderButtons(
                startId = nextDeckButtonId(deckPages.flatMap { it.buttons }),
                startPosition = 0,
                count = 9
            )
        } else {
            emptyList()
        }
        val newPage = DeckPageConfig(
            id = nextDeckPageId(deckPages),
            name = "Page $nextPageNumber",
            buttons = newButtons
        )
        val updatedPages = deckPages + newPage
        deckPages = updatedPages
        activeDeckPageId = newPage.id
        saveDeckPages(context, updatedPages)
        if (consolePage) {
            val updatedLayout = consoleThreeByThreeLayout(newButtons)
            saveConsoleLayoutForPage(newPage.id, updatedLayout)
        }
    }

    fun deleteDeckPage(pageId: Int) {
        if (deckPages.size <= 1 || pageId == deckPages.first().id) return
        val currentIndex = deckPages.indexOfFirst { it.id == pageId }.coerceAtLeast(0)
        val updatedPages = deckPages.filterNot { it.id == pageId }
        val nextIndex = currentIndex.coerceAtMost(updatedPages.lastIndex)
        deckPages = updatedPages
        activeDeckPageId = updatedPages[nextIndex].id
        removeConsoleLayoutForPage(pageId)
        consoleLayout = consoleLayoutForPage(updatedPages[nextIndex])
        saveDeckPages(context, updatedPages)
        saveConsoleLayout(context, consoleLayout)
    }

    fun deleteActiveDeckPage() {
        deleteDeckPage(activeDeckPage.id)
    }

    fun resetFirstDeckPage() {
        val firstPage = deckPages.firstOrNull() ?: return
        val settingsButtons = firstPage.buttons
            .filter { buttonAppAction(it) == DeckActionType.Settings }
            .ifEmpty { normalizeDeckButtons(emptyList(), darkTheme) }
            .mapIndexed { index, button -> button.copy(position = index) }
        val updatedPages = deckPages.map { page ->
            if (page.id == firstPage.id) page.copy(buttons = settingsButtons) else page
        }
        deckPages = updatedPages
        activeDeckPageId = firstPage.id
        consoleLayout = consoleLayoutForPage(firstPage)
        saveConsoleLayout(context, consoleLayout)
        saveDeckPages(context, updatedPages)
    }

    fun deleteDeckButton(button: DeckButton) {
        if (button.appWidgetId != INVALID_APP_WIDGET_ID) {
            if (appWidgetHost != null) {
                appWidgetHost.deleteAppWidgetId(button.appWidgetId)
            }
        }
        val deletedPageId = deckPages.firstOrNull { pageConfig ->
            pageConfig.buttons.any { existing -> existing.id == button.id }
        }?.id
        val updatedPages = deckPages.map { pageConfig ->
            pageConfig.copy(buttons = pageConfig.buttons.filterNot { existing -> existing.id == button.id })
        }
        deckPages = updatedPages
        saveDeckPages(context, updatedPages)
        editingButton = null
        val firstPageId = updatedPages.firstOrNull()?.id
        val deletedPage = updatedPages.firstOrNull { it.id == deletedPageId }
        if (page == AppPage.LayoutEditor &&
            deletedPage != null &&
            deletedPage.id != firstPageId &&
            deletedPage.buttons.isEmpty()
        ) {
            confirmEmptyPageDeletePageId = deletedPage.id
        }
    }

    fun cancelEditingButton(button: DeckButton) {
        if (pendingNewButtonId != button.id) {
            editingButton = null
            return
        }
        if (button.appWidgetId != INVALID_APP_WIDGET_ID) {
            appWidgetHost?.deleteAppWidgetId(button.appWidgetId)
        }
        val createdPageId = pendingNewButtonCreatedPageId
        val currentPages = deckPages.map { pageConfig ->
            pageConfig.copy(buttons = pageConfig.buttons.filterNot { existing -> existing.id == button.id })
        }
        val currentLayout = consoleLayout.copy(
            rows = consoleLayout.rows.map { row -> row.filterNot { it == button.id } }
        )
        val updatedPages = if (createdPageId != null && currentPages.size > 1) {
            currentPages.filterNot { pageConfig ->
                pageConfig.id == createdPageId && pageConfig.buttons.isEmpty()
            }
        } else {
            currentPages
        }
        deckPages = updatedPages
        if (updatedPages.none { it.id == activeDeckPageId }) {
            activeDeckPageId = updatedPages.first().id
        }
        saveDeckPages(context, updatedPages)
        saveConsoleLayoutForActivePage(currentLayout, removeEmptyRows = true)
        pendingNewButtonId = null
        pendingNewButtonCreatedPageId = null
        editingButton = null
    }

    fun finishLayoutEditor() {
        if (hasSettingsButton(deckPages)) {
            page = AppPage.Settings
        } else {
            confirmSettingsButtonRestore = true
        }
    }

    fun restoreSettingsButtonAndFinish() {
        val updatedPages = restoreSettingsButton(deckPages, deckColumns, deckRows, darkTheme)
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
            consoleLayout = consoleLayoutForPage(deckPages[target])
            saveConsoleLayout(context, consoleLayout)
        }
    }

    fun performDeckButtonAction(button: DeckButton, payloadOverride: String = button.payload): Boolean {
        return when (button.actionType) {
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
            DeckActionType.MediaKey -> hidManager.sendMediaKey(payloadOverride)
            DeckActionType.Hotkey -> hidManager.sendHotkey(payloadOverride)
            DeckActionType.Text -> hidManager.sendText(payloadOverride)
            DeckActionType.RunCommand -> hidManager.runWindowsCommand(payloadOverride)
            DeckActionType.Utility -> runUtilityAction(context, payloadOverride)
            DeckActionType.AppCommand -> when (appCommandAction(payloadOverride)) {
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
    }

    fun logDeckButtonAction(button: DeckButton, payload: String, delivered: Boolean) {
        val note = when {
            buttonAppAction(button.actionType, payload) == DeckActionType.Settings -> "opened settings"
            buttonAppAction(button.actionType, payload) == DeckActionType.BluetoothStatus -> "opened connection"
            buttonAppAction(button.actionType, payload) == DeckActionType.PreviousPage -> "previous page"
            buttonAppAction(button.actionType, payload) == DeckActionType.NextPage -> "next page"
            delivered -> "sent"
            button.actionType == DeckActionType.AppCommand -> "not supported by Bluetooth keyboard"
            button.actionType == DeckActionType.Utility -> "utility unavailable"
            hidStatus.state != HidConnectionState.Connected -> "no connected PC"
            else -> "unsupported payload"
        }
        logs = listOf(
            ActivityLog(
                buttonTitle = button.title,
                payload = payload,
                delivered = delivered,
                note = note
            )
        ) + logs.take(9)
    }

    fun pressDeckButton(button: DeckButton) {
        val delivered = performDeckButtonAction(button)
        logDeckButtonAction(button, button.payload, delivered)
    }

    fun pressTrimButton(button: DeckButton, step: Int) {
        if (step == 0) return
        val payload = controlPayloadForStep(button, step)
        if (payload.isBlank()) return
        var delivered = false
        val repeatCount = 1
        repeat(repeatCount) {
            delivered = performDeckButtonAction(button, payload) || delivered
        }
        logDeckButtonAction(button, payload, delivered)
    }

    fun moveDeckButtonToSlot(button: DeckButton, targetPosition: Int) {
        val currentPage = deckPages.firstOrNull { it.id == activeDeckPageId } ?: return
        val currentButton = currentPage.buttons.firstOrNull { it.id == button.id } ?: return
        val currentButtons = currentPage.buttons
        val pageCapacity = pageButtonCapacity(currentPage.id, deckPages, deckColumns, deckRows)
        if (targetPosition !in 0 until pageCapacity || currentButton.position == targetPosition) return
        val showTitle = currentPage.id == deckPages.firstOrNull()?.id
        val otherButtons = currentButtons.filterNot { it.id == currentButton.id }
        val movedButton = currentButton.copy(position = targetPosition)
        val updatedButtons = if (canPlaceButton(movedButton, otherButtons, deckColumns, deckRows, showTitle)) {
            currentButtons.map { existing ->
                if (existing.id == currentButton.id) movedButton else existing
            }
        } else {
            val targetButton = buttonAtPosition(
                buttons = otherButtons,
                targetPosition = targetPosition,
                columns = deckColumns,
                rows = deckRows,
                showTitle = showTitle
            ) ?: return
            if (!sameButtonSize(currentButton, targetButton, deckColumns, deckRows, showTitle)) return
            val swappedButton = currentButton.copy(position = targetButton.position)
            val swappedTargetButton = targetButton.copy(position = currentButton.position)
            val remainingButtons = currentButtons.filterNot { it.id == currentButton.id || it.id == targetButton.id }
            if (!canPlaceButton(swappedButton, remainingButtons + swappedTargetButton, deckColumns, deckRows, showTitle)) return
            if (!canPlaceButton(swappedTargetButton, remainingButtons + swappedButton, deckColumns, deckRows, showTitle)) return
            currentButtons.map { existing ->
                when (existing.id) {
                    currentButton.id -> swappedButton
                    targetButton.id -> swappedTargetButton
                    else -> existing
                }
            }
        }
        val updatedPages = updateDeckPage(deckPages, currentPage.id) {
            updatedButtons
        }
        deckPages = updatedPages
        saveDeckPages(context, updatedPages)
    }

    DisposableEffect(hidManager) {
        onDispose { hidManager.stop() }
    }

    val appColors = deckThemeColors(deckUiMode, darkTheme)
    val bluetoothConnected = hidStatus.state == HidConnectionState.Connected
    val companionConnected = false
    val shouldKeepScreenOn = bluetoothConnected || companionConnected || (BuildConfig.DEBUG && debugKeepScreenOn)
    SideEffect {
        (context as? Activity)?.window?.let { activityWindow ->
            val backgroundColor = appColors.backgroundGradient.first().toArgb()
            activityWindow.setBackgroundDrawable(ColorDrawable(backgroundColor))
            activityWindow.decorView.setBackgroundColor(backgroundColor)
            if (shouldKeepScreenOn) {
                activityWindow.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                activityWindow.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }
    }
    val activeFontSize = if (deckUiMode == DeckUiMode.Console) consoleFontSize else classicFontSize
    MobileDeckTheme(
        style = deckUiMode.toThemeStyle(),
        fontSizeScale = activeFontSize.scale
    ) {
    CompositionLocalProvider(LocalDeckThemeColors provides appColors) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(appColors.backgroundGradient)),
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        val connectHost: (PairedHidHost) -> Unit = { host ->
            val started = hidManager.connectOrRegister(host.address)
            logs = listOf(
                ActivityLog(
                    buttonTitle = "Connect",
                    payload = host.name,
                    delivered = started,
                    note = if (started) "registration or connection requested" else "connection failed"
                )
            ) + logs.take(9)
        }
        when (page) {
            AppPage.Deck -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (
                    deckUiMode == DeckUiMode.Classic &&
                    classicDeckBackground.type != ClassicDeckBackgroundType.Default
                ) {
                    ClassicDeckBackgroundLayer(
                        modifier = Modifier.fillMaxSize(),
                        background = classicDeckBackground
                    )
                }
                DeckPage(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (deckUiMode == DeckUiMode.Console) {
                                Modifier
                            } else {
                                Modifier.padding(10.dp)
                            }
                        ),
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
                    onConsoleSettings = { page = AppPage.Settings },
                    classicSolidButtonBackground = classicSolidButtonBackground,
                    classicDeckBackground = classicDeckBackground,
                    previewMode = false,
                    pageSwipeAxis = pageSwipeAxis,
                    pageSwipeMode = pageSwipeMode,
                    pageSwipeAnimation = pageSwipeAnimation,
                    pageSwipeDelta = lastPageDelta,
                    pageAnimationSequence = pageAnimationSequence,
                    onPageSwipe = ::switchDeckPage,
                    onAddPage = ::addDeckPage,
                    onButtonPressed = ::pressDeckButton,
                    onTrimStep = ::pressTrimButton,
                    onButtonTouchStarted = { context.applicationContext.vibrateButtonPress(buttonVibrationLevel) },
                    onButtonTouchEnded = { context.applicationContext.vibrateButtonPress(buttonVibrationLevel) },
                    onButtonEdit = {},
                    onButtonMoved = ::moveDeckButtonToSlot,
                    onEmptySlotPressed = { slot -> addDeckButton(slot, editAfterCreate = true) }
                )
                if (showClassicTutorial && classicTutorialStep == SettingsTutorialStep.DeckSettingsButton) {
                    ClassicDeckSettingsButtonTutorialOverlay(
                        modifier = Modifier.fillMaxSize(),
                        settingsButton = activeDeckPage.buttons.firstOrNull { buttonAppAction(it) == DeckActionType.Settings },
                        columns = deckColumns,
                        rows = deckRows,
                        spacing = deckSpacing.dp,
                        showTitle = activeDeckPage.id == deckPages.firstOrNull()?.id,
                        pageSwipeAxis = pageSwipeAxis,
                        onDismiss = {
                            showClassicTutorial = false
                            classicTutorialStep = SettingsTutorialStep.Bluetooth
                            page = AppPage.Settings
                            saveClassicTutorialSeen(context)
                        }
                    )
                }
            }

            AppPage.LayoutEditor -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (classicDeckBackground.type != ClassicDeckBackgroundType.Default) {
                    ClassicDeckBackgroundLayer(
                        modifier = Modifier.fillMaxSize(),
                        background = classicDeckBackground
                    )
                }
                LayoutEditorPage(
                    modifier = Modifier
                        .fillMaxSize()
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
                    classicSolidButtonBackground = classicSolidButtonBackground,
                    classicDeckBackground = classicDeckBackground,
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
                    onButtonDragStarted = { context.applicationContext.vibrateButtonPress(buttonVibrationLevel) },
                    onTrimStep = ::pressTrimButton,
                    onButtonMoved = ::moveDeckButtonToSlot,
                    onButtonDeleted = ::deleteDeckButton,
                    onEmptySlotPressed = { slot -> addDeckButton(slot, editAfterCreate = true) }
                )
            }

            AppPage.ConsoleLayoutEditor -> ConsoleLayoutEditorPage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(10.dp),
                buttons = deckButtons,
                layout = consoleLayout,
                activePageIndex = deckPages.indexOfFirst { it.id == activeDeckPage.id }.coerceAtLeast(0),
                pageCount = deckPages.size,
                diagnostics = consoleLayoutDiagnostics,
                initialEditMode = consoleLayoutEditorInitialMode,
                onBack = { page = AppPage.Settings },
                onPageSwipe = { delta ->
                    addConsoleLayoutDiagnostic("page swipe delta=$delta")
                    switchDeckPage(delta)
                },
                onAddPage = {
                    addConsoleLayoutDiagnostic("add page requested count=${deckPages.size}/$MAX_PAGES")
                    addDeckPage()
                },
                onAddRow = { targetRowIndex ->
                    val rows = consoleLayoutRowIds(
                        consoleLayout,
                        activeDeckPage.buttons.filterNot { buttonAppAction(it) == DeckActionType.Settings },
                        DEFAULT_COLUMNS,
                        DEFAULT_ROWS
                    )
                    val insertIndex = targetRowIndex.coerceIn(0, rows.size)
                    addConsoleLayoutDiagnostic("add row clicked targetRow=${insertIndex + 1} rows=${rows.size}/$MAX_CONSOLE_LAYOUT_ROWS raw=${consoleLayout.rows.size}")
                    if (rows.size < MAX_CONSOLE_LAYOUT_ROWS) {
                        val newButtons = consolePlaceholderButtons(
                            startId = nextDeckButtonId(deckPages.flatMap { it.buttons }),
                            startPosition = activeDeckPage.buttons.size,
                            count = 3
                        )
                        val updatedPages = updateDeckPage(deckPages, activeDeckPage.id) { pageConfig ->
                            pageConfig.buttons + newButtons
                        }
                        val updatedRows = rows.toMutableList().apply {
                            add(insertIndex, newButtons.map { it.id })
                        }
                        val updatedBase = consoleLayout.copy(
                            rows = updatedRows,
                            rowWeights = insertedConsoleRowWeights(
                                layout = consoleLayout,
                                rowCount = rows.size,
                                insertIndex = insertIndex
                            )
                        )
                        val updated = updatedBase.copy(rowWeights = normalizedConsoleRowWeights(updatedBase, updatedBase.rows.size))
                        deckPages = updatedPages
                        saveDeckPages(context, updatedPages)
                        saveConsoleLayoutForPage(activeDeckPage.id, updated)
                        addConsoleLayoutDiagnostic("add row saved targetRow=${insertIndex + 1} rows=${updated.rows.size} weights=${updated.rowWeights.size}")
                    } else {
                        addConsoleLayoutDiagnostic("add row ignored max rows reached")
                    }
                },
                onRemoveRow = { rowIndex ->
                    val rows = consoleLayoutRowIds(
                        consoleLayout,
                        activeDeckPage.buttons.filterNot { buttonAppAction(it) == DeckActionType.Settings },
                        DEFAULT_COLUMNS,
                        DEFAULT_ROWS
                    )
                    addConsoleLayoutDiagnostic("remove row clicked row=${rowIndex + 1} rows=${rows.size}")
                    if (rowIndex in rows.indices) {
                        val removedButtonIds = rows[rowIndex].toSet()
                        val updatedPages = updateDeckPage(deckPages, activeDeckPage.id) { pageConfig ->
                            pageConfig.buttons.filterNot { it.id in removedButtonIds }
                        }
                        val updatedBase = consoleLayout.copy(
                            rows = rows.filterIndexed { index, _ -> index != rowIndex },
                            rowWeights = removedConsoleRowWeights(
                                layout = consoleLayout,
                                rowCount = rows.size,
                                removeIndex = rowIndex
                            )
                        )
                        val updated = updatedBase.copy(rowWeights = normalizedConsoleRowWeights(updatedBase, updatedBase.rows.size))
                        deckPages = updatedPages
                        saveDeckPages(context, updatedPages)
                        saveConsoleLayoutForActivePage(updated, removeEmptyRows = true)
                        addConsoleLayoutDiagnostic("remove row saved rows=${updated.rows.size}")
                    } else {
                        addConsoleLayoutDiagnostic("remove row ignored")
                    }
                },
                onMoveRow = { rowIndex, delta ->
                    val rows = consoleLayoutRowIds(
                        consoleLayout,
                        activeDeckPage.buttons.filterNot { buttonAppAction(it) == DeckActionType.Settings },
                        DEFAULT_COLUMNS,
                        DEFAULT_ROWS
                    )
                    if (rows.isEmpty()) {
                        addConsoleLayoutDiagnostic("move row ignored; no rows")
                    } else {
                        val targetIndex = (rowIndex + delta).coerceIn(rows.indices)
                        addConsoleLayoutDiagnostic("move row clicked from=${rowIndex + 1} to=${targetIndex + 1}")
                        if (targetIndex != rowIndex) {
                            val weights = normalizedConsoleRowWeights(consoleLayout, rows.size)
                            val updatedRows = rows.toMutableList().apply {
                                val row = removeAt(rowIndex)
                                add(targetIndex, row)
                            }
                            val updated = consoleLayout.copy(rows = updatedRows, rowWeights = weights)
                            saveConsoleLayoutForActivePage(updated)
                            addConsoleLayoutDiagnostic("move row saved rows=${updated.rows.size}")
                        }
                    }
                },
                onReset = {
                    val updated = defaultConsoleLayout(deckButtons)
                    saveConsoleLayoutForPage(activeDeckPage.id, updated)
                    addConsoleLayoutDiagnostic("reset console layout rows=${updated.rows.size}")
                },
                onLayoutChange = { updated ->
                    val merged = consoleLayout.copy(
                        rowWeights = normalizedConsoleWeightValues(updated.rowWeights, consoleLayout.rows.size),
                        sidebarFraction = updated.sidebarFraction
                    )
                    saveConsoleLayoutForActivePage(merged)
                    addConsoleLayoutDiagnostic("layout changed rows=${merged.rows.size} weights=${merged.rowWeights.size}")
                },
                onPickButton = { rowIndex ->
                    addConsoleButton(rowIndex)
                },
                onRemoveButton = { rowIndex, buttonIndex ->
                    val rows = consoleLayoutRowIds(
                        consoleLayout,
                        activeDeckPage.buttons.filterNot { buttonAppAction(it) == DeckActionType.Settings },
                        DEFAULT_COLUMNS,
                        DEFAULT_ROWS
                    )
                    val buttonId = rows.getOrNull(rowIndex)?.getOrNull(buttonIndex)
                    if (buttonId != null) {
                        val updated = consoleLayout.copy(
                            rows = rows.mapIndexed { index, row ->
                                if (index == rowIndex) row.filterIndexed { itemIndex, _ -> itemIndex != buttonIndex } else row
                            }
                        )
                        val remainingOccurrences = updated.rows.flatten().count { it == buttonId }
                        if (remainingOccurrences == 0) {
                            val updatedPages = updateDeckPage(deckPages, activeDeckPage.id) { pageConfig ->
                                pageConfig.buttons.filterNot { it.id == buttonId }
                            }
                            deckPages = updatedPages
                            saveDeckPages(context, updatedPages)
                        }
                        saveConsoleLayoutForActivePage(updated, removeEmptyRows = true)
                        addConsoleLayoutDiagnostic("remove button row=${rowIndex + 1} index=${buttonIndex + 1} id=$buttonId")
                    }
                },
                onMoveButton = { rowIndex, fromIndex, delta ->
                    val rows = consoleLayoutRowIds(
                        consoleLayout,
                        activeDeckPage.buttons.filterNot { buttonAppAction(it) == DeckActionType.Settings },
                        DEFAULT_COLUMNS,
                        DEFAULT_ROWS
                    )
                    val targetIndex = rows.getOrNull(rowIndex)?.indices?.let { (fromIndex + delta).coerceIn(it) } ?: fromIndex
                    addConsoleLayoutDiagnostic("move button row=${rowIndex + 1} from=${fromIndex + 1} to=${targetIndex + 1}")
                    if (targetIndex != fromIndex && rowIndex in rows.indices) {
                        val updatedRows = rows.mapIndexed { index, row ->
                            if (index != rowIndex) {
                                row
                            } else {
                                row.toMutableList().apply {
                                    val item = this[fromIndex]
                                    this[fromIndex] = this[targetIndex]
                                    this[targetIndex] = item
                                }
                            }
                        }
                        val updated = consoleLayout.copy(rows = updatedRows)
                        saveConsoleLayoutForActivePage(updated)
                        addConsoleLayoutDiagnostic("move button saved")
                    }
                },
                onMoveButtonTo = { fromRowIndex, fromIndex, toRowIndex, toIndex ->
                    val rows = consoleLayoutRowIds(
                        consoleLayout,
                        activeDeckPage.buttons.filterNot { buttonAppAction(it) == DeckActionType.Settings },
                        DEFAULT_COLUMNS,
                        DEFAULT_ROWS
                    )
                    if (
                        fromRowIndex in rows.indices &&
                        toRowIndex in rows.indices &&
                        fromIndex in rows[fromRowIndex].indices &&
                        toIndex in rows[toRowIndex].indices
                    ) {
                        val mutableRows = rows.map { it.toMutableList() }.toMutableList()
                        val movingButtonId = mutableRows[fromRowIndex][fromIndex]
                        mutableRows[fromRowIndex][fromIndex] = mutableRows[toRowIndex][toIndex]
                        mutableRows[toRowIndex][toIndex] = movingButtonId
                        val updated = consoleLayout.copy(rows = mutableRows)
                        saveConsoleLayoutForActivePage(updated)
                        addConsoleLayoutDiagnostic("swap button id=$movingButtonId from=${fromRowIndex + 1}:${fromIndex + 1} to=${toRowIndex + 1}:${toIndex + 1}")
                    }
                },
                onEditButton = { button ->
                    addConsoleLayoutDiagnostic("edit button id=${button.id} title=${button.title}")
                    editingButton = button
                }
            )

            AppPage.IconStyleTest -> {
                if (BuildConfig.DEBUG) {
                    ConsoleIconStyleTestPage(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        onBack = { page = AppPage.Settings }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        page = AppPage.Settings
                    }
                }
            }

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
                classicSolidButtonBackground = classicSolidButtonBackground,
                classicDeckBackground = classicDeckBackground,
                deckUiMode = deckUiMode,
                classicFontSize = classicFontSize,
                consoleFontSize = consoleFontSize,
                consolePanelOptions = consolePanelOptions,
                pairingDiscoverable = pairingDiscoverable,
                showClassicTutorial = showClassicTutorial,
                classicTutorialStep = classicTutorialStep,
                debugKeepScreenOn = debugKeepScreenOn,
                onLayoutEditor = {
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    page = AppPage.LayoutEditor
                },
                onConsoleLayoutEditor = { editMode ->
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    consoleLayoutEditorInitialMode = editMode
                    page = AppPage.ConsoleLayoutEditor
                },
                onExportBundle = {
                    pendingExportJson = context.createDeckBundleJson(currentDeckBundleSnapshot())
                    val fileName = "mobiledeck-${SimpleDateFormat("yyyyMMdd-HHmm", Locale.US).format(Date())}.json"
                    exportDeckBundleLauncher.launch(fileName)
                },
                onImportBundle = {
                    importDeckBundleLauncher.launch(arrayOf("application/json", "text/*", "*/*"))
                },
                onOpenIconStyleTest = {
                    if (BuildConfig.DEBUG) {
                        context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                        page = AppPage.IconStyleTest
                    }
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
                onClassicSolidButtonBackgroundChange = { enabled ->
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    classicSolidButtonBackground = enabled
                    saveClassicSolidButtonBackground(context, enabled)
                },
                onClassicFontSizeChange = { option ->
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    classicFontSize = option
                    saveClassicFontSizeOption(context, option)
                },
                onConsoleFontSizeChange = { option ->
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    consoleFontSize = option
                    saveConsoleFontSizeOption(context, option)
                },
                onClassicDeckBackgroundChange = { background ->
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    classicDeckBackground = background
                    saveClassicDeckBackground(context, background)
                },
                onPickClassicDeckBackgroundImage = {
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    classicBackgroundImageLauncher.launch(arrayOf("image/*"))
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
                onShowClassicTutorial = {
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    classicTutorialStep = SettingsTutorialStep.Bluetooth
                    showClassicTutorial = true
                },
                onDebugKeepScreenOnChange = { enabled ->
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    debugKeepScreenOn = enabled
                },
                onDismissClassicTutorial = {
                    showClassicTutorial = false
                    classicTutorialStep = SettingsTutorialStep.Bluetooth
                    page = AppPage.Settings
                    saveClassicTutorialSeen(context)
                },
                onClassicTutorialStepChange = { tutorialStep ->
                    classicTutorialStep = tutorialStep
                },
            )
        }
    }

    editingButton?.let { button ->
        EditButtonDialog(
            button = button,
            status = hidStatus,
            appWidgetHost = appWidgetHost,
            appWidgetManager = appWidgetManager,
            classicSolidButtonBackground = classicSolidButtonBackground,
            consoleStyle = page == AppPage.ConsoleLayoutEditor || deckUiMode == DeckUiMode.Console,
            onDismiss = { cancelEditingButton(button) },
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
                pendingNewButtonId = null
                pendingNewButtonCreatedPageId = null
                editingButton = null
            },
            onPickWidget = { updated ->
                pickWidgetForButton(updated)
            }
        )
    }

    confirmEmptyPageDeletePageId?.let { pageId ->
        AlertDialog(
            onDismissRequest = { confirmEmptyPageDeletePageId = null },
            title = { Text(stringResource(R.string.delete_current_page)) },
            text = { Text(stringResource(R.string.confirm_delete_page)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmEmptyPageDeletePageId = null
                        deleteDeckPage(pageId)
                    }
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmEmptyPageDeletePageId = null }) {
                    Text(stringResource(R.string.cancel))
                }
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
    classicSolidButtonBackground: Boolean,
    classicDeckBackground: ClassicDeckBackground,
    deckUiMode: DeckUiMode,
    classicFontSize: DeckFontSizeOption,
    consoleFontSize: DeckFontSizeOption,
    consolePanelOptions: ConsolePanelOptions,
    pairingDiscoverable: Boolean,
    showClassicTutorial: Boolean,
    classicTutorialStep: SettingsTutorialStep,
    debugKeepScreenOn: Boolean,
    pageName: String,
    pageCount: Int,
    pairedHosts: List<PairedHidHost>,
    onBack: () -> Unit,
    onLayoutEditor: () -> Unit,
    onConsoleLayoutEditor: (ConsoleLayoutEditMode) -> Unit,
    onExportBundle: () -> Unit,
    onImportBundle: () -> Unit,
    onOpenIconStyleTest: () -> Unit,
    onPageSwipeAxisChange: (PageSwipeAxis) -> Unit,
    onPageSwipeModeChange: (PageSwipeMode) -> Unit,
    onPageSwipeAnimationChange: (Boolean) -> Unit,
    onInfinitePageSwipeChange: (Boolean) -> Unit,
    onButtonVibrationLevelChange: (ButtonVibrationLevel) -> Unit,
    onClassicSolidButtonBackgroundChange: (Boolean) -> Unit,
    onClassicFontSizeChange: (DeckFontSizeOption) -> Unit,
    onConsoleFontSizeChange: (DeckFontSizeOption) -> Unit,
    onClassicDeckBackgroundChange: (ClassicDeckBackground) -> Unit,
    onPickClassicDeckBackgroundImage: () -> Unit,
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
    onAddPage: () -> Unit,
    onShowClassicTutorial: () -> Unit,
    onDebugKeepScreenOnChange: (Boolean) -> Unit,
    onDismissClassicTutorial: () -> Unit,
    onClassicTutorialStepChange: (SettingsTutorialStep) -> Unit
) {
    val colors = deckThemeColors(deckUiMode, isSystemInDarkTheme())
    var consoleSettingsCategory by remember { mutableStateOf(ConsoleSettingsCategory.Bluetooth) }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(colors.backgroundGradient)
            )
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            SettingsSidebar(
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxHeight(),
                status = status,
                deckUiMode = deckUiMode,
                pairedHosts = pairedHosts,
                pairingDiscoverable = pairingDiscoverable,
                selectedConsoleCategory = consoleSettingsCategory,
                onBack = onBack,
                onDeckUiModeChange = onDeckUiModeChange,
                onConsoleCategoryChange = { consoleSettingsCategory = it },
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
                        category = consoleSettingsCategory,
                        status = status,
                        deckPages = deckPages,
                        activePageId = activePageId,
                        pageSwipeMode = pageSwipeMode,
                        pageSwipeAnimation = pageSwipeAnimation,
                        infinitePageSwipe = infinitePageSwipe,
                        buttonVibrationLevel = buttonVibrationLevel,
                        consoleFontSize = consoleFontSize,
                        consolePanelOptions = consolePanelOptions,
                        pageName = pageName,
                        pageCount = pageCount,
                        pairedHosts = pairedHosts,
                        pairingDiscoverable = pairingDiscoverable,
                        logs = logs,
                        debugKeepScreenOn = debugKeepScreenOn,
                        onStart = onStart,
                        onStop = onStop,
                        onMakeDiscoverable = onMakeDiscoverable,
                        onCancelDiscoverable = onCancelDiscoverable,
                        onRefreshHosts = onRefreshHosts,
                        onConnectHost = onConnectHost,
                        onPageSwipeModeChange = onPageSwipeModeChange,
                        onPageSwipeAnimationChange = onPageSwipeAnimationChange,
                        onInfinitePageSwipeChange = onInfinitePageSwipeChange,
                        onButtonVibrationLevelChange = onButtonVibrationLevelChange,
                        onConsoleFontSizeChange = onConsoleFontSizeChange,
                        onConsolePanelOptionsChange = onConsolePanelOptionsChange,
                        onConsoleLayoutEditor = onConsoleLayoutEditor,
                        onAddPage = onAddPage,
                        onExportBundle = onExportBundle,
                        onImportBundle = onImportBundle,
                        onOpenIconStyleTest = onOpenIconStyleTest,
                        onShowClassicTutorial = onShowClassicTutorial,
                        onDebugKeepScreenOnChange = onDebugKeepScreenOnChange
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
                        classicSolidButtonBackground = classicSolidButtonBackground,
                        classicFontSize = classicFontSize,
                        classicDeckBackground = classicDeckBackground,
                        pageName = pageName,
                        pageCount = pageCount,
                        logs = logs,
                        debugKeepScreenOn = debugKeepScreenOn,
                        onPageSwipeAxisChange = onPageSwipeAxisChange,
                        onPageSwipeModeChange = onPageSwipeModeChange,
                        onPageSwipeAnimationChange = onPageSwipeAnimationChange,
                        onInfinitePageSwipeChange = onInfinitePageSwipeChange,
                        onButtonVibrationLevelChange = onButtonVibrationLevelChange,
                        onClassicSolidButtonBackgroundChange = onClassicSolidButtonBackgroundChange,
                        onClassicFontSizeChange = onClassicFontSizeChange,
                        onClassicDeckBackgroundChange = onClassicDeckBackgroundChange,
                        onPickClassicDeckBackgroundImage = onPickClassicDeckBackgroundImage,
                        onLayoutEditor = onLayoutEditor,
                        onExportBundle = onExportBundle,
                        onImportBundle = onImportBundle,
                        onOpenIconStyleTest = onOpenIconStyleTest,
                        onColumnsChange = onColumnsChange,
                        onRowsChange = onRowsChange,
                        onSpacingChange = onSpacingChange,
                        onAddPage = onAddPage,
                        onShowClassicTutorial = onShowClassicTutorial,
                        onDebugKeepScreenOnChange = onDebugKeepScreenOnChange
                    )
                }
            }
        }
        if (showClassicTutorial && classicTutorialStep != SettingsTutorialStep.DeckSettingsButton) {
            ClassicSettingsTutorialOverlay(
                modifier = Modifier.fillMaxSize(),
                step = classicTutorialStep,
                onStepChange = onClassicTutorialStepChange,
                onDismiss = onDismissClassicTutorial
            )
        }
    }
}

private enum class ConsoleSettingsCategory {
    Bluetooth,
    Layout,
    Background,
    Controls,
    App
}

private enum class ConsoleLayoutEditMode(@StringRes val labelRes: Int) {
    Layout(R.string.console_layout_mode_layout),
    Buttons(R.string.console_layout_mode_buttons)
}

@StringRes
private fun consoleSettingsCategoryTitleRes(category: ConsoleSettingsCategory): Int {
    return when (category) {
        ConsoleSettingsCategory.Bluetooth -> R.string.console_settings_category_bluetooth
        ConsoleSettingsCategory.Layout -> R.string.console_settings_category_layout
        ConsoleSettingsCategory.Background -> R.string.console_settings_category_background
        ConsoleSettingsCategory.Controls -> R.string.console_settings_category_controls
        ConsoleSettingsCategory.App -> R.string.console_settings_category_app
    }
}

@StringRes
private fun consoleSettingsCategorySubtitleRes(category: ConsoleSettingsCategory): Int {
    return when (category) {
        ConsoleSettingsCategory.Bluetooth -> R.string.console_settings_category_bluetooth_desc
        ConsoleSettingsCategory.Layout -> R.string.console_settings_category_layout_desc
        ConsoleSettingsCategory.Background -> R.string.console_settings_category_background_desc
        ConsoleSettingsCategory.Controls -> R.string.console_settings_category_controls_desc
        ConsoleSettingsCategory.App -> R.string.console_settings_category_app_desc
    }
}

private fun consoleSettingsCategoryIcon(category: ConsoleSettingsCategory): ImageVector {
    return when (category) {
        ConsoleSettingsCategory.Bluetooth -> Icons.Filled.Bluetooth
        ConsoleSettingsCategory.Layout -> Icons.Filled.GridView
        ConsoleSettingsCategory.Background -> Icons.Filled.Image
        ConsoleSettingsCategory.Controls -> Icons.Filled.TouchApp
        ConsoleSettingsCategory.App -> Icons.Filled.Info
    }
}

@Composable
private fun SettingsSidebar(
    modifier: Modifier = Modifier,
    status: HidStatus,
    deckUiMode: DeckUiMode,
    pairedHosts: List<PairedHidHost>,
    pairingDiscoverable: Boolean,
    selectedConsoleCategory: ConsoleSettingsCategory,
    onBack: () -> Unit,
    onDeckUiModeChange: (DeckUiMode) -> Unit,
    onConsoleCategoryChange: (ConsoleSettingsCategory) -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onMakeDiscoverable: () -> Unit,
    onCancelDiscoverable: () -> Unit,
    onRefreshHosts: () -> Unit,
    onConnectHost: (PairedHidHost) -> Unit
) {
    val colors = deckThemeColors(deckUiMode, isSystemInDarkTheme())
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
        if (deckUiMode == DeckUiMode.Console) {
            ConsoleSettingsCategoryList(
                selected = selectedConsoleCategory,
                onSelected = onConsoleCategoryChange
            )
        } else {
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
}

@Composable
private fun ConsoleSettingsCategoryList(
    selected: ConsoleSettingsCategory,
    onSelected: (ConsoleSettingsCategory) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        ConsoleSettingsCategory.values().forEach { category ->
            ConsoleSettingsCategoryButton(
                category = category,
                selected = selected == category,
                onClick = { onSelected(category) }
            )
        }
    }
}

@Composable
private fun ConsoleSettingsCategoryButton(
    category: ConsoleSettingsCategory,
    selected: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val darkTheme = isSystemInDarkTheme()
    val shape = RoundedCornerShape(14.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .consoleSettingsCategoryButtonShadow(
                shape = shape,
                darkTheme = darkTheme,
                pressed = pressed
            ),
        color = if (selected) colors.consoleButtonFeatured else colors.consoleButtonDefault,
        contentColor = if (selected) Color.White else colors.textPrimary,
        shape = shape,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        interactionSource = interactionSource,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Icon(
                imageVector = consoleSettingsCategoryIcon(category),
                contentDescription = null,
                tint = if (selected) Color.White else colors.textPrimary,
                modifier = Modifier.size(22.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stringResource(consoleSettingsCategoryTitleRes(category)),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selected) Color.White else colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(consoleSettingsCategorySubtitleRes(category)),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (selected) Color.White.copy(alpha = 0.78f) else colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun UiModeToggle(
    deckUiMode: DeckUiMode,
    onDeckUiModeChange: (DeckUiMode) -> Unit
) {
    val darkTheme = isSystemInDarkTheme()
    val colors = deckThemeColors(deckUiMode, darkTheme)
    val consoleSelected = deckUiMode == DeckUiMode.Console
    val selectedStart by animateColorAsState(
        targetValue = if (consoleSelected) colors.consoleButtonDefault else Color(0xFF0B63D1),
        label = "settingsToggleStart"
    )
    val selectedEnd by animateColorAsState(
        targetValue = if (consoleSelected) colors.consoleButtonDefault else Color(0xFF228BFF),
        label = "settingsToggleEnd"
    )
    val consoleHairline by animateColorAsState(
        targetValue = if (consoleSelected) {
            consoleHairlineColor(darkTheme)
        } else {
            Color.Transparent
        },
        label = "settingsToggleHairline"
    )
    val selectedShape = RoundedCornerShape(7.dp)
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
        ) {
            if (consoleSelected) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .offset { consoleHairlineOffset(pressed = false) }
                        .clip(selectedShape)
                        .background(consoleHairline)
                )
            }
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(selectedShape)
                    .background(
                        Brush.linearGradient(
                            listOf(selectedStart, selectedEnd)
                        )
                    )
                    .then(
                        if (consoleSelected) {
                            Modifier.consoleUiModeToggleInnerShadow(
                                shape = selectedShape,
                                darkTheme = darkTheme
                            )
                        } else {
                            Modifier
                        }
                    )
            ) {
                if (consoleSelected) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.White.copy(alpha = if (darkTheme) 0.14f else 0.34f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = if (darkTheme) 0.16f else 0.06f)
                                    )
                                )
                            )
                    )
                }
            }
        }
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
                            color = if (deckUiMode == mode) {
                                if (consoleSelected) colors.textPrimary else Color.White
                            } else {
                                colors.textSecondary
                            },
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

private fun Modifier.consoleUiModeToggleInnerShadow(
    shape: Shape,
    darkTheme: Boolean
): Modifier {
    return this
        .innerShadow(
            shape = shape,
            shadow = DropShadow(
                radius = 8.dp,
                spread = 1.dp,
                offset = DpOffset((-2).dp, (-2).dp),
                color = Color.Black.copy(alpha = if (darkTheme) 0.42f else 0.22f)
            )
        )
        .innerShadow(
            shape = shape,
            shadow = DropShadow(
                radius = 7.dp,
                spread = 0.7.dp,
                offset = DpOffset(2.dp, 2.dp),
                color = Color.White.copy(alpha = if (darkTheme) 0.08f else 0.28f)
            )
        )
}

@Composable
private fun ConsoleLayoutEntryToggle(
    modifier: Modifier = Modifier,
    onOpenMode: (ConsoleLayoutEditMode) -> Unit
) {
    var selectedMode by remember { mutableStateOf(ConsoleLayoutEditMode.Layout) }
    ConsoleLayoutModeToggle(
        modifier = modifier,
        selectedMode = selectedMode,
        onModeChange = { mode ->
            selectedMode = mode
            onOpenMode(mode)
        }
    )
}

@Composable
private fun ConsoleLayoutModeToggle(
    modifier: Modifier = Modifier,
    selectedMode: ConsoleLayoutEditMode,
    onModeChange: (ConsoleLayoutEditMode) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val darkTheme = isSystemInDarkTheme()
    val selectedShape = RoundedCornerShape(12.dp)
    BoxWithConstraints(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(colors.consolePreviewBackground)
            .border(0.8.dp, colors.cardBorder.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
            .padding(4.dp)
    ) {
        val selectedIndex = if (selectedMode == ConsoleLayoutEditMode.Buttons) 1f else 0f
        val offsetIndex by animateFloatAsState(selectedIndex, label = "consoleLayoutEditMode")
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .offset { IntOffset((constraints.maxWidth * 0.5f * offsetIndex).roundToInt(), 0) }
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset { consoleHairlineOffset(pressed = false) }
                    .clip(selectedShape)
                    .background(consoleHairlineColor(darkTheme))
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clip(selectedShape)
                    .background(colors.consoleButtonFeatured)
            )
        }
        Row(modifier = Modifier.fillMaxSize()) {
            ConsoleLayoutEditMode.values().forEach { mode ->
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    color = Color.Transparent,
                    contentColor = colors.textPrimary,
                    onClick = { onModeChange(mode) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (mode == ConsoleLayoutEditMode.Layout) Icons.Filled.GridView else Icons.Filled.Edit,
                            contentDescription = null,
                            tint = if (selectedMode == mode) Color.White else colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(7.dp))
                        Text(
                            text = stringResource(mode.labelRes),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selectedMode == mode) Color.White else colors.textSecondary,
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
    val colors = deckThemeColors(deckUiMode, isSystemInDarkTheme())
    val accent = settingsModeAccent(deckUiMode)
    val classicHeader = deckUiMode == DeckUiMode.Classic
    val classicHeaderBackground = accent.copy(alpha = if (isSystemInDarkTheme()) 0.5f else 0.18f)
    val cardShape = RoundedCornerShape(if (deckUiMode == DeckUiMode.Console) 20.dp else 8.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (deckUiMode == DeckUiMode.Console) {
                    Modifier.consolePanelDropShadow(
                        shape = cardShape,
                        darkTheme = isSystemInDarkTheme()
                    )
                } else {
                    Modifier
                }
            )
            .clip(cardShape)
            .background(colors.cardBackground)
            .then(
                if (deckUiMode == DeckUiMode.Console) {
                    Modifier
                } else {
                    Modifier.border(1.dp, accent.copy(alpha = 0.48f), cardShape)
                }
            ),
        verticalArrangement = Arrangement.spacedBy(if (deckUiMode == DeckUiMode.Console) 12.dp else 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    when {
                        classicHeader -> classicHeaderBackground
                        deckUiMode == DeckUiMode.Console -> colors.consoleSidebar
                        else -> colors.toggleBackground.copy(alpha = 0.54f)
                    }
                )
                .padding(if (deckUiMode == DeckUiMode.Console) 14.dp else 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (deckUiMode == DeckUiMode.Console) {
                ConsoleSettingsIconTile(Icons.Filled.Bluetooth)
            } else {
                SettingsIconTile(Icons.Filled.Bluetooth, accent)
            }
            Text(
                text = stringResource(R.string.settings_hid_management),
                modifier = Modifier.weight(1f),
                style = if (deckUiMode == DeckUiMode.Console) MaterialTheme.typography.titleMedium else MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            SettingsStatusBadge(status.state)
        }
        Column(
            modifier = Modifier.padding(
                start = if (deckUiMode == DeckUiMode.Console) 14.dp else 10.dp,
                end = if (deckUiMode == DeckUiMode.Console) 14.dp else 10.dp,
                bottom = if (deckUiMode == DeckUiMode.Console) 14.dp else 10.dp
            ),
            verticalArrangement = Arrangement.spacedBy(if (deckUiMode == DeckUiMode.Console) 12.dp else 10.dp)
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
                horizontalArrangement = Arrangement.spacedBy(if (deckUiMode == DeckUiMode.Console) 10.dp else 8.dp)
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
    val colors = deckThemeColors(deckUiMode, isSystemInDarkTheme())
    val consoleMode = deckUiMode == DeckUiMode.Console
    val shape = RoundedCornerShape(if (consoleMode) 12.dp else 8.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val background = if (highlighted) {
        Brush.linearGradient(listOf(settingsModeAccent(deckUiMode), Color(0xFF0B7FE8)))
    } else {
        Brush.linearGradient(listOf(colors.actionStart, colors.actionEnd))
    }
    Surface(
        modifier = modifier.then(
            if (consoleMode) {
                Modifier.consoleButtonDropShadow(
                    shape = shape,
                    darkTheme = isSystemInDarkTheme(),
                    pressed = pressed
                )
            } else {
                Modifier
            }
        ),
        color = if (consoleMode) colors.consoleButtonDefault else Color.Transparent,
        contentColor = colors.textPrimary,
        shape = shape,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        interactionSource = interactionSource,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .then(if (consoleMode) Modifier else Modifier.background(background))
                .then(if (consoleMode) Modifier else Modifier.border(1.dp, accent.copy(alpha = 0.42f), shape))
                .padding(horizontal = 10.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (!consoleMode && highlighted) Color.White else colors.textPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(7.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (!consoleMode && highlighted) Color.White else colors.textPrimary,
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
    val colors = deckThemeColors(deckUiMode, isSystemInDarkTheme())
    val accent = settingsModeAccent(deckUiMode)
    val consoleMode = deckUiMode == DeckUiMode.Console
    val shape = RoundedCornerShape(if (consoleMode) 14.dp else 8.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (consoleMode) {
                    Modifier.consoleButtonDropShadow(
                        shape = shape,
                        darkTheme = isSystemInDarkTheme(),
                        pressed = pressed
                    )
                } else {
                    Modifier
                }
            ),
        color = if (consoleMode) colors.consoleButtonDefault else colors.toggleBackground.copy(alpha = 0.58f),
        contentColor = colors.textPrimary,
        shape = shape,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        interactionSource = interactionSource,
        onClick = {
            if (pairingDiscoverable) onCancelDiscoverable() else onMakeDiscoverable()
        }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (consoleMode) {
                ConsoleSettingsIconTile(Icons.Filled.Search)
            } else {
                SettingsIconTile(Icons.Filled.Search, accent)
            }
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
    val colors = deckThemeColors(deckUiMode, isSystemInDarkTheme())
    val accent = settingsModeAccent(deckUiMode)
    val consoleMode = deckUiMode == DeckUiMode.Console
    val shape = RoundedCornerShape(if (consoleMode) 14.dp else 8.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (consoleMode) {
                    Modifier.consoleButtonDropShadow(
                        shape = shape,
                        darkTheme = isSystemInDarkTheme(),
                        pressed = false
                    )
                } else {
                    Modifier
                }
            )
            .clip(shape)
            .background(if (consoleMode) colors.consoleButtonDefault else colors.toggleBackground.copy(alpha = 0.42f))
            .then(if (consoleMode) Modifier else Modifier.border(1.dp, colors.cardBorder.copy(alpha = 0.7f), shape))
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
                val hostShape = RoundedCornerShape(if (consoleMode) 10.dp else 6.dp)
                val hostInteractionSource = remember(host.address) { MutableInteractionSource() }
                val hostPressed by hostInteractionSource.collectIsPressedAsState()
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(
                            if (consoleMode) {
                                Modifier.consoleButtonDropShadow(
                                    shape = hostShape,
                                    darkTheme = isSystemInDarkTheme(),
                                    pressed = hostPressed
                                )
                            } else {
                                Modifier
                            }
                        ),
                    color = if (consoleMode) colors.consoleSidebar else colors.cardBackground.copy(alpha = 0.68f),
                    shape = hostShape,
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp,
                    interactionSource = hostInteractionSource,
                    onClick = { onConnectHost(host) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = pairedHostIcon(host.type),
                            contentDescription = null,
                            tint = if (consoleMode) colors.textPrimary else accent,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
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
}

private fun pairedHostIcon(type: PairedHidHostType): ImageVector {
    return when (type) {
        PairedHidHostType.Computer -> Icons.Filled.Computer
        PairedHidHostType.Tablet -> Icons.Filled.TabletAndroid
        PairedHidHostType.Phone -> Icons.Filled.PhoneAndroid
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
    classicSolidButtonBackground: Boolean,
    classicFontSize: DeckFontSizeOption,
    classicDeckBackground: ClassicDeckBackground,
    pageName: String,
    pageCount: Int,
    logs: List<ActivityLog>,
    debugKeepScreenOn: Boolean,
    onPageSwipeAxisChange: (PageSwipeAxis) -> Unit,
    onPageSwipeModeChange: (PageSwipeMode) -> Unit,
    onPageSwipeAnimationChange: (Boolean) -> Unit,
    onInfinitePageSwipeChange: (Boolean) -> Unit,
    onButtonVibrationLevelChange: (ButtonVibrationLevel) -> Unit,
    onClassicSolidButtonBackgroundChange: (Boolean) -> Unit,
    onClassicFontSizeChange: (DeckFontSizeOption) -> Unit,
    onClassicDeckBackgroundChange: (ClassicDeckBackground) -> Unit,
    onPickClassicDeckBackgroundImage: () -> Unit,
    onLayoutEditor: () -> Unit,
    onExportBundle: () -> Unit,
    onImportBundle: () -> Unit,
    onOpenIconStyleTest: () -> Unit,
    onColumnsChange: (Int) -> Unit,
    onRowsChange: (Int) -> Unit,
    onSpacingChange: (Int) -> Unit,
    onAddPage: () -> Unit,
    onShowClassicTutorial: () -> Unit,
    onDebugKeepScreenOnChange: (Boolean) -> Unit
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
                buttonVibrationLevel = buttonVibrationLevel,
                classicSolidButtonBackground = classicSolidButtonBackground,
                classicFontSize = classicFontSize,
                onLayoutEditor = onLayoutEditor,
                onButtonVibrationLevelChange = onButtonVibrationLevelChange,
                onClassicSolidButtonBackgroundChange = onClassicSolidButtonBackgroundChange,
                onClassicFontSizeChange = onClassicFontSizeChange
            )
        }
        item {
            ClassicBackgroundSettingsCard(
                background = classicDeckBackground,
                onBackgroundChange = onClassicDeckBackgroundChange,
                onPickImage = onPickClassicDeckBackgroundImage
            )
        }
        if (BuildConfig.DEBUG) {
            item {
                SettingsSwitchRow(
                    icon = Icons.Filled.Settings,
                    iconColor = ClassicButtonAccent,
                    title = stringResource(R.string.debug_keep_screen_on_title),
                    subtitle = stringResource(R.string.debug_keep_screen_on_desc),
                    checked = debugKeepScreenOn,
                    onCheckedChange = onDebugKeepScreenOnChange
                )
            }
            item {
                SettingsDiagnosticsCard(logs)
            }
        }
        item {
            SettingsAppInfoRow(
                mode = DeckUiMode.Classic,
                onExportBundle = onExportBundle,
                onImportBundle = onImportBundle,
                onOpenIconStyleTest = onOpenIconStyleTest,
                onShowClassicTutorial = onShowClassicTutorial
            )
        }
    }
}

@Composable
private fun ConsoleSettingsContent(
    category: ConsoleSettingsCategory,
    status: HidStatus,
    deckPages: List<DeckPageConfig>,
    activePageId: Int,
    pageSwipeMode: PageSwipeMode,
    pageSwipeAnimation: Boolean,
    infinitePageSwipe: Boolean,
    buttonVibrationLevel: ButtonVibrationLevel,
    consoleFontSize: DeckFontSizeOption,
    consolePanelOptions: ConsolePanelOptions,
    pageName: String,
    pageCount: Int,
    pairedHosts: List<PairedHidHost>,
    pairingDiscoverable: Boolean,
    logs: List<ActivityLog>,
    debugKeepScreenOn: Boolean,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onMakeDiscoverable: () -> Unit,
    onCancelDiscoverable: () -> Unit,
    onRefreshHosts: () -> Unit,
    onConnectHost: (PairedHidHost) -> Unit,
    onPageSwipeModeChange: (PageSwipeMode) -> Unit,
    onPageSwipeAnimationChange: (Boolean) -> Unit,
    onInfinitePageSwipeChange: (Boolean) -> Unit,
    onButtonVibrationLevelChange: (ButtonVibrationLevel) -> Unit,
    onConsoleFontSizeChange: (DeckFontSizeOption) -> Unit,
    onConsolePanelOptionsChange: (ConsolePanelOptions) -> Unit,
    onConsoleLayoutEditor: (ConsoleLayoutEditMode) -> Unit,
    onAddPage: () -> Unit,
    onExportBundle: () -> Unit,
    onImportBundle: () -> Unit,
    onOpenIconStyleTest: () -> Unit,
    onShowClassicTutorial: () -> Unit,
    onDebugKeepScreenOnChange: (Boolean) -> Unit
) {
    SettingsDetailContent(
        mode = DeckUiMode.Console,
        accent = Color(0xFF00A6E7),
        icon = consoleSettingsCategoryIcon(category),
        title = stringResource(consoleSettingsCategoryTitleRes(category)),
        subtitle = stringResource(consoleSettingsCategorySubtitleRes(category))
    ) {
        when (category) {
            ConsoleSettingsCategory.Bluetooth -> {
                item {
                    BluetoothManagementBox(
                        status = status,
                        deckUiMode = DeckUiMode.Console,
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
            ConsoleSettingsCategory.Layout -> {
                item {
                    ConsolePreviewCard {
                        ConsoleSettingsPreview(panelOptions = consolePanelOptions)
                    }
                }
                item {
                    ConsoleSettingRow(
                        icon = Icons.Filled.GridView,
                        title = stringResource(R.string.console_layout_editor),
                        subtitle = stringResource(R.string.settings_console_layout_desc),
                        trailing = {
                            ConsoleLayoutEntryToggle(
                                modifier = Modifier.width(280.dp),
                                onOpenMode = onConsoleLayoutEditor
                            )
                        }
                    )
                }
                item {
                    ConsolePageSummaryRow(
                        pageName = pageName,
                        pageCount = pageCount,
                        activeIndex = deckPages.indexOfFirst { it.id == activePageId }.coerceAtLeast(0),
                        onAddPage = onAddPage
                    )
                }
            }
            ConsoleSettingsCategory.Background -> {
                item {
                    ConsolePreviewCard {
                        ConsoleSettingsPreview(panelOptions = consolePanelOptions)
                    }
                }
                item {
                    ConsolePanelOptionsCard(
                        options = consolePanelOptions,
                        onOptionsChange = onConsolePanelOptionsChange
                    )
                }
                item {
                    ConsoleFontSizeSettingRow(
                        fontSize = consoleFontSize,
                        onFontSizeChange = onConsoleFontSizeChange
                    )
                }
            }
            ConsoleSettingsCategory.Controls -> {
                item {
                    ConsoleSettingRow(
                        icon = Icons.Filled.SwapHoriz,
                        title = stringResource(R.string.settings_page_direction),
                        subtitle = stringResource(R.string.settings_console_horizontal_desc),
                        trailing = {
                            SettingsValuePill(text = stringResource(R.string.page_axis_horizontal_short))
                        }
                    )
                }
                item {
                    ConsolePageSwipeModeSettingRow(
                        icon = Icons.Filled.TouchApp,
                        pageSwipeMode = pageSwipeMode,
                        onPageSwipeModeChange = onPageSwipeModeChange
                    )
                }
                item {
                    ConsoleSwitchRow(
                        icon = Icons.Filled.Refresh,
                        title = stringResource(R.string.settings_page_wrap),
                        subtitle = stringResource(R.string.settings_page_wrap_desc),
                        checked = infinitePageSwipe,
                        onCheckedChange = onInfinitePageSwipeChange
                    )
                }
                item {
                    ConsoleSwitchRow(
                        icon = Icons.Filled.PlayArrow,
                        title = stringResource(R.string.settings_page_animation),
                        subtitle = stringResource(R.string.settings_page_animation_desc),
                        checked = pageSwipeAnimation,
                        onCheckedChange = onPageSwipeAnimationChange
                    )
                }
                item {
                    ConsoleVibrationSettingRow(
                        buttonVibrationLevel = buttonVibrationLevel,
                        onButtonVibrationLevelChange = onButtonVibrationLevelChange
                    )
                }
            }
            ConsoleSettingsCategory.App -> {
                if (BuildConfig.DEBUG) {
                    item {
                        ConsoleSwitchRow(
                            icon = Icons.Filled.Settings,
                            title = stringResource(R.string.debug_keep_screen_on_title),
                            subtitle = stringResource(R.string.debug_keep_screen_on_desc),
                            checked = debugKeepScreenOn,
                            onCheckedChange = onDebugKeepScreenOnChange
                        )
                    }
                    item {
                        SettingsDiagnosticsCard(logs)
                    }
                }
                item {
                    ConsoleSettingsAppInfoRow(
                        onExportBundle = onExportBundle,
                        onImportBundle = onImportBundle,
                        onOpenIconStyleTest = onOpenIconStyleTest,
                        onShowClassicTutorial = onShowClassicTutorial
                    )
                }
            }
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
    val colors = deckThemeColors(mode, isSystemInDarkTheme())
    val consoleMode = mode == DeckUiMode.Console
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
            contentPadding = if (consoleMode) {
                PaddingValues(start = 22.dp, top = 22.dp, end = 22.dp, bottom = 42.dp)
            } else {
                PaddingValues(start = 14.dp, top = 14.dp, end = 14.dp, bottom = 34.dp)
            },
            verticalArrangement = Arrangement.spacedBy(if (consoleMode) 12.dp else 8.dp)
        ) {
            item {
                if (consoleMode) {
                    ConsoleSettingsHeader(
                        icon = icon,
                        title = title,
                        subtitle = subtitle
                    )
                } else {
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
                shadowElevation = 0.dp
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
private fun ConsoleSettingsHeader(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    val colors = LocalDeckThemeColors.current
    val shape = RoundedCornerShape(20.dp)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .consolePanelDropShadow(shape = shape, darkTheme = isSystemInDarkTheme()),
        shape = shape,
        color = colors.consoleSidebar,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ConsoleSettingsIconTile(icon)
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
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
        }
    }
}

@Composable
private fun SettingsAppInfoRow(
    mode: DeckUiMode,
    onExportBundle: () -> Unit,
    onImportBundle: () -> Unit,
    onOpenIconStyleTest: () -> Unit,
    onShowClassicTutorial: () -> Unit
) {
    val colors = deckThemeColors(mode, isSystemInDarkTheme())
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accent.copy(alpha = 0.82f),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    onClick = onExportBundle
                ) {
                    Text(
                        text = stringResource(R.string.export_bundle),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Button(
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accent.copy(alpha = 0.82f),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    onClick = onImportBundle
                ) {
                    Text(
                        text = stringResource(R.string.import_bundle),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (BuildConfig.DEBUG) {
                    Button(
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accent.copy(alpha = 0.82f),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        onClick = onOpenIconStyleTest
                    ) {
                        Text(
                            text = "아이콘 테스트",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Button(
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accent,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    onClick = onShowClassicTutorial
                ) {
                    Text(
                        text = stringResource(R.string.tutorial),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private enum class ConsoleIconStyleVariant {
    AgslGradient,
    BrushGradientVertical,
    BrushGradientDiagonal,
    BrushGradientSoft,
    CloneRim1Px,
    CloneRim2Px,
    RoundNoPath,
    SharpPathThin,
    RoundPathThin,
    AutoPathAngularHairline,
    AutoPathAngularThin,
    AutoPathAngularThick,
    AutoPathAngularStrong,
    AutoPathRound,
    ThickNoPath,
    ThickPathThin,
    ThickPathMedium,
    ModifierShadowLow,
    ModifierShadowHigh,
    DropShadowLow,
    DropShadowMedium,
    DropShadowHigh
}

private enum class ConsoleIconStyleTone(
    val title: String,
    val bakedTone: BakedIconTone
) {
    MatteSlate("Matte slate", BakedIconTone.MatteSlate),
    MattePearl("Matte pearl", BakedIconTone.MattePearl),
    BlueGray("Blue gray", BakedIconTone.BlueGray)
}

private data class ConsoleIconStyleSample(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val tone: ConsoleIconStyleTone,
    val variant: ConsoleIconStyleVariant
)

@Composable
private fun ConsoleIconStyleTestPage(
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    var previewDarkTheme by remember { mutableStateOf(false) }
    var selectedIconIndex by remember { mutableStateOf(0) }
    var iconSize by remember { mutableStateOf(72f) }
    var dropShadowEnabled by remember { mutableStateOf(true) }
    var shadowRadius by remember { mutableStateOf(14f) }
    var shadowSpread by remember { mutableStateOf(3f) }
    var shadowOffsetX by remember { mutableStateOf(0f) }
    var shadowOffsetY by remember { mutableStateOf(4f) }
    var shadowAlpha by remember { mutableStateOf(0.26f) }
    var bakedShadowEnabled by remember { mutableStateOf(true) }
    var bakedShadowScale by remember { mutableStateOf(1.25f) }
    var cloneRimEnabled by remember { mutableStateOf(true) }
    var cloneRimOffset by remember { mutableStateOf(1.4f) }
    var cloneRimAlpha by remember { mutableStateOf(0.42f) }
    var hairlineEnabled by remember { mutableStateOf(true) }
    var hairlineAlpha by remember { mutableStateOf(0.34f) }
    var iconAlpha by remember { mutableStateOf(1f) }
    var buttonPressedPreview by remember { mutableStateOf(false) }
    var buttonBackgroundAlpha by remember { mutableStateOf(1f) }
    var buttonCornerRadius by remember { mutableStateOf(12f) }
    var buttonWidth by remember { mutableStateOf(96f) }
    var buttonHeight by remember { mutableStateOf(38f) }
    var buttonHorizontalPadding by remember { mutableStateOf(14f) }
    var buttonVerticalPadding by remember { mutableStateOf(8f) }
    var buttonTextSize by remember { mutableStateOf(14f) }
    var buttonRimEnabled by remember { mutableStateOf(true) }
    var buttonRimAlpha by remember { mutableStateOf(0.24f) }
    var buttonShadowEnabled by remember { mutableStateOf(true) }
    var buttonShadowRadius by remember { mutableStateOf(18f) }
    var buttonShadowSpread by remember { mutableStateOf(6f) }
    var buttonShadowOffsetY by remember { mutableStateOf(4f) }
    var buttonShadowAlpha by remember { mutableStateOf(0.34f) }
    var buttonHairlineEnabled by remember { mutableStateOf(true) }
    var buttonHairlineAlpha by remember { mutableStateOf(0.22f) }
    var buttonHairlineOffset by remember { mutableStateOf(1f) }
    val colors = deckThemeColors(DeckUiMode.Console, darkTheme = previewDarkTheme)
    val previewIcons = remember {
        listOf(
            Icons.Filled.Keyboard to "Keyboard",
            Icons.Filled.PlayArrow to "Play",
            Icons.Filled.Bluetooth to "Bluetooth",
            Icons.Filled.Settings to "Settings",
            Icons.Filled.VolumeUp to "Volume"
        )
    }
    val selectedIcon = previewIcons[selectedIconIndex.coerceIn(previewIcons.indices)].first
    fun resetButtonValues() {
        buttonPressedPreview = false
        buttonBackgroundAlpha = 1f
        buttonCornerRadius = 12f
        buttonWidth = 96f
        buttonHeight = 38f
        buttonHorizontalPadding = 14f
        buttonVerticalPadding = 8f
        buttonTextSize = 14f
        buttonRimEnabled = true
        buttonRimAlpha = if (previewDarkTheme) 0.18f else 0.32f
        buttonShadowEnabled = true
        buttonShadowRadius = 18f
        buttonShadowSpread = 6f
        buttonShadowOffsetY = 4f
        buttonShadowAlpha = if (previewDarkTheme) 0.34f else 0.14f
        buttonHairlineEnabled = true
        buttonHairlineAlpha = if (previewDarkTheme) 0.22f else 0.92f
        buttonHairlineOffset = 1f
    }

    CompositionLocalProvider(LocalDeckThemeColors provides colors) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Brush.linearGradient(colors.backgroundGradient))
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary)
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("설정")
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Console icon tuning",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "dropShadow, baked shadow, rim, hairline 값을 실시간으로 조절합니다.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary
                        )
                    }
                    ConsoleIconStyleThemeButton(
                        text = "화이트",
                        selected = !previewDarkTheme,
                        colors = colors,
                        onClick = { previewDarkTheme = false }
                    )
                    ConsoleIconStyleThemeButton(
                        text = "블랙",
                        selected = previewDarkTheme,
                        colors = colors,
                        onClick = { previewDarkTheme = true }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ConsoleIconTuningPreview(
                        modifier = Modifier
                            .width(420.dp)
                            .fillMaxHeight(),
                        icon = selectedIcon,
                        iconSize = iconSize.dp,
                        dropShadowEnabled = dropShadowEnabled,
                        shadowRadius = shadowRadius.dp,
                        shadowSpread = shadowSpread.dp,
                        shadowOffset = DpOffset(shadowOffsetX.dp, shadowOffsetY.dp),
                        shadowAlpha = shadowAlpha,
                        bakedShadowEnabled = bakedShadowEnabled,
                        bakedShadowScale = bakedShadowScale,
                        cloneRimEnabled = cloneRimEnabled,
                        cloneRimOffset = cloneRimOffset.dp,
                        cloneRimAlpha = cloneRimAlpha,
                        hairlineEnabled = hairlineEnabled,
                        hairlineAlpha = hairlineAlpha,
                        iconAlpha = iconAlpha,
                        buttonWidth = buttonWidth.dp,
                        buttonHeight = buttonHeight.dp,
                        buttonHorizontalPadding = buttonHorizontalPadding.dp,
                        buttonVerticalPadding = buttonVerticalPadding.dp,
                        buttonTextSize = buttonTextSize,
                        buttonPressedPreview = buttonPressedPreview,
                        buttonBackgroundAlpha = buttonBackgroundAlpha,
                        buttonCornerRadius = buttonCornerRadius.dp,
                        buttonRimEnabled = buttonRimEnabled,
                        buttonRimAlpha = buttonRimAlpha,
                        buttonShadowEnabled = buttonShadowEnabled,
                        buttonShadowRadius = buttonShadowRadius.dp,
                        buttonShadowSpread = buttonShadowSpread.dp,
                        buttonShadowOffsetY = buttonShadowOffsetY.dp,
                        buttonShadowAlpha = buttonShadowAlpha,
                        buttonHairlineEnabled = buttonHairlineEnabled,
                        buttonHairlineAlpha = buttonHairlineAlpha,
                        buttonHairlineOffset = buttonHairlineOffset.dp,
                        colors = colors,
                        darkTheme = previewDarkTheme
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ConsoleIconTuningCard(title = "아이콘") {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                previewIcons.forEachIndexed { index, (icon, label) ->
                                    ConsoleIconChoiceButton(
                                        icon = icon,
                                        label = label,
                                        selected = selectedIconIndex == index,
                                        colors = colors,
                                        onClick = { selectedIconIndex = index }
                                    )
                                }
                            }
                            ConsoleIconTuningSlider("아이콘 크기", iconSize, 40f..110f) { iconSize = it }
                            ConsoleIconTuningSlider("아이콘 알파", iconAlpha, 0.2f..1f) { iconAlpha = it }
                        }
                        ConsoleIconTuningCard(title = "dropShadow") {
                            ConsoleIconTuningSwitch("사용", dropShadowEnabled) { dropShadowEnabled = it }
                            ConsoleIconTuningSlider("radius", shadowRadius, 0f..34f) { shadowRadius = it }
                            ConsoleIconTuningSlider("spread", shadowSpread, 0f..12f) { shadowSpread = it }
                            ConsoleIconTuningSlider("offset X", shadowOffsetX, -12f..12f) { shadowOffsetX = it }
                            ConsoleIconTuningSlider("offset Y", shadowOffsetY, -12f..18f) { shadowOffsetY = it }
                            ConsoleIconTuningSlider("alpha", shadowAlpha, 0f..0.7f) { shadowAlpha = it }
                        }
                        ConsoleIconTuningCard(title = "baked shadow") {
                            ConsoleIconTuningSwitch("사용", bakedShadowEnabled) { bakedShadowEnabled = it }
                            ConsoleIconTuningSlider("render scale", bakedShadowScale, 1.0f..1.8f) { bakedShadowScale = it }
                        }
                        ConsoleIconTuningCard(title = "clone rim / hairline") {
                            ConsoleIconTuningSwitch("clone rim", cloneRimEnabled) { cloneRimEnabled = it }
                            ConsoleIconTuningSlider("rim offset", cloneRimOffset, 0f..4f) { cloneRimOffset = it }
                            ConsoleIconTuningSlider("rim alpha", cloneRimAlpha, 0f..1f) { cloneRimAlpha = it }
                            ConsoleIconTuningSwitch("hairline", hairlineEnabled) { hairlineEnabled = it }
                            ConsoleIconTuningSlider("hairline alpha", hairlineAlpha, 0f..1f) { hairlineAlpha = it }
                        }
                        ConsoleIconTuningCard(title = "아이콘 테스트 버튼 효과") {
                            ConsoleIconTuningResetButton(text = "현재 기본값으로 초기화", onClick = ::resetButtonValues)
                            ConsoleIconTuningSwitch("pressed preview", buttonPressedPreview) { buttonPressedPreview = it }
                            ConsoleIconTuningSlider("background alpha", buttonBackgroundAlpha, 0.2f..1f) { buttonBackgroundAlpha = it }
                            ConsoleIconTuningSlider("corner radius", buttonCornerRadius, 4f..24f) { buttonCornerRadius = it }
                            ConsoleIconTuningSlider("button width", buttonWidth, 64f..180f) { buttonWidth = it }
                            ConsoleIconTuningSlider("button height", buttonHeight, 28f..64f) { buttonHeight = it }
                            ConsoleIconTuningSlider("padding X", buttonHorizontalPadding, 6f..28f) { buttonHorizontalPadding = it }
                            ConsoleIconTuningSlider("padding Y", buttonVerticalPadding, 3f..18f) { buttonVerticalPadding = it }
                            ConsoleIconTuningSlider("text size", buttonTextSize, 10f..20f) { buttonTextSize = it }
                            ConsoleIconTuningSwitch("white rim", buttonRimEnabled) { buttonRimEnabled = it }
                            ConsoleIconTuningSlider("rim alpha", buttonRimAlpha, 0f..0.7f) { buttonRimAlpha = it }
                            ConsoleIconTuningSwitch("dropShadow", buttonShadowEnabled) { buttonShadowEnabled = it }
                            ConsoleIconTuningSlider("shadow radius", buttonShadowRadius, 0f..34f) { buttonShadowRadius = it }
                            ConsoleIconTuningSlider("shadow spread", buttonShadowSpread, 0f..14f) { buttonShadowSpread = it }
                            ConsoleIconTuningSlider("shadow offset Y", buttonShadowOffsetY, -4f..16f) { buttonShadowOffsetY = it }
                            ConsoleIconTuningSlider("shadow alpha", buttonShadowAlpha, 0f..0.7f) { buttonShadowAlpha = it }
                            ConsoleIconTuningSwitch("hairline", buttonHairlineEnabled) { buttonHairlineEnabled = it }
                            ConsoleIconTuningSlider("hairline alpha", buttonHairlineAlpha, 0f..0.8f) { buttonHairlineAlpha = it }
                            ConsoleIconTuningSlider("hairline offset", buttonHairlineOffset, 0f..5f) { buttonHairlineOffset = it }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConsoleIconTuningPreview(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconSize: Dp,
    dropShadowEnabled: Boolean,
    shadowRadius: Dp,
    shadowSpread: Dp,
    shadowOffset: DpOffset,
    shadowAlpha: Float,
    bakedShadowEnabled: Boolean,
    bakedShadowScale: Float,
    cloneRimEnabled: Boolean,
    cloneRimOffset: Dp,
    cloneRimAlpha: Float,
    hairlineEnabled: Boolean,
    hairlineAlpha: Float,
    iconAlpha: Float,
    buttonWidth: Dp,
    buttonHeight: Dp,
    buttonHorizontalPadding: Dp,
    buttonVerticalPadding: Dp,
    buttonTextSize: Float,
    buttonPressedPreview: Boolean,
    buttonBackgroundAlpha: Float,
    buttonCornerRadius: Dp,
    buttonRimEnabled: Boolean,
    buttonRimAlpha: Float,
    buttonShadowEnabled: Boolean,
    buttonShadowRadius: Dp,
    buttonShadowSpread: Dp,
    buttonShadowOffsetY: Dp,
    buttonShadowAlpha: Float,
    buttonHairlineEnabled: Boolean,
    buttonHairlineAlpha: Float,
    buttonHairlineOffset: Dp,
    colors: DeckThemeColors,
    darkTheme: Boolean
) {
    val shape = RoundedCornerShape(28.dp)
    val iconContainerSize = iconSize * bakedShadowScale.coerceAtLeast(1f)
    val iconSizePx = with(LocalDensity.current) { iconSize.toPx().roundToInt() }
    val shadowSizePx = with(LocalDensity.current) { iconContainerSize.toPx().roundToInt() }
    val bakedShadow = rememberBakedVectorIconShadow(
        key = "icon-tuning:${icon.name}:$iconSizePx:$shadowSizePx:$darkTheme",
        imageVector = icon,
        sizePx = iconSizePx,
        shadowSizePx = shadowSizePx,
        enabled = bakedShadowEnabled,
        lightMode = !darkTheme
    )
    Surface(
        modifier = modifier,
        shape = shape,
        color = colors.consolePreviewBackground,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .border(0.7.dp, colors.cardBorder.copy(alpha = 0.58f), shape)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Preview",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(28.dp),
                color = colors.consoleButtonDefault,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(iconContainerSize)
                            .then(
                                if (dropShadowEnabled) {
                                    Modifier.dropShadow(
                                        shape = CircleShape,
                                        shadow = DropShadow(
                                            radius = shadowRadius,
                                            spread = shadowSpread,
                                            offset = shadowOffset,
                                            color = Color.Black.copy(alpha = shadowAlpha.coerceIn(0f, 1f))
                                        )
                                    )
                                } else {
                                    Modifier
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        bakedShadow?.let { shadow ->
                            Image(
                                bitmap = shadow,
                                contentDescription = null,
                                modifier = Modifier.size(iconContainerSize)
                            )
                        }
                        if (cloneRimEnabled) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = Color.Black.copy(alpha = cloneRimAlpha.coerceIn(0f, 1f)),
                                modifier = Modifier
                                    .offset(x = cloneRimOffset, y = cloneRimOffset)
                                    .size(iconSize)
                            )
                        }
                        if (hairlineEnabled) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = hairlineAlpha.coerceIn(0f, 1f)),
                                modifier = Modifier
                                    .offset(x = (-cloneRimOffset / 2f), y = (-cloneRimOffset / 2f))
                                    .size(iconSize)
                            )
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = iconAlpha.coerceIn(0f, 1f)),
                            modifier = Modifier.size(iconSize)
                        )
                    }
                }
            }
            Text(
                text = "dropShadow + baked shadow + clone rim + hairline",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textSecondary,
                textAlign = TextAlign.Center
            )
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = colors.consoleButtonDefault.copy(alpha = 0.34f),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "작은 텍스트 버튼 미리보기",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ConsoleButtonEffectPreview(
                            text = "아이콘 테스트",
                            buttonWidth = buttonWidth,
                            buttonHeight = buttonHeight,
                            horizontalPadding = buttonHorizontalPadding,
                            verticalPadding = buttonVerticalPadding,
                            textSize = buttonTextSize,
                            pressed = buttonPressedPreview,
                            backgroundAlpha = buttonBackgroundAlpha,
                            cornerRadius = buttonCornerRadius,
                            rimEnabled = buttonRimEnabled,
                            rimAlpha = buttonRimAlpha,
                            shadowEnabled = buttonShadowEnabled,
                            shadowRadius = buttonShadowRadius,
                            shadowSpread = buttonShadowSpread,
                            shadowOffsetY = buttonShadowOffsetY,
                            shadowAlpha = buttonShadowAlpha,
                            hairlineEnabled = buttonHairlineEnabled,
                            hairlineAlpha = buttonHairlineAlpha,
                            hairlineOffset = buttonHairlineOffset,
                            colors = colors,
                            darkTheme = darkTheme
                        )
                        ConsoleButtonEffectPreview(
                            text = "튜토리얼",
                            buttonWidth = buttonWidth * 0.82f,
                            buttonHeight = buttonHeight,
                            horizontalPadding = buttonHorizontalPadding,
                            verticalPadding = buttonVerticalPadding,
                            textSize = buttonTextSize,
                            pressed = false,
                            backgroundAlpha = buttonBackgroundAlpha,
                            cornerRadius = buttonCornerRadius,
                            rimEnabled = buttonRimEnabled,
                            rimAlpha = buttonRimAlpha,
                            shadowEnabled = buttonShadowEnabled,
                            shadowRadius = buttonShadowRadius,
                            shadowSpread = buttonShadowSpread,
                            shadowOffsetY = buttonShadowOffsetY,
                            shadowAlpha = buttonShadowAlpha,
                            hairlineEnabled = buttonHairlineEnabled,
                            hairlineAlpha = buttonHairlineAlpha,
                            hairlineOffset = buttonHairlineOffset,
                            colors = colors,
                            darkTheme = darkTheme
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConsoleButtonEffectPreview(
    text: String,
    buttonWidth: Dp,
    buttonHeight: Dp,
    horizontalPadding: Dp,
    verticalPadding: Dp,
    textSize: Float,
    pressed: Boolean,
    backgroundAlpha: Float,
    cornerRadius: Dp,
    rimEnabled: Boolean,
    rimAlpha: Float,
    shadowEnabled: Boolean,
    shadowRadius: Dp,
    shadowSpread: Dp,
    shadowOffsetY: Dp,
    shadowAlpha: Float,
    hairlineEnabled: Boolean,
    hairlineAlpha: Float,
    hairlineOffset: Dp,
    colors: DeckThemeColors,
    darkTheme: Boolean
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = Modifier
            .width(buttonWidth + hairlineOffset * 2f + shadowSpread * 2f)
            .height(buttonHeight + hairlineOffset * 2f + shadowRadius * 0.35f),
        contentAlignment = Alignment.Center
    ) {
        if (hairlineEnabled) {
            Box(
                modifier = Modifier
                    .offset(
                        x = if (pressed) hairlineOffset else -hairlineOffset,
                        y = if (pressed) hairlineOffset else -hairlineOffset
                    )
                    .width(buttonWidth)
                    .height(buttonHeight)
                    .clip(shape)
                    .background(consoleHairlineColor(darkTheme).copy(alpha = hairlineAlpha.coerceIn(0f, 1f)))
            )
        }
        Surface(
            modifier = Modifier
                .then(
                    if (rimEnabled) {
                        Modifier.dropShadow(
                            shape = shape,
                            shadow = DropShadow(
                                radius = 0.dp,
                                spread = if (pressed) 0.8.dp else 1.4.dp,
                                offset = DpOffset.Zero,
                                color = Color.White.copy(alpha = rimAlpha.coerceIn(0f, 1f))
                            )
                        )
                    } else {
                        Modifier
                    }
                )
                .then(
                    if (shadowEnabled) {
                        Modifier.dropShadow(
                            shape = shape,
                            shadow = DropShadow(
                                radius = if (pressed) shadowRadius * 0.55f else shadowRadius,
                                spread = if (pressed) shadowSpread * 0.45f else shadowSpread,
                                offset = DpOffset(0.dp, if (pressed) shadowOffsetY * 0.35f else shadowOffsetY),
                                color = Color.Black.copy(alpha = shadowAlpha.coerceIn(0f, 1f))
                            )
                        )
                    } else {
                        Modifier
                    }
                ),
            shape = shape,
            color = colors.consoleButtonDefault.copy(alpha = backgroundAlpha.coerceIn(0f, 1f)),
            contentColor = colors.textPrimary,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            onClick = {}
        ) {
            Box(
                modifier = Modifier
                    .width(buttonWidth)
                    .height(buttonHeight)
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(fontSize = textSize.sp),
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ConsoleIconTuningCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = LocalDeckThemeColors.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = colors.consolePreviewBackground,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .border(0.7.dp, colors.cardBorder.copy(alpha = 0.52f), RoundedCornerShape(18.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            content()
        }
    }
}

@Composable
private fun ConsoleIconTuningSlider(
    label: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.textPrimary
            )
            Text(
                text = if (range.endInclusive <= 1.1f) "%.2f".format(value) else "%.1f".format(value),
                style = MaterialTheme.typography.labelMedium,
                color = colors.textSecondary
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = range
        )
    }
}

@Composable
private fun ConsoleIconTuningSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = colors.textPrimary
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun ConsoleIconTuningResetButton(
    text: String,
    onClick: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colors.consoleButtonFeatured.copy(alpha = 0.22f),
        contentColor = colors.textPrimary,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = colors.textPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ConsoleIconChoiceButton(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    colors: DeckThemeColors,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.size(54.dp),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) colors.consoleButtonFeatured else colors.consoleButtonDefault,
        contentColor = if (selected) Color.White else colors.textPrimary,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun ConsoleIconStyleThemeButton(
    text: String,
    selected: Boolean,
    colors: DeckThemeColors,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(10.dp)
    val containerColor = if (selected) colors.consoleButtonFeatured else colors.consoleButtonDefault
    Button(
        onClick = onClick,
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = if (selected) Color.White else colors.textPrimary
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun ConsoleIconStyleSidebarSample(
    modifier: Modifier,
    colors: DeckThemeColors,
    darkTheme: Boolean
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = colors.sidebarBackground,
        shadowElevation = 7.dp,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(26.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                Box(
                    modifier = Modifier
                        .size(width = 38.dp, height = 24.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRoundRect(
                            color = Color(0xFF79B9EA),
                            topLeft = Offset(size.width * 0.05f, size.height * 0.18f),
                            size = Size(size.width * 0.16f, size.height * 0.64f),
                            cornerRadius = CornerRadius(12f, 12f)
                        )
                        drawRoundRect(
                            color = Color(0xFF79B9EA),
                            topLeft = Offset(size.width * 0.34f, size.height * 0.18f),
                            size = Size(size.width * 0.16f, size.height * 0.64f),
                            cornerRadius = CornerRadius(12f, 12f)
                        )
                    }
                }
                Text(
                    text = "MobileDeck",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = colors.textPrimary
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    Box(
                        modifier = Modifier
                            .size(13.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE53935))
                    )
                    Text(
                        text = "연결 안 됨",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE53935)
                    )
                }
                Text(
                    text = "Ready to register\nBluetooth keyboard",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colors.textSecondary
                )
            }
                ConsoleIconStylePreviewIcon(
                    sample = ConsoleIconStyleSample(
                        title = "Settings",
                    subtitle = "",
                    icon = Icons.Filled.Settings,
                    tone = ConsoleIconStyleTone.MatteSlate,
                        variant = ConsoleIconStyleVariant.CloneRim1Px
                    ),
                    iconSize = 54.dp,
                    darkTheme = darkTheme
                )
            }
        }
}

@Composable
private fun ConsoleIconStyleSampleCard(
    modifier: Modifier,
    sample: ConsoleIconStyleSample,
    colors: DeckThemeColors,
    darkTheme: Boolean
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = colors.consoleButtonDefault,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ConsoleIconStylePreviewIcon(
                    sample = sample,
                    iconSize = 72.dp,
                    darkTheme = darkTheme
                )
                Text(
                    text = sample.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = sample.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ConsoleIconStylePreviewIcon(
    sample: ConsoleIconStyleSample,
    iconSize: Dp,
    darkTheme: Boolean
) {
    val shadowRenderSize = iconSize * 1.25f
    val iconSizePx = with(LocalDensity.current) { iconSize.toPx().roundToInt() }
    val shadowSizePx = with(LocalDensity.current) { shadowRenderSize.toPx().roundToInt() }
    val bakedShadow = rememberBakedVectorIconShadow(
        key = "icon-style-test:${sample.title}:${sample.subtitle}:${sample.icon.name}",
        imageVector = sample.icon,
        sizePx = iconSizePx,
        shadowSizePx = shadowSizePx,
        enabled = true,
        lightMode = !darkTheme
    )
    Box(
        modifier = Modifier.size(shadowRenderSize),
        contentAlignment = Alignment.Center
    ) {
        bakedShadow?.let { shadow ->
            Image(
                bitmap = shadow,
                contentDescription = null,
                modifier = Modifier.size(shadowRenderSize)
            )
        }
        when (sample.variant) {
            ConsoleIconStyleVariant.SharpPathThin,
            ConsoleIconStyleVariant.RoundPathThin,
            ConsoleIconStyleVariant.AutoPathAngularHairline,
            ConsoleIconStyleVariant.AutoPathAngularThin,
            ConsoleIconStyleVariant.AutoPathAngularThick,
            ConsoleIconStyleVariant.AutoPathAngularStrong,
            ConsoleIconStyleVariant.AutoPathRound,
            ConsoleIconStyleVariant.ThickPathThin,
            ConsoleIconStyleVariant.ThickPathMedium -> ConsoleBakedGeometryIcon(
                sample = sample,
                iconSize = iconSize
            )
            ConsoleIconStyleVariant.ModifierShadowLow,
            ConsoleIconStyleVariant.ModifierShadowHigh -> ConsoleModifierShadowIcon(
                sample = sample,
                iconSize = iconSize
            )
            ConsoleIconStyleVariant.DropShadowLow,
            ConsoleIconStyleVariant.DropShadowMedium,
            ConsoleIconStyleVariant.DropShadowHigh -> ConsoleDropShadowIcon(
                sample = sample,
                iconSize = iconSize
            )
            else -> ConsoleAgslGlossIcon(
                sample = sample,
                iconSize = iconSize
            )
        }
    }
}

@Composable
private fun ConsoleModifierShadowIcon(
    sample: ConsoleIconStyleSample,
    iconSize: Dp
) {
    val elevation = when (sample.variant) {
        ConsoleIconStyleVariant.ModifierShadowHigh -> 12.dp
        else -> 5.dp
    }
    Box(
        modifier = Modifier
            .size(iconSize * 1.18f)
            .shadow(
                elevation = elevation,
                shape = CircleShape,
                clip = false,
                ambientColor = Color(0xFF2C4058).copy(alpha = 0.34f),
                spotColor = Color(0xFF2C4058).copy(alpha = 0.42f)
            ),
        contentAlignment = Alignment.Center
    ) {
        ConsoleAgslGlossIcon(
            sample = sample.copy(variant = ConsoleIconStyleVariant.AgslGradient),
            iconSize = iconSize
        )
    }
}

@Composable
private fun ConsoleDropShadowIcon(
    sample: ConsoleIconStyleSample,
    iconSize: Dp
) {
    val offset = with(LocalDensity.current) {
        when (sample.variant) {
            ConsoleIconStyleVariant.DropShadowHigh -> 5.toDp()
            ConsoleIconStyleVariant.DropShadowMedium -> 3.toDp()
            ConsoleIconStyleVariant.DropShadowLow -> 3.toDp()
            else -> 0.toDp()
        }
    }
    val alpha = when (sample.variant) {
        ConsoleIconStyleVariant.DropShadowHigh -> 0.30f
        ConsoleIconStyleVariant.DropShadowMedium -> 0.22f
        ConsoleIconStyleVariant.DropShadowLow -> 0.22f
        else -> 0f
    }
    Box(
        modifier = Modifier
                .size(iconSize * 1.2f),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = sample.icon,
            contentDescription = null,
            modifier = Modifier
                .size(iconSize)
                .offset(x = offset, y = offset),
            tint = Color(0xFF2C4058).copy(alpha = alpha)
        )
        if (sample.variant == ConsoleIconStyleVariant.DropShadowHigh) {
            Icon(
                imageVector = sample.icon,
                contentDescription = null,
                modifier = Modifier
                    .size(iconSize)
                    .offset(x = offset * 0.55f, y = offset * 0.55f),
                tint = Color(0xFF2C4058).copy(alpha = 0.12f)
            )
        }
        ConsoleAgslGlossIcon(
            sample = sample.copy(variant = ConsoleIconStyleVariant.AgslGradient),
            iconSize = iconSize
        )
    }
}

@Composable
private fun ConsoleBakedGeometryIcon(
    sample: ConsoleIconStyleSample,
    iconSize: Dp
) {
    val style = when (sample.variant) {
        ConsoleIconStyleVariant.SharpPathThin -> BakedIconBodyStyle.SharpThin
        ConsoleIconStyleVariant.RoundPathThin -> BakedIconBodyStyle.RoundThin
        ConsoleIconStyleVariant.AutoPathAngularHairline -> BakedIconBodyStyle.SharpHairline
        ConsoleIconStyleVariant.AutoPathAngularThin -> BakedIconBodyStyle.SharpThin
        ConsoleIconStyleVariant.AutoPathAngularThick -> BakedIconBodyStyle.ThickThin
        ConsoleIconStyleVariant.AutoPathAngularStrong -> BakedIconBodyStyle.ThickMedium
        ConsoleIconStyleVariant.AutoPathRound -> BakedIconBodyStyle.RoundThin
        ConsoleIconStyleVariant.ThickPathMedium -> BakedIconBodyStyle.ThickMedium
        else -> BakedIconBodyStyle.ThickThin
    }
    val sizePx = with(LocalDensity.current) { iconSize.toPx().roundToInt() }
    val image = rememberBakedVectorIconBody(
        key = "geometry-${sample.title}-${sample.icon.name}",
        imageVector = sample.icon,
        sizePx = sizePx,
        style = style,
        tone = sample.tone.bakedTone,
        enabled = true
    )
    if (image != null) {
        Image(
            bitmap = image,
            contentDescription = sample.title,
            modifier = Modifier.size(iconSize)
        )
    }
}

private const val CONSOLE_ICON_MATTE_SLATE_SHADER = """
uniform float2 resolution;

half4 main(float2 coord) {
    float2 uv = coord / resolution;
    float top = 1.0 - smoothstep(0.0, 0.92, uv.y);
    float edge = 1.0 - smoothstep(0.0, 0.10, min(min(uv.x, 1.0 - uv.x), min(uv.y, 1.0 - uv.y)));
    half3 color = mix(half3(0.37, 0.47, 0.60), half3(0.55, 0.66, 0.79), top * 0.55);
    color += half3(0.08, 0.10, 0.12) * edge * 0.18;
    color += half3(0.11, 0.15, 0.18) * (1.0 - uv.y) * 0.12;
    return half4(color, 1.0);
}
"""

private const val CONSOLE_ICON_MATTE_PEARL_SHADER = """
uniform float2 resolution;

half4 main(float2 coord) {
    float2 uv = coord / resolution;
    float top = 1.0 - smoothstep(0.0, 0.86, uv.y);
    float edge = 1.0 - smoothstep(0.0, 0.09, min(min(uv.x, 1.0 - uv.x), min(uv.y, 1.0 - uv.y)));
    half3 color = mix(half3(0.76, 0.84, 0.92), half3(0.96, 0.99, 1.0), top * 0.52);
    color = mix(color, half3(0.60, 0.70, 0.82), edge * 0.16);
    return half4(color, 1.0);
}
"""

private const val CONSOLE_ICON_MATTE_BLUE_GRAY_SHADER = """
uniform float2 resolution;

half4 main(float2 coord) {
    float2 uv = coord / resolution;
    float vertical = 1.0 - smoothstep(0.0, 1.0, uv.y);
    float diagonal = smoothstep(0.0, 1.0, (1.0 - uv.y) * 0.7 + uv.x * 0.3);
    float edge = 1.0 - smoothstep(0.0, 0.08, min(min(uv.x, 1.0 - uv.x), min(uv.y, 1.0 - uv.y)));
    half3 color = mix(half3(0.36, 0.48, 0.62), half3(0.68, 0.80, 0.91), vertical * 0.42 + diagonal * 0.12);
    color += half3(0.04, 0.08, 0.12) * edge * 0.18;
    return half4(color, 1.0);
}
"""

private const val CONSOLE_ICON_THIN_BEVEL_SHADER = """
uniform float2 resolution;

half4 main(float2 coord) {
    float2 uv = coord / resolution;
    float topLeft = (1.0 - uv.x) * 0.42 + (1.0 - uv.y) * 0.58;
    float bottomRight = uv.x * 0.42 + uv.y * 0.58;
    float edge = 1.0 - smoothstep(0.0, 0.055, min(min(uv.x, 1.0 - uv.x), min(uv.y, 1.0 - uv.y)));
    half3 color = half3(0.44, 0.55, 0.68);
    color += half3(0.20, 0.24, 0.28) * topLeft * 0.20;
    color -= half3(0.12, 0.15, 0.18) * bottomRight * 0.18;
    color += half3(0.14, 0.17, 0.20) * edge * 0.16;
    return half4(color, 1.0);
}
"""

private const val CONSOLE_ICON_SOFT_CENTER_SHADER = """
uniform float2 resolution;

half4 main(float2 coord) {
    float2 uv = coord / resolution;
    float center = 1.0 - smoothstep(0.0, 0.78, distance(uv, float2(0.46, 0.36)));
    float top = 1.0 - smoothstep(0.0, 0.94, uv.y);
    float edge = 1.0 - smoothstep(0.0, 0.08, min(min(uv.x, 1.0 - uv.x), min(uv.y, 1.0 - uv.y)));
    half3 color = half3(0.48, 0.59, 0.72);
    color += half3(0.16, 0.20, 0.24) * center * 0.26;
    color += half3(0.08, 0.10, 0.12) * top * 0.10;
    color -= half3(0.09, 0.12, 0.15) * edge * 0.13;
    return half4(color, 1.0);
}
"""

private const val CONSOLE_ICON_MATTE_BLUE_SHADER = """
uniform float2 resolution;

half4 main(float2 coord) {
    float2 uv = coord / resolution;
    float top = 1.0 - smoothstep(0.0, 0.92, uv.y);
    float edge = 1.0 - smoothstep(0.0, 0.08, min(min(uv.x, 1.0 - uv.x), min(uv.y, 1.0 - uv.y)));
    half3 color = mix(half3(0.12, 0.46, 0.82), half3(0.58, 0.80, 0.96), top * 0.42);
    color = mix(color, half3(0.07, 0.28, 0.56), edge * 0.14);
    return half4(color, 1.0);
}
"""

@Composable
private fun ConsoleAgslGlossIcon(
    sample: ConsoleIconStyleSample,
    iconSize: Dp
) {
    val shaderBrush = if (sample.variant == ConsoleIconStyleVariant.AgslGradient) {
        rememberAgslGlossBrush(sample.tone)
    } else {
        null
    }
    val fallbackBrush = fallbackIconGlossBrush(sample.tone, sample.variant)
    val rimOffset = with(LocalDensity.current) {
        when (sample.variant) {
            ConsoleIconStyleVariant.CloneRim1Px -> 1.toDp()
            ConsoleIconStyleVariant.CloneRim2Px -> 2.toDp()
            else -> 0.dp
        }
    }
    Box(
        modifier = Modifier.size(iconSize * 1.08f),
        contentAlignment = Alignment.Center
    ) {
        if (rimOffset > 0.dp) {
            Icon(
                imageVector = sample.icon,
                contentDescription = null,
                modifier = Modifier
                    .size(iconSize)
                    .offset(x = -rimOffset, y = -rimOffset),
                tint = Color.White.copy(alpha = 0.39f)
            )
            Icon(
                imageVector = sample.icon,
                contentDescription = null,
                modifier = Modifier
                    .size(iconSize)
                    .offset(x = rimOffset, y = rimOffset),
                tint = Color(0xFF2C4058).copy(alpha = 0.17f)
            )
        }
        Icon(
            imageVector = sample.icon,
            contentDescription = sample.title,
            modifier = Modifier
                .size(iconSize)
                .graphicsLayer {
                    compositingStrategy = CompositingStrategy.Offscreen
                }
                .drawWithCache {
                    val brush = shaderBrush ?: fallbackBrush
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            brush = brush,
                            blendMode = BlendMode.SrcAtop
                        )
                    }
                },
            tint = Color.White
        )
    }
}

@Composable
private fun rememberAgslGlossBrush(tone: ConsoleIconStyleTone): ShaderBrush? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        return null
    }
    return remember(tone) {
        RuntimeShaderIconGlossBrush(consoleIconShaderSource(tone))
    }
}

private fun fallbackIconGlossBrush(
    tone: ConsoleIconStyleTone,
    variant: ConsoleIconStyleVariant = ConsoleIconStyleVariant.BrushGradientVertical
): Brush {
    val colors = when (tone) {
        ConsoleIconStyleTone.BlueGray -> listOf(Color(0xFF9AAFC4), Color(0xFF5F748E), Color(0xFF40556D))
        ConsoleIconStyleTone.MattePearl -> listOf(Color(0xFFE7F0F8), Color(0xFFC7D3E0), Color(0xFF8FA2B8))
        ConsoleIconStyleTone.MatteSlate -> listOf(Color(0xFF8EA3BA), Color(0xFF617790), Color(0xFF3F536B))
    }
    return when (variant) {
        ConsoleIconStyleVariant.BrushGradientDiagonal -> Brush.linearGradient(
            colors = listOf(colors[0], colors[1], colors[2]),
            start = Offset.Zero,
            end = Offset(96f, 96f)
        )
        ConsoleIconStyleVariant.BrushGradientSoft -> Brush.radialGradient(
            colorStops = arrayOf(
                0.00f to colors[0],
                0.48f to colors[1],
                1.00f to colors[2]
            )
        )
        else -> Brush.verticalGradient(colors)
    }
}

private fun consoleIconShaderSource(tone: ConsoleIconStyleTone): String {
    return when (tone) {
        ConsoleIconStyleTone.MatteSlate -> CONSOLE_ICON_MATTE_SLATE_SHADER
        ConsoleIconStyleTone.MattePearl -> CONSOLE_ICON_MATTE_PEARL_SHADER
        ConsoleIconStyleTone.BlueGray -> CONSOLE_ICON_MATTE_BLUE_GRAY_SHADER
    }
}

private class RuntimeShaderIconGlossBrush(shaderSource: String) : ShaderBrush() {
    private val shader = RuntimeShader(shaderSource)

    override fun createShader(size: Size): Shader {
        shader.setFloatUniform("resolution", size.width, size.height)
        return shader
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
                    borderless = true,
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
                    borderless = true,
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
    }
}

@Composable
private fun ClassicButtonSettingsCard(
    buttonVibrationLevel: ButtonVibrationLevel,
    classicSolidButtonBackground: Boolean,
    classicFontSize: DeckFontSizeOption,
    onLayoutEditor: () -> Unit,
    onButtonVibrationLevelChange: (ButtonVibrationLevel) -> Unit,
    onClassicSolidButtonBackgroundChange: (Boolean) -> Unit,
    onClassicFontSizeChange: (DeckFontSizeOption) -> Unit
) {
    ClassicConceptSectionCard(
        icon = Icons.Filled.TouchApp,
        title = stringResource(R.string.button_settings),
        subtitle = stringResource(R.string.button_settings_desc),
        accent = ClassicButtonAccent,
        secondaryAccent = ClassicButtonSecondaryAccent,
        trailing = {
            SettingsCycleButton(
                text = stringResource(R.string.button_editor),
                accent = ClassicButtonAccent,
                onClick = onLayoutEditor
            )
        }
    ) {
        ClassicSettingsControlRow(
            icon = Icons.Filled.Vibration,
            iconColor = ClassicButtonAccent,
            title = stringResource(R.string.settings_vibration),
            subtitle = stringResource(R.string.settings_vibration_desc),
            trailing = {
                Button(
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ClassicButtonAccent,
                        contentColor = Color.White
                    ),
                    onClick = { onButtonVibrationLevelChange(buttonVibrationLevel.next()) }
                ) {
                    Text(stringResource(buttonVibrationLevel.shortLabelRes))
                }
            }
        )
        ClassicSettingsControlRow(
            icon = Icons.Filled.GridView,
            iconColor = ClassicButtonAccent,
            title = stringResource(R.string.settings_solid_button_background),
            subtitle = stringResource(R.string.settings_solid_button_background_desc),
            trailing = {
                SettingsSwitch(
                    checked = classicSolidButtonBackground,
                    accent = ClassicButtonAccent,
                    onCheckedChange = onClassicSolidButtonBackgroundChange
                )
            }
        )
        ClassicSettingsControlRow(
            icon = Icons.Filled.TextFields,
            iconColor = ClassicButtonAccent,
            title = stringResource(R.string.settings_font_size),
            subtitle = stringResource(R.string.settings_font_size_desc),
            trailing = {
                SettingsSegmentedControl(
                    options = DeckFontSizeOption.values().toList(),
                    selected = classicFontSize,
                    label = { stringResource(it.shortLabelRes) },
                    accent = ClassicButtonAccent,
                    borderless = true,
                    onSelected = onClassicFontSizeChange
                )
            }
        )
    }
}

@Composable
private fun ClassicBackgroundSettingsCard(
    background: ClassicDeckBackground,
    onBackgroundChange: (ClassicDeckBackground) -> Unit,
    onPickImage: () -> Unit
) {
    val darkTheme = isSystemInDarkTheme()
    val swatches = remember(darkTheme) { classicBackgroundPalette(darkTheme) }
    val selectedSwatchBorder = if (darkTheme) Color.White else Color(0xFF1B2A35)
    ClassicConceptSectionCard(
        icon = Icons.Filled.Image,
        title = stringResource(R.string.classic_background_title),
        subtitle = stringResource(R.string.classic_background_desc),
        accent = ClassicBackgroundAccent,
        secondaryAccent = ClassicBackgroundSecondaryAccent,
        trailing = {
            SettingsSegmentedControl(
                options = ClassicDeckBackgroundType.values().toList(),
                selected = background.type,
                label = { stringResource(it.labelRes) },
                accent = ClassicBackgroundAccent,
                onSelected = { type ->
                    val nextBackground = when (type) {
                        ClassicDeckBackgroundType.Default -> background.copy(type = type)
                        ClassicDeckBackgroundType.Color -> background.copy(type = type)
                        ClassicDeckBackgroundType.Image -> background.copy(type = type)
                    }
                    onBackgroundChange(nextBackground)
                }
            )
        }
    ) {
        ClassicSettingsControlRow(
            icon = Icons.Filled.Image,
            iconColor = ClassicBackgroundAccent,
            title = stringResource(R.string.classic_background_preview),
            subtitle = stringResource(
                when (background.type) {
                    ClassicDeckBackgroundType.Default -> R.string.classic_background_default_desc
                    ClassicDeckBackgroundType.Color -> R.string.classic_background_color_desc
                    ClassicDeckBackgroundType.Image -> R.string.classic_background_image_desc
                }
            ),
            trailing = {
                ClassicBackgroundPreview(background = background)
            }
        )
        ClassicSettingsControlRow(
            icon = Icons.Filled.GridView,
            iconColor = ClassicBackgroundAccent,
            title = stringResource(R.string.classic_background_color),
            subtitle = stringResource(R.string.classic_background_color_desc),
            trailing = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    swatches.forEach { swatch ->
                        Surface(
                            modifier = Modifier
                                .size(34.dp)
                                .border(
                                    width = if (background.type == ClassicDeckBackgroundType.Color && background.color == swatch) 2.dp else 1.dp,
                                    color = if (background.type == ClassicDeckBackgroundType.Color && background.color == swatch) selectedSwatchBorder else ClassicBackgroundAccent.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            shape = RoundedCornerShape(8.dp),
                            color = swatch,
                            onClick = {
                                onBackgroundChange(
                                    background.copy(
                                        type = ClassicDeckBackgroundType.Color,
                                        color = swatch
                                    )
                                )
                            }
                        ) {}
                    }
                }
            }
        )
        ClassicSettingsControlRow(
            icon = Icons.Filled.Image,
            iconColor = ClassicBackgroundAccent,
            title = stringResource(R.string.classic_background_image),
            subtitle = stringResource(R.string.classic_background_image_picker_desc),
            trailing = {
                Button(
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ClassicBackgroundAccent,
                        contentColor = Color.White
                    ),
                    onClick = onPickImage
                ) {
                    Text(stringResource(R.string.classic_background_pick_image))
                }
            }
        )
    }
}

@Composable
private fun ClassicBackgroundPreview(background: ClassicDeckBackground) {
    Box(
        modifier = Modifier
            .size(width = 96.dp, height = 44.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, ClassicBackgroundAccent.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
    ) {
        ClassicDeckBackgroundLayer(
            modifier = Modifier.fillMaxSize(),
            background = background
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.45f))
                )
            }
        }
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
    val buttonColors = classicButtonPalette(isSystemInDarkTheme())

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
        Box(
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
        val darkTheme = isSystemInDarkTheme()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(previewHeight)
                .clip(RoundedCornerShape(18.dp))
                .background(colors.consolePreviewBackground)
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .width(118.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .background(colors.consoleSidebar)
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
                val panelRows = listOf(
                    R.string.console_panel_connection to panelOptions.showConnection,
                    R.string.console_panel_message to panelOptions.showMessage,
                    R.string.console_panel_clock to panelOptions.showClock,
                    R.string.console_panel_date to panelOptions.showDate
                )
                panelRows.forEach { (labelRes, enabled) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(20.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(colors.cardBackground.copy(alpha = if (enabled) 0.72f else 0.30f))
                            .padding(horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(if (enabled) colors.consoleButtonFeatured else colors.textMuted)
                        )
                        Text(
                            text = stringResource(labelRes),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (enabled) colors.textPrimary else colors.textMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                if (panelOptions.showClock) {
                    Text(
                        text = "23:41",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        maxLines = 1
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ConsoleSettingsPreviewButton(
                        modifier = Modifier.weight(1.2f),
                        icon = Icons.Filled.PlayArrow,
                        title = stringResource(R.string.media_play_pause),
                        subtitle = "Media",
                        selected = true,
                        darkTheme = darkTheme
                    )
                    ConsoleSettingsPreviewButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.SkipPrevious,
                        title = stringResource(R.string.media_previous),
                        subtitle = "Key",
                        darkTheme = darkTheme
                    )
                    ConsoleSettingsPreviewButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.VolumeUp,
                        title = stringResource(R.string.media_volume_up),
                        subtitle = "Trim",
                        darkTheme = darkTheme
                    )
                }
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ConsoleSettingsPreviewButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.Keyboard,
                        title = "Explorer",
                        subtitle = "Win+E",
                        darkTheme = darkTheme
                    )
                    ConsoleSettingsPreviewButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.Settings,
                        title = stringResource(R.string.action_settings),
                        subtitle = "Deck",
                        darkTheme = darkTheme
                    )
                    ConsoleSettingsPreviewButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.Apps,
                        title = stringResource(R.string.control_style_joypad),
                        subtitle = stringResource(R.string.joypad_mode_8_way),
                        darkTheme = darkTheme
                    )
                }
            }
        }
    }
}

@Composable
private fun ConsoleSettingsPreviewButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    selected: Boolean = false,
    darkTheme: Boolean
) {
    val colors = LocalDeckThemeColors.current
    val shape = RoundedCornerShape(14.dp)
    Row(
        modifier = modifier
            .fillMaxHeight()
            .consoleButtonDropShadow(shape = shape, darkTheme = darkTheme, pressed = selected)
            .clip(shape)
            .background(if (selected) colors.consoleButtonFeatured else colors.consoleButtonDefault)
            .border(
                1.dp,
                if (selected) colors.consoleButtonFeatured.copy(alpha = 0.8f) else Color.White.copy(alpha = if (darkTheme) 0.10f else 0.38f),
                shape
            )
            .padding(horizontal = 9.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = colors.textPrimary
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = colors.textPrimary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ConsoleSettingsIconTile(icon: ImageVector) {
    val colors = LocalDeckThemeColors.current
    Box(
        modifier = Modifier
            .size(44.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colors.textPrimary,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun ConsoleSettingsCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val shape = RoundedCornerShape(20.dp)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .consolePanelDropShadow(shape = shape, darkTheme = isSystemInDarkTheme()),
        shape = shape,
        color = colors.consoleSidebar,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = content
        )
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
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    ConsoleSettingsCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ConsoleSettingsIconTile(icon)
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
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ConsoleSettingRow(
        icon = icon,
        title = title,
        subtitle = subtitle,
        trailing = { Switch(checked = checked, onCheckedChange = onCheckedChange) }
    )
}

@Composable
private fun ConsolePageSwipeModeSettingRow(
    icon: ImageVector,
    pageSwipeMode: PageSwipeMode,
    onPageSwipeModeChange: (PageSwipeMode) -> Unit
) {
    ConsoleSettingRow(
        icon = icon,
        title = stringResource(R.string.settings_page_swipe_mode),
        subtitle = stringResource(R.string.settings_page_swipe_mode_desc),
        trailing = {
            SettingsSegmentedControl(
                options = PageSwipeMode.values().toList(),
                selected = pageSwipeMode,
                label = { stringResource(it.labelRes) },
                borderless = true,
                onSelected = onPageSwipeModeChange
            )
        }
    )
}

@Composable
private fun ConsoleVibrationSettingRow(
    buttonVibrationLevel: ButtonVibrationLevel,
    onButtonVibrationLevelChange: (ButtonVibrationLevel) -> Unit
) {
    ConsoleSettingRow(
        icon = Icons.Filled.Vibration,
        title = stringResource(R.string.settings_vibration),
        subtitle = stringResource(R.string.settings_vibration_desc),
        trailing = {
            ConsolePillButton(
                text = stringResource(buttonVibrationLevel.shortLabelRes),
                onClick = { onButtonVibrationLevelChange(buttonVibrationLevel.next()) }
            )
        }
    )
}

@Composable
private fun ConsoleFontSizeSettingRow(
    fontSize: DeckFontSizeOption,
    onFontSizeChange: (DeckFontSizeOption) -> Unit
) {
    ConsoleSettingRow(
        icon = Icons.Filled.TextFields,
        title = stringResource(R.string.settings_font_size),
        subtitle = stringResource(R.string.settings_font_size_desc),
        trailing = {
            SettingsSegmentedControl(
                options = DeckFontSizeOption.values().toList(),
                selected = fontSize,
                label = { stringResource(it.shortLabelRes) },
                borderless = true,
                onSelected = onFontSizeChange
            )
        }
    )
}

@Composable
private fun ConsolePageSummaryRow(
    pageName: String,
    pageCount: Int,
    activeIndex: Int,
    onAddPage: () -> Unit
) {
    ConsoleSettingRow(
        icon = Icons.Filled.Apps,
        title = stringResource(R.string.add_page_count, pageCount, MAX_PAGES),
        subtitle = pageLayoutSummary(pageName, pageCount, null, null),
        trailing = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                PageIndicator(pageCount = pageCount, activeIndex = activeIndex)
                ConsolePillButton(text = stringResource(R.string.add_page), onClick = onAddPage)
            }
        }
    )
}

@Composable
private fun ConsoleSettingsAppInfoRow(
    onExportBundle: () -> Unit,
    onImportBundle: () -> Unit,
    onOpenIconStyleTest: () -> Unit,
    onShowClassicTutorial: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    ConsoleSettingsCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ConsoleSettingsIconTile(Icons.Filled.Info)
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
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ConsolePillButton(text = stringResource(R.string.export_bundle), onClick = onExportBundle)
                ConsolePillButton(text = stringResource(R.string.import_bundle), onClick = onImportBundle)
                if (BuildConfig.DEBUG) {
                    ConsolePillButton(text = "아이콘 테스트", onClick = onOpenIconStyleTest)
                }
                ConsolePillButton(text = stringResource(R.string.tutorial), onClick = onShowClassicTutorial)
            }
        }
    }
}

@Composable
private fun ConsolePillButton(
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val shape = RoundedCornerShape(12.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    Surface(
        modifier = Modifier.consoleButtonDropShadow(
            shape = shape,
            darkTheme = isSystemInDarkTheme(),
            pressed = pressed,
            drawHairline = false
        ),
        shape = shape,
        color = if (enabled) colors.consoleButtonDefault else colors.toggleBackground,
        contentColor = if (enabled) colors.textPrimary else colors.textMuted,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        interactionSource = interactionSource,
        onClick = { if (enabled) onClick() }
    ) {
        Text(
            modifier = Modifier
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
    ConsoleSettingsCard {
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
    ConsoleSettingsCard {
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
    borderless: Boolean = false,
    onSelected: (T) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val activeColor = accent ?: MaterialTheme.colorScheme.primary
    val shape = RoundedCornerShape(7.dp)
    val containerColor = if (borderless) {
        colors.toggleBackground.copy(alpha = 0.54f)
    } else {
        colors.toggleBackground
    }
    val activeContainerColor = if (borderless) {
        activeColor
    } else {
        activeColor.copy(alpha = if (isSystemInDarkTheme()) 0.86f else 0.78f)
    }
    Row(
        modifier = Modifier
            .height(38.dp)
            .clip(shape)
            .background(containerColor)
            .then(
                if (borderless) {
                    Modifier
                } else {
                    Modifier.border(1.dp, colors.cardBorder, shape)
                }
            )
    ) {
        options.forEach { option ->
            val active = option == selected
            Surface(
                color = if (active) activeContainerColor else Color.Transparent,
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
    val colors = LocalDeckThemeColors.current
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = Modifier
            .consoleButtonDropShadow(
                shape = shape,
                darkTheme = isSystemInDarkTheme(),
                pressed = false
            )
            .clip(shape)
            .background(colors.consoleButtonDefault)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary
        )
    }
}

@Composable
private fun SettingsCard(
    modifier: Modifier = Modifier,
    accent: Color = Color(0xFF0EA5FF),
    themeColors: DeckThemeColors? = null,
    borderAlpha: Float = 0.18f,
    borderWidth: Dp = 1.dp,
    shadowElevation: Dp = 0.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = themeColors ?: LocalDeckThemeColors.current
    val borderColor by animateColorAsState(
        targetValue = accent.copy(alpha = borderAlpha).compositeOver(colors.cardBorder.copy(alpha = 0.42f)),
        label = "settingsCardBorder"
    )
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = colors.cardBackground,
        tonalElevation = 0.dp,
        shadowElevation = shadowElevation
    ) {
        Column(
            modifier = Modifier
                .border(borderWidth, borderColor, RoundedCornerShape(8.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
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
                                Icon(
                                    imageVector = pairedHostIcon(host.type),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
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
    classicSolidButtonBackground: Boolean,
    classicDeckBackground: ClassicDeckBackground,
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
    onButtonDragStarted: () -> Unit,
    onTrimStep: (DeckButton, Int) -> Unit,
    onButtonMoved: (DeckButton, Int) -> Unit,
    onButtonDeleted: (DeckButton) -> Unit,
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

    val hasCustomBackground = classicDeckBackground.type != ClassicDeckBackgroundType.Default

    Box(
        modifier = modifier.clipToBounds()
    ) {
        if (hasCustomBackground) {
            ClassicDeckBackgroundLayer(
                modifier = Modifier.matchParentSize(),
                background = classicDeckBackground
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    shape = RoundedCornerShape(8.dp),
                    colors = buttonEditorSolidButtonColors(),
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
                Button(
                    shape = RoundedCornerShape(8.dp),
                    enabled = isFirstPage || deckPages.size > 1,
                    colors = buttonEditorSolidButtonColors(),
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
                    classicSolidButtonBackground = classicSolidButtonBackground,
                    classicDeckBackground = if (hasCustomBackground) ClassicDeckBackground() else classicDeckBackground,
                    previewMode = true,
                    pageSwipeAxis = pageSwipeAxis,
                    pageSwipeMode = pageSwipeMode,
                    pageSwipeAnimation = pageSwipeAnimation,
                    pageSwipeDelta = pageSwipeDelta,
                    pageAnimationSequence = pageAnimationSequence,
                    onPageSwipe = onPageSwipe,
                    onAddPage = onAddPage,
                    onButtonPressed = onButtonEdit,
                    onTrimStep = onTrimStep,
                    onButtonTouchStarted = onButtonDragStarted,
                    onButtonEdit = onButtonEdit,
                    onButtonMoved = onButtonMoved,
                    onButtonDeleted = onButtonDeleted,
                    onEmptySlotPressed = onEmptySlotPressed
                )
            }
        }
    }
}

@Composable
private fun buttonEditorSolidButtonColors() = ButtonDefaults.buttonColors(
    containerColor = ClassicButtonAccent,
    contentColor = Color.White,
    disabledContainerColor = ClassicButtonAccent.copy(alpha = 0.34f),
    disabledContentColor = Color.White.copy(alpha = 0.54f)
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
    onConsoleSettings: () -> Unit = {},
    classicSolidButtonBackground: Boolean,
    classicDeckBackground: ClassicDeckBackground,
    previewMode: Boolean,
    pageSwipeAxis: PageSwipeAxis,
    pageSwipeMode: PageSwipeMode,
    pageSwipeAnimation: Boolean,
    pageSwipeDelta: Int,
    pageAnimationSequence: Int,
    onPageSwipe: (Int) -> Unit,
    onAddPage: () -> Unit,
    onButtonPressed: (DeckButton) -> Unit,
    onTrimStep: (DeckButton, Int) -> Unit,
    onButtonTouchStarted: () -> Unit = {},
    onButtonTouchEnded: () -> Unit = {},
    onButtonEdit: (DeckButton) -> Unit,
    onButtonMoved: (DeckButton, Int) -> Unit,
    onButtonDeleted: (DeckButton) -> Unit = {},
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
                onSettings = onConsoleSettings,
                onPageSwipe = onPageSwipe,
                onButtonPressed = onButtonPressed,
                onTrimStep = onTrimStep,
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
            ClassicDeckBackgroundLayer(
                modifier = Modifier.fillMaxSize(),
                background = classicDeckBackground
            )
            AnimatedContent(
                targetState = PageAnimationTarget(activePageId, pageSwipeDelta, pageAnimationSequence),
                modifier = Modifier.fillMaxSize(),
                transitionSpec = {
                    val slideGap = with(density) { maxOf(spacing, PAGE_TRANSITION_GAP).toPx().roundToInt() }
                    if (!pageSwipeAnimation) {
                        fadeIn(tween(0)) togetherWith fadeOut(tween(0))
                    } else if (pageSwipeAxis == PageSwipeAxis.Horizontal) {
                        slideInHorizontally { width -> targetState.delta.signOrOne() * (width + slideGap) } togetherWith
                            slideOutHorizontally { width -> -targetState.delta.signOrOne() * (width + slideGap) }
                    } else {
                        slideInVertically { height -> targetState.delta.signOrOne() * (height + slideGap) } togetherWith
                            slideOutVertically { height -> -targetState.delta.signOrOne() * (height + slideGap) }
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
                    classicSolidButtonBackground = classicSolidButtonBackground,
                    previewMode = previewMode,
                    showTitle = target.pageId == deckPages.firstOrNull()?.id,
                    onButtonPressed = onButtonPressed,
                    onTrimStep = onTrimStep,
                    onButtonTouchStarted = onButtonTouchStarted,
                    onButtonTouchEnded = onButtonTouchEnded,
                    onButtonEdit = onButtonEdit,
                    onButtonMoved = onButtonMoved,
                    onButtonDeleted = onButtonDeleted,
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
private fun ClassicDeckBackgroundLayer(
    modifier: Modifier = Modifier,
    background: ClassicDeckBackground
) {
    when (background.type) {
        ClassicDeckBackgroundType.Default -> Unit
        ClassicDeckBackgroundType.Color -> Box(
            modifier = modifier.background(background.color)
        )
        ClassicDeckBackgroundType.Image -> {
            if (background.imageUri.isBlank()) {
                Box(modifier = modifier.background(background.color))
            } else {
                val context = LocalContext.current
                val isGif = remember(background.imageUri) { context.isGifImage(background.imageUri) }

                if (isGif) {
                    ClassicGifBackground(
                        modifier = modifier,
                        uriString = background.imageUri,
                        fallbackColor = background.color
                    )
                } else {
                    val bitmap = remember(background.imageUri) {
                        context.loadClassicBackgroundBitmap(background.imageUri)
                    }
                    if (bitmap != null) {
                        Image(
                            modifier = modifier,
                            bitmap = bitmap,
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(modifier = modifier.background(background.color))
                    }
                }
            }
        }
    }
}

@Composable
private fun ClassicGifBackground(
    modifier: Modifier = Modifier,
    uriString: String,
    fallbackColor: Color
) {
    val context = LocalContext.current
    val movie = remember(uriString) { context.loadClassicBackgroundMovie(uriString) }
    var frameTime by remember(uriString) { mutableStateOf(0L) }

    LaunchedEffect(movie) {
        if (movie == null) return@LaunchedEffect
        while (true) {
            frameTime = withFrameMillis { it }
        }
    }

    if (movie == null || movie.width() <= 0 || movie.height() <= 0) {
        Box(modifier = modifier.background(fallbackColor))
        return
    }

    Canvas(modifier = modifier) {
        val duration = movie.duration().takeIf { it > 0 } ?: 1000
        val scale = maxOf(size.width / movie.width(), size.height / movie.height())
        val dx = (size.width - movie.width() * scale) / 2f
        val dy = (size.height - movie.height() * scale) / 2f
        movie.setTime((frameTime % duration).toInt())
        drawIntoCanvas { canvas ->
            val nativeCanvas = canvas.nativeCanvas
            val saveCount = nativeCanvas.save()
            nativeCanvas.translate(dx, dy)
            nativeCanvas.scale(scale, scale)
            movie.draw(nativeCanvas, 0f, 0f)
            nativeCanvas.restoreToCount(saveCount)
        }
    }
}

private fun Context.isGifImage(uriString: String): Boolean {
    val uri = Uri.parse(uriString)
    return contentResolver.getType(uri)?.equals("image/gif", ignoreCase = true) == true ||
        uriString.substringAfterLast('.', "").equals("gif", ignoreCase = true)
}

private fun Context.loadClassicBackgroundBitmap(uriString: String): ImageBitmap? = runCatching {
    contentResolver.openInputStream(Uri.parse(uriString))?.use { input ->
        BitmapFactory.decodeStream(input)?.asImageBitmap()
    }
}.getOrNull()

private fun Context.loadClassicBackgroundMovie(uriString: String): Movie? = runCatching {
    contentResolver.openInputStream(Uri.parse(uriString))?.use { input ->
        Movie.decodeStream(input)
    }
}.getOrNull()

private val PAGE_TRANSITION_GAP = 24.dp
private const val MAX_CONSOLE_LAYOUT_ROWS = 4
private fun widgetSpanForProvider(
    info: AppWidgetProviderInfo?,
    columns: Int,
    rows: Int
): Pair<Int, Int> {
    val width = maxOf(info?.minResizeWidth ?: 0, info?.minWidth ?: 0)
    val height = maxOf(info?.minResizeHeight ?: 0, info?.minHeight ?: 0)
    val fallback = 2.coerceAtMost(columns).coerceAtLeast(1) to 2.coerceAtMost(rows).coerceAtLeast(1)
    if (width <= 0 || height <= 0) return fallback

    val ratio = width.toFloat() / height.toFloat()
    val span = when {
        ratio >= 2.25f -> 3 to 1
        ratio >= 1.25f -> 3 to 2
        ratio >= 0.85f -> 2 to 2
        else -> 1 to 2
    }
    return span.first.coerceIn(1, minOf(MAX_BUTTON_SPAN_COLUMNS, columns.coerceAtLeast(1))) to
        span.second.coerceIn(1, minOf(MAX_BUTTON_SPAN_ROWS, rows.coerceAtLeast(1)))
}

private fun placeFixedSpanButton(
    button: DeckButton,
    otherButtons: List<DeckButton>,
    columns: Int,
    rows: Int,
    showTitle: Boolean
): DeckButton {
    val safeColumns = columns.coerceAtLeast(1)
    val safeRows = rows.coerceAtLeast(1)
    val spanColumns = button.spanColumns.coerceIn(1, minOf(MAX_BUTTON_SPAN_COLUMNS, safeColumns))
    val spanRows = button.spanRows.coerceIn(1, minOf(MAX_BUTTON_SPAN_ROWS, safeRows))
    val candidate = button.copy(spanColumns = spanColumns, spanRows = spanRows)
    val occupied = otherButtons.flatMap {
        occupiedSlotsForButton(it, safeColumns, safeRows, showTitle)
    }.toSet()
    val capacity = safeColumns * safeRows - if (showTitle) 1 else 0

    fun exactSlots(position: Int): List<Int>? {
        if (position !in 0 until capacity) return null
        val anchorSlot = if (showTitle) position + 1 else position
        if (anchorSlot !in 0 until safeColumns * safeRows) return null
        val column = anchorSlot % safeColumns
        val row = anchorSlot / safeColumns
        if (column + spanColumns > safeColumns || row + spanRows > safeRows) return null
        return buildList {
            repeat(spanRows) { rowOffset ->
                repeat(spanColumns) { columnOffset ->
                    add((row + rowOffset) * safeColumns + column + columnOffset)
                }
            }
        }.takeIf { slots -> !showTitle || 0 !in slots }
    }

    fun fits(position: Int): Boolean {
        val slots = exactSlots(position) ?: return false
        return slots.none { it in occupied }
    }

    if (fits(candidate.position)) return candidate
    val openPosition = (0 until capacity).firstOrNull(::fits)
    if (openPosition != null) return candidate.copy(position = openPosition)

    return shrinkButtonToAvailable(candidate, otherButtons, safeColumns, safeRows, showTitle)
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
    onSettings: () -> Unit,
    onPageSwipe: (Int) -> Unit,
    onButtonPressed: (DeckButton) -> Unit,
    onTrimStep: (DeckButton, Int) -> Unit,
    onButtonTouchStarted: () -> Unit,
    onButtonTouchEnded: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val density = LocalDensity.current
    val consoleSpacing = maxOf(spacing, 10.dp)
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val sidebarWidth = consoleSidebarWidth(maxWidth, layout.sidebarFraction)
            val consoleGap = 14.dp
            val deckStart = sidebarWidth * 0.5f
            val deckVisibleStartPadding = maxOf(sidebarWidth - deckStart + consoleGap, 0.dp)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = deckStart)
            ) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                ) {
                    BoxWithConstraints(modifier = Modifier.matchParentSize()) {
                        val slideGapPx = with(density) {
                            maxOf(consoleSpacing, PAGE_TRANSITION_GAP).toPx().roundToInt()
                        }
                        AnimatedContent(
                            targetState = PageAnimationTarget(activePageId, pageSwipeDelta, pageAnimationSequence),
                            modifier = Modifier.fillMaxSize(),
                            transitionSpec = {
                                if (!pageSwipeAnimation) {
                                    fadeIn(tween(0)) togetherWith fadeOut(tween(0))
                                } else {
                                    slideInHorizontally { width -> targetState.delta.signOrOne() * (width + slideGapPx) } togetherWith
                                        slideOutHorizontally { width -> -targetState.delta.signOrOne() * (width + slideGapPx) }
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
                            val consoleWeights = normalizedConsoleRowWeights(layout, consoleRows.size)
                            ConsoleButtonRows(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = deckVisibleStartPadding)
                                    .then(swipeModifier)
                                    .padding(bottom = 12.dp),
                                rows = consoleRows,
                                rowWeights = consoleWeights,
                                spacing = consoleSpacing,
                                status = status,
                                appWidgetHost = appWidgetHost,
                                appWidgetManager = appWidgetManager,
                                onButtonPressed = onButtonPressed,
                                onTrimStep = onTrimStep,
                                onButtonTouchStarted = onButtonTouchStarted,
                                onButtonTouchEnded = onButtonTouchEnded
                            )
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .width(sidebarWidth)
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
            ) {
                ConsoleSidebar(
                    modifier = Modifier.matchParentSize(),
                    status = status,
                    panelOptions = panelOptions,
                    onSettings = onSettings
                )
            }
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


@Composable
private fun ConsoleButtonRows(
    modifier: Modifier = Modifier,
    rows: List<List<DeckButton>>,
    rowWeights: List<Float>,
    spacing: Dp,
    status: HidStatus,
    appWidgetHost: AppWidgetHost?,
    appWidgetManager: AppWidgetManager?,
    onButtonPressed: (DeckButton) -> Unit,
    onTrimStep: (DeckButton, Int) -> Unit,
    onButtonTouchStarted: () -> Unit,
    onButtonTouchEnded: () -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val safeRows = rows.ifEmpty { listOf(emptyList()) }
        val weights = rowWeights.takeIf { it.size == safeRows.size } ?: List(safeRows.size) { 1f }
        val availableHeight = maxHeight - spacing * (safeRows.size - 1).coerceAtLeast(0).toFloat()
        val totalWeight = weights.sum().takeIf { it > 0f } ?: safeRows.size.toFloat()
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            safeRows.forEachIndexed { rowIndex, rowButtons ->
                val rowHeight = availableHeight * (weights[rowIndex] / totalWeight)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(rowHeight),
                    horizontalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    rowButtons.forEach { button ->
                        val keyModifier = if (button.isTrimControl()) {
                            Modifier
                                .width(rowHeight)
                                .fillMaxHeight()
                        } else {
                            Modifier
                                .weight(button.spanColumns.coerceAtLeast(1).toFloat())
                                .fillMaxHeight()
                        }
                        DeckKey(
                            modifier = keyModifier,
                            button = button,
                            status = status,
                            appWidgetHost = appWidgetHost,
                            appWidgetManager = appWidgetManager,
                            visualMode = DeckUiMode.Console,
                            classicSolidButtonBackground = true,
                            enabled = true,
                            previewMode = false,
                            columns = rowButtons.size.coerceAtLeast(1),
                            slot = button.position,
                            cellSize = rowHeight,
                            spacing = spacing,
                            onPressed = { onButtonPressed(button) },
                            onTrimStep = { step -> onTrimStep(button, step) },
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
}

private fun consoleLayoutRows(
    layout: ConsoleLayoutConfig,
    buttons: List<DeckButton>,
    columns: Int,
    rows: Int
): List<List<DeckButton>> {
    val buttonById = buttons.associateBy { it.id }
    val layoutRows = consoleLayoutRowIds(layout, buttons, columns, rows)
    return layoutRows.map { row ->
        row.mapNotNull { buttonById[it] }
    }
}

private fun consoleLayoutRowIds(
    layout: ConsoleLayoutConfig,
    buttons: List<DeckButton>,
    columns: Int,
    rows: Int
): List<List<Int>> {
    val validIds = buttons.map { it.id }.toSet()
    val resolvedRows = layout.rows
        .map { row -> row.filter { it in validIds } }
        .filter { it.isNotEmpty() }
    if (resolvedRows.isNotEmpty()) return resolvedRows
    return defaultConsoleRows(buttons, columns, rows)
        .map { row -> row.map { it.id } }
        .filter { it.isNotEmpty() }
}

private fun defaultConsoleLayout(buttons: List<DeckButton>): ConsoleLayoutConfig {
    val buttonIds = buttons.filterNot { buttonAppAction(it) == DeckActionType.Settings }.associateBy { it.id }
    val defaultRows = defaultConsoleRows(buttonIds.values.toList(), DEFAULT_COLUMNS, DEFAULT_ROWS)
    return ConsoleLayoutConfig(
        rows = defaultRows.map { row -> row.map { it.id } },
        rowWeights = List(defaultRows.size) { 1f },
        sidebarFraction = CONSOLE_DEFAULT_SIDEBAR_FRACTION
    )
}

private fun consoleSidebarWidth(totalWidth: Dp, sidebarFraction: Float): Dp {
    val fraction = sidebarFraction.coerceIn(CONSOLE_MIN_SIDEBAR_FRACTION, CONSOLE_MAX_SIDEBAR_FRACTION)
    return (totalWidth * fraction).coerceIn(124.dp, 280.dp)
}

private fun consoleHairlineColor(darkTheme: Boolean): Color {
    return Color(0xFFFFFBF2).copy(alpha = if (darkTheme) 0.22f else 0.92f)
}

private fun consoleHairlineOffset(pressed: Boolean): IntOffset {
    return if (pressed) IntOffset(1, 1) else IntOffset(-1, -1)
}

private fun Modifier.consoleHairlineHighlight(
    shape: Shape,
    darkTheme: Boolean,
    pressed: Boolean = false
): Modifier {
    return drawWithCache {
        val outline = shape.createOutline(size, layoutDirection, this)
        val offset = consoleHairlineOffset(pressed)
        val color = consoleHairlineColor(darkTheme)
        onDrawBehind {
            translate(left = offset.x.toFloat(), top = offset.y.toFloat()) {
                when (outline) {
                    is Outline.Rectangle -> drawRect(
                        color = color,
                        topLeft = outline.rect.topLeft,
                        size = outline.rect.size
                    )
                    is Outline.Rounded -> {
                        val roundRect = outline.roundRect
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(roundRect.left, roundRect.top),
                            size = Size(
                                width = roundRect.right - roundRect.left,
                                height = roundRect.bottom - roundRect.top
                            ),
                            cornerRadius = roundRect.topLeftCornerRadius
                        )
                    }
                    is Outline.Generic -> drawPath(
                        path = outline.path,
                        color = color
                    )
                }
            }
        }
    }
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

private fun Modifier.consolePanelDropShadow(
    shape: Shape,
    darkTheme: Boolean
): Modifier {
    return this
        .dropShadow(
            shape = shape,
            shadow = DropShadow(
                radius = if (darkTheme) 18.dp else 22.dp,
                spread = if (darkTheme) 4.dp else 5.dp,
                offset = DpOffset(0.dp, 3.dp),
                color = Color.Black.copy(alpha = if (darkTheme) 0.34f else 0.14f)
            )
        )
        .consoleHairlineHighlight(
            shape = shape,
            darkTheme = darkTheme,
            pressed = false
        )
}

private fun Modifier.consoleButtonDropShadow(
    shape: Shape,
    darkTheme: Boolean,
    pressed: Boolean,
    drawHairline: Boolean = true
): Modifier {
    val blackAlpha = when {
        pressed && darkTheme -> 0.22f
        pressed -> 0.10f
        darkTheme -> 0.34f
        else -> 0.14f
    }
    return this
        .dropShadow(
            shape = shape,
            shadow = DropShadow(
                radius = if (pressed) 2.dp else 3.dp,
                spread = if (pressed) 1.6.dp else 2.4.dp,
                offset = DpOffset(0.dp, 0.dp),
                color = Color.Black.copy(alpha = if (darkTheme) 0.20f else 0.08f)
            )
        )
        .dropShadow(
            shape = shape,
            shadow = DropShadow(
                radius = if (pressed) 9.dp else 18.dp,
                spread = if (pressed) 2.dp else 6.dp,
                offset = DpOffset(0.dp, if (pressed) 1.dp else 4.dp),
                color = Color.Black.copy(alpha = blackAlpha)
            )
        )
        .then(
            if (drawHairline) {
                Modifier.consoleHairlineHighlight(
                    shape = shape,
                    darkTheme = darkTheme,
                    pressed = pressed
                )
            } else {
                Modifier
            }
        )
}

private fun Modifier.consoleSettingsCategoryButtonShadow(
    shape: Shape,
    darkTheme: Boolean,
    pressed: Boolean
): Modifier {
    return if (pressed) {
        this
            .innerShadow(
                shape = shape,
                shadow = DropShadow(
                    radius = 8.dp,
                    spread = 1.2.dp,
                    offset = DpOffset(2.dp, 2.dp),
                    color = Color.Black.copy(alpha = if (darkTheme) 0.34f else 0.18f)
                )
            )
            .innerShadow(
                shape = shape,
                shadow = DropShadow(
                    radius = 5.dp,
                    spread = 0.6.dp,
                    offset = DpOffset((-1).dp, (-1).dp),
                    color = Color.White.copy(alpha = if (darkTheme) 0.08f else 0.18f)
                )
            )
    } else {
        this
            .dropShadow(
                shape = shape,
                shadow = DropShadow(
                    radius = 14.dp,
                    spread = 4.dp,
                    offset = DpOffset(0.dp, 3.dp),
                    color = Color.Black.copy(alpha = if (darkTheme) 0.28f else 0.12f)
                )
            )
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
    val shape = RoundedCornerShape(20.dp)
    Surface(
        modifier = modifier.consolePanelDropShadow(
            shape = shape,
            darkTheme = isSystemInDarkTheme()
        ),
        shape = shape,
        color = colors.consoleSidebar,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        ConsoleRaisedButtonFrame(
            modifier = Modifier.fillMaxSize(),
            shape = shape,
            cornerRadius = 20.dp,
            drawShadow = false,
            drawHighlight = false,
            shadowColor = Color.Black,
            highlightColor = null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
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
}

@Composable
private fun ConsoleLayoutEditorPage(
    modifier: Modifier = Modifier,
    buttons: List<DeckButton>,
    layout: ConsoleLayoutConfig,
    activePageIndex: Int,
    pageCount: Int,
    diagnostics: List<String>,
    initialEditMode: ConsoleLayoutEditMode,
    onBack: () -> Unit,
    onPageSwipe: (Int) -> Unit,
    onAddPage: () -> Unit,
    onAddRow: (Int) -> Unit,
    onRemoveRow: (Int) -> Unit,
    onMoveRow: (Int, Int) -> Unit,
    onReset: () -> Unit,
    onLayoutChange: (ConsoleLayoutConfig) -> Unit,
    onPickButton: (Int) -> Unit,
    onRemoveButton: (Int, Int) -> Unit,
    onMoveButton: (Int, Int, Int) -> Unit,
    onMoveButtonTo: (Int, Int, Int, Int) -> Unit,
    onEditButton: (DeckButton) -> Unit
) {
    val buttonById = buttons.associateBy { it.id }
    val colors = LocalDeckThemeColors.current
    val rows = consoleLayoutRowIds(
        layout = layout,
        buttons = buttons.filterNot { buttonAppAction(it) == DeckActionType.Settings },
        columns = DEFAULT_COLUMNS,
        rows = DEFAULT_ROWS
    ).ifEmpty { listOf(emptyList()) }
    var selectedRowIndex by remember { mutableStateOf(0) }
    var editMode by remember(initialEditMode) { mutableStateOf(initialEditMode) }
    var selectedButtonSlot by remember {
        mutableStateOf(rows.firstOrNull()?.indices?.firstOrNull()?.let { ConsoleButtonSlotKey(0, it) })
    }
    var dragInProgress by remember { mutableStateOf(false) }
    LaunchedEffect(rows) {
        selectedRowIndex = selectedRowIndex.coerceIn(0, rows.lastIndex.coerceAtLeast(0))
        selectedButtonSlot = selectedButtonSlot?.takeIf { slot ->
            slot.rowIndex in rows.indices && slot.buttonIndex in rows[slot.rowIndex].indices
        } ?: rows.firstOrNull { it.isNotEmpty() }?.let { row ->
            ConsoleButtonSlotKey(rows.indexOf(row), 0)
        }
    }
    fun addRowAndSelect() {
        if (rows.size < MAX_CONSOLE_LAYOUT_ROWS) {
            val targetRowIndex = rows.size.coerceAtMost(MAX_CONSOLE_LAYOUT_ROWS - 1)
            onAddRow(targetRowIndex)
            selectedRowIndex = targetRowIndex
        }
    }
    LazyColumn(
        modifier = modifier.background(Brush.linearGradient(colors.backgroundGradient)),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        userScrollEnabled = !dragInProgress
    ) {
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = colors.cardBackground,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.6.dp, colors.cardBorder.copy(alpha = 0.58f), RoundedCornerShape(14.dp))
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
                    ConsoleLayoutModeToggle(
                        modifier = Modifier.width(310.dp),
                        selectedMode = editMode,
                        onModeChange = { editMode = it }
                    )
                    ConsolePillButton(text = stringResource(R.string.reset), onClick = onReset)
                }
            }
        }

        item {
            val configuration = LocalConfiguration.current
            val rawRatio = configuration.screenWidthDp.toFloat() / configuration.screenHeightDp.coerceAtLeast(1).toFloat()
            val deviceRatio = if (rawRatio >= 1f) rawRatio else 1f / rawRatio
            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth()
            ) {
                val previewContentPadding = 12.dp
                val previewBottomControlsHeight = 42.dp
                val previewBottomControlsGap = 10.dp
                val previewHeight = ((maxWidth - previewContentPadding * 2f) / deviceRatio +
                    previewContentPadding * 2f +
                    previewBottomControlsGap +
                    previewBottomControlsHeight).coerceAtLeast(260.dp)
                ConsoleLayoutTuningPreview(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(previewHeight),
                    layout = layout,
                    buttons = buttons,
                    activePageIndex = activePageIndex,
                    pageCount = pageCount,
                    selectedRowIndex = selectedRowIndex,
                    selectedButtonSlot = selectedButtonSlot,
                    editMode = editMode,
                    onSelectRow = { selectedRowIndex = it.coerceIn(rows.indices) },
                    onSelectButton = { rowIndex, buttonIndex, _ ->
                        selectedButtonSlot = ConsoleButtonSlotKey(rowIndex, buttonIndex)
                        selectedRowIndex = rowIndex.coerceIn(rows.indices)
                    },
                    onPickButton = { rowIndex ->
                        selectedRowIndex = rowIndex.coerceIn(rows.indices)
                        onPickButton(selectedRowIndex)
                    },
                    onPreviousPage = { onPageSwipe(-1) },
                    onNextPage = { onPageSwipe(1) },
                    onAddPage = onAddPage,
                    canAddRow = rows.size < MAX_CONSOLE_LAYOUT_ROWS,
                    onAddRow = ::addRowAndSelect,
                    onRemoveRow = { rowIndex ->
                        onRemoveRow(rowIndex)
                        selectedRowIndex = rowIndex.coerceAtMost((rows.size - 2).coerceAtLeast(0))
                    },
                    onMoveRow = { rowIndex, delta ->
                        val targetIndex = (rowIndex + delta).coerceIn(rows.indices)
                        onMoveRow(rowIndex, delta)
                        selectedRowIndex = targetIndex
                    },
                    onMoveButtonTo = { fromRowIndex, fromIndex, toRowIndex, toIndex ->
                        onMoveButtonTo(fromRowIndex, fromIndex, toRowIndex, toIndex)
                        selectedRowIndex = toRowIndex.coerceIn(rows.indices)
                        selectedButtonSlot = ConsoleButtonSlotKey(toRowIndex, toIndex)
                    },
                    onLayoutChange = onLayoutChange,
                    onDragStateChange = { dragInProgress = it }
                )
            }
        }
        if (editMode == ConsoleLayoutEditMode.Buttons) {
            item {
                val selectedButton = selectedButtonSlot?.let { slot ->
                    rows.getOrNull(slot.rowIndex)?.getOrNull(slot.buttonIndex)?.let { id -> buttonById[id] }
                }
                val safeRowIndex = selectedButtonSlot?.rowIndex?.takeIf { it in rows.indices }
                    ?: selectedRowIndex.coerceIn(rows.indices)
                val safeButtonIndex = selectedButtonSlot?.buttonIndex?.takeIf { index ->
                    index in rows.getOrNull(safeRowIndex).orEmpty().indices
                } ?: -1
                ConsoleButtonEditPanel(
                    selectedButton = selectedButton,
                    onPickButton = { onPickButton(safeRowIndex) },
                    onEditButton = { selectedButton?.let(onEditButton) },
                    onRemoveButton = {
                        if (selectedButton != null && safeButtonIndex >= 0) {
                            onRemoveButton(safeRowIndex, safeButtonIndex)
                            selectedButtonSlot = null
                        }
                    }
                )
            }
        }
        if (BuildConfig.DEBUG) {
            item {
                ConsoleLayoutDiagnosticsCard(diagnostics = diagnostics)
            }
        }
    }
}

@Composable
private fun ConsoleLayoutDiagnosticsCard(
    diagnostics: List<String>
) {
    val colors = LocalDeckThemeColors.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = colors.consolePreviewBackground,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .border(0.6.dp, colors.cardBorder.copy(alpha = 0.52f), RoundedCornerShape(14.dp))
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = stringResource(R.string.console_layout_diagnostics),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            if (diagnostics.isEmpty()) {
                Text(
                    text = stringResource(R.string.console_layout_diagnostics_empty),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
            } else {
                diagnostics.take(5).forEach { line ->
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun ConsoleLayoutEditorRowCard(
    rowIndex: Int,
    row: List<Int>,
    buttonById: Map<Int, DeckButton>,
    onPickButton: () -> Unit,
    onEditButton: (DeckButton) -> Unit,
    onRemoveButton: (Int) -> Unit,
    onMoveButton: (Int, Int) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            color = colors.consolePreviewBackground,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .border(0.6.dp, colors.cardBorder.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
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
                    ConsolePillButton(text = stringResource(R.string.add_button), onClick = onPickButton)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.mapNotNull { buttonById[it] }.forEachIndexed { index, button ->
                        ConsoleEditorButtonItem(
                            modifier = Modifier.weight(1f),
                            button = button,
                            canMoveLeft = index > 0,
                            canMoveRight = index < row.lastIndex,
                            onEdit = { onEditButton(button) },
                            onRemove = { onRemoveButton(button.id) },
                            onMoveLeft = { onMoveButton(index, -1) },
                            onMoveRight = { onMoveButton(index, 1) }
                        )
                    }
                    if (row.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF00A6E7).copy(alpha = 0.13f),
                            onClick = onPickButton
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

@Composable
private fun ConsoleButtonEditPanel(
    selectedButton: DeckButton?,
    onPickButton: () -> Unit,
    onEditButton: () -> Unit,
    onRemoveButton: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = colors.consolePreviewBackground,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .border(0.6.dp, colors.cardBorder.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(selectedButton?.let { consoleButtonColor(it) } ?: colors.consoleButtonDefault.copy(alpha = 0.48f))
                    .border(2.dp, Color(0xFF62B7FF).copy(alpha = if (selectedButton == null) 0.34f else 1f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                val icon = selectedButton?.let { materialIconFor(it) }
                if (icon != null) {
                    LiftedIcon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(38.dp),
                        shadowSize = 38.dp,
                        lifted = false
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = colors.textSecondary,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = selectedButton?.title?.takeIf { it.isNotBlank() } ?: stringResource(R.string.add_button),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        tint = colors.textPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = selectedButton?.subtitle?.takeIf { it.isNotBlank() }
                        ?: if (selectedButton == null) stringResource(R.string.console_button_edit_select_hint) else stringResource(R.string.edit_value_keyboard),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            ConsoleButtonEditPanelAction(
                icon = Icons.Filled.Add,
                text = stringResource(R.string.add_button),
                accent = Color(0xFF62B7FF),
                onClick = onPickButton
            )
            ConsoleButtonEditPanelAction(
                icon = Icons.Filled.Edit,
                text = stringResource(R.string.edit),
                accent = Color(0xFF62B7FF),
                enabled = selectedButton != null,
                onClick = onEditButton
            )
            ConsoleButtonEditPanelAction(
                icon = Icons.Filled.Delete,
                text = stringResource(R.string.delete),
                accent = Color(0xFFFF5B5B),
                enabled = selectedButton != null,
                onClick = onRemoveButton
            )
        }
    }
}

@Composable
private fun ConsoleButtonEditPanelAction(
    icon: ImageVector,
    text: String,
    accent: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    Surface(
        modifier = Modifier.size(width = 150.dp, height = 70.dp),
        shape = RoundedCornerShape(14.dp),
        color = if (enabled) accent.copy(alpha = 0.16f) else colors.consoleButtonDefault.copy(alpha = 0.28f),
        contentColor = if (enabled) Color.White else colors.textMuted,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        onClick = { if (enabled) onClick() }
    ) {
        Row(
            modifier = Modifier
                .border(1.dp, if (enabled) accent.copy(alpha = 0.72f) else colors.cardBorder.copy(alpha = 0.38f), RoundedCornerShape(14.dp))
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = if (enabled) accent else colors.textMuted, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (enabled) colors.textPrimary else colors.textMuted,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ConsoleRowOrderTag(
    modifier: Modifier = Modifier,
    selected: Boolean,
    dragThresholdPx: Float,
    onSelect: () -> Unit,
    onDragStart: () -> Unit,
    onDragOffset: (Float) -> Unit,
    onDragStop: () -> Unit,
    onMoveBy: (Int) -> Unit,
    onPreviewMove: (Int) -> Unit,
    resolveDrag: (Float) -> Pair<Int, Float>
) {
    val colors = LocalDeckThemeColors.current
    val darkTheme = isSystemInDarkTheme()
    var dragging by remember { mutableStateOf(false) }
    var dragY by remember { mutableStateOf(0f) }
    var pendingMoveDelta by remember { mutableStateOf(0) }
    val shape = RoundedCornerShape(10.dp)
    Surface(
        modifier = modifier
            .pointerInput(dragThresholdPx) {
                detectDragGestures(
                    onDragStart = {
                        dragging = true
                        dragY = 0f
                        pendingMoveDelta = 0
                        onPreviewMove(0)
                        onSelect()
                        onDragStart()
                    },
                    onDragCancel = {
                        dragging = false
                        dragY = 0f
                        pendingMoveDelta = 0
                        onPreviewMove(0)
                        onDragOffset(0f)
                        onDragStop()
                    },
                    onDragEnd = {
                        val moveDelta = pendingMoveDelta
                        dragging = false
                        dragY = 0f
                        pendingMoveDelta = 0
                        if (moveDelta != 0) {
                            onMoveBy(moveDelta)
                        } else {
                            onPreviewMove(0)
                            onDragOffset(0f)
                        }
                        onDragStop()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragY += dragAmount.y
                        val (moveDelta, residualOffset) = resolveDrag(dragY)
                        pendingMoveDelta = moveDelta
                        onDragOffset(residualOffset)
                        onPreviewMove(pendingMoveDelta)
                    }
                )
            }
            .border(
                if (selected || dragging) 2.dp else 1.dp,
                if (selected || dragging) {
                    if (darkTheme) Color(0xFF76DFFF) else Color(0xFF0876B8)
                } else {
                    if (darkTheme) Color.White.copy(alpha = 0.12f) else Color(0xFF6FA8C9).copy(alpha = 0.72f)
                },
                shape
            ),
        shape = shape,
        color = when {
            selected || dragging -> colors.consoleButtonFeatured.copy(alpha = 0.84f)
            darkTheme -> colors.consoleButtonDefault.copy(alpha = 0.68f)
            else -> Color.White.copy(alpha = 0.74f)
        },
        contentColor = when {
            selected || dragging -> Color.White
            darkTheme -> colors.textPrimary
            else -> Color(0xFF0A3147)
        },
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        onClick = onSelect
    ) {
        Column(
            modifier = Modifier.padding(vertical = 7.dp, horizontal = 5.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ConsoleDragDots(
                dotColor = if (selected || dragging) Color.White else if (darkTheme) colors.textSecondary else Color(0xFF0A3147)
            )
        }
    }
}

@Composable
private fun ConsoleDragDots(
    dotColor: Color
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(3) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(2) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                    )
                }
            }
        }
    }
}

@Composable
private fun ConsoleButtonDragHandle(
    modifier: Modifier = Modifier,
    selected: Boolean,
    horizontalThresholdPx: Float,
    verticalThresholdPx: Float,
    onSelect: () -> Unit,
    onDragStart: () -> Unit,
    onDragOffset: (Float, Float) -> Unit,
    onDragStop: () -> Unit,
    onMoveBy: (dragX: Float, dragY: Float) -> Unit,
    onPreviewMove: (dragX: Float, dragY: Float) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val darkTheme = isSystemInDarkTheme()
    var dragging by remember { mutableStateOf(false) }
    var dragX by remember { mutableStateOf(0f) }
    var dragY by remember { mutableStateOf(0f) }
    Surface(
        modifier = modifier
            .size(width = 42.dp, height = 34.dp)
            .pointerInput(horizontalThresholdPx, verticalThresholdPx) {
                detectDragGestures(
                    onDragStart = {
                        dragging = true
                        dragX = 0f
                        dragY = 0f
                        onPreviewMove(0f, 0f)
                        onSelect()
                        onDragStart()
                    },
                    onDragCancel = {
                        dragging = false
                        dragX = 0f
                        dragY = 0f
                        onPreviewMove(0f, 0f)
                        onDragOffset(0f, 0f)
                        onDragStop()
                    },
                    onDragEnd = {
                        val finalDragX = dragX
                        val finalDragY = dragY
                        dragging = false
                        dragX = 0f
                        dragY = 0f
                        if (finalDragX != 0f || finalDragY != 0f) {
                            onMoveBy(finalDragX, finalDragY)
                        } else {
                            onPreviewMove(0f, 0f)
                            onDragOffset(0f, 0f)
                        }
                        onDragStop()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragX += dragAmount.x
                        dragY += dragAmount.y
                        onDragOffset(dragX, dragY)
                        onPreviewMove(dragX, dragY)
                    }
                )
            },
        shape = RoundedCornerShape(10.dp),
        color = when {
            dragging || selected -> colors.consoleButtonFeatured.copy(alpha = 0.86f)
            darkTheme -> Color.Black.copy(alpha = 0.28f)
            else -> Color.White.copy(alpha = 0.76f)
        },
        contentColor = if (dragging || selected || darkTheme) Color.White else Color(0xFF0A3147),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        onClick = onSelect
    ) {
        Box(contentAlignment = Alignment.Center) {
            ConsoleDragDots(
                dotColor = if (dragging || selected || darkTheme) Color.White else Color(0xFF0A3147)
            )
        }
    }
}

@Composable
private fun ConsoleLayoutAddRowFooter(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    onAddRow: () -> Unit
) {
    val darkTheme = isSystemInDarkTheme()
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (darkTheme) Color(0xFF00A6E7).copy(alpha = 0.13f) else Color(0xFFD9EEF8),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        onClick = { if (enabled) onAddRow() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = when {
                    !enabled -> if (darkTheme) Color.White.copy(alpha = 0.34f) else Color(0xFF7E95A5)
                    darkTheme -> Color(0xFF76DFFF)
                    else -> Color(0xFF0876B8)
                },
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.add_console_row),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = when {
                    !enabled -> if (darkTheme) Color.White.copy(alpha = 0.34f) else Color(0xFF7E95A5)
                    darkTheme -> Color(0xFF76DFFF)
                    else -> Color(0xFF0876B8)
                }
            )
        }
    }
}

@Composable
private fun ConsoleLayoutPageControls(
    modifier: Modifier = Modifier,
    activePageIndex: Int,
    pageCount: Int,
    onAddPage: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.widthIn(min = 42.dp),
            text = stringResource(R.string.layout_editor_page_position, activePageIndex + 1, pageCount),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = LocalDeckThemeColors.current.textPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        ConsoleLayoutEditorIconButton(
            icon = Icons.Filled.Add,
            label = stringResource(R.string.add_page),
            enabled = pageCount < MAX_PAGES,
            onClick = onAddPage
        )
    }
}

@Composable
private fun ConsoleLayoutEditorIconButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    enabled: Boolean = true,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val darkTheme = isSystemInDarkTheme()
    val shape = RoundedCornerShape(12.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val active = enabled && (selected || pressed)
    Surface(
        modifier = modifier
            .size(width = 42.dp, height = 34.dp)
            .consoleButtonDropShadow(
                shape = shape,
                darkTheme = darkTheme,
                pressed = pressed,
                drawHairline = false
            ),
        shape = shape,
        color = when {
            !enabled -> colors.toggleBackground
            active -> colors.consoleButtonFeatured
            else -> colors.consoleButtonDefault
        },
        contentColor = when {
            !enabled -> colors.textMuted
            active -> Color.White
            darkTheme -> colors.textPrimary
            else -> Color(0xFF0A3147)
        },
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        interactionSource = interactionSource,
        onClick = { if (enabled) onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(17.dp)
            )
        }
    }
}

@Composable
private fun ConsoleRowDeleteIcon(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    selected: Boolean,
    onClick: () -> Unit
) {
    val darkTheme = isSystemInDarkTheme()
    val borderColor = when {
        !enabled -> if (darkTheme) Color.White.copy(alpha = 0.16f) else Color(0xFF9AACB8).copy(alpha = 0.5f)
        selected -> if (darkTheme) Color(0xFFFF8A8A) else Color(0xFFB21F2D)
        darkTheme -> Color.White.copy(alpha = 0.42f)
        else -> Color(0xFF24526D)
    }
    val iconColor = when {
        !enabled -> if (darkTheme) Color.White.copy(alpha = 0.28f) else Color(0xFF8A9AA6)
        selected -> if (darkTheme) Color(0xFFFFA0A0) else Color(0xFFB21F2D)
        darkTheme -> Color.White.copy(alpha = 0.86f)
        else -> Color(0xFF183E54)
    }
    Surface(
        modifier = modifier
            .size(36.dp)
            .border(1.6.dp, borderColor, CircleShape),
        shape = CircleShape,
        color = if (darkTheme) Color.Black.copy(alpha = 0.14f) else Color.White.copy(alpha = 0.72f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        onClick = { if (enabled) onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = stringResource(R.string.remove_row),
                modifier = Modifier.size(18.dp),
                tint = iconColor
            )
        }
    }
}

@Composable
private fun ConsoleButtonAddOverlayIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val darkTheme = isSystemInDarkTheme()
    Surface(
        modifier = modifier
            .size(36.dp)
            .border(
                1.4.dp,
                if (darkTheme) Color(0xFF76DFFF).copy(alpha = 0.72f) else Color(0xFF0876B8).copy(alpha = 0.72f),
                CircleShape
            ),
        shape = CircleShape,
        color = if (darkTheme) Color.Black.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.74f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_button),
                modifier = Modifier.size(20.dp),
                tint = if (darkTheme) Color(0xFF76DFFF) else Color(0xFF0876B8)
            )
        }
    }
}

@Composable
private fun ConsoleSidebarGapIndicator(
    modifier: Modifier = Modifier
) {
    val darkTheme = isSystemInDarkTheme()
    Canvas(modifier = modifier) {
        val lineWidth = 1.2.dp.toPx()
        val dashHeight = 7.dp.toPx()
        val dashGap = 7.dp.toPx()
        val x = size.width / 2f - lineWidth / 2f
        var y = 0f
        while (y < size.height) {
            drawRoundRect(
                color = (if (darkTheme) Color(0xFF76DFFF) else Color(0xFF0876B8)).copy(alpha = 0.62f),
                topLeft = Offset(x, y),
                size = Size(lineWidth, dashHeight.coerceAtMost(size.height - y)),
                cornerRadius = CornerRadius(lineWidth, lineWidth)
            )
            y += dashHeight + dashGap
        }
    }
}

@Composable
private fun ConsoleRowBoundaryIndicator(
    modifier: Modifier = Modifier
) {
    val darkTheme = isSystemInDarkTheme()
    Canvas(modifier = modifier) {
        val lineHeight = 1.2.dp.toPx()
        val dashWidth = 8.dp.toPx()
        val dashGap = 7.dp.toPx()
        val y = size.height / 2f - lineHeight / 2f
        var x = 0f
        while (x < size.width) {
            drawRoundRect(
                color = (if (darkTheme) Color(0xFF76DFFF) else Color(0xFF0876B8)).copy(alpha = 0.52f),
                topLeft = Offset(x, y),
                size = Size(dashWidth.coerceAtMost(size.width - x), lineHeight),
                cornerRadius = CornerRadius(lineHeight, lineHeight)
            )
            x += dashWidth + dashGap
        }
    }
}

@Composable
private fun ConsoleLayoutTuningPreview(
    modifier: Modifier = Modifier,
    layout: ConsoleLayoutConfig,
    buttons: List<DeckButton>,
    activePageIndex: Int,
    pageCount: Int,
    selectedRowIndex: Int,
    selectedButtonSlot: ConsoleButtonSlotKey?,
    editMode: ConsoleLayoutEditMode,
    onSelectRow: (Int) -> Unit,
    onSelectButton: (Int, Int, DeckButton) -> Unit,
    onPickButton: (Int) -> Unit,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    onAddPage: () -> Unit,
    canAddRow: Boolean,
    onAddRow: () -> Unit,
    onRemoveRow: (Int) -> Unit,
    onMoveRow: (Int, Int) -> Unit,
    onMoveButtonTo: (Int, Int, Int, Int) -> Unit,
    onLayoutChange: (ConsoleLayoutConfig) -> Unit,
    onDragStateChange: (Boolean) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val density = LocalDensity.current
    val buttonById = buttons.associateBy { it.id }
    val rows = consoleLayoutRowIds(
        layout = layout,
        buttons = buttons.filterNot { buttonAppAction(it) == DeckActionType.Settings },
        columns = DEFAULT_COLUMNS,
        rows = DEFAULT_ROWS
    ).ifEmpty { listOf(emptyList()) }
    val rowButtons = rows.map { row -> row.mapNotNull { buttonById[it] } }
    BoxWithConstraints(modifier = modifier) {
        var workingSidebarFraction by remember(layout.sidebarFraction) {
            mutableStateOf(layout.sidebarFraction)
        }
        val workingLayout = layout.copy(sidebarFraction = workingSidebarFraction)
        val contentPadding = 12.dp
        val contentGap = 14.dp
        val bottomControlsGap = 10.dp
        val bottomControlsHeight = 42.dp
        val contentWidth = (maxWidth - contentPadding * 2f).coerceAtLeast(1.dp)
        val layoutAreaHeight = (maxHeight - contentPadding * 2f - bottomControlsGap - bottomControlsHeight).coerceAtLeast(1.dp)
        val sidebarWidth = consoleSidebarWidth(contentWidth, workingSidebarFraction)
        val rowsAreaX = contentPadding + sidebarWidth + contentGap
        val rowsAreaWidth = (contentWidth - sidebarWidth - contentGap).coerceAtLeast(1.dp)
        val editOverlayEndInset = if (editMode == ConsoleLayoutEditMode.Layout) 20.dp else 0.dp
        val visibleRowsAreaWidth = (rowsAreaWidth - editOverlayEndInset).coerceAtLeast(1.dp)
        val addRowWidth = if (visibleRowsAreaWidth < 190.dp) visibleRowsAreaWidth else visibleRowsAreaWidth.coerceAtMost(260.dp)
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(18.dp),
            color = colors.backgroundGradient.first(),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .background(Brush.linearGradient(colors.backgroundGradient))
                    .border(0.6.dp, colors.cardBorder.copy(alpha = 0.48f), RoundedCornerShape(18.dp))
                    .padding(contentPadding),
                verticalArrangement = Arrangement.spacedBy(bottomControlsGap)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(contentGap)
                ) {
                    ConsoleLayoutPreviewSidebar(
                        modifier = Modifier
                            .width(sidebarWidth)
                            .fillMaxHeight()
                    )
                    ConsoleLayoutPreviewRows(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(end = editOverlayEndInset),
                        rows = rowButtons,
                        layout = workingLayout,
                        selectedRowIndex = selectedRowIndex,
                        selectedButtonSlot = selectedButtonSlot,
                        editMode = editMode,
                        onSelectRow = onSelectRow,
                        onSelectButton = onSelectButton,
                        onPickButton = onPickButton,
                        onRemoveRow = onRemoveRow,
                        onMoveRow = onMoveRow,
                        onMoveButtonTo = onMoveButtonTo,
                        onLayoutChange = { updated ->
                            onLayoutChange(updated.copy(sidebarFraction = workingSidebarFraction))
                        },
                        onDragStateChange = onDragStateChange
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(bottomControlsHeight)
                ) {
                    ConsoleLayoutEditorIconButton(
                        modifier = Modifier.align(Alignment.CenterStart),
                        icon = Icons.Filled.SkipPrevious,
                        label = stringResource(R.string.action_previous_page),
                        enabled = activePageIndex > 0,
                        onClick = onPreviousPage
                    )
                    ConsoleLayoutPageControls(
                        modifier = Modifier.align(Alignment.Center),
                        activePageIndex = activePageIndex,
                        pageCount = pageCount,
                        onAddPage = onAddPage
                    )
                    ConsoleLayoutEditorIconButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        icon = Icons.Filled.SkipNext,
                        label = stringResource(R.string.action_next_page),
                        enabled = activePageIndex < pageCount - 1,
                        onClick = onNextPage
                    )
                }
            }
        }
        if (editMode == ConsoleLayoutEditMode.Layout) {
            ConsoleLayoutAddRowFooter(
                modifier = Modifier
                    .offset(
                        x = rowsAreaX + (visibleRowsAreaWidth - addRowWidth) / 2f,
                        y = contentPadding + layoutAreaHeight - 17.dp
                    )
                    .width(addRowWidth)
                    .height(34.dp)
                    .zIndex(8f),
                enabled = canAddRow,
                onAddRow = onAddRow
            )
            ConsoleSidebarGapIndicator(
                modifier = Modifier
                    .offset(x = contentPadding + sidebarWidth, y = contentPadding)
                    .width(contentGap)
                    .height(layoutAreaHeight)
                    .zIndex(5f)
            )
                ConsoleVerticalSplitHandle(
                    modifier = Modifier
                    .offset(x = contentPadding + sidebarWidth + contentGap / 2f - 16.dp, y = (-18).dp)
                    .width(32.dp)
                    .height(72.dp)
                    .zIndex(8f),
                onDelta = { deltaPx ->
                    val totalWidthPx = with(density) { contentWidth.toPx() }.coerceAtLeast(1f)
                    workingSidebarFraction = (workingSidebarFraction + deltaPx / totalWidthPx)
                        .coerceIn(CONSOLE_MIN_SIDEBAR_FRACTION, CONSOLE_MAX_SIDEBAR_FRACTION)
                    onLayoutChange(workingLayout.copy(sidebarFraction = workingSidebarFraction))
                }
            )
        }
    }
}

@Composable
private fun ConsoleLayoutPreviewSidebar(modifier: Modifier = Modifier) {
    val colors = LocalDeckThemeColors.current
    val darkTheme = isSystemInDarkTheme()
    val shape = RoundedCornerShape(20.dp)
    Surface(
        modifier = modifier.consolePanelDropShadow(
            shape = shape,
            darkTheme = darkTheme
        ),
        shape = shape,
        color = colors.consoleSidebar,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        ConsoleRaisedButtonFrame(
            modifier = Modifier.fillMaxSize(),
            shape = shape,
            cornerRadius = 20.dp,
            drawShadow = false,
            drawHighlight = false
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LiftedText(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary,
                        lifted = false
                    )
                    Text(
                        text = stringResource(R.string.status_connected),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFF79E46F)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(colors.consoleButtonDefault),
                    contentAlignment = Alignment.Center
                ) {
                    LiftedIcon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        shadowSize = 22.dp,
                        tint = colors.textPrimary,
                        lifted = true
                    )
                }
            }
        }
    }
}

@Composable
private fun ConsoleLayoutPreviewRows(
    modifier: Modifier = Modifier,
    rows: List<List<DeckButton>>,
    layout: ConsoleLayoutConfig,
    selectedRowIndex: Int,
    selectedButtonSlot: ConsoleButtonSlotKey?,
    editMode: ConsoleLayoutEditMode,
    onSelectRow: (Int) -> Unit,
    onSelectButton: (Int, Int, DeckButton) -> Unit,
    onPickButton: (Int) -> Unit,
    onRemoveRow: (Int) -> Unit,
    onMoveRow: (Int, Int) -> Unit,
    onMoveButtonTo: (Int, Int, Int, Int) -> Unit,
    onLayoutChange: (ConsoleLayoutConfig) -> Unit,
    onDragStateChange: (Boolean) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val density = LocalDensity.current
    val rowSpacing = 10.dp
    val buttonSpacing = 12.dp
    BoxWithConstraints(modifier = modifier) {
        val previewRowsWidth = maxWidth
        val safeRows = rows.ifEmpty { listOf(emptyList()) }
        val selectedIndex = selectedRowIndex.coerceIn(safeRows.indices)
        var weights by remember(layout.rows, layout.rowWeights, safeRows.size) {
            mutableStateOf(normalizedConsoleRowWeights(layout, safeRows.size))
        }
        val totalWeight = weights.sum().takeIf { it > 0f } ?: safeRows.size.toFloat()
        val availableHeight = (maxHeight -
            rowSpacing * (safeRows.size - 1).coerceAtLeast(0).toFloat()).coerceAtLeast(1.dp)
        val rowHeights = weights.map { availableHeight * (it / totalWeight) }
        val rowTops = mutableListOf<Dp>()
        var accumulatedTop = 0.dp
        rowHeights.forEach { rowHeight ->
            rowTops += accumulatedTop
            accumulatedTop += rowHeight + rowSpacing
        }
        fun previewTargetIndex(rowIndex: Int, move: Pair<Int, Int>?): Int {
            if (move == null) return rowIndex
            val (fromIndex, toIndex) = move
            return when {
                rowIndex == fromIndex -> toIndex
                fromIndex < toIndex && rowIndex in (fromIndex + 1)..toIndex -> rowIndex - 1
                fromIndex > toIndex && rowIndex in toIndex until fromIndex -> rowIndex + 1
                else -> rowIndex
            }.coerceIn(rowHeights.indices)
        }
        fun rowPreviewOffset(rowIndex: Int, move: Pair<Int, Int>?): Dp {
            val targetIndex = previewTargetIndex(rowIndex, move)
            return rowTops[targetIndex] - rowTops[rowIndex]
        }
        fun rowPreviewHeight(rowIndex: Int, move: Pair<Int, Int>?): Dp {
            return rowHeights[previewTargetIndex(rowIndex, move)]
        }
        fun resolveRowDragDelta(rowIndex: Int, dragOffsetPx: Float): Pair<Int, Float> {
            if (rowHeights.isEmpty()) return 0 to 0f
            val draggedCenter = rowTops[rowIndex] + rowHeights[rowIndex] / 2f + with(density) { dragOffsetPx.toDp() }
            val targetIndex = rowHeights.indices.minBy { index ->
                abs(with(density) { ((rowTops[index] + rowHeights[index] / 2f) - draggedCenter).toPx() })
            }
            val baseOffsetPx = with(density) { (rowTops[targetIndex] - rowTops[rowIndex]).toPx() }
            return (targetIndex - rowIndex) to (dragOffsetPx - baseOffsetPx)
        }
        val buttonPreviewBounds = safeRows.mapIndexed { rowIndex, row ->
            val gapWidth = buttonSpacing * (row.size - 1).coerceAtLeast(0).toFloat()
            val fixedWidth = row.fold(0.dp) { total, button ->
                if (button.isTrimControl()) total + rowHeights[rowIndex] else total
            }
            val weightedButtons = row.filterNot { it.isTrimControl() }
            val totalButtonWeight = weightedButtons
                .sumOf { it.spanColumns.coerceAtLeast(1).toDouble() }
                .toFloat()
                .takeIf { it > 0f } ?: 1f
            val flexibleWidth = (previewRowsWidth - gapWidth - fixedWidth).coerceAtLeast(1.dp)
            var left = 0.dp
            row.map { button ->
                val width = if (button.isTrimControl()) {
                    rowHeights[rowIndex]
                } else {
                    flexibleWidth * (button.spanColumns.coerceAtLeast(1).toFloat() / totalButtonWeight)
                }
                ConsoleButtonPreviewBounds(left = left, width = width)
                    .also { left += width + buttonSpacing }
            }
        }
        fun buttonSwapPreviewOffset(rowIndex: Int, buttonIndex: Int, move: ButtonPreviewMove?): ConsoleButtonPreviewOffset {
            if (move == null) return ConsoleButtonPreviewOffset.Zero
            val fromBounds = buttonPreviewBounds.getOrNull(move.fromRowIndex)?.getOrNull(move.fromIndex)
                ?: return ConsoleButtonPreviewOffset.Zero
            val toBounds = buttonPreviewBounds.getOrNull(move.toRowIndex)?.getOrNull(move.toIndex)
                ?: return ConsoleButtonPreviewOffset.Zero
            return when {
                rowIndex == move.fromRowIndex && buttonIndex == move.fromIndex -> {
                    ConsoleButtonPreviewOffset(
                        x = toBounds.left - fromBounds.left,
                        y = rowTops[move.toRowIndex] - rowTops[move.fromRowIndex]
                    )
                }
                rowIndex == move.toRowIndex && buttonIndex == move.toIndex -> {
                    ConsoleButtonPreviewOffset(
                        x = fromBounds.left - toBounds.left,
                        y = rowTops[move.fromRowIndex] - rowTops[move.toRowIndex]
                    )
                }
                else -> ConsoleButtonPreviewOffset.Zero
            }
        }
        fun buttonSwapPreviewScale(rowIndex: Int, buttonIndex: Int, move: ButtonPreviewMove?): ConsoleButtonPreviewScale {
            if (move == null) return ConsoleButtonPreviewScale.Normal
            val fromBounds = buttonPreviewBounds.getOrNull(move.fromRowIndex)?.getOrNull(move.fromIndex)
                ?: return ConsoleButtonPreviewScale.Normal
            val toBounds = buttonPreviewBounds.getOrNull(move.toRowIndex)?.getOrNull(move.toIndex)
                ?: return ConsoleButtonPreviewScale.Normal
            return when {
                rowIndex == move.fromRowIndex && buttonIndex == move.fromIndex -> {
                    ConsoleButtonPreviewScale(
                        x = (toBounds.width.value / fromBounds.width.value.coerceAtLeast(1f)).coerceAtLeast(0.1f),
                        y = (rowHeights[move.toRowIndex].value / rowHeights[move.fromRowIndex].value.coerceAtLeast(1f)).coerceAtLeast(0.1f)
                    )
                }
                rowIndex == move.toRowIndex && buttonIndex == move.toIndex -> {
                    ConsoleButtonPreviewScale(
                        x = (fromBounds.width.value / toBounds.width.value.coerceAtLeast(1f)).coerceAtLeast(0.1f),
                        y = (rowHeights[move.fromRowIndex].value / rowHeights[move.toRowIndex].value.coerceAtLeast(1f)).coerceAtLeast(0.1f)
                    )
                }
                else -> ConsoleButtonPreviewScale.Normal
            }
        }
        fun resolveButtonDragMove(fromRowIndex: Int, fromIndex: Int, dragX: Float, dragY: Float): ButtonPreviewMove? {
            val fromBounds = buttonPreviewBounds.getOrNull(fromRowIndex)?.getOrNull(fromIndex) ?: return null
            val sourceCenterX = fromBounds.left + fromBounds.width / 2f
            val sourceCenterY = rowTops.getOrNull(fromRowIndex)?.let { it + rowHeights[fromRowIndex] / 2f } ?: return null
            val draggedCenterX = sourceCenterX + with(density) { dragX.toDp() }
            val draggedCenterY = sourceCenterY + with(density) { dragY.toDp() }
            val targetRowIndex = rowHeights.indices.minByOrNull { index ->
                abs(with(density) { ((rowTops[index] + rowHeights[index] / 2f) - draggedCenterY).toPx() })
            } ?: return null
            val targetRowBounds = buttonPreviewBounds.getOrNull(targetRowIndex).orEmpty()
            if (targetRowBounds.isEmpty()) return null
            val targetIndex = targetRowBounds.indices.minByOrNull { index ->
                val targetBounds = targetRowBounds[index]
                abs(with(density) { ((targetBounds.left + targetBounds.width / 2f) - draggedCenterX).toPx() })
            } ?: return null
            return if (targetRowIndex == fromRowIndex && targetIndex == fromIndex) {
                null
            } else {
                ButtonPreviewMove(fromRowIndex, fromIndex, targetRowIndex, targetIndex)
            }
        }
    var draggingRowIndex by remember { mutableStateOf<Int?>(null) }
    var rowDragOffsetPx by remember { mutableStateOf(0f) }
    var rowPreviewMove by remember { mutableStateOf<Pair<Int, Int>?>(null) }
    var draggingButtonSlot by remember { mutableStateOf<ConsoleButtonSlotKey?>(null) }
    var buttonDragOffsetX by remember { mutableStateOf(0f) }
    var buttonDragOffsetY by remember { mutableStateOf(0f) }
    var buttonPreviewMove by remember { mutableStateOf<ButtonPreviewMove?>(null) }
        fun updateButtonDragPreview(fromRowIndex: Int, fromIndex: Int, dragX: Float, dragY: Float) {
            val move = resolveButtonDragMove(fromRowIndex, fromIndex, dragX, dragY)
            buttonPreviewMove = move
            val previewOffset = buttonSwapPreviewOffset(fromRowIndex, fromIndex, move)
            buttonDragOffsetX = dragX - with(density) { previewOffset.x.toPx() }
            buttonDragOffsetY = dragY - with(density) { previewOffset.y.toPx() }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            safeRows.forEachIndexed { rowIndex, buttons ->
                val rowKey = buttons.joinToString(separator = ":") { it.id.toString() }.ifBlank { "empty-$rowIndex" }
                key(rowKey) {
                    val selected = rowIndex == selectedIndex
                    val dragging = draggingRowIndex == rowIndex
                    val previewRowOffset = rowPreviewOffset(rowIndex, rowPreviewMove)
                    val previewRowHeight = rowPreviewHeight(rowIndex, rowPreviewMove)
                    val animatedTop by animateDpAsState(rowTops[rowIndex], label = "consoleRowTop")
                    val animatedPreviewRowOffset by animateDpAsState(previewRowOffset, label = "consoleRowPreviewOffset")
                    val animatedHeight by animateDpAsState(previewRowHeight, label = "consoleRowHeight")
                    Row(
                        modifier = Modifier
                            .offset(y = animatedTop + animatedPreviewRowOffset)
                            .zIndex(if (dragging) 5f else 0f)
                            .graphicsLayer {
                                translationY = if (dragging) rowDragOffsetPx else 0f
                                alpha = if (dragging) 0.68f else 1f
                                scaleX = if (dragging) 0.985f else 1f
                                scaleY = if (dragging) 0.985f else 1f
                            }
                            .fillMaxWidth()
                            .height(animatedHeight)
                            .border(
                                width = if (selected && editMode == ConsoleLayoutEditMode.Layout) 2.dp else 0.dp,
                                color = if (selected && editMode == ConsoleLayoutEditMode.Layout) {
                                    if (isSystemInDarkTheme()) Color(0xFF76DFFF) else Color(0xFF0876B8)
                                } else {
                                    Color.Transparent
                                },
                                shape = RoundedCornerShape(18.dp)
                            )
                            .pointerInput(rowIndex, editMode) {
                                detectTapGestures {
                                    if (editMode == ConsoleLayoutEditMode.Layout) onSelectRow(rowIndex)
                                }
                            }
                            .padding(if (selected && editMode == ConsoleLayoutEditMode.Layout) 2.dp else 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
                    ) {
                        if (buttons.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(colors.consoleButtonDefault.copy(alpha = 0.26f))
                            )
                        } else {
                            buttons.forEachIndexed { buttonIndex, button ->
                                val slotKey = ConsoleButtonSlotKey(rowIndex, buttonIndex)
                                val draggingButton = draggingButtonSlot == slotKey
                                val selectedButton = selectedButtonSlot == slotKey
                                val previewButtonOffset = buttonSwapPreviewOffset(rowIndex, buttonIndex, buttonPreviewMove)
                                val previewButtonScale = buttonSwapPreviewScale(rowIndex, buttonIndex, buttonPreviewMove)
                                val animatedPreviewButtonX by animateDpAsState(previewButtonOffset.x, label = "consoleButtonPreviewX")
                                val animatedPreviewButtonY by animateDpAsState(previewButtonOffset.y, label = "consoleButtonPreviewY")
                                val animatedPreviewScaleX by animateFloatAsState(previewButtonScale.x, label = "consoleButtonPreviewScaleX")
                                val animatedPreviewScaleY by animateFloatAsState(previewButtonScale.y, label = "consoleButtonPreviewScaleY")
                                val previewButtonModifier = if (button.isTrimControl()) {
                                    Modifier
                                        .width(rowHeights[rowIndex])
                                        .fillMaxHeight()
                                } else {
                                    Modifier
                                        .weight(button.spanColumns.coerceAtLeast(1).toFloat())
                                        .fillMaxHeight()
                                }
                                Box(
                                    modifier = previewButtonModifier
                                        .zIndex(if (draggingButton) 6f else 0f)
                                        .graphicsLayer {
                                            val previewX = with(density) { animatedPreviewButtonX.toPx() }
                                            val previewY = with(density) { animatedPreviewButtonY.toPx() }
                                            translationX = if (draggingButton) previewX + buttonDragOffsetX else previewX
                                            translationY = if (draggingButton) previewY + buttonDragOffsetY else previewY
                                            alpha = if (draggingButton) 0.72f else 1f
                                            transformOrigin = TransformOrigin(0f, 0f)
                                            scaleX = animatedPreviewScaleX * if (draggingButton) 0.985f else 1f
                                            scaleY = animatedPreviewScaleY * if (draggingButton) 0.985f else 1f
                                        }
                                ) {
                                    ConsoleLayoutPreviewButton(
                                        modifier = Modifier.fillMaxSize(),
                                        button = button,
                                        selected = editMode == ConsoleLayoutEditMode.Buttons && selectedButton,
                                        onClick = {
                                            if (editMode == ConsoleLayoutEditMode.Buttons) {
                                                onSelectRow(rowIndex)
                                                onSelectButton(rowIndex, buttonIndex, button)
                                            }
                                        }
                                    )
                                    if (editMode == ConsoleLayoutEditMode.Buttons) {
                                        ConsoleButtonDragHandle(
                                            modifier = Modifier
                                                .align(Alignment.CenterStart)
                                                .offset(x = (-21).dp)
                                                .zIndex(8f),
                                            selected = selectedButton,
                                            horizontalThresholdPx = with(density) {
                                                ((previewRowsWidth - buttonSpacing * (DEFAULT_COLUMNS - 1).toFloat()) / DEFAULT_COLUMNS.toFloat() + buttonSpacing).toPx()
                                            },
                                            verticalThresholdPx = with(density) { (rowHeights[rowIndex] * 0.48f + rowSpacing * 0.5f).toPx() },
                                            onSelect = {
                                                onSelectRow(rowIndex)
                                                onSelectButton(rowIndex, buttonIndex, button)
                                            },
                                            onDragStart = {
                                                draggingButtonSlot = slotKey
                                                buttonDragOffsetX = 0f
                                                buttonDragOffsetY = 0f
                                                buttonPreviewMove = null
                                                onDragStateChange(true)
                                            },
                                            onDragOffset = { x, y ->
                                                updateButtonDragPreview(rowIndex, buttonIndex, x, y)
                                            },
                                            onDragStop = {
                                                draggingButtonSlot = null
                                                buttonDragOffsetX = 0f
                                                buttonDragOffsetY = 0f
                                                buttonPreviewMove = null
                                                onDragStateChange(false)
                                            },
                                            onMoveBy = { dragX, dragY ->
                                                val move = resolveButtonDragMove(rowIndex, buttonIndex, dragX, dragY)
                                                if (move != null) {
                                                    onMoveButtonTo(move.fromRowIndex, move.fromIndex, move.toRowIndex, move.toIndex)
                                                    onSelectRow(move.toRowIndex)
                                                    onSelectButton(move.toRowIndex, move.toIndex, button)
                                                }
                                            },
                                            onPreviewMove = { dragX, dragY ->
                                                updateButtonDragPreview(rowIndex, buttonIndex, dragX, dragY)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (editMode == ConsoleLayoutEditMode.Layout) {
            var boundaryY = 0.dp
            rowHeights.dropLast(1).forEachIndexed { index, rowHeight ->
                boundaryY += rowHeight
                ConsoleRowBoundaryIndicator(
                    modifier = Modifier
                        .offset(x = 0.dp, y = boundaryY + rowSpacing / 2f - 1.dp)
                        .fillMaxWidth()
                        .height(2.dp)
                        .zIndex(5f)
                )
                ConsoleRowHeightHandle(
                    modifier = Modifier
                        .offset(x = (-52).dp, y = boundaryY + rowSpacing / 2f - 17.dp)
                        .width(48.dp)
                        .height(34.dp)
                        .zIndex(6f),
                    onDelta = { deltaPx ->
                        val totalHeightPx = with(density) { maxHeight.toPx() }.coerceAtLeast(1f)
                        val deltaWeight = deltaPx / totalHeightPx * totalWeight
                        val updatedWeights = adjustConsoleRowBoundary(weights, index, deltaWeight)
                        weights = updatedWeights
                        onLayoutChange(layout.copy(rowWeights = updatedWeights))
                    }
                )
                boundaryY += rowSpacing
            }

            var rowTop = 0.dp
            rowHeights.forEachIndexed { rowIndex, rowHeight ->
                val selected = rowIndex == selectedIndex
                val dragging = draggingRowIndex == rowIndex
                val previewRowOffset = rowPreviewOffset(rowIndex, rowPreviewMove)
                val previewRowHeight = rowPreviewHeight(rowIndex, rowPreviewMove)
                val animatedTop by animateDpAsState(rowTop, label = "consoleRowControlTop")
                val animatedPreviewRowOffset by animateDpAsState(previewRowOffset, label = "consoleRowControlPreviewOffset")
                val dragOffset = if (dragging) with(density) { rowDragOffsetPx.toDp() } else 0.dp
                val visualTop = animatedTop + animatedPreviewRowOffset + dragOffset
                ConsoleRowOrderTag(
                    modifier = Modifier
                        .offset(x = (-14).dp, y = visualTop + previewRowHeight / 2f - 23.dp)
                        .width(28.dp)
                        .height(46.dp)
                        .zIndex(if (dragging) 9f else 7f),
                    selected = selected,
                    dragThresholdPx = with(density) { (rowHeight * 0.48f + rowSpacing * 0.5f).toPx() },
                    onSelect = { onSelectRow(rowIndex) },
                    onDragStart = {
                        draggingRowIndex = rowIndex
                        rowDragOffsetPx = 0f
                        rowPreviewMove = null
                        onDragStateChange(true)
                    },
                    onDragOffset = { offsetPx ->
                        rowDragOffsetPx = offsetPx
                    },
                    onDragStop = {
                        draggingRowIndex = null
                        rowDragOffsetPx = 0f
                        rowPreviewMove = null
                        onDragStateChange(false)
                    },
                    onMoveBy = { delta ->
                        val targetIndex = (rowIndex + delta).coerceIn(safeRows.indices)
                        if (targetIndex != rowIndex) {
                            draggingRowIndex = targetIndex
                            onMoveRow(rowIndex, delta)
                            onSelectRow(targetIndex)
                        }
                    },
                    onPreviewMove = { delta ->
                        val targetIndex = (rowIndex + delta).coerceIn(safeRows.indices)
                        rowPreviewMove = if (targetIndex != rowIndex) rowIndex to targetIndex else null
                    },
                    resolveDrag = { dragOffsetPx -> resolveRowDragDelta(rowIndex, dragOffsetPx) }
                )
                ConsoleRowDeleteIcon(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 18.dp, y = visualTop + previewRowHeight / 2f - 18.dp)
                        .zIndex(if (dragging) 9f else 7f),
                    enabled = true,
                    selected = selected,
                    onClick = {
                        onRemoveRow(rowIndex)
                        onSelectRow(rowIndex.coerceAtMost((safeRows.size - 2).coerceAtLeast(0)))
                    }
                )
                rowTop += rowHeight + rowSpacing
            }
        }
        if (editMode == ConsoleLayoutEditMode.Buttons) {
            var rowTop = 0.dp
            rowHeights.forEachIndexed { rowIndex, rowHeight ->
                val animatedTop by animateDpAsState(rowTop, label = "consoleButtonAddTop")
                ConsoleButtonAddOverlayIcon(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-8).dp, y = animatedTop + rowHeight / 2f - 18.dp)
                        .zIndex(7f),
                    onClick = {
                        onSelectRow(rowIndex)
                        onPickButton(rowIndex)
                    }
                )
                rowTop += rowHeight + rowSpacing
            }
        }
    }
}

private data class ButtonPreviewMove(
    val fromRowIndex: Int,
    val fromIndex: Int,
    val toRowIndex: Int,
    val toIndex: Int
)

private data class ConsoleButtonPreviewBounds(
    val left: Dp,
    val width: Dp
)

private data class ConsoleButtonPreviewOffset(
    val x: Dp,
    val y: Dp
) {
    companion object {
        val Zero = ConsoleButtonPreviewOffset(0.dp, 0.dp)
    }
}

private data class ConsoleButtonPreviewScale(
    val x: Float,
    val y: Float
) {
    companion object {
        val Normal = ConsoleButtonPreviewScale(1f, 1f)
    }
}

private data class ConsoleButtonSlotKey(
    val rowIndex: Int,
    val buttonIndex: Int
)

private enum class ButtonVisualShape {
    Horizontal,
    Square,
    Vertical
}

private fun buttonVisualShape(width: Dp, height: Dp): ButtonVisualShape {
    val safeHeight = height.value.coerceAtLeast(1f)
    val ratio = width.value / safeHeight
    return when {
        ratio >= 1.55f -> ButtonVisualShape.Horizontal
        ratio <= 0.72f -> ButtonVisualShape.Vertical
        else -> ButtonVisualShape.Square
    }
}

@Composable
private fun ConsoleRaisedButtonFrame(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(18.dp),
    cornerRadius: Dp = 18.dp,
    pressed: Boolean = false,
    drawShadow: Boolean = true,
    drawHighlight: Boolean = false,
    shadowColor: Color = Color.Black,
    highlightColor: Color? = null,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    Box(modifier = modifier) {
        if (drawShadow) {
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer {
                        scaleX = 1.12f
                        scaleY = 1.12f
                    }
            ) {
                val radiusPx = with(density) { cornerRadius.toPx() }
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.00f to shadowColor.copy(alpha = if (pressed) 0.36f else 0.32f),
                            0.24f to shadowColor.copy(alpha = if (pressed) 0.30f else 0.25f),
                            0.48f to shadowColor.copy(alpha = if (pressed) 0.13f else 0.15f),
                            0.74f to shadowColor.copy(alpha = if (pressed) 0.04f else 0.06f),
                            1.00f to Color.Transparent
                        ),
                        center = Offset(size.width / 2f, size.height / 2f),
                        radius = maxOf(size.width, size.height) * 0.72f
                    ),
                    topLeft = Offset.Zero,
                    size = Size(
                        width = size.width,
                        height = size.height
                    ),
                    cornerRadius = CornerRadius(radiusPx * 1.16f, radiusPx * 1.16f)
                )
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colorStops = arrayOf(
                            0.00f to shadowColor.copy(alpha = if (pressed) 0.14f else 0.12f),
                            0.44f to shadowColor.copy(alpha = if (pressed) 0.08f else 0.07f),
                            0.72f to shadowColor.copy(alpha = if (pressed) 0.02f else 0.03f),
                            1.00f to Color.Transparent
                        ),
                        center = Offset(size.width / 2f, size.height / 2f),
                        radius = maxOf(size.width, size.height)
                    ),
                    topLeft = Offset.Zero,
                    size = Size(width = size.width, height = size.height),
                    cornerRadius = CornerRadius(radiusPx * 1.24f, radiusPx * 1.24f)
                )
            }
        }
        content()
        if (drawHighlight) {
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .clip(shape)
            ) {
                val strokeWidth = with(density) { 0.8.dp.toPx() }
                val inset = strokeWidth / 2f
                val radiusPx = with(density) { cornerRadius.toPx() }
                val accent = highlightColor
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = if (pressed) {
                            listOf(
                                Color.Black.copy(alpha = 0.36f),
                                Color.Black.copy(alpha = 0.14f),
                                (accent ?: Color.White).copy(alpha = if (accent == null) 0.12f else 0.20f),
                                (accent ?: Color.White).copy(alpha = if (accent == null) 0.24f else 0.34f)
                            )
                        } else {
                            listOf(
                                (accent ?: Color.White).copy(alpha = if (accent == null) 0.34f else 0.56f),
                                (accent ?: Color.White).copy(alpha = if (accent == null) 0.13f else 0.24f),
                                Color.Black.copy(alpha = 0.10f),
                                Color.Black.copy(alpha = 0.30f)
                            )
                        },
                        start = Offset.Zero,
                        end = Offset(size.width, size.height)
                    ),
                    topLeft = Offset(inset, inset),
                    size = Size(
                        width = (size.width - strokeWidth).coerceAtLeast(0f),
                        height = (size.height - strokeWidth).coerceAtLeast(0f)
                    ),
                    cornerRadius = CornerRadius(radiusPx, radiusPx),
                    style = Stroke(width = strokeWidth)
                )
            }
        }
    }
}

@Composable
private fun ConsoleLayoutPreviewButton(
    modifier: Modifier = Modifier,
    button: DeckButton,
    selected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val color = consoleButtonColor(button)
    val colors = LocalDeckThemeColors.current
    val darkTheme = isSystemInDarkTheme()
    val contentColor = if (darkTheme) Color.White else Color(0xFF243950)
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = modifier
            .consoleButtonDropShadow(
                shape = shape,
                darkTheme = darkTheme,
                pressed = false
            )
            .border(
                if (selected) 2.dp else 0.dp,
                if (selected) Color(0xFF62B7FF) else Color.Transparent,
                shape
            )
    ) {
    Surface(
        modifier = Modifier
            .matchParentSize()
            .clip(shape),
        shape = shape,
        color = color,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp,
        onClick = onClick
    ) {
        ConsoleRaisedButtonFrame(
            modifier = Modifier.fillMaxSize(),
            shape = shape,
            cornerRadius = 20.dp,
            drawShadow = false
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    materialIconFor(button)?.let { icon ->
                        LiftedIcon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(28.dp),
                            shadowSize = 28.dp,
                            lifted = true
                        )
                    }
                    buttonDisplayTitle(button).takeIf { it.isNotBlank() }?.let { title ->
                        LiftedText(
                            text = title,
                            color = contentColor.copy(alpha = if (darkTheme) 1f else 0.86f),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            lifted = false
                        )
                    }
                }
            }
        }
    }
    }
}

@Composable
private fun ConsoleLayoutAddButtonSlot(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val darkTheme = isSystemInDarkTheme()
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = colors.consoleButtonDefault.copy(alpha = if (darkTheme) 0.54f else 0.42f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.add_button),
                tint = colors.textSecondary.copy(alpha = if (darkTheme) 0.66f else 0.58f),
                modifier = Modifier
                    .size(34.dp)
                    .border(1.4.dp, colors.textSecondary.copy(alpha = 0.32f), CircleShape)
                    .padding(5.dp)
            )
        }
    }
}

@Composable
private fun ConsoleVerticalSplitHandle(
    modifier: Modifier = Modifier,
    onDelta: (Float) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val darkTheme = isSystemInDarkTheme()
    var dragging by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { dragging = true },
                    onDragCancel = { dragging = false },
                    onDragEnd = { dragging = false },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDelta(dragAmount.x)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomEnd = 14.dp, bottomStart = 14.dp))
                .background(
                    when {
                        dragging -> colors.consoleButtonFeatured.copy(alpha = 0.86f)
                        darkTheme -> colors.consoleButtonDefault.copy(alpha = 0.70f)
                        else -> Color.White.copy(alpha = 0.74f)
                    }
                )
                .border(
                    1.dp,
                    if (dragging) Color(0xFF76DFFF) else if (darkTheme) Color.White.copy(alpha = 0.16f) else Color(0xFF0876B8),
                    RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomEnd = 14.dp, bottomStart = 14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "↔",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (dragging || darkTheme) Color.White else Color(0xFF0A3147)
            )
        }
    }
}

@Composable
private fun ConsoleRowHeightHandle(
    modifier: Modifier = Modifier,
    onDelta: (Float) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val darkTheme = isSystemInDarkTheme()
    var dragging by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { dragging = true },
                    onDragCancel = { dragging = false },
                    onDragEnd = { dragging = false },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        onDelta(dragAmount.y)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 5.dp, topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 5.dp))
                .background(
                    when {
                        dragging -> colors.consoleButtonFeatured.copy(alpha = 0.86f)
                        darkTheme -> colors.consoleButtonDefault.copy(alpha = 0.70f)
                        else -> Color.White.copy(alpha = 0.74f)
                    }
                )
                .border(
                    1.dp,
                    if (dragging) Color(0xFF76DFFF) else if (darkTheme) Color.White.copy(alpha = 0.16f) else Color(0xFF0876B8),
                    RoundedCornerShape(topStart = 5.dp, topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 5.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "↕",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (dragging || darkTheme) Color.White else Color(0xFF0A3147)
            )
        }
    }
}

private fun adjustConsoleRowBoundary(
    weights: List<Float>,
    boundaryIndex: Int,
    deltaWeight: Float
): List<Float> {
    if (boundaryIndex !in 0 until weights.lastIndex) return weights
    val next = weights.toMutableList()
    val upper = next[boundaryIndex]
    val lower = next[boundaryIndex + 1]
    val applied = deltaWeight.coerceIn(CONSOLE_MIN_ROW_WEIGHT - upper, lower - CONSOLE_MIN_ROW_WEIGHT)
    next[boundaryIndex] = upper + applied
    next[boundaryIndex + 1] = lower - applied
    return next
}

private fun insertedConsoleRowWeights(
    layout: ConsoleLayoutConfig,
    rowCount: Int,
    insertIndex: Int
): List<Float> {
    val next = normalizedConsoleRowWeights(layout, rowCount).toMutableList()
    next.add(insertIndex.coerceIn(0, next.size), 1f)
    return normalizedConsoleWeightValues(next, rowCount + 1)
}

private fun removedConsoleRowWeights(
    layout: ConsoleLayoutConfig,
    rowCount: Int,
    removeIndex: Int
): List<Float> {
    val next = normalizedConsoleRowWeights(layout, rowCount).toMutableList()
    if (removeIndex in next.indices) {
        next.removeAt(removeIndex)
    }
    return normalizedConsoleWeightValues(next, (rowCount - 1).coerceAtLeast(0))
}

private fun normalizedConsoleWeightValues(
    weights: List<Float>,
    rowCount: Int
): List<Float> {
    val safeCount = rowCount.coerceAtLeast(0)
    if (safeCount == 0) return emptyList()
    val padded = weights
        .take(safeCount)
        .map { it.coerceAtLeast(CONSOLE_MIN_ROW_WEIGHT) } +
        List((safeCount - weights.size).coerceAtLeast(0)) { 1f }
    val total = padded.sum().takeIf { it > 0f } ?: safeCount.toFloat()
    return padded.map { it / total * safeCount.toFloat() }
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
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = consoleButtonColor(button),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        ConsoleRaisedButtonFrame(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(18.dp),
            cornerRadius = 18.dp,
            drawShadow = false
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                materialIconFor(button)?.let { icon ->
                    LiftedIcon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
                        shadowSize = 24.dp,
                        lifted = false
                    )
                }
                LiftedText(
                    text = button.title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lifted = false
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
    val availableButtons = buttons
    AlertDialog(
        modifier = Modifier.fillMaxWidth(0.9f),
        onDismissRequest = onDismiss,
        containerColor = colors.cardBackground,
        titleContentColor = colors.textPrimary,
        textContentColor = colors.textPrimary,
        shape = RoundedCornerShape(18.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.add_button),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.console_button_picker_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary
                    )
                }
                ConsoleLayoutEditorIconButton(
                    icon = Icons.Filled.Close,
                    label = stringResource(R.string.cancel),
                    onClick = onDismiss
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (availableButtons.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = colors.consolePreviewBackground,
                            tonalElevation = 0.dp,
                            shadowElevation = 0.dp
                        ) {
                            Text(
                                modifier = Modifier.padding(18.dp),
                                text = stringResource(R.string.console_button_picker_empty),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.textSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(availableButtons) { button ->
                        ConsoleButtonPickerItem(
                            button = button,
                            assigned = button.id in assignedIds,
                            onClick = { onSelect(button) }
                        )
                    }
                }
            }
        },
        confirmButton = {}
    )
}

@Composable
private fun ConsoleButtonPickerItem(
    button: DeckButton,
    assigned: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val shape = RoundedCornerShape(16.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .consoleButtonDropShadow(
                shape = shape,
                darkTheme = isSystemInDarkTheme(),
                pressed = pressed
            ),
        shape = shape,
        color = colors.consolePreviewBackground,
        contentColor = colors.textPrimary,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        interactionSource = interactionSource,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .border(0.7.dp, colors.cardBorder.copy(alpha = 0.58f), shape)
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(consoleButtonColor(button)),
                contentAlignment = Alignment.Center
            ) {
                materialIconFor(button)?.let { icon ->
                    LiftedIcon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp),
                        shadowSize = 26.dp,
                        lifted = false
                    )
                } ?: Text(
                    text = button.icon.ifBlank { button.title.take(1).uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = buttonDisplayTitle(button),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (assigned) {
                        stringResource(R.string.console_button_picker_assigned)
                    } else {
                        button.subtitle.ifBlank { button.payload.ifBlank { stringResource(button.actionType.labelRes) } }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = if (assigned) Color(0xFF76DFFF) else colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = colors.textSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
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
            val eventPass = PointerEventPass.Final

            fun resetTracking() {
                tracking = false
                previousCentroid = null
                totalDrag = Offset.Zero
                maxPointerCount = 0
                multiTouchActive = false
            }

            while (true) {
                val event = awaitPointerEvent(eventPass)
                if (event.changes.any { it.isConsumed }) {
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
    classicSolidButtonBackground: Boolean,
    previewMode: Boolean,
    showTitle: Boolean,
    onButtonPressed: (DeckButton) -> Unit,
    onTrimStep: (DeckButton, Int) -> Unit,
    onButtonTouchStarted: () -> Unit,
    onButtonTouchEnded: () -> Unit,
    onButtonEdit: (DeckButton) -> Unit,
    onButtonMoved: (DeckButton, Int) -> Unit,
    onButtonDeleted: (DeckButton) -> Unit,
    onEmptySlotPressed: (Int) -> Unit
) {
    val safeColumns = columns.coerceAtLeast(1)
    val safeRows = rows.coerceAtLeast(1)
    val slotCount = safeColumns * safeRows
    val buttonSlots = buttons.associateBy { buttonToSlot(it, showTitle) }
    val occupiedSlots = buttons.flatMap { occupiedSlotsForButton(it, safeColumns, safeRows, showTitle) }.toSet()
    val gridWidth = cellSize * safeColumns.toFloat() + spacing * (safeColumns - 1).coerceAtLeast(0).toFloat()
    val gridHeight = cellSize * safeRows.toFloat() + spacing * (safeRows - 1).coerceAtLeast(0).toFloat()
    val density = LocalDensity.current
    var swapHoverCandidate by remember { mutableStateOf<DragSwapCandidate?>(null) }
    var activeSwapPreview by remember { mutableStateOf<DragSwapCandidate?>(null) }
    var draggedButtonId by remember { mutableStateOf<Int?>(null) }
    var trashArmed by remember { mutableStateOf(false) }
    val trashWidth = 138.dp
    val trashHeight = 58.dp

    LaunchedEffect(swapHoverCandidate) {
        activeSwapPreview = null
        val candidate = swapHoverCandidate ?: return@LaunchedEffect
        delay(260)
        activeSwapPreview = candidate
    }

    fun isOverTrash(button: DeckButton, dragOffset: Offset): Boolean {
        val slot = buttonToSlot(button, showTitle)
        val columnIndex = slot.floorMod(safeColumns)
        val rowIndex = slot / safeColumns
        val spanColumns = button.effectiveSpanColumns(safeColumns, showTitle)
        val spanRows = button.effectiveSpanRows(safeColumns, safeRows, showTitle)
        return with(density) {
            val stepPx = (cellSize + spacing).toPx()
            val buttonWidthPx = (cellSize * spanColumns.toFloat() + spacing * (spanColumns - 1).coerceAtLeast(0).toFloat()).toPx()
            val buttonHeightPx = (cellSize * spanRows.toFloat() + spacing * (spanRows - 1).coerceAtLeast(0).toFloat()).toPx()
            val center = Offset(
                x = stepPx * columnIndex + buttonWidthPx / 2f + dragOffset.x,
                y = stepPx * rowIndex + buttonHeightPx / 2f + dragOffset.y
            )
            val trashWidthPx = trashWidth.toPx()
            val trashHeightPx = trashHeight.toPx()
            val trashLeft = gridWidth.toPx() / 2f - trashWidthPx / 2f
            val trashRight = trashLeft + trashWidthPx
            val trashTop = -trashHeightPx - 16.dp.toPx()
            val trashBottom = 10.dp.toPx()
            center.x in trashLeft..trashRight && center.y in trashTop..trashBottom
        }
    }

    fun candidateForDrag(button: DeckButton, targetPosition: Int?): DragSwapCandidate? {
        if (targetPosition == null || targetPosition == button.position) return null
        val targetButton = buttonAtPosition(
            buttons = buttons.filterNot { it.id == button.id },
            targetPosition = targetPosition,
            columns = safeColumns,
            rows = safeRows,
            showTitle = showTitle
        ) ?: return null
        if (!sameButtonSize(button, targetButton, safeColumns, safeRows, showTitle)) return null
        return DragSwapCandidate(
            draggedButtonId = button.id,
            targetButtonId = targetButton.id,
            sourcePosition = button.position,
            targetPosition = targetButton.position
        )
    }

    fun previewOffsetFor(button: DeckButton): Offset {
        val preview = activeSwapPreview?.takeIf { it.targetButtonId == button.id } ?: return Offset.Zero
        val sourceSlot = if (showTitle) preview.sourcePosition + 1 else preview.sourcePosition
        val targetSlot = if (showTitle) preview.targetPosition + 1 else preview.targetPosition
        val columnDelta = sourceSlot.floorMod(safeColumns) - targetSlot.floorMod(safeColumns)
        val rowDelta = sourceSlot / safeColumns - targetSlot / safeColumns
        return with(density) {
            Offset(
                x = (cellSize + spacing).toPx() * columnDelta,
                y = (cellSize + spacing).toPx() * rowDelta
            )
        }
    }

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
                        classicSolidButtonBackground = classicSolidButtonBackground,
                        enabled = true,
                        previewMode = previewMode,
                        columns = safeColumns,
                        slot = buttonPosition,
                        cellSize = cellSize,
                        spacing = spacing,
                        swapPreviewOffset = previewOffsetFor(button),
                        onPressed = { onButtonPressed(button) },
                        onTrimStep = { step -> onTrimStep(button, step) },
                        onPressFeedback = onButtonTouchStarted,
                        onReleaseFeedback = onButtonTouchEnded,
                        onEdit = { onButtonEdit(button) },
                        onDragStarted = {
                            draggedButtonId = button.id
                            trashArmed = false
                        },
                        onDragHover = { targetPosition ->
                            swapHoverCandidate = candidateForDrag(button, targetPosition)
                        },
                        onDragOffsetChanged = { offset ->
                            val overTrash = isOverTrash(button, offset)
                            trashArmed = overTrash
                            if (overTrash) {
                                swapHoverCandidate = null
                                activeSwapPreview = null
                            }
                        },
                        onDropDelete = { offset ->
                            if (isOverTrash(button, offset)) {
                                onButtonDeleted(button)
                                true
                            } else {
                                false
                            }
                        },
                        onDragFinished = {
                            swapHoverCandidate = null
                            activeSwapPreview = null
                            draggedButtonId = null
                            trashArmed = false
                        },
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
            AnimatedVisibility(
                visible = draggedButtonId != null,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-76).dp)
                    .zIndex(4f)
            ) {
                TrashDropTarget(active = trashArmed)
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
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            val compact = maxHeight < 58.dp || maxWidth < 74.dp
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(R.string.deck_title),
                    style = if (compact) MaterialTheme.typography.labelLarge else MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = if (compact) 1 else 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier
                        .padding(top = if (compact) 3.dp else 6.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(if (compact) 7.dp else 8.dp)
                            .clip(CircleShape)
                            .background(statusDotColor(status.state))
                    )
                    Text(
                        text = stringResource(status.state.labelRes()),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
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

private fun Modifier.trimSwitchGesture(
    enabled: Boolean,
    style: DeckControlStyle,
    joyPadEightWay: Boolean,
    onPress: () -> Unit,
    onRelease: () -> Unit,
    onPreviewStep: (Int) -> Unit,
    onActiveStepChange: (Int) -> Unit,
    onStep: (Int) -> Unit
): Modifier {
    if (!enabled) return this
    return pointerInput(style, joyPadEightWay, onStep, onPreviewStep, onActiveStepChange) {
        fun levelForMagnitude(magnitude: Float): Int {
            return when {
                magnitude >= 0.72f -> 3
                magnitude >= 0.38f -> 2
                magnitude > 0f -> 1
                else -> 0
            }
        }

        fun normalizedDegrees(degrees: Float): Float {
            var normalized = degrees
            while (normalized > 180f) normalized -= 360f
            while (normalized < -180f) normalized += 360f
            return normalized
        }

        fun stepFromSignedMagnitude(signedMagnitude: Float): Int {
            val deadZone = 0.16f
            val magnitude = abs(signedMagnitude)
            if (magnitude <= deadZone) return 0
            val normalizedMagnitude = ((magnitude - deadZone) / (1f - deadZone)).coerceIn(0f, 1f)
            val direction = if (signedMagnitude > 0f) 1 else -1
            return direction * levelForMagnitude(normalizedMagnitude)
        }

        fun sliderStepFor(position: Offset, previousStep: Int): Int {
            val horizontal = size.width >= size.height * 1.15f
            val start = if (horizontal) size.width * 0.12f else size.height * 0.12f
            val end = if (horizontal) size.width * 0.88f else size.height * 0.88f
            val center = if (horizontal) size.width / 2f else size.height / 2f
            val axisPosition = if (horizontal) position.x else position.y
            val clampedPosition = axisPosition.coerceIn(start, end)
            val halfRange = ((end - start) / 2f).coerceAtLeast(1f)
            val signedMagnitude = if (horizontal) {
                ((clampedPosition - center) / halfRange).coerceIn(-1f, 1f)
            } else {
                ((center - clampedPosition) / halfRange).coerceIn(-1f, 1f)
            }
            val step = stepFromSignedMagnitude(signedMagnitude)
            val outsideTrack = if (horizontal) {
                position.x !in start..end || position.y !in 0f..size.height.toFloat()
            } else {
                position.x !in 0f..size.width.toFloat() || position.y !in start..end
            }
            return if (step == 0 && outsideTrack) {
                previousStep
            } else {
                step
            }
        }

        fun knobStepFor(position: Offset, previousStep: Int): Int {
            val center = Offset(size.width / 2f, size.height / 2f)
            val fromCenter = position - center
            if (abs(fromCenter.x) < size.width * 0.08f && abs(fromCenter.y) < size.height * 0.08f) {
                return 0
            }
            val angleDegrees = atan2(fromCenter.y, fromCenter.x) * 180f / Math.PI.toFloat()
            val rawDeltaFromTop = normalizedDegrees(angleDegrees + 90f)
            val deltaFromTop = if (abs(rawDeltaFromTop) > 126f && previousStep != 0) {
                if (previousStep > 0) 126f else -126f
            } else {
                rawDeltaFromTop.coerceIn(-126f, 126f)
            }
            val signedMagnitude = deltaFromTop / 126f
            val step = stepFromSignedMagnitude(signedMagnitude)
            return if (step == 0 && (
                    position.x !in 0f..size.width.toFloat() ||
                        position.y !in 0f..size.height.toFloat()
                )
            ) {
                previousStep
            } else {
                step
            }
        }

        fun joyPadStepFor(position: Offset, previousStep: Int): Int {
            val center = Offset(size.width / 2f, size.height / 2f)
            val fromCenter = position - center
            val distance = maxOf(abs(fromCenter.x), abs(fromCenter.y))
            val deadZone = minOf(size.width, size.height) * 0.14f
            if (distance <= deadZone) {
                return if (
                    previousStep != 0 &&
                    (position.x !in 0f..size.width.toFloat() || position.y !in 0f..size.height.toFloat())
                ) {
                    previousStep
                } else {
                    0
                }
            }
            if (!joyPadEightWay) {
                return if (abs(fromCenter.x) >= abs(fromCenter.y)) {
                    if (fromCenter.x > 0f) JOYPAD_STEP_RIGHT else JOYPAD_STEP_LEFT
                } else {
                    if (fromCenter.y < 0f) JOYPAD_STEP_UP else JOYPAD_STEP_DOWN
                }
            }
            val angle = (atan2(fromCenter.y, fromCenter.x) * 180f / Math.PI.toFloat() + 360f) % 360f
            return when (((angle + 22.5f) / 45f).toInt() % 8) {
                0 -> JOYPAD_STEP_RIGHT
                1 -> JOYPAD_STEP_DOWN_RIGHT
                2 -> JOYPAD_STEP_DOWN
                3 -> JOYPAD_STEP_DOWN_LEFT
                4 -> JOYPAD_STEP_LEFT
                5 -> JOYPAD_STEP_UP_LEFT
                6 -> JOYPAD_STEP_UP
                else -> JOYPAD_STEP_UP_RIGHT
            }
        }

        fun infiniteWheelAngleFor(position: Offset): Float? {
            val center = Offset(size.width / 2f, size.height / 2f)
            val fromCenter = position - center
            val minimumRadius = minOf(size.width, size.height) * 0.12f
            if (fromCenter.getDistance() < minimumRadius) return null
            return atan2(fromCenter.y, fromCenter.x) * 180f / Math.PI.toFloat()
        }

        fun stepFor(totalDrag: Offset, downPosition: Offset, currentPosition: Offset, dragDelta: Offset, previousStep: Int): Int {
            return if (style == DeckControlStyle.TrimSlider) {
                sliderStepFor(currentPosition, previousStep)
            } else if (style == DeckControlStyle.JoyPad) {
                joyPadStepFor(currentPosition, previousStep)
            } else {
                knobStepFor(currentPosition, previousStep)
            }
        }

        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            down.consume()
            var totalDrag = Offset.Zero
            var currentPosition = down.position
            var lastStep = if (style == DeckControlStyle.InfiniteWheel) {
                0
            } else {
                stepFor(totalDrag, down.position, currentPosition, Offset.Zero, 0)
            }
            var joyPadActiveStep = 0
            var wheelPreviousAngle = infiniteWheelAngleFor(down.position)
            var wheelAngleAccumulator = 0f
            var wheelVisualTick = 0
            val wheelDegreesPerNotch = 360f / INFINITE_WHEEL_NOTCHES_PER_REVOLUTION.toFloat()
            fun setJoyPadStep(nextStep: Int) {
                if (style != DeckControlStyle.JoyPad || nextStep == joyPadActiveStep) return
                if (joyPadActiveStep != 0) {
                    onStep(joyPadReleaseStep(joyPadActiveStep))
                }
                joyPadActiveStep = nextStep
                if (joyPadActiveStep != 0) {
                    onStep(joyPadActiveStep)
                }
            }
            try {
                onPress()
                onPreviewStep(lastStep)
                if (style == DeckControlStyle.JoyPad) {
                    setJoyPadStep(lastStep)
                } else {
                    onActiveStepChange(lastStep)
                }
                while (true) {
                    val event = awaitPointerEvent(PointerEventPass.Initial)
                    val change = event.changes.firstOrNull { it.id == down.id }
                    if (change != null) {
                        currentPosition = change.position
                        val dragDelta = currentPosition - change.previousPosition
                        totalDrag += dragDelta
                        change.consume()
                        if (style == DeckControlStyle.InfiniteWheel) {
                            val nextAngle = infiniteWheelAngleFor(currentPosition)
                            if (wheelPreviousAngle != null && nextAngle != null) {
                                wheelAngleAccumulator += normalizedDegrees(nextAngle - wheelPreviousAngle!!)
                                while (wheelAngleAccumulator >= wheelDegreesPerNotch) {
                                    wheelVisualTick += 1
                                    onPreviewStep(wheelVisualTick)
                                    onStep(1)
                                    wheelAngleAccumulator -= wheelDegreesPerNotch
                                }
                                while (wheelAngleAccumulator <= -wheelDegreesPerNotch) {
                                    wheelVisualTick -= 1
                                    onPreviewStep(wheelVisualTick)
                                    onStep(-1)
                                    wheelAngleAccumulator += wheelDegreesPerNotch
                                }
                            }
                            if (nextAngle != null) {
                                wheelPreviousAngle = nextAngle
                            }
                        } else {
                            lastStep = stepFor(totalDrag, down.position, currentPosition, dragDelta, lastStep)
                            onPreviewStep(lastStep)
                            if (style == DeckControlStyle.JoyPad) {
                                setJoyPadStep(lastStep)
                            } else {
                                onActiveStepChange(lastStep)
                            }
                        }
                    }
                    if (event.changes.all { it.changedToUp() || !it.pressed }) {
                        break
                    }
                }
            } finally {
                if (joyPadActiveStep != 0) {
                    onStep(joyPadReleaseStep(joyPadActiveStep))
                    joyPadActiveStep = 0
                }
                onActiveStepChange(0)
                onRelease()
                if (style != DeckControlStyle.InfiniteWheel) {
                    onPreviewStep(0)
                }
            }
        }
    }
}

@Composable
private fun TrimSwitchContent(
    modifier: Modifier = Modifier,
    button: DeckButton,
    visualMode: DeckUiMode,
    contentColor: Color,
    cellSize: Dp,
    visualStep: Int
) {
    val isConsole = visualMode == DeckUiMode.Console
    val colors = LocalDeckThemeColors.current
    val darkTheme = isSystemInDarkTheme()
    val trimAccent = if (isConsole) Color(0xFF3D8EFF) else button.color
    val inactiveStroke = contentColor.copy(alpha = if (darkTheme) 0.34f else 0.48f)
    val style = button.controlStyle
    val title = buttonDisplayTitle(button).ifBlank { stringResource(style.labelRes) }
    val animatedStep by animateFloatAsState(
        targetValue = visualStep.toFloat(),
        animationSpec = tween(durationMillis = 120),
        label = "trimSwitchStep"
    )
    val activeFraction = (animatedStep / 3f).coerceIn(-1f, 1f)
    Box(
        modifier = modifier.padding(if (isConsole) 8.dp else 7.dp),
        contentAlignment = Alignment.Center
    ) {
        if (style == DeckControlStyle.JoyPad) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val eightWay = joyPadPayloadParts(button.payload).eightWay
                val padSize = minOf(size.width, size.height) * if (isConsole) 0.76f else 0.72f
                val arm = padSize * 0.34f
                val block = padSize * 0.35f
                val center = Offset(size.width / 2f, size.height / 2f)
                val selected = joyPadBaseStep(animatedStep.roundToInt())
                fun isPadBlockActive(step: Int): Boolean {
                    return selected == step ||
                        (step == JOYPAD_STEP_UP && (selected == JOYPAD_STEP_UP_LEFT || selected == JOYPAD_STEP_UP_RIGHT)) ||
                        (step == JOYPAD_STEP_DOWN && (selected == JOYPAD_STEP_DOWN_LEFT || selected == JOYPAD_STEP_DOWN_RIGHT)) ||
                        (step == JOYPAD_STEP_LEFT && (selected == JOYPAD_STEP_UP_LEFT || selected == JOYPAD_STEP_DOWN_LEFT)) ||
                        (step == JOYPAD_STEP_RIGHT && (selected == JOYPAD_STEP_UP_RIGHT || selected == JOYPAD_STEP_DOWN_RIGHT))
                }
                fun blockColor(step: Int): Color {
                    return if (isPadBlockActive(step)) {
                        trimAccent.copy(alpha = 0.92f)
                    } else {
                        colors.consoleButtonDefault.copy(alpha = if (isConsole) 0.78f else 0.48f)
                    }
                }
                fun drawPadBlock(step: Int, topLeft: Offset, blockSize: Size) {
                    val active = isPadBlockActive(step)
                    val corner = block * if (isConsole) 0.18f else 0.22f
                    drawRoundRect(
                        color = Color.Black.copy(alpha = if (darkTheme) 0.36f else 0.16f),
                        topLeft = topLeft + Offset(0f, block * 0.08f),
                        size = blockSize,
                        cornerRadius = CornerRadius(corner, corner)
                    )
                    drawRoundRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                contentColor.copy(alpha = if (active) 0.22f else 0.16f),
                                blockColor(step),
                                Color.Black.copy(alpha = if (darkTheme) 0.34f else 0.12f)
                            ),
                            center = Offset(topLeft.x + blockSize.width * 0.34f, topLeft.y + blockSize.height * 0.28f),
                            radius = maxOf(blockSize.width, blockSize.height)
                        ),
                        topLeft = topLeft,
                        size = blockSize,
                        cornerRadius = CornerRadius(corner, corner)
                    )
                    drawRoundRect(
                        color = if (active) trimAccent.copy(alpha = 0.88f) else inactiveStroke.copy(alpha = 0.42f),
                        topLeft = topLeft,
                        size = blockSize,
                        cornerRadius = CornerRadius(corner, corner),
                        style = Stroke(width = if (active) 1.5.dp.toPx() else 1.dp.toPx())
                    )
                    val arrowColor = if (active) Color.White.copy(alpha = 0.94f) else contentColor.copy(alpha = 0.84f)
                    val arrowSize = minOf(blockSize.width, blockSize.height) * 0.20f
                    val arrowCenter = Offset(topLeft.x + blockSize.width / 2f, topLeft.y + blockSize.height / 2f)
                    val path = Path()
                    when (joyPadBaseStep(step)) {
                        JOYPAD_STEP_UP -> {
                            path.moveTo(arrowCenter.x, arrowCenter.y - arrowSize)
                            path.lineTo(arrowCenter.x - arrowSize, arrowCenter.y + arrowSize * 0.70f)
                            path.lineTo(arrowCenter.x + arrowSize, arrowCenter.y + arrowSize * 0.70f)
                        }
                        JOYPAD_STEP_DOWN -> {
                            path.moveTo(arrowCenter.x, arrowCenter.y + arrowSize)
                            path.lineTo(arrowCenter.x - arrowSize, arrowCenter.y - arrowSize * 0.70f)
                            path.lineTo(arrowCenter.x + arrowSize, arrowCenter.y - arrowSize * 0.70f)
                        }
                        JOYPAD_STEP_LEFT -> {
                            path.moveTo(arrowCenter.x - arrowSize, arrowCenter.y)
                            path.lineTo(arrowCenter.x + arrowSize * 0.70f, arrowCenter.y - arrowSize)
                            path.lineTo(arrowCenter.x + arrowSize * 0.70f, arrowCenter.y + arrowSize)
                        }
                        else -> {
                            path.moveTo(arrowCenter.x + arrowSize, arrowCenter.y)
                            path.lineTo(arrowCenter.x - arrowSize * 0.70f, arrowCenter.y - arrowSize)
                            path.lineTo(arrowCenter.x - arrowSize * 0.70f, arrowCenter.y + arrowSize)
                        }
                    }
                    path.close()
                    drawPath(path = path, color = arrowColor)
                }
                if (eightWay) {
                    val diagonalSize = padSize * 0.23f
                    fun drawDiagonal(step: Int, xSign: Float, ySign: Float) {
                        drawRoundRect(
                            color = blockColor(step).copy(alpha = if (selected == step) 0.76f else 0.36f),
                            topLeft = Offset(
                                center.x + xSign * padSize * 0.31f - diagonalSize / 2f,
                                center.y + ySign * padSize * 0.31f - diagonalSize / 2f
                            ),
                            size = Size(diagonalSize, diagonalSize),
                            cornerRadius = CornerRadius(diagonalSize * 0.32f, diagonalSize * 0.32f)
                        )
                    }
                    drawDiagonal(JOYPAD_STEP_UP_LEFT, -1f, -1f)
                    drawDiagonal(JOYPAD_STEP_UP_RIGHT, 1f, -1f)
                    drawDiagonal(JOYPAD_STEP_DOWN_LEFT, -1f, 1f)
                    drawDiagonal(JOYPAD_STEP_DOWN_RIGHT, 1f, 1f)
                }
                drawPadBlock(
                    JOYPAD_STEP_UP,
                    Offset(center.x - arm / 2f, center.y - padSize / 2f),
                    Size(arm, block)
                )
                drawPadBlock(
                    JOYPAD_STEP_DOWN,
                    Offset(center.x - arm / 2f, center.y + padSize / 2f - block),
                    Size(arm, block)
                )
                drawPadBlock(
                    JOYPAD_STEP_LEFT,
                    Offset(center.x - padSize / 2f, center.y - arm / 2f),
                    Size(block, arm)
                )
                drawPadBlock(
                    JOYPAD_STEP_RIGHT,
                    Offset(center.x + padSize / 2f - block, center.y - arm / 2f),
                    Size(block, arm)
                )
                drawCircle(
                    color = Color.Black.copy(alpha = if (darkTheme) 0.42f else 0.18f),
                    radius = arm * 0.28f,
                    center = center + Offset(0f, arm * 0.04f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(contentColor.copy(alpha = 0.36f), Color.Black.copy(alpha = 0.28f)),
                        center = Offset(center.x - arm * 0.10f, center.y - arm * 0.12f),
                        radius = arm * 0.42f
                    ),
                    radius = arm * 0.24f,
                    center = center
                )
            }
            Text(
                text = title,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.80f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
        } else if (style == DeckControlStyle.InfiniteWheel) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val wheelRadius = minOf(size.width, size.height) * if (isConsole) 0.38f else 0.34f
                val center = Offset(size.width / 2f, size.height / 2f)
                val rotationOffset = animatedStep * (360f / INFINITE_WHEEL_NOTCHES_PER_REVOLUTION.toFloat())
                drawCircle(
                    color = Color.Black.copy(alpha = if (darkTheme) 0.34f else 0.12f),
                    radius = wheelRadius * 1.02f,
                    center = center + Offset(0f, wheelRadius * 0.08f)
                )
                repeat(INFINITE_WHEEL_NOTCHES_PER_REVOLUTION) { index ->
                    val angle = ((index * (360f / INFINITE_WHEEL_NOTCHES_PER_REVOLUTION)) + rotationOffset) *
                        (Math.PI.toFloat() / 180f)
                    val major = index % 3 == 0
                    drawLine(
                        color = if (major) trimAccent.copy(alpha = if (isConsole) 0.70f else 0.58f) else inactiveStroke.copy(alpha = 0.58f),
                        start = Offset(
                            x = center.x + cos(angle) * wheelRadius * if (major) 1.16f else 1.12f,
                            y = center.y + sin(angle) * wheelRadius * if (major) 1.16f else 1.12f
                        ),
                        end = Offset(
                            x = center.x + cos(angle) * wheelRadius * if (major) 1.30f else 1.24f,
                            y = center.y + sin(angle) * wheelRadius * if (major) 1.30f else 1.24f
                        ),
                        strokeWidth = if (major) 1.6.dp.toPx() else 1.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            contentColor.copy(alpha = if (darkTheme) 0.22f else 0.38f),
                            colors.consoleButtonDefault.copy(alpha = if (isConsole) 0.70f else 0.42f),
                            Color.Black.copy(alpha = if (darkTheme) 0.42f else 0.16f)
                        ),
                        center = Offset(center.x - wheelRadius * 0.35f, center.y - wheelRadius * 0.45f),
                        radius = wheelRadius * 1.28f
                    ),
                    radius = wheelRadius,
                    center = center
                )
                drawCircle(
                    color = Color.Black.copy(alpha = if (darkTheme) 0.58f else 0.22f),
                    radius = wheelRadius,
                    center = center,
                    style = Stroke(width = 1.4.dp.toPx())
                )
                drawCircle(
                    color = trimAccent.copy(alpha = if (isConsole) 0.90f else 0.78f),
                    radius = wheelRadius * 1.03f,
                    center = center,
                    style = Stroke(width = if (isConsole) 2.2.dp.toPx() else 1.8.dp.toPx())
                )
                val markerAngle = (-90f + rotationOffset) * (Math.PI.toFloat() / 180f)
                val markerCenter = Offset(
                    center.x + cos(markerAngle) * wheelRadius * 0.82f,
                    center.y + sin(markerAngle) * wheelRadius * 0.82f
                )
                drawCircle(
                    color = contentColor.copy(alpha = 0.82f),
                    radius = wheelRadius * 0.08f,
                    center = markerCenter
                )
                val arrowY = center.y
                val arrowAlpha = if (isConsole) 0.70f else 0.58f
                drawLine(
                    color = trimAccent.copy(alpha = arrowAlpha),
                    start = Offset(center.x - wheelRadius * 1.55f, arrowY - wheelRadius * 0.16f),
                    end = Offset(center.x - wheelRadius * 1.70f, arrowY),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = trimAccent.copy(alpha = arrowAlpha),
                    start = Offset(center.x - wheelRadius * 1.55f, arrowY + wheelRadius * 0.16f),
                    end = Offset(center.x - wheelRadius * 1.70f, arrowY),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = trimAccent.copy(alpha = arrowAlpha),
                    start = Offset(center.x + wheelRadius * 1.55f, arrowY - wheelRadius * 0.16f),
                    end = Offset(center.x + wheelRadius * 1.70f, arrowY),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = trimAccent.copy(alpha = arrowAlpha),
                    start = Offset(center.x + wheelRadius * 1.55f, arrowY + wheelRadius * 0.16f),
                    end = Offset(center.x + wheelRadius * 1.70f, arrowY),
                    strokeWidth = 1.5.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            Text(
                text = title,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.80f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
        } else if (style == DeckControlStyle.TrimSlider) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val horizontal = size.width >= size.height * 1.15f
                val trackLength = if (horizontal) size.width * 0.78f else size.height * 0.74f
                val trackThickness = if (horizontal) {
                    minOf(size.height * 0.16f, size.width * 0.10f).coerceAtLeast(7.dp.toPx())
                } else {
                    minOf(size.width * 0.18f, size.height * 0.10f).coerceAtLeast(7.dp.toPx())
                }
                val trackLeft = if (horizontal) center.x - trackLength / 2f else center.x - trackThickness / 2f
                val trackTop = if (horizontal) center.y - trackThickness / 2f else center.y - trackLength / 2f
                val trackSize = if (horizontal) Size(trackLength, trackThickness) else Size(trackThickness, trackLength)
                val trackCorner = trackThickness / 2f
                val thumbRadius = minOf(size.width, size.height) * if (isConsole) 0.20f else 0.18f
                val travel = (trackLength / 2f - thumbRadius * 0.82f).coerceAtLeast(1f)
                val thumbCenter = if (horizontal) {
                    Offset(center.x + activeFraction * travel, center.y)
                } else {
                    Offset(center.x, center.y - activeFraction * travel)
                }
                repeat(11) { index ->
                    val tickPosition = if (horizontal) trackLeft + trackLength * (index / 10f) else trackTop + trackLength * (index / 10f)
                    val major = index == 5
                    if (horizontal) {
                        val tickTop = center.y - thumbRadius * 1.46f
                        drawLine(
                            color = if (major) contentColor.copy(alpha = 0.70f) else inactiveStroke,
                            start = Offset(tickPosition, tickTop),
                            end = Offset(tickPosition, tickTop + if (major) thumbRadius * 0.34f else thumbRadius * 0.24f),
                            strokeWidth = if (major) 1.7.dp.toPx() else 1.2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    } else {
                        val tickLeft = center.x + thumbRadius * 1.05f
                        drawLine(
                            color = if (major) contentColor.copy(alpha = 0.70f) else inactiveStroke,
                            start = Offset(tickLeft, tickPosition),
                            end = Offset(tickLeft + if (major) thumbRadius * 0.34f else thumbRadius * 0.24f, tickPosition),
                            strokeWidth = if (major) 1.7.dp.toPx() else 1.2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
                drawRoundRect(
                    color = Color.Black.copy(alpha = if (darkTheme) 0.48f else 0.18f),
                    topLeft = if (horizontal) Offset(trackLeft, trackTop + trackThickness * 0.12f) else Offset(trackLeft + trackThickness * 0.12f, trackTop),
                    size = if (horizontal) trackSize else Size(trackThickness, trackLength),
                    cornerRadius = CornerRadius(trackCorner, trackCorner)
                )
                drawRoundRect(
                    brush = if (horizontal) {
                        Brush.horizontalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.16f), colors.consoleButtonDefault.copy(alpha = 0.86f))
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(colors.consoleButtonDefault.copy(alpha = 0.86f), Color.Black.copy(alpha = 0.16f))
                        )
                    },
                    topLeft = Offset(trackLeft, trackTop),
                    size = trackSize,
                    cornerRadius = CornerRadius(trackCorner, trackCorner)
                )
                if (activeFraction != 0f) {
                    if (horizontal) {
                        val activeLeft = minOf(center.x, thumbCenter.x)
                        val activeWidth = abs(thumbCenter.x - center.x).coerceAtLeast(trackThickness * 0.50f)
                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(trimAccent.copy(alpha = 0.58f), trimAccent.copy(alpha = 0.92f))
                            ),
                            topLeft = Offset(activeLeft, trackTop + trackThickness * 0.16f),
                            size = Size(activeWidth, trackThickness * 0.68f),
                            cornerRadius = CornerRadius(trackCorner, trackCorner)
                        )
                    } else {
                        val activeTop = minOf(center.y, thumbCenter.y)
                        val activeHeight = abs(thumbCenter.y - center.y).coerceAtLeast(trackThickness * 0.50f)
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(trimAccent.copy(alpha = 0.92f), trimAccent.copy(alpha = 0.58f))
                            ),
                            topLeft = Offset(trackLeft + trackThickness * 0.16f, activeTop),
                            size = Size(trackThickness * 0.68f, activeHeight),
                            cornerRadius = CornerRadius(trackCorner, trackCorner)
                        )
                    }
                }
                drawCircle(
                    color = Color.Black.copy(alpha = if (darkTheme) 0.42f else 0.16f),
                    radius = thumbRadius * 1.04f,
                    center = thumbCenter + Offset(0f, thumbRadius * 0.10f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = if (isConsole) 0.16f else 0.72f),
                            colors.consoleButtonDefault.copy(alpha = if (isConsole) 0.86f else 0.46f),
                            Color.Black.copy(alpha = if (darkTheme) 0.34f else 0.12f)
                        ),
                        center = Offset(thumbCenter.x - thumbRadius * 0.30f, thumbCenter.y - thumbRadius * 0.38f),
                        radius = thumbRadius * 1.22f
                    ),
                    radius = thumbRadius,
                    center = thumbCenter
                )
                drawCircle(
                    color = trimAccent.copy(alpha = if (isConsole) 0.92f else 0.66f),
                    radius = thumbRadius,
                    center = thumbCenter,
                    style = Stroke(width = if (abs(activeFraction) > 0.01f) 1.8.dp.toPx() else 1.dp.toPx())
                )
                drawLine(
                    color = if (abs(activeFraction) > 0.01f) trimAccent else contentColor.copy(alpha = 0.70f),
                    start = if (horizontal) {
                        Offset(thumbCenter.x, thumbCenter.y - thumbRadius * 0.36f)
                    } else {
                        Offset(thumbCenter.x - thumbRadius * 0.36f, thumbCenter.y)
                    },
                    end = if (horizontal) {
                        Offset(thumbCenter.x, thumbCenter.y + thumbRadius * 0.36f)
                    } else {
                        Offset(thumbCenter.x + thumbRadius * 0.36f, thumbCenter.y)
                    },
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            Text(
                text = title,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                style = if (cellSize < 74.dp) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
                color = contentColor.copy(alpha = 0.92f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
        } else {
            Canvas(modifier = Modifier.matchParentSize()) {
                val knobRadius = minOf(size.width, size.height) * if (isConsole) 0.34f else 0.32f
                val center = Offset(size.width / 2f, size.height / 2f)
                val angleDegrees = -90f + activeFraction * 126f
                val angle = angleDegrees * (Math.PI.toFloat() / 180f)
                val arcTopLeft = Offset(center.x - knobRadius * 1.16f, center.y - knobRadius * 1.16f)
                val arcSize = Size(knobRadius * 2.32f, knobRadius * 2.32f)
                repeat(27) { index ->
                    val tickAngle = (144f + index * (252f / 26f)) * (Math.PI.toFloat() / 180f)
                    val major = index % 6 == 0
                    drawLine(
                        color = inactiveStroke.copy(alpha = if (major) 0.66f else 0.42f),
                        start = Offset(
                            center.x + cos(tickAngle) * knobRadius * 1.23f,
                            center.y + sin(tickAngle) * knobRadius * 1.23f
                        ),
                        end = Offset(
                            center.x + cos(tickAngle) * knobRadius * if (major) 1.38f else 1.32f,
                            center.y + sin(tickAngle) * knobRadius * if (major) 1.38f else 1.32f
                        ),
                        strokeWidth = if (major) 1.3.dp.toPx() else 0.9.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
                drawArc(
                    color = Color.Black.copy(alpha = if (darkTheme) 0.42f else 0.14f),
                    startAngle = 144f,
                    sweepAngle = 252f,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                )
                if (activeFraction != 0f) {
                    drawArc(
                        color = trimAccent.copy(alpha = if (isConsole) 0.86f else 0.80f),
                        startAngle = -90f,
                        sweepAngle = activeFraction * 126f,
                        useCenter = false,
                        topLeft = arcTopLeft,
                        size = arcSize,
                        style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                drawCircle(
                    color = Color.Black.copy(alpha = if (darkTheme) 0.38f else 0.14f),
                    radius = knobRadius * 1.02f,
                    center = center + Offset(0f, knobRadius * 0.08f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            contentColor.copy(alpha = if (darkTheme) 0.20f else 0.36f),
                            colors.consoleButtonDefault.copy(alpha = if (isConsole) 0.72f else 0.42f),
                            Color.Black.copy(alpha = if (darkTheme) 0.42f else 0.14f)
                        ),
                        center = Offset(center.x - knobRadius * 0.30f, center.y - knobRadius * 0.40f),
                        radius = knobRadius * 1.30f
                    ),
                    radius = knobRadius,
                    center = center
                )
                drawCircle(
                    color = Color.Black.copy(alpha = if (darkTheme) 0.58f else 0.22f),
                    radius = knobRadius,
                    center = center,
                    style = Stroke(width = 1.2.dp.toPx())
                )
                val indicatorStart = Offset(
                    center.x + cos(angle) * knobRadius * 0.42f,
                    center.y + sin(angle) * knobRadius * 0.42f
                )
                val indicatorEnd = Offset(
                    center.x + cos(angle) * knobRadius * 0.76f,
                    center.y + sin(angle) * knobRadius * 0.76f
                )
                drawLine(
                    color = if (activeFraction == 0f) contentColor.copy(alpha = 0.76f) else trimAccent.copy(alpha = 0.96f),
                    start = indicatorStart,
                    end = indicatorEnd,
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            Text(
                text = title,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.80f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
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
    classicSolidButtonBackground: Boolean,
    enabled: Boolean,
    previewMode: Boolean,
    columns: Int,
    slot: Int,
    cellSize: Dp,
    spacing: Dp,
    swapPreviewOffset: Offset = Offset.Zero,
    contentScale: Float = 1f,
    onPressed: () -> Unit,
    onTrimStep: (Int) -> Unit = {},
    onPressFeedback: () -> Unit,
    onReleaseFeedback: () -> Unit,
    onEdit: () -> Unit,
    onDragStarted: () -> Unit = {},
    onDragHover: (Int?) -> Unit = {},
    onDragOffsetChanged: (Offset) -> Unit = {},
    onDropDelete: (Offset) -> Boolean = { false },
    onDragFinished: () -> Unit = {},
    onMove: (Int) -> Unit
) {
    val isConsole = visualMode == DeckUiMode.Console
    val themeColors = LocalDeckThemeColors.current
    val darkTheme = isSystemInDarkTheme()
    val containerColor = when {
        !enabled -> MaterialTheme.colorScheme.surfaceVariant
        isConsole -> consoleButtonColor(button)
        !classicSolidButtonBackground -> themeColors.cardBackground.copy(alpha = if (darkTheme) 0.9f else 0.96f)
        else -> button.color
    }
    val contentColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurfaceVariant
        isConsole -> if (darkTheme) Color.White else Color(0xFF243950)
        !isConsole && !classicSolidButtonBackground -> if (button.color.luminance() > 0.62f) {
            themeColors.textPrimary
        } else {
            button.color
        }
        else -> Color.White
    }
    val buttonShape = RoundedCornerShape(if (isConsole) 18.dp else 8.dp)
    val density = LocalDensity.current
    var dragOffset by remember(button.id) { mutableStateOf(Offset.Zero) }
    var dragInProgress by remember(button.id) { mutableStateOf(false) }
    var suppressDragReturnAnimation by remember(button.id) { mutableStateOf(false) }
    var touchPressed by remember(button.id) { mutableStateOf(false) }
    var trimVisualStep by remember(button.id) { mutableStateOf(0) }
    var trimRepeatStep by remember(button.id) { mutableStateOf(0) }
    LaunchedEffect(button.controlStyle, trimVisualStep, trimRepeatStep) {
        if (button.controlStyle != DeckControlStyle.InfiniteWheel && trimVisualStep != 0 && trimRepeatStep == 0) {
            delay(180)
            trimVisualStep = 0
        }
    }
    LaunchedEffect(button.id, button.controlStyle, trimRepeatStep) {
        if (!button.controlStyle.usesTrimRepeatFrequency()) return@LaunchedEffect
        var firstSend = true
        while (trimRepeatStep != 0) {
            onTrimStep(trimRepeatStep)
            delay(trimRepeatDelayMillis(trimRepeatStep, firstSend))
            firstSend = false
        }
    }
    val moveThresholdPx = with(density) { ((cellSize + spacing) * 0.55f).toPx() }
    val dragActive = dragInProgress && (abs(dragOffset.x) > moveThresholdPx || abs(dragOffset.y) > moveThresholdPx)
    val displayOffset = dragOffset + swapPreviewOffset
    val spanColumns = button.effectiveSpanColumns(columns.coerceAtLeast(1), false)
    val visualWidth = cellSize * spanColumns.toFloat() + spacing * (spanColumns - 1).coerceAtLeast(0).toFloat()
    val visualHeight = cellSize * button.spanRows.coerceAtLeast(1).toFloat() + spacing * (button.spanRows - 1).coerceAtLeast(0).toFloat()
    val visualShape = remember(visualWidth, visualHeight) { buttonVisualShape(visualWidth, visualHeight) }
    val animatedX by animateFloatAsState(
        targetValue = displayOffset.x,
        animationSpec = tween(
            durationMillis = when {
                suppressDragReturnAnimation -> 0
                previewMode && displayOffset == Offset.Zero -> 0
                displayOffset == Offset.Zero -> 140
                else -> 80
            }
        ),
        label = "keyDragX"
    )
    val animatedY by animateFloatAsState(
        targetValue = displayOffset.y,
        animationSpec = tween(
            durationMillis = when {
                suppressDragReturnAnimation -> 0
                previewMode && displayOffset == Offset.Zero -> 0
                displayOffset == Offset.Zero -> 140
                else -> 80
            }
        ),
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
    LaunchedEffect(suppressDragReturnAnimation, dragInProgress, displayOffset) {
        if (suppressDragReturnAnimation && !dragInProgress && displayOffset == Offset.Zero) {
            suppressDragReturnAnimation = false
        }
    }
    val dragModifier = if (previewMode) {
        Modifier.pointerInput(button.id, columns, slot, cellSize, spacing) {
            fun targetPositionFor(offset: Offset): Int? {
                val stepPx = with(density) { (cellSize + spacing).toPx() }
                val columnDelta = (offset.x / stepPx).roundToInt()
                val rowDelta = (offset.y / stepPx).roundToInt()
                val target = slot + columnDelta + rowDelta * columns
                return target.takeIf { it >= 0 }
            }
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    suppressDragReturnAnimation = false
                    dragOffset = Offset.Zero
                    dragInProgress = true
                    onPressFeedback()
                    onDragStarted()
                    onDragHover(null)
                    onDragOffsetChanged(Offset.Zero)
                },
                onDrag = { change, dragAmount ->
                    change.consume()
                    dragOffset += dragAmount
                    onDragOffsetChanged(dragOffset)
                    onDragHover(targetPositionFor(dragOffset))
                },
                onDragEnd = {
                    if (!onDropDelete(dragOffset)) {
                        val targetPosition = targetPositionFor(dragOffset)
                        if (targetPosition != null) {
                            suppressDragReturnAnimation = targetPosition != slot
                            onMove(targetPosition)
                        }
                    }
                    dragOffset = Offset.Zero
                    dragInProgress = false
                    onDragFinished()
                },
                onDragCancel = {
                    suppressDragReturnAnimation = false
                    dragOffset = Offset.Zero
                    dragInProgress = false
                    onDragFinished()
                }
            )
        }
    } else {
        Modifier
    }
    val clickModifier = when {
        hasWidget || letWidgetHandleTouch -> Modifier
        button.isTrimControl() && !previewMode -> Modifier.trimSwitchGesture(
            enabled = enabled,
            style = button.controlStyle,
            joyPadEightWay = joyPadPayloadParts(button.payload).eightWay,
            onPress = onPressFeedback,
            onRelease = onReleaseFeedback,
            onPreviewStep = { step -> trimVisualStep = step },
            onActiveStepChange = { step ->
                trimRepeatStep = if (button.controlStyle.usesTrimRepeatFrequency()) step else 0
            },
            onStep = onTrimStep
        )
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
    val surfaceColor = if (touchPressed && isConsole) {
        themeColors.consoleButtonFeatured
    } else if (touchPressed) {
        Color.White.copy(alpha = 0.16f).compositeOver(containerColor)
    } else {
        containerColor
    }
    val classicOutlineColor = if (!isConsole && !classicSolidButtonBackground) {
        if (darkTheme) {
            Color.White.copy(alpha = 0.34f).compositeOver(button.color)
        } else {
            button.color
        }.copy(alpha = 0.96f)
    } else {
        Color.Transparent
    }

    Box(
        modifier = modifier
            .then(dragModifier)
            .zIndex(
                when {
                    dragInProgress -> 2f
                    swapPreviewOffset != Offset.Zero -> 1f
                    else -> 0f
                }
            )
            .then(
                if (isConsole) {
                    Modifier.consoleButtonDropShadow(
                        shape = buttonShape,
                        darkTheme = darkTheme,
                        pressed = touchPressed
                    )
                } else {
                    Modifier
                }
            )
            .graphicsLayer {
                translationX = if (dragInProgress) displayOffset.x else animatedX
                translationY = if (dragInProgress) displayOffset.y else animatedY
                scaleX = animatedScale
                scaleY = animatedScale
            }
    ) {
        Surface(
            modifier = Modifier
                .matchParentSize()
                .then(
                    if (!isConsole && !classicSolidButtonBackground) {
                        Modifier.border(2.dp, classicOutlineColor, buttonShape)
                    } else {
                        Modifier
                    }
                )
            .clip(buttonShape)
            .then(clickModifier),
        shape = buttonShape,
        tonalElevation = if (isConsole) 0.dp else 2.dp,
        shadowElevation = 0.dp,
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
        if (button.isTrimControl()) {
            TrimSwitchContent(
                modifier = Modifier.fillMaxSize(),
                button = button,
                visualMode = visualMode,
                contentColor = contentColor,
                cellSize = cellSize,
                visualStep = trimVisualStep
            )
            return@Surface
        }
        if (isConsole) {
            ConsoleRaisedButtonFrame(
                modifier = Modifier.fillMaxSize(),
                shape = buttonShape,
                cornerRadius = 18.dp,
                pressed = touchPressed,
                drawShadow = false,
                drawHighlight = false,
                shadowColor = Color.Black,
                highlightColor = null
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                ) {
                    ClassicDeckKeyContent(
                        button = button,
                        status = status,
                        contentColor = contentColor,
                        cellSize = cellSize,
                        visualShape = visualShape,
                        contentScale = contentScale,
                        liftedContent = true
                    )
                }
            }
            return@Surface
        }
        if (!isConsole) {
            ClassicDeckKeyContent(
                button = button,
                status = status,
                contentColor = contentColor,
                cellSize = cellSize,
                visualShape = visualShape,
                contentScale = contentScale
            )
            return@Surface
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isConsole) {
                        Modifier
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.White.copy(alpha = 0.18f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.18f)
                                    )
                                )
                            )
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
}

@Composable
private fun ClassicDeckKeyContent(
    button: DeckButton,
    status: HidStatus,
    contentColor: Color,
    cellSize: Dp,
    visualShape: ButtonVisualShape = ButtonVisualShape.Square,
    contentScale: Float = 1f,
    liftedContent: Boolean = false
) {
    val displayTitle = buttonDisplayTitle(button)
    val subtitleText = buttonSubtitle(button, status)
    val hasTitle = displayTitle.isNotBlank()
    val hasSubtitle = subtitleText.isNotBlank()
    val showText = cellSize >= 72.dp && button.displayMode != DeckDisplayMode.IconOnly
    val showSubtitle = cellSize >= 86.dp && hasSubtitle
    val horizontalLayout = visualShape == ButtonVisualShape.Horizontal &&
        button.displayMode != DeckDisplayMode.IconOnly &&
        showText
    if (horizontalLayout) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .graphicsLayer {
                    scaleX = contentScale
                    scaleY = contentScale
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (button.displayMode != DeckDisplayMode.KeywordOnly) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(min = 42.dp, max = cellSize * 0.82f),
                    contentAlignment = Alignment.Center
                ) {
                    DeckButtonIcon(
                        button = button,
                        tint = contentColor,
                        large = false,
                        lifted = liftedContent,
                        shadowMaxSize = cellSize * 0.82f
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                if (hasTitle) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        if (buttonAppAction(button) == DeckActionType.BluetoothStatus) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(statusDotColor(status.state))
                            )
                        }
                        LiftedText(
                            text = displayTitle,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = contentColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            lifted = liftedContent
                        )
                    }
                }
                if (showSubtitle) {
                    LiftedText(
                        text = subtitleText,
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor.copy(alpha = 0.84f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lifted = liftedContent
                    )
                }
            }
        }
        return
    }
    if (showText && button.displayMode == DeckDisplayMode.KeywordOnly) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (hasTitle) {
                    LiftedText(
                        text = displayTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        lifted = liftedContent
                    )
                }
            }
            if (showSubtitle) {
                LiftedText(
                    text = subtitleText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor.copy(alpha = 0.86f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    lifted = liftedContent
                )
            }
        }
        return
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
            .graphicsLayer {
                scaleX = contentScale
                scaleY = contentScale
            },
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
                    large = button.displayMode == DeckDisplayMode.IconOnly || !showText,
                    lifted = liftedContent,
                    shadowMaxSize = cellSize * 0.98f
                )
            } else {
                LiftedText(
                    text = displayTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lifted = liftedContent
                )
            }
        }
        if (showText) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (hasTitle) {
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
                        LiftedText(
                            text = displayTitle,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = contentColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                            lifted = liftedContent
                        )
                    }
                }
                if (showSubtitle) {
                    LiftedText(
                        text = subtitleText,
                        style = MaterialTheme.typography.labelSmall,
                        color = contentColor.copy(alpha = 0.82f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        lifted = liftedContent
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
    var widgetLoadFailed by remember(appWidgetId) { mutableStateOf(false) }
    if (providerInfo == null || appWidgetHost == null || widgetLoadFailed) {
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
            FrameLayout(it).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        update = { container ->
            runCatching {
                container.removeAllViews()
                val hostView = appWidgetHost.createView(context, appWidgetId, providerInfo).apply {
                    setAppWidget(appWidgetId, providerInfo)
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
                container.addView(hostView)
            }.onFailure { error ->
                Log.w("MobileDeck", "Failed to host app widget $appWidgetId", error)
                container.removeAllViews()
                widgetLoadFailed = true
            }
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
    large: Boolean = false,
    lifted: Boolean = false,
    shadowMaxSize: Dp? = null
) {
    val image = rememberImageBitmap(button.iconImageUri)
    val iconSize = if (large) 80.dp else 52.dp
    val shadowRenderSize = boundedIconShadowSize(iconSize, shadowMaxSize)
    val iconSizePx = with(LocalDensity.current) { iconSize.toPx().roundToInt() }
    val shadowSizePx = with(LocalDensity.current) { shadowRenderSize.toPx().roundToInt() }
    val lightConsoleMode = lifted && !isSystemInDarkTheme()
    when {
        image != null -> {
            if (lifted) {
                val shadow = rememberBakedBitmapIconShadow(
                    key = button.iconImageUri,
                    imageBitmap = image,
                    sizePx = iconSizePx,
                    shadowSizePx = shadowSizePx,
                    enabled = true,
                    lightMode = lightConsoleMode
                )
                Box(modifier = Modifier.size(shadowRenderSize)) {
                    if (shadow != null) {
                        Image(
                            bitmap = shadow,
                            contentDescription = null,
                            modifier = Modifier
                                .size(shadowRenderSize)
                                .align(Alignment.Center)
                        )
                    }
                    Image(
                        bitmap = image,
                        contentDescription = button.title,
                        modifier = Modifier
                            .size(iconSize)
                            .align(Alignment.Center)
                    )
                }
            } else {
                Image(
                    bitmap = image,
                    contentDescription = button.title,
                    modifier = Modifier.size(iconSize)
                )
            }
        }

        materialIconFor(button) != null -> {
            LiftedIcon(
                imageVector = materialIconFor(button)!!,
                contentDescription = button.title,
                modifier = Modifier.size(iconSize),
                shadowSize = iconSize,
                shadowMaxSize = shadowMaxSize,
                shadowKey = "${button.icon}:${button.actionType}:${button.payload}",
                tint = tint,
                lifted = lifted
            )
        }

        else -> {
            LiftedText(
                text = button.icon.ifBlank { button.title.take(1).uppercase() },
                style = if (large) MaterialTheme.typography.displayMedium else MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = tint,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                lifted = lifted
            )
        }
    }
}

@Composable
private fun LiftedIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    shadowSize: Dp = 48.dp,
    shadowMaxSize: Dp? = null,
    shadowKey: String = imageVector.name,
    tint: Color,
    lifted: Boolean
) {
    if (!lifted) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifier,
            tint = tint
        )
        return
    }
    val shadowSizePx = with(LocalDensity.current) { shadowSize.toPx().roundToInt() }
    val shadowRenderSize = boundedIconShadowSize(shadowSize, shadowMaxSize)
    val shadowRenderSizePx = with(LocalDensity.current) { shadowRenderSize.toPx().roundToInt() }
    val lightConsoleMode = !isSystemInDarkTheme()
    val glossTone = if (lightConsoleMode) ConsoleIconStyleTone.MattePearl else ConsoleIconStyleTone.MatteSlate
    val agslGlossBrush = rememberAgslGlossBrush(glossTone)
    val fallbackGlossBrush = fallbackIconGlossBrush(glossTone)
    val shadow = rememberBakedVectorIconShadow(
        key = "$shadowKey:${imageVector.name}:${imageVector.viewportWidth}:${imageVector.viewportHeight}",
        imageVector = imageVector,
        sizePx = shadowSizePx,
        shadowSizePx = shadowRenderSizePx,
        enabled = true,
        lightMode = lightConsoleMode
    )
    Box(
        modifier = Modifier.size(shadowRenderSize),
        contentAlignment = Alignment.Center
    ) {
        if (shadow != null) {
            Image(
                bitmap = shadow,
                contentDescription = null,
                modifier = Modifier
                    .size(shadowRenderSize)
                    .align(Alignment.Center)
            )
        }
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = if (lightConsoleMode) {
                modifier
                    .graphicsLayer {
                        compositingStrategy = CompositingStrategy.Offscreen
                    }
                    .drawWithCache {
                        val brush = agslGlossBrush ?: fallbackGlossBrush
                        onDrawWithContent {
                            drawContent()
                            drawRect(
                                brush = brush,
                                blendMode = BlendMode.SrcAtop
                            )
                        }
                    }
            } else {
                modifier
            },
            tint = if (lightConsoleMode) Color.White else tint
        )
    }
}

private fun boundedIconShadowSize(iconSize: Dp, shadowMaxSize: Dp?): Dp {
    val preferred = 38.dp + iconSize * 0.56f
    val maxSize = shadowMaxSize ?: preferred
    val bounded = if (preferred > maxSize) maxSize else preferred
    return if (bounded < iconSize) iconSize else bounded
}

@Composable
private fun LiftedText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle,
    fontWeight: FontWeight? = null,
    color: Color,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    textAlign: TextAlign? = null,
    lifted: Boolean = false
) {
    if (!lifted) {
        Text(
            text = text,
            modifier = modifier,
            style = style,
            fontWeight = fontWeight,
            color = color,
            maxLines = maxLines,
            overflow = overflow,
            textAlign = textAlign
        )
        return
    }
    Text(
        text = text,
        modifier = modifier,
        style = style,
        fontWeight = fontWeight,
        color = color,
        maxLines = maxLines,
        overflow = overflow,
        textAlign = textAlign
    )
}

@Composable
private fun consoleButtonColor(button: DeckButton): Color {
    val colors = LocalDeckThemeColors.current
    return colors.consoleButtonDefault
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

private const val TRIM_PAYLOAD_SEPARATOR = "|"
private const val JOYPAD_STEP_UP = 10
private const val JOYPAD_STEP_DOWN = -10
private const val JOYPAD_STEP_LEFT = -20
private const val JOYPAD_STEP_RIGHT = 20
private const val JOYPAD_STEP_UP_LEFT = 30
private const val JOYPAD_STEP_UP_RIGHT = 40
private const val JOYPAD_STEP_DOWN_LEFT = -30
private const val JOYPAD_STEP_DOWN_RIGHT = -40
private const val JOYPAD_RELEASE_OFFSET = 1000
private const val INFINITE_WHEEL_NOTCHES_PER_REVOLUTION = 24

private fun DeckButton.isTrimControl(): Boolean {
    return controlStyle == DeckControlStyle.TrimSlider ||
        controlStyle == DeckControlStyle.TrimKnob ||
        controlStyle == DeckControlStyle.InfiniteWheel ||
        controlStyle == DeckControlStyle.JoyPad
}

private fun DeckControlStyle.usesTrimRepeatFrequency(): Boolean {
    return this == DeckControlStyle.TrimSlider ||
        this == DeckControlStyle.TrimKnob
}

private fun trimRepeatDelayMillis(step: Int, firstSend: Boolean): Long {
    if (firstSend) return 0L
    return when (abs(step)) {
        1 -> 300L
        2 -> 180L
        else -> 95L
    }
}

private fun trimPayload(lowerPayload: String, upperPayload: String): String {
    val lower = normalizedTrimPayloadPart(lowerPayload, MEDIA_VOLUME_DOWN)
    val upper = normalizedTrimPayloadPart(upperPayload, MEDIA_VOLUME_UP)
    return "$lower$TRIM_PAYLOAD_SEPARATOR$upper"
}

private fun trimPayloadParts(payload: String): Pair<String, String> {
    if (!payload.contains(TRIM_PAYLOAD_SEPARATOR)) {
        return MEDIA_VOLUME_DOWN to MEDIA_VOLUME_UP
    }
    val parts = payload.split(TRIM_PAYLOAD_SEPARATOR, limit = 2)
    val lower = normalizedTrimPayloadPart(parts.getOrNull(0), MEDIA_VOLUME_DOWN)
    val upper = normalizedTrimPayloadPart(parts.getOrNull(1), MEDIA_VOLUME_UP)
    return lower to upper
}

private fun normalizedTrimPayloadPart(payload: String?, fallback: String): String {
    val trimmed = payload.orEmpty().trim()
    if (trimmed.isBlank()) return fallback
    return mediaKeyChoice(trimmed)?.payload ?: trimmed
}

private fun trimPayloadForStep(payload: String, step: Int): String {
    val (lower, upper) = trimPayloadParts(payload)
    return if (step >= 0) upper else lower
}

private enum class JoyPadDirection(
    val key: String,
    val step: Int,
    @StringRes val labelRes: Int,
    val defaultPressPayload: String
) {
    Up("up", JOYPAD_STEP_UP, R.string.joypad_up_action, "UP"),
    Down("down", JOYPAD_STEP_DOWN, R.string.joypad_down_action, "DOWN"),
    Left("left", JOYPAD_STEP_LEFT, R.string.joypad_left_action, "LEFT"),
    Right("right", JOYPAD_STEP_RIGHT, R.string.joypad_right_action, "RIGHT"),
    UpLeft("upLeft", JOYPAD_STEP_UP_LEFT, R.string.joypad_up_left_action, "UP+LEFT"),
    UpRight("upRight", JOYPAD_STEP_UP_RIGHT, R.string.joypad_up_right_action, "UP+RIGHT"),
    DownLeft("downLeft", JOYPAD_STEP_DOWN_LEFT, R.string.joypad_down_left_action, "DOWN+LEFT"),
    DownRight("downRight", JOYPAD_STEP_DOWN_RIGHT, R.string.joypad_down_right_action, "DOWN+RIGHT");
}

private data class JoyPadActionPayloads(
    val press: String,
    val release: String = ""
)

private data class JoyPadPayloads(
    val eightWay: Boolean = false,
    val actions: Map<JoyPadDirection, JoyPadActionPayloads> = emptyMap()
) {
    fun action(direction: JoyPadDirection): JoyPadActionPayloads {
        return actions[direction] ?: JoyPadActionPayloads(direction.defaultPressPayload)
    }

    fun withDirection(direction: JoyPadDirection, press: String, release: String): JoyPadPayloads {
        return copy(actions = actions + (direction to JoyPadActionPayloads(press, release)))
    }

    fun withEightWay(enabled: Boolean): JoyPadPayloads {
        return copy(eightWay = enabled)
    }
}

private val joyPadCardinalDirections = listOf(
    JoyPadDirection.Up,
    JoyPadDirection.Down,
    JoyPadDirection.Left,
    JoyPadDirection.Right
)

private val joyPadDiagonalDirections = listOf(
    JoyPadDirection.UpLeft,
    JoyPadDirection.UpRight,
    JoyPadDirection.DownLeft,
    JoyPadDirection.DownRight
)

private val joyPadAllDirections = joyPadCardinalDirections + joyPadDiagonalDirections

private fun joyPadDirectionForStep(step: Int): JoyPadDirection? {
    val baseStep = joyPadBaseStep(step)
    return joyPadAllDirections.firstOrNull { it.step == baseStep }
}

private fun joyPadReleaseStep(step: Int): Int {
    return if (step > 0) step + JOYPAD_RELEASE_OFFSET else step - JOYPAD_RELEASE_OFFSET
}

private fun joyPadBaseStep(step: Int): Int {
    return when {
        step > JOYPAD_RELEASE_OFFSET -> step - JOYPAD_RELEASE_OFFSET
        step < -JOYPAD_RELEASE_OFFSET -> step + JOYPAD_RELEASE_OFFSET
        else -> step
    }
}

private fun joyPadIsReleaseStep(step: Int): Boolean {
    return abs(step) > JOYPAD_RELEASE_OFFSET
}

private fun joyPadPayload(payloads: JoyPadPayloads): String {
    val directions = JSONObject()
    joyPadAllDirections.forEach { direction ->
        val action = payloads.action(direction)
        directions.put(
            direction.key,
            JSONObject()
                .put("press", action.press.trim())
                .put("release", action.release.trim())
        )
    }
    return JSONObject()
        .put("kind", "joypad")
        .put("version", 2)
        .put("eightWay", payloads.eightWay)
        .put("directions", directions)
        .toString()
}

private fun joyPadPayload(
    upPayload: String,
    downPayload: String,
    leftPayload: String,
    rightPayload: String
): String {
    return joyPadPayload(
        JoyPadPayloads(
            actions = mapOf(
                JoyPadDirection.Up to JoyPadActionPayloads(upPayload.ifBlank { "UP" }),
                JoyPadDirection.Down to JoyPadActionPayloads(downPayload.ifBlank { "DOWN" }),
                JoyPadDirection.Left to JoyPadActionPayloads(leftPayload.ifBlank { "LEFT" }),
                JoyPadDirection.Right to JoyPadActionPayloads(rightPayload.ifBlank { "RIGHT" })
            )
        )
    )
}

private fun joyPadPayloadParts(payload: String): JoyPadPayloads {
    val trimmed = payload.trim()
    if (trimmed.startsWith("{")) {
        val parsed = runCatching {
            val root = JSONObject(trimmed)
            val directions = root.optJSONObject("directions") ?: JSONObject()
            JoyPadPayloads(
                eightWay = root.optBoolean("eightWay", false),
                actions = joyPadAllDirections.associateWith { direction ->
                    val item = directions.optJSONObject(direction.key)
                    JoyPadActionPayloads(
                        press = item?.optString("press").orEmpty().ifBlank { direction.defaultPressPayload },
                        release = item?.optString("release").orEmpty()
                    )
                }
            )
        }.getOrNull()
        if (parsed != null) return parsed
    }
    val parts = payload.split(TRIM_PAYLOAD_SEPARATOR, limit = 4)
    return JoyPadPayloads(
        actions = mapOf(
            JoyPadDirection.Up to JoyPadActionPayloads(parts.getOrNull(0).orEmpty().ifBlank { "UP" }),
            JoyPadDirection.Down to JoyPadActionPayloads(parts.getOrNull(1).orEmpty().ifBlank { "DOWN" }),
            JoyPadDirection.Left to JoyPadActionPayloads(parts.getOrNull(2).orEmpty().ifBlank { "LEFT" }),
            JoyPadDirection.Right to JoyPadActionPayloads(parts.getOrNull(3).orEmpty().ifBlank { "RIGHT" })
        )
    )
}

private fun controlPayloadForStep(button: DeckButton, step: Int): String {
    if (button.controlStyle == DeckControlStyle.JoyPad) {
        val payloads = joyPadPayloadParts(button.payload)
        val direction = joyPadDirectionForStep(step) ?: JoyPadDirection.Up
        val action = payloads.action(direction)
        return if (joyPadIsReleaseStep(step)) action.release else action.press
    }
    return trimPayloadForStep(button.payload, step)
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
        ICON_IMAGE -> Icons.Filled.Image
        ICON_COMPUTER -> Icons.Filled.Computer
        ICON_PHONE -> Icons.Filled.PhoneAndroid
        ICON_TABLET -> Icons.Filled.TabletAndroid
        ICON_LINK -> Icons.Filled.Link
        ICON_SEARCH -> Icons.Filled.Search
        ICON_REFRESH -> Icons.Filled.Refresh
        ICON_SAVE -> Icons.Filled.Save
        ICON_TOUCH -> Icons.Filled.TouchApp
        ICON_VIDEO -> Icons.Filled.Videocam
        ICON_INFO -> Icons.Filled.Info
        else -> null
    }
}

private fun iconChoices(): List<IconChoice> {
    return listOf(
        IconChoice(ICON_SETTINGS, R.string.icon_settings),
        IconChoice(ICON_BLUETOOTH, R.string.icon_bluetooth),
        IconChoice(ICON_KEYBOARD, R.string.icon_keyboard),
        IconChoice(ICON_CODE, R.string.icon_code),
        IconChoice(ICON_TEXT, R.string.icon_text_fields),
        IconChoice(ICON_PLAY, R.string.icon_play),
        IconChoice(ICON_STOP, R.string.icon_stop),
        IconChoice(ICON_PREVIOUS, R.string.icon_previous),
        IconChoice(ICON_NEXT, R.string.icon_next),
        IconChoice(ICON_VOLUME_OFF, R.string.icon_volume_off),
        IconChoice(ICON_VOLUME_DOWN, R.string.icon_volume_down),
        IconChoice(ICON_VOLUME_UP, R.string.icon_volume_up),
        IconChoice(ICON_IMAGE, R.string.icon_image),
        IconChoice(ICON_APPS, R.string.icon_apps),
        IconChoice(ICON_COMPUTER, R.string.icon_computer),
        IconChoice(ICON_PHONE, R.string.icon_phone),
        IconChoice(ICON_TABLET, R.string.icon_tablet),
        IconChoice(ICON_LINK, R.string.icon_link),
        IconChoice(ICON_SEARCH, R.string.icon_search),
        IconChoice(ICON_REFRESH, R.string.icon_refresh),
        IconChoice(ICON_SAVE, R.string.icon_save),
        IconChoice(ICON_TOUCH, R.string.icon_touch),
        IconChoice(ICON_VIDEO, R.string.icon_video),
        IconChoice(ICON_INFO, R.string.icon_info)
    )
}

private fun selectedIconChoice(key: String): IconChoice {
    return if (key == ICON_AUTO) {
        IconChoice(ICON_AUTO, R.string.icon_auto)
    } else {
        iconChoices().firstOrNull { it.key == key } ?: iconChoices().first()
    }
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
    val normalizedPayload = payload
        .trim()
        .uppercase()
        .replace("-", "_")
        .replace(" ", "_")
    val compactPayload = normalizedPayload.replace("_", "")
    val canonicalPayload = when (compactPayload) {
        "MUTE" -> MEDIA_MUTE
        "STOP" -> MEDIA_STOP
        "NEXT" -> MEDIA_NEXT
        "PREVIOUS", "PREV" -> MEDIA_PREVIOUS
        "VOLUMEUP" -> MEDIA_VOLUME_UP
        "VOLUMEDOWN" -> MEDIA_VOLUME_DOWN
        "PLAYPAUSE", "PLAY", "PAUSE" -> MEDIA_PLAY_PAUSE
        "NEXTTRACK" -> MEDIA_NEXT
        "PREVIOUSTRACK" -> MEDIA_PREVIOUS
        else -> normalizedPayload
    }
    return mediaKeyChoices().firstOrNull { it.payload == canonicalPayload }
}

private fun selectedMediaKeyChoice(payload: String, fallbackPayload: String = MEDIA_MUTE): MediaKeyChoice {
    return mediaKeyChoice(payload) ?: mediaKeyChoice(fallbackPayload) ?: mediaKeyChoices().first()
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
        "Discoverable mode is active. Pair from the PC while HID is registered." -> stringResource(R.string.status_message_discoverable_active)
        "Discoverable mode is active. Pair from the PC while the Bluetooth keyboard is registered." -> stringResource(R.string.status_message_discoverable_active)
        "Discoverable request was canceled." -> stringResource(R.string.status_message_discoverable_request_canceled)
        "Discoverable request finished. Pair from the PC while HID is registered." -> stringResource(R.string.status_message_discoverable_finished)
        "Discoverable request finished. Pair from the PC while the Bluetooth keyboard is registered." -> stringResource(R.string.status_message_discoverable_finished)
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
    appWidgetHost: AppWidgetHost?,
    appWidgetManager: AppWidgetManager?,
    classicSolidButtonBackground: Boolean,
    consoleStyle: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (DeckButton) -> Unit,
    onPickWidget: (DeckButton) -> Unit
) {
    var title by remember(button.id) { mutableStateOf(button.title) }
    var subtitle by remember(button.id) { mutableStateOf(button.subtitle) }
    var icon by remember(button.id) { mutableStateOf(button.icon) }
    var iconImageUri by remember(button.id) { mutableStateOf(button.iconImageUri) }
    var displayMode by remember(button.id) { mutableStateOf(button.displayMode) }
    var showTitleField by remember(button.id) { mutableStateOf(button.displayMode != DeckDisplayMode.IconOnly && button.title.isNotBlank()) }
    var showSubtitleField by remember(button.id) { mutableStateOf(button.subtitle.isNotBlank()) }
    var payload by remember(button.id) { mutableStateOf(button.payload) }
    var actionType by remember(button.id) { mutableStateOf(button.actionType) }
    var spanColumns by remember(button.id) { mutableStateOf(button.spanColumns) }
    var spanRows by remember(button.id) { mutableStateOf(button.spanRows) }
    var appWidgetId by remember(button.id) { mutableStateOf(button.appWidgetId) }
    var appWidgetTouchable by remember(button.id) { mutableStateOf(button.appWidgetTouchable) }
    var controlStyle by remember(button.id) { mutableStateOf(button.controlStyle) }
    var trimLowerPayload by remember(button.id) { mutableStateOf(trimPayloadParts(button.payload).first) }
    var trimUpperPayload by remember(button.id) { mutableStateOf(trimPayloadParts(button.payload).second) }
    var joyPadPayloads by remember(button.id) { mutableStateOf(joyPadPayloadParts(button.payload)) }
    var iconMenuExpanded by remember { mutableStateOf(false) }
    var mediaMenuExpanded by remember { mutableStateOf(false) }
    var trimLowerMediaMenuExpanded by remember { mutableStateOf(false) }
    var trimUpperMediaMenuExpanded by remember { mutableStateOf(false) }
    var appCommandMenuExpanded by remember { mutableStateOf(false) }
    var utilityMenuExpanded by remember { mutableStateOf(false) }
    var appPickerVisible by remember { mutableStateOf(false) }
    var keyInputHelpVisible by remember { mutableStateOf(false) }
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
            icon = ""
        }
    }
    LaunchedEffect(appPickerVisible) {
        if (appPickerVisible && launchableApps == null) {
            launchableApps = withContext(Dispatchers.IO) {
                loadLaunchableApps(context.applicationContext)
            }
        }
    }
    LaunchedEffect(button.appWidgetId, button.spanColumns, button.spanRows, button.controlStyle) {
        if (button.appWidgetId == appWidgetId &&
            button.spanColumns == spanColumns &&
            button.spanRows == spanRows &&
            button.controlStyle == controlStyle
        ) {
            return@LaunchedEffect
        }
        title = button.title
        subtitle = button.subtitle
        icon = button.icon
        iconImageUri = button.iconImageUri
        displayMode = button.displayMode
        showTitleField = button.displayMode != DeckDisplayMode.IconOnly && button.title.isNotBlank()
        showSubtitleField = button.subtitle.isNotBlank()
        payload = button.payload
        actionType = button.actionType
        spanColumns = button.spanColumns
        spanRows = button.spanRows
        appWidgetId = button.appWidgetId
        appWidgetTouchable = button.appWidgetTouchable
        controlStyle = button.controlStyle
        trimLowerPayload = trimPayloadParts(button.payload).first
        trimUpperPayload = trimPayloadParts(button.payload).second
        joyPadPayloads = joyPadPayloadParts(button.payload)
        actionPanel = editPanelForButton(button)
    }
    val appCommandActions = listOf(
        DeckActionType.BluetoothStatus,
        DeckActionType.PreviousPage,
        DeckActionType.NextPage,
        DeckActionType.Settings
    )
    val selectedAppCommand = appCommandAction(payload) ?: DeckActionType.BluetoothStatus
    val isTrimControl = controlStyle != DeckControlStyle.Button
    val selectedIcon = selectedIconChoice(icon)
    val selectedIconLabelRes = when {
        iconImageUri.startsWith(APP_ICON_URI_PREFIX) -> R.string.pick_app_icon
        iconImageUri.isNotBlank() -> R.string.pick_image
        else -> selectedIcon.labelRes
    }
    val showIconInPreview = !isTrimControl && displayMode != DeckDisplayMode.KeywordOnly
    val showTitleInPreview = showTitleField
    val showSubtitleInput = actionPanel != EditActionPanel.AppCommand
    val showSubtitleInPreview = showSubtitleInput && showSubtitleField
    val showTextInPreview = showTitleInPreview || showSubtitleInPreview
    val selectedMedia = selectedMediaKeyChoice(payload)
    val selectedTrimLowerMedia = selectedMediaKeyChoice(trimLowerPayload, MEDIA_VOLUME_DOWN)
    val selectedTrimUpperMedia = selectedMediaKeyChoice(trimUpperPayload, MEDIA_VOLUME_UP)
    val selectedUtility = selectedUtilityChoice(payload)
    val canSave = (actionPanel == EditActionPanel.Widget || title.isNotBlank() || !showTitleInPreview) &&
        (!payloadRequired(actionType) || payload.isNotBlank())
    val configuration = LocalConfiguration.current
    val compactLandscapeDialog = configuration.screenHeightDp < 420
    val dialogWidthFraction = when {
        compactLandscapeDialog -> 0.98f
        configuration.screenWidthDp >= 900 -> 0.9f
        else -> 0.94f
    }
    val dialogHeightFraction = if (compactLandscapeDialog) 0.9f else 0.84f
    val dialogPadding = if (compactLandscapeDialog) 10.dp else if (configuration.screenWidthDp >= 900) 14.dp else 12.dp
    val dialogColumnSpacing = if (compactLandscapeDialog) 8.dp else 12.dp
    val previewPaneWidth = if (compactLandscapeDialog) 176.dp else 208.dp
    fun shouldReplaceDefaultTitle(): Boolean {
        return title.isBlank() || title == "New key" || title == button.title
    }
    fun setDefaultTitleIfAllowed(newTitle: String, newSubtitle: String = "") {
        if (shouldReplaceDefaultTitle()) {
            title = newTitle
            subtitle = newSubtitle
            showTitleField = newTitle.isNotBlank()
            showSubtitleField = newSubtitle.isNotBlank()
            displayMode = displayModeWith(showIcon = showIconInPreview, showText = newTitle.isNotBlank() || newSubtitle.isNotBlank())
        }
    }
    fun applyControlStyle(style: DeckControlStyle) {
        controlStyle = style
        if (style == DeckControlStyle.Button) {
            if (
                actionPanel == EditActionPanel.KeyboardInput &&
                (actionType == DeckActionType.MediaKey || payload.contains(TRIM_PAYLOAD_SEPARATOR))
            ) {
                actionType = DeckActionType.Hotkey
                payload = "WIN+E"
            }
            return
        }
        icon = ICON_AUTO
        iconImageUri = ""
        displayMode = displayModeWith(showIcon = false, showText = showTextInPreview)
        if (style == DeckControlStyle.JoyPad) {
            actionType = DeckActionType.Hotkey
            payload = joyPadPayload(joyPadPayloads)
            setDefaultTitleIfAllowed(context.getString(R.string.control_style_joypad), "D-pad")
            spanColumns = 2
            spanRows = 2
        } else {
            actionType = DeckActionType.MediaKey
            trimLowerPayload = trimLowerPayload.ifBlank { MEDIA_VOLUME_DOWN }
            trimUpperPayload = trimUpperPayload.ifBlank { MEDIA_VOLUME_UP }
            payload = trimPayload(trimLowerPayload, trimUpperPayload)
            setDefaultTitleIfAllowed("Volume", "Trim")
            if (style == DeckControlStyle.TrimSlider) {
                spanColumns = 1
                spanRows = 2
            } else if (style == DeckControlStyle.TrimKnob || style == DeckControlStyle.InfiniteWheel) {
                spanColumns = 2
                spanRows = 2
            }
        }
    }
    fun selectActionPanel(panel: EditActionPanel) {
        val previousActionType = actionType
        if (panel != EditActionPanel.KeyboardInput && isTrimControl) {
            controlStyle = DeckControlStyle.Button
        }
        actionPanel = panel
        when (panel) {
            EditActionPanel.AppCommand -> {
                actionType = DeckActionType.AppCommand
                if (appCommandAction(payload) == null) payload = DeckActionType.BluetoothStatus.name
                val command = appCommandAction(payload) ?: DeckActionType.BluetoothStatus
                subtitle = ""
                showSubtitleField = false
                setDefaultTitleIfAllowed(context.getString(command.labelRes))
            }
            EditActionPanel.KeyboardInput -> {
                actionType = DeckActionType.Hotkey
                if (previousActionType != DeckActionType.Hotkey &&
                    previousActionType != DeckActionType.Text ||
                    payload.isBlank() ||
                    payload == "CTRL+F9"
                ) {
                    payload = "WIN+E"
                }
                setDefaultTitleIfAllowed("Explorer", "Win+E")
            }
            EditActionPanel.RunCommand -> {
                actionType = DeckActionType.RunCommand
                if (previousActionType != DeckActionType.RunCommand || payload.isBlank()) payload = "notepad"
                setDefaultTitleIfAllowed("Notepad", "notepad")
            }
            EditActionPanel.Utility -> {
                actionType = DeckActionType.Utility
                if (utilityChoice(payload) == null) payload = UTILITY_TIME
                icon = ICON_AUTO
                iconImageUri = ""
                val utility = utilityChoice(payload) ?: utilityChoice(UTILITY_TIME)!!
                setDefaultTitleIfAllowed(context.getString(utility.labelRes))
            }
            EditActionPanel.Widget -> Unit
        }
    }
    fun editedButton(): DeckButton {
        val savedPayload = if (controlStyle == DeckControlStyle.JoyPad) {
            joyPadPayload(joyPadPayloads)
        } else if (isTrimControl) {
            trimPayload(trimLowerPayload, trimUpperPayload)
        } else if (actionType == DeckActionType.AppCommand) {
            selectedAppCommand.name
        } else if (actionType == DeckActionType.MediaKey) {
            selectedMedia.payload
        } else if (actionType == DeckActionType.Utility) {
            selectedUtility.payload
        } else if (payloadRequired(actionType)) {
            payload.trim()
        } else {
            ""
        }
        return button.copy(
            title = if (showTitleInPreview) title.trim() else "",
            subtitle = if (shouldHideSubtitleEditor(actionType, savedPayload) || !showSubtitleField) "" else subtitle.trim(),
            icon = if (isTrimControl) "" else icon.trim(),
            iconImageUri = if (isTrimControl) "" else iconImageUri,
            displayMode = displayModeWith(showIcon = showIconInPreview, showText = showTextInPreview),
            actionType = actionType,
            payload = savedPayload,
            spanColumns = spanColumns.coerceIn(1, MAX_BUTTON_SPAN_COLUMNS),
            spanRows = spanRows.coerceIn(1, MAX_BUTTON_SPAN_ROWS),
            appWidgetId = appWidgetId,
            appWidgetTouchable = appWidgetTouchable,
            controlStyle = controlStyle
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
                icon = ""
                iconImageUri = appIconUri(app.packageName)
                appPickerVisible = false
            }
        )
    }
    if (keyInputHelpVisible) {
        AlertDialog(
            onDismissRequest = { keyInputHelpVisible = false },
            title = { Text(stringResource(R.string.key_input_help_title)) },
            text = { Text(stringResource(R.string.key_input_help_body)) },
            confirmButton = {
                TextButton(onClick = { keyInputHelpVisible = false }) {
                    Text(stringResource(R.string.confirm))
                }
            }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val darkTheme = isSystemInDarkTheme()
        val dialogShape = RoundedCornerShape(if (consoleStyle) 20.dp else 12.dp)
        val dialogBackground = if (consoleStyle) {
            colors.consolePreviewBackground.compositeOver(colors.backgroundGradient.first())
        } else {
            colors.cardBackground.compositeOver(colors.backgroundGradient.first())
        }
        val dialogBorder = if (consoleStyle) {
            Color.White.copy(alpha = if (darkTheme) 0.12f else 0.54f)
        } else {
            Color.Transparent
        }
        val panelAccent = if (consoleStyle) colors.consoleButtonFeatured else ClassicButtonAccent
        val previewButton = button.copy(
            title = if (showTitleInPreview) title else "",
            subtitle = if (showSubtitleInPreview) subtitle else "",
            icon = if (isTrimControl) "" else icon,
            iconImageUri = if (isTrimControl) "" else iconImageUri,
            displayMode = displayModeWith(showIcon = showIconInPreview, showText = showTextInPreview),
            actionType = actionType,
            payload = when {
                controlStyle == DeckControlStyle.JoyPad -> joyPadPayload(joyPadPayloads)
                isTrimControl -> trimPayload(trimLowerPayload, trimUpperPayload)
                else -> payload
            },
            appWidgetId = appWidgetId,
            appWidgetTouchable = appWidgetTouchable,
            spanColumns = spanColumns,
            spanRows = spanRows,
            controlStyle = controlStyle
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth(dialogWidthFraction)
                .fillMaxHeight(dialogHeightFraction)
                .then(
                    if (consoleStyle) {
                        Modifier.consolePanelDropShadow(dialogShape, darkTheme)
                    } else {
                        Modifier
                    }
                ),
            color = dialogBackground,
            contentColor = colors.textPrimary,
            shape = dialogShape,
            tonalElevation = if (consoleStyle) 0.dp else 8.dp,
            shadowElevation = if (consoleStyle) 0.dp else 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, dialogBorder, dialogShape)
                    .padding(dialogPadding)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(dialogColumnSpacing)
                ) {
                    EditActionPanelRail(
                        selected = actionPanel,
                        accent = panelAccent,
                        consoleStyle = consoleStyle,
                        compact = compactLandscapeDialog,
                        onSelected = ::selectActionPanel
                    )

                    KeyEditPreviewPane(
                        modifier = Modifier.width(previewPaneWidth),
                        previewButton = previewButton,
                        status = status,
                        appWidgetHost = appWidgetHost,
                        appWidgetManager = appWidgetManager,
                        classicSolidButtonBackground = classicSolidButtonBackground,
                        consoleStyle = consoleStyle
                    )

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ClassicEditDialogButton(
                                    modifier = Modifier.weight(1f),
                                    text = stringResource(R.string.cancel),
                                    consoleStyle = consoleStyle,
                                    onClick = onDismiss
                                )
                                ClassicEditDialogButton(
                                    modifier = Modifier.weight(1.25f),
                                    text = stringResource(R.string.save),
                                    icon = Icons.Filled.Save,
                                    highlighted = true,
                                    enabled = canSave,
                                    consoleStyle = consoleStyle,
                                    onClick = { onSave(editedButton()) }
                                )
                            }
                        }
                        if (actionPanel == EditActionPanel.KeyboardInput) {
                            item {
                                KeyEditSettingRow(
                                    icon = Icons.Filled.Keyboard,
                                    title = stringResource(R.string.control_button_type),
                                    consoleStyle = consoleStyle
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        ClassicEditDialogButton(
                                            modifier = Modifier.weight(1f),
                                            text = stringResource(R.string.control_style_button),
                                            highlighted = controlStyle == DeckControlStyle.Button,
                                            consoleStyle = consoleStyle,
                                            onClick = { applyControlStyle(DeckControlStyle.Button) }
                                        )
                                        ClassicEditDialogButton(
                                            modifier = Modifier.weight(1f),
                                            text = stringResource(R.string.control_style_control_button),
                                            highlighted = controlStyle != DeckControlStyle.Button,
                                            consoleStyle = consoleStyle,
                                            onClick = {
                                                if (controlStyle == DeckControlStyle.Button) {
                                                    applyControlStyle(DeckControlStyle.TrimSlider)
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                            if (controlStyle != DeckControlStyle.Button) {
                                item {
                                    KeyEditSettingRow(
                                        icon = Icons.Filled.SwapHoriz,
                                        title = stringResource(R.string.control_style),
                                        consoleStyle = consoleStyle
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            listOf(
                                                DeckControlStyle.TrimSlider,
                                                DeckControlStyle.TrimKnob,
                                                DeckControlStyle.InfiniteWheel,
                                                DeckControlStyle.JoyPad
                                            ).forEach { style ->
                                                ClassicEditDialogButton(
                                                    modifier = Modifier.weight(1f),
                                                    text = stringResource(style.labelRes),
                                                    highlighted = controlStyle == style,
                                                    consoleStyle = consoleStyle,
                                                    onClick = { applyControlStyle(style) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            KeyEditSettingRow(
                                icon = Icons.Filled.TextFields,
                                title = editActionValueLabel(actionPanel),
                                consoleStyle = consoleStyle
                            ) {
                                if (actionPanel == EditActionPanel.KeyboardInput && controlStyle == DeckControlStyle.JoyPad) {
                                    JoyPadPayloadEditor(
                                        payloads = joyPadPayloads,
                                        consoleStyle = consoleStyle,
                                        onChange = { updated ->
                                            joyPadPayloads = updated
                                            payload = joyPadPayload(updated)
                                        }
                                    )
                                } else if (actionPanel == EditActionPanel.KeyboardInput && isTrimControl) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        ExposedDropdownMenuBox(
                                            expanded = trimUpperMediaMenuExpanded,
                                            onExpandedChange = { trimUpperMediaMenuExpanded = !trimUpperMediaMenuExpanded }
                                        ) {
                                            CompactKeyEditTextField(
                                                modifier = Modifier
                                                    .menuAnchor()
                                                    .fillMaxWidth(),
                                                value = stringResource(selectedTrimUpperMedia.labelRes),
                                                onValueChange = {},
                                                readOnly = true,
                                                label = stringResource(R.string.trim_upper_action),
                                                trailingIcon = {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = trimUpperMediaMenuExpanded)
                                                }
                                            )
                                            ExposedDropdownMenu(
                                                expanded = trimUpperMediaMenuExpanded,
                                                onDismissRequest = { trimUpperMediaMenuExpanded = false }
                                            ) {
                                                mediaKeyChoices().forEach { item ->
                                                    DropdownMenuItem(
                                                        text = { Text(stringResource(item.labelRes)) },
                                                        onClick = {
                                                            trimUpperPayload = item.payload
                                                            payload = trimPayload(trimLowerPayload, trimUpperPayload)
                                                            trimUpperMediaMenuExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                        ExposedDropdownMenuBox(
                                            expanded = trimLowerMediaMenuExpanded,
                                            onExpandedChange = { trimLowerMediaMenuExpanded = !trimLowerMediaMenuExpanded }
                                        ) {
                                            CompactKeyEditTextField(
                                                modifier = Modifier
                                                    .menuAnchor()
                                                    .fillMaxWidth(),
                                                value = stringResource(selectedTrimLowerMedia.labelRes),
                                                onValueChange = {},
                                                readOnly = true,
                                                label = stringResource(R.string.trim_lower_action),
                                                trailingIcon = {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = trimLowerMediaMenuExpanded)
                                                }
                                            )
                                            ExposedDropdownMenu(
                                                expanded = trimLowerMediaMenuExpanded,
                                                onDismissRequest = { trimLowerMediaMenuExpanded = false }
                                            ) {
                                                mediaKeyChoices().forEach { item ->
                                                    DropdownMenuItem(
                                                        text = { Text(stringResource(item.labelRes)) },
                                                        onClick = {
                                                            trimLowerPayload = item.payload
                                                            payload = trimPayload(trimLowerPayload, trimUpperPayload)
                                                            trimLowerMediaMenuExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                } else if (actionPanel == EditActionPanel.Utility) {
                                    ExposedDropdownMenuBox(
                                        expanded = utilityMenuExpanded,
                                        onExpandedChange = { utilityMenuExpanded = !utilityMenuExpanded }
                                    ) {
                                        CompactKeyEditTextField(
                                            modifier = Modifier
                                                .menuAnchor()
                                                .fillMaxWidth(),
                                            value = stringResource(selectedUtility.labelRes),
                                            onValueChange = {},
                                            readOnly = true,
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
                                                        iconImageUri = ""
                                                        setDefaultTitleIfAllowed(context.getString(item.labelRes))
                                                        utilityMenuExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                } else if (actionPanel == EditActionPanel.AppCommand) {
                                    ExposedDropdownMenuBox(
                                        expanded = appCommandMenuExpanded,
                                        onExpandedChange = { appCommandMenuExpanded = !appCommandMenuExpanded }
                                    ) {
                                        CompactKeyEditTextField(
                                            modifier = Modifier
                                                .menuAnchor()
                                                .fillMaxWidth(),
                                            value = stringResource(selectedAppCommand.labelRes),
                                            onValueChange = {},
                                            readOnly = true,
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
                                                        subtitle = ""
                                                        showSubtitleField = false
                                                        setDefaultTitleIfAllowed(context.getString(item.labelRes))
                                                        appCommandMenuExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                } else if (actionPanel == EditActionPanel.Widget) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                        }
                                        OutlinedButton(
                                            modifier = Modifier.fillMaxWidth(),
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
                                } else if (actionPanel == EditActionPanel.KeyboardInput) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CompactKeyEditTextField(
                                            modifier = Modifier.weight(1f),
                                            value = payload,
                                            onValueChange = {
                                                actionType = DeckActionType.Hotkey
                                                payload = it
                                            },
                                            enabled = true,
                                            label = ""
                                        )
                                        Box {
                                            OutlinedButton(
                                                modifier = Modifier.height(48.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp),
                                                onClick = { mediaMenuExpanded = true }
                                            ) {
                                                Text(
                                                    text = stringResource(R.string.edit_value_media),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            DropdownMenu(
                                                expanded = mediaMenuExpanded,
                                                onDismissRequest = { mediaMenuExpanded = false }
                                            ) {
                                                mediaKeyChoices().forEach { item ->
                                                    DropdownMenuItem(
                                                        text = { Text(stringResource(item.labelRes)) },
                                                        onClick = {
                                                            actionType = DeckActionType.MediaKey
                                                            payload = item.payload
                                                            controlStyle = DeckControlStyle.Button
                                                            icon = ICON_AUTO
                                                            iconImageUri = ""
                                                            setDefaultTitleIfAllowed(context.getString(item.labelRes))
                                                            mediaMenuExpanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                        IconButton(
                                            modifier = Modifier.size(48.dp),
                                            onClick = { keyInputHelpVisible = true }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Help,
                                                contentDescription = stringResource(R.string.key_input_help_title),
                                                tint = colors.textSecondary
                                            )
                                        }
                                    }
                                } else {
                                    CompactKeyEditTextField(
                                        modifier = Modifier.fillMaxWidth(),
                                        value = payload,
                                        onValueChange = { payload = it },
                                        enabled = payloadRequired(actionType),
                                        label = ""
                                    )
                                }
                            }
                        }
                        if (buttonAppAction(actionType, payload) == DeckActionType.BluetoothStatus) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(colors.toggleBackground.copy(alpha = 0.5f))
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
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
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = colors.textSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        if (actionPanel != EditActionPanel.Widget && !isTrimControl) {
                            item {
                                KeyEditSettingRow(
                                    icon = if (iconImageUri.isBlank()) Icons.Filled.Keyboard else Icons.Filled.Image,
                                    title = stringResource(R.string.icon),
                                    consoleStyle = consoleStyle,
                                    trailing = {
                                        KeyEditCheckbox(
                                            checked = showIconInPreview,
                                            onCheckedChange = { checked ->
                                                displayMode = displayModeWith(
                                                    showIcon = checked,
                                                    showText = showTextInPreview
                                                )
                                            }
                                        )
                                    }
                                ) {
                                    ExposedDropdownMenuBox(
                                        modifier = Modifier.fillMaxWidth(),
                                        expanded = iconMenuExpanded,
                                        onExpandedChange = {
                                            if (showIconInPreview) iconMenuExpanded = !iconMenuExpanded
                                        }
                                    ) {
                                        CompactKeyEditTextField(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor(),
                                            value = stringResource(selectedIconLabelRes),
                                            onValueChange = {},
                                            readOnly = true,
                                            enabled = showIconInPreview,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = iconMenuExpanded)
                                            }
                                        )
                                        ExposedDropdownMenu(
                                            expanded = iconMenuExpanded,
                                            onDismissRequest = { iconMenuExpanded = false }
                                        ) {
                                            IconChoiceDropdownItem(
                                                icon = Icons.Filled.Image,
                                                label = stringResource(
                                                    if (iconImageUri.isBlank()) R.string.pick_image else R.string.change_image
                                                ),
                                                highlighted = true,
                                                onClick = {
                                                    iconMenuExpanded = false
                                                    imagePicker.launch(arrayOf("image/*"))
                                                }
                                            )
                                            IconChoiceDropdownItem(
                                                icon = Icons.Filled.Apps,
                                                label = stringResource(R.string.pick_app_icon),
                                                highlighted = true,
                                                onClick = {
                                                    iconMenuExpanded = false
                                                    appPickerVisible = true
                                                }
                                            )
                                            IconChoiceDropdownItem(
                                                icon = null,
                                                label = stringResource(R.string.icon_auto),
                                                highlighted = true,
                                                onClick = {
                                                    icon = ICON_AUTO
                                                    iconImageUri = ""
                                                    iconMenuExpanded = false
                                                }
                                            )
                                            iconChoices().forEach { item ->
                                                IconChoiceDropdownItem(
                                                    icon = iconVectorForKey(item.key),
                                                    label = stringResource(item.labelRes),
                                                    onClick = {
                                                        icon = item.key
                                                        iconImageUri = ""
                                                        iconMenuExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            item {
                                KeyEditSettingRow(
                                    icon = Icons.Filled.Code,
                                    title = stringResource(R.string.key_content),
                                    consoleStyle = consoleStyle,
                                    trailing = {
                                        KeyEditCheckboxColumn(
                                            checked = listOfNotNull(
                                                showTitleInPreview,
                                                showSubtitleField.takeIf { showSubtitleInput }
                                            ),
                                            onCheckedChange = listOfNotNull(
                                                { checked: Boolean ->
                                                    showTitleField = checked
                                                    displayMode = displayModeWith(
                                                        showIcon = showIconInPreview,
                                                        showText = checked || showSubtitleInPreview
                                                    )
                                                },
                                                ({ checked: Boolean ->
                                                    showSubtitleField = checked
                                                    displayMode = displayModeWith(
                                                        showIcon = showIconInPreview,
                                                        showText = showTitleInPreview || checked
                                                    )
                                                }).takeIf { showSubtitleInput }
                                            )
                                        )
                                    }
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        CompactKeyEditTextField(
                                            modifier = Modifier.fillMaxWidth(),
                                            value = title,
                                            onValueChange = { title = it },
                                            label = stringResource(R.string.title),
                                            enabled = true,
                                        )
                                        if (showSubtitleInput) {
                                            CompactKeyEditTextField(
                                                modifier = Modifier.fillMaxWidth(),
                                                value = subtitle,
                                                onValueChange = { subtitle = it },
                                                label = stringResource(R.string.subtitle),
                                                enabled = true,
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private enum class SettingsTutorialStep {
    Bluetooth,
    Layout,
    Buttons,
    DeckSettingsButton
}

private fun nextSettingsTutorialStep(step: SettingsTutorialStep): SettingsTutorialStep? {
    return when (step) {
        SettingsTutorialStep.Bluetooth -> SettingsTutorialStep.Layout
        SettingsTutorialStep.Layout -> SettingsTutorialStep.Buttons
        SettingsTutorialStep.Buttons -> SettingsTutorialStep.DeckSettingsButton
        SettingsTutorialStep.DeckSettingsButton -> null
    }
}

private fun settingsTutorialStepNumber(step: SettingsTutorialStep): Int {
    return when (step) {
        SettingsTutorialStep.Bluetooth -> 1
        SettingsTutorialStep.Layout -> 2
        SettingsTutorialStep.Buttons -> 3
        SettingsTutorialStep.DeckSettingsButton -> 4
    }
}

@Composable
private fun ClassicSettingsTutorialOverlay(
    modifier: Modifier = Modifier,
    step: SettingsTutorialStep,
    onStepChange: (SettingsTutorialStep) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val accent = when (step) {
        SettingsTutorialStep.Bluetooth -> Color(0xFF25B9FF)
        SettingsTutorialStep.Layout -> ClassicLayoutAccent
        SettingsTutorialStep.Buttons -> ClassicButtonAccent
        SettingsTutorialStep.DeckSettingsButton -> ClassicButtonAccent
    }
    val stepNumber = settingsTutorialStepNumber(step)
    val title = when (step) {
        SettingsTutorialStep.Bluetooth -> stringResource(R.string.classic_tutorial_connect_title)
        SettingsTutorialStep.Layout -> stringResource(R.string.classic_tutorial_layout_title)
        SettingsTutorialStep.Buttons -> stringResource(R.string.classic_tutorial_button_settings_title)
        SettingsTutorialStep.DeckSettingsButton -> stringResource(R.string.classic_tutorial_settings_button_title)
    }
    val body = when (step) {
        SettingsTutorialStep.Bluetooth -> stringResource(R.string.classic_tutorial_connect_body)
        SettingsTutorialStep.Layout -> stringResource(R.string.classic_tutorial_layout_body)
        SettingsTutorialStep.Buttons -> stringResource(R.string.classic_tutorial_button_settings_body)
        SettingsTutorialStep.DeckSettingsButton -> stringResource(R.string.classic_tutorial_settings_button_body)
    }

    Surface(
        modifier = modifier,
        color = Color.Black.copy(alpha = 0.66f),
        contentColor = Color.White,
        onClick = {}
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val sidebarWidth = 300.dp
            val contentWidth = (maxWidth - sidebarWidth - 28.dp).coerceAtLeast(360.dp)
            val highlightModifier = when (step) {
                SettingsTutorialStep.Bluetooth -> Modifier
                    .offset(x = 12.dp, y = 100.dp)
                    .size(width = sidebarWidth - 24.dp, height = 430.dp)
                SettingsTutorialStep.Layout -> Modifier
                    .offset(x = sidebarWidth + 14.dp, y = 82.dp)
                    .size(width = contentWidth, height = 252.dp)
                SettingsTutorialStep.Buttons -> Modifier
                    .offset(x = sidebarWidth + 14.dp, y = 342.dp)
                    .size(width = contentWidth, height = 168.dp)
                SettingsTutorialStep.DeckSettingsButton -> Modifier
            }
            val calloutAlignment = when (step) {
                SettingsTutorialStep.Bluetooth -> Alignment.CenterEnd
                SettingsTutorialStep.Layout -> Alignment.BottomEnd
                SettingsTutorialStep.Buttons -> Alignment.TopEnd
                SettingsTutorialStep.DeckSettingsButton -> Alignment.Center
            }
            val calloutPadding = when (step) {
                SettingsTutorialStep.Bluetooth -> PaddingValues(end = 34.dp)
                SettingsTutorialStep.Layout -> PaddingValues(end = 34.dp, bottom = 28.dp)
                SettingsTutorialStep.Buttons -> PaddingValues(end = 34.dp, top = 34.dp)
                SettingsTutorialStep.DeckSettingsButton -> PaddingValues(0.dp)
            }

            TutorialHighlightFrame(
                modifier = highlightModifier,
                accent = accent,
                stepNumber = stepNumber,
                title = title,
                showBluetoothOrder = step == SettingsTutorialStep.Bluetooth
            )

            Surface(
                modifier = Modifier
                    .align(calloutAlignment)
                    .padding(calloutPadding)
                    .widthIn(max = 430.dp),
                shape = RoundedCornerShape(12.dp),
                color = colors.cardBackground.copy(alpha = 0.98f),
                contentColor = colors.textPrimary,
                shadowElevation = 16.dp,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(accent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stepNumber.toString(),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }
                    Text(
                        text = body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text(stringResource(R.string.skip))
                        }
                        Button(
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accent,
                                contentColor = Color.White
                            ),
                            onClick = {
                                val next = nextSettingsTutorialStep(step)
                                if (next == null) onDismiss() else onStepChange(next)
                            }
                        ) {
                            Text(
                                text = stringResource(
                                    if (nextSettingsTutorialStep(step) == null) R.string.classic_tutorial_done else R.string.next
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ClassicDeckSettingsButtonTutorialOverlay(
    modifier: Modifier = Modifier,
    settingsButton: DeckButton?,
    columns: Int,
    rows: Int,
    spacing: Dp,
    showTitle: Boolean,
    pageSwipeAxis: PageSwipeAxis,
    onDismiss: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val accent = ClassicButtonAccent
    Surface(
        modifier = modifier,
        color = Color.Black.copy(alpha = 0.62f),
        contentColor = Color.White,
        onClick = {}
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            val safeColumns = columns.coerceIn(MIN_COLUMNS, MAX_COLUMNS)
            val safeRows = rows.coerceIn(MIN_ROWS, MAX_ROWS)
            val safeSpacing = spacing.coerceIn(MIN_SPACING_DP.dp, MAX_SPACING_DP.dp)
            val reservedWidth = if (pageSwipeAxis == PageSwipeAxis.Vertical) 12.dp else 0.dp
            val reservedHeight = if (pageSwipeAxis == PageSwipeAxis.Horizontal) 12.dp else 0.dp
            val maxCellWidth = (maxWidth - reservedWidth - safeSpacing * (safeColumns - 1).coerceAtLeast(0).toFloat()) / safeColumns.toFloat()
            val maxCellHeight = (maxHeight - reservedHeight - safeSpacing * (safeRows - 1).coerceAtLeast(0).toFloat()) / safeRows.toFloat()
            val cellSize = minOf(maxCellWidth, maxCellHeight)
            val gridWidth = cellSize * safeColumns.toFloat() + safeSpacing * (safeColumns - 1).coerceAtLeast(0).toFloat()
            val gridHeight = cellSize * safeRows.toFloat() + safeSpacing * (safeRows - 1).coerceAtLeast(0).toFloat()
            val gridLeft = (maxWidth - reservedWidth - gridWidth) / 2f
            val gridTop = (maxHeight - reservedHeight - gridHeight) / 2f
            val targetButton = settingsButton
            val highlightModifier = if (targetButton != null) {
                val slot = buttonToSlot(targetButton, showTitle)
                val columnIndex = slot.floorMod(safeColumns)
                val rowIndex = slot / safeColumns
                val spanColumns = targetButton.effectiveSpanColumns(safeColumns, showTitle)
                val spanRows = targetButton.effectiveSpanRows(safeColumns, safeRows, showTitle)
                val buttonWidth = cellSize * spanColumns.toFloat() + safeSpacing * (spanColumns - 1).coerceAtLeast(0).toFloat()
                val buttonHeight = cellSize * spanRows.toFloat() + safeSpacing * (spanRows - 1).coerceAtLeast(0).toFloat()
                Modifier
                    .offset(
                        x = gridLeft + (cellSize + safeSpacing) * columnIndex.toFloat(),
                        y = gridTop + (cellSize + safeSpacing) * rowIndex.toFloat()
                    )
                    .size(buttonWidth, buttonHeight)
            } else {
                Modifier
                    .align(Alignment.Center)
                    .size(width = 220.dp, height = 110.dp)
            }

            TutorialHighlightFrame(
                modifier = highlightModifier,
                accent = accent,
                stepNumber = settingsTutorialStepNumber(SettingsTutorialStep.DeckSettingsButton),
                title = stringResource(R.string.action_settings),
                showBluetoothOrder = false
            )

            Surface(
                modifier = Modifier
                    .align(Alignment.Center)
                    .widthIn(max = 430.dp),
                shape = RoundedCornerShape(12.dp),
                color = colors.cardBackground.copy(alpha = 0.98f),
                contentColor = colors.textPrimary,
                shadowElevation = 16.dp,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(accent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = settingsTutorialStepNumber(SettingsTutorialStep.DeckSettingsButton).toString(),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Text(
                            text = stringResource(R.string.classic_tutorial_settings_button_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }
                    Text(
                        text = stringResource(R.string.classic_tutorial_settings_button_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accent,
                                contentColor = Color.White
                            ),
                            onClick = onDismiss
                        ) {
                            Text(stringResource(R.string.classic_tutorial_go_to_settings))
                        }
                    }
                }
                }
            }
        }
    }

@Composable
private fun TutorialHighlightFrame(
    modifier: Modifier = Modifier,
    accent: Color,
    stepNumber: Int,
    title: String,
    showBluetoothOrder: Boolean
) {
    Surface(
        modifier = modifier
            .border(2.dp, accent, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        color = accent.copy(alpha = 0.10f),
        contentColor = Color.White,
        shadowElevation = 12.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(accent),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stepNumber.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (showBluetoothOrder) {
                TutorialOrderChip(number = 1, text = stringResource(R.string.classic_tutorial_order_register))
                TutorialOrderChip(number = 2, text = stringResource(R.string.classic_tutorial_order_discoverable))
                TutorialOrderChip(number = 3, text = stringResource(R.string.classic_tutorial_order_pair_pc))
            }
        }
    }
}

@Composable
private fun TutorialOrderChip(number: Int, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(999.dp))
            .background(Color.Black.copy(alpha = 0.32f))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TrashDropTarget(active: Boolean) {
    val colors = LocalDeckThemeColors.current
    val background by animateColorAsState(
        targetValue = if (active) Color(0xFFE5484D) else colors.cardBackground.copy(alpha = 0.96f),
        label = "trashDropBackground"
    )
    val border by animateColorAsState(
        targetValue = if (active) Color(0xFFFF9A9E) else ClassicButtonAccent.copy(alpha = 0.5f),
        label = "trashDropBorder"
    )
    val iconTint by animateColorAsState(
        targetValue = if (active) Color.White else colors.textPrimary,
        label = "trashDropIcon"
    )
    Surface(
        modifier = Modifier
            .size(width = 138.dp, height = 58.dp)
            .border(1.dp, border, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        color = background,
        tonalElevation = 8.dp,
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = stringResource(R.string.delete),
                tint = iconTint,
                modifier = Modifier.size(if (active) 30.dp else 26.dp)
            )
        }
    }
}

@Composable
private fun EditActionPanelRail(
    selected: EditActionPanel,
    accent: Color,
    consoleStyle: Boolean = false,
    compact: Boolean = false,
    onSelected: (EditActionPanel) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val railShape = RoundedCornerShape(if (consoleStyle) 16.dp else 12.dp)
    Column(
        modifier = Modifier
            .width(if (compact) 76.dp else 88.dp)
            .fillMaxHeight()
            .clip(railShape)
            .background(if (consoleStyle) colors.consoleButtonDefault.copy(alpha = 0.72f) else colors.toggleBackground.copy(alpha = 0.46f))
            .border(
                1.dp,
                if (consoleStyle) Color.White.copy(alpha = if (isSystemInDarkTheme()) 0.10f else 0.42f) else colors.cardBorder,
                railShape
            )
            .verticalScroll(rememberScrollState())
            .padding(if (compact) 5.dp else 6.dp),
        verticalArrangement = Arrangement.spacedBy(if (compact) 5.dp else 6.dp)
    ) {
        EditActionPanel.values().forEach { panel ->
            val active = panel == selected
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (compact) 66.dp else 78.dp),
                shape = RoundedCornerShape(10.dp),
                color = if (active) {
                    if (consoleStyle) accent else accent.copy(alpha = if (isSystemInDarkTheme()) 0.30f else 0.22f)
                } else {
                    Color.Transparent
                },
                contentColor = if (active) accent else colors.textSecondary,
                enabled = true,
                onClick = { onSelected(panel) }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(
                            1.dp,
                            if (active && !consoleStyle) accent.copy(alpha = 0.75f) else Color.Transparent,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = editActionPanelIcon(panel),
                        contentDescription = null,
                        tint = if (active) Color.White else colors.textSecondary,
                        modifier = Modifier.size(if (compact) 24.dp else 28.dp)
                    )
                    Spacer(modifier = Modifier.height(if (compact) 5.dp else 8.dp))
                    Text(
                        text = stringResource(panel.labelRes),
                        style = if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (active && consoleStyle) Color.White else if (active) colors.textPrimary else colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun KeyEditPreviewPane(
    modifier: Modifier = Modifier,
    previewButton: DeckButton,
    status: HidStatus,
    appWidgetHost: AppWidgetHost?,
    appWidgetManager: AppWidgetManager?,
    classicSolidButtonBackground: Boolean,
    consoleStyle: Boolean = false
) {
    val colors = LocalDeckThemeColors.current
    val previewRatio = previewButton.spanColumns.coerceAtLeast(1).toFloat() /
        previewButton.spanRows.coerceAtLeast(1).toFloat()
    val paneBackground = if (consoleStyle) colors.consoleButtonDefault else colors.toggleBackground.compositeOver(colors.backgroundGradient.first())
    val paneShape = RoundedCornerShape(if (consoleStyle) 16.dp else 10.dp)
    Column(
        modifier = modifier
            .fillMaxHeight(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = stringResource(R.string.edit_key),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(top = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(paneShape)
                    .background(paneBackground)
                    .border(
                        1.dp,
                        if (consoleStyle) Color.White.copy(alpha = if (isSystemInDarkTheme()) 0.12f else 0.42f) else colors.cardBorder,
                        paneShape
                    )
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                BoxWithConstraints(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val spanColumns = previewButton.spanColumns.coerceAtLeast(1)
                    val spanRows = previewButton.spanRows.coerceAtLeast(1)
                    val availableWidth = maxWidth * 0.92f
                    val availableHeight = maxHeight * 0.92f
                    val previewWidth: Dp
                    val previewHeight: Dp
                    if (availableWidth / availableHeight > previewRatio) {
                        previewHeight = availableHeight
                        previewWidth = availableHeight * previewRatio
                    } else {
                        previewWidth = availableWidth
                        previewHeight = availableWidth / previewRatio
                    }
                    val previewCellSize = previewWidth / spanColumns.toFloat()
                    DeckKey(
                        modifier = Modifier.size(width = previewWidth, height = previewHeight),
                        button = previewButton,
                        status = status,
                        appWidgetHost = appWidgetHost,
                        appWidgetManager = appWidgetManager,
                        visualMode = if (consoleStyle) DeckUiMode.Console else DeckUiMode.Classic,
                        classicSolidButtonBackground = classicSolidButtonBackground,
                        enabled = true,
                        previewMode = previewButton.appWidgetId != INVALID_APP_WIDGET_ID,
                        columns = spanColumns,
                        slot = 0,
                        cellSize = previewCellSize,
                        spacing = 0.dp,
                        contentScale = 0.98f,
                        onPressed = {},
                        onPressFeedback = {},
                        onReleaseFeedback = {},
                        onEdit = {},
                        onMove = {}
                    )
                }
            }
            Text(
                text = stringResource(R.string.preview),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 0.dp, y = (-18).dp)
                    .zIndex(1f)
                    .padding(horizontal = 2.dp),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun IconChoiceDropdownItem(
    icon: ImageVector?,
    label: String,
    highlighted: Boolean = false,
    onClick: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val accent = ClassicButtonAccent
    val contentColor = if (highlighted) accent else colors.textPrimary
    DropdownMenuItem(
        modifier = Modifier.height(40.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (highlighted) {
                                    accent.copy(alpha = 0.18f)
                                } else {
                                    colors.toggleBackground.copy(alpha = 0.42f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier.size(17.dp),
                            tint = contentColor
                        )
                    }
                }
                Text(
                    text = label,
                    color = contentColor,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (highlighted) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        },
        onClick = onClick
    )
}

@Composable
private fun KeyEditSettingRow(
    icon: ImageVector,
    title: String,
    consoleStyle: Boolean = false,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val compact = LocalConfiguration.current.screenHeightDp < 420
    val shape = RoundedCornerShape(if (consoleStyle) 14.dp else 10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (consoleStyle) colors.consoleButtonDefault.copy(alpha = 0.66f) else colors.toggleBackground.copy(alpha = 0.48f))
            .border(
                1.dp,
                if (consoleStyle) Color.White.copy(alpha = if (isSystemInDarkTheme()) 0.10f else 0.38f) else colors.cardBorder,
                shape
            )
            .padding(horizontal = if (compact) 8.dp else 10.dp, vertical = if (compact) 6.dp else 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 10.dp)
    ) {
        SettingsIconTile(icon, if (consoleStyle) colors.consoleButtonFeatured else ClassicButtonAccent)
        Row(
            modifier = Modifier.width(if (compact) 78.dp else 92.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = title,
                style = if (compact) MaterialTheme.typography.labelLarge else MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            trailing?.invoke()
        }
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            content()
        }
    }
}

@Composable
private fun JoyPadPayloadEditor(
    payloads: JoyPadPayloads,
    consoleStyle: Boolean,
    onChange: (JoyPadPayloads) -> Unit
) {
    val directions = if (payloads.eightWay) joyPadAllDirections else joyPadCardinalDirections
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ClassicEditDialogButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.joypad_mode_4_way),
                highlighted = !payloads.eightWay,
                consoleStyle = consoleStyle,
                onClick = { onChange(payloads.withEightWay(false)) }
            )
            ClassicEditDialogButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.joypad_mode_8_way),
                highlighted = payloads.eightWay,
                consoleStyle = consoleStyle,
                onClick = { onChange(payloads.withEightWay(true)) }
            )
        }
        directions.chunked(2).forEach { rowDirections ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowDirections.forEach { direction ->
                    JoyPadDirectionPayloadEditor(
                        modifier = Modifier.weight(1f),
                        direction = direction,
                        action = payloads.action(direction),
                        onChange = { press, release ->
                            onChange(payloads.withDirection(direction, press, release))
                        }
                    )
                }
                if (rowDirections.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun JoyPadDirectionPayloadEditor(
    modifier: Modifier = Modifier,
    direction: JoyPadDirection,
    action: JoyPadActionPayloads,
    onChange: (String, String) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = stringResource(direction.labelRes),
            style = MaterialTheme.typography.labelMedium,
            color = LocalDeckThemeColors.current.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            CompactKeyEditTextField(
                modifier = Modifier.weight(1f),
                value = action.press,
                onValueChange = { onChange(it, action.release) },
                label = stringResource(R.string.joypad_press_action),
                enabled = true
            )
            CompactKeyEditTextField(
                modifier = Modifier.weight(1f),
                value = action.release,
                onValueChange = { onChange(action.press, it) },
                label = stringResource(R.string.joypad_release_action),
                enabled = true
            )
        }
    }
}

@Composable
private fun CompactKeyEditTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    enabled: Boolean = true,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val colors = LocalDeckThemeColors.current
    val textColor = if (enabled) colors.textPrimary else colors.textSecondary.copy(alpha = 0.58f)
    val borderColor = if (enabled) colors.cardBorder else colors.cardBorder.copy(alpha = 0.52f)
    Row(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.cardBackground.copy(alpha = if (enabled) 0.58f else 0.32f))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(start = 10.dp, end = if (trailingIcon == null) 10.dp else 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            if (label.isNotBlank()) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                readOnly = readOnly,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = textColor),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (trailingIcon != null) {
            Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center
            ) {
                trailingIcon()
            }
        }
    }
}

@Composable
private fun KeyEditCheckboxColumn(
    checked: List<Boolean>,
    onCheckedChange: List<(Boolean) -> Unit>
) {
    val fieldHeight = 48.dp
    val fieldSpacing = 8.dp
    if (checked.size <= 1) {
        Box(
            modifier = Modifier.height(fieldHeight),
            contentAlignment = Alignment.CenterEnd
        ) {
            KeyEditCheckbox(
                checked = checked.firstOrNull() == true,
                onCheckedChange = onCheckedChange.firstOrNull() ?: {}
            )
        }
        return
    }
    Column(
        modifier = Modifier.height(fieldHeight * checked.size.toFloat() + fieldSpacing * (checked.size - 1).toFloat()),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.End
    ) {
        checked.forEachIndexed { index, isChecked ->
            Box(
                modifier = Modifier.height(fieldHeight),
                contentAlignment = Alignment.CenterEnd
            ) {
                KeyEditCheckbox(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange.getOrNull(index) ?: {}
                )
            }
        }
    }
}

@Composable
private fun KeyEditCheckbox(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    Surface(
        modifier = modifier.size(28.dp),
        shape = RoundedCornerShape(7.dp),
        color = colors.cardBackground.copy(alpha = 0.34f),
        contentColor = colors.textPrimary,
        onClick = { onCheckedChange(!checked) }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Checkbox(
                modifier = Modifier.size(28.dp),
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

private fun displayModeWith(showIcon: Boolean, showText: Boolean): DeckDisplayMode {
    return when {
        showIcon && showText -> DeckDisplayMode.IconAndText
        showIcon -> DeckDisplayMode.IconOnly
        showText -> DeckDisplayMode.KeywordOnly
        else -> DeckDisplayMode.IconOnly
    }
}

private fun shouldHideSubtitleEditor(actionType: DeckActionType, payload: String): Boolean {
    return buttonAppAction(actionType, payload) != null
}

private fun editPanelForButton(button: DeckButton): EditActionPanel {
    if (button.appWidgetId != INVALID_APP_WIDGET_ID) return EditActionPanel.Widget
    return when (buttonAppAction(button) ?: button.actionType) {
        DeckActionType.Settings,
        DeckActionType.BluetoothStatus,
        DeckActionType.PreviousPage,
        DeckActionType.NextPage,
        DeckActionType.AppCommand -> EditActionPanel.AppCommand
        DeckActionType.MediaKey,
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
        EditActionPanel.RunCommand -> Icons.Filled.Apps
        EditActionPanel.Utility -> Icons.Filled.Apps
    }
}

@Composable
private fun editActionValueLabel(panel: EditActionPanel): String {
    return when (panel) {
        EditActionPanel.AppCommand -> stringResource(R.string.edit_value_app)
        EditActionPanel.KeyboardInput -> stringResource(R.string.edit_value_keyboard)
        EditActionPanel.Widget -> stringResource(R.string.edit_value_widget)
        EditActionPanel.RunCommand -> stringResource(R.string.edit_value_command)
        EditActionPanel.Utility -> stringResource(R.string.edit_value_utility)
    }
}

@Composable
private fun EditDialogSection(
    icon: ImageVector,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = LocalDeckThemeColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.toggleBackground.copy(alpha = 0.42f))
            .border(1.dp, ClassicButtonAccent.copy(alpha = 0.22f), RoundedCornerShape(8.dp)),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colors.cardBackground.copy(alpha = 0.55f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SettingsIconTile(icon, ClassicButtonAccent)
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
        }
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun ClassicEditDialogButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    highlighted: Boolean = false,
    enabled: Boolean = true,
    consoleStyle: Boolean = false,
    onClick: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val compact = LocalConfiguration.current.screenHeightDp < 420
    val background = if (consoleStyle) {
        if (highlighted) colors.consoleButtonFeatured else colors.consoleButtonDefault
    } else if (highlighted) ClassicButtonAccent else colors.toggleBackground.copy(alpha = 0.48f)
    val contentColor = if (highlighted) Color.White else colors.textPrimary
    Button(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick,
        shape = RoundedCornerShape(if (consoleStyle) 12.dp else 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = contentColor,
            disabledContainerColor = if (consoleStyle) colors.consoleButtonDefault.copy(alpha = 0.38f) else colors.toggleBackground.copy(alpha = 0.25f),
            disabledContentColor = colors.textSecondary.copy(alpha = 0.45f)
        ),
        contentPadding = PaddingValues(horizontal = if (compact) 8.dp else 20.dp, vertical = if (compact) 9.dp else 12.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(19.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = if (compact) MaterialTheme.typography.labelMedium else MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
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

@Preview(
    name = "Classic Deck",
    widthDp = 960,
    heightDp = 432,
    showBackground = true
)
@Composable
private fun MobileDeckPreview() {
    MobileDeckTheme {
        val pages = remember { defaultDeckPages() }
        val colors = deckThemeColors(DeckUiMode.Classic, isSystemInDarkTheme())
        CompositionLocalProvider(LocalDeckThemeColors provides colors) {
            DeckPage(
                modifier = Modifier.fillMaxSize(),
                buttons = pages.first().buttons,
                deckPages = pages,
                activePageId = pages.first().id,
                columns = DEFAULT_COLUMNS,
                rows = DEFAULT_ROWS,
                spacing = DEFAULT_SPACING_DP.dp,
                status = HidStatus(HidConnectionState.Connected, "Preview"),
                appWidgetHost = null,
                appWidgetManager = null,
                uiMode = DeckUiMode.Classic,
                consoleLayout = ConsoleLayoutConfig(emptyList()),
                consolePanelOptions = ConsolePanelOptions(),
                classicSolidButtonBackground = true,
                classicDeckBackground = ClassicDeckBackground(),
                previewMode = false,
                pageSwipeAxis = PageSwipeAxis.Horizontal,
                pageSwipeMode = PageSwipeMode.Disabled,
                pageSwipeAnimation = false,
                pageSwipeDelta = 1,
                pageAnimationSequence = 0,
                onPageSwipe = {},
                onAddPage = {},
                onButtonPressed = {},
                onTrimStep = { _, _ -> },
                onButtonEdit = {},
                onButtonMoved = { _, _ -> },
                onEmptySlotPressed = {}
            )
        }
    }
}

@Preview(
    name = "Console Deck Light",
    widthDp = 960,
    heightDp = 432,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun ConsoleDeckLightPreview() {
    ConsoleDeckPreviewContent()
}

@Preview(
    name = "Console Deck Dark",
    widthDp = 960,
    heightDp = 432,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ConsoleDeckDarkPreview() {
    ConsoleDeckPreviewContent()
}


@Composable
private fun ConsoleDeckPreviewContent() {
    MobileDeckTheme(style = MobileDeckThemeStyle.Console) {
        val pages = remember { defaultDeckPages() }
        val colors = deckThemeColors(DeckUiMode.Console, isSystemInDarkTheme())
        val layout = remember(pages) { defaultConsoleLayout(pages.first().buttons) }
        CompositionLocalProvider(LocalDeckThemeColors provides colors) {
            ConsoleDeckSurface(
                modifier = Modifier.fillMaxSize(),
                deckPages = pages,
                activePageId = pages.first().id,
                pageSwipeAnimation = true,
                pageSwipeDelta = 1,
                pageAnimationSequence = 0,
                pageSwipeMode = PageSwipeMode.Disabled,
                layout = layout,
                panelOptions = ConsolePanelOptions(),
                columns = DEFAULT_COLUMNS,
                rows = DEFAULT_ROWS,
                spacing = DEFAULT_SPACING_DP.dp,
                status = HidStatus(HidConnectionState.Disconnected, "Ready to register Bluetooth keyboard"),
                appWidgetHost = null,
                appWidgetManager = null,
                onSettings = {},
                onPageSwipe = {},
                onButtonPressed = {},
                onTrimStep = { _, _ -> },
                onButtonTouchStarted = {},
                onButtonTouchEnded = {}
            )
        }
    }
}

