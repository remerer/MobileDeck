package com.remerer.mobiledeck

import android.annotation.SuppressLint
import android.app.Activity
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
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
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCodeScanner
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
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
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
import androidx.compose.ui.graphics.drawscope.Fill
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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

internal class CodexButtonTaskCoordinator(
    private val scope: CoroutineScope,
    private val taskStates: MutableMap<Int, CodexButtonTaskState>,
    private val commandFailureCodes: MutableMap<Int, String>,
    private val submittingButtons: MutableMap<Int, Boolean>,
    private val submitJob: suspend (CompanionSettings, CodexButtonBindingPayload) -> CodexJobApiResult,
    private val pollJob: suspend (CompanionSettings, String, String) -> CodexJobApiResult,
    private val delayMillis: suspend (Long) -> Unit = { delay(it) },
    private val nowMillis: () -> Long = SystemClock::elapsedRealtime,
    private val onAccepted: (Int, CodexJobSnapshot) -> Unit = { _, _ -> },
    private val onFailure: (Int, String) -> Unit = { _, _ -> },
    private val onConnectionChanged: (Boolean) -> Unit = {}
) {
    private val requestJobs = mutableMapOf<Int, Job>()
    private val expiryJobs = mutableMapOf<Int, Job>()
    private val bindings = mutableMapOf<Int, CodexButtonBindingPayload>()

    fun submitOnce(
        buttonId: Int,
        settings: CompanionSettings,
        binding: CodexButtonBindingPayload
    ): Boolean {
        if (!CompanionReleaseRoutePolicy.allowsCodexSubmit(settings, codexBindingPayloadJson(binding))) {
            return false
        }
        val existing = taskStates[buttonId]
        if (existing?.suppressesDuplicateSubmit == true || existing?.reconnecting == true) return false
        if (existing?.snapshot?.status == CodexJobStatus.Cancelled &&
            !existing.isTerminalDisplayExpired(nowMillis())
        ) {
            return false
        }
        if (requestJobs[buttonId]?.isActive == true || submittingButtons[buttonId] == true) return false

        expiryJobs.remove(buttonId)?.cancel()
        taskStates.remove(buttonId)
        commandFailureCodes.remove(buttonId)
        bindings[buttonId] = binding
        submittingButtons[buttonId] = true

        val requestJob = scope.launch(start = CoroutineStart.LAZY) {
            submitUntilAccepted(buttonId, settings, binding)
        }
        requestJobs[buttonId] = requestJob
        requestJob.invokeOnCompletion {
            if (requestJobs[buttonId] === requestJob) {
                requestJobs.remove(buttonId)
                submittingButtons.remove(buttonId)
            }
        }
        requestJob.start()
        return true
    }

    fun retainBindings(validBindings: Map<Int, CodexButtonBindingPayload>) {
        val staleIds = bindings.keys.filter { buttonId -> bindings[buttonId] != validBindings[buttonId] }
        staleIds.forEach(::removeButton)
    }

    fun clear() {
        (requestJobs.values + expiryJobs.values).forEach(Job::cancel)
        requestJobs.clear()
        expiryJobs.clear()
        bindings.clear()
        taskStates.clear()
        commandFailureCodes.clear()
        submittingButtons.clear()
    }

    private suspend fun submitUntilAccepted(
        buttonId: Int,
        settings: CompanionSettings,
        binding: CodexButtonBindingPayload
    ) {
        var reconnectAttempt = 0
        while (bindings[buttonId] == binding) {
            val result = submitJob(settings, binding)
            val snapshot = result.snapshot
            when {
                snapshot != null -> {
                    onConnectionChanged(true)
                    submittingButtons.remove(buttonId)
                    onAccepted(buttonId, snapshot)
                    observeSnapshot(buttonId, settings, binding, snapshot)
                    return
                }
                result.reconnectRequired -> {
                    onConnectionChanged(false)
                    delayMillis(CodexButtonTaskState.reconnectDelayMillis(reconnectAttempt))
                    reconnectAttempt += 1
                }
                else -> {
                    storeCommandFailure(buttonId, result.failureCode)
                    return
                }
            }
        }
    }

    private suspend fun observeSnapshot(
        buttonId: Int,
        settings: CompanionSettings,
        binding: CodexButtonBindingPayload,
        acceptedSnapshot: CodexJobSnapshot
    ) {
        var current = acceptedSnapshot
        storeSnapshot(buttonId, current)
        if (current.status.isTerminal) return

        while (bindings[buttonId] == binding && current.status.isActive) {
            delayMillis(CODEX_JOB_POLL_INTERVAL_MILLIS)
            var result = pollJob(settings, current.jobId, binding.bindingId)
            var reconnectAttempt = 0
            while (result.reconnectRequired && bindings[buttonId] == binding) {
                taskStates[buttonId] = CodexButtonTaskState(
                    snapshot = current,
                    reconnecting = true,
                    reconnectAttempt = reconnectAttempt
                )
                onConnectionChanged(false)
                delayMillis(CodexButtonTaskState.reconnectDelayMillis(reconnectAttempt))
                reconnectAttempt += 1
                result = pollJob(settings, current.jobId, binding.bindingId)
            }

            val next = result.snapshot
            when {
                next != null -> {
                    onConnectionChanged(true)
                    current = next
                    storeSnapshot(buttonId, current)
                }
                result.reconnectRequired -> return
                else -> {
                    storeCommandFailure(buttonId, result.failureCode)
                    return
                }
            }
        }
    }

    private fun storeSnapshot(buttonId: Int, snapshot: CodexJobSnapshot) {
        commandFailureCodes.remove(buttonId)
        val observedAt = if (
            snapshot.status == CodexJobStatus.Completed || snapshot.status == CodexJobStatus.Cancelled
        ) {
            nowMillis()
        } else {
            null
        }
        val state = CodexButtonTaskState(
            snapshot = snapshot,
            terminalObservedAtMillis = observedAt
        )
        taskStates[buttonId] = state
        if (observedAt != null) scheduleTerminalExpiry(buttonId, state)
    }

    private fun scheduleTerminalExpiry(buttonId: Int, observedState: CodexButtonTaskState) {
        expiryJobs.remove(buttonId)?.cancel()
        val expiryJob = scope.launch {
            delayMillis(CODEX_TERMINAL_DISPLAY_MILLIS)
            if (taskStates[buttonId] === observedState && observedState.isTerminalDisplayExpired(nowMillis())) {
                taskStates.remove(buttonId)
                bindings.remove(buttonId)
            }
        }
        expiryJobs[buttonId] = expiryJob
        expiryJob.invokeOnCompletion {
            if (expiryJobs[buttonId] === expiryJob) expiryJobs.remove(buttonId)
        }
    }

    private fun storeCommandFailure(buttonId: Int, errorCode: String?) {
        onConnectionChanged(true)
        taskStates.remove(buttonId)
        submittingButtons.remove(buttonId)
        val safeCode = projectSafeCodexFailureCode(errorCode) ?: "internal_error"
        commandFailureCodes[buttonId] = safeCode
        onFailure(buttonId, safeCode)
    }

    private fun removeButton(buttonId: Int) {
        requestJobs.remove(buttonId)?.cancel()
        expiryJobs.remove(buttonId)?.cancel()
        bindings.remove(buttonId)
        taskStates.remove(buttonId)
        commandFailureCodes.remove(buttonId)
        submittingButtons.remove(buttonId)
    }
}

internal enum class CodexButtonVisualPhase {
    Queued,
    Running,
    Completed,
    Failed,
    Cancelled,
    Disabled,
    Disconnected,
    Reconnecting
}

internal data class CodexButtonVisualStatus(
    val phase: CodexButtonVisualPhase,
    val elapsedMs: Long = 0L,
    val safeFailureCode: String? = null
)

internal fun codexButtonVisualStatus(
    taskState: CodexButtonTaskState?,
    commandFailureCode: String?,
    submitting: Boolean,
    configured: Boolean,
    connected: Boolean
): CodexButtonVisualStatus? {
    if (commandFailureCode != null) {
        return CodexButtonVisualStatus(
            phase = CodexButtonVisualPhase.Failed,
            safeFailureCode = projectSafeCodexFailureCode(commandFailureCode) ?: "internal_error"
        )
    }
    if (taskState?.reconnecting == true) {
        return CodexButtonVisualStatus(CodexButtonVisualPhase.Reconnecting)
    }
    taskState?.snapshot?.let { snapshot ->
        return when (snapshot.status) {
            CodexJobStatus.Queued -> CodexButtonVisualStatus(CodexButtonVisualPhase.Queued)
            CodexJobStatus.Running -> CodexButtonVisualStatus(
                CodexButtonVisualPhase.Running,
                elapsedMs = snapshot.elapsedMs
            )
            CodexJobStatus.Completed -> CodexButtonVisualStatus(CodexButtonVisualPhase.Completed)
            CodexJobStatus.Failed -> CodexButtonVisualStatus(
                CodexButtonVisualPhase.Failed,
                safeFailureCode = taskState.safeFailureCode
            )
            CodexJobStatus.Cancelled -> CodexButtonVisualStatus(CodexButtonVisualPhase.Cancelled)
        }
    }
    if (submitting) {
        return CodexButtonVisualStatus(
            if (connected) CodexButtonVisualPhase.Queued else CodexButtonVisualPhase.Reconnecting
        )
    }
    if (!configured) return CodexButtonVisualStatus(CodexButtonVisualPhase.Disabled)
    if (!connected) return CodexButtonVisualStatus(CodexButtonVisualPhase.Disconnected)
    return null
}

@Composable
internal fun CodexButtonStatusOverlay(
    status: CodexButtonVisualStatus?,
    modifier: Modifier = Modifier
) {
    status ?: return
    val elapsed = formatCodexElapsed(status.elapsedMs)
    val label = when (status.phase) {
        CodexButtonVisualPhase.Queued -> stringResource(R.string.codex_status_queued)
        CodexButtonVisualPhase.Running -> stringResource(R.string.codex_status_running_elapsed, elapsed)
        CodexButtonVisualPhase.Completed -> stringResource(R.string.codex_status_completed)
        CodexButtonVisualPhase.Failed -> status.safeFailureCode?.let { safeCode ->
            stringResource(R.string.codex_status_failed_code, safeCode)
        } ?: stringResource(R.string.codex_status_failed)
        CodexButtonVisualPhase.Cancelled -> stringResource(R.string.codex_status_cancelled)
        CodexButtonVisualPhase.Disabled -> stringResource(R.string.codex_status_disabled)
        CodexButtonVisualPhase.Disconnected -> stringResource(R.string.codex_status_disconnected)
        CodexButtonVisualPhase.Reconnecting -> stringResource(R.string.codex_status_reconnecting)
    }
    val tint = when (status.phase) {
        CodexButtonVisualPhase.Completed -> Color(0xFF4CD48A)
        CodexButtonVisualPhase.Failed -> Color(0xFFFFC14D)
        CodexButtonVisualPhase.Cancelled,
        CodexButtonVisualPhase.Disabled,
        CodexButtonVisualPhase.Disconnected -> Color(0xFFC7D0D9)
        else -> Color.White
    }
    Row(
        modifier = modifier
            .semantics { contentDescription = label }
            .clip(RoundedCornerShape(6.dp))
            .background(Color.Black.copy(alpha = 0.7f))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        when (status.phase) {
            CodexButtonVisualPhase.Queued,
            CodexButtonVisualPhase.Running -> CircularProgressIndicator(
                modifier = Modifier.size(12.dp),
                color = tint,
                strokeWidth = 1.5.dp
            )
            CodexButtonVisualPhase.Completed -> Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                modifier = Modifier.size(13.dp),
                tint = tint
            )
            CodexButtonVisualPhase.Failed -> Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                modifier = Modifier.size(13.dp),
                tint = tint
            )
            CodexButtonVisualPhase.Cancelled -> Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                modifier = Modifier.size(13.dp),
                tint = tint
            )
            CodexButtonVisualPhase.Disabled -> Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                modifier = Modifier.size(13.dp),
                tint = tint
            )
            CodexButtonVisualPhase.Disconnected -> Icon(
                imageVector = Icons.Filled.Link,
                contentDescription = null,
                modifier = Modifier.size(13.dp),
                tint = tint
            )
            CodexButtonVisualPhase.Reconnecting -> Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = null,
                modifier = Modifier.size(13.dp),
                tint = tint
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = tint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

internal fun formatCodexElapsed(elapsedMs: Long): String {
    val totalSeconds = elapsedMs.coerceAtLeast(0L) / 1_000L
    return "${totalSeconds / 60}:${(totalSeconds % 60).toString().padStart(2, '0')}"
}

private fun codexBindingPayloadJson(binding: CodexButtonBindingPayload): String {
    return """{"programId":"codex","command":"exec.submit","args":{"contractVersion":${binding.contractVersion},"presetId":"${binding.presetId}","bindingId":"${binding.bindingId}"}}"""
}

