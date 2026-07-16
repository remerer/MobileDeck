package com.remerer.mobiledeck

import androidx.annotation.StringRes
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import com.remerer.mobiledeck.ui.theme.MobileDeckThemeStyle

enum class DeckActionType(@StringRes val labelRes: Int) {
    Settings(R.string.action_settings),
    BluetoothStatus(R.string.action_bluetooth_status),
    PreviousPage(R.string.action_previous_page),
    NextPage(R.string.action_next_page),
    MediaKey(R.string.action_media_key),
    Hotkey(R.string.action_hotkey),
    Text(R.string.action_text),
    RunCommand(R.string.action_run_command),
    CompanionCommand(R.string.action_companion_command),
    CompanionControl(R.string.action_companion_control),
    CompanionStatus(R.string.action_companion_status),
    Utility(R.string.action_utility),
    AppCommand(R.string.action_app_command)
}

enum class EditActionPanel(@StringRes val labelRes: Int) {
    AppCommand(R.string.app_command_target),
    KeyboardInput(R.string.action_keyboard_input),
    Widget(R.string.action_widget),
    RunCommand(R.string.action_run_command),
    Utility(R.string.action_utility)
}

enum class DeckDisplayMode(@StringRes val labelRes: Int) {
    IconOnly(R.string.display_icon_only),
    IconAndText(R.string.display_icon_and_text),
    KeywordOnly(R.string.display_keyword_only)
}

enum class DeckControlStyle(@StringRes val labelRes: Int) {
    Button(R.string.control_style_button),
    TrimSlider(R.string.control_style_trim_slider),
    TrimKnob(R.string.control_style_trim_knob),
    InfiniteWheel(R.string.control_style_infinite_wheel),
    JoyPad(R.string.control_style_joypad),
    AnalogStick(R.string.control_style_analog_stick),
    CompanionToggle(R.string.control_style_companion_toggle)
}

enum class PageSwipeAxis(@StringRes val labelRes: Int, @StringRes val shortLabelRes: Int) {
    Horizontal(R.string.page_axis_horizontal, R.string.page_axis_horizontal_short),
    Vertical(R.string.page_axis_vertical, R.string.page_axis_vertical_short)
}

enum class PageSwipeMode(@StringRes val labelRes: Int) {
    Disabled(R.string.page_swipe_mode_disabled),
    SingleTouch(R.string.page_swipe_mode_single),
    MultiTouch(R.string.page_swipe_mode_multi);

    fun next(): PageSwipeMode {
        val values = values()
        return values[(ordinal + 1) % values.size]
    }
}

enum class DeckUiMode(@StringRes val labelRes: Int) {
    Classic(R.string.deck_ui_classic),
    Console(R.string.deck_ui_console)
}

enum class DeckFontSizeOption(
    @StringRes val labelRes: Int,
    @StringRes val shortLabelRes: Int,
    val scale: Float
) {
    System(R.string.font_size_system, R.string.font_size_system_short, 1.0f),
    Small(R.string.font_size_small, R.string.font_size_small_short, 0.90f),
    Medium(R.string.font_size_medium, R.string.font_size_medium_short, 1.0f),
    Large(R.string.font_size_large, R.string.font_size_large_short, 1.12f)
}

enum class ClassicDeckBackgroundType(@StringRes val labelRes: Int) {
    Default(R.string.classic_background_default),
    Color(R.string.classic_background_color),
    Image(R.string.classic_background_image)
}

data class ClassicDeckBackground(
    val type: ClassicDeckBackgroundType = ClassicDeckBackgroundType.Default,
    val color: Color = Color(0xFF10151B),
    val imageUri: String = ""
)

fun DeckUiMode.toThemeStyle(): MobileDeckThemeStyle {
    return when (this) {
        DeckUiMode.Classic -> MobileDeckThemeStyle.Classic
        DeckUiMode.Console -> MobileDeckThemeStyle.Console
    }
}

data class DeckButton(
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
    val appWidgetTouchable: Boolean = true,
    val controlStyle: DeckControlStyle = DeckControlStyle.Button,
    val controlStyleRaw: String = controlStyle.name,
    val companionControl: String = ""
)

enum class DeckButtonPlatformAvailability {
    Shared,
    AndroidOnly,
    CompanionRequired
}

enum class DeckButtonExecutionRoute {
    AndroidOrHid,
    CompanionOpen,
    CompanionProgramCommand,
    CompanionControlUpdate,
    ReadOnly,
    Unavailable
}

data class DeckButtonExecutionDecision(
    val route: DeckButtonExecutionRoute,
    val source: String = "",
    val value: Any? = null
)

