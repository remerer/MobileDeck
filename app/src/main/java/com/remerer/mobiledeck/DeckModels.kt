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
    Utility(R.string.action_utility),
    AppCommand(R.string.action_app_command)
}

enum class EditActionPanel(@StringRes val labelRes: Int) {
    AppCommand(R.string.app_command_target),
    KeyboardInput(R.string.action_keyboard_input),
    Widget(R.string.action_widget),
    MediaKey(R.string.action_media_key),
    RunCommand(R.string.action_run_command),
    Utility(R.string.action_utility)
}

enum class DeckDisplayMode(@StringRes val labelRes: Int) {
    IconOnly(R.string.display_icon_only),
    IconAndText(R.string.display_icon_and_text),
    KeywordOnly(R.string.display_keyword_only)
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
    val appWidgetTouchable: Boolean = true
)

data class UtilityChoice(
    val payload: String,
    @StringRes val labelRes: Int
)

data class DeckPageConfig(
    val id: Int,
    val name: String,
    val buttons: List<DeckButton>
)

data class DragSwapCandidate(
    val draggedButtonId: Int,
    val targetButtonId: Int,
    val sourcePosition: Int,
    val targetPosition: Int
)

data class ConsoleLayoutConfig(
    val rows: List<List<Int>>
)

data class ConsolePanelOptions(
    val showConnection: Boolean = true,
    val showMessage: Boolean = true,
    val showClock: Boolean = true,
    val showDate: Boolean = true
)

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

val DefaultDeckThemeColors = DeckThemeColors(
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

val LocalDeckThemeColors = staticCompositionLocalOf { DefaultDeckThemeColors }

val ClassicLayoutAccent = Color(0xFF9B5DE5)
val ClassicLayoutSecondaryAccent = Color(0xFF5F2AA0)
val ClassicButtonAccent = Color(0xFFE47B17)
val ClassicButtonSecondaryAccent = Color(0xFFB85B00)

enum class AppPage {
    Deck,
    LayoutEditor,
    ConsoleLayoutEditor,
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