class MainActivity : ComponentActivity() {
    private var pendingCompanionPairingUri by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingCompanionPairingUri = if (BuildConfig.DEBUG) companionPairingUriFromIntent(intent) else null
        val debugLaunch = debugLaunchConfigFromIntent(intent)
        window.setBackgroundDrawable(ColorDrawable(AndroidColor.BLACK))
        window.decorView.setBackgroundColor(AndroidColor.BLACK)
        enableEdgeToEdge()
        window.statusBarColor = AndroidColor.TRANSPARENT
        window.navigationBarColor = AndroidColor.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)
        hideSystemBars()
        setContent {
            MobileDeckTheme {
                MobileDeckApp(
                    debugLaunch = debugLaunch,
                    pendingCompanionPairingUri = pendingCompanionPairingUri,
                    onCompanionPairingUriConsumed = {
                        pendingCompanionPairingUri = null
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        pendingCompanionPairingUri = if (BuildConfig.DEBUG) companionPairingUriFromIntent(intent) else null
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

private data class DebugLaunchConfig(
    val page: AppPage? = null,
    val deckPageIndex: Int? = null,
    val studyPageIndex: Int? = null,
    val uiMode: DeckUiMode? = null
)

private fun debugLaunchConfigFromIntent(intent: Intent?): DebugLaunchConfig {
    if (!BuildConfig.DEBUG || intent == null) return DebugLaunchConfig()
    val page = when (intent.getStringExtra("mobiledeck.debug.page").orEmpty()) {
        "deck" -> AppPage.Deck
        "settings" -> AppPage.Settings
        "layout" -> AppPage.LayoutEditor
        "consoleLayout" -> AppPage.ConsoleLayoutEditor
        "buttonStudy" -> AppPage.IconStyleTest
        else -> null
    }
    val uiMode = when (intent.getStringExtra("mobiledeck.debug.uiMode").orEmpty()) {
        "classic" -> DeckUiMode.Classic
        "console" -> DeckUiMode.Console
        else -> null
    }
    return DebugLaunchConfig(
        page = page,
        deckPageIndex = intent.takeIf { it.hasExtra("mobiledeck.debug.deckPageIndex") }
            ?.getIntExtra("mobiledeck.debug.deckPageIndex", 0),
        studyPageIndex = intent.takeIf { it.hasExtra("mobiledeck.debug.studyPageIndex") }
            ?.getIntExtra("mobiledeck.debug.studyPageIndex", 0),
        uiMode = uiMode
    )
}

@Composable
private fun MobileDeckApp(
    debugLaunch: DebugLaunchConfig = DebugLaunchConfig(),
    pendingCompanionPairingUri: String? = null,
    onCompanionPairingUriConsumed: () -> Unit = {}
) {
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
    var activeDeckPageId by remember {
        val debugIndex = debugLaunch.deckPageIndex?.coerceIn(0, deckPages.lastIndex)
        mutableStateOf(debugIndex?.let { deckPages[it].id } ?: deckPages.first().id)
    }
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
    var deckUiMode by remember { mutableStateOf(debugLaunch.uiMode ?: loadDeckUiMode(context)) }
    var classicFontSize by remember { mutableStateOf(loadClassicFontSizeOption(context)) }
    var consoleFontSize by remember { mutableStateOf(loadConsoleFontSizeOption(context)) }
    val initialConsoleLayouts = remember { loadConsoleLayouts(context) }
    val initialConsoleLayout = remember { initialConsoleLayouts[activeDeckPageId] ?: loadConsoleLayout(context) }
    var consoleSidebarFraction by remember { mutableStateOf(initialConsoleLayout.sidebarFraction) }
    var consoleLayouts by remember {
        mutableStateOf(initialConsoleLayouts.mapValues { (_, layout) ->
            layout.copy(sidebarFraction = consoleSidebarFraction)
        })
    }
    var consoleLayout by remember {
        mutableStateOf(initialConsoleLayout.copy(sidebarFraction = consoleSidebarFraction))
    }
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
    var guideCardsVisible by remember { mutableStateOf(loadGuideCards(context)) }
    var page by remember { mutableStateOf(debugLaunch.page ?: AppPage.Deck) }
    var showClassicTutorial by remember { mutableStateOf(shouldShowClassicTutorial(context)) }
    var activeTutorialMode by remember { mutableStateOf(DeckUiMode.Classic) }
    var classicTutorialStep by remember { mutableStateOf(SettingsTutorialStep.PcConnection) }
    var confirmSettingsButtonRestore by remember { mutableStateOf(false) }
    var debugKeepScreenOn by remember { mutableStateOf(false) }
    var companionSettings by remember { mutableStateOf(loadCompanionSettings(context)) }
    var companionStatus by remember { mutableStateOf(CompanionConnectionStatus()) }
    var companionRouteActive by remember { mutableStateOf(false) }
    var companionSyncViewToPc by remember { mutableStateOf(loadCompanionSyncViewToPc(context)) }
    var companionFollowPcView by remember { mutableStateOf(loadCompanionFollowPcView(context)) }
    var skipNextViewOnlyBundleUpload by remember { mutableStateOf(false) }
    var suppressNextViewSyncUpload by remember { mutableStateOf(false) }
    var classicCompanionConnectionExpanded by remember { mutableStateOf(loadClassicCompanionConnectionExpanded(context)) }
    var confirmEmptyPageDeletePageId by remember { mutableStateOf<Int?>(null) }
    val companionClient = remember(context) { CompanionApiClient(context.applicationContext) }
    val coroutineScope = rememberCoroutineScope()
    val activeDeckPage = deckPages.firstOrNull { it.id == activeDeckPageId } ?: deckPages.first()
    val activeButtonMode = if (page == AppPage.ConsoleLayoutEditor || deckUiMode == DeckUiMode.Console) {
        DeckUiMode.Console
    } else {
        DeckUiMode.Classic
    }
    val deckButtons = activeDeckPage.buttonsForMode(activeButtonMode)
    val activeConsoleButtons = activeDeckPage.buttonsForMode(DeckUiMode.Console)
    val allDeckButtons = deckPages.flatMap { it.classicButtons + it.consoleButtons }
    val latestAllDeckButtons by rememberUpdatedState(allDeckButtons)
    val codexTaskStates = remember { mutableStateMapOf<Int, CodexButtonTaskState>() }
    val codexCommandFailures = remember { mutableStateMapOf<Int, String>() }
    val codexSubmittingButtons = remember { mutableStateMapOf<Int, Boolean>() }
    val codexTaskCoordinator = remember(companionClient, coroutineScope) {
        CodexButtonTaskCoordinator(
            scope = coroutineScope,
            taskStates = codexTaskStates,
            commandFailureCodes = codexCommandFailures,
            submittingButtons = codexSubmittingButtons,
            submitJob = { settings, binding ->
                withContext(Dispatchers.IO) {
                    runCatching { companionClient.submitCodexJob(settings, binding) }
                        .getOrElse { CodexJobApiResult(reconnectRequired = true) }
                }
            },
            pollJob = { settings, jobId, bindingId ->
                withContext(Dispatchers.IO) {
                    runCatching { companionClient.codexJobStatus(settings, jobId, bindingId) }
                        .getOrElse { CodexJobApiResult(reconnectRequired = true) }
                }
            },
            onAccepted = { buttonId, snapshot ->
                val buttonTitle = latestAllDeckButtons.firstOrNull { it.id == buttonId }?.title ?: "Codex"
                logs = listOf(
                    ActivityLog(
                        buttonTitle = buttonTitle,
                        payload = "codex exec.submit",
                        delivered = true,
                        note = if (snapshot.duplicate) "accepted existing job" else "accepted"
                    )
                ) + logs.take(9)
            },
            onFailure = { buttonId, safeCode ->
                val buttonTitle = latestAllDeckButtons.firstOrNull { it.id == buttonId }?.title ?: "Codex"
                logs = listOf(
                    ActivityLog(
                        buttonTitle = buttonTitle,
                        payload = "codex exec.submit",
                        delivered = false,
                        note = safeCode
                    )
                ) + logs.take(9)
            },
            onConnectionChanged = { connected ->
                companionStatus = companionStatus.copy(
                    connected = connected,
                    message = if (connected) "Companion connected" else "Companion reconnecting"
                )
            }
        )
    }
    val activeCodexBindings = allDeckButtons.mapNotNull { button ->
        if (button.actionType != DeckActionType.CompanionCommand) return@mapNotNull null
        CodexButtonBindingPayload.parse(button.payload)?.let { binding -> button.id to binding }
    }.toMap()

    LaunchedEffect(activeCodexBindings) {
        codexTaskCoordinator.retainBindings(activeCodexBindings)
    }

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
            if (activeTutorialMode == DeckUiMode.Classic && deckUiMode != DeckUiMode.Classic) {
                deckUiMode = DeckUiMode.Classic
                saveDeckUiMode(context, DeckUiMode.Classic)
            } else if (activeTutorialMode == DeckUiMode.Console && deckUiMode != DeckUiMode.Console) {
                deckUiMode = DeckUiMode.Console
                saveDeckUiMode(context, DeckUiMode.Console)
            }
            if (classicTutorialStep == SettingsTutorialStep.DeckSettingsButton) {
                val settingsPage = if (activeTutorialMode == DeckUiMode.Console) {
                    deckPages.firstOrNull()
                } else {
                    deckPages.firstOrNull { pageConfig ->
                        pageConfig.classicButtons.any { buttonAppAction(it) == DeckActionType.Settings }
                    }
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

    fun updateButtonEverywhere(button: DeckButton, mode: DeckUiMode = activeButtonMode) {
        val updatedPages = updateDeckButton(deckPages, button, mode)
        deckPages = updatedPages
        saveDeckPages(context, updatedPages)
    }

    fun assignWidgetToButton(buttonId: Int, widgetId: Int) {
        val info = appWidgetManager?.getAppWidgetInfo(widgetId)
        val sourcePage = deckPages.firstOrNull { pageConfig ->
            pageConfig.buttonsForMode(activeButtonMode).any { it.id == buttonId }
        }
        val pageButtons = sourcePage?.buttonsForMode(activeButtonMode) ?: return
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

    fun updateCompanionSettings(settings: CompanionSettings) {
        if (!BuildConfig.DEBUG) return
        codexTaskCoordinator.clear()
        companionSettings = settings
        saveCompanionSettings(context, settings)
        if (!settings.isConfigured()) {
            companionRouteActive = false
            companionStatus = CompanionConnectionStatus(message = "Companion is not configured")
        }
    }

    fun updateClassicCompanionConnectionExpanded(expanded: Boolean) {
        classicCompanionConnectionExpanded = expanded
        saveClassicCompanionConnectionExpanded(context, expanded)
    }

    fun updateCompanionSyncViewToPc(enabled: Boolean) {
        if (!BuildConfig.DEBUG) return
        companionSyncViewToPc = enabled
        saveCompanionSyncViewToPc(context, enabled)
        if (enabled && companionFollowPcView) {
            companionFollowPcView = false
            saveCompanionFollowPcView(context, false)
        }
    }

    fun updateCompanionFollowPcView(enabled: Boolean) {
        if (!BuildConfig.DEBUG) return
        companionFollowPcView = enabled
        saveCompanionFollowPcView(context, enabled)
        if (enabled && companionSyncViewToPc) {
            companionSyncViewToPc = false
            saveCompanionSyncViewToPc(context, false)
        }
    }

    fun applyLocalDeckUiMode(mode: DeckUiMode) {
        if (deckUiMode == mode) return
        if (BuildConfig.DEBUG && companionFollowPcView) {
            updateCompanionFollowPcView(false)
        }
        deckUiMode = mode
        saveDeckUiMode(context, mode)
    }

    fun applyCompanionStatus(result: CompanionApiResult) {
        val capabilities = result.data.optJSONArray("capabilities")?.let { array ->
            List(array.length()) { index -> array.optString(index) }.filter { it.isNotBlank() }.toSet()
        } ?: companionStatus.capabilities
        companionStatus = CompanionConnectionStatus(
            connected = result.ok,
            message = result.message,
            appName = result.data.optString("appName").ifBlank { companionStatus.appName },
            version = result.data.optString("version").ifBlank { companionStatus.version },
            capabilities = capabilities
        )
    }

    LaunchedEffect(Unit) {
        if (!BuildConfig.DEBUG) return@LaunchedEffect
        if (companionSyncViewToPc && companionFollowPcView) {
            updateCompanionFollowPcView(false)
        }
    }

    fun testCompanionConnection(settingsOverride: CompanionSettings = companionSettings) {
        if (!BuildConfig.DEBUG) return
        coroutineScope.launch {
            companionStatus = companionStatus.copy(connected = false, message = "Checking Companion...")
            val result = withContext(Dispatchers.IO) {
                runCatching { companionClient.status(settingsOverride) }
                    .getOrElse { error -> CompanionApiResult(false, error.message ?: "Companion check failed") }
            }
            applyCompanionStatus(result)
            logs = listOf(
                ActivityLog(
                    buttonTitle = "Companion",
                    payload = settingsOverride.endpoint,
                    delivered = result.ok,
                    note = if (result.ok) "connected" else result.message.ifBlank { "connection failed" }
                )
            ) + logs.take(9)
        }
    }

    fun applyCompanionPairingQr(rawValue: String) {
        if (!BuildConfig.DEBUG) return
        val parsed = parseCompanionPairingQr(rawValue).getOrElse { error ->
            companionStatus = CompanionConnectionStatus(
                connected = false,
                message = error.message ?: "Invalid Companion QR"
            )
            logs = listOf(
                ActivityLog(
                    buttonTitle = "Companion",
                    payload = "QR",
                    delivered = false,
                    note = error.message ?: "invalid pairing QR"
                )
            ) + logs.take(9)
            return
        }
        updateCompanionSettings(parsed)
        companionStatus = CompanionConnectionStatus(message = "Companion QR pairing saved")
        logs = listOf(
            ActivityLog(
                buttonTitle = "Companion",
                payload = parsed.endpoint,
                delivered = true,
                note = "QR pairing saved"
            )
        ) + logs.take(9)
        testCompanionConnection(parsed)
    }

    fun scanCompanionQr() {
        if (!BuildConfig.DEBUG) return
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        GmsBarcodeScanning.getClient(context, options)
            .startScan()
            .addOnSuccessListener { barcode ->
                applyCompanionPairingQr(barcode.rawValue.orEmpty())
            }
            .addOnFailureListener { error ->
                companionStatus = CompanionConnectionStatus(
                    connected = false,
                    message = error.message ?: "Companion QR scan failed"
                )
            }
    }

    LaunchedEffect(pendingCompanionPairingUri) {
        if (!BuildConfig.DEBUG) return@LaunchedEffect
        val uri = pendingCompanionPairingUri ?: return@LaunchedEffect
        applyCompanionPairingQr(uri)
        onCompanionPairingUriConsumed()
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
        codexTaskCoordinator.clear()
        val importedPages = ensureSettingsButton(imported.pages, darkTheme).ifEmpty {
            defaultDeckPages(darkTheme = darkTheme)
        }
        val nextActivePageId = importedPages.first().id
        val importedSidebarFraction = (imported.consoleLayouts[nextActivePageId] ?: imported.consoleLayouts.values.firstOrNull())
            ?.sidebarFraction
            ?.coerceIn(CONSOLE_MIN_SIDEBAR_FRACTION, CONSOLE_MAX_SIDEBAR_FRACTION)
            ?: consoleSidebarFraction
        val importedLayouts = imported.consoleLayouts
            .filterKeys { pageId -> importedPages.any { it.id == pageId } }
            .mapValues { (_, layout) -> layout.copy(sidebarFraction = importedSidebarFraction) }
        val nextConsoleLayout = (importedLayouts[nextActivePageId] ?: defaultConsoleLayout(importedPages.first().consoleButtons))
            .copy(sidebarFraction = importedSidebarFraction)

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
        consoleSidebarFraction = importedSidebarFraction
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

    suspend fun uploadCurrentDeckToCompanion(settings: CompanionSettings = companionSettings): CompanionApiResult {
        if (!BuildConfig.DEBUG) return CompanionApiResult(false, "Companion is disabled")
        return withContext(Dispatchers.IO) {
            runCatching {
                val bundle = JSONObject(context.createDeckBundleJson(currentDeckBundleSnapshot()))
                companionClient.updateMobileDeckBundle(settings, bundle)
            }.getOrElse { error ->
                CompanionApiResult(false, error.message ?: "Companion deck upload failed")
            }
        }
    }

    suspend fun applyCompanionBundle(bundle: JSONObject): Boolean {
        return runCatching {
            applyImportedDeckBundle(context.importDeckBundleJson(bundle.toString()))
        }.fold(
            onSuccess = { true },
            onFailure = { error ->
                companionStatus = CompanionConnectionStatus(
                    connected = false,
                    message = error.message ?: "Could not apply Companion deck"
                )
                false
            }
        )
    }

    fun sendCurrentDeckToCompanion() {
        if (!BuildConfig.DEBUG) return
        coroutineScope.launch {
            companionStatus = companionStatus.copy(message = "Sending deck to Companion...")
            val result = uploadCurrentDeckToCompanion()
            applyCompanionStatus(result)
            logs = listOf(
                ActivityLog(
                    buttonTitle = "Companion",
                    payload = "mobiledeck.bundle.update",
                    delivered = result.ok,
                    note = if (result.ok) "deck sent to PC" else result.message.ifBlank { "deck upload failed" }
                )
            ) + logs.take(9)
        }
    }

    fun applyCompanionDeckToAndroid() {
        if (!BuildConfig.DEBUG) return
        coroutineScope.launch {
            companionStatus = companionStatus.copy(message = "Applying deck from Companion...")
            val result = withContext(Dispatchers.IO) {
                runCatching { companionClient.getMobileDeckBundle(companionSettings) }
                    .getOrElse { error -> CompanionApiResult(false, error.message ?: "Companion deck download failed") }
            }
            val applied = if (result.ok) {
                applyCompanionBundle(result.data.getJSONObject("bundle"))
            } else {
                false
            }
            if (result.ok && applied) {
                applyCompanionStatus(result)
            } else if (!result.ok) {
                applyCompanionStatus(result)
            }
            logs = listOf(
                ActivityLog(
                    buttonTitle = "Companion",
                    payload = "mobiledeck.bundle.get",
                    delivered = result.ok && applied,
                    note = when {
                        result.ok && applied -> "deck applied from PC"
                        result.ok -> companionStatus.message.ifBlank { "deck apply failed" }
                        else -> result.message.ifBlank { "deck download failed" }
                    }
                )
            ) + logs.take(9)
        }
    }

    LaunchedEffect(companionSettings.enabled, companionSettings.endpoint, companionSettings.pairingToken) {
        if (!companionSettings.isConfigured()) return@LaunchedEffect
        companionStatus = companionStatus.copy(message = "Checking Companion...")
        val result = withContext(Dispatchers.IO) {
            runCatching { companionClient.status(companionSettings) }
                .getOrElse { error -> CompanionApiResult(false, error.message ?: "Companion check failed") }
        }
        applyCompanionStatus(result)
    }

    LaunchedEffect(companionStatus.connected) {
        if (!BuildConfig.DEBUG) return@LaunchedEffect
        companionRouteActive = companionStatus.connected
    }

    LaunchedEffect(
        companionStatus.connected,
        companionSettings.enabled,
        companionSettings.endpoint,
        companionSettings.pairingToken,
        companionSyncViewToPc,
        activeDeckPageId,
        deckUiMode
    ) {
        if (!BuildConfig.DEBUG) return@LaunchedEffect
        if (!companionSyncViewToPc || !companionStatus.connected || !companionSettings.isConfigured()) return@LaunchedEffect
        if (suppressNextViewSyncUpload) {
            suppressNextViewSyncUpload = false
            return@LaunchedEffect
        }
        delay(180)
        val result = withContext(Dispatchers.IO) {
            runCatching {
                companionClient.updateMobileDeckView(companionSettings, activeDeckPageId, deckUiMode)
            }.getOrElse { error -> CompanionApiResult(false, error.message ?: "Companion view sync failed") }
        }
        if (!result.ok) applyCompanionStatus(result)
    }

    LaunchedEffect(
        companionStatus.connected,
        companionSettings.enabled,
        companionSettings.endpoint,
        companionSettings.pairingToken,
        deckPages,
        consoleLayouts,
        consoleLayout,
        deckColumns,
        deckRows,
        deckSpacing,
        pageSwipeAxis,
        pageSwipeMode,
        pageSwipeAnimation,
        infinitePageSwipe,
        buttonVibrationLevel,
        classicSolidButtonBackground,
        classicDeckBackground,
        deckUiMode,
        classicFontSize,
        consoleFontSize,
        consolePanelOptions
    ) {
        if (!BuildConfig.DEBUG) return@LaunchedEffect
        if (!companionStatus.connected || !companionSettings.isConfigured()) return@LaunchedEffect
        if (skipNextViewOnlyBundleUpload) {
            skipNextViewOnlyBundleUpload = false
            return@LaunchedEffect
        }
        delay(250)
        val result = uploadCurrentDeckToCompanion()
        if (!result.ok) {
            applyCompanionStatus(result)
        }
    }

    LaunchedEffect(
        companionStatus.connected,
        companionSettings.enabled,
        companionSettings.endpoint,
        companionSettings.pairingToken
    ) {
        if (!BuildConfig.DEBUG) return@LaunchedEffect
        if (!companionStatus.connected || !companionSettings.isConfigured()) return@LaunchedEffect
        while (true) {
            delay(1200)
            val result = withContext(Dispatchers.IO) {
                runCatching { companionClient.syncPending(companionSettings) }
                    .getOrElse { error -> CompanionApiResult(false, error.message ?: "Companion sync failed") }
            }
            if (!result.ok) {
                applyCompanionStatus(result)
                continue
            }
            val pendingBundle = result.data.optJSONObject("bundle")
            if (result.data.optBoolean("hasPendingBundle", false) && pendingBundle != null) {
                val applied = applyCompanionBundle(pendingBundle)
                if (applied) {
                    val uploadResult = uploadCurrentDeckToCompanion()
                    if (!uploadResult.ok) {
                        applyCompanionStatus(uploadResult)
                    }
                    logs = listOf(
                        ActivityLog(
                            buttonTitle = "Companion",
                            payload = "mobiledeck.sync.pending",
                            delivered = uploadResult.ok,
                            note = if (uploadResult.ok) "PC deck applied" else uploadResult.message.ifBlank { "sync upload failed" }
                        )
                    ) + logs.take(9)
                }
            }
            if (companionFollowPcView && result.data.optBoolean("hasPendingViewState", false)) {
                result.data.optJSONObject("viewState")?.let { viewState ->
                    val nextPageId = viewState.optInt("activePageId", activeDeckPageId)
                    val nextMode = runCatching {
                        DeckUiMode.valueOf(viewState.optString("deckUiMode", deckUiMode.name))
                    }.getOrDefault(deckUiMode)
                    val pageChanged = deckPages.any { it.id == nextPageId } && nextPageId != activeDeckPageId
                    val modeChanged = nextMode != deckUiMode
                    if (pageChanged || modeChanged) {
                        suppressNextViewSyncUpload = true
                        skipNextViewOnlyBundleUpload = modeChanged
                        if (pageChanged) activeDeckPageId = nextPageId
                        if (modeChanged) deckUiMode = nextMode
                    }
                }
            }
        }
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
            id = nextDeckButtonId(allDeckButtons),
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
                buttons = listOf(newButton.copy(position = 0)),
                classicButtons = if (activeButtonMode == DeckUiMode.Classic) {
                    listOf(newButton.copy(position = 0))
                } else {
                    emptyList()
                },
                consoleButtons = if (activeButtonMode == DeckUiMode.Console) {
                    listOf(newButton.copy(position = 0))
                } else {
                    emptyList()
                }
            )
            createdPageId = newPage.id
            activeDeckPageId = newPage.id
            deckPages + newPage
        } else {
            updateDeckPage(deckPages, activeDeckPage.id, activeButtonMode) {
                it.buttonsForMode(activeButtonMode) + newButton
            }
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
            activeConsoleButtons.filterNot { buttonAppAction(it) == DeckActionType.Settings },
            DEFAULT_COLUMNS,
            DEFAULT_ROWS
        ).ifEmpty { listOf(emptyList()) }
        val safeRowIndex = rowIndex.coerceIn(visibleRows.indices)
        val colors = defaultDeckColors(darkTheme)
        val newButton = DeckButton(
            id = nextDeckButtonId(allDeckButtons),
            title = "Explorer",
            subtitle = "Win+E",
            icon = ICON_AUTO,
            iconImageUri = "",
            displayMode = DeckDisplayMode.IconAndText,
            actionType = DeckActionType.Hotkey,
            payload = "WIN+E",
            color = colors[activeConsoleButtons.size % colors.size],
            position = (activeConsoleButtons.maxOfOrNull { it.position } ?: -1) + 1
        )
        val updatedPages = updateDeckPage(deckPages, activeDeckPage.id, DeckUiMode.Console) { pageConfig ->
            pageConfig.consoleButtons + newButton
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

    fun consoleThreeByThreeLayout(buttons: List<DeckButton>, sidebarFraction: Float = consoleSidebarFraction): ConsoleLayoutConfig {
        return ConsoleLayoutConfig(
            rows = buttons.take(9).chunked(3).map { row -> row.map { it.id } },
            rowWeights = List(3) { 1f },
            sidebarFraction = sidebarFraction
        )
    }

    fun saveConsoleLayoutForPage(pageId: Int, layout: ConsoleLayoutConfig) {
        val sidebarFraction = layout.sidebarFraction
            .coerceIn(CONSOLE_MIN_SIDEBAR_FRACTION, CONSOLE_MAX_SIDEBAR_FRACTION)
        consoleSidebarFraction = sidebarFraction
        val syncedLayout = layout.copy(sidebarFraction = sidebarFraction)
        val updatedLayouts = (consoleLayouts + (pageId to syncedLayout)).mapValues { (_, value) ->
            value.copy(sidebarFraction = sidebarFraction)
        }
        consoleLayouts = updatedLayouts
        saveConsoleLayouts(context, updatedLayouts)
        if (pageId == activeDeckPageId) {
            consoleLayout = syncedLayout
            saveConsoleLayout(context, syncedLayout)
        }
    }

    fun removeConsoleLayoutForPage(pageId: Int) {
        val updatedLayouts = consoleLayouts - pageId
        consoleLayouts = updatedLayouts
        saveConsoleLayouts(context, updatedLayouts)
    }

    fun consoleLayoutForPage(pageConfig: DeckPageConfig): ConsoleLayoutConfig {
        return (consoleLayouts[pageConfig.id] ?: defaultConsoleLayout(pageConfig.consoleButtons))
            .copy(sidebarFraction = consoleSidebarFraction)
    }

    fun ensureUniqueConsoleLayoutSlots() {
        val visibleButtons = activeConsoleButtons.filterNot { buttonAppAction(it) == DeckActionType.Settings }
        val buttonById = visibleButtons.associateBy { it.id }
        val rows = consoleLayoutRowIds(consoleLayout, visibleButtons, DEFAULT_COLUMNS, DEFAULT_ROWS)
        val seenIds = mutableSetOf<Int>()
        var nextId = nextDeckButtonId(allDeckButtons)
        var nextPosition = (activeConsoleButtons.maxOfOrNull { it.position } ?: -1) + 1
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
        val updatedPages = updateDeckPage(deckPages, activeDeckPage.id, DeckUiMode.Console) { pageConfig ->
            pageConfig.consoleButtons + clonedButtons
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

    LaunchedEffect(page, activeDeckPageId, consoleLayout.rows, activeConsoleButtons) {
        if (page == AppPage.ConsoleLayoutEditor) {
            ensureUniqueConsoleLayoutSlots()
        }
    }

    fun saveConsoleLayoutForActivePage(updated: ConsoleLayoutConfig, removeEmptyRows: Boolean = false) {
        val activeButtonIds = activeConsoleButtons.map { it.id }.toSet()
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
                startId = nextDeckButtonId(allDeckButtons),
                startPosition = 0,
                count = 9
            )
        } else {
            emptyList()
        }
        val newPage = DeckPageConfig(
            id = nextDeckPageId(deckPages),
            name = "Page $nextPageNumber",
            buttons = if (consolePage) emptyList() else newButtons,
            classicButtons = if (consolePage) emptyList() else newButtons,
            consoleButtons = if (consolePage) newButtons else emptyList()
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
        val settingsButtons = firstPage.classicButtons
            .filter { buttonAppAction(it) == DeckActionType.Settings }
            .ifEmpty { normalizeDeckButtons(emptyList(), darkTheme) }
            .mapIndexed { index, button -> button.copy(position = index) }
        val updatedPages = deckPages.map { page ->
            if (page.id == firstPage.id) page.withButtonsForMode(DeckUiMode.Classic, settingsButtons) else page
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
            pageConfig.buttonsForMode(activeButtonMode).any { existing -> existing.id == button.id }
        }?.id
        val updatedPages = deckPages.map { pageConfig ->
            pageConfig.withButtonsForMode(
                activeButtonMode,
                pageConfig.buttonsForMode(activeButtonMode).filterNot { existing -> existing.id == button.id }
            )
        }
        deckPages = updatedPages
        saveDeckPages(context, updatedPages)
        editingButton = null
        val firstPageId = updatedPages.firstOrNull()?.id
        val deletedPage = updatedPages.firstOrNull { it.id == deletedPageId }
        if (page == AppPage.LayoutEditor &&
            deletedPage != null &&
            deletedPage.id != firstPageId &&
            deletedPage.classicButtons.isEmpty()
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
            pageConfig.withButtonsForMode(
                activeButtonMode,
                pageConfig.buttonsForMode(activeButtonMode).filterNot { existing -> existing.id == button.id }
            )
        }
        val currentLayout = consoleLayout.copy(
            rows = consoleLayout.rows.map { row -> row.filterNot { it == button.id } }
        )
        val updatedPages = if (createdPageId != null && currentPages.size > 1) {
            currentPages.filterNot { pageConfig ->
                pageConfig.id == createdPageId && pageConfig.buttonsForMode(activeButtonMode).isEmpty()
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
            DeckActionType.CompanionCommand -> false
            DeckActionType.CompanionControl -> false
            DeckActionType.CompanionStatus -> false
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

    fun buttonUsesCompanionFirstRoute(button: DeckButton): Boolean {
        return when (button.actionType) {
            DeckActionType.Hotkey,
            DeckActionType.MediaKey,
            DeckActionType.Text,
            DeckActionType.RunCommand,
            DeckActionType.CompanionCommand -> true
            DeckActionType.CompanionControl -> button.usesCompanionControlRoute()
            else -> false
        }
    }

    suspend fun sendDeckButtonToCompanion(button: DeckButton, payloadOverride: String): CompanionApiResult? {
        if (!BuildConfig.DEBUG) return null
        val control = if (button.actionType == DeckActionType.CompanionControl) {
            companionControlData(button)
        } else {
            null
        }
        val decision = button.executionDecision(
            companionAvailable = true,
            payloadOverride = payloadOverride,
            companionControlSource = control?.source?.ifBlank { button.payload } ?: button.payload,
            companionControlValue = control?.let { companionControlRequestValue(button, payloadOverride) }
        )
        return withContext(Dispatchers.IO) {
            runCatching {
                when (decision.route) {
                    DeckButtonExecutionRoute.CompanionOpen -> companionClient.open(companionSettings, payloadOverride)
                    DeckButtonExecutionRoute.CompanionProgramCommand -> companionClient.programCommand(
                        companionSettings,
                        payloadOverride
                    )
                    DeckButtonExecutionRoute.CompanionControlUpdate -> companionClient.controlUpdate(
                        companionSettings,
                        decision.source,
                        decision.value ?: return@runCatching CompanionApiResult(false, "Companion control value is missing")
                    )
                    DeckButtonExecutionRoute.AndroidOrHid -> when (button.actionType) {
                        DeckActionType.Hotkey,
                        DeckActionType.MediaKey -> companionClient.inputCommand(
                            companionSettings,
                            button.actionType.name,
                            payloadOverride
                        )
                        DeckActionType.Text -> companionClient.text(companionSettings, payloadOverride)
                        DeckActionType.RunCommand -> companionClient.open(companionSettings, payloadOverride)
                        else -> return@runCatching CompanionApiResult(false, "Unsupported Companion route")
                    }
                    DeckButtonExecutionRoute.ReadOnly,
                    DeckButtonExecutionRoute.Unavailable -> return@runCatching CompanionApiResult(
                        false,
                        "Companion route is unavailable"
                    )
                }
            }.getOrElse { error ->
                CompanionApiResult(false, error.message ?: "Companion transport failed")
            }
        }
    }

    suspend fun performDeckButtonActionAsync(button: DeckButton, payloadOverride: String = button.payload): Boolean {
        val companionAvailable = BuildConfig.DEBUG && companionStatus.connected && companionSettings.isConfigured()
        when (button.executionDecision(companionAvailable, payloadOverride).route) {
            DeckButtonExecutionRoute.Unavailable -> {
                companionStatus = CompanionConnectionStatus(message = "Companion is not connected")
                return false
            }
            DeckButtonExecutionRoute.ReadOnly -> return false
            else -> Unit
        }

        if (companionAvailable && buttonUsesCompanionFirstRoute(button)) {
            val companionResult = sendDeckButtonToCompanion(button, payloadOverride)
            if (companionResult != null) {
                applyCompanionStatus(companionResult)
                return companionResult.ok
            }
        }

        if (button.platformAvailability() == DeckButtonPlatformAvailability.CompanionRequired) return false
        return performDeckButtonAction(button, payloadOverride)
    }

    fun logDeckButtonAction(button: DeckButton, payload: String, delivered: Boolean) {
        val note = when {
            buttonAppAction(button.actionType, payload) == DeckActionType.Settings -> "opened settings"
            buttonAppAction(button.actionType, payload) == DeckActionType.BluetoothStatus -> "opened connection"
            buttonAppAction(button.actionType, payload) == DeckActionType.PreviousPage -> "previous page"
            buttonAppAction(button.actionType, payload) == DeckActionType.NextPage -> "next page"
            delivered -> "sent"
            button.isCompanionOnly() -> "companion unavailable"
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

    fun logCodexRouteRejection(button: DeckButton, note: String) {
        logs = listOf(
            ActivityLog(
                buttonTitle = button.title,
                payload = "codex exec.submit",
                delivered = false,
                note = note
            )
        ) + logs.take(9)
    }

    fun pressDeckButton(button: DeckButton) {
        if (button.actionType == DeckActionType.CompanionCommand) {
            val strictBinding = CodexButtonBindingPayload.parse(button.payload)
            if (strictBinding != null) {
                val releaseBinding = CompanionReleaseRoutePolicy.bindingForSubmit(
                    companionSettings,
                    button.payload
                )
                if (releaseBinding == null) {
                    logCodexRouteRejection(button, "codex route disabled")
                    return
                }
                codexTaskCoordinator.submitOnce(button.id, companionSettings, releaseBinding)
                return
            }
            if (!BuildConfig.DEBUG) {
                logCodexRouteRejection(button, "invalid_request")
                return
            }
        }
        coroutineScope.launch {
            val delivered = performDeckButtonActionAsync(button)
            logDeckButtonAction(button, button.payload, delivered)
        }
    }

    fun pressAnalogStick(button: DeckButton, value: JSONObject) {
        if (button.controlStyle != DeckControlStyle.AnalogStick) return
        val control = companionControlData(button)
        val valueWithSettings = JSONObject(value.toString()).apply {
            put("deadZone", control.deadZone.toDouble())
        }
        coroutineScope.launch {
            val delivered = if (BuildConfig.DEBUG && companionStatus.connected && companionSettings.isConfigured()) {
                val result = withContext(Dispatchers.IO) {
                    runCatching {
                        companionClient.controlUpdate(
                            companionSettings,
                            control.source.ifBlank { button.payload },
                            valueWithSettings
                        )
                    }.getOrElse { error ->
                        CompanionApiResult(false, error.message ?: "Companion transport failed")
                    }
                }
                applyCompanionStatus(result)
                result.ok
            } else {
                withContext(Dispatchers.IO) {
                    hidManager.sendJoystickState(
                        x = valueWithSettings.optDouble("x", 0.0),
                        y = valueWithSettings.optDouble("y", 0.0)
                    )
                }
            }
            if (!valueWithSettings.optBoolean("active", false)) {
                logDeckButtonAction(button, valueWithSettings.toString(), delivered)
            }
        }
    }

    fun pressAnalogStick(button: DeckButton, point: AnalogStickPoint) {
        pressAnalogStick(button, analogStickValueForPoint(point, companionControlData(button).deadZone))
    }

    fun pressTrimButton(button: DeckButton, step: Int) {
        if (button.controlStyle == DeckControlStyle.JoyPad) {
            val payloads = joyPadCardinalPayloadsForStep(button, step)
            coroutineScope.launch {
                val delivered = withContext(Dispatchers.IO) {
                    if (payloads.isEmpty()) {
                        hidManager.releaseKeyboardKeys()
                    } else {
                        hidManager.sendHotkeyState(payloads)
                    }
                }
                logDeckButtonAction(button, payloads.joinToString("+").ifBlank { "release" }, delivered)
            }
            return
        }
        if (button.controlStyle == DeckControlStyle.AnalogStick) {
            pressAnalogStick(button, analogStickValueForStep(step))
            return
        }
        if (step == 0 && button.controlStyle != DeckControlStyle.AnalogStick) return
        val payload = controlPayloadForStep(button, step)
        if (payload.isBlank()) return
        var delivered = false
        val repeatCount = 1
        coroutineScope.launch {
            repeat(repeatCount) {
                delivered = performDeckButtonActionAsync(button, payload) || delivered
            }
            logDeckButtonAction(button, payload, delivered)
        }
    }

    fun moveDeckButtonToSlot(button: DeckButton, targetPosition: Int) {
        val currentPage = deckPages.firstOrNull { it.id == activeDeckPageId } ?: return
        val currentButtons = currentPage.buttonsForMode(activeButtonMode)
        val currentButton = currentButtons.firstOrNull { it.id == button.id } ?: return
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
        val updatedPages = updateDeckPage(deckPages, currentPage.id, activeButtonMode) {
            updatedButtons
        }
        deckPages = updatedPages
        saveDeckPages(context, updatedPages)
    }

    DisposableEffect(hidManager) {
        onDispose { hidManager.stop() }
    }
    DisposableEffect(companionClient) {
        onDispose {
            codexTaskCoordinator.clear()
            companionClient.close()
        }
    }

    val appColors = deckThemeColors(deckUiMode, darkTheme)
    val bluetoothConnected = hidStatus.state == HidConnectionState.Connected
    val companionConnected = BuildConfig.DEBUG && companionStatus.connected && companionSettings.isConfigured()
    val codexCompanionConfigured = companionSettings.isConfigured()
    val codexCompanionConnected = companionStatus.connected && codexCompanionConfigured
    val companionControlMode = when {
        companionConnected -> CompanionControlMode.CompanionActive
        bluetoothConnected -> CompanionControlMode.HidOnly
        else -> CompanionControlMode.Disconnected
    }
    val displayHidStatus = if (companionControlMode == CompanionControlMode.CompanionActive) {
        HidStatus(HidConnectionState.Connected, "Companion active")
    } else {
        hidStatus
    }
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
                    status = displayHidStatus,
                    appWidgetHost = appWidgetHost,
                    appWidgetManager = appWidgetManager,
                    uiMode = deckUiMode,
                    consoleLayout = consoleLayout,
                    consolePanelOptions = consolePanelOptions,
                    onConsoleSettings = { page = AppPage.Settings },
                    classicSolidButtonBackground = classicSolidButtonBackground,
                    classicDeckBackground = classicDeckBackground,
                    companionConnected = companionConnected,
                    codexTaskStates = codexTaskStates,
                    codexCommandFailures = codexCommandFailures,
                    codexSubmittingButtons = codexSubmittingButtons,
                    codexCompanionConfigured = codexCompanionConfigured,
                    codexCompanionConnected = codexCompanionConnected,
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
                    onAnalogValue = ::pressAnalogStick,
                    onButtonTouchStarted = { context.applicationContext.vibrateButtonPress(buttonVibrationLevel) },
                    onButtonTouchEnded = { context.applicationContext.vibrateButtonPress(buttonVibrationLevel) },
                    onButtonEdit = {},
                    onButtonMoved = ::moveDeckButtonToSlot,
                    onEmptySlotPressed = { slot -> addDeckButton(slot, editAfterCreate = true) }
                )
                if (showClassicTutorial && classicTutorialStep == SettingsTutorialStep.DeckSettingsButton) {
                    val dismissTutorial = {
                        showClassicTutorial = false
                        classicTutorialStep = SettingsTutorialStep.PcConnection
                        page = AppPage.Settings
                        saveClassicTutorialSeen(context)
                    }
                    if (activeTutorialMode == DeckUiMode.Console) {
                        ConsoleDeckSettingsButtonTutorialOverlay(
                            modifier = Modifier.fillMaxSize(),
                            onDismiss = dismissTutorial
                        )
                    } else {
                        ClassicDeckSettingsButtonTutorialOverlay(
                            modifier = Modifier.fillMaxSize(),
                            settingsButton = activeDeckPage.classicButtons.firstOrNull { buttonAppAction(it) == DeckActionType.Settings },
                            columns = deckColumns,
                            rows = deckRows,
                            spacing = deckSpacing.dp,
                            showTitle = activeDeckPage.id == deckPages.firstOrNull()?.id,
                            pageSwipeAxis = pageSwipeAxis,
                            onDismiss = dismissTutorial
                        )
                    }
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
                    showGuideCards = guideCardsVisible,
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
                showGuideCards = guideCardsVisible,
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
                        activeConsoleButtons.filterNot { buttonAppAction(it) == DeckActionType.Settings },
                        DEFAULT_COLUMNS,
                        DEFAULT_ROWS
                    )
                    val insertIndex = targetRowIndex.coerceIn(0, rows.size)
                    addConsoleLayoutDiagnostic("add row clicked targetRow=${insertIndex + 1} rows=${rows.size}/$MAX_CONSOLE_LAYOUT_ROWS raw=${consoleLayout.rows.size}")
                    if (rows.size < MAX_CONSOLE_LAYOUT_ROWS) {
                        val newButtons = consolePlaceholderButtons(
                            startId = nextDeckButtonId(allDeckButtons),
                            startPosition = activeConsoleButtons.size,
                            count = 3
                        )
                        val updatedPages = updateDeckPage(deckPages, activeDeckPage.id, DeckUiMode.Console) { pageConfig ->
                            pageConfig.consoleButtons + newButtons
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
                        activeConsoleButtons.filterNot { buttonAppAction(it) == DeckActionType.Settings },
                        DEFAULT_COLUMNS,
                        DEFAULT_ROWS
                    )
                    addConsoleLayoutDiagnostic("remove row clicked row=${rowIndex + 1} rows=${rows.size}")
                    if (rowIndex in rows.indices) {
                        val removedButtonIds = rows[rowIndex].toSet()
                        val updatedPages = updateDeckPage(deckPages, activeDeckPage.id, DeckUiMode.Console) { pageConfig ->
                            pageConfig.consoleButtons.filterNot { it.id in removedButtonIds }
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
                        activeConsoleButtons.filterNot { buttonAppAction(it) == DeckActionType.Settings },
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
                    val updated = defaultConsoleLayout(deckButtons).copy(sidebarFraction = consoleSidebarFraction)
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
                        activeConsoleButtons.filterNot { buttonAppAction(it) == DeckActionType.Settings },
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
                            val updatedPages = updateDeckPage(deckPages, activeDeckPage.id, DeckUiMode.Console) { pageConfig ->
                                pageConfig.consoleButtons.filterNot { it.id == buttonId }
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
                        activeConsoleButtons.filterNot { buttonAppAction(it) == DeckActionType.Settings },
                        DEFAULT_COLUMNS,
                        DEFAULT_ROWS
                    )
                    val targetInsertIndex = if (delta > 0) {
                        fromIndex + delta + 1
                    } else {
                        fromIndex + delta
                    }
                    addConsoleLayoutDiagnostic("move button row=${rowIndex + 1} from=${fromIndex + 1} insert=${targetInsertIndex + 1}")
                    val insertResult = insertConsoleRowItem(
                        rows = rows,
                        fromRowIndex = rowIndex,
                        fromIndex = fromIndex,
                        toRowIndex = rowIndex,
                        toIndex = targetInsertIndex
                    )
                    if (insertResult != null) {
                        val updated = consoleLayout.copy(rows = insertResult.rows)
                        saveConsoleLayoutForActivePage(updated)
                        addConsoleLayoutDiagnostic("move button inserted index=${insertResult.insertedIndex + 1}")
                    }
                },
                onMoveButtonTo = { fromRowIndex, fromIndex, toRowIndex, toIndex ->
                    val rows = consoleLayoutRowIds(
                        consoleLayout,
                        activeConsoleButtons.filterNot { buttonAppAction(it) == DeckActionType.Settings },
                        DEFAULT_COLUMNS,
                        DEFAULT_ROWS
                    )
                    val movingButtonId = rows.getOrNull(fromRowIndex)?.getOrNull(fromIndex)
                    val insertResult = insertConsoleRowItem(
                        rows = rows,
                        fromRowIndex = fromRowIndex,
                        fromIndex = fromIndex,
                        toRowIndex = toRowIndex,
                        toIndex = toIndex
                    )
                    if (insertResult != null && movingButtonId != null) {
                        val updated = consoleLayout.copy(rows = insertResult.rows)
                        saveConsoleLayoutForActivePage(updated)
                        addConsoleLayoutDiagnostic("insert button id=$movingButtonId from=${fromRowIndex + 1}:${fromIndex + 1} to=${toRowIndex + 1}:${insertResult.insertedIndex + 1}")
                    }
                },
                onEditButton = { button ->
                    addConsoleLayoutDiagnostic("edit button id=${button.id} title=${button.title}")
                    editingButton = button
                }
            )

            AppPage.IconStyleTest -> {
                if (BuildConfig.DEBUG) {
                    ButtonShapeStudyPage(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        initialPageIndex = debugLaunch.studyPageIndex ?: 0,
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
                activeTutorialMode = activeTutorialMode,
                classicTutorialStep = classicTutorialStep,
                debugKeepScreenOn = debugKeepScreenOn,
                companionSettings = companionSettings,
                companionStatus = companionStatus,
                companionRouteActive = companionRouteActive,
                companionSyncViewToPc = companionSyncViewToPc,
                companionFollowPcView = companionFollowPcView,
                classicCompanionConnectionExpanded = classicCompanionConnectionExpanded,
                guideCardsVisible = guideCardsVisible,
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
                    applyLocalDeckUiMode(mode)
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
                    activeTutorialMode = deckUiMode
                    classicTutorialStep = SettingsTutorialStep.PcConnection
                    showClassicTutorial = true
                },
                onDebugKeepScreenOnChange = { enabled ->
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    debugKeepScreenOn = enabled
                },
                onGuideCardsVisibleChange = { enabled ->
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    guideCardsVisible = enabled
                    saveGuideCards(context, enabled)
                },
                onCompanionSettingsChange = { settings ->
                    updateCompanionSettings(settings)
                },
                onCompanionSyncViewToPcChange = { enabled ->
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    updateCompanionSyncViewToPc(enabled)
                },
                onCompanionFollowPcViewChange = { enabled ->
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    updateCompanionFollowPcView(enabled)
                },
                onCompanionRouteActiveChange = { active ->
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    companionRouteActive = companionStatus.connected
                },
                onClassicCompanionConnectionExpandedChange = { expanded ->
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    updateClassicCompanionConnectionExpanded(expanded)
                },
                onTestCompanion = {
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    testCompanionConnection()
                },
                onScanCompanionQr = {
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    scanCompanionQr()
                },
                onSendDeckToCompanion = {
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    sendCurrentDeckToCompanion()
                },
                onApplyDeckFromCompanion = {
                    context.applicationContext.vibrateButtonPress(buttonVibrationLevel)
                    applyCompanionDeckToAndroid()
                },
                onDismissClassicTutorial = {
                    showClassicTutorial = false
                    activeTutorialMode = DeckUiMode.Classic
                    classicTutorialStep = SettingsTutorialStep.PcConnection
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
            showGuideCards = guideCardsVisible,
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
                    activeDeckPage.buttonsForMode(activeButtonMode).filterNot { it.id == updated.id },
                    deckColumns,
                    deckRows,
                    showTitle
                )
                val updatedPages = updateDeckButton(deckPages, adjustedButton, activeButtonMode)
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
    activeTutorialMode: DeckUiMode,
    classicTutorialStep: SettingsTutorialStep,
    debugKeepScreenOn: Boolean,
    companionSettings: CompanionSettings,
    companionStatus: CompanionConnectionStatus,
    companionRouteActive: Boolean,
    companionSyncViewToPc: Boolean,
    companionFollowPcView: Boolean,
    classicCompanionConnectionExpanded: Boolean,
    guideCardsVisible: Boolean,
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
    onGuideCardsVisibleChange: (Boolean) -> Unit,
    onCompanionSettingsChange: (CompanionSettings) -> Unit,
    onCompanionSyncViewToPcChange: (Boolean) -> Unit,
    onCompanionFollowPcViewChange: (Boolean) -> Unit,
    onCompanionRouteActiveChange: (Boolean) -> Unit,
    onClassicCompanionConnectionExpandedChange: (Boolean) -> Unit,
    onTestCompanion: () -> Unit,
    onScanCompanionQr: () -> Unit,
    onSendDeckToCompanion: () -> Unit,
    onApplyDeckFromCompanion: () -> Unit,
    onDismissClassicTutorial: () -> Unit,
    onClassicTutorialStepChange: (SettingsTutorialStep) -> Unit
) {
    val colors = deckThemeColors(deckUiMode, isSystemInDarkTheme())
    var consoleSettingsCategory by remember { mutableStateOf(ConsoleSettingsCategory.PcConnection) }
    val context = LocalContext.current
    LaunchedEffect(showClassicTutorial, classicTutorialStep, activeTutorialMode) {
        if (showClassicTutorial && activeTutorialMode == DeckUiMode.Console) {
            consoleSettingsCategory = consoleTutorialCategory(classicTutorialStep)
        }
    }
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
                companionSettings = companionSettings,
                companionStatus = companionStatus,
                companionRouteActive = companionRouteActive,
                companionSyncViewToPc = companionSyncViewToPc,
                companionFollowPcView = companionFollowPcView,
                classicCompanionConnectionExpanded = classicCompanionConnectionExpanded,
                onBack = onBack,
                onDeckUiModeChange = onDeckUiModeChange,
                onConsoleCategoryChange = { consoleSettingsCategory = it },
                onCompanionSettingsChange = onCompanionSettingsChange,
                onCompanionSyncViewToPcChange = onCompanionSyncViewToPcChange,
                onCompanionFollowPcViewChange = onCompanionFollowPcViewChange,
                onCompanionRouteActiveChange = onCompanionRouteActiveChange,
                onClassicCompanionConnectionExpandedChange = onClassicCompanionConnectionExpandedChange,
                onTestCompanion = onTestCompanion,
                onScanCompanionQr = onScanCompanionQr,
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
                        companionSettings = companionSettings,
                        companionStatus = companionStatus,
                        companionRouteActive = companionRouteActive,
                        companionSyncViewToPc = companionSyncViewToPc,
                        companionFollowPcView = companionFollowPcView,
                        showDetailGuides = guideCardsVisible,
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
                        onDebugKeepScreenOnChange = onDebugKeepScreenOnChange,
                        onShowDetailGuidesChange = onGuideCardsVisibleChange,
                        onCompanionSettingsChange = onCompanionSettingsChange,
                        onCompanionSyncViewToPcChange = onCompanionSyncViewToPcChange,
                        onCompanionFollowPcViewChange = onCompanionFollowPcViewChange,
                        onCompanionRouteActiveChange = onCompanionRouteActiveChange,
                        onTestCompanion = onTestCompanion,
                        onScanCompanionQr = onScanCompanionQr,
                        onSendDeckToCompanion = onSendDeckToCompanion,
                        onApplyDeckFromCompanion = onApplyDeckFromCompanion
                    )
                } else {
                    ClassicSettingsContent(
                        status = status,
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
                        pairedHosts = pairedHosts,
                        pairingDiscoverable = pairingDiscoverable,
                        logs = logs,
                        debugKeepScreenOn = debugKeepScreenOn,
                        companionSettings = companionSettings,
                        companionStatus = companionStatus,
                        companionRouteActive = companionRouteActive,
                        companionSyncViewToPc = companionSyncViewToPc,
                        companionFollowPcView = companionFollowPcView,
                        showDetailGuides = guideCardsVisible,
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
                        onStart = onStart,
                        onStop = onStop,
                        onMakeDiscoverable = onMakeDiscoverable,
                        onCancelDiscoverable = onCancelDiscoverable,
                        onRefreshHosts = onRefreshHosts,
                        onConnectHost = onConnectHost,
                        onDebugKeepScreenOnChange = onDebugKeepScreenOnChange,
                        onShowDetailGuidesChange = onGuideCardsVisibleChange,
                        onCompanionSettingsChange = onCompanionSettingsChange,
                        onCompanionSyncViewToPcChange = onCompanionSyncViewToPcChange,
                        onCompanionFollowPcViewChange = onCompanionFollowPcViewChange,
                        onCompanionRouteActiveChange = onCompanionRouteActiveChange,
                        onTestCompanion = onTestCompanion,
                        onScanCompanionQr = onScanCompanionQr,
                        onSendDeckToCompanion = onSendDeckToCompanion,
                        onApplyDeckFromCompanion = onApplyDeckFromCompanion
                    )
                }
            }
        }
        if (showClassicTutorial && classicTutorialStep != SettingsTutorialStep.DeckSettingsButton) {
            SettingsTutorialOverlay(
                modifier = Modifier.fillMaxSize(),
                mode = activeTutorialMode,
                step = classicTutorialStep,
                onStepChange = onClassicTutorialStepChange,
                onDismiss = onDismissClassicTutorial
            )
        }
    }
}

private enum class ConsoleSettingsCategory {
    PcConnection,
    Layout,
    Background,
    Controls,
    App
}

private fun consoleTutorialCategory(step: SettingsTutorialStep): ConsoleSettingsCategory {
    return when (step) {
        SettingsTutorialStep.PcConnection -> ConsoleSettingsCategory.PcConnection
        SettingsTutorialStep.UiMode -> ConsoleSettingsCategory.App
        SettingsTutorialStep.Layout -> ConsoleSettingsCategory.Layout
        SettingsTutorialStep.Buttons -> ConsoleSettingsCategory.Layout
        SettingsTutorialStep.Background -> ConsoleSettingsCategory.Background
        SettingsTutorialStep.Backup -> ConsoleSettingsCategory.App
        SettingsTutorialStep.DeckSettingsButton -> ConsoleSettingsCategory.App
    }
}

private enum class ConsoleLayoutEditMode(@StringRes val labelRes: Int) {
    Layout(R.string.console_layout_mode_layout),
    Buttons(R.string.console_layout_mode_buttons)
}

@StringRes
private fun consoleSettingsCategoryTitleRes(category: ConsoleSettingsCategory): Int {
    return when (category) {
        ConsoleSettingsCategory.PcConnection -> R.string.console_settings_category_pc_connection
        ConsoleSettingsCategory.Layout -> R.string.console_settings_category_layout
        ConsoleSettingsCategory.Background -> R.string.console_settings_category_background
        ConsoleSettingsCategory.Controls -> R.string.console_settings_category_controls
        ConsoleSettingsCategory.App -> R.string.console_settings_category_app
    }
}

@StringRes
private fun consoleSettingsCategorySubtitleRes(category: ConsoleSettingsCategory): Int {
    return when (category) {
        ConsoleSettingsCategory.PcConnection -> R.string.console_settings_category_pc_connection_desc
        ConsoleSettingsCategory.Layout -> R.string.console_settings_category_layout_desc
        ConsoleSettingsCategory.Background -> R.string.console_settings_category_background_desc
        ConsoleSettingsCategory.Controls -> R.string.console_settings_category_controls_desc
        ConsoleSettingsCategory.App -> R.string.console_settings_category_app_desc
    }
}

private fun consoleSettingsCategoryIcon(category: ConsoleSettingsCategory): ImageVector {
    return when (category) {
        ConsoleSettingsCategory.PcConnection -> Icons.Filled.Computer
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
    companionSettings: CompanionSettings,
    companionStatus: CompanionConnectionStatus,
    companionRouteActive: Boolean,
    companionSyncViewToPc: Boolean,
    companionFollowPcView: Boolean,
    classicCompanionConnectionExpanded: Boolean,
    onBack: () -> Unit,
    onDeckUiModeChange: (DeckUiMode) -> Unit,
    onConsoleCategoryChange: (ConsoleSettingsCategory) -> Unit,
    onCompanionSettingsChange: (CompanionSettings) -> Unit,
    onCompanionSyncViewToPcChange: (Boolean) -> Unit,
    onCompanionFollowPcViewChange: (Boolean) -> Unit,
    onCompanionRouteActiveChange: (Boolean) -> Unit,
    onClassicCompanionConnectionExpandedChange: (Boolean) -> Unit,
    onTestCompanion: () -> Unit,
    onScanCompanionQr: () -> Unit,
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
            ClassicPcConnectionSidebarBox(
                status = status,
                settings = companionSettings,
                companionStatus = companionStatus,
                routeActive = companionRouteActive,
                syncViewToPc = companionSyncViewToPc,
                followPcView = companionFollowPcView,
                pairedHosts = pairedHosts,
                pairingDiscoverable = pairingDiscoverable,
                companionExpanded = classicCompanionConnectionExpanded,
                onCompanionExpandedChange = onClassicCompanionConnectionExpandedChange,
                onSettingsChange = onCompanionSettingsChange,
                onSyncViewToPcChange = onCompanionSyncViewToPcChange,
                onFollowPcViewChange = onCompanionFollowPcViewChange,
                onRouteActiveChange = onCompanionRouteActiveChange,
                onTestCompanion = onTestCompanion,
                onScanCompanionQr = onScanCompanionQr,
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
private fun ClassicPcConnectionSidebarBox(
    status: HidStatus,
    settings: CompanionSettings,
    companionStatus: CompanionConnectionStatus,
    routeActive: Boolean,
    syncViewToPc: Boolean,
    followPcView: Boolean,
    pairedHosts: List<PairedHidHost>,
    pairingDiscoverable: Boolean,
    companionExpanded: Boolean,
    onCompanionExpandedChange: (Boolean) -> Unit,
    onSettingsChange: (CompanionSettings) -> Unit,
    onSyncViewToPcChange: (Boolean) -> Unit,
    onFollowPcViewChange: (Boolean) -> Unit,
    onRouteActiveChange: (Boolean) -> Unit,
    onTestCompanion: () -> Unit,
    onScanCompanionQr: () -> Unit,
    onStart: () -> Unit,
    onStop: () -> Unit,
    onMakeDiscoverable: () -> Unit,
    onCancelDiscoverable: () -> Unit,
    onRefreshHosts: () -> Unit,
    onConnectHost: (PairedHidHost) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val companionUiEnabled = BuildConfig.DEBUG
    val accent = Color(0xFF25B9FF)
    val secondaryAccent = Color(0xFF0B7FE8)
    val shape = RoundedCornerShape(8.dp)
    val bluetoothHidUnsupported = Build.VERSION.SDK_INT < Build.VERSION_CODES.P
    var showBluetoothUnsupportedDialog by remember { mutableStateOf(false) }
    val connectionConnected = if (companionUiEnabled) {
        pcConnectionConnected(companionStatus, status)
    } else {
        status.state == HidConnectionState.Connected
    }
    val activeRouteText = when {
        !companionUiEnabled && bluetoothHidUnsupported -> stringResource(R.string.pc_connection_bluetooth_unavailable_release_short)
        !companionUiEnabled -> stringResource(status.state.labelRes())
        bluetoothHidUnsupported && companionStatus.connected ->
            companionStatus.appName.ifBlank { stringResource(R.string.companion_connected) }
        bluetoothHidUnsupported -> stringResource(R.string.pc_connection_companion_required)
        companionStatus.connected -> companionStatus.appName.ifBlank { stringResource(R.string.companion_connected) }
        status.state == HidConnectionState.Connected -> stringResource(status.state.labelRes())
        else -> stringResource(R.string.status_disconnected)
    }
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = colors.cardBackground,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.border(1.dp, accent.copy(alpha = 0.68f), shape),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = accent.copy(alpha = if (isSystemInDarkTheme()) 0.42f else 0.28f),
                contentColor = colors.textPrimary
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SettingsIconTile(Icons.Filled.Computer, accent)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.console_settings_category_pc_connection),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (connectionConnected) Color(0xFF2ECA73) else Color(0xFFE05252))
                            )
                            Text(
                                text = activeRouteText,
                                style = MaterialTheme.typography.labelSmall,
                                color = colors.textSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                if (companionUiEnabled) {
                    ClassicSidebarSubBox(
                        accent = accent
                    ) {
                        ClassicSidebarCollapsibleHeader(
                            icon = Icons.Filled.Link,
                            title = stringResource(R.string.companion_settings_title),
                            subtitle = stringResource(companionConnectionState(companionStatus).labelRes()),
                            accent = accent,
                            connected = companionStatus.connected,
                            expanded = companionExpanded,
                            onToggle = { onCompanionExpandedChange(!companionExpanded) }
                        )
                        AnimatedVisibility(visible = companionExpanded) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = settings.endpoint,
                                    onValueChange = { onSettingsChange(settings.copy(endpoint = it)) },
                                    singleLine = true,
                                    label = { Text(stringResource(R.string.companion_endpoint)) },
                                    placeholder = { Text("ws://192.168.0.2:17652") }
                                )
                                OutlinedTextField(
                                    modifier = Modifier.fillMaxWidth(),
                                    value = settings.pairingToken,
                                    onValueChange = { onSettingsChange(settings.copy(pairingToken = it)) },
                                    singleLine = true,
                                    label = { Text(stringResource(R.string.companion_pairing_token)) }
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    SidebarCompactActionButton(
                                        modifier = Modifier.weight(1f),
                                        icon = Icons.Filled.QrCodeScanner,
                                        title = stringResource(R.string.companion_scan_qr),
                                        accent = accent,
                                        highlighted = true,
                                        deckUiMode = DeckUiMode.Classic,
                                        onClick = onScanCompanionQr
                                    )
                                    SidebarCompactActionButton(
                                        modifier = Modifier.weight(1f),
                                        icon = Icons.Filled.Search,
                                        title = stringResource(R.string.companion_test_connection),
                                        accent = accent,
                                        highlighted = companionStatus.connected,
                                        deckUiMode = DeckUiMode.Classic,
                                        onClick = onTestCompanion
                                    )
                                }
                                CompanionViewSyncOptions(
                                    syncViewToPc = syncViewToPc,
                                    followPcView = followPcView,
                                    consoleStyle = false,
                                    onSyncViewToPcChange = onSyncViewToPcChange,
                                    onFollowPcViewChange = onFollowPcViewChange
                                )
                            }
                        }
                    }
                }
                ClassicSidebarSubBox(
                    accent = secondaryAccent,
                    onClick = if (bluetoothHidUnsupported) {
                        { showBluetoothUnsupportedDialog = true }
                    } else {
                        null
                    }
                ) {
                    ClassicSidebarModeDivider(
                        title = stringResource(R.string.pc_connection_bluetooth_mode),
                        subtitle = if (bluetoothHidUnsupported) {
                            stringResource(R.string.pc_connection_bluetooth_unavailable_short)
                        } else {
                            stringResource(status.state.labelRes())
                        },
                        accent = secondaryAccent,
                        connected = !bluetoothHidUnsupported && status.state == HidConnectionState.Connected
                    )
                    if (bluetoothHidUnsupported) {
                        Text(
                            modifier = Modifier.padding(8.dp),
                            text = stringResource(
                                if (companionUiEnabled) {
                                    R.string.pc_connection_bluetooth_unavailable_desc
                                } else {
                                    R.string.pc_connection_bluetooth_unavailable_release_desc
                                }
                            ),
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    } else {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                SidebarCompactActionButton(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Filled.Bluetooth,
                                    title = stringResource(R.string.register_hid),
                                    accent = secondaryAccent,
                                    highlighted = status.state != HidConnectionState.Connected,
                                    deckUiMode = DeckUiMode.Classic,
                                    onClick = onStart
                                )
                                SidebarCompactActionButton(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Filled.Stop,
                                    title = stringResource(R.string.stop),
                                    accent = secondaryAccent,
                                    highlighted = false,
                                    deckUiMode = DeckUiMode.Classic,
                                    onClick = onStop
                                )
                            }
                            SidebarDiscoverableRow(
                                deckUiMode = DeckUiMode.Classic,
                                pairingDiscoverable = pairingDiscoverable,
                                onMakeDiscoverable = onMakeDiscoverable,
                                onCancelDiscoverable = onCancelDiscoverable
                            )
                            PairedHostsInlineSection(
                                pairedHosts = pairedHosts,
                                deckUiMode = DeckUiMode.Classic,
                                onRefreshHosts = onRefreshHosts,
                                onConnectHost = onConnectHost
                            )
                        }
                    }
                }
            }
        }
    }
    if (showBluetoothUnsupportedDialog) {
        BluetoothHidUnsupportedDialog(
            onDismiss = { showBluetoothUnsupportedDialog = false }
        )
    }
}

@Composable
private fun ClassicSidebarSubBox(
    accent: Color,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    val boxContent: @Composable () -> Unit = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(accent.copy(alpha = if (isSystemInDarkTheme()) 0.12f else 0.09f))
                .border(1.dp, accent.copy(alpha = 0.34f), shape),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            content = content
        )
    }
    if (onClick == null) {
        boxContent()
    } else {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent,
            shape = shape,
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            onClick = onClick
        ) {
            boxContent()
        }
    }
}

@Composable
private fun ClassicSidebarCollapsibleHeader(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accent: Color,
    connected: Boolean,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val headerBackground = accent.copy(alpha = if (isSystemInDarkTheme()) 0.2f else 0.14f)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = headerBackground,
        contentColor = colors.textPrimary,
        onClick = onToggle
    ) {
        Column {
            Row(
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SettingsIconTile(icon, accent)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (connected) Color(0xFF2ECA73) else Color(0xFFE05252))
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
                Icon(
                    imageVector = if (expanded) Icons.Filled.Remove else Icons.Filled.Add,
                    contentDescription = null,
                    tint = colors.textPrimary,
                    modifier = Modifier.size(17.dp)
                )
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(accent.copy(alpha = 0.34f))
            )
        }
    }
}