fun DeckButton.platformAvailability(): DeckButtonPlatformAvailability {
    return when (actionType) {
        DeckActionType.Settings,
        DeckActionType.BluetoothStatus,
        DeckActionType.PreviousPage,
        DeckActionType.NextPage,
        DeckActionType.Utility,
        DeckActionType.AppCommand -> DeckButtonPlatformAvailability.AndroidOnly

        DeckActionType.CompanionCommand,
        DeckActionType.CompanionStatus -> DeckButtonPlatformAvailability.CompanionRequired

        DeckActionType.CompanionControl -> when (controlStyle) {
            DeckControlStyle.JoyPad,
            DeckControlStyle.AnalogStick -> DeckButtonPlatformAvailability.Shared
            else -> DeckButtonPlatformAvailability.CompanionRequired
        }

        DeckActionType.MediaKey,
        DeckActionType.Hotkey,
        DeckActionType.Text,
        DeckActionType.RunCommand -> DeckButtonPlatformAvailability.Shared
    }
}

fun DeckButton.executionDecision(
    companionAvailable: Boolean,
    payloadOverride: String = payload,
    companionControlSource: String = payload,
    companionControlValue: Any? = null
): DeckButtonExecutionDecision {
    if (platformAvailability() != DeckButtonPlatformAvailability.CompanionRequired) {
        return DeckButtonExecutionDecision(DeckButtonExecutionRoute.AndroidOrHid)
    }
    if (!companionAvailable) {
        return DeckButtonExecutionDecision(DeckButtonExecutionRoute.Unavailable)
    }
    return when (actionType) {
        DeckActionType.CompanionStatus -> DeckButtonExecutionDecision(DeckButtonExecutionRoute.ReadOnly)
        DeckActionType.CompanionCommand -> DeckButtonExecutionDecision(
            route = if (payloadOverride.trim().startsWithHttpScheme()) {
                DeckButtonExecutionRoute.CompanionOpen
            } else {
                DeckButtonExecutionRoute.CompanionProgramCommand
            }
        )
        DeckActionType.CompanionControl -> DeckButtonExecutionDecision(
            route = DeckButtonExecutionRoute.CompanionControlUpdate,
            source = companionControlSource,
            value = companionControlValue
        )
        else -> DeckButtonExecutionDecision(DeckButtonExecutionRoute.Unavailable)
    }
}

private fun String.startsWithHttpScheme(): Boolean {
    return startsWith("http://", ignoreCase = true) || startsWith("https://", ignoreCase = true)
}

data class DeckButtonDisplayCapabilities(
    val supportsIconImage: Boolean,
    val supportsText: Boolean,
    val dedicatedControlSurfaceOnly: Boolean
)

fun deckButtonDisplayCapabilities(
    controlStyle: DeckControlStyle,
    actionType: DeckActionType
): DeckButtonDisplayCapabilities {
    val dedicatedControlSurfaceOnly = controlStyle != DeckControlStyle.Button
    if (dedicatedControlSurfaceOnly) {
        return DeckButtonDisplayCapabilities(
            supportsIconImage = false,
            supportsText = false,
            dedicatedControlSurfaceOnly = true
        )
    }
    return when (actionType) {
        DeckActionType.CompanionControl -> DeckButtonDisplayCapabilities(
            supportsIconImage = false,
            supportsText = true,
            dedicatedControlSurfaceOnly = false
        )
        else -> DeckButtonDisplayCapabilities(
            supportsIconImage = true,
            supportsText = true,
            dedicatedControlSurfaceOnly = false
        )
    }
}

fun DeckButton.displayCapabilities(): DeckButtonDisplayCapabilities {
    return deckButtonDisplayCapabilities(controlStyle, actionType)
}

data class UtilityChoice(
    val payload: String,
    @StringRes val labelRes: Int
)

data class DeckPageConfig(
    val id: Int,
    val name: String,
    val buttons: List<DeckButton>,
    val classicButtons: List<DeckButton> = buttons,
    val consoleButtons: List<DeckButton> = buttons
) {
    fun buttonsForMode(mode: DeckUiMode): List<DeckButton> {
        return when (mode) {
            DeckUiMode.Classic -> classicButtons
            DeckUiMode.Console -> consoleButtons
        }
    }

    fun withButtonsForMode(mode: DeckUiMode, updatedButtons: List<DeckButton>): DeckPageConfig {
        return when (mode) {
            DeckUiMode.Classic -> copy(
                buttons = updatedButtons,
                classicButtons = updatedButtons
            )
            DeckUiMode.Console -> copy(
                buttons = updatedButtons,
                consoleButtons = updatedButtons
            )
        }
    }
}

data class DragSwapCandidate(
    val draggedButtonId: Int,
    val targetButtonId: Int,
    val sourcePosition: Int,
    val targetPosition: Int
)

data class ConsoleLayoutConfig(
    val rows: List<List<Int>>,
    val rowWeights: List<Float> = emptyList(),
    val sidebarFraction: Float = CONSOLE_DEFAULT_SIDEBAR_FRACTION
)

data class ConsolePanelOptions(
    val showConnection: Boolean = true,
    val showMessage: Boolean = true,
    val showClock: Boolean = true,
    val showDate: Boolean = true
)

data class CompanionSettings(
    val enabled: Boolean = false,
    val endpoint: String = "",
    val pairingToken: String = ""
) {
    fun isConfigured(): Boolean = enabled && endpoint.isNotBlank() && pairingToken.isNotBlank()
}