@Composable
private fun ClassicSidebarModeDivider(
    title: String,
    subtitle: String,
    accent: Color,
    connected: Boolean
) {
    val colors = LocalDeckThemeColors.current
    val darkTheme = isSystemInDarkTheme()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(accent.copy(alpha = if (darkTheme) 0.2f else 0.14f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SettingsIconTile(Icons.Filled.Bluetooth, accent)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (connected) Color(0xFF2ECA73) else Color(0xFFE05252))
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
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(accent.copy(alpha = 0.34f))
        )
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
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .then(
                        if (consoleSelected) {
                            Modifier.consoleButtonDropShadow(
                                shape = selectedShape,
                                darkTheme = darkTheme,
                                pressed = false
                            )
                        } else {
                            Modifier
                        }
                    )
                    .clip(selectedShape)
                    .then(
                        if (consoleSelected) {
                            Modifier.background(colors.consoleButtonDefault)
                        } else {
                            Modifier.background(
                                Brush.linearGradient(
                                    listOf(selectedStart, selectedEnd)
                                )
                            )
                        }
                    )
            )
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
    val shape = RoundedCornerShape(if (consoleMode) 14.dp else 8.dp)
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
    status: HidStatus,
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
    pairedHosts: List<PairedHidHost>,
    pairingDiscoverable: Boolean,
    logs: List<ActivityLog>,
    debugKeepScreenOn: Boolean,
    companionSettings: CompanionSettings,
    companionStatus: CompanionConnectionStatus,
    companionRouteActive: Boolean,
    companionSyncViewToPc: Boolean,
    companionFollowPcView: Boolean,
    showDetailGuides: Boolean,
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
    onStart: () -> Unit,
    onStop: () -> Unit,
    onMakeDiscoverable: () -> Unit,
    onCancelDiscoverable: () -> Unit,
    onRefreshHosts: () -> Unit,
    onConnectHost: (PairedHidHost) -> Unit,
    onDebugKeepScreenOnChange: (Boolean) -> Unit,
    onShowDetailGuidesChange: (Boolean) -> Unit,
    onCompanionSettingsChange: (CompanionSettings) -> Unit,
    onCompanionSyncViewToPcChange: (Boolean) -> Unit,
    onCompanionFollowPcViewChange: (Boolean) -> Unit,
    onCompanionRouteActiveChange: (Boolean) -> Unit,
    onTestCompanion: () -> Unit,
    onScanCompanionQr: () -> Unit,
    onSendDeckToCompanion: () -> Unit,
    onApplyDeckFromCompanion: () -> Unit
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
                DebugZoneCard(
                    mode = DeckUiMode.Classic,
                    logs = logs,
                    debugKeepScreenOn = debugKeepScreenOn,
                    onDebugKeepScreenOnChange = onDebugKeepScreenOnChange,
                    onOpenButtonStudy = onOpenIconStyleTest
                )
            }
        }
        item {
            ClassicSettingsControlRow(
                icon = Icons.Filled.Help,
                iconColor = ClassicButtonAccent,
                title = stringResource(R.string.console_detail_guides_title),
                subtitle = stringResource(R.string.console_detail_guides_desc),
                trailing = {
                    SettingsSwitch(
                        checked = showDetailGuides,
                        accent = ClassicButtonAccent,
                        onCheckedChange = onShowDetailGuidesChange
                    )
                }
            )
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
    companionSettings: CompanionSettings,
    companionStatus: CompanionConnectionStatus,
    companionRouteActive: Boolean,
    companionSyncViewToPc: Boolean,
    companionFollowPcView: Boolean,
    showDetailGuides: Boolean,
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
    onDebugKeepScreenOnChange: (Boolean) -> Unit,
    onShowDetailGuidesChange: (Boolean) -> Unit,
    onCompanionSettingsChange: (CompanionSettings) -> Unit,
    onCompanionSyncViewToPcChange: (Boolean) -> Unit,
    onCompanionFollowPcViewChange: (Boolean) -> Unit,
    onCompanionRouteActiveChange: (Boolean) -> Unit,
    onTestCompanion: () -> Unit,
    onScanCompanionQr: () -> Unit,
    onSendDeckToCompanion: () -> Unit,
    onApplyDeckFromCompanion: () -> Unit
) {
    val companionUiEnabled = BuildConfig.DEBUG
    val effectiveCompanionStatus = if (companionUiEnabled) companionStatus else CompanionConnectionStatus()
    var showBluetoothFallback by remember(category, companionStatus.connected, companionRouteActive) {
        mutableStateOf(!companionStatus.connected || !companionRouteActive)
    }
    SettingsDetailContent(
        mode = DeckUiMode.Console,
        accent = Color(0xFF00A6E7),
        icon = consoleSettingsCategoryIcon(category),
        title = stringResource(consoleSettingsCategoryTitleRes(category)),
        subtitle = stringResource(
            if (!companionUiEnabled && category == ConsoleSettingsCategory.PcConnection) {
                R.string.console_settings_category_pc_connection_desc_release
            } else {
                consoleSettingsCategorySubtitleRes(category)
            }
        ),
        connectionStatusLabel = if (category == ConsoleSettingsCategory.PcConnection) {
            if (companionUiEnabled) pcConnectionTargetText(effectiveCompanionStatus, status) else stringResource(status.state.labelRes())
        } else {
            null
        },
        connectionConnected = category == ConsoleSettingsCategory.PcConnection &&
            if (companionUiEnabled) pcConnectionConnected(effectiveCompanionStatus, status) else status.state == HidConnectionState.Connected
    ) {
        if (showDetailGuides) {
            item {
                ConsoleMenuDetailCard(category)
            }
        }
        when (category) {
            ConsoleSettingsCategory.PcConnection -> {
                if (companionUiEnabled) {
                    item {
                        CompanionSettingsCard(
                            settings = companionSettings,
                            status = companionStatus,
                            hidStatus = status,
                            routeActive = companionRouteActive,
                            consoleStyle = true,
                            syncViewToPc = companionSyncViewToPc,
                            followPcView = companionFollowPcView,
                            onSettingsChange = onCompanionSettingsChange,
                            onRouteActiveChange = onCompanionRouteActiveChange,
                            onSyncViewToPcChange = onCompanionSyncViewToPcChange,
                            onFollowPcViewChange = onCompanionFollowPcViewChange,
                            onTestCompanion = onTestCompanion,
                            onScanCompanionQr = onScanCompanionQr,
                            onSendDeckToCompanion = onSendDeckToCompanion,
                            onApplyDeckFromCompanion = onApplyDeckFromCompanion
                        )
                    }
                }
                if (!companionUiEnabled || showBluetoothFallback) {
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
                } else {
                    item {
                        PcConnectionFallbackSummaryCard(
                            status = status,
                            consoleStyle = true,
                            onShowFallback = { showBluetoothFallback = true }
                        )
                    }
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
                item {
                    ConsoleSwitchRow(
                        icon = Icons.Filled.Help,
                        title = stringResource(R.string.console_detail_guides_title),
                        subtitle = stringResource(R.string.console_detail_guides_desc),
                        checked = showDetailGuides,
                        onCheckedChange = onShowDetailGuidesChange
                    )
                }
                if (BuildConfig.DEBUG) {
                    item {
                        DebugZoneCard(
                            mode = DeckUiMode.Console,
                            logs = logs,
                            debugKeepScreenOn = debugKeepScreenOn,
                            onDebugKeepScreenOnChange = onDebugKeepScreenOnChange,
                            onOpenButtonStudy = onOpenIconStyleTest
                        )
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
    connectionStatusLabel: String? = null,
    connectionConnected: Boolean = false,
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
                        subtitle = subtitle,
                        connectionStatusLabel = connectionStatusLabel,
                        connectionConnected = connectionConnected
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SettingsIconTile(icon, accent)
                        Column(modifier = Modifier.weight(1f)) {
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
                        if (connectionStatusLabel != null) {
                            PcConnectionHeaderStatusPill(
                                label = connectionStatusLabel,
                                connected = connectionConnected,
                                consoleStyle = false
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
    subtitle: String,
    connectionStatusLabel: String? = null,
    connectionConnected: Boolean = false
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
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
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
            if (connectionStatusLabel != null) {
                PcConnectionHeaderStatusPill(
                    label = connectionStatusLabel,
                    connected = connectionConnected,
                    consoleStyle = true
                )
            }
        }
    }
}

@Composable
private fun PcConnectionHeaderStatusPill(
    label: String,
    connected: Boolean,
    consoleStyle: Boolean
) {
    val colors = LocalDeckThemeColors.current
    val shape = RoundedCornerShape(if (consoleStyle) 14.dp else 999.dp)
    Row(
        modifier = Modifier
            .clip(shape)
            .background(
                if (consoleStyle) {
                    colors.consoleButtonDefault.copy(alpha = 0.62f)
                } else {
                    colors.toggleBackground.copy(alpha = 0.42f)
                }
            )
            .border(
                1.dp,
                (if (connected) Color(0xFF2ECA73) else Color(0xFFE05252)).copy(alpha = 0.36f),
                shape
            )
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (connected) Color(0xFF2ECA73) else Color(0xFFE05252))
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun DebugZoneCard(
    mode: DeckUiMode,
    logs: List<ActivityLog>,
    debugKeepScreenOn: Boolean,
    onDebugKeepScreenOnChange: (Boolean) -> Unit,
    onOpenButtonStudy: () -> Unit
) {
    val colors = deckThemeColors(mode, isSystemInDarkTheme())
    val consoleStyle = mode == DeckUiMode.Console
    val accent = if (consoleStyle) colors.consoleButtonFeatured else ClassicButtonAccent
    val content: @Composable ColumnScope.() -> Unit = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (consoleStyle) {
                ConsoleSettingsIconTile(Icons.Filled.Settings)
            } else {
                SettingsIconTile(Icons.Filled.Settings, accent)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stringResource(R.string.debug_zone_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Text(
                    text = stringResource(R.string.debug_zone_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (consoleStyle) {
                ConsolePillButton(
                    text = stringResource(R.string.button_shape_study),
                    onClick = onOpenButtonStudy
                )
            } else {
                Button(
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accent,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    onClick = onOpenButtonStudy
                ) {
                    Text(
                        text = stringResource(R.string.button_shape_study),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = stringResource(R.string.debug_keep_screen_on_title),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Text(
                    text = stringResource(R.string.debug_keep_screen_on_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Switch(
                checked = debugKeepScreenOn,
                onCheckedChange = onDebugKeepScreenOnChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = accent,
                    uncheckedThumbColor = colors.textSecondary,
                    uncheckedTrackColor = colors.toggleBackground
                )
            )
        }
        Text(
            text = stringResource(R.string.diagnostics),
            style = MaterialTheme.typography.labelLarge,
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
    if (consoleStyle) {
        ConsoleSettingsCard(content = content)
    } else {
        SettingsCard(accent = accent, themeColors = colors, content = content)
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

private enum class ButtonShapeStudyTheme(
    val label: String,
    val mode: DeckUiMode,
    val darkTheme: Boolean
) {
    ClassicLight("클래식 화이트", DeckUiMode.Classic, false),
    ClassicDark("클래식 블랙", DeckUiMode.Classic, true),
    ConsoleLight("콘솔 화이트", DeckUiMode.Console, false),
    ConsoleDark("콘솔 블랙", DeckUiMode.Console, true)
}

private enum class ButtonShapeStudyKind(
    val title: String,
    val subtitle: String
) {
    Square("정사각 버튼", "아이콘 중심"),
    Horizontal("가로형 버튼", "아이콘 + 주/부제목"),
    Vertical("세로형 버튼", "세로 2칸 계열"),
    Settings("설정 버튼", "설정 진입"),
    Bluetooth("Bluetooth 상태", "HID 연결"),
    PageMove("페이지 이동", "이전 / 다음"),
    Media("미디어 키", "재생 / 볼륨"),
    Hotkey("단축키", "조합키"),
    TextInput("텍스트 입력", "문자열 전송"),
    RunCommand("실행 명령", "PC 명령"),
    AppCommand("앱 명령", "앱별 액션"),
    Utility("유틸리티", "앱 내부 동작"),
    CompanionCommand("Companion 전용", "PC 앱 필요"),
    CompanionStatus("Companion 상태", "연결 정보"),
    Slider("슬라이더", "방향은 버튼 비율 기준"),
    Knob("제한 노브", "중앙 복귀 + 단계 입력"),
    Wheel("무한 휠", "하드웨어 휠 노치"),
    JoyPad("D패드", "4방향 / 8방향"),
    AnalogStick("아날로그 스틱", "가상 스틱"),
    Toggle("토글 버튼", "Companion 상태값")
}

private data class ButtonShapeStudyItem(
    val kind: ButtonShapeStudyKind,
    val weight: Float = 1f,
    val height: Dp = 150.dp,
    val title: String = kind.title,
    val subtitle: String = kind.subtitle
)

private data class ButtonShapeStudyPageSpec(
    val title: String,
    val description: String,
    val rows: List<List<ButtonShapeStudyItem>>
)

private val buttonShapeStudyPages = listOf(
    ButtonShapeStudyPageSpec(
        title = "덱 기본 버튼",
        description = "메인 화면에 항상 등장하는 기본 버튼 타입과 상태 버튼입니다.",
        rows = listOf(
            listOf(
                ButtonShapeStudyItem(ButtonShapeStudyKind.Settings, weight = 1f, height = 150.dp),
                ButtonShapeStudyItem(ButtonShapeStudyKind.Bluetooth, weight = 1f, height = 150.dp),
                ButtonShapeStudyItem(ButtonShapeStudyKind.PageMove, weight = 1f, height = 150.dp)
            ),
            listOf(
                ButtonShapeStudyItem(ButtonShapeStudyKind.Media, weight = 1.18f, height = 156.dp),
                ButtonShapeStudyItem(ButtonShapeStudyKind.Hotkey, weight = 1f, height = 156.dp),
                ButtonShapeStudyItem(ButtonShapeStudyKind.Utility, weight = 1.22f, height = 156.dp)
            )
        )
    ),
    ButtonShapeStudyPageSpec(
        title = "입력 / 실행 버튼",
        description = "키 입력 편집기에서 고르는 액션 버튼들을 같은 카드 규칙으로 비교합니다.",
        rows = listOf(
            listOf(
                ButtonShapeStudyItem(ButtonShapeStudyKind.TextInput, weight = 1f, height = 150.dp),
                ButtonShapeStudyItem(ButtonShapeStudyKind.RunCommand, weight = 1.15f, height = 150.dp),
                ButtonShapeStudyItem(ButtonShapeStudyKind.AppCommand, weight = 1f, height = 150.dp)
            ),
            listOf(
                ButtonShapeStudyItem(ButtonShapeStudyKind.Utility, weight = 1f, height = 150.dp),
                ButtonShapeStudyItem(ButtonShapeStudyKind.Hotkey, weight = 1.45f, height = 150.dp, title = "긴 단축키", subtitle = "조합키 입력")
            )
        )
    ),
    ButtonShapeStudyPageSpec(
        title = "컨트롤 버튼",
        description = "슬라이더, 노브, 무한 휠, D패드, 아날로그 스틱, 토글의 기본/조작 중 모양을 확인합니다.",
        rows = listOf(
            listOf(
                ButtonShapeStudyItem(ButtonShapeStudyKind.Slider, weight = 1.45f, height = 154.dp, title = "가로 슬라이더"),
                ButtonShapeStudyItem(ButtonShapeStudyKind.Knob, weight = 1f, height = 154.dp),
                ButtonShapeStudyItem(ButtonShapeStudyKind.Wheel, weight = 1f, height = 154.dp)
            ),
            listOf(
                ButtonShapeStudyItem(ButtonShapeStudyKind.Slider, weight = 0.72f, height = 190.dp, title = "세로 슬라이더", subtitle = "세로형 버튼"),
                ButtonShapeStudyItem(ButtonShapeStudyKind.JoyPad, weight = 1f, height = 190.dp),
                ButtonShapeStudyItem(ButtonShapeStudyKind.AnalogStick, weight = 1f, height = 190.dp)
            )
        )
    ),
    ButtonShapeStudyPageSpec(
        title = "비율별 표출",
        description = "같은 버튼 내용이 정사각형, 가로형, 세로형으로 바뀔 때의 정렬 기준입니다.",
        rows = listOf(
            listOf(
                ButtonShapeStudyItem(ButtonShapeStudyKind.Square, weight = 0.78f, height = 174.dp),
                ButtonShapeStudyItem(ButtonShapeStudyKind.Horizontal, weight = 1.42f, height = 174.dp),
                ButtonShapeStudyItem(ButtonShapeStudyKind.Vertical, weight = 0.70f, height = 174.dp)
            ),
            listOf(
                ButtonShapeStudyItem(ButtonShapeStudyKind.Media, weight = 1.6f, height = 136.dp, title = "긴 미디어 버튼", subtitle = "가로형 정보 배치"),
                ButtonShapeStudyItem(ButtonShapeStudyKind.RunCommand, weight = 1.6f, height = 136.dp, title = "긴 실행 버튼", subtitle = "가로형 명령 표시")
            )
        )
    )
)

@Composable
private fun ButtonShapeStudyPage(
    modifier: Modifier = Modifier,
    initialPageIndex: Int = 0,
    onBack: () -> Unit
) {
    var selectedTheme by remember { mutableStateOf(ButtonShapeStudyTheme.ConsoleDark) }
    var activePreview by remember { mutableStateOf(true) }
    var studyPageIndex by remember {
        mutableStateOf(initialPageIndex.coerceIn(buttonShapeStudyPages.indices))
    }
    val colors = deckThemeColors(selectedTheme.mode, selectedTheme.darkTheme)
    val pageSpec = buttonShapeStudyPages[studyPageIndex.coerceIn(buttonShapeStudyPages.indices)]

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
                        shape = RoundedCornerShape(if (selectedTheme.mode == DeckUiMode.Console) 14.dp else 8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary)
                    ) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("설정")
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "버튼 형태 시안",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "일반 버튼과 슬라이더, 노브, 휠, D패드, 아날로그 스틱이 테마별로 같은 규칙을 갖는지 확인합니다.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.textSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    ButtonShapeStudyToggle(
                        modifier = Modifier.width(150.dp),
                        text = if (activePreview) "조작 중" else "기본",
                        selected = activePreview,
                        colors = colors,
                        consoleStyle = selectedTheme.mode == DeckUiMode.Console,
                        onClick = { activePreview = !activePreview }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ButtonShapeStudyTheme.values().forEach { theme ->
                        ButtonShapeStudyToggle(
                            modifier = Modifier.weight(1f),
                            text = theme.label,
                            selected = selectedTheme == theme,
                            colors = colors,
                            consoleStyle = selectedTheme.mode == DeckUiMode.Console,
                            onClick = { selectedTheme = theme }
                        )
                    }
                }
                ButtonShapeStudyPager(
                    pageSpec = pageSpec,
                    pageIndex = studyPageIndex,
                    pageCount = buttonShapeStudyPages.size,
                    colors = colors,
                    consoleStyle = selectedTheme.mode == DeckUiMode.Console,
                    onPrevious = {
                        studyPageIndex = if (studyPageIndex == 0) buttonShapeStudyPages.lastIndex else studyPageIndex - 1
                    },
                    onNext = {
                        studyPageIndex = if (studyPageIndex == buttonShapeStudyPages.lastIndex) 0 else studyPageIndex + 1
                    }
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ButtonShapeStudyBoard(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        pageSpec = pageSpec,
                        theme = selectedTheme,
                        activePreview = activePreview
                    )
                    ButtonShapeStudyNotes(
                        modifier = Modifier
                            .width(300.dp)
                            .fillMaxHeight(),
                        pageSpec = pageSpec,
                        theme = selectedTheme
                    )
                }
            }
        }
    }
}

@Composable
private fun ButtonShapeStudyPager(
    pageSpec: ButtonShapeStudyPageSpec,
    pageIndex: Int,
    pageCount: Int,
    colors: DeckThemeColors,
    consoleStyle: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ButtonShapeStudyToggle(
            modifier = Modifier.width(86.dp),
            text = "이전",
            selected = false,
            colors = colors,
            consoleStyle = consoleStyle,
            onClick = onPrevious
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${pageIndex + 1} / $pageCount  ${pageSpec.title}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = pageSpec.description,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(pageCount) { index ->
                Box(
                    modifier = Modifier
                        .size(width = if (index == pageIndex) 22.dp else 8.dp, height = 8.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(
                            if (index == pageIndex) colors.consoleButtonFeatured else colors.textMuted.copy(alpha = 0.36f)
                        )
                )
            }
        }
        ButtonShapeStudyToggle(
            modifier = Modifier.width(86.dp),
            text = "다음",
            selected = false,
            colors = colors,
            consoleStyle = consoleStyle,
            onClick = onNext
        )
    }
}

@Composable
private fun ButtonShapeStudyToggle(
    modifier: Modifier = Modifier,
    text: String,
    selected: Boolean,
    colors: DeckThemeColors,
    consoleStyle: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(if (consoleStyle) 14.dp else 999.dp)
    Surface(
        modifier = modifier.height(42.dp),
        shape = shape,
        color = if (selected) {
            if (consoleStyle) colors.consoleButtonFeatured else ClassicButtonAccent
        } else {
            if (consoleStyle) colors.consoleButtonDefault else colors.toggleBackground
        },
        contentColor = if (selected) Color.White else colors.textPrimary,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = if (consoleStyle) 0.7.dp else 0.dp,
                    color = if (selected) Color.White.copy(alpha = 0.22f) else colors.cardBorder,
                    shape = shape
                )
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
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
private fun ButtonShapeStudyBoard(
    modifier: Modifier,
    pageSpec: ButtonShapeStudyPageSpec,
    theme: ButtonShapeStudyTheme,
    activePreview: Boolean
) {
    val colors = LocalDeckThemeColors.current
    val consoleStyle = theme.mode == DeckUiMode.Console
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(if (consoleStyle) 26.dp else 12.dp),
        color = if (consoleStyle) colors.consolePreviewBackground else colors.cardBackground,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .border(
                    width = if (consoleStyle) 0.8.dp else 1.dp,
                    color = colors.cardBorder.copy(alpha = if (consoleStyle) 0.72f else 0.95f),
                    shape = RoundedCornerShape(if (consoleStyle) 26.dp else 12.dp)
                )
                .verticalScroll(rememberScrollState())
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ButtonShapeStudyLiveControls(theme = theme)
            ButtonShapeStudySectionTitle(pageSpec.title)
            pageSpec.rows.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { item ->
                        ButtonShapeStudySample(
                            modifier = Modifier
                                .weight(item.weight)
                                .height(item.height),
                            kind = item.kind,
                            title = item.title,
                            subtitle = item.subtitle,
                            theme = theme,
                            active = activePreview
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ButtonShapeStudyLiveControls(theme: ButtonShapeStudyTheme) {
    val colors = LocalDeckThemeColors.current
    val consoleStyle = theme.mode == DeckUiMode.Console
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = "조작 샘플",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary,
                maxLines = 1
            )
            Text(
                text = "로컬 프리뷰 · 실제 전송 없음",
                style = MaterialTheme.typography.labelMedium,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ButtonShapeStudyLiveSample(
                modifier = Modifier.weight(1.35f).height(132.dp),
                kind = ButtonShapeStudyKind.Slider,
                title = "슬라이더",
                theme = theme
            )
            ButtonShapeStudyLiveSample(
                modifier = Modifier.weight(1f).height(132.dp),
                kind = ButtonShapeStudyKind.Knob,
                title = "노브",
                theme = theme
            )
            ButtonShapeStudyLiveSample(
                modifier = Modifier.weight(1f).height(132.dp),
                kind = ButtonShapeStudyKind.Wheel,
                title = "휠",
                theme = theme
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ButtonShapeStudyLiveSample(
                modifier = Modifier.weight(1f).height(154.dp),
                kind = ButtonShapeStudyKind.JoyPad,
                title = "D패드",
                theme = theme
            )
            ButtonShapeStudyLiveSample(
                modifier = Modifier.weight(1f).height(154.dp),
                kind = ButtonShapeStudyKind.AnalogStick,
                title = "아날로그",
                theme = theme
            )
            ButtonShapeStudyLiveSample(
                modifier = Modifier.weight(1f).height(154.dp),
                kind = ButtonShapeStudyKind.Toggle,
                title = "토글",
                theme = theme
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(colors.cardBorder.copy(alpha = if (consoleStyle) 0.58f else 0.70f))
        )
    }
}

@Composable
private fun ButtonShapeStudyLiveSample(
    modifier: Modifier,
    kind: ButtonShapeStudyKind,
    title: String,
    theme: ButtonShapeStudyTheme
) {
    var active by remember(kind) { mutableStateOf(false) }
    var output by remember(kind) { mutableStateOf(buttonShapeStudyDefaultOutput(kind)) }
    var activeStep by remember(kind) { mutableStateOf(0) }
    var analogValue by remember(kind) { mutableStateOf(Offset.Zero) }
    fun update(position: Offset, size: IntSize) {
        output = buttonShapeStudyOutputFor(kind, position, size)
        if (kind == ButtonShapeStudyKind.AnalogStick) {
            val point = analogStickPointForPosition(position, size, DEFAULT_ANALOG_STICK_DEAD_ZONE)
            analogValue = Offset(point.x, point.y)
            activeStep = 0
        } else {
            activeStep = buttonShapeStudyStepFor(kind, position, size)
        }
    }
    val gestureModifier = Modifier.pointerInput(kind) {
        awaitEachGesture {
            val down = awaitFirstDown(requireUnconsumed = false, pass = PointerEventPass.Initial)
            down.consume()
            if (kind == ButtonShapeStudyKind.Toggle) {
                active = !active
                output = "IN tap / OUT ${if (active) "ON" else "OFF"}"
            } else {
                active = true
                update(down.position, size)
            }
            while (true) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                val change = event.changes.firstOrNull { it.id == down.id }
                if (change != null && kind != ButtonShapeStudyKind.Toggle) {
                    update(change.position, size)
                    change.consume()
                }
                if (event.changes.all { it.changedToUp() || !it.pressed }) break
            }
            if (kind != ButtonShapeStudyKind.Toggle) {
                active = false
                activeStep = 0
                analogValue = Offset.Zero
                output = buttonShapeStudyReleaseOutput(kind, output)
            }
        }
    }
    ButtonShapeStudySample(
        modifier = modifier.then(gestureModifier),
        kind = kind,
        title = title,
        subtitle = output,
        theme = theme,
        active = active,
        activeStep = activeStep,
        forcedAnalogValue = if (kind == ButtonShapeStudyKind.AnalogStick) analogValue else null
    )
}

private fun buttonShapeStudyDefaultOutput(kind: ButtonShapeStudyKind): String {
    return when (kind) {
        ButtonShapeStudyKind.Toggle -> "IN idle / OUT OFF"
        ButtonShapeStudyKind.AnalogStick -> "IN 0,0 / OUT idle"
        ButtonShapeStudyKind.JoyPad -> "IN center / OUT idle"
        ButtonShapeStudyKind.Wheel -> "IN 0 notch / OUT idle"
        ButtonShapeStudyKind.Knob -> "IN 0 deg / OUT idle"
        ButtonShapeStudyKind.Slider -> "IN 50% / OUT idle"
        else -> "IN idle / OUT preview"
    }
}

private fun buttonShapeStudyReleaseOutput(kind: ButtonShapeStudyKind, previous: String): String {
    return when (kind) {
        ButtonShapeStudyKind.AnalogStick -> "IN 0,0 / OUT center"
        ButtonShapeStudyKind.JoyPad -> "IN center / OUT idle"
        ButtonShapeStudyKind.Slider,
        ButtonShapeStudyKind.Knob,
        ButtonShapeStudyKind.Wheel -> previous.replace("drag", "release")
        else -> previous
    }
}

private fun buttonShapeStudyOutputFor(kind: ButtonShapeStudyKind, position: Offset, size: IntSize): String {
    val width = size.width.coerceAtLeast(1).toFloat()
    val height = size.height.coerceAtLeast(1).toFloat()
    val center = Offset(width / 2f, height / 2f)
    return when (kind) {
        ButtonShapeStudyKind.Slider -> {
            val horizontal = width >= height * 1.12f
            val value = if (horizontal) {
                ((position.x / width) * 100f).roundToInt().coerceIn(0, 100)
            } else {
                (((height - position.y) / height) * 100f).roundToInt().coerceIn(0, 100)
            }
            "IN drag / OUT $value%"
        }
        ButtonShapeStudyKind.Knob -> {
            val angle = atan2(position.y - center.y, position.x - center.x) * 180f / Math.PI.toFloat() + 90f
            val normalized = angle.coerceIn(-126f, 126f).roundToInt()
            "IN drag / OUT ${normalized}deg"
        }
        ButtonShapeStudyKind.Wheel -> {
            val angle = (atan2(position.y - center.y, position.x - center.x) * 180f / Math.PI.toFloat() + 360f) % 360f
            val notch = (angle / (360f / INFINITE_WHEEL_NOTCHES_PER_REVOLUTION)).roundToInt()
            "IN drag / OUT notch $notch"
        }
        ButtonShapeStudyKind.JoyPad -> {
            val direction = when (buttonShapeStudyJoyPadStepFor(position, size)) {
                JOYPAD_STEP_UP -> "up"
                JOYPAD_STEP_DOWN -> "down"
                JOYPAD_STEP_LEFT -> "left"
                JOYPAD_STEP_RIGHT -> "right"
                JOYPAD_STEP_UP_LEFT -> "up+left"
                JOYPAD_STEP_UP_RIGHT -> "up+right"
                JOYPAD_STEP_DOWN_LEFT -> "down+left"
                JOYPAD_STEP_DOWN_RIGHT -> "down+right"
                else -> "center"
            }
            "IN $direction / OUT press"
        }
        ButtonShapeStudyKind.AnalogStick -> {
            val point = analogStickPointForPosition(position, size, DEFAULT_ANALOG_STICK_DEAD_ZONE)
            val x = point.x
            val y = point.y
            "IN ${"%.1f".format(Locale.US, x)},${"%.1f".format(Locale.US, y)} / OUT XY"
        }
        else -> buttonShapeStudyDefaultOutput(kind)
    }
}

private fun buttonShapeStudyStepFor(kind: ButtonShapeStudyKind, position: Offset, size: IntSize): Int {
    val width = size.width.coerceAtLeast(1).toFloat()
    val height = size.height.coerceAtLeast(1).toFloat()
    val center = Offset(width / 2f, height / 2f)
    return when (kind) {
        ButtonShapeStudyKind.Slider -> {
            val horizontal = width >= height * 1.12f
            val fraction = if (horizontal) {
                (position.x / width).coerceIn(0f, 1f)
            } else {
                ((height - position.y) / height).coerceIn(0f, 1f)
            }
            ((fraction - 0.5f) * 6f).roundToInt().coerceIn(-3, 3)
        }
        ButtonShapeStudyKind.Knob -> {
            val angle = atan2(position.y - center.y, position.x - center.x) * 180f / Math.PI.toFloat() + 90f
            (angle.coerceIn(-126f, 126f) / 42f).roundToInt().coerceIn(-3, 3)
        }
        ButtonShapeStudyKind.Wheel -> {
            val angle = (atan2(position.y - center.y, position.x - center.x) * 180f / Math.PI.toFloat() + 360f) % 360f
            (angle / (360f / INFINITE_WHEEL_NOTCHES_PER_REVOLUTION)).roundToInt()
        }
        ButtonShapeStudyKind.JoyPad,
        ButtonShapeStudyKind.AnalogStick -> buttonShapeStudyJoyPadStepFor(position, size)
        else -> 0
    }
}

private fun buttonShapeStudyJoyPadStepFor(position: Offset, size: IntSize): Int {
    val width = size.width.coerceAtLeast(1).toFloat()
    val height = size.height.coerceAtLeast(1).toFloat()
    val center = Offset(width / 2f, height / 2f)
    val fromCenter = position - center
    val deadZone = minOf(width, height) * 0.13f
    if (maxOf(abs(fromCenter.x), abs(fromCenter.y)) <= deadZone) return 0
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

@Composable
private fun ButtonShapeStudySectionTitle(text: String) {
    val colors = LocalDeckThemeColors.current
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = colors.textPrimary
    )
}

@Composable
private fun ButtonShapeStudySample(
    modifier: Modifier,
    kind: ButtonShapeStudyKind,
    title: String,
    subtitle: String,
    theme: ButtonShapeStudyTheme,
    active: Boolean,
    activeStep: Int = 0,
    forcedAnalogValue: Offset? = null
) {
    val colors = LocalDeckThemeColors.current
    val button = remember(kind, title, subtitle, theme, active) {
        buttonShapeStudyButton(kind, title, subtitle, theme, active)
    }
    var output by remember(kind) { mutableStateOf(buttonShapeStudyDefaultOutput(kind)) }
    val usesExternalGestureState = subtitle.startsWith("IN ") || activeStep != 0
    val displayOutput = if (usesExternalGestureState) subtitle else output
    BoxWithConstraints(modifier = modifier) {
        val spanColumns = button.spanColumns.coerceAtLeast(1)
        val spanRows = button.spanRows.coerceAtLeast(1)
        val cellSize = minOf(maxWidth / spanColumns.toFloat(), maxHeight / spanRows.toFloat())
            .coerceAtLeast(48.dp)
        DeckKey(
            modifier = Modifier.fillMaxSize(),
            button = button,
            status = HidStatus(HidConnectionState.Connected, "Preview"),
            appWidgetHost = null,
            appWidgetManager = null,
            visualMode = theme.mode,
            classicSolidButtonBackground = true,
            enabled = true,
            companionConnected = true,
            previewMode = false,
            columns = spanColumns,
            slot = 0,
            cellSize = cellSize,
            spacing = 0.dp,
            contentScale = 0.96f,
            forcedPressed = if (active) true else null,
            forcedVisualStep = if (activeStep != 0) activeStep else null,
            forcedAnalogValue = forcedAnalogValue,
            onPressed = {
                output = "tap · ${button.actionType.name}"
            },
            onTrimStep = { step ->
                output = buttonShapeStudyOutputForStep(button, step)
            },
            onAnalogValue = { point ->
                output = "analog ${"%.1f".format(Locale.US, point.x)}, ${"%.1f".format(Locale.US, point.y)}"
            },
            onPressFeedback = {
                if (!button.isTrimControl()) output = "press"
            },
            onReleaseFeedback = {
                if (!button.isTrimControl()) output = "release"
            },
            onEdit = {},
            onMove = {}
        )
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(7.dp),
            shape = RoundedCornerShape(999.dp),
            color = colors.backgroundGradient.first().copy(alpha = if (theme.darkTheme) 0.50f else 0.66f),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp
        ) {
            Text(
                text = "$title · $displayOutput",
                modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun buttonShapeStudyButton(
    kind: ButtonShapeStudyKind,
    title: String,
    subtitle: String,
    theme: ButtonShapeStudyTheme,
    active: Boolean
): DeckButton {
    val controlStyle = when (kind) {
        ButtonShapeStudyKind.Slider -> DeckControlStyle.TrimSlider
        ButtonShapeStudyKind.Knob -> DeckControlStyle.TrimKnob
        ButtonShapeStudyKind.Wheel -> DeckControlStyle.InfiniteWheel
        ButtonShapeStudyKind.JoyPad -> DeckControlStyle.JoyPad
        ButtonShapeStudyKind.AnalogStick -> DeckControlStyle.AnalogStick
        ButtonShapeStudyKind.Toggle -> DeckControlStyle.CompanionToggle
        else -> DeckControlStyle.Button
    }
    val actionType = when (kind) {
        ButtonShapeStudyKind.Settings -> DeckActionType.Settings
        ButtonShapeStudyKind.Bluetooth -> DeckActionType.BluetoothStatus
        ButtonShapeStudyKind.PageMove -> DeckActionType.NextPage
        ButtonShapeStudyKind.Media,
        ButtonShapeStudyKind.Slider,
        ButtonShapeStudyKind.Knob,
        ButtonShapeStudyKind.Wheel,
        ButtonShapeStudyKind.JoyPad -> DeckActionType.MediaKey
        ButtonShapeStudyKind.Hotkey,
        ButtonShapeStudyKind.Square,
        ButtonShapeStudyKind.Horizontal,
        ButtonShapeStudyKind.Vertical -> DeckActionType.Hotkey
        ButtonShapeStudyKind.TextInput -> DeckActionType.Text
        ButtonShapeStudyKind.RunCommand -> DeckActionType.RunCommand
        ButtonShapeStudyKind.AppCommand -> DeckActionType.AppCommand
        ButtonShapeStudyKind.Utility -> DeckActionType.Utility
        ButtonShapeStudyKind.CompanionCommand -> DeckActionType.CompanionCommand
        ButtonShapeStudyKind.CompanionStatus -> DeckActionType.CompanionStatus
        ButtonShapeStudyKind.AnalogStick,
        ButtonShapeStudyKind.Toggle -> DeckActionType.CompanionControl
    }
    val payload = when (kind) {
        ButtonShapeStudyKind.Settings,
        ButtonShapeStudyKind.Bluetooth,
        ButtonShapeStudyKind.PageMove -> ""
        ButtonShapeStudyKind.Media -> MEDIA_PLAY_PAUSE
        ButtonShapeStudyKind.Slider,
        ButtonShapeStudyKind.Knob,
        ButtonShapeStudyKind.Wheel -> trimPayload(MEDIA_VOLUME_DOWN, MEDIA_VOLUME_UP)
        ButtonShapeStudyKind.JoyPad -> joyPadPayload(
            JoyPadPayloads(
                eightWay = true,
                actions = mapOf(
                    JoyPadDirection.Up to JoyPadActionPayloads(MEDIA_VOLUME_UP),
                    JoyPadDirection.Down to JoyPadActionPayloads(MEDIA_VOLUME_DOWN),
                    JoyPadDirection.Left to JoyPadActionPayloads(MEDIA_PREVIOUS),
                    JoyPadDirection.Right to JoyPadActionPayloads(MEDIA_NEXT)
                )
            )
        )
        ButtonShapeStudyKind.Hotkey,
        ButtonShapeStudyKind.Square,
        ButtonShapeStudyKind.Horizontal,
        ButtonShapeStudyKind.Vertical -> "CTRL+K"
        ButtonShapeStudyKind.TextInput -> "MobileDeck"
        ButtonShapeStudyKind.RunCommand -> "notepad"
        ButtonShapeStudyKind.AppCommand -> DeckActionType.Settings.name
        ButtonShapeStudyKind.Utility -> UTILITY_TIME
        ButtonShapeStudyKind.CompanionCommand -> "demo.command"
        ButtonShapeStudyKind.CompanionStatus -> "system.cpu"
        ButtonShapeStudyKind.AnalogStick -> ANALOG_STICK_DEFAULT_SOURCE
        ButtonShapeStudyKind.Toggle -> "manual.toggle"
    }
    val icon = when (kind) {
        ButtonShapeStudyKind.Settings -> ICON_SETTINGS
        ButtonShapeStudyKind.Bluetooth -> ICON_BLUETOOTH
        ButtonShapeStudyKind.PageMove -> ICON_NEXT
        ButtonShapeStudyKind.Media,
        ButtonShapeStudyKind.Horizontal -> ICON_VOLUME_UP
        ButtonShapeStudyKind.TextInput -> ICON_TEXT
        ButtonShapeStudyKind.RunCommand,
        ButtonShapeStudyKind.AppCommand,
        ButtonShapeStudyKind.Utility -> ICON_APPS
        ButtonShapeStudyKind.CompanionCommand,
        ButtonShapeStudyKind.CompanionStatus,
        ButtonShapeStudyKind.Toggle -> ICON_COMPUTER
        else -> ICON_KEYBOARD
    }
    val companionControl = when (kind) {
        ButtonShapeStudyKind.AnalogStick -> JSONObject()
            .put("kind", "AnalogStick")
            .put("source", ANALOG_STICK_DEFAULT_SOURCE)
            .put("deadZone", DEFAULT_ANALOG_STICK_DEAD_ZONE.toDouble())
            .toString()
        ButtonShapeStudyKind.Toggle -> JSONObject()
            .put("kind", "Toggle")
            .put("source", "manual.toggle")
            .put("value", active)
            .toString()
        else -> ""
    }
    return DeckButton(
        id = 4000 + kind.ordinal,
        title = title,
        subtitle = subtitle,
        icon = icon,
        iconImageUri = "",
        displayMode = if (kind == ButtonShapeStudyKind.Vertical) DeckDisplayMode.KeywordOnly else DeckDisplayMode.IconAndText,
        actionType = actionType,
        payload = payload,
        color = buttonShapeStudyAccent(kind, theme),
        position = 0,
        spanColumns = when (kind) {
            ButtonShapeStudyKind.Horizontal -> 2
            else -> 1
        },
        spanRows = when (kind) {
            ButtonShapeStudyKind.Vertical -> 2
            else -> 1
        },
        controlStyle = controlStyle,
        companionControl = companionControl
    )
}

private fun buttonShapeStudyOutputForStep(button: DeckButton, step: Int): String {
    return when (button.controlStyle) {
        DeckControlStyle.AnalogStick -> {
            val value = analogStickValueForStep(step)
            val x = value.optDouble("x", 0.0)
            val y = value.optDouble("y", 0.0)
            "analog ${"%.1f".format(Locale.US, x)}, ${"%.1f".format(Locale.US, y)}"
        }
        DeckControlStyle.JoyPad -> {
            val steps = joyPadActiveCardinalSteps(step)
            val label = if (steps.isEmpty()) {
                "center"
            } else {
                steps.joinToString("+") { activeStep ->
                    when (activeStep) {
                        JOYPAD_STEP_UP -> "up"
                        JOYPAD_STEP_DOWN -> "down"
                        JOYPAD_STEP_LEFT -> "left"
                        JOYPAD_STEP_RIGHT -> "right"
                        else -> ""
                    }
                }
            }
            "d-pad $label"
        }
        DeckControlStyle.InfiniteWheel -> "wheel notch $step"
        DeckControlStyle.TrimSlider,
        DeckControlStyle.TrimKnob -> "control step $step"
        DeckControlStyle.CompanionToggle -> "toggle"
        DeckControlStyle.Button -> "tap"
    }
}

@Composable
private fun ButtonShapeStudyNormalContent(
    kind: ButtonShapeStudyKind,
    visualShape: ButtonVisualShape,
    active: Boolean,
    contentColor: Color,
    accent: Color
) {
    val icon = when (kind) {
        ButtonShapeStudyKind.Horizontal -> Icons.Filled.VolumeUp
        ButtonShapeStudyKind.Vertical -> Icons.Filled.Keyboard
        ButtonShapeStudyKind.Settings -> Icons.Filled.Settings
        ButtonShapeStudyKind.Bluetooth -> Icons.Filled.Bluetooth
        ButtonShapeStudyKind.PageMove -> Icons.Filled.SkipNext
        ButtonShapeStudyKind.Media -> Icons.Filled.VolumeUp
        ButtonShapeStudyKind.Hotkey -> Icons.Filled.Keyboard
        ButtonShapeStudyKind.TextInput -> Icons.Filled.TextFields
        ButtonShapeStudyKind.RunCommand -> Icons.Filled.Code
        ButtonShapeStudyKind.AppCommand -> Icons.Filled.Apps
        ButtonShapeStudyKind.Utility -> Icons.Filled.Tune
        ButtonShapeStudyKind.CompanionCommand -> Icons.Filled.Computer
        ButtonShapeStudyKind.CompanionStatus -> Icons.Filled.Link
        else -> Icons.Filled.PlayArrow
    }
    val title = when (kind) {
        ButtonShapeStudyKind.Settings -> "Settings"
        ButtonShapeStudyKind.Bluetooth -> "Bluetooth"
        ButtonShapeStudyKind.PageMove -> "Page"
        ButtonShapeStudyKind.Media -> "Volume"
        ButtonShapeStudyKind.Hotkey -> "Ctrl + K"
        ButtonShapeStudyKind.TextInput -> "Text"
        ButtonShapeStudyKind.RunCommand -> "Run"
        ButtonShapeStudyKind.AppCommand -> "App"
        ButtonShapeStudyKind.Utility -> "Utility"
        ButtonShapeStudyKind.CompanionCommand -> "PC Action"
        ButtonShapeStudyKind.CompanionStatus -> "Companion"
        ButtonShapeStudyKind.Vertical -> "Fn"
        else -> "Play"
    }
    val subtitle = when (kind) {
        ButtonShapeStudyKind.CompanionStatus -> if (active) "Connected" else "Disconnected"
        ButtonShapeStudyKind.Bluetooth -> if (active) "HID connected" else "Ready"
        ButtonShapeStudyKind.PageMove -> "Next page"
        ButtonShapeStudyKind.Hotkey -> "Keyboard input"
        ButtonShapeStudyKind.TextInput -> "Send phrase"
        ButtonShapeStudyKind.RunCommand -> "PowerShell"
        ButtonShapeStudyKind.CompanionCommand -> "Companion only"
        else -> "Button action"
    }
    if (visualShape == ButtonVisualShape.Horizontal) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(42.dp),
                tint = if (active) Color.White else contentColor
            )
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.64f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 18.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(if (visualShape == ButtonVisualShape.Vertical) 34.dp else 48.dp),
                tint = if (active) Color.White else contentColor
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                maxLines = 1
            )
        }
    }
    if (active) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.62f)
                .height(3.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(accent.copy(alpha = 0.78f))
        )
    }
}

@Composable
private fun ButtonShapeStudySliderGraphic(
    horizontal: Boolean,
    active: Boolean,
    contentColor: Color,
    accent: Color,
    consoleStyle: Boolean
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val trackLength = if (horizontal) size.width * 0.68f else size.height * 0.58f
        val trackThickness = if (consoleStyle) 8.dp.toPx() else 7.dp.toPx()
        val knobRadius = minOf(size.width, size.height) * if (consoleStyle) 0.17f else 0.15f
        val progress = if (active) 0.72f else 0.5f
        val start = if (horizontal) {
            Offset(center.x - trackLength / 2f, center.y)
        } else {
            Offset(center.x, center.y + trackLength / 2f)
        }
        val end = if (horizontal) {
            Offset(center.x + trackLength / 2f, center.y)
        } else {
            Offset(center.x, center.y - trackLength / 2f)
        }
        val knob = Offset(
            x = start.x + (end.x - start.x) * progress,
            y = start.y + (end.y - start.y) * progress
        )
        drawLine(
            color = Color.Black.copy(alpha = 0.36f),
            start = start,
            end = end,
            strokeWidth = trackThickness,
            cap = StrokeCap.Round
        )
        drawLine(
            color = accent.copy(alpha = if (active) 0.94f else 0.58f),
            start = start,
            end = knob,
            strokeWidth = trackThickness,
            cap = StrokeCap.Round
        )
        repeat(7) { index ->
            val tickFraction = index / 6f
            val tickCenter = Offset(
                x = start.x + (end.x - start.x) * tickFraction,
                y = start.y + (end.y - start.y) * tickFraction
            )
            val tickStart = if (horizontal) tickCenter + Offset(0f, -22.dp.toPx()) else tickCenter + Offset(-20.dp.toPx(), 0f)
            val tickEnd = if (horizontal) tickCenter + Offset(0f, -10.dp.toPx()) else tickCenter + Offset(-8.dp.toPx(), 0f)
            drawLine(
                color = contentColor.copy(alpha = if (tickFraction == 0.5f) 0.86f else 0.42f),
                start = tickStart,
                end = tickEnd,
                strokeWidth = 1.5.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        drawCircle(Color.Black.copy(alpha = 0.32f), knobRadius * 1.10f, knob + Offset(0f, knobRadius * 0.18f))
        drawCircle(contentColor.copy(alpha = 0.90f), knobRadius, knob)
        val markerStart = if (horizontal) knob + Offset(0f, -knobRadius * 0.52f) else knob + Offset(knobRadius * 0.52f, 0f)
        val markerEnd = if (horizontal) knob + Offset(0f, knobRadius * 0.52f) else knob + Offset(-knobRadius * 0.52f, 0f)
        drawLine(accent, markerStart, markerEnd, strokeWidth = 2.2.dp.toPx(), cap = StrokeCap.Round)
        drawLine(contentColor.copy(alpha = 0.50f), center + Offset(-8.dp.toPx(), 0f), center + Offset(8.dp.toPx(), 0f), 1.dp.toPx())
    }
}

@Composable
private fun ButtonShapeStudyKnobGraphic(
    active: Boolean,
    contentColor: Color,
    accent: Color,
    infinite: Boolean
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f - 6.dp.toPx())
        val radius = minOf(size.width, size.height) * if (infinite) 0.34f else 0.30f
        val tickRadius = radius * 1.26f
        val tickCount = if (infinite) INFINITE_WHEEL_NOTCHES_PER_REVOLUTION else 17
        repeat(tickCount) { index ->
            val fraction = index / tickCount.toFloat()
            val angle = if (infinite) {
                fraction * 360f
            } else {
                -126f + fraction * 252f
            } - 90f
            val rad = Math.toRadians(angle.toDouble()).toFloat()
            val outer = center + Offset(cos(rad), sin(rad)) * tickRadius
            val inner = center + Offset(cos(rad), sin(rad)) * (tickRadius - if (index % 3 == 0) 10.dp.toPx() else 6.dp.toPx())
            val topTick = if (infinite) index == 0 else index == tickCount / 2
            drawLine(
                color = if (topTick) accent.copy(alpha = if (active) 0.92f else 0.72f) else contentColor.copy(alpha = if (infinite) 0.36f else 0.42f),
                start = inner,
                end = outer,
                strokeWidth = if (topTick) 1.8.dp.toPx() else 1.2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        drawCircle(Color.Black.copy(alpha = 0.42f), radius * 1.08f, center + Offset(0f, radius * 0.10f))
        drawCircle(contentColor.copy(alpha = 0.12f), radius * 1.02f, center)
        drawCircle(Color.Black.copy(alpha = 0.26f), radius, center)
        if (active) {
            drawCircle(
                color = accent.copy(alpha = 0.42f),
                radius = radius * 1.12f,
                center = center,
                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        val markerAngle = Math.toRadians((if (active) 38f else 0f) - 90.0).toFloat()
        val markerOuter = center + Offset(cos(markerAngle), sin(markerAngle)) * (radius * 0.76f)
        val markerInner = center + Offset(cos(markerAngle), sin(markerAngle)) * (radius * 0.36f)
        drawLine(
            color = if (active) accent else contentColor.copy(alpha = 0.78f),
            start = markerInner,
            end = markerOuter,
            strokeWidth = 3.dp.toPx(),
            cap = StrokeCap.Round
        )
        drawCircle(accent.copy(alpha = if (active) 0.90f else 0.52f), 4.dp.toPx(), center + Offset(0f, -tickRadius))
    }
}

@Composable
private fun ButtonShapeStudyJoyPadGraphic(
    active: Boolean,
    activeStep: Int,
    contentColor: Color,
    accent: Color
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f - 4.dp.toPx())
        val padSize = minOf(size.width, size.height) * 0.68f
        val half = padSize * 0.46f
        val armHalf = padSize * 0.15f
        fun dPadPath(offset: Offset = Offset.Zero): Path {
            return Path().apply {
                moveTo(center.x - armHalf + offset.x, center.y - half + offset.y)
                lineTo(center.x + armHalf + offset.x, center.y - half + offset.y)
                lineTo(center.x + armHalf + offset.x, center.y - armHalf + offset.y)
                lineTo(center.x + half + offset.x, center.y - armHalf + offset.y)
                lineTo(center.x + half + offset.x, center.y + armHalf + offset.y)
                lineTo(center.x + armHalf + offset.x, center.y + armHalf + offset.y)
                lineTo(center.x + armHalf + offset.x, center.y + half + offset.y)
                lineTo(center.x - armHalf + offset.x, center.y + half + offset.y)
                lineTo(center.x - armHalf + offset.x, center.y + armHalf + offset.y)
                lineTo(center.x - half + offset.x, center.y + armHalf + offset.y)
                lineTo(center.x - half + offset.x, center.y - armHalf + offset.y)
                lineTo(center.x - armHalf + offset.x, center.y - armHalf + offset.y)
                close()
            }
        }
        fun drawArrow(arrowCenter: Offset, labelAngle: Float, activeBlock: Boolean) {
            val rad = Math.toRadians(labelAngle.toDouble()).toFloat()
            val arrowTip = arrowCenter + Offset(cos(rad), sin(rad)) * (padSize * 0.06f)
            val left = arrowCenter + Offset(cos(rad + 2.55f), sin(rad + 2.55f)) * (padSize * 0.05f)
            val right = arrowCenter + Offset(cos(rad - 2.55f), sin(rad - 2.55f)) * (padSize * 0.05f)
            val path = Path().apply {
                moveTo(arrowTip.x, arrowTip.y)
                lineTo(left.x, left.y)
                lineTo(right.x, right.y)
                close()
            }
            if (!active || activeBlock) {
                drawPath(path, Color.White.copy(alpha = if (activeBlock) 0.96f else 0.36f))
            }
        }
        fun drawActiveArm(step: Int) {
            val topLeft: Offset
            val rectSize: Size
            when (step) {
                JOYPAD_STEP_UP -> {
                    topLeft = Offset(center.x - armHalf, center.y - half)
                    rectSize = Size(armHalf * 2f, half + armHalf)
                }
                JOYPAD_STEP_DOWN -> {
                    topLeft = Offset(center.x - armHalf, center.y - armHalf)
                    rectSize = Size(armHalf * 2f, half + armHalf)
                }
                JOYPAD_STEP_LEFT -> {
                    topLeft = Offset(center.x - half, center.y - armHalf)
                    rectSize = Size(half + armHalf, armHalf * 2f)
                }
                else -> {
                    topLeft = Offset(center.x - armHalf, center.y - armHalf)
                    rectSize = Size(half + armHalf, armHalf * 2f)
                }
            }
            drawRoundRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.22f),
                        accent.copy(alpha = 0.88f),
                        accent.copy(alpha = 0.58f)
                    ),
                    center = center + Offset(-padSize * 0.10f, -padSize * 0.14f),
                    radius = padSize * 0.58f
                ),
                topLeft = topLeft,
                size = rectSize,
                cornerRadius = CornerRadius(armHalf * 0.55f, armHalf * 0.55f)
            )
        }
        val selected = if (activeStep != 0) activeStep else if (active) JOYPAD_STEP_UP_LEFT else 0
        val activeSteps = joyPadActiveCardinalSteps(selected)
        drawPath(
            path = dPadPath(Offset(0f, padSize * 0.035f)),
            color = Color.Black.copy(alpha = 0.30f)
        )
        drawPath(
            path = dPadPath(),
            brush = Brush.radialGradient(
                colors = listOf(
                    contentColor.copy(alpha = if (active) 0.24f else 0.16f),
                    Color.Black.copy(alpha = if (active) 0.24f else 0.30f),
                    Color.Black.copy(alpha = 0.34f)
                ),
                center = Offset(center.x - padSize * 0.10f, center.y - padSize * 0.14f),
                radius = padSize * 0.82f
            )
        )
        activeSteps.forEach { drawActiveArm(it) }
        drawPath(
            path = dPadPath(),
            color = if (activeSteps.isNotEmpty()) accent.copy(alpha = 0.88f) else contentColor.copy(alpha = 0.28f),
            style = Stroke(width = if (activeSteps.isNotEmpty()) 1.6.dp.toPx() else 1.2.dp.toPx())
        )
        drawCircle(
            color = Color.Black.copy(alpha = 0.34f),
            radius = armHalf * 0.42f,
            center = center
        )
        drawCircle(
            color = contentColor.copy(alpha = if (activeSteps.isNotEmpty()) 0.80f else 0.32f),
            radius = armHalf * 0.16f,
            center = center
        )
        drawArrow(Offset(center.x, center.y - padSize * 0.31f), -90f, activeSteps.contains(JOYPAD_STEP_UP))
        drawArrow(Offset(center.x, center.y + padSize * 0.31f), 90f, activeSteps.contains(JOYPAD_STEP_DOWN))
        drawArrow(Offset(center.x - padSize * 0.31f, center.y), 180f, activeSteps.contains(JOYPAD_STEP_LEFT))
        drawArrow(Offset(center.x + padSize * 0.31f, center.y), 0f, activeSteps.contains(JOYPAD_STEP_RIGHT))
    }
}

@Composable
private fun ButtonShapeStudyAnalogStickGraphic(
    active: Boolean,
    contentColor: Color,
    accent: Color
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2f, size.height / 2f - 4.dp.toPx())
        val radius = minOf(size.width, size.height) * 0.28f
        val stickOffset = if (active) Offset(radius * 0.34f, -radius * 0.24f) else Offset.Zero
        drawCircle(
            color = Color.Black.copy(alpha = 0.34f),
            radius = radius * 1.18f,
            center = center + Offset(0f, radius * 0.08f)
        )
        drawCircle(
            color = contentColor.copy(alpha = 0.12f),
            radius = radius * 1.08f,
            center = center
        )
        drawCircle(
            color = accent.copy(alpha = if (active) 0.42f else 0.18f),
            radius = radius * 1.08f,
            center = center,
            style = Stroke(width = 2.dp.toPx())
        )
        repeat(8) { index ->
            val rad = Math.toRadians((index * 45.0).toDouble()).toFloat()
            drawLine(
                color = contentColor.copy(alpha = 0.24f),
                start = center + Offset(cos(rad), sin(rad)) * radius * 0.74f,
                end = center + Offset(cos(rad), sin(rad)) * radius * 0.96f,
                strokeWidth = 1.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        drawCircle(
            color = Color.Black.copy(alpha = 0.42f),
            radius = radius * 0.54f,
            center = center + stickOffset + Offset(0f, radius * 0.08f)
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    contentColor.copy(alpha = 0.78f),
                    if (active) accent.copy(alpha = 0.82f) else Color.Black.copy(alpha = 0.20f)
                ),
                center = center + stickOffset + Offset(-radius * 0.18f, -radius * 0.18f),
                radius = radius * 0.80f
            ),
            radius = radius * 0.50f,
            center = center + stickOffset
        )
        drawCircle(
            color = contentColor.copy(alpha = 0.42f),
            radius = radius * 0.50f,
            center = center + stickOffset,
            style = Stroke(width = 1.2.dp.toPx())
        )
    }
}

@Composable
private fun ButtonShapeStudyToggleGraphic(
    active: Boolean,
    contentColor: Color,
    accent: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 18.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (active) "Connected" else "Disconnected",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            maxLines = 1
        )
        Spacer(Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .width(132.dp)
                .height(48.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Color.Black.copy(alpha = 0.28f))
                .border(1.dp, contentColor.copy(alpha = 0.24f), RoundedCornerShape(999.dp))
                .padding(4.dp),
            contentAlignment = if (active) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (active) accent else contentColor.copy(alpha = 0.66f))
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Companion value: ${if (active) "ON" else "--"}",
            style = MaterialTheme.typography.bodySmall,
            color = contentColor.copy(alpha = 0.68f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ButtonShapeStudyNotes(
    modifier: Modifier,
    pageSpec: ButtonShapeStudyPageSpec,
    theme: ButtonShapeStudyTheme
) {
    val colors = LocalDeckThemeColors.current
    val consoleStyle = theme.mode == DeckUiMode.Console
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(if (consoleStyle) 24.dp else 12.dp),
        color = if (consoleStyle) colors.consolePreviewBackground.copy(alpha = 0.86f) else colors.cardBackground.copy(alpha = 0.90f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(0.8.dp, colors.cardBorder.copy(alpha = 0.70f), RoundedCornerShape(if (consoleStyle) 24.dp else 12.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "테스트 페이지",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            ButtonShapeStudyNote(pageSpec.title, pageSpec.description)
            ButtonShapeStudyNote("가로형", "아이콘은 왼쪽, 주/부제목은 오른쪽에 고정해 긴 버튼을 텍스트 카드처럼 읽게 합니다.")
            ButtonShapeStudyNote("정사각형", "아이콘 또는 짧은 키워드를 중심에 두고 제목은 최소화합니다.")
            ButtonShapeStudyNote("세로형", "클래식 2칸 슬라이더나 Fn 계열처럼 세로 조작임을 바로 알 수 있어야 합니다.")
            ButtonShapeStudyNote("컨트롤", "슬라이더/노브/휠은 앱 내부 동작과 같은 방향, 중앙 기준선, 최소/최대 마커를 공유합니다.")
            ButtonShapeStudyNote("테마", if (consoleStyle) {
                "콘솔은 버튼 카드 안의 컴포넌트가 카드 재질과 같은 하이라이트를 갖는 방향입니다."
            } else {
                "클래식은 단색 버튼과 명확한 테두리/슬라이더 색을 유지하는 방향입니다."
            })
        }
    }
}

@Composable
private fun ButtonShapeStudyNote(
    title: String,
    body: String
) {
    val colors = LocalDeckThemeColors.current
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            modifier = Modifier
                .padding(top = 7.dp)
                .size(7.dp)
                .clip(CircleShape)
                .background(colors.consoleButtonFeatured)
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )
        }
    }
}

private fun buttonShapeStudyAccent(kind: ButtonShapeStudyKind, theme: ButtonShapeStudyTheme): Color {
    if (theme.mode == DeckUiMode.Console) return Color(0xFF3D8EFF)
    return when (kind) {
        ButtonShapeStudyKind.Slider -> Color(0xFF8CC63E)
        ButtonShapeStudyKind.Knob -> Color(0xFF2F8FFF)
        ButtonShapeStudyKind.Wheel -> Color(0xFF8B55E8)
        ButtonShapeStudyKind.JoyPad -> Color(0xFFE47B17)
        ButtonShapeStudyKind.AnalogStick -> Color(0xFF35AFC8)
        ButtonShapeStudyKind.Toggle -> Color(0xFF2ECA73)
        else -> ClassicButtonAccent
    }
}

private fun buttonShapeStudyBrush(
    kind: ButtonShapeStudyKind,
    theme: ButtonShapeStudyTheme,
    active: Boolean
): Brush {
    val accent = buttonShapeStudyAccent(kind, theme)
    if (theme.mode == DeckUiMode.Classic) {
        return Brush.verticalGradient(
            listOf(
                accent.copy(alpha = if (active) 0.86f else 0.70f),
                accent.copy(alpha = if (active) 0.78f else 0.62f)
            )
        )
    }
    val baseTop = if (theme.darkTheme) Color(0xFF2B4054) else Color(0xFFF9FCFF)
    val baseBottom = if (theme.darkTheme) Color(0xFF1C2D3F) else Color(0xFFE8F1F8)
    return Brush.verticalGradient(
        listOf(
            Color.White.copy(alpha = if (theme.darkTheme) 0.10f else 0.52f).compositeOver(baseTop),
            if (active) accent.copy(alpha = 0.20f).compositeOver(baseTop) else baseTop,
            baseBottom
        )
    )
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

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
                        subtitle = "Control",
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
private fun ConsoleMenuDetailCard(category: ConsoleSettingsCategory) {
    val colors = LocalDeckThemeColors.current
    val accent = colors.consoleButtonFeatured
    ConsoleSettingsCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ConsoleSettingsIconTile(Icons.Filled.Help)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(consoleMenuDetailTitleRes(category)),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Text(
                    text = stringResource(consoleMenuDetailBodyRes(category)),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary
                )
                consoleMenuDetailItemRes(category).forEach { resId ->
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 6.dp)
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(accent)
                        )
                        Text(
                            text = stringResource(resId),
                            style = MaterialTheme.typography.bodySmall,
                            color = colors.textSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@StringRes
private fun consoleMenuDetailTitleRes(category: ConsoleSettingsCategory): Int {
    return when (category) {
        ConsoleSettingsCategory.PcConnection -> R.string.console_detail_pc_title
        ConsoleSettingsCategory.Layout -> R.string.console_detail_layout_title
        ConsoleSettingsCategory.Background -> R.string.console_detail_background_title
        ConsoleSettingsCategory.Controls -> R.string.console_detail_controls_title
        ConsoleSettingsCategory.App -> R.string.console_detail_app_title
    }
}

@StringRes
private fun consoleMenuDetailBodyRes(category: ConsoleSettingsCategory): Int {
    return when (category) {
        ConsoleSettingsCategory.PcConnection -> if (BuildConfig.DEBUG) {
            R.string.console_detail_pc_body
        } else {
            R.string.console_detail_pc_body_release
        }
        ConsoleSettingsCategory.Layout -> R.string.console_detail_layout_body
        ConsoleSettingsCategory.Background -> R.string.console_detail_background_body
        ConsoleSettingsCategory.Controls -> R.string.console_detail_controls_body
        ConsoleSettingsCategory.App -> R.string.console_detail_app_body
    }
}

private fun consoleMenuDetailItemRes(category: ConsoleSettingsCategory): List<Int> {
    return when (category) {
        ConsoleSettingsCategory.PcConnection -> if (BuildConfig.DEBUG) {
            listOf(
                R.string.console_detail_pc_item_companion,
                R.string.console_detail_pc_item_bluetooth,
                R.string.console_detail_pc_item_status
            )
        } else {
            listOf(
                R.string.console_detail_pc_item_bluetooth,
                R.string.console_detail_pc_item_status
            )
        }
        ConsoleSettingsCategory.Layout -> listOf(
            R.string.console_detail_layout_item_preview,
            R.string.console_detail_layout_item_modes,
            R.string.console_detail_layout_item_pages
        )
        ConsoleSettingsCategory.Background -> listOf(
            R.string.console_detail_background_item_panel,
            R.string.console_detail_background_item_font,
            R.string.console_detail_background_item_theme
        )
        ConsoleSettingsCategory.Controls -> listOf(
            R.string.console_detail_controls_item_swipe,
            R.string.console_detail_controls_item_animation,
            R.string.console_detail_controls_item_vibration
        )
        ConsoleSettingsCategory.App -> listOf(
            R.string.console_detail_app_item_transfer,
            R.string.console_detail_app_item_tutorial,
            R.string.console_detail_app_item_debug
        )
    }
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
                ConsolePillButton(text = stringResource(R.string.console_tutorial_button), onClick = onShowClassicTutorial)
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
private fun CompanionSettingsCard(
    settings: CompanionSettings,
    status: CompanionConnectionStatus,
    hidStatus: HidStatus,
    routeActive: Boolean,
    consoleStyle: Boolean,
    syncViewToPc: Boolean,
    followPcView: Boolean,
    onSettingsChange: (CompanionSettings) -> Unit,
    onRouteActiveChange: (Boolean) -> Unit,
    onSyncViewToPcChange: (Boolean) -> Unit,
    onFollowPcViewChange: (Boolean) -> Unit,
    onTestCompanion: () -> Unit,
    onScanCompanionQr: () -> Unit,
    onSendDeckToCompanion: () -> Unit,
    onApplyDeckFromCompanion: () -> Unit
) {
    if (consoleStyle) {
        ConsoleCompanionSettingsCard(
            settings = settings,
            status = status,
            syncViewToPc = syncViewToPc,
            followPcView = followPcView,
            onSyncViewToPcChange = onSyncViewToPcChange,
            onFollowPcViewChange = onFollowPcViewChange,
            onTestCompanion = onTestCompanion,
            onScanCompanionQr = onScanCompanionQr
        )
    } else {
        ClassicCompanionSettingsCard(
            settings = settings,
            status = status,
            hidStatus = hidStatus,
            routeActive = routeActive,
            onSettingsChange = onSettingsChange,
            onRouteActiveChange = onRouteActiveChange,
            syncViewToPc = syncViewToPc,
            followPcView = followPcView,
            onSyncViewToPcChange = onSyncViewToPcChange,
            onFollowPcViewChange = onFollowPcViewChange,
            onTestCompanion = onTestCompanion,
            onScanCompanionQr = onScanCompanionQr,
            onSendDeckToCompanion = onSendDeckToCompanion,
            onApplyDeckFromCompanion = onApplyDeckFromCompanion
        )
    }
}

@Composable
private fun ConsoleCompanionSettingsCard(
    settings: CompanionSettings,
    status: CompanionConnectionStatus,
    syncViewToPc: Boolean,
    followPcView: Boolean,
    onSyncViewToPcChange: (Boolean) -> Unit,
    onFollowPcViewChange: (Boolean) -> Unit,
    onTestCompanion: () -> Unit,
    onScanCompanionQr: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val accent = colors.consoleButtonFeatured
    ConsoleSettingsCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ConsoleSettingsIconTile(Icons.Filled.Link)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.companion_settings_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Text(
                    text = stringResource(R.string.companion_settings_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            SettingsStatusBadge(companionConnectionState(status))
        }
        Text(
            text = if (status.connected) {
                stringResource(R.string.companion_pc_ready)
            } else {
                status.message.ifBlank { stringResource(R.string.companion_disconnected) }
            },
            style = MaterialTheme.typography.bodySmall,
            color = colors.textSecondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ClassicEditDialogButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.companion_scan_qr),
                icon = Icons.Filled.QrCodeScanner,
                consoleStyle = true,
                onClick = onScanCompanionQr
            )
            ClassicEditDialogButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.companion_test_connection),
                highlighted = status.connected,
                consoleStyle = true,
                enabled = settings.isConfigured(),
                onClick = onTestCompanion
            )
        }
        if (status.connected || BuildConfig.DEBUG) {
            CompanionViewSyncOptions(
                syncViewToPc = syncViewToPc,
                followPcView = followPcView,
                consoleStyle = true,
                onSyncViewToPcChange = onSyncViewToPcChange,
                onFollowPcViewChange = onFollowPcViewChange
            )
        }
    }
}

@Composable
private fun ClassicCompanionSettingsCard(
    settings: CompanionSettings,
    status: CompanionConnectionStatus,
    hidStatus: HidStatus,
    routeActive: Boolean,
    onSettingsChange: (CompanionSettings) -> Unit,
    onRouteActiveChange: (Boolean) -> Unit,
    syncViewToPc: Boolean,
    followPcView: Boolean,
    onSyncViewToPcChange: (Boolean) -> Unit,
    onFollowPcViewChange: (Boolean) -> Unit,
    onTestCompanion: () -> Unit,
    onScanCompanionQr: () -> Unit,
    onSendDeckToCompanion: () -> Unit,
    onApplyDeckFromCompanion: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val accent = ClassicButtonAccent
    val shape = RoundedCornerShape(8.dp)
    val canUpdateBundle = settings.isConfigured() &&
        status.capabilities.contains("mobiledeck.bundle.update")
    val canGetBundle = settings.isConfigured() &&
        status.capabilities.contains("mobiledeck.bundle.get")
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = colors.cardBackground,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.border(1.dp, accent.copy(alpha = 0.46f), shape),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(accent.copy(alpha = if (isSystemInDarkTheme()) 0.5f else 0.2f))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SettingsIconTile(Icons.Filled.Link, accent)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.companion_settings_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = stringResource(R.string.companion_settings_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                SettingsSwitch(
                    checked = settings.enabled,
                    accent = accent,
                    onCheckedChange = { onSettingsChange(settings.copy(enabled = it)) }
                )
            }
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ClassicRouteStatusRow(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.Computer,
                        title = stringResource(R.string.companion_connected),
                        subtitle = stringResource(companionConnectionState(status).labelRes()),
                        active = status.connected,
                        connected = status.connected,
                        accent = accent
                    )
                    ClassicRouteStatusRow(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Filled.Bluetooth,
                        title = stringResource(R.string.companion_bluetooth_fallback),
                        subtitle = stringResource(hidStatus.state.labelRes()),
                        active = !status.connected && hidStatus.state == HidConnectionState.Connected,
                        connected = hidStatus.state == HidConnectionState.Connected,
                        accent = Color(0xFF6B8CA8)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1.25f),
                        value = settings.endpoint,
                        onValueChange = { onSettingsChange(settings.copy(endpoint = it)) },
                        singleLine = true,
                        label = { Text(stringResource(R.string.companion_endpoint)) },
                        placeholder = { Text("ws://192.168.0.2:17652") }
                    )
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = settings.pairingToken,
                        onValueChange = { onSettingsChange(settings.copy(pairingToken = it)) },
                        singleLine = true,
                        label = { Text(stringResource(R.string.companion_pairing_token)) }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ClassicEditDialogButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.companion_scan_qr),
                        icon = Icons.Filled.QrCodeScanner,
                        onClick = onScanCompanionQr
                    )
                    ClassicEditDialogButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.companion_test_connection),
                        highlighted = status.connected,
                        enabled = settings.isConfigured(),
                        onClick = onTestCompanion
                    )
                    ClassicEditDialogButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.companion_send_deck),
                        enabled = canUpdateBundle,
                        onClick = onSendDeckToCompanion
                    )
                    ClassicEditDialogButton(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.companion_apply_deck),
                        enabled = canGetBundle,
                        onClick = onApplyDeckFromCompanion
                    )
                }
                if (status.connected || BuildConfig.DEBUG) {
                    CompanionViewSyncOptions(
                        syncViewToPc = syncViewToPc,
                        followPcView = followPcView,
                        consoleStyle = false,
                        onSyncViewToPcChange = onSyncViewToPcChange,
                        onFollowPcViewChange = onFollowPcViewChange
                    )
                }
            }
        }
    }
}

@Composable
private fun ConsoleNestedPanel(
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = LocalDeckThemeColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(colors.consoleButtonDefault.copy(alpha = 0.58f))
            .border(1.dp, colors.cardBorder.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        content = content
    )
}

@Composable
private fun ConsoleRouteBox(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    active: Boolean,
    accent: Color,
    connected: Boolean,
    compact: Boolean = false,
) {
    val colors = LocalDeckThemeColors.current
    val shape = RoundedCornerShape(16.dp)
    Surface(
        modifier = modifier.heightIn(min = if (compact) 62.dp else 88.dp),
        shape = shape,
        color = if (active) accent else colors.consoleButtonDefault.copy(alpha = if (!compact) 0.74f else 0.42f),
        contentColor = if (active) Color.White else colors.textPrimary,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .border(
                    1.dp,
                    if (active) Color.White.copy(alpha = 0.34f) else colors.cardBorder.copy(alpha = 0.24f),
                    shape
                )
                .padding(if (compact) 10.dp else 12.dp),
            verticalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 7.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(if (compact) 6.dp else 7.dp)
                        .clip(CircleShape)
                        .background(if (connected) Color(0xFF2ECA73) else Color(0xFFE05252))
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (active) Color.White else colors.textPrimary.copy(alpha = if (compact) 0.62f else 1f),
                    modifier = Modifier.size(if (compact) 19.dp else 22.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = if (active) Color.White else colors.textPrimary.copy(alpha = if (compact) 0.68f else 1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (active) Color.White.copy(alpha = 0.82f) else colors.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ClassicRouteStatusRow(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    subtitle: String,
    active: Boolean,
    connected: Boolean,
    accent: Color
) {
    val colors = LocalDeckThemeColors.current
    val background = if (active) accent.copy(alpha = 0.22f) else colors.toggleBackground.copy(alpha = 0.28f)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .border(
                1.dp,
                if (active) accent.copy(alpha = 0.52f) else colors.cardBorder.copy(alpha = 0.28f),
                RoundedCornerShape(8.dp)
            )
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        SettingsIconTile(icon, if (active) accent else colors.neutralIconBackground)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
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
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (connected) Color(0xFF2ECA73) else Color(0xFFE05252))
        )
    }
}

@Composable
private fun BluetoothHidUnsupportedDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.ok))
            }
        },
        title = {
            Text(stringResource(R.string.pc_connection_bluetooth_unavailable_title))
        },
        text = {
            Text(stringResource(R.string.pc_connection_bluetooth_unavailable_desc))
        }
    )
}