data class CompanionConnectionStatus(
    val connected: Boolean = false,
    val message: String = "",
    val appName: String = "",
    val version: String = "",
    val capabilities: Set<String> = emptySet()
)

enum class CompanionControlMode {
    HidOnly,
    CompanionConnected,
    CompanionActive,
    Disconnected
}

data class ActivityLog(
    val buttonTitle: String,
    val payload: String,
    val delivered: Boolean,
    val note: String
)

data class IconChoice(
    val key: String,
    @StringRes val labelRes: Int
)

data class LaunchableAppChoice(
    val label: String,
    val packageName: String,
    val icon: ImageBitmap?
)

data class MediaKeyChoice(
    val payload: String,
    @StringRes val labelRes: Int
)

data class PageAnimationTarget(
    val pageId: Int,
    val delta: Int,
    val sequence: Int
)

data class DeckThemeColors(
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

val ClassicDarkDeckThemeColors = DeckThemeColors(
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
    consoleSidebar = Color(0xFF17212B),
    consolePreviewBackground = Color(0xFF101820),
    consoleButtonDefault = Color(0xFF24313D),
    consoleButtonFeatured = Color(0xFF245B9D),
    consoleButtonSystem = Color(0xFF1F5DAD)
)

val ClassicLightDeckThemeColors = DeckThemeColors(
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

val ConsoleDarkDeckThemeColors = DeckThemeColors(
    backgroundGradient = listOf(Color(0xFF08111A), Color(0xFF112033), Color(0xFF0A1825)),
    sidebarBackground = Color(0xFF0A1520).copy(alpha = 0.92f),
    sidebarBorder = Color.White.copy(alpha = 0.08f),
    cardBackground = Color(0xFF142638).copy(alpha = 0.9f),
    cardBorder = Color.White.copy(alpha = 0.045f),
    textPrimary = Color.White,
    textSecondary = Color.White.copy(alpha = 0.64f),
    textMuted = Color.White.copy(alpha = 0.38f),
    toggleBackground = Color(0xFF192B3D),
    actionStart = Color(0xFF132331),
    actionEnd = Color(0xFF0F1D2B),
    neutralIconBackground = Color(0xFF263A4D),
    consoleSidebar = Color(0xFF1D2E40),
    consolePreviewBackground = Color(0xFF0E1E2D),
    consoleButtonDefault = Color(0xFF233548),
    consoleButtonFeatured = Color(0xFF245B9D),
    consoleButtonSystem = Color(0xFF1F5DAD)
)

val ConsoleLightDeckThemeColors = DeckThemeColors(
    backgroundGradient = listOf(Color(0xFFF6FAFD), Color(0xFFECF3F8), Color(0xFFE1ECF3)),
    sidebarBackground = Color(0xFFFAFCFE).copy(alpha = 0.98f),
    sidebarBorder = Color(0xFFC9D8E4).copy(alpha = 0.72f),
    cardBackground = Color(0xFFFAFCFE).copy(alpha = 0.96f),
    cardBorder = Color(0xFFC8D6E3).copy(alpha = 0.58f),
    textPrimary = Color(0xFF172A3D),
    textSecondary = Color(0xFF64748A),
    textMuted = Color(0xFF91A0AE),
    toggleBackground = Color(0xFFF0F5F9),
    actionStart = Color(0xFFF8FCFF),
    actionEnd = Color(0xFFEAF5FB),
    neutralIconBackground = Color(0xFFE4EEF6),
    consoleSidebar = Color(0xFFFAFCFE),
    consolePreviewBackground = Color(0xFFEEF5FA),
    consoleButtonDefault = Color(0xFFF4F8FB),
    consoleButtonFeatured = Color(0xFF1976B7),
    consoleButtonSystem = Color(0xFF1D82BE)
)

fun deckThemeColors(mode: DeckUiMode, darkTheme: Boolean): DeckThemeColors {
    return when (mode) {
        DeckUiMode.Classic -> if (darkTheme) ClassicDarkDeckThemeColors else ClassicLightDeckThemeColors
        DeckUiMode.Console -> if (darkTheme) ConsoleDarkDeckThemeColors else ConsoleLightDeckThemeColors
    }
}

val DefaultDeckThemeColors = ClassicDarkDeckThemeColors

val LocalDeckThemeColors = staticCompositionLocalOf { DefaultDeckThemeColors }

val ClassicLayoutAccent = Color(0xFF9B5DE5)
val ClassicLayoutSecondaryAccent = Color(0xFF5F2AA0)
val ClassicBackgroundAccent = Color(0xFF008B8B)
val ClassicBackgroundSecondaryAccent = Color(0xFF005F73)
val ClassicButtonAccent = Color(0xFFE47B17)
val ClassicButtonSecondaryAccent = Color(0xFFB85B00)

enum class AppPage {
    Deck,
    LayoutEditor,
    ConsoleLayoutEditor,
    IconStyleTest,
    Settings
}

enum class BluetoothPermissionAction {
    RegisterHid,
    MakeDiscoverable
}

enum class ButtonVibrationLevel(
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