@Composable
private fun PcConnectionFallbackSummaryCard(
    status: HidStatus,
    consoleStyle: Boolean,
    onShowFallback: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val accent = if (consoleStyle) colors.consoleButtonFeatured else ClassicButtonAccent
    val content: @Composable ColumnScope.() -> Unit = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (consoleStyle) {
                ConsoleSettingsIconTile(Icons.Filled.Bluetooth)
            } else {
                SettingsIconTile(Icons.Filled.Bluetooth, colors.neutralIconBackground)
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = stringResource(R.string.pc_connection_fallback_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = stringResource(R.string.pc_connection_fallback_desc, stringResource(status.state.labelRes())),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            ClassicEditDialogButton(
                text = stringResource(R.string.pc_connection_show_fallback),
                highlighted = false,
                consoleStyle = consoleStyle,
                onClick = onShowFallback
            )
        }
    }
    if (consoleStyle) {
        ConsoleSettingsCard(content = content)
    } else {
        SettingsCard(accent = accent, content = content)
    }
}

private fun companionConnectionState(status: CompanionConnectionStatus): HidConnectionState {
    return if (status.connected) {
        HidConnectionState.Connected
    } else {
        HidConnectionState.Disconnected
    }
}

@Composable
private fun pcConnectionTargetText(
    companionStatus: CompanionConnectionStatus,
    hidStatus: HidStatus
): String {
    return when {
        companionStatus.connected -> companionStatus.appName.ifBlank { stringResource(R.string.companion_connected) }
        hidStatus.state == HidConnectionState.Connected -> stringResource(hidStatus.state.labelRes())
        else -> stringResource(R.string.status_disconnected)
    }
}

private fun pcConnectionConnected(
    companionStatus: CompanionConnectionStatus,
    hidStatus: HidStatus
): Boolean {
    return companionStatus.connected || hidStatus.state == HidConnectionState.Connected
}

private fun companionPairingUriFromIntent(intent: Intent?): String? {
    if (intent?.action != Intent.ACTION_VIEW) return null
    val uri = intent.data ?: return null
    return uri.toString().takeIf { uri.isCompanionPairingUri() }
}

private fun parseCompanionPairingQr(rawValue: String): Result<CompanionSettings> {
    return runCatching {
        val value = rawValue.trim()
        if (value.isCompanionPairingUri()) {
            return@runCatching parseCompanionPairingUri(Uri.parse(value))
        }
        val root = JSONObject(rawValue)
        require(root.optString("format") == "mobiledeck.pairing") {
            "Unsupported Companion pairing QR"
        }
        require(root.optInt("version") in 1..3) {
            "Unsupported Companion pairing version"
        }
        val endpoint = normalizeCompanionEndpoint(root.optString("endpoint"))
        val pairingToken = root.optString("pairingToken")
            .ifBlank { root.optString("token") }
            .ifBlank {
                root.optJSONArray("nextPairingTokens")?.optString(0).orEmpty()
            }
            .trim()
        if (pairingToken.isBlank()) {
            root.optString("uri").trim().takeIf { it.isCompanionPairingUri() }?.let { uri ->
                return@runCatching parseCompanionPairingUri(Uri.parse(uri))
            }
        }
        companionEndpointValidationMessage(endpoint)?.let { message -> error(message) }
        require(pairingToken.length >= 6) {
            "Invalid Companion pairing token"
        }
        CompanionSettings(
            enabled = true,
            endpoint = endpoint,
            pairingToken = pairingToken
        )
    }
}

private fun String.isCompanionPairingUri(): Boolean {
    return runCatching { Uri.parse(this).isCompanionPairingUri() }.getOrDefault(false)
}

private fun Uri.isCompanionPairingUri(): Boolean {
    return scheme == "mobiledeck" && host == "pair"
}

private fun parseCompanionPairingUri(uri: Uri): CompanionSettings {
    require(uri.isCompanionPairingUri()) {
        "Unsupported Companion pairing QR"
    }
    require(uri.getQueryParameter("v") == "2" || uri.getQueryParameter("v") == "3") {
        "Unsupported Companion pairing version"
    }
    val endpoint = normalizeCompanionEndpoint(uri.getQueryParameter("e").orEmpty())
    val pairingToken = uri.getQueryParameter("t").orEmpty().trim()
    companionEndpointValidationMessage(endpoint)?.let { message -> error(message) }
    require(pairingToken.length >= 6) {
        "Invalid Companion pairing token"
    }
    return CompanionSettings(
        enabled = true,
        endpoint = endpoint,
        pairingToken = pairingToken
    )
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
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    Switch(
        checked = checked,
        enabled = enabled,
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
private fun CompanionViewSyncOptions(
    syncViewToPc: Boolean,
    followPcView: Boolean,
    consoleStyle: Boolean,
    onSyncViewToPcChange: (Boolean) -> Unit,
    onFollowPcViewChange: (Boolean) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val accent = if (consoleStyle) colors.consoleButtonFeatured else ClassicButtonAccent
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(if (consoleStyle) 14.dp else 8.dp))
            .background(
                if (consoleStyle) {
                    colors.consoleButtonDefault.copy(alpha = 0.34f)
                } else {
                    colors.toggleBackground.copy(alpha = 0.26f)
                }
            )
            .border(
                1.dp,
                if (consoleStyle) colors.cardBorder.copy(alpha = 0.22f) else accent.copy(alpha = 0.22f),
                RoundedCornerShape(if (consoleStyle) 14.dp else 8.dp)
            )
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.companion_view_sync_title),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary
        )
        CompanionViewSyncRow(
            title = stringResource(R.string.companion_sync_view_to_pc),
            subtitle = stringResource(R.string.companion_sync_view_to_pc_desc),
            checked = syncViewToPc,
            accent = accent,
            onCheckedChange = onSyncViewToPcChange
        )
        CompanionViewSyncRow(
            title = stringResource(R.string.companion_follow_pc_view),
            subtitle = stringResource(R.string.companion_follow_pc_view_desc),
            checked = followPcView,
            accent = accent,
            onCheckedChange = onFollowPcViewChange
        )
    }
}

@Composable
private fun CompanionViewSyncRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    accent: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        SettingsSwitch(
            checked = checked,
            accent = accent,
            onCheckedChange = onCheckedChange
        )
    }
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
    showGuideCards: Boolean,
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

            if (showGuideCards) {
                EditorGuideCard(
                    title = stringResource(R.string.layout_editor_tutorial_title),
                    body = stringResource(R.string.layout_editor_tutorial_body),
                    consoleStyle = false
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
private fun EditorGuideCard(
    title: String,
    body: String,
    consoleStyle: Boolean
) {
    val colors = LocalDeckThemeColors.current
    val shape = RoundedCornerShape(if (consoleStyle) 14.dp else 8.dp)
    val accent = if (consoleStyle) colors.consoleButtonFeatured else ClassicButtonAccent
    val cardColor = if (consoleStyle) {
        colors.consoleButtonDefault
    } else {
        colors.cardBackground.copy(alpha = 0.96f)
    }
    val borderColor = if (consoleStyle) {
        colors.cardBorder.copy(alpha = 0.42f)
    } else {
        colors.cardBorder.copy(alpha = 0.72f)
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (consoleStyle) {
                    Modifier.consoleButtonDropShadow(
                        shape = shape,
                        darkTheme = isSystemInDarkTheme(),
                        pressed = false,
                        drawHairline = true
                    )
                } else {
                    Modifier
                }
            )
            .border(
                1.dp,
                borderColor,
                shape
            ),
        shape = shape,
        color = cardColor,
        contentColor = colors.textPrimary,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(20.dp)
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = body,
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
    companionConnected: Boolean = true,
    codexTaskStates: Map<Int, CodexButtonTaskState> = emptyMap(),
    codexCommandFailures: Map<Int, String> = emptyMap(),
    codexSubmittingButtons: Map<Int, Boolean> = emptyMap(),
    codexCompanionConfigured: Boolean = false,
    codexCompanionConnected: Boolean = false,
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
    onAnalogValue: (DeckButton, AnalogStickPoint) -> Unit = { _, _ -> },
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
                onAnalogValue = onAnalogValue,
                onButtonTouchStarted = onButtonTouchStarted,
                onButtonTouchEnded = onButtonTouchEnded,
                companionConnected = companionConnected,
                codexTaskStates = codexTaskStates,
                codexCommandFailures = codexCommandFailures,
                codexSubmittingButtons = codexSubmittingButtons,
                codexCompanionConfigured = codexCompanionConfigured,
                codexCompanionConnected = codexCompanionConnected
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
                val targetButtons = deckPages.firstOrNull { it.id == target.pageId }?.classicButtons.orEmpty()
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
                    companionConnected = companionConnected,
                    codexTaskStates = codexTaskStates,
                    codexCommandFailures = codexCommandFailures,
                    codexSubmittingButtons = codexSubmittingButtons,
                    codexCompanionConfigured = codexCompanionConfigured,
                    codexCompanionConnected = codexCompanionConnected,
                    previewMode = previewMode,
                    showTitle = target.pageId == deckPages.firstOrNull()?.id,
                    onButtonPressed = onButtonPressed,
                    onTrimStep = onTrimStep,
                    onAnalogValue = onAnalogValue,
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
                val isVideo = remember(background.imageUri) { context.isVideoAsset(background.imageUri) }

                if (isGif) {
                    ClassicGifBackground(
                        modifier = modifier,
                        uriString = background.imageUri,
                        fallbackColor = background.color
                    )
                } else if (isVideo) {
                    Box(modifier = modifier.background(background.color))
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

private fun Context.isVideoAsset(uriString: String): Boolean {
    val uri = Uri.parse(uriString)
    val mimeType = runCatching { contentResolver.getType(uri) }.getOrNull().orEmpty()
    return mimeType.startsWith("video/", ignoreCase = true) ||
        uriString.substringAfterLast('.', "").lowercase(Locale.US) in setOf("mp4", "webm")
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
    onAnalogValue: (DeckButton, AnalogStickPoint) -> Unit = { _, _ -> },
    onButtonTouchStarted: () -> Unit,
    onButtonTouchEnded: () -> Unit,
    companionConnected: Boolean = true,
    codexTaskStates: Map<Int, CodexButtonTaskState> = emptyMap(),
    codexCommandFailures: Map<Int, String> = emptyMap(),
    codexSubmittingButtons: Map<Int, Boolean> = emptyMap(),
    codexCompanionConfigured: Boolean = false,
    codexCompanionConnected: Boolean = false
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
                    Box(modifier = Modifier.matchParentSize()) {
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
                            val targetButtons = deckPages.firstOrNull { it.id == target.pageId }?.consoleButtons.orEmpty()
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
                                companionConnected = companionConnected,
                                codexTaskStates = codexTaskStates,
                                codexCommandFailures = codexCommandFailures,
                                codexSubmittingButtons = codexSubmittingButtons,
                                codexCompanionConfigured = codexCompanionConfigured,
                                codexCompanionConnected = codexCompanionConnected,
                                onButtonPressed = onButtonPressed,
                                onTrimStep = onTrimStep,
                                onAnalogValue = onAnalogValue,
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
    onAnalogValue: (DeckButton, AnalogStickPoint) -> Unit,
    onButtonTouchStarted: () -> Unit,
    onButtonTouchEnded: () -> Unit,
    companionConnected: Boolean,
    codexTaskStates: Map<Int, CodexButtonTaskState> = emptyMap(),
    codexCommandFailures: Map<Int, String> = emptyMap(),
    codexSubmittingButtons: Map<Int, Boolean> = emptyMap(),
    codexCompanionConfigured: Boolean = false,
    codexCompanionConnected: Boolean = false
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
                            companionConnected = companionConnected,
                            codexTaskState = codexTaskStates[button.id],
                            codexCommandFailure = codexCommandFailures[button.id],
                            codexSubmitting = codexSubmittingButtons[button.id] == true,
                            codexCompanionConfigured = codexCompanionConfigured,
                            codexCompanionConnected = codexCompanionConnected,
                            enabled = true,
                            previewMode = false,
                            columns = rowButtons.size.coerceAtLeast(1),
                            slot = button.position,
                            cellSize = rowHeight,
                            spacing = spacing,
                            onPressed = { onButtonPressed(button) },
                            onTrimStep = { step -> onTrimStep(button, step) },
                            onAnalogValue = { point -> onAnalogValue(button, point) },
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
        val compactHairline = size.minDimension < 56.dp.toPx()
        val color = consoleHairlineColor(darkTheme).copy(
            alpha = consoleHairlineColor(darkTheme).alpha * if (compactHairline) 0.42f else 1f
        )
        onDrawBehind {
            translate(left = offset.x.toFloat(), top = offset.y.toFloat()) {
                when (outline) {
                    is Outline.Rectangle -> {
                        if (compactHairline) {
                            drawRect(
                                color = color,
                                topLeft = outline.rect.topLeft,
                                size = outline.rect.size,
                                style = Stroke(width = 1.dp.toPx())
                            )
                        } else {
                            drawRect(
                                color = color,
                                topLeft = outline.rect.topLeft,
                                size = outline.rect.size
                            )
                        }
                    }
                    is Outline.Rounded -> {
                        val roundRect = outline.roundRect
                        if (compactHairline) {
                            drawRoundRect(
                                color = color,
                                topLeft = Offset(roundRect.left, roundRect.top),
                                size = Size(
                                    width = roundRect.right - roundRect.left,
                                    height = roundRect.bottom - roundRect.top
                                ),
                                cornerRadius = roundRect.topLeftCornerRadius,
                                style = Stroke(width = 1.dp.toPx())
                            )
                        } else {
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
                    }
                    is Outline.Generic -> drawPath(
                        path = outline.path,
                        color = color,
                        style = if (compactHairline) Stroke(width = 1.dp.toPx()) else Fill
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
    val orderedButtons = (media + regular + bottom).distinctBy { it.id }
    val rowCount = ((orderedButtons.size + 3) / 4)
        .coerceIn(1, MAX_CONSOLE_LAYOUT_ROWS)
    val rowsList = orderedButtons.distributeEvenly(rowCount)

    return if (rowsList.isEmpty()) {
        List(rows.coerceAtLeast(1)) { emptyList() }
    } else {
        rowsList
    }
}

private fun List<DeckButton>.distributeEvenly(rowCount: Int): List<List<DeckButton>> {
    if (isEmpty() || rowCount <= 0) return emptyList()
    val safeRowCount = rowCount.coerceAtMost(size)
    val baseSize = size / safeRowCount
    val extraRows = size % safeRowCount
    var cursor = 0
    return List(safeRowCount) { index ->
        val rowSize = baseSize + if (index < extraRows) 1 else 0
        subList(cursor, cursor + rowSize).also {
            cursor += rowSize
        }
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
                radius = if (darkTheme) 14.dp else 16.dp,
                spread = if (darkTheme) 2.dp else 1.5.dp,
                offset = DpOffset(0.dp, 2.dp),
                color = Color.Black.copy(alpha = if (darkTheme) 0.22f else 0.08f)
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
        pressed && darkTheme -> 0.18f
        pressed -> 0.08f
        darkTheme -> 0.22f
        else -> 0.08f
    }
    return this
        .dropShadow(
            shape = shape,
            shadow = DropShadow(
                radius = if (pressed) 1.5.dp else 2.dp,
                spread = if (pressed) 0.8.dp else 1.dp,
                offset = DpOffset(0.dp, 0.dp),
                color = Color.Black.copy(alpha = if (darkTheme) 0.14f else 0.04f)
            )
        )
        .dropShadow(
            shape = shape,
            shadow = DropShadow(
                radius = if (pressed) 7.dp else 12.dp,
                spread = if (pressed) 1.dp else 2.dp,
                offset = DpOffset(0.dp, if (pressed) 1.dp else 2.dp),
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
    showGuideCards: Boolean,
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

        if (showGuideCards) {
            item {
                EditorGuideCard(
                    title = stringResource(R.string.console_layout_editor_tutorial_title),
                    body = stringResource(
                        if (editMode == ConsoleLayoutEditMode.Layout) {
                            R.string.console_layout_editor_tutorial_layout_body
                        } else {
                            R.string.console_layout_editor_tutorial_buttons_body
                        }
                    ),
                    consoleStyle = true
                )
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
                        val selectedIndex = if (fromRowIndex == toRowIndex && toIndex > fromIndex) {
                            toIndex - 1
                        } else {
                            toIndex
                        }.coerceIn(
                            0,
                            (rows.getOrNull(toRowIndex).orEmpty().size + if (fromRowIndex == toRowIndex) 0 else 1)
                                .minus(1)
                                .coerceAtLeast(0)
                        )
                        selectedButtonSlot = ConsoleButtonSlotKey(toRowIndex, selectedIndex)
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
    dotColor: Color,
    dotSize: Dp = 4.dp,
    dotGap: Dp = 4.dp,
    rowGap: Dp = 5.dp
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(rowGap),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        repeat(3) {
            Row(horizontalArrangement = Arrangement.spacedBy(dotGap)) {
                repeat(2) {
                    Box(
                        modifier = Modifier
                            .size(dotSize)
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
    val shape = RoundedCornerShape(9.dp)
    Surface(
        modifier = modifier
            .size(width = 30.dp, height = 38.dp)
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
        shape = shape,
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
        Box(
            modifier = Modifier.border(
                1.dp,
                when {
                    dragging || selected -> Color.White.copy(alpha = 0.42f)
                    darkTheme -> Color.White.copy(alpha = 0.16f)
                    else -> Color(0xFF0A3147).copy(alpha = 0.22f)
                },
                shape
            ),
            contentAlignment = Alignment.Center
        ) {
            ConsoleDragDots(
                dotColor = if (dragging || selected || darkTheme) Color.White else Color(0xFF0A3147),
                dotSize = 4.dp,
                dotGap = 4.dp,
                rowGap = 4.dp
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
        fun previewBoundsFor(previewRows: List<List<DeckButton>>): List<List<ConsoleButtonPreviewBounds>> {
            return previewRows.mapIndexed { rowIndex, row ->
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
        }
        val buttonPreviewBounds = previewBoundsFor(safeRows)
        fun insertedButtonRows(move: ButtonPreviewMove?): List<List<DeckButton>>? {
            if (move == null) return null
            return insertConsoleRowItem(
                rows = safeRows,
                fromRowIndex = move.fromRowIndex,
                fromIndex = move.fromIndex,
                toRowIndex = move.toRowIndex,
                toIndex = move.toIndex
            )?.rows
        }
        fun findButtonSlot(previewRows: List<List<DeckButton>>, button: DeckButton): ConsoleButtonSlotKey? {
            previewRows.forEachIndexed { rowIndex, row ->
                val buttonIndex = row.indexOfFirst { it.id == button.id }
                if (buttonIndex >= 0) return ConsoleButtonSlotKey(rowIndex, buttonIndex)
            }
            return null
        }
        fun buttonInsertPreviewOffset(rowIndex: Int, buttonIndex: Int, move: ButtonPreviewMove?): ConsoleButtonPreviewOffset {
            val targetRows = insertedButtonRows(move) ?: return ConsoleButtonPreviewOffset.Zero
            val button = safeRows.getOrNull(rowIndex)?.getOrNull(buttonIndex) ?: return ConsoleButtonPreviewOffset.Zero
            val targetSlot = findButtonSlot(targetRows, button) ?: return ConsoleButtonPreviewOffset.Zero
            if (targetSlot.rowIndex == rowIndex && targetSlot.buttonIndex == buttonIndex) return ConsoleButtonPreviewOffset.Zero
            val currentBounds = buttonPreviewBounds.getOrNull(rowIndex)?.getOrNull(buttonIndex)
                ?: return ConsoleButtonPreviewOffset.Zero
            val targetBounds = previewBoundsFor(targetRows).getOrNull(targetSlot.rowIndex)?.getOrNull(targetSlot.buttonIndex)
                ?: return ConsoleButtonPreviewOffset.Zero
            return ConsoleButtonPreviewOffset(
                x = targetBounds.left - currentBounds.left,
                y = rowTops[targetSlot.rowIndex] - rowTops[rowIndex]
            )
        }
        fun buttonInsertPreviewScale(rowIndex: Int, buttonIndex: Int, move: ButtonPreviewMove?): ConsoleButtonPreviewScale {
            val targetRows = insertedButtonRows(move) ?: return ConsoleButtonPreviewScale.Normal
            val button = safeRows.getOrNull(rowIndex)?.getOrNull(buttonIndex) ?: return ConsoleButtonPreviewScale.Normal
            val targetSlot = findButtonSlot(targetRows, button) ?: return ConsoleButtonPreviewScale.Normal
            val currentBounds = buttonPreviewBounds.getOrNull(rowIndex)?.getOrNull(buttonIndex)
                ?: return ConsoleButtonPreviewScale.Normal
            val targetBounds = previewBoundsFor(targetRows).getOrNull(targetSlot.rowIndex)?.getOrNull(targetSlot.buttonIndex)
                ?: return ConsoleButtonPreviewScale.Normal
            return ConsoleButtonPreviewScale(
                x = (targetBounds.width.value / currentBounds.width.value.coerceAtLeast(1f)).coerceAtLeast(0.1f),
                y = (rowHeights[targetSlot.rowIndex].value / rowHeights[rowIndex].value.coerceAtLeast(1f)).coerceAtLeast(0.1f)
            )
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
            val targetIndex = targetRowBounds.indexOfFirst { targetBounds ->
                draggedCenterX < targetBounds.left + targetBounds.width / 2f
            }.takeIf { it >= 0 } ?: targetRowBounds.size
            val result = insertConsoleRowItem(
                rows = safeRows,
                fromRowIndex = fromRowIndex,
                fromIndex = fromIndex,
                toRowIndex = targetRowIndex,
                toIndex = targetIndex
            )
            return if (result == null) {
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
            buttonDragOffsetX = dragX
            buttonDragOffsetY = dragY
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
                                val previewButtonOffset = buttonInsertPreviewOffset(rowIndex, buttonIndex, buttonPreviewMove)
                                val previewButtonScale = buttonInsertPreviewScale(rowIndex, buttonIndex, buttonPreviewMove)
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
                                            translationX = if (draggingButton) buttonDragOffsetX else previewX
                                            translationY = if (draggingButton) buttonDragOffsetY else previewY
                                            alpha = if (draggingButton) 0.72f else 1f
                                            transformOrigin = TransformOrigin(0f, 0.5f)
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
                                                .offset(x = (-15).dp)
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
                                                    val selectedIndex = if (move.fromRowIndex == move.toRowIndex && move.toIndex > move.fromIndex) {
                                                        move.toIndex - 1
                                                    } else {
                                                        move.toIndex
                                                    }.coerceIn(
                                                        0,
                                                        (safeRows.getOrNull(move.toRowIndex).orEmpty().size + if (move.fromRowIndex == move.toRowIndex) 0 else 1)
                                                            .minus(1)
                                                            .coerceAtLeast(0)
                                                    )
                                                    onSelectButton(move.toRowIndex, selectedIndex, button)
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

private data class ConsoleRowInsertResult<T>(
    val rows: List<List<T>>,
    val insertedIndex: Int
)

private fun <T> insertConsoleRowItem(
    rows: List<List<T>>,
    fromRowIndex: Int,
    fromIndex: Int,
    toRowIndex: Int,
    toIndex: Int
): ConsoleRowInsertResult<T>? {
    if (fromRowIndex !in rows.indices || toRowIndex !in rows.indices) return null
    if (fromIndex !in rows[fromRowIndex].indices) return null

    val mutableRows = rows.map { it.toMutableList() }.toMutableList()
    val movingItem = mutableRows[fromRowIndex].removeAt(fromIndex)
    val adjustedInsertIndex = if (fromRowIndex == toRowIndex && toIndex > fromIndex) {
        toIndex - 1
    } else {
        toIndex
    }.coerceIn(0, mutableRows[toRowIndex].size)

    if (fromRowIndex == toRowIndex && adjustedInsertIndex == fromIndex) return null
    mutableRows[toRowIndex].add(adjustedInsertIndex, movingItem)
    return ConsoleRowInsertResult(
        rows = mutableRows.map { it.toList() },
        insertedIndex = adjustedInsertIndex
    )
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
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                val previewCellSize = if (maxWidth < maxHeight) maxWidth else maxHeight
                val visualShape = remember(maxWidth, maxHeight) { buttonVisualShape(maxWidth, maxHeight) }
                ClassicDeckKeyContent(
                    button = button,
                    status = HidStatus(HidConnectionState.Disconnected, ""),
                    contentColor = contentColor.copy(alpha = if (darkTheme) 1f else 0.86f),
                    cellSize = previewCellSize,
                    visualShape = visualShape,
                    liftedContent = true
                )
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
    companionConnected: Boolean = true,
    codexTaskStates: Map<Int, CodexButtonTaskState> = emptyMap(),
    codexCommandFailures: Map<Int, String> = emptyMap(),
    codexSubmittingButtons: Map<Int, Boolean> = emptyMap(),
    codexCompanionConfigured: Boolean = false,
    codexCompanionConnected: Boolean = false,
    previewMode: Boolean,
    showTitle: Boolean,
    onButtonPressed: (DeckButton) -> Unit,
    onTrimStep: (DeckButton, Int) -> Unit,
    onAnalogValue: (DeckButton, AnalogStickPoint) -> Unit,
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
                        companionConnected = companionConnected,
                        codexTaskState = codexTaskStates[button.id],
                        codexCommandFailure = codexCommandFailures[button.id],
                        codexSubmitting = codexSubmittingButtons[button.id] == true,
                        codexCompanionConfigured = codexCompanionConfigured,
                        codexCompanionConnected = codexCompanionConnected,
                        enabled = true,
                        previewMode = previewMode,
                        columns = safeColumns,
                        slot = buttonPosition,
                        cellSize = cellSize,
                        spacing = spacing,
                        swapPreviewOffset = previewOffsetFor(button),
                        onPressed = { onButtonPressed(button) },
                        onTrimStep = { step -> onTrimStep(button, step) },
                        onAnalogValue = { point -> onAnalogValue(button, point) },
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
    analogDeadZone: Float,
    onPress: () -> Unit,
    onRelease: () -> Unit,
    onPreviewStep: (Int) -> Unit,
    onPreviewValue: (Float) -> Unit,
    onPreviewAnalogValue: (AnalogStickPoint) -> Unit,
    onActiveStepChange: (Int) -> Unit,
    onStep: (Int) -> Unit,
    onAnalogValue: (AnalogStickPoint) -> Unit
): Modifier {
    if (!enabled) return this
    return pointerInput(style, joyPadEightWay, analogDeadZone, onStep, onPreviewStep, onPreviewValue, onPreviewAnalogValue, onActiveStepChange, onAnalogValue) {
        fun levelForMagnitude(magnitude: Float): Int {
            return when {
                magnitude >= 0.58f -> 2
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

        fun sliderMagnitudeFor(position: Offset, previousMagnitude: Float): Float {
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
            val outsideTrack = if (horizontal) {
                position.x !in start..end || position.y !in 0f..size.height.toFloat()
            } else {
                position.x !in 0f..size.width.toFloat() || position.y !in start..end
            }
            return if (abs(signedMagnitude) <= 0.16f && outsideTrack) {
                previousMagnitude
            } else {
                signedMagnitude
            }
        }

        fun sliderStepFor(position: Offset, previousStep: Int): Int {
            return stepFromSignedMagnitude(sliderMagnitudeFor(position, previousStep / 2f))
        }

        fun knobMagnitudeFor(position: Offset, previousMagnitude: Float): Float {
            val center = Offset(size.width / 2f, size.height / 2f)
            val fromCenter = position - center
            if (abs(fromCenter.x) < size.width * 0.08f && abs(fromCenter.y) < size.height * 0.08f) {
                return 0f
            }
            val angleDegrees = atan2(fromCenter.y, fromCenter.x) * 180f / Math.PI.toFloat()
            val rawDeltaFromTop = normalizedDegrees(angleDegrees + 90f)
            val deltaFromTop = if (abs(rawDeltaFromTop) > 126f && abs(previousMagnitude) > 0.01f) {
                if (previousMagnitude > 0f) 126f else -126f
            } else {
                rawDeltaFromTop.coerceIn(-126f, 126f)
            }
            val signedMagnitude = deltaFromTop / 126f
            return if (abs(signedMagnitude) <= 0.16f && (
                    position.x !in 0f..size.width.toFloat() ||
                        position.y !in 0f..size.height.toFloat()
                )
            ) {
                previousMagnitude
            } else {
                signedMagnitude
            }
        }

        fun knobStepFor(position: Offset, previousStep: Int): Int {
            return stepFromSignedMagnitude(knobMagnitudeFor(position, previousStep / 2f))
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
            } else if (style == DeckControlStyle.JoyPad || style == DeckControlStyle.AnalogStick) {
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
            var wheelVisualPosition = 0f
            var lastPreviewValue = 0f
            var lastAnalogEmitMillis = 0L
            var lastAnalogPoint = AnalogStickPoint(0f, 0f, false)
            val wheelDegreesPerNotch = 360f / INFINITE_WHEEL_NOTCHES_PER_REVOLUTION.toFloat()
            fun previewValueFor(position: Offset): Float {
                return when (style) {
                    DeckControlStyle.TrimSlider -> sliderMagnitudeFor(position, lastPreviewValue)
                    DeckControlStyle.TrimKnob -> knobMagnitudeFor(position, lastPreviewValue)
                    else -> lastPreviewValue
                }
            }
            fun setJoyPadStep(nextStep: Int) {
                if (style != DeckControlStyle.JoyPad || nextStep == joyPadActiveStep) return
                joyPadActiveStep = nextStep
                onStep(joyPadActiveStep)
            }
            fun analogPointChangedEnough(point: AnalogStickPoint): Boolean {
                return point.active != lastAnalogPoint.active ||
                    abs(point.x - lastAnalogPoint.x) >= 0.025f ||
                    abs(point.y - lastAnalogPoint.y) >= 0.025f
            }
            fun setAnalogPoint(position: Offset, force: Boolean = false) {
                if (style != DeckControlStyle.AnalogStick) return
                val point = analogStickPointForPosition(position, size, analogDeadZone)
                onPreviewAnalogValue(point)
                val now = SystemClock.uptimeMillis()
                if (force || now - lastAnalogEmitMillis >= 16L || analogPointChangedEnough(point)) {
                    lastAnalogEmitMillis = now
                    lastAnalogPoint = point
                    onAnalogValue(point)
                }
            }
            try {
                onPress()
                onPreviewStep(lastStep)
                if (style == DeckControlStyle.TrimSlider || style == DeckControlStyle.TrimKnob) {
                    lastPreviewValue = previewValueFor(currentPosition)
                    onPreviewValue(lastPreviewValue)
                }
                if (style == DeckControlStyle.AnalogStick) {
                    setAnalogPoint(currentPosition)
                } else if (style == DeckControlStyle.JoyPad) {
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
                                val angleDelta = normalizedDegrees(nextAngle - wheelPreviousAngle!!)
                                wheelAngleAccumulator += angleDelta
                                wheelVisualPosition += angleDelta / wheelDegreesPerNotch
                                onPreviewValue(wheelVisualPosition)
                                while (wheelAngleAccumulator >= wheelDegreesPerNotch) {
                                    onStep(1)
                                    wheelAngleAccumulator -= wheelDegreesPerNotch
                                }
                                while (wheelAngleAccumulator <= -wheelDegreesPerNotch) {
                                    onStep(-1)
                                    wheelAngleAccumulator += wheelDegreesPerNotch
                                }
                            }
                            if (nextAngle != null) {
                                wheelPreviousAngle = nextAngle
                            }
                        } else if (style == DeckControlStyle.AnalogStick) {
                            setAnalogPoint(currentPosition)
                        } else {
                            lastStep = stepFor(totalDrag, down.position, currentPosition, dragDelta, lastStep)
                            onPreviewStep(lastStep)
                            if (style == DeckControlStyle.TrimSlider || style == DeckControlStyle.TrimKnob) {
                                lastPreviewValue = previewValueFor(currentPosition)
                                onPreviewValue(lastPreviewValue)
                            }
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
                if (style == DeckControlStyle.AnalogStick) {
                    val center = AnalogStickPoint(0f, 0f, false)
                    onPreviewAnalogValue(center)
                    lastAnalogPoint = center
                    onAnalogValue(center)
                } else if (style == DeckControlStyle.JoyPad) {
                    setJoyPadStep(0)
                } else {
                    onActiveStepChange(0)
                }
                onRelease()
                if (style != DeckControlStyle.InfiniteWheel) {
                    onPreviewStep(0)
                    onPreviewValue(0f)
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
    visualStep: Int,
    visualValue: Float = visualStep / 2f,
    analogValue: Offset = Offset.Zero
) {
    val isConsole = visualMode == DeckUiMode.Console
    val colors = LocalDeckThemeColors.current
    val darkTheme = isSystemInDarkTheme()
    val trimAccent = if (isConsole) Color(0xFF3D8EFF) else button.color
    val inactiveStroke = contentColor.copy(alpha = if (darkTheme) 0.34f else 0.48f)
    val style = button.controlStyle
    val companionValueLabel = companionControlValueLabel(button)
    val activeFraction = visualValue.coerceIn(-1f, 1f)
    Box(
        modifier = modifier.padding(if (isConsole) 8.dp else 7.dp),
        contentAlignment = Alignment.Center
    ) {
        if (style == DeckControlStyle.CompanionToggle) {
            CompanionToggleContent(
                modifier = Modifier.matchParentSize(),
                button = button,
                contentColor = contentColor,
                accent = trimAccent
            )
        } else if (style == DeckControlStyle.JoyPad) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val padSize = minOf(size.width, size.height) * if (isConsole) 0.76f else 0.72f
                val center = Offset(size.width / 2f, size.height / 2f)
                val selected = joyPadBaseStep(visualStep)
                val activeSteps = joyPadActiveCardinalSteps(selected)
                fun dPadPath(offset: Offset = Offset.Zero): Path {
                    val half = padSize * 0.46f
                    val armHalf = padSize * 0.15f
                    return Path().apply {
                        moveTo(center.x - armHalf + offset.x, center.y - half + offset.y)
                        lineTo(center.x + armHalf + offset.x, center.y - half + offset.y)
                        lineTo(center.x + armHalf + offset.x, center.y - armHalf + offset.y)
                        lineTo(center.x + half + offset.x, center.y - armHalf + offset.y)
                        lineTo(center.x + half + offset.x, center.y + armHalf + offset.y)
                        lineTo(center.x + armHalf + offset.x, center.y + armHalf + offset.y)
                        lineTo(center.x + armHalf + offset.x, center.y + half + offset.y)
                        lineTo(center.x - armHalf + offset.x, center.y + half + offset.y)
                        lineTo(center.x - armHalf + offset.x, center.y + armHalf + offset.y)
                        lineTo(center.x - half + offset.x, center.y + armHalf + offset.y)
                        lineTo(center.x - half + offset.x, center.y - armHalf + offset.y)
                        lineTo(center.x - armHalf + offset.x, center.y - armHalf + offset.y)
                        close()
                    }
                }
                fun drawArrow(step: Int, arrowCenter: Offset) {
                    val active = activeSteps.contains(step)
                    if (activeSteps.isNotEmpty() && !active) return
                    val arrowColor = if (active) Color.White.copy(alpha = 0.94f) else contentColor.copy(alpha = 0.84f)
                    val arrowSize = padSize * 0.075f
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
                fun drawActiveArm(step: Int) {
                    val half = padSize * 0.46f
                    val armHalf = padSize * 0.15f
                    val topLeft: Offset
                    val rectSize: Size
                    when (step) {
                        JOYPAD_STEP_UP -> {
                            topLeft = Offset(center.x - armHalf, center.y - half)
                            rectSize = Size(armHalf * 2f, half + armHalf)
                        }
                        JOYPAD_STEP_DOWN -> {
                            topLeft = Offset(center.x - armHalf, center.y - armHalf)
                            rectSize = Size(armHalf * 2f, half + armHalf)
                        }
                        JOYPAD_STEP_LEFT -> {
                            topLeft = Offset(center.x - half, center.y - armHalf)
                            rectSize = Size(half + armHalf, armHalf * 2f)
                        }
                        else -> {
                            topLeft = Offset(center.x - armHalf, center.y - armHalf)
                            rectSize = Size(half + armHalf, armHalf * 2f)
                        }
                    }
                    drawRoundRect(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = if (isConsole) 0.22f else 0.18f),
                                trimAccent.copy(alpha = if (isConsole) 0.92f else 0.86f),
                                trimAccent.copy(alpha = if (isConsole) 0.58f else 0.66f)
                            ),
                            center = Offset(center.x - padSize * 0.12f, center.y - padSize * 0.16f),
                            radius = padSize * 0.58f
                        ),
                        topLeft = topLeft,
                        size = rectSize,
                        cornerRadius = CornerRadius(armHalf * 0.55f, armHalf * 0.55f)
                    )
                }
                val dpadActive = activeSteps.isNotEmpty()
                drawPath(
                    path = dPadPath(Offset(0f, padSize * 0.035f)),
                    color = Color.Black.copy(alpha = if (darkTheme) 0.40f else 0.16f)
                )
                drawPath(
                    path = dPadPath(),
                    brush = Brush.radialGradient(
                        colors = listOf(
                            contentColor.copy(alpha = if (dpadActive) 0.26f else 0.16f),
                            colors.consoleButtonDefault.copy(alpha = if (isConsole) 0.82f else 0.50f),
                            Color.Black.copy(alpha = if (darkTheme) 0.38f else 0.12f)
                        ),
                        center = Offset(center.x - padSize * 0.12f, center.y - padSize * 0.16f),
                        radius = padSize * 0.86f
                    )
                )
                activeSteps.forEach { drawActiveArm(it) }
                drawPath(
                    path = dPadPath(),
                    color = if (dpadActive) trimAccent.copy(alpha = 0.9f) else inactiveStroke.copy(alpha = 0.46f),
                    style = Stroke(width = if (dpadActive) 1.5.dp.toPx() else 1.dp.toPx())
                )
                drawCircle(
                    color = Color.Black.copy(alpha = if (darkTheme) 0.34f else 0.14f),
                    radius = padSize * 0.055f,
                    center = center
                )
                drawCircle(
                    color = contentColor.copy(alpha = if (dpadActive) 0.82f else 0.34f),
                    radius = padSize * 0.020f,
                    center = center
                )
                drawArrow(JOYPAD_STEP_UP, Offset(center.x, center.y - padSize * 0.31f))
                drawArrow(JOYPAD_STEP_DOWN, Offset(center.x, center.y + padSize * 0.31f))
                drawArrow(JOYPAD_STEP_LEFT, Offset(center.x - padSize * 0.31f, center.y))
                drawArrow(JOYPAD_STEP_RIGHT, Offset(center.x + padSize * 0.31f, center.y))
            }
        } else if (style == DeckControlStyle.AnalogStick) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val padSize = minOf(size.width, size.height) * if (isConsole) 0.72f else 0.68f
                val radius = padSize * 0.38f
                val center = Offset(size.width / 2f, size.height / 2f)
                val active = analogValue.getDistance() > 0.001f
                val clampedAnalog = if (analogValue.getDistance() > 1f) {
                    analogValue / analogValue.getDistance()
                } else {
                    analogValue
                }
                val directionOffset = Offset(clampedAnalog.x * radius * 0.44f, clampedAnalog.y * radius * 0.44f)
                drawCircle(
                    color = Color.Black.copy(alpha = if (darkTheme) 0.42f else 0.18f),
                    radius = radius * 1.24f,
                    center = center + Offset(0f, radius * 0.10f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            contentColor.copy(alpha = if (darkTheme) 0.18f else 0.34f),
                            colors.consoleButtonDefault.copy(alpha = if (isConsole) 0.76f else 0.45f),
                            Color.Black.copy(alpha = if (darkTheme) 0.42f else 0.15f)
                        ),
                        center = Offset(center.x - radius * 0.35f, center.y - radius * 0.40f),
                        radius = radius * 1.40f
                    ),
                    radius = radius * 1.14f,
                    center = center
                )
                drawCircle(
                    color = trimAccent.copy(alpha = if (active) 0.70f else 0.28f),
                    radius = radius * 1.15f,
                    center = center,
                    style = Stroke(width = if (active) 2.2.dp.toPx() else 1.3.dp.toPx())
                )
                repeat(8) { index ->
                    val rad = Math.toRadians((index * 45.0).toDouble()).toFloat()
                    drawLine(
                        color = inactiveStroke.copy(alpha = 0.55f),
                        start = center + Offset(cos(rad), sin(rad)) * radius * 0.84f,
                        end = center + Offset(cos(rad), sin(rad)) * radius * 1.03f,
                        strokeWidth = 1.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
                val stickCenter = center + directionOffset
                drawCircle(
                    color = Color.Black.copy(alpha = if (darkTheme) 0.50f else 0.20f),
                    radius = radius * 0.55f,
                    center = stickCenter + Offset(0f, radius * 0.08f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            contentColor.copy(alpha = 0.78f),
                            if (active) trimAccent.copy(alpha = 0.88f) else colors.consoleButtonDefault.copy(alpha = 0.72f),
                            Color.Black.copy(alpha = if (darkTheme) 0.44f else 0.14f)
                        ),
                        center = stickCenter + Offset(-radius * 0.18f, -radius * 0.20f),
                        radius = radius * 0.82f
                    ),
                    radius = radius * 0.50f,
                    center = stickCenter
                )
                drawCircle(
                    color = contentColor.copy(alpha = 0.36f),
                    radius = radius * 0.50f,
                    center = stickCenter,
                    style = Stroke(width = 1.2.dp.toPx())
                )
            }
        } else if (style == DeckControlStyle.InfiniteWheel) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val wheelRadius = minOf(size.width, size.height) * if (isConsole) 0.38f else 0.34f
                val center = Offset(size.width / 2f, size.height / 2f)
                val rotationOffset = visualValue * (360f / INFINITE_WHEEL_NOTCHES_PER_REVOLUTION.toFloat())
                drawCircle(
                    color = Color.Black.copy(alpha = if (darkTheme) 0.34f else 0.12f),
                    radius = wheelRadius * 1.02f,
                    center = center + Offset(0f, wheelRadius * 0.08f)
                )
                repeat(INFINITE_WHEEL_NOTCHES_PER_REVOLUTION) { index ->
                    val topTick = index == 0
                    val angle = ((index * (360f / INFINITE_WHEEL_NOTCHES_PER_REVOLUTION)) - 90f) *
                        (Math.PI.toFloat() / 180f)
                    val major = index % 3 == 0
                    drawLine(
                        color = when {
                            topTick -> trimAccent.copy(alpha = if (isConsole) 0.96f else 0.86f)
                            major -> trimAccent.copy(alpha = if (isConsole) 0.70f else 0.58f)
                            else -> inactiveStroke.copy(alpha = 0.58f)
                        },
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
                    val topTick = index == 13
                    drawLine(
                        color = if (topTick) {
                            trimAccent.copy(alpha = if (isConsole) 0.94f else 0.82f)
                        } else {
                            inactiveStroke.copy(alpha = if (major) 0.66f else 0.42f)
                        },
                        start = Offset(
                            center.x + cos(tickAngle) * knobRadius * 1.23f,
                            center.y + sin(tickAngle) * knobRadius * 1.23f
                        ),
                        end = Offset(
                            center.x + cos(tickAngle) * knobRadius * if (major) 1.38f else 1.32f,
                            center.y + sin(tickAngle) * knobRadius * if (major) 1.38f else 1.32f
                        ),
                        strokeWidth = if (topTick) 1.8.dp.toPx() else if (major) 1.3.dp.toPx() else 0.9.dp.toPx(),
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
        }
        if (companionValueLabel.isNotBlank() && style != DeckControlStyle.CompanionToggle) {
            Text(
                text = companionValueLabel,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.Black.copy(alpha = if (darkTheme) 0.30f else 0.12f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.92f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun CompanionToggleContent(
    modifier: Modifier = Modifier,
    button: DeckButton,
    contentColor: Color,
    accent: Color
) {
    val control = companionControlData(button)
    val checked = control.booleanValue ?: false
    val valueText = companionControlValueLabel(button)
    Box(
        modifier = modifier.padding(horizontal = 10.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(78.dp)
                .height(36.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(if (checked) accent.copy(alpha = 0.72f) else Color.Black.copy(alpha = 0.22f))
                .border(
                    width = 1.dp,
                    color = if (checked) accent.copy(alpha = 0.92f) else contentColor.copy(alpha = 0.30f),
                    shape = RoundedCornerShape(999.dp)
                )
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(if (checked) Alignment.CenterEnd else Alignment.CenterStart)
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.94f),
                                contentColor.copy(alpha = if (checked) 0.88f else 0.62f),
                                Color.Black.copy(alpha = 0.16f)
                            )
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = if (checked) 0.48f else 0.18f), CircleShape)
            )
        }
        if (valueText.isNotBlank()) {
            Text(
                text = valueText,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(999.dp))
                    .background(Color.Black.copy(alpha = 0.18f))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                style = MaterialTheme.typography.labelSmall,
                color = contentColor.copy(alpha = 0.86f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
    companionConnected: Boolean,
    codexTaskState: CodexButtonTaskState? = null,
    codexCommandFailure: String? = null,
    codexSubmitting: Boolean = false,
    codexCompanionConfigured: Boolean = false,
    codexCompanionConnected: Boolean = false,
    previewMode: Boolean,
    columns: Int,
    slot: Int,
    cellSize: Dp,
    spacing: Dp,
    swapPreviewOffset: Offset = Offset.Zero,
    contentScale: Float = 1f,
    forcedPressed: Boolean? = null,
    forcedVisualStep: Int? = null,
    forcedAnalogValue: Offset? = null,
    onPressed: () -> Unit,
    onTrimStep: (Int) -> Unit = {},
    onAnalogValue: (AnalogStickPoint) -> Unit = {},
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
    val isStrictCodexButton = button.actionType == DeckActionType.CompanionCommand &&
        CodexButtonBindingPayload.parse(button.payload) != null
    val companionUnavailable = !isStrictCodexButton && button.isCompanionOnly() && !companionConnected && !previewMode
    val codexUnavailable = isStrictCodexButton && !codexCompanionConfigured && !previewMode
    val keyEnabled = enabled && !companionUnavailable && !codexUnavailable
    val suppressCodexTap = isStrictCodexButton && !previewMode && (
        codexSubmitting ||
            codexTaskState?.reconnecting == true ||
            codexTaskState?.snapshot?.status?.isActive == true ||
            codexTaskState?.snapshot?.status == CodexJobStatus.Cancelled
        )
    val interactionEnabled = keyEnabled && !suppressCodexTap
    val codexVisualStatus = if (isStrictCodexButton && !previewMode) {
        codexButtonVisualStatus(
            taskState = codexTaskState,
            commandFailureCode = codexCommandFailure,
            submitting = codexSubmitting,
            configured = codexCompanionConfigured,
            connected = codexCompanionConnected
        )
    } else {
        null
    }
    val containerColor = when {
        !keyEnabled -> MaterialTheme.colorScheme.surfaceVariant
        isConsole -> consoleButtonColor(button)
        !classicSolidButtonBackground -> themeColors.cardBackground.copy(alpha = if (darkTheme) 0.9f else 0.96f)
        else -> button.color
    }
    val contentColor = when {
        !keyEnabled -> MaterialTheme.colorScheme.onSurfaceVariant
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
    var trimVisualValue by remember(button.id) { mutableStateOf(0f) }
    var trimRepeatStep by remember(button.id) { mutableStateOf(0) }
    var analogVisualValue by remember(button.id) { mutableStateOf(Offset.Zero) }
    val effectivePressed = forcedPressed ?: touchPressed
    val effectiveTrimVisualStep = forcedVisualStep ?: trimVisualStep
    val effectiveAnalogValue = forcedAnalogValue ?: analogVisualValue
    LaunchedEffect(button.controlStyle, trimVisualStep, trimRepeatStep) {
        if (button.controlStyle != DeckControlStyle.InfiniteWheel &&
            button.controlStyle != DeckControlStyle.JoyPad &&
            button.controlStyle != DeckControlStyle.AnalogStick &&
            trimVisualStep != 0 &&
            trimRepeatStep == 0
        ) {
            delay(180)
            trimVisualStep = 0
        }
    }
    LaunchedEffect(button.id, button.controlStyle, trimRepeatStep) {
        if (!button.controlStyle.usesTrimRepeatFrequency()) return@LaunchedEffect
        var repeatIndex = 0
        while (trimRepeatStep != 0) {
            onTrimStep(trimRepeatStep)
            delay(trimRepeatDelayMillis(trimRepeatStep, repeatIndex))
            repeatIndex += 1
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
        button.actionType == DeckActionType.CompanionStatus && !previewMode -> Modifier
        button.isTrimControl() && !previewMode -> Modifier.trimSwitchGesture(
            enabled = interactionEnabled,
            style = button.controlStyle,
            joyPadEightWay = joyPadPayloadParts(button.payload).eightWay,
            analogDeadZone = companionControlData(button).deadZone,
            onPress = onPressFeedback,
            onRelease = onReleaseFeedback,
            onPreviewStep = { step -> trimVisualStep = step },
            onPreviewValue = { value -> trimVisualValue = value },
            onPreviewAnalogValue = { point ->
                analogVisualValue = Offset(point.x, point.y)
            },
            onActiveStepChange = { step ->
                trimRepeatStep = if (button.controlStyle.usesTrimRepeatFrequency()) step else 0
            },
            onStep = onTrimStep,
            onAnalogValue = onAnalogValue
        )
        previewMode -> Modifier.deckSlotGesture(
            enabled = interactionEnabled,
            onClick = onPressed
        )
        else -> Modifier.deckTapGesture(
            enabled = interactionEnabled,
            onPress = onPressFeedback,
            onRelease = onReleaseFeedback,
            onPressedChange = { touchPressed = it },
            onClick = onPressed
        )
    }
    val surfaceColor = if (effectivePressed && isConsole) {
        themeColors.consoleButtonFeatured
    } else if (effectivePressed) {
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
                        pressed = effectivePressed
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
        if (button.controlStyle == DeckControlStyle.CompanionToggle) {
            TrimSwitchContent(
                modifier = Modifier.fillMaxSize(),
                button = button,
                visualMode = visualMode,
                contentColor = contentColor,
                cellSize = cellSize,
                visualStep = effectiveTrimVisualStep,
                visualValue = trimVisualValue,
                analogValue = effectiveAnalogValue
            )
            return@Surface
        }
        if (button.isTrimControl()) {
            TrimSwitchContent(
                modifier = Modifier.fillMaxSize(),
                button = button,
                visualMode = visualMode,
                contentColor = contentColor,
                cellSize = cellSize,
                visualStep = effectiveTrimVisualStep,
                visualValue = trimVisualValue,
                analogValue = effectiveAnalogValue
            )
            return@Surface
        }
        if (isConsole) {
            ConsoleRaisedButtonFrame(
                modifier = Modifier.fillMaxSize(),
                shape = buttonShape,
                cornerRadius = 18.dp,
                pressed = effectivePressed,
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
        if (companionUnavailable) {
            CompanionOnlyDisabledOverlay(
                modifier = Modifier.matchParentSize(),
                isConsole = isConsole,
                shape = buttonShape
            )
        }
        CodexButtonStatusOverlay(
            status = codexVisualStatus,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(5.dp)
                .widthIn(max = visualWidth - 10.dp)
        )
    }
}

@Composable
private fun CompanionOnlyDisabledOverlay(
    modifier: Modifier,
    isConsole: Boolean,
    shape: Shape
) {
    val overlayColor = if (isConsole) Color(0xFF07111C).copy(alpha = 0.58f) else Color.Black.copy(alpha = 0.34f)
    val accent = if (isConsole) Color(0xFF59B8FF) else ClassicButtonAccent
    Box(
        modifier = modifier
            .clip(shape)
            .background(overlayColor)
            .border(1.dp, accent.copy(alpha = 0.42f), shape)
            .padding(6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(Color.Black.copy(alpha = 0.34f))
                .padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = accent
            )
            Text(
                text = stringResource(R.string.companion_only_badge),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
    val displayCapabilities = button.displayCapabilities()
    val hasTitle = displayTitle.isNotBlank()
    val hasSubtitle = subtitleText.isNotBlank()
    val showText = displayCapabilities.supportsText && cellSize >= 72.dp && button.displayMode != DeckDisplayMode.IconOnly
    val showSubtitle = cellSize >= 86.dp && hasSubtitle
    val horizontalLayout = visualShape == ButtonVisualShape.Horizontal &&
        button.displayMode == DeckDisplayMode.IconAndText &&
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
            horizontalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.widthIn(max = cellSize * 2.4f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (displayCapabilities.supportsIconImage && button.displayMode != DeckDisplayMode.KeywordOnly) {
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
                    modifier = Modifier.weight(1f, fill = false),
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
            if (displayCapabilities.supportsIconImage && (button.displayMode != DeckDisplayMode.KeywordOnly || !showText)) {
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
    companionControlValueLabel(button).takeIf { it.isNotBlank() }?.let { return it }
    return when {
        buttonAppAction(button) == DeckActionType.BluetoothStatus -> stringResource(status.state.labelRes())
        button.actionType == DeckActionType.Utility && button.payload == UTILITY_TIME -> currentDateText()
        button.actionType == DeckActionType.CompanionStatus -> button.subtitle.ifBlank {
            stringResource(R.string.action_companion_status)
        }
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
        DeckActionType.CompanionCommand -> Icons.Filled.Link
        DeckActionType.CompanionControl -> Icons.Filled.Tune
        DeckActionType.CompanionStatus -> Icons.Filled.Info
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
private const val INFINITE_WHEEL_NOTCHES_PER_REVOLUTION = 24
private const val ANALOG_STICK_DEFAULT_SOURCE = "manual.analog"

private fun DeckButton.isTrimControl(): Boolean {
    return controlStyle == DeckControlStyle.TrimSlider ||
        controlStyle == DeckControlStyle.TrimKnob ||
        controlStyle == DeckControlStyle.InfiniteWheel ||
        controlStyle == DeckControlStyle.JoyPad ||
        controlStyle == DeckControlStyle.AnalogStick
}

private fun DeckButton.isCompanionOnly(): Boolean {
    return platformAvailability() == DeckButtonPlatformAvailability.CompanionRequired
}

private fun DeckButton.hasCompanionControl(): Boolean {
    return companionControl.isNotBlank()
}

private fun DeckButton.hasCompanionControlValue(): Boolean {
    return hasCompanionControl() && companionControlData(this).hasExplicitValue
}

private fun DeckButton.hasCompanionNumericValue(): Boolean {
    val control = companionControlData(this)
    return hasCompanionControl() && control.hasExplicitValue && control.numericValue != null
}

private fun DeckButton.usesCompanionControlRoute(): Boolean {
    if (controlStyle == DeckControlStyle.AnalogStick) {
        return false
    }
    if (controlStyle == DeckControlStyle.CompanionToggle) {
        return actionType == DeckActionType.CompanionControl || hasCompanionControlValue()
    }
    if (isTrimControl()) {
        return hasCompanionNumericValue()
    }
    return actionType == DeckActionType.CompanionControl && hasCompanionControlValue()
}

private fun DeckControlStyle.usesTrimRepeatFrequency(): Boolean {
    return this == DeckControlStyle.TrimSlider ||
        this == DeckControlStyle.TrimKnob
}

private fun trimRepeatDelayMillis(step: Int, repeatIndex: Int): Long {
    if (repeatIndex == 0) return 0L
    val baseDelay = when (abs(step)) {
        1 -> 320L
        else -> 210L
    }
    val acceleration = (repeatIndex.coerceAtMost(8) * 8L)
    val minimumDelay = when (abs(step)) {
        1 -> 250L
        else -> 150L
    }
    return (baseDelay - acceleration).coerceAtLeast(minimumDelay)
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
    val press: String
)

private data class JoyPadPayloads(
    val eightWay: Boolean = false,
    val actions: Map<JoyPadDirection, JoyPadActionPayloads> = emptyMap()
) {
    fun action(direction: JoyPadDirection): JoyPadActionPayloads {
        actions[direction]?.let { return it }
        return JoyPadActionPayloads(defaultJoyPadPayloadForDirection(direction, actions))
    }

    fun withDirection(direction: JoyPadDirection, press: String): JoyPadPayloads {
        return copy(actions = actions + (direction to JoyPadActionPayloads(press)))
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

private fun defaultJoyPadPayloadForDirection(
    direction: JoyPadDirection,
    actions: Map<JoyPadDirection, JoyPadActionPayloads>
): String {
    val direct = direction.defaultPressPayload
    return when (direction) {
        JoyPadDirection.UpLeft -> listOf(JoyPadDirection.Up, JoyPadDirection.Left)
        JoyPadDirection.UpRight -> listOf(JoyPadDirection.Up, JoyPadDirection.Right)
        JoyPadDirection.DownLeft -> listOf(JoyPadDirection.Down, JoyPadDirection.Left)
        JoyPadDirection.DownRight -> listOf(JoyPadDirection.Down, JoyPadDirection.Right)
        else -> return direct
    }
        .map { cardinal -> actions[cardinal]?.press?.trim().orEmpty().ifBlank { cardinal.defaultPressPayload } }
        .joinToString("+")
}

private fun joyPadDirectionForStep(step: Int): JoyPadDirection? {
    val baseStep = joyPadBaseStep(step)
    return joyPadAllDirections.firstOrNull { it.step == baseStep }
}

private fun joyPadBaseStep(step: Int): Int {
    return step
}

private fun joyPadActiveCardinalSteps(step: Int): Set<Int> {
    return when (joyPadBaseStep(step)) {
        JOYPAD_STEP_UP -> setOf(JOYPAD_STEP_UP)
        JOYPAD_STEP_DOWN -> setOf(JOYPAD_STEP_DOWN)
        JOYPAD_STEP_LEFT -> setOf(JOYPAD_STEP_LEFT)
        JOYPAD_STEP_RIGHT -> setOf(JOYPAD_STEP_RIGHT)
        JOYPAD_STEP_UP_LEFT -> setOf(JOYPAD_STEP_UP, JOYPAD_STEP_LEFT)
        JOYPAD_STEP_UP_RIGHT -> setOf(JOYPAD_STEP_UP, JOYPAD_STEP_RIGHT)
        JOYPAD_STEP_DOWN_LEFT -> setOf(JOYPAD_STEP_DOWN, JOYPAD_STEP_LEFT)
        JOYPAD_STEP_DOWN_RIGHT -> setOf(JOYPAD_STEP_DOWN, JOYPAD_STEP_RIGHT)
        else -> emptySet()
    }
}

private fun joyPadPayload(payloads: JoyPadPayloads): String {
    val directions = JSONObject()
    joyPadCardinalDirections.forEach { direction ->
        val action = payloads.action(direction)
        directions.put(direction.key, action.press.trim())
    }
    return JSONObject()
        .put("kind", "joypad")
        .put("version", 3)
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
                actions = joyPadCardinalDirections.associateWith { direction ->
                    val raw = directions.opt(direction.key)
                    JoyPadActionPayloads(
                        press = when (raw) {
                            is String -> raw
                            is JSONObject -> raw.optString("press").orEmpty()
                            else -> ""
                        }.ifBlank { direction.defaultPressPayload }
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
    if (button.controlStyle == DeckControlStyle.AnalogStick) {
        return analogStickValueForStep(step).toString()
    }
    if (button.usesCompanionControlRoute()) {
        return companionControlStepValue(button, step)?.toString().orEmpty()
    }
    if (button.controlStyle == DeckControlStyle.JoyPad) {
        val payloads = joyPadPayloadParts(button.payload)
        val direction = joyPadDirectionForStep(step) ?: JoyPadDirection.Up
        return payloads.action(direction).press
    }
    return trimPayloadForStep(button.payload, step)
}

private fun joyPadCardinalPayloadsForStep(button: DeckButton, step: Int): List<String> {
    val payloads = joyPadPayloadParts(button.payload)
    return joyPadActiveCardinalSteps(step)
        .mapNotNull { activeStep -> joyPadDirectionForStep(activeStep) }
        .map { direction -> payloads.action(direction).press }
        .filter { it.isNotBlank() }
}

private const val DEFAULT_ANALOG_STICK_DEAD_ZONE = 0.12f

private data class AnalogStickPoint(
    val x: Float,
    val y: Float,
    val active: Boolean
)

private fun analogStickPointForPosition(position: Offset, size: IntSize, deadZone: Float): AnalogStickPoint {
    val width = size.width.coerceAtLeast(1).toFloat()
    val height = size.height.coerceAtLeast(1).toFloat()
    val center = Offset(width / 2f, height / 2f)
    val radius = (minOf(width, height) / 2f).coerceAtLeast(1f)
    val raw = (position - center) / radius
    val distance = raw.getDistance()
    val normalizedDeadZone = deadZone.coerceIn(0f, 0.75f)
    if (distance <= normalizedDeadZone) return AnalogStickPoint(0f, 0f, false)
    val magnitude = ((distance.coerceAtMost(1f) - normalizedDeadZone) / (1f - normalizedDeadZone))
        .coerceIn(0f, 1f)
    val direction = if (distance > 0f) raw / distance else Offset.Zero
    return AnalogStickPoint(
        x = (direction.x * magnitude).coerceIn(-1f, 1f),
        y = (direction.y * magnitude).coerceIn(-1f, 1f),
        active = magnitude > 0f
    )
}

private fun analogStickValueForPoint(point: AnalogStickPoint, deadZone: Float = DEFAULT_ANALOG_STICK_DEAD_ZONE): JSONObject {
    return JSONObject()
        .put("kind", "AnalogStick")
        .put("x", point.x.toDouble())
        .put("y", point.y.toDouble())
        .put("active", point.active)
        .put("deadZone", deadZone.toDouble())
}

private fun analogStickOffsetFromValue(value: JSONObject): Offset {
    return Offset(
        x = value.optDouble("x", 0.0).toFloat().coerceIn(-1f, 1f),
        y = value.optDouble("y", 0.0).toFloat().coerceIn(-1f, 1f)
    )
}

private fun analogStickValueForStep(step: Int): JSONObject {
    val baseStep = joyPadBaseStep(step)
    val released = baseStep == 0
    val direction = joyPadDirectionForStep(baseStep)
    val diagonal = 0.707
    val (x, y) = when (direction) {
        JoyPadDirection.Up -> 0.0 to -1.0
        JoyPadDirection.Down -> 0.0 to 1.0
        JoyPadDirection.Left -> -1.0 to 0.0
        JoyPadDirection.Right -> 1.0 to 0.0
        JoyPadDirection.UpLeft -> -diagonal to -diagonal
        JoyPadDirection.UpRight -> diagonal to -diagonal
        JoyPadDirection.DownLeft -> -diagonal to diagonal
        JoyPadDirection.DownRight -> diagonal to diagonal
        null -> 0.0 to 0.0
    }
    return JSONObject()
        .put("kind", "AnalogStick")
        .put("x", if (released) 0.0 else x)
        .put("y", if (released) 0.0 else y)
        .put("active", !released)
        .put("direction", if (released) "center" else direction?.key.orEmpty())
        .put("deadZone", DEFAULT_ANALOG_STICK_DEAD_ZONE.toDouble())
}

private fun analogStickValueFromPayload(payload: String): JSONObject {
    val trimmed = payload.trim()
    if (trimmed.startsWith("{")) {
        return runCatching { JSONObject(trimmed) }.getOrDefault(analogStickValueForStep(0))
    }
    return analogStickValueForStep(trimmed.toIntOrNull() ?: 0)
}

private data class CompanionControlData(
    val kind: String = "",
    val source: String = "",
    val unit: String = "",
    val valueText: String = "",
    val numericValue: Double? = null,
    val booleanValue: Boolean? = null,
    val min: Double? = null,
    val max: Double? = null,
    val step: Double = 1.0,
    val deadZone: Float = DEFAULT_ANALOG_STICK_DEAD_ZONE,
    val hasExplicitValue: Boolean = false
)

private fun companionControlData(button: DeckButton): CompanionControlData {
    val raw = button.companionControl.trim()
    val fallbackKind = companionControlKind(button)
    if (raw.isBlank()) {
        return companionControlDefaults(button, fallbackKind)
    }
    return runCatching {
        val root = JSONObject(raw)
        val kind = root.optString("kind").ifBlank { fallbackKind }
        val hasExplicitValue = root.has("value") && !root.isNull("value")
        val value = root.opt("value")
        val numericValue = when (value) {
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull()
            else -> null
        }
        val booleanValue = when (value) {
            is Boolean -> value
            is String -> value.equals("true", ignoreCase = true) || value == "1"
            else -> null
        }
        val defaultControl = companionControlDefaults(button, kind)
        val min = root.optDoubleOrNull("min") ?: defaultControl.min
        val max = maxOf((min ?: 0.0) + 1.0, root.optDoubleOrNull("max") ?: defaultControl.max ?: 100.0)
        val normalizedNumber = numericValue
            ?.coerceIn(min ?: Double.NEGATIVE_INFINITY, max)
            ?: defaultControl.numericValue.takeIf { hasExplicitValue }
        val normalizedBoolean = booleanValue ?: defaultControl.booleanValue.takeIf { hasExplicitValue }
        val valueText = when {
            hasExplicitValue && normalizedNumber != null -> {
                val displayNumber = if (normalizedNumber % 1.0 == 0.0) {
                    normalizedNumber.toInt().toString()
                } else {
                    "%.1f".format(Locale.US, normalizedNumber)
                }
                "$displayNumber${root.optString("unit").ifBlank { defaultControl.unit }}"
            }
            hasExplicitValue && normalizedBoolean != null -> if (normalizedBoolean) "ON" else "OFF"
            hasExplicitValue -> value?.toString().orEmpty()
            else -> ""
        }
        CompanionControlData(
            kind = kind,
            source = root.optString("source").ifBlank { button.payload },
            unit = root.optString("unit").ifBlank { defaultControl.unit },
            valueText = valueText,
            numericValue = normalizedNumber,
            booleanValue = normalizedBoolean,
            min = min,
            max = max,
            step = root.optDoubleOrNull("step")?.takeIf { it > 0.0 } ?: defaultControl.step,
            deadZone = (root.optDoubleOrNull("deadZone")?.toFloat() ?: defaultControl.deadZone).coerceIn(0f, 0.5f),
            hasExplicitValue = hasExplicitValue
        )
    }.getOrDefault(companionControlDefaults(button, fallbackKind))
}

private fun companionControlKind(button: DeckButton): String {
    return when {
        button.controlStyleRaw == "CompanionSlider" -> "Slider"
        button.controlStyleRaw == "CompanionToggle" -> "Toggle"
        button.controlStyle == DeckControlStyle.TrimSlider -> "Slider"
        button.controlStyle == DeckControlStyle.TrimKnob -> "Knob"
        button.controlStyle == DeckControlStyle.InfiniteWheel -> "Wheel"
        button.controlStyle == DeckControlStyle.JoyPad -> "DPad"
        button.controlStyle == DeckControlStyle.AnalogStick -> "AnalogStick"
        button.controlStyle == DeckControlStyle.CompanionToggle -> "Toggle"
        else -> "Slider"
    }
}

private fun companionControlDefaults(button: DeckButton, kind: String): CompanionControlData {
    val source = button.payload.ifBlank {
        when (kind) {
            "Toggle" -> "manual.toggle"
            "AnalogStick" -> ANALOG_STICK_DEFAULT_SOURCE
            else -> "manual.value"
        }
    }
    return if (kind == "Toggle") {
        CompanionControlData(
            kind = kind,
            source = source,
            valueText = "OFF",
            booleanValue = false
        )
    } else if (kind == "AnalogStick") {
        CompanionControlData(
            kind = kind,
            source = source,
            deadZone = DEFAULT_ANALOG_STICK_DEAD_ZONE
        )
    } else {
        val min = if (kind == "Knob") -3.0 else 0.0
        val max = if (kind == "Knob") 3.0 else 100.0
        val value = if (kind == "Knob") 0.0 else min
        val unit = if (kind == "Knob") "" else "%"
        CompanionControlData(
            kind = kind,
            source = source,
            unit = unit,
            valueText = if (value % 1.0 == 0.0) "${value.toInt()}$unit" else "$value$unit",
            numericValue = value,
            min = min,
            max = max,
            step = 1.0
        )
    }
}

private fun CompanionControlData.editableValueText(): String {
    if (!hasExplicitValue && kind != "Toggle") return ""
    booleanValue?.let { return it.toString() }
    numericValue?.let { return compactNumberText(it) }
    return valueText.removeSuffix(unit)
}

private fun compactNumberText(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toInt().toString()
    } else {
        "%.3f".format(Locale.US, value).trimEnd('0').trimEnd('.')
    }
}

private fun companionControlJson(
    kind: String,
    source: String,
    valueText: String,
    minText: String,
    maxText: String,
    stepText: String,
    unit: String
): String {
    val root = JSONObject()
        .put("kind", kind)
        .put("source", source.ifBlank { if (kind == "Toggle") "manual.toggle" else "manual.value" })
    val normalizedValue = valueText.trim()
    if (normalizedValue.isNotBlank()) {
        val value: Any = when {
            kind == "Toggle" -> normalizedValue.equals("true", ignoreCase = true) || normalizedValue == "1" || normalizedValue.equals("on", ignoreCase = true)
            else -> normalizedValue.toDoubleOrNull() ?: normalizedValue
        }
        root.put("value", value)
    } else if (kind == "Toggle") {
        root.put("value", false)
    }
    if (kind != "Toggle") {
        minText.trim().toDoubleOrNull()?.let { root.put("min", it) }
        maxText.trim().toDoubleOrNull()?.let { root.put("max", it) }
        stepText.trim().toDoubleOrNull()?.takeIf { it > 0.0 }?.let { root.put("step", it) }
        if (unit.isNotBlank()) root.put("unit", unit)
    }
    return root.toString()
}

private fun JSONObject.optDoubleOrNull(name: String): Double? {
    if (!has(name) || isNull(name)) return null
    return runCatching { getDouble(name) }.getOrNull()
}

private fun companionControlValueLabel(button: DeckButton): String {
    val control = companionControlData(button)
    return if (control.hasExplicitValue) control.valueText else ""
}

private fun companionControlStepValue(button: DeckButton, step: Int): Any? {
    if (button.controlStyle == DeckControlStyle.AnalogStick) {
        return analogStickValueForStep(step)
    }
    val control = companionControlData(button)
    if (!control.hasExplicitValue) return null
    val current = control.numericValue ?: return null
    val next = current + step.toDouble() * control.step
    return next
        .let { value -> control.min?.let { value.coerceAtLeast(it) } ?: value }
        .let { value -> control.max?.let { value.coerceAtMost(it) } ?: value }
}

private fun companionControlRequestValue(button: DeckButton, payloadOverride: String): Any {
    if (button.controlStyle == DeckControlStyle.AnalogStick) {
        return analogStickValueFromPayload(payloadOverride)
    }
    val control = companionControlData(button)
    if (button.controlStyle == DeckControlStyle.CompanionToggle || control.booleanValue != null) {
        return !(control.booleanValue ?: false)
    }
    payloadOverride.toDoubleOrNull()?.let { return it }
    control.numericValue?.let { return it }
    return payloadOverride.ifBlank { control.valueText.ifBlank { true.toString() } }
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

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditButtonDialog(
    button: DeckButton,
    status: HidStatus,
    appWidgetHost: AppWidgetHost?,
    appWidgetManager: AppWidgetManager?,
    classicSolidButtonBackground: Boolean,
    consoleStyle: Boolean = false,
    showGuideCards: Boolean,
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
    var analogDeadZone by remember(button.id) { mutableStateOf(companionControlData(button).deadZone) }
    var companionControlSource by remember(button.id) {
        mutableStateOf(companionControlData(button).source.ifBlank { button.payload })
    }
    var companionControlValue by remember(button.id) {
        mutableStateOf(companionControlData(button).editableValueText())
    }
    var companionControlMin by remember(button.id) {
        mutableStateOf(companionControlData(button).min?.let(::compactNumberText).orEmpty())
    }
    var companionControlMax by remember(button.id) {
        mutableStateOf(companionControlData(button).max?.let(::compactNumberText).orEmpty())
    }
    var companionControlStep by remember(button.id) {
        mutableStateOf(companionControlData(button).step.let(::compactNumberText))
    }
    var companionControlUnit by remember(button.id) {
        mutableStateOf(companionControlData(button).unit)
    }
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
        val companionControl = companionControlData(button)
        analogDeadZone = companionControl.deadZone
        companionControlSource = companionControl.source.ifBlank { button.payload }
        companionControlValue = companionControl.editableValueText()
        companionControlMin = companionControl.min?.let(::compactNumberText).orEmpty()
        companionControlMax = companionControl.max?.let(::compactNumberText).orEmpty()
        companionControlStep = compactNumberText(companionControl.step)
        companionControlUnit = companionControl.unit
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
    val displayCapabilities = deckButtonDisplayCapabilities(controlStyle, actionType)
    val showIconInPreview = displayCapabilities.supportsIconImage && displayMode != DeckDisplayMode.KeywordOnly
    val showTitleInPreview = showTitleField
    val showSubtitleInput = actionPanel != EditActionPanel.AppCommand
    val showSubtitleInPreview = showSubtitleInput && showSubtitleField
    val showTextInPreview = displayCapabilities.supportsText && (showTitleInPreview || showSubtitleInPreview)
    val selectedMedia = selectedMediaKeyChoice(payload)
    val selectedTrimLowerMedia = selectedMediaKeyChoice(trimLowerPayload, MEDIA_VOLUME_DOWN)
    val selectedTrimUpperMedia = selectedMediaKeyChoice(trimUpperPayload, MEDIA_VOLUME_UP)
    val selectedUtility = selectedUtilityChoice(payload)
    val isCompanionValueControl = when (controlStyle) {
        DeckControlStyle.AnalogStick,
        DeckControlStyle.CompanionToggle -> BuildConfig.DEBUG
        DeckControlStyle.TrimSlider,
        DeckControlStyle.TrimKnob,
        DeckControlStyle.InfiniteWheel -> button.hasCompanionNumericValue()
        DeckControlStyle.JoyPad,
        DeckControlStyle.Button -> false
    }
    val isCompanionControlButton = actionType == DeckActionType.CompanionStatus ||
        (actionType == DeckActionType.CompanionControl && (controlStyle == DeckControlStyle.Button || isCompanionValueControl)) ||
        (button.hasCompanionControlValue() && isCompanionValueControl)
    val storesCompanionValue = isCompanionValueControl &&
        (actionType == DeckActionType.CompanionControl || button.hasCompanionControlValue())
    val effectivePayload = if (storesCompanionValue) companionControlSource else payload
    val canSave = (actionPanel == EditActionPanel.Widget || title.isNotBlank() || !showTitleInPreview) &&
        (!payloadRequired(actionType) || effectivePayload.isNotBlank())
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
            setDefaultTitleIfAllowed(context.getString(style.labelRes), "D-pad")
            spanColumns = 2
            spanRows = 2
        } else if (style == DeckControlStyle.AnalogStick) {
            actionType = DeckActionType.CompanionControl
            payload = ANALOG_STICK_DEFAULT_SOURCE
            companionControlSource = ANALOG_STICK_DEFAULT_SOURCE
            analogDeadZone = DEFAULT_ANALOG_STICK_DEAD_ZONE
            setDefaultTitleIfAllowed(context.getString(style.labelRes), "Analog")
            spanColumns = 2
            spanRows = 2
        } else if (style == DeckControlStyle.CompanionToggle) {
            actionType = DeckActionType.CompanionControl
            companionControlSource = companionControlSource.ifBlank { "manual.toggle" }
            companionControlValue = companionControlValue.ifBlank { "false" }
            companionControlMin = ""
            companionControlMax = ""
            companionControlStep = "1"
            companionControlUnit = ""
            payload = companionControlSource
            setDefaultTitleIfAllowed(context.getString(style.labelRes), "Toggle")
            spanColumns = 1
            spanRows = 1
        } else {
            actionType = DeckActionType.MediaKey
            trimLowerPayload = trimLowerPayload.ifBlank { MEDIA_VOLUME_DOWN }
            trimUpperPayload = trimUpperPayload.ifBlank { MEDIA_VOLUME_UP }
            payload = trimPayload(trimLowerPayload, trimUpperPayload)
            setDefaultTitleIfAllowed("Volume", "Control")
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
                if (previousActionType != DeckActionType.CompanionCommand &&
                    previousActionType != DeckActionType.CompanionStatus
                ) {
                    actionType = DeckActionType.RunCommand
                }
                val wasRunPanelAction = previousActionType == DeckActionType.RunCommand ||
                    previousActionType == DeckActionType.CompanionCommand ||
                    previousActionType == DeckActionType.CompanionStatus
                if (!wasRunPanelAction || payload.isBlank()) {
                    payload = "notepad"
                }
                setDefaultTitleIfAllowed(
                    when (actionType) {
                        DeckActionType.CompanionCommand -> "Companion"
                        DeckActionType.CompanionStatus -> "PC Status"
                        else -> "Notepad"
                    },
                    payload
                )
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
        val savedCompanionControl = when {
            controlStyle == DeckControlStyle.AnalogStick -> JSONObject()
                .put("kind", "AnalogStick")
                .put("source", companionControlSource.ifBlank { payload.ifBlank { ANALOG_STICK_DEFAULT_SOURCE } })
                .put("deadZone", analogDeadZone.toDouble())
                .toString()
            storesCompanionValue -> companionControlJson(
                kind = companionControlKind(button.copy(controlStyle = controlStyle)),
                source = companionControlSource.ifBlank { payload },
                valueText = companionControlValue,
                minText = companionControlMin,
                maxText = companionControlMax,
                stepText = companionControlStep,
                unit = companionControlUnit
            )
            else -> ""
        }
        val savedPayload = if (controlStyle == DeckControlStyle.JoyPad) {
            joyPadPayload(joyPadPayloads)
        } else if (storesCompanionValue) {
            companionControlSource.trim().ifBlank { payload.trim() }
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
            icon = if (!displayCapabilities.supportsIconImage && !isCompanionControlButton) "" else icon.trim(),
            iconImageUri = if (!displayCapabilities.supportsIconImage && !isCompanionControlButton) "" else iconImageUri,
            displayMode = displayModeWith(showIcon = showIconInPreview, showText = showTextInPreview),
            actionType = actionType,
            payload = savedPayload,
            spanColumns = spanColumns.coerceIn(1, MAX_BUTTON_SPAN_COLUMNS),
            spanRows = spanRows.coerceIn(1, MAX_BUTTON_SPAN_ROWS),
            appWidgetId = appWidgetId,
            appWidgetTouchable = appWidgetTouchable,
            controlStyle = controlStyle,
            controlStyleRaw = if (controlStyle == button.controlStyle) button.controlStyleRaw else controlStyle.name,
            companionControl = savedCompanionControl
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
            icon = if (!displayCapabilities.supportsIconImage) "" else icon,
            iconImageUri = if (!displayCapabilities.supportsIconImage) "" else iconImageUri,
            displayMode = displayModeWith(showIcon = showIconInPreview, showText = showTextInPreview),
            actionType = actionType,
            payload = when {
                controlStyle == DeckControlStyle.JoyPad -> joyPadPayload(joyPadPayloads)
                controlStyle == DeckControlStyle.AnalogStick -> payload
                isTrimControl && !isCompanionValueControl -> trimPayload(trimLowerPayload, trimUpperPayload)
                else -> payload
            },
            appWidgetId = appWidgetId,
            appWidgetTouchable = appWidgetTouchable,
            spanColumns = spanColumns,
            spanRows = spanRows,
            controlStyle = controlStyle,
            companionControl = if (controlStyle == DeckControlStyle.AnalogStick) {
                JSONObject()
                    .put("kind", "AnalogStick")
                    .put("source", companionControlSource.ifBlank { ANALOG_STICK_DEFAULT_SOURCE })
                    .put("deadZone", analogDeadZone.toDouble())
                    .toString()
            } else if (storesCompanionValue) {
                companionControlJson(
                    kind = companionControlKind(button.copy(controlStyle = controlStyle)),
                    source = companionControlSource.ifBlank { payload },
                    valueText = companionControlValue,
                    minText = companionControlMin,
                    maxText = companionControlMax,
                    stepText = companionControlStep,
                    unit = companionControlUnit
                )
            } else {
                button.companionControl
            }
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
                        if (showGuideCards) {
                            item {
                                EditorGuideCard(
                                    title = stringResource(R.string.button_editor_tutorial_title),
                                    body = stringResource(R.string.button_editor_tutorial_body),
                                    consoleStyle = consoleStyle
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
                                            val controlStyles = listOf(
                                                DeckControlStyle.TrimSlider,
                                                DeckControlStyle.TrimKnob,
                                                DeckControlStyle.InfiniteWheel,
                                                DeckControlStyle.JoyPad,
                                                DeckControlStyle.AnalogStick
                                            ) + if (BuildConfig.DEBUG) listOf(DeckControlStyle.CompanionToggle) else emptyList()
                                            controlStyles.forEach { style ->
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
                                if (actionPanel == EditActionPanel.KeyboardInput &&
                                    controlStyle == DeckControlStyle.JoyPad
                                ) {
                                    JoyPadPayloadEditor(
                                        payloads = joyPadPayloads,
                                        consoleStyle = consoleStyle,
                                        onChange = { updated ->
                                            joyPadPayloads = updated
                                            payload = joyPadPayload(updated)
                                        }
                                    )
                                } else if (actionPanel == EditActionPanel.KeyboardInput &&
                                    controlStyle == DeckControlStyle.AnalogStick
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = stringResource(
                                                if (BuildConfig.DEBUG) {
                                                    R.string.analog_stick_companion_only_desc
                                                } else {
                                                    R.string.analog_stick_release_desc
                                                }
                                            ),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = colors.textSecondary
                                        )
                                        if (BuildConfig.DEBUG) {
                                            CompactKeyEditTextField(
                                                modifier = Modifier.fillMaxWidth(),
                                                value = companionControlSource.ifBlank { ANALOG_STICK_DEFAULT_SOURCE },
                                                onValueChange = {
                                                    companionControlSource = it
                                                    payload = it
                                                },
                                                enabled = true,
                                                label = stringResource(R.string.companion_source)
                                            )
                                        }
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = stringResource(R.string.analog_stick_dead_zone),
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = colors.textPrimary,
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = stringResource(R.string.analog_stick_dead_zone_desc),
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = colors.textSecondary,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                            Text(
                                                text = "${(analogDeadZone * 100f).roundToInt()}%",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = colors.textPrimary,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        CompactSlider(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(30.dp),
                                            value = analogDeadZone,
                                            onValueChange = { analogDeadZone = it.coerceIn(0f, 0.5f) },
                                            valueRange = 0f..0.5f
                                        )
                                    }
                                } else if (actionPanel == EditActionPanel.KeyboardInput && isCompanionValueControl) {
                                    CompanionValueControlEditor(
                                        source = companionControlSource,
                                        value = companionControlValue,
                                        min = companionControlMin,
                                        max = companionControlMax,
                                        step = companionControlStep,
                                        unit = companionControlUnit,
                                        showRange = controlStyle != DeckControlStyle.CompanionToggle,
                                        onSourceChange = {
                                            companionControlSource = it
                                            payload = it
                                        },
                                        onValueChange = { companionControlValue = it },
                                        onMinChange = { companionControlMin = it },
                                        onMaxChange = { companionControlMax = it },
                                        onStepChange = { companionControlStep = it },
                                        onUnitChange = { companionControlUnit = it }
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
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                                onClick = { keyInputHelpVisible = !keyInputHelpVisible }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Filled.Help,
                                                    contentDescription = stringResource(R.string.key_input_help_title),
                                                    tint = if (keyInputHelpVisible) panelAccent else colors.textSecondary
                                                )
                                            }
                                        }
                                        KeyInputTokenPreview(
                                            value = payload,
                                            consoleStyle = consoleStyle
                                        )
                                        AnimatedVisibility(visible = keyInputHelpVisible) {
                                            KeyInputReferencePanel(
                                                consoleStyle = consoleStyle,
                                                onTokenSelected = { token ->
                                                    actionType = DeckActionType.Hotkey
                                                    payload = appendKeyInputToken(payload, token)
                                                },
                                                onDismiss = { keyInputHelpVisible = false }
                                            )
                                        }
                                    }
                                } else {
                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        if (actionPanel == EditActionPanel.RunCommand) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                ClassicEditDialogButton(
                                                    modifier = Modifier.weight(1f),
                                                    text = stringResource(R.string.action_run_command),
                                                    highlighted = actionType == DeckActionType.RunCommand,
                                                    consoleStyle = consoleStyle,
                                                    onClick = { actionType = DeckActionType.RunCommand }
                                                )
                                                if (BuildConfig.DEBUG) {
                                                    ClassicEditDialogButton(
                                                        modifier = Modifier.weight(1f),
                                                        text = stringResource(R.string.action_companion_command),
                                                        highlighted = actionType == DeckActionType.CompanionCommand,
                                                        consoleStyle = consoleStyle,
                                                        onClick = { actionType = DeckActionType.CompanionCommand }
                                                    )
                                                    ClassicEditDialogButton(
                                                        modifier = Modifier.weight(1f),
                                                        text = stringResource(R.string.action_companion_status),
                                                        highlighted = actionType == DeckActionType.CompanionStatus,
                                                        consoleStyle = consoleStyle,
                                                        onClick = {
                                                            actionType = DeckActionType.CompanionStatus
                                                            if (payload.isBlank() || payload == "notepad") payload = "system.cpuUsage"
                                                            setDefaultTitleIfAllowed("CPU", "PC status")
                                                        }
                                                    )
                                                }
                                            }
                                        }
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
                        if (actionPanel != EditActionPanel.Widget && displayCapabilities.supportsIconImage) {
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
                        } else if (actionPanel != EditActionPanel.Widget && displayCapabilities.supportsText) {
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
                                                        showIcon = false,
                                                        showText = checked || showSubtitleInPreview
                                                    )
                                                },
                                                ({ checked: Boolean ->
                                                    showSubtitleField = checked
                                                    displayMode = displayModeWith(
                                                        showIcon = false,
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
    PcConnection,
    UiMode,
    Layout,
    Buttons,
    Background,
    Backup,
    DeckSettingsButton
}

private fun nextSettingsTutorialStep(step: SettingsTutorialStep): SettingsTutorialStep? {
    return when (step) {
        SettingsTutorialStep.PcConnection -> SettingsTutorialStep.UiMode
        SettingsTutorialStep.UiMode -> SettingsTutorialStep.Layout
        SettingsTutorialStep.Layout -> SettingsTutorialStep.Buttons
        SettingsTutorialStep.Buttons -> SettingsTutorialStep.Background
        SettingsTutorialStep.Background -> SettingsTutorialStep.Backup
        SettingsTutorialStep.Backup -> SettingsTutorialStep.DeckSettingsButton
        SettingsTutorialStep.DeckSettingsButton -> null
    }
}

private fun settingsTutorialStepNumber(step: SettingsTutorialStep): Int {
    return when (step) {
        SettingsTutorialStep.PcConnection -> 1
        SettingsTutorialStep.UiMode -> 2
        SettingsTutorialStep.Layout -> 3
        SettingsTutorialStep.Buttons -> 4
        SettingsTutorialStep.Background -> 5
        SettingsTutorialStep.Backup -> 6
        SettingsTutorialStep.DeckSettingsButton -> 7
    }
}

@Composable
private fun SettingsTutorialOverlay(
    modifier: Modifier = Modifier,
    mode: DeckUiMode,
    step: SettingsTutorialStep,
    onStepChange: (SettingsTutorialStep) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val consoleAccent = when (step) {
        SettingsTutorialStep.PcConnection,
        SettingsTutorialStep.Buttons,
        SettingsTutorialStep.DeckSettingsButton -> colors.consoleButtonSystem
        SettingsTutorialStep.UiMode,
        SettingsTutorialStep.Layout,
        SettingsTutorialStep.Background,
        SettingsTutorialStep.Backup -> colors.consoleButtonFeatured
    }
    val accent = when (step) {
        SettingsTutorialStep.PcConnection -> if (mode == DeckUiMode.Console) consoleAccent else Color(0xFF25B9FF)
        SettingsTutorialStep.UiMode -> if (mode == DeckUiMode.Console) consoleAccent else Color(0xFF2ECA73)
        SettingsTutorialStep.Layout -> if (mode == DeckUiMode.Console) consoleAccent else ClassicLayoutAccent
        SettingsTutorialStep.Buttons -> if (mode == DeckUiMode.Console) consoleAccent else ClassicButtonAccent
        SettingsTutorialStep.Background -> if (mode == DeckUiMode.Console) consoleAccent else Color(0xFF9D5CFF)
        SettingsTutorialStep.Backup -> if (mode == DeckUiMode.Console) consoleAccent else Color(0xFF2B9098)
        SettingsTutorialStep.DeckSettingsButton -> if (mode == DeckUiMode.Console) consoleAccent else ClassicButtonAccent
    }
    val stepNumber = settingsTutorialStepNumber(step)
    val title = tutorialStepTitle(step, mode)
    val body = tutorialStepBody(step, mode)
    val details = tutorialDetailItems(step, mode)

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
                SettingsTutorialStep.PcConnection -> Modifier
                    .offset(x = 12.dp, y = 100.dp)
                    .size(width = sidebarWidth - 24.dp, height = 430.dp)
                SettingsTutorialStep.UiMode -> Modifier
                    .offset(x = 12.dp, y = 54.dp)
                    .size(width = sidebarWidth - 24.dp, height = 76.dp)
                SettingsTutorialStep.Layout -> Modifier
                    .offset(x = sidebarWidth + 14.dp, y = 82.dp)
                    .size(width = contentWidth, height = 252.dp)
                SettingsTutorialStep.Buttons -> Modifier
                    .offset(x = sidebarWidth + 14.dp, y = 342.dp)
                    .size(width = contentWidth, height = 168.dp)
                SettingsTutorialStep.Background -> Modifier
                    .offset(x = sidebarWidth + 14.dp, y = 518.dp)
                    .size(width = contentWidth, height = 150.dp)
                SettingsTutorialStep.Backup -> Modifier
                    .offset(x = sidebarWidth + 14.dp, y = 676.dp)
                    .size(width = contentWidth, height = 126.dp)
                SettingsTutorialStep.DeckSettingsButton -> Modifier
            }
            val calloutAlignment = when (step) {
                SettingsTutorialStep.PcConnection -> Alignment.CenterEnd
                SettingsTutorialStep.UiMode -> Alignment.CenterEnd
                SettingsTutorialStep.Layout -> Alignment.BottomEnd
                SettingsTutorialStep.Buttons -> Alignment.TopEnd
                SettingsTutorialStep.Background -> Alignment.TopEnd
                SettingsTutorialStep.Backup -> Alignment.TopEnd
                SettingsTutorialStep.DeckSettingsButton -> Alignment.Center
            }
            val calloutPadding = when (step) {
                SettingsTutorialStep.PcConnection -> PaddingValues(end = 34.dp)
                SettingsTutorialStep.UiMode -> PaddingValues(end = 34.dp)
                SettingsTutorialStep.Layout -> PaddingValues(end = 34.dp, bottom = 28.dp)
                SettingsTutorialStep.Buttons -> PaddingValues(end = 34.dp, top = 34.dp)
                SettingsTutorialStep.Background -> PaddingValues(end = 34.dp, top = 30.dp)
                SettingsTutorialStep.Backup -> PaddingValues(end = 34.dp, top = 30.dp)
                SettingsTutorialStep.DeckSettingsButton -> PaddingValues(0.dp)
            }

            TutorialHighlightFrame(
                modifier = highlightModifier,
                accent = accent,
                stepNumber = stepNumber,
                title = title,
                showBluetoothOrder = mode == DeckUiMode.Classic && step == SettingsTutorialStep.PcConnection
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
                    TutorialDetailList(items = details, accent = accent)
                    if (mode == DeckUiMode.Console) {
                        ConsoleTutorialDemo(step = step, accent = accent)
                    }
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
private fun tutorialStepTitle(step: SettingsTutorialStep, mode: DeckUiMode): String {
    val consoleMode = mode == DeckUiMode.Console
    return when (step) {
        SettingsTutorialStep.PcConnection -> stringResource(if (consoleMode) R.string.console_tutorial_connect_title else R.string.classic_tutorial_connect_title)
        SettingsTutorialStep.UiMode -> stringResource(if (consoleMode) R.string.console_tutorial_ui_mode_title else R.string.classic_tutorial_ui_mode_title)
        SettingsTutorialStep.Layout -> stringResource(if (consoleMode) R.string.console_tutorial_layout_title else R.string.classic_tutorial_layout_title)
        SettingsTutorialStep.Buttons -> stringResource(if (consoleMode) R.string.console_tutorial_button_settings_title else R.string.classic_tutorial_button_settings_title)
        SettingsTutorialStep.Background -> stringResource(if (consoleMode) R.string.console_tutorial_background_title else R.string.classic_tutorial_background_title)
        SettingsTutorialStep.Backup -> stringResource(if (consoleMode) R.string.console_tutorial_backup_title else R.string.classic_tutorial_backup_title)
        SettingsTutorialStep.DeckSettingsButton -> stringResource(if (consoleMode) R.string.console_tutorial_settings_button_title else R.string.classic_tutorial_settings_button_title)
    }
}

@Composable
private fun tutorialStepBody(step: SettingsTutorialStep, mode: DeckUiMode): String {
    val consoleMode = mode == DeckUiMode.Console
    return when (step) {
        SettingsTutorialStep.PcConnection -> stringResource(
            when {
                BuildConfig.DEBUG && consoleMode -> R.string.console_tutorial_connect_body
                BuildConfig.DEBUG -> R.string.classic_tutorial_connect_body
                consoleMode -> R.string.console_tutorial_connect_body_release
                else -> R.string.classic_tutorial_connect_body_release
            }
        )
        SettingsTutorialStep.UiMode -> stringResource(if (consoleMode) R.string.console_tutorial_ui_mode_body else R.string.classic_tutorial_ui_mode_body)
        SettingsTutorialStep.Layout -> stringResource(if (consoleMode) R.string.console_tutorial_layout_body else R.string.classic_tutorial_layout_body)
        SettingsTutorialStep.Buttons -> stringResource(if (consoleMode) R.string.console_tutorial_button_settings_body else R.string.classic_tutorial_button_settings_body)
        SettingsTutorialStep.Background -> stringResource(if (consoleMode) R.string.console_tutorial_background_body else R.string.classic_tutorial_background_body)
        SettingsTutorialStep.Backup -> stringResource(if (consoleMode) R.string.console_tutorial_backup_body else R.string.classic_tutorial_backup_body)
        SettingsTutorialStep.DeckSettingsButton -> stringResource(if (consoleMode) R.string.console_tutorial_settings_button_body else R.string.classic_tutorial_settings_button_body)
    }
}

@Composable
private fun tutorialDetailItems(step: SettingsTutorialStep, mode: DeckUiMode): List<String> {
    if (mode == DeckUiMode.Console) {
        return when (step) {
            SettingsTutorialStep.PcConnection -> if (BuildConfig.DEBUG) {
                listOf(
                    stringResource(R.string.console_tutorial_connect_detail_companion),
                    stringResource(R.string.console_tutorial_connect_detail_bluetooth),
                    stringResource(R.string.console_tutorial_connect_detail_status)
                )
            } else {
                listOf(
                    stringResource(R.string.console_tutorial_connect_detail_bluetooth),
                    stringResource(R.string.console_tutorial_connect_detail_status)
                )
            }
            SettingsTutorialStep.UiMode -> listOf(
                stringResource(R.string.console_tutorial_ui_mode_detail_sidebar),
                stringResource(R.string.console_tutorial_ui_mode_detail_cards),
                stringResource(R.string.console_tutorial_ui_mode_detail_shared)
            )
            SettingsTutorialStep.Layout -> listOf(
                stringResource(R.string.console_tutorial_layout_detail_modes),
                stringResource(R.string.console_tutorial_layout_detail_rows),
                stringResource(R.string.console_tutorial_layout_detail_pages)
            )
            SettingsTutorialStep.Buttons -> listOf(
                stringResource(R.string.console_tutorial_buttons_detail_insert),
                stringResource(R.string.console_tutorial_buttons_detail_editor),
                stringResource(R.string.console_tutorial_buttons_detail_shapes)
            )
            SettingsTutorialStep.Background -> listOf(
                stringResource(R.string.console_tutorial_background_detail_panel),
                stringResource(R.string.console_tutorial_background_detail_font),
                stringResource(R.string.console_tutorial_background_detail_theme)
            )
            SettingsTutorialStep.Backup -> listOf(
                stringResource(R.string.console_tutorial_backup_detail_export),
                stringResource(R.string.console_tutorial_backup_detail_import),
                stringResource(R.string.console_tutorial_backup_detail_debug)
            )
            SettingsTutorialStep.DeckSettingsButton -> listOf(
                stringResource(R.string.console_tutorial_settings_detail_location),
                stringResource(R.string.console_tutorial_settings_detail_restore),
                stringResource(R.string.console_tutorial_settings_detail_repeat)
            )
        }
    }
    return when (step) {
        SettingsTutorialStep.PcConnection -> if (BuildConfig.DEBUG) {
            listOf(
                stringResource(R.string.classic_tutorial_connect_detail_companion),
                stringResource(R.string.classic_tutorial_connect_detail_bluetooth),
                stringResource(R.string.classic_tutorial_connect_detail_status)
            )
        } else {
            listOf(
                stringResource(R.string.classic_tutorial_connect_detail_bluetooth),
                stringResource(R.string.classic_tutorial_connect_detail_status_release)
            )
        }
        SettingsTutorialStep.UiMode -> listOf(
            stringResource(R.string.classic_tutorial_ui_mode_detail_classic),
            stringResource(R.string.classic_tutorial_ui_mode_detail_console),
            stringResource(R.string.classic_tutorial_ui_mode_detail_settings)
        )
        SettingsTutorialStep.Layout -> listOf(
            stringResource(R.string.classic_tutorial_layout_detail_grid),
            stringResource(R.string.classic_tutorial_layout_detail_pages),
            stringResource(R.string.classic_tutorial_layout_detail_swipe)
        )
        SettingsTutorialStep.Buttons -> listOf(
            stringResource(R.string.classic_tutorial_buttons_detail_editor),
            stringResource(
                if (BuildConfig.DEBUG) {
                    R.string.classic_tutorial_buttons_detail_actions
                } else {
                    R.string.classic_tutorial_buttons_detail_actions_release
                }
            ),
            stringResource(R.string.classic_tutorial_buttons_detail_controls)
        )
        SettingsTutorialStep.Background -> listOf(
            stringResource(R.string.classic_tutorial_background_detail_color),
            stringResource(R.string.classic_tutorial_background_detail_image),
            stringResource(R.string.classic_tutorial_background_detail_theme)
        )
        SettingsTutorialStep.Backup -> listOf(
            stringResource(R.string.classic_tutorial_backup_detail_export),
            stringResource(R.string.classic_tutorial_backup_detail_import),
            stringResource(R.string.classic_tutorial_backup_detail_test)
        )
        SettingsTutorialStep.DeckSettingsButton -> listOf(
            stringResource(R.string.classic_tutorial_settings_detail_location),
            stringResource(R.string.classic_tutorial_settings_detail_restore),
            stringResource(R.string.classic_tutorial_settings_detail_repeat)
        )
    }
}

@Composable
private fun TutorialDetailList(
    items: List<String>,
    accent: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        items.forEach { item ->
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(accent)
                )
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalDeckThemeColors.current.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ConsoleTutorialDemo(
    step: SettingsTutorialStep,
    accent: Color
) {
    val colors = LocalDeckThemeColors.current
    val label = stringResource(
        when (step) {
            SettingsTutorialStep.PcConnection -> if (BuildConfig.DEBUG) {
                R.string.console_tutorial_demo_connection
            } else {
                R.string.console_tutorial_demo_connection_release
            }
            SettingsTutorialStep.UiMode -> R.string.console_tutorial_demo_sidebar
            SettingsTutorialStep.Layout -> R.string.console_tutorial_demo_layout
            SettingsTutorialStep.Buttons -> R.string.console_tutorial_demo_drag
            SettingsTutorialStep.Background -> R.string.console_tutorial_demo_panel
            SettingsTutorialStep.Backup -> R.string.console_tutorial_demo_debug
            SettingsTutorialStep.DeckSettingsButton -> R.string.console_tutorial_demo_settings
        }
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(accent.copy(alpha = 0.10f))
            .border(1.dp, accent.copy(alpha = 0.36f), RoundedCornerShape(10.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = colors.textPrimary
        )
        when (step) {
            SettingsTutorialStep.PcConnection -> ConsoleTutorialConnectionDemo(accent)
            SettingsTutorialStep.UiMode -> ConsoleTutorialSidebarDemo(accent)
            SettingsTutorialStep.Layout -> ConsoleTutorialLayoutDemo(accent, drag = false)
            SettingsTutorialStep.Buttons -> ConsoleTutorialLayoutDemo(accent, drag = true)
            SettingsTutorialStep.Background -> ConsoleTutorialPanelDemo(accent)
            SettingsTutorialStep.Backup -> ConsoleTutorialDebugDemo(accent)
            SettingsTutorialStep.DeckSettingsButton -> ConsoleTutorialSettingsDemo(accent)
        }
    }
}

@Composable
private fun ConsoleTutorialConnectionDemo(accent: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        val connectionItems = if (BuildConfig.DEBUG) {
            listOf(R.string.companion_settings_title, R.string.settings_hid_management)
        } else {
            listOf(R.string.settings_hid_management)
        }
        connectionItems.forEachIndexed { index, resId ->
            val highlighted = BuildConfig.DEBUG && index == 0
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (highlighted) accent.copy(alpha = 0.24f) else Color.White.copy(alpha = 0.08f))
                    .border(1.dp, accent.copy(alpha = if (highlighted) 0.54f else 0.22f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(resId),
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalDeckThemeColors.current.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ConsoleTutorialSidebarDemo(accent: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        ConsoleSettingsCategory.values().forEach { category ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(32.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(if (category == ConsoleSettingsCategory.Layout) accent.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.08f))
                    .border(1.dp, accent.copy(alpha = 0.30f), RoundedCornerShape(7.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(consoleSettingsCategoryTitleRes(category)),
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalDeckThemeColors.current.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ConsoleTutorialLayoutDemo(
    accent: Color,
    drag: Boolean
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(72.dp)
                .height(74.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(Color.White.copy(alpha = 0.08f))
                .border(1.dp, accent.copy(alpha = 0.24f), RoundedCornerShape(9.dp))
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(3) { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    repeat(if (row == 2) 2 else 3) { column ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(22.dp)
                                .offset(
                                    x = if (drag && row == 0 && column == 0) 18.dp else 0.dp,
                                    y = if (drag && row == 0 && column == 0) 20.dp else 0.dp
                                )
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (drag && row == 0 && column == 0) accent.copy(alpha = 0.42f) else Color.White.copy(alpha = 0.10f))
                                .border(1.dp, accent.copy(alpha = 0.26f), RoundedCornerShape(6.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConsoleTutorialPanelDemo(accent: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (index == 1) accent.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f))
                    .border(1.dp, accent.copy(alpha = 0.30f), RoundedCornerShape(8.dp))
            )
        }
    }
}

@Composable
private fun ConsoleTutorialDebugDemo(accent: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        listOf(R.string.export_bundle, R.string.import_bundle, R.string.console_tutorial_button).forEachIndexed { index, resId ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .clip(RoundedCornerShape(7.dp))
                    .background(if (index == 2) accent.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.08f))
                    .border(1.dp, accent.copy(alpha = 0.28f), RoundedCornerShape(7.dp)),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    text = stringResource(resId),
                    style = MaterialTheme.typography.labelSmall,
                    color = LocalDeckThemeColors.current.textPrimary
                )
            }
        }
    }
}

@Composable
private fun ConsoleTutorialSettingsDemo(accent: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(accent.copy(alpha = 0.22f))
            .border(1.dp, accent.copy(alpha = 0.48f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = null,
                tint = LocalDeckThemeColors.current.textPrimary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = stringResource(R.string.action_settings),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = LocalDeckThemeColors.current.textPrimary
            )
        }
    }
}

@Composable
private fun ConsoleDeckSettingsButtonTutorialOverlay(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val accent = colors.consoleButtonSystem
    val title = tutorialStepTitle(SettingsTutorialStep.DeckSettingsButton, DeckUiMode.Console)
    val body = tutorialStepBody(SettingsTutorialStep.DeckSettingsButton, DeckUiMode.Console)
    val details = tutorialDetailItems(SettingsTutorialStep.DeckSettingsButton, DeckUiMode.Console)
    Surface(
        modifier = modifier,
        color = Color.Black.copy(alpha = 0.66f),
        contentColor = Color.White,
        onClick = {}
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val frameX = 26.dp
            val frameY = (maxHeight - 74.dp).coerceAtLeast(26.dp)
            TutorialHighlightFrame(
                modifier = Modifier
                    .offset(x = frameX, y = frameY)
                    .size(52.dp),
                accent = accent,
                stepNumber = settingsTutorialStepNumber(SettingsTutorialStep.DeckSettingsButton),
                title = title,
                showBluetoothOrder = false
            )
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 96.dp, bottom = 24.dp)
                    .widthIn(max = 430.dp),
                shape = RoundedCornerShape(14.dp),
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
                    TutorialDetailList(items = details, accent = accent)
                    ConsoleTutorialDemo(step = SettingsTutorialStep.DeckSettingsButton, accent = accent)
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
                            Text(stringResource(R.string.classic_tutorial_done))
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
                        companionConnected = true,
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
private fun KeyInputTokenPreview(
    value: String,
    consoleStyle: Boolean
) {
    val colors = LocalDeckThemeColors.current
    val tokens = remember(value) { keyInputTokens(value) }
    if (tokens.isEmpty()) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.key_input_token_preview),
            style = MaterialTheme.typography.labelSmall,
            color = colors.textSecondary,
            maxLines = 1
        )
        tokens.forEach { token ->
            KeyInputTokenChip(
                text = token.value,
                type = token.type,
                consoleStyle = consoleStyle
            )
        }
    }
}

@Composable
private fun KeyInputReferencePanel(
    consoleStyle: Boolean,
    onTokenSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalDeckThemeColors.current
    val shape = RoundedCornerShape(if (consoleStyle) 14.dp else 10.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (consoleStyle) colors.consoleButtonDefault.copy(alpha = 0.74f) else colors.toggleBackground.copy(alpha = 0.46f))
            .border(1.dp, colors.cardBorder.copy(alpha = if (consoleStyle) 0.74f else 0.48f), shape)
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Help,
                contentDescription = null,
                tint = if (consoleStyle) colors.consoleButtonFeatured else ClassicButtonAccent,
                modifier = Modifier.size(18.dp)
            )
            Text(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.key_input_rules_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
            IconButton(
                modifier = Modifier.size(32.dp),
                onClick = onDismiss
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.cancel),
                    tint = colors.textSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        KeyInputReferenceSection(
            title = stringResource(R.string.key_input_rule_modifiers),
            description = stringResource(R.string.key_input_rule_modifiers_desc),
            tokens = listOf("CTRL", "ALT", "SHIFT", "WIN"),
            type = KeyInputTokenType.Modifier,
            consoleStyle = consoleStyle,
            onTokenSelected = onTokenSelected
        )
        KeyInputReferenceSection(
            title = stringResource(R.string.key_input_rule_text_keys),
            description = stringResource(R.string.key_input_rule_text_keys_desc),
            tokens = listOf("\"ENTER\"", "\"CTRL\"", "\"A+B\"", "\"\"\"\""),
            type = KeyInputTokenType.TextKey,
            consoleStyle = consoleStyle,
            onTokenSelected = onTokenSelected
        )
        KeyInputReferenceSection(
            title = stringResource(R.string.key_input_rule_special_keys),
            description = stringResource(R.string.key_input_rule_special_keys_desc),
            tokens = listOf("F1", "SPACE", "ENTER", "ESC", "TAB", "LEFT"),
            type = KeyInputTokenType.SpecialKey,
            consoleStyle = consoleStyle,
            onTokenSelected = onTokenSelected
        )
        KeyInputReferenceSection(
            title = stringResource(R.string.key_input_rule_media_keys),
            description = stringResource(R.string.key_input_rule_media_keys_desc),
            tokens = listOf("PLAY_PAUSE", "VOLUME_UP", "VOLUME_DOWN", "MUTE"),
            type = KeyInputTokenType.MediaKey,
            consoleStyle = consoleStyle,
            onTokenSelected = onTokenSelected
        )
        KeyInputReferenceSection(
            title = stringResource(R.string.key_input_rule_operator),
            description = stringResource(R.string.key_input_rule_operator_desc),
            tokens = listOf("+", ","),
            type = KeyInputTokenType.Operator,
            consoleStyle = consoleStyle,
            onTokenSelected = onTokenSelected
        )
        if (BuildConfig.DEBUG) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background((if (consoleStyle) colors.consoleButtonFeatured else ClassicButtonAccent).copy(alpha = 0.12f))
                    .border(
                        1.dp,
                        (if (consoleStyle) colors.consoleButtonFeatured else ClassicButtonAccent).copy(alpha = 0.36f),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = if (consoleStyle) colors.consoleButtonFeatured else ClassicButtonAccent,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(R.string.key_input_rule_companion_note),
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun KeyInputReferenceSection(
    title: String,
    description: String,
    tokens: List<String>,
    type: KeyInputTokenType,
    consoleStyle: Boolean,
    onTokenSelected: (String) -> Unit
) {
    val colors = LocalDeckThemeColors.current
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary,
                maxLines = 1
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            tokens.forEach { token ->
                KeyInputTokenChip(
                    text = token,
                    type = type,
                    consoleStyle = consoleStyle,
                    onClick = { onTokenSelected(token) }
                )
            }
        }
    }
}

@Composable
private fun KeyInputTokenChip(
    text: String,
    type: KeyInputTokenType,
    consoleStyle: Boolean,
    onClick: (() -> Unit)? = null
) {
    val colors = LocalDeckThemeColors.current
    val baseAccent = when (type) {
        KeyInputTokenType.Modifier -> if (consoleStyle) colors.consoleButtonFeatured else ClassicButtonAccent
        KeyInputTokenType.TextKey -> colors.textSecondary
        KeyInputTokenType.SpecialKey -> Color(0xFF4DA3FF)
        KeyInputTokenType.MediaKey -> Color(0xFFFF9F2E)
        KeyInputTokenType.Operator -> Color(0xFF35C46F)
    }
    val shape = RoundedCornerShape(if (type == KeyInputTokenType.Operator) 999.dp else 8.dp)
    Row(
        modifier = Modifier
            .clip(shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .background(baseAccent.copy(alpha = if (type == KeyInputTokenType.Operator) 0.20f else 0.14f))
            .border(1.dp, baseAccent.copy(alpha = 0.48f), shape)
            .padding(horizontal = if (type == KeyInputTokenType.Operator) 9.dp else 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (type != KeyInputTokenType.Operator && type != KeyInputTokenType.TextKey) {
            Text(
                text = stringResource(type.labelRes),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = baseAccent,
                maxLines = 1
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (type == KeyInputTokenType.Operator) baseAccent else colors.textPrimary,
            maxLines = 1
        )
    }
}

private fun appendKeyInputToken(current: String, token: String): String {
    val next = token.trim().uppercase()
    if (next.isBlank()) return current
    val trimmed = current.trim()
    if (trimmed.isBlank()) return if (next == "+" || next == ",") "" else next
    val endsWithOperator = trimmed.endsWith("+") || trimmed.endsWith(",")
    val isOperator = next == "+" || next == ","
    return when {
        isOperator && endsWithOperator -> trimmed.dropLast(1) + next
        isOperator -> trimmed + next
        endsWithOperator -> trimmed + next
        else -> "$trimmed+$next"
    }
}

private enum class KeyInputTokenType(
    val labelRes: Int
) {
    Modifier(R.string.key_input_token_modifier),
    TextKey(R.string.key_input_token_text),
    SpecialKey(R.string.key_input_token_special),
    MediaKey(R.string.key_input_token_media),
    Operator(R.string.key_input_token_operator)
}

private data class KeyInputToken(
    val value: String,
    val type: KeyInputTokenType
)

private fun keyInputTokens(value: String): List<KeyInputToken> {
    if (value.isBlank()) return emptyList()
    val result = mutableListOf<KeyInputToken>()
    val token = StringBuilder()
    var quoted = false
    var tokenQuoted = false
    fun flushToken() {
        val raw = token.toString()
        val display = if (tokenQuoted) raw else raw.trim().uppercase()
        if (display.isNotBlank()) {
            result += KeyInputToken(display, if (tokenQuoted) KeyInputTokenType.TextKey else keyInputTokenType(display))
        }
        token.clear()
        tokenQuoted = false
    }
    var index = 0
    while (index < value.length) {
        val char = value[index]
        when {
            char == '"' && quoted && value.getOrNull(index + 1) == '"' -> {
                token.append('"')
                index += 1
            }
            char == '"' -> {
                if (!quoted && token.isBlank()) tokenQuoted = true
                quoted = !quoted
            }
            !quoted && (char == '+' || char == ',') -> {
                flushToken()
                result += KeyInputToken(char.toString(), KeyInputTokenType.Operator)
            }
            else -> token.append(char)
        }
        index += 1
    }
    flushToken()
    return result
}

private fun keyInputTokenType(token: String): KeyInputTokenType {
    val normalized = token.trim().uppercase()
    val modifiers = setOf("CTRL", "CONTROL", "ALT", "SHIFT", "WIN", "WINDOWS", "CMD", "META")
    val specialKeys = setOf(
        "SPACE", "ENTER", "ESC", "ESCAPE", "TAB", "BACKSPACE", "DELETE",
        "UP", "DOWN", "LEFT", "RIGHT", "HOME", "END", "PAGEUP", "PAGEDOWN"
    )
    return when {
        normalized in modifiers -> KeyInputTokenType.Modifier
        mediaKeyChoice(normalized) != null -> KeyInputTokenType.MediaKey
        normalized in specialKeys -> KeyInputTokenType.SpecialKey
        Regex("F\\d{1,2}").matches(normalized) -> KeyInputTokenType.SpecialKey
        else -> KeyInputTokenType.TextKey
    }
}

@Composable
private fun JoyPadPayloadEditor(
    payloads: JoyPadPayloads,
    consoleStyle: Boolean,
    onChange: (JoyPadPayloads) -> Unit
) {
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
        joyPadCardinalDirections.chunked(2).forEach { rowDirections ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowDirections.forEach { direction ->
                    JoyPadDirectionPayloadEditor(
                        modifier = Modifier.weight(1f),
                        direction = direction,
                        action = payloads.action(direction),
                        onChange = { press ->
                            onChange(payloads.withDirection(direction, press))
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
    onChange: (String) -> Unit
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
        CompactKeyEditTextField(
            modifier = Modifier.fillMaxWidth(),
            value = action.press,
            onValueChange = onChange,
            label = stringResource(R.string.payload),
            enabled = true
        )
    }
}

@Composable
private fun CompanionValueControlEditor(
    source: String,
    value: String,
    min: String,
    max: String,
    step: String,
    unit: String,
    showRange: Boolean,
    onSourceChange: (String) -> Unit,
    onValueChange: (String) -> Unit,
    onMinChange: (String) -> Unit,
    onMaxChange: (String) -> Unit,
    onStepChange: (String) -> Unit,
    onUnitChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        CompactKeyEditTextField(
            modifier = Modifier.fillMaxWidth(),
            value = source,
            onValueChange = onSourceChange,
            label = stringResource(R.string.companion_source)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CompactKeyEditTextField(
                modifier = Modifier.weight(1f),
                value = value,
                onValueChange = onValueChange,
                label = stringResource(R.string.companion_value)
            )
            if (showRange) {
                CompactKeyEditTextField(
                    modifier = Modifier.weight(1f),
                    value = unit,
                    onValueChange = onUnitChange,
                    label = stringResource(R.string.companion_unit)
                )
            }
        }
        if (showRange) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CompactKeyEditTextField(
                    modifier = Modifier.weight(1f),
                    value = min,
                    onValueChange = onMinChange,
                    label = stringResource(R.string.companion_min)
                )
                CompactKeyEditTextField(
                    modifier = Modifier.weight(1f),
                    value = max,
                    onValueChange = onMaxChange,
                    label = stringResource(R.string.companion_max)
                )
                CompactKeyEditTextField(
                    modifier = Modifier.weight(1f),
                    value = step,
                    onValueChange = onStepChange,
                    label = stringResource(R.string.companion_step)
                )
            }
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
    if (button.controlStyle != DeckControlStyle.Button) return EditActionPanel.KeyboardInput
    return when (buttonAppAction(button) ?: button.actionType) {
        DeckActionType.Settings,
        DeckActionType.BluetoothStatus,
        DeckActionType.PreviousPage,
        DeckActionType.NextPage,
        DeckActionType.AppCommand -> EditActionPanel.AppCommand
        DeckActionType.MediaKey,
        DeckActionType.Hotkey,
        DeckActionType.Text -> EditActionPanel.KeyboardInput
        DeckActionType.RunCommand,
        DeckActionType.CompanionCommand,
        DeckActionType.CompanionControl,
        DeckActionType.CompanionStatus -> EditActionPanel.RunCommand
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
    if (consoleStyle) {
        val shape = RoundedCornerShape(14.dp)
        val interactionSource = remember { MutableInteractionSource() }
        val pressed by interactionSource.collectIsPressedAsState()
        val background = if (enabled) {
            if (highlighted) colors.consoleButtonFeatured else colors.consoleButtonDefault
        } else {
            colors.consoleButtonDefault.copy(alpha = 0.38f)
        }
        val contentColor = if (enabled) {
            if (highlighted) Color.White else colors.textPrimary
        } else {
            colors.textSecondary.copy(alpha = 0.45f)
        }
        Box(
            modifier = modifier
                .consoleButtonDropShadow(
                    shape = shape,
                    darkTheme = isSystemInDarkTheme(),
                    pressed = pressed
                )
                .clip(shape)
                .background(background)
                .clickable(
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.padding(
                    horizontal = if (compact) 8.dp else 20.dp,
                    vertical = if (compact) 9.dp else 12.dp
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(19.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = if (compact) MaterialTheme.typography.labelMedium else MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        return
    }
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
                buttons = pages.first().classicButtons,
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
        val layout = remember(pages) { defaultConsoleLayout(pages.first().consoleButtons) }
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

