package com.remerer.mobiledeck

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import org.json.JSONArray
import org.json.JSONObject

fun defaultDeckColors(): List<Color> {
    return listOf(
        Color(0xFF005A9C),
        Color(0xFF6A4C93),
        Color(0xFF006D77),
        Color(0xFF9D4E15),
        Color(0xFF4F772D),
        Color(0xFF8A1C1C)
    )
}

fun defaultButtons(): List<DeckButton> {
    val colors = defaultDeckColors()

    return listOf(
        DeckButton(1, "Bluetooth", "Connection", ICON_AUTO, "", DeckDisplayMode.IconAndText, DeckActionType.AppCommand, DeckActionType.BluetoothStatus.name, colors[0], position = 0),
        DeckButton(2, "Play", "Pause", ICON_AUTO, "", DeckDisplayMode.IconAndText, DeckActionType.MediaKey, MEDIA_PLAY_PAUSE, colors[1], position = 1),
        DeckButton(3, "Mute", "Media", ICON_AUTO, "", DeckDisplayMode.IconAndText, DeckActionType.MediaKey, MEDIA_MUTE, colors[0], position = 2),
        DeckButton(4, "Vol -", "Media", ICON_AUTO, "", DeckDisplayMode.IconAndText, DeckActionType.MediaKey, MEDIA_VOLUME_DOWN, colors[0], position = 3),
        DeckButton(5, "Vol +", "Media", ICON_AUTO, "", DeckDisplayMode.IconAndText, DeckActionType.MediaKey, MEDIA_VOLUME_UP, colors[2], position = 4),
        DeckButton(6, "Previous", "Track", ICON_AUTO, "", DeckDisplayMode.IconAndText, DeckActionType.MediaKey, MEDIA_PREVIOUS, colors[3], position = 6),
        DeckButton(7, "Stop", "Media", ICON_AUTO, "", DeckDisplayMode.IconAndText, DeckActionType.MediaKey, MEDIA_STOP, colors[5], position = 7),
        DeckButton(8, "Next", "Track", ICON_AUTO, "", DeckDisplayMode.IconAndText, DeckActionType.MediaKey, MEDIA_NEXT, colors[4], position = 8),
        DeckButton(9, "Desktop", "Win+D", ICON_AUTO, "", DeckDisplayMode.IconAndText, DeckActionType.Hotkey, "WIN+D", colors[4], position = 9),
        DeckButton(10, "Explorer", "Win+E", ICON_AUTO, "", DeckDisplayMode.IconAndText, DeckActionType.Hotkey, "WIN+E", colors[2], position = 10),
        DeckButton(11, "Task View", "Win+Tab", ICON_AUTO, "", DeckDisplayMode.IconAndText, DeckActionType.Hotkey, "WIN+TAB", colors[1], position = 11),
        DeckButton(12, "Run", "Win+R", ICON_AUTO, "", DeckDisplayMode.IconAndText, DeckActionType.Hotkey, "WIN+R", colors[3], position = 12),
        DeckButton(13, "Time", "Clock", ICON_AUTO, "", DeckDisplayMode.IconAndText, DeckActionType.Utility, UTILITY_TIME, colors[5], position = 13, spanColumns = 2),
        DeckButton(14, "Weather", "Forecast", ICON_AUTO, "", DeckDisplayMode.IconAndText, DeckActionType.Utility, UTILITY_WEATHER, colors[2], position = 15),
        DeckButton(15, "Settings", "Deck", ICON_SETTINGS, "", DeckDisplayMode.IconAndText, DeckActionType.Settings, "", colors[4], position = 16)
    )
}

fun defaultSecondPageButtons(): List<DeckButton> {
    val colors = defaultDeckColors()

    return listOf(
        DeckButton(16, "Prev Page", "Deck", ICON_PREVIOUS, "", DeckDisplayMode.IconAndText, DeckActionType.AppCommand, DeckActionType.PreviousPage.name, colors[3], position = 0),
        DeckButton(17, "Next Page", "Deck", ICON_NEXT, "", DeckDisplayMode.IconAndText, DeckActionType.AppCommand, DeckActionType.NextPage.name, colors[4], position = 1),
        DeckButton(18, "Copy", "Ctrl+C", ICON_KEYBOARD, "", DeckDisplayMode.IconAndText, DeckActionType.Hotkey, "CTRL+C", colors[2], position = 2),
        DeckButton(19, "Paste", "Ctrl+V", ICON_KEYBOARD, "", DeckDisplayMode.IconAndText, DeckActionType.Hotkey, "CTRL+V", colors[2], position = 3),
        DeckButton(20, "Select All", "Ctrl+A", ICON_KEYBOARD, "", DeckDisplayMode.IconAndText, DeckActionType.Hotkey, "CTRL+A", colors[1], position = 4),
        DeckButton(21, "Screenshot", "Win+Shift+S", ICON_KEYBOARD, "", DeckDisplayMode.IconAndText, DeckActionType.Hotkey, "WIN+SHIFT+S", colors[0], position = 5),
        DeckButton(22, "Notepad", "Run", ICON_APPS, "", DeckDisplayMode.IconAndText, DeckActionType.RunCommand, "notepad", colors[3], position = 6),
        DeckButton(23, "Calculator", "Run", ICON_APPS, "", DeckDisplayMode.IconAndText, DeckActionType.RunCommand, "calc", colors[5], position = 7),
        DeckButton(24, "Paint", "Run", ICON_APPS, "", DeckDisplayMode.IconAndText, DeckActionType.RunCommand, "mspaint", colors[1], position = 8),
        DeckButton(25, "Terminal", "Run", ICON_CODE, "", DeckDisplayMode.IconAndText, DeckActionType.RunCommand, "cmd", colors[0], position = 9),
        DeckButton(26, "Lock", "Win+L", ICON_KEYBOARD, "", DeckDisplayMode.IconAndText, DeckActionType.Hotkey, "WIN+L", colors[4], position = 10),
        DeckButton(27, "Task Manager", "Ctrl+Shift+Esc", ICON_KEYBOARD, "", DeckDisplayMode.IconAndText, DeckActionType.Hotkey, "CTRL+SHIFT+ESC", colors[5], position = 11),
        DeckButton(28, "Hello", "Text", ICON_TEXT, "", DeckDisplayMode.IconAndText, DeckActionType.Text, "Hello from MobileDeck", colors[2], position = 12, spanColumns = 2),
        DeckButton(29, "Control", "Panel", ICON_APPS, "", DeckDisplayMode.IconAndText, DeckActionType.RunCommand, "control", colors[3], position = 14),
        DeckButton(30, "Refresh", "F5", ICON_KEYBOARD, "", DeckDisplayMode.IconAndText, DeckActionType.Hotkey, "F5", colors[0], position = 15)
    )
}

fun loadDeckButtons(context: Context): List<DeckButton> {
    val raw = context.deckPrefs().getString(PREF_BUTTONS, null) ?: return defaultButtons()
    return runCatching {
        val array = JSONArray(raw)
        List(array.length().coerceAtMost(MAX_PAGES)) { index ->
            decodeDeckButton(array.getJSONObject(index), index)
        }
    }.map { normalizeDeckButtons(it) }.getOrDefault(defaultButtons())
}

fun saveDeckButtons(context: Context, buttons: List<DeckButton>) {
    val array = JSONArray()
    buttons.forEach { button ->
        array.put(encodeDeckButton(button))
    }
    context.deckPrefs().edit().putString(PREF_BUTTONS, array.toString()).apply()
}

fun loadDeckPages(context: Context): List<DeckPageConfig> {
    val raw = context.deckPrefs().getString(PREF_PAGES, null)
        ?: return defaultDeckPages(loadDeckButtons(context))
    return runCatching {
        val array = JSONArray(raw)
        val pages = List(array.length()) { index ->
            val item = array.getJSONObject(index)
            val buttons = item.optJSONArray("buttons") ?: JSONArray()
            DeckPageConfig(
                id = item.getInt("id"),
                name = item.optString("name", "Page ${index + 1}"),
                buttons = List(buttons.length()) { buttonIndex ->
                    decodeDeckButton(buttons.getJSONObject(buttonIndex), buttonIndex)
                }
            )
        }
        if (pages.isEmpty()) {
            defaultDeckPages()
        } else {
            ensureSettingsButton(pages)
        }
    }.getOrDefault(defaultDeckPages())
}

fun defaultDeckPages(firstPageButtons: List<DeckButton> = defaultButtons()): List<DeckPageConfig> {
    return listOf(
        DeckPageConfig(1, "Page 1", firstPageButtons),
        DeckPageConfig(2, "Page 2", defaultSecondPageButtons())
    )
}

fun saveDeckPages(context: Context, pages: List<DeckPageConfig>) {
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

fun loadConsoleLayout(context: Context): ConsoleLayoutConfig {
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

fun saveConsoleLayout(context: Context, layout: ConsoleLayoutConfig) {
    val array = JSONArray()
    layout.rows.forEach { row ->
        val rowArray = JSONArray()
        row.forEach { rowArray.put(it) }
        array.put(rowArray)
    }
    context.deckPrefs().edit().putString(PREF_CONSOLE_LAYOUT, array.toString()).apply()
}

fun loadConsolePanelOptions(context: Context): ConsolePanelOptions {
    val prefs = context.deckPrefs()
    return ConsolePanelOptions(
        showConnection = prefs.getBoolean(PREF_CONSOLE_PANEL_CONNECTION, true),
        showMessage = prefs.getBoolean(PREF_CONSOLE_PANEL_MESSAGE, true),
        showClock = prefs.getBoolean(PREF_CONSOLE_PANEL_CLOCK, true),
        showDate = prefs.getBoolean(PREF_CONSOLE_PANEL_DATE, true)
    )
}

fun saveConsolePanelOptions(context: Context, options: ConsolePanelOptions) {
    context.deckPrefs().edit()
        .putBoolean(PREF_CONSOLE_PANEL_CONNECTION, options.showConnection)
        .putBoolean(PREF_CONSOLE_PANEL_MESSAGE, options.showMessage)
        .putBoolean(PREF_CONSOLE_PANEL_CLOCK, options.showClock)
        .putBoolean(PREF_CONSOLE_PANEL_DATE, options.showDate)
        .apply()
}

fun encodeDeckButton(button: DeckButton): JSONObject {
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

fun decodeDeckButton(item: JSONObject, fallbackPosition: Int): DeckButton {
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

fun normalizeDeckButtons(buttons: List<DeckButton>): List<DeckButton> {
    if (buttons.any { it.actionType == DeckActionType.Settings }) return buttons
    val colors = defaultDeckColors()
    val settingsButton = DeckButton(
        id = nextDeckButtonId(buttons),
        title = "Settings",
        subtitle = "Deck",
        icon = ICON_SETTINGS,
        iconImageUri = "",
        displayMode = DeckDisplayMode.IconAndText,
        actionType = DeckActionType.Settings,
        payload = "",
        color = colors[4],
        position = nextOpenPosition(buttons, DEFAULT_COLUMNS * DEFAULT_ROWS - 1)
    )
    return listOf(settingsButton) + buttons
}

fun ensureSettingsButton(pages: List<DeckPageConfig>): List<DeckPageConfig> {
    return if (hasSettingsButton(pages)) pages else restoreSettingsButton(pages, DEFAULT_COLUMNS, DEFAULT_ROWS)
}

fun hasSettingsButton(pages: List<DeckPageConfig>): Boolean {
    return pages.any { page -> page.buttons.any { buttonAppAction(it) == DeckActionType.Settings } }
}

fun restoreSettingsButton(
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

fun settingsDeckButton(existingButtons: List<DeckButton>, position: Int): DeckButton {
    val colors = defaultDeckColors()
    return DeckButton(
        id = nextDeckButtonId(existingButtons),
        title = "Settings",
        subtitle = "Deck",
        icon = ICON_SETTINGS,
        iconImageUri = "",
        displayMode = DeckDisplayMode.IconAndText,
        actionType = DeckActionType.Settings,
        payload = "",
        color = colors[4],
        position = position
    )
}

fun payloadRequired(actionType: DeckActionType): Boolean {
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

fun appCommandAction(payload: String): DeckActionType? {
    return runCatching {
        DeckActionType.valueOf(payload)
    }.getOrNull()?.takeIf {
        it == DeckActionType.Settings ||
            it == DeckActionType.BluetoothStatus ||
            it == DeckActionType.PreviousPage ||
            it == DeckActionType.NextPage
    }
}

fun buttonAppAction(button: DeckButton): DeckActionType? {
    return buttonAppAction(button.actionType, button.payload)
}

fun buttonAppAction(actionType: DeckActionType, payload: String): DeckActionType? {
    return when (actionType) {
        DeckActionType.Settings,
        DeckActionType.BluetoothStatus,
        DeckActionType.PreviousPage,
        DeckActionType.NextPage -> actionType
        DeckActionType.AppCommand -> appCommandAction(payload)
        else -> null
    }
}

fun loadDeckColumns(context: Context): Int {
    return context.deckPrefs().getInt(PREF_COLUMNS, DEFAULT_COLUMNS).coerceIn(MIN_COLUMNS, MAX_COLUMNS)
}

fun saveDeckColumns(context: Context, columns: Int) {
    context.deckPrefs().edit().putInt(PREF_COLUMNS, columns.coerceIn(MIN_COLUMNS, MAX_COLUMNS)).apply()
}

fun loadDeckRows(context: Context): Int {
    return context.deckPrefs().getInt(PREF_ROWS, DEFAULT_ROWS).coerceIn(MIN_ROWS, MAX_ROWS)
}

fun saveDeckRows(context: Context, rows: Int) {
    context.deckPrefs().edit().putInt(PREF_ROWS, rows.coerceIn(MIN_ROWS, MAX_ROWS)).apply()
}

fun loadDeckSpacing(context: Context): Int {
    return context.deckPrefs().getInt(PREF_SPACING, DEFAULT_SPACING_DP).coerceIn(MIN_SPACING_DP, MAX_SPACING_DP)
}

fun saveDeckSpacing(context: Context, spacing: Int) {
    context.deckPrefs().edit().putInt(PREF_SPACING, spacing.coerceIn(MIN_SPACING_DP, MAX_SPACING_DP)).apply()
}

fun loadPageSwipeAxis(context: Context): PageSwipeAxis {
    return runCatching {
        PageSwipeAxis.valueOf(context.deckPrefs().getString(PREF_PAGE_SWIPE_AXIS, null) ?: PageSwipeAxis.Horizontal.name)
    }.getOrDefault(PageSwipeAxis.Horizontal)
}

fun savePageSwipeAxis(context: Context, axis: PageSwipeAxis) {
    context.deckPrefs().edit().putString(PREF_PAGE_SWIPE_AXIS, axis.name).apply()
}

fun loadPageSwipeMode(context: Context): PageSwipeMode {
    return runCatching {
        PageSwipeMode.valueOf(context.deckPrefs().getString(PREF_PAGE_SWIPE_MODE, null) ?: PageSwipeMode.SingleTouch.name)
    }.getOrElse {
        if (context.deckPrefs().getBoolean(PREF_MULTI_TOUCH_PAGE_SWIPE, true)) {
            PageSwipeMode.MultiTouch
        } else {
            PageSwipeMode.Disabled
        }
    }
}

fun savePageSwipeMode(context: Context, mode: PageSwipeMode) {
    context.deckPrefs().edit()
        .putString(PREF_PAGE_SWIPE_MODE, mode.name)
        .putBoolean(PREF_MULTI_TOUCH_PAGE_SWIPE, mode == PageSwipeMode.MultiTouch)
        .apply()
}

fun loadPageSwipeAnimation(context: Context): Boolean {
    return context.deckPrefs().getBoolean(PREF_PAGE_SWIPE_ANIMATION, true)
}

fun savePageSwipeAnimation(context: Context, enabled: Boolean) {
    context.deckPrefs().edit().putBoolean(PREF_PAGE_SWIPE_ANIMATION, enabled).apply()
}

fun loadInfinitePageSwipe(context: Context): Boolean {
    return context.deckPrefs().getBoolean(PREF_INFINITE_PAGE_SWIPE, true)
}

fun saveInfinitePageSwipe(context: Context, enabled: Boolean) {
    context.deckPrefs().edit().putBoolean(PREF_INFINITE_PAGE_SWIPE, enabled).apply()
}

fun loadButtonVibrationLevel(context: Context): ButtonVibrationLevel {
    return runCatching {
        ButtonVibrationLevel.valueOf(
            context.deckPrefs().getString(PREF_BUTTON_VIBRATION_LEVEL, null) ?: ButtonVibrationLevel.Strong.name
        )
    }.getOrDefault(ButtonVibrationLevel.Strong)
}

fun saveButtonVibrationLevel(context: Context, level: ButtonVibrationLevel) {
    context.deckPrefs().edit().putString(PREF_BUTTON_VIBRATION_LEVEL, level.name).apply()
}

fun loadClassicSolidButtonBackground(context: Context): Boolean {
    return context.deckPrefs().getBoolean(PREF_CLASSIC_SOLID_BUTTON_BACKGROUND, true)
}

fun saveClassicSolidButtonBackground(context: Context, enabled: Boolean) {
    context.deckPrefs().edit().putBoolean(PREF_CLASSIC_SOLID_BUTTON_BACKGROUND, enabled).apply()
}

fun loadClassicDeckBackground(context: Context): ClassicDeckBackground {
    val prefs = context.deckPrefs()
    val type = runCatching {
        ClassicDeckBackgroundType.valueOf(
            prefs.getString(PREF_CLASSIC_DECK_BACKGROUND_TYPE, null) ?: ClassicDeckBackgroundType.Default.name
        )
    }.getOrDefault(ClassicDeckBackgroundType.Default)
    return ClassicDeckBackground(
        type = type,
        color = Color(prefs.getInt(PREF_CLASSIC_DECK_BACKGROUND_COLOR, 0xFF10151B.toInt())),
        imageUri = prefs.getString(PREF_CLASSIC_DECK_BACKGROUND_IMAGE_URI, null).orEmpty()
    )
}

fun saveClassicDeckBackground(context: Context, background: ClassicDeckBackground) {
    context.deckPrefs().edit()
        .putString(PREF_CLASSIC_DECK_BACKGROUND_TYPE, background.type.name)
        .putInt(PREF_CLASSIC_DECK_BACKGROUND_COLOR, background.color.toArgb())
        .putString(PREF_CLASSIC_DECK_BACKGROUND_IMAGE_URI, background.imageUri)
        .apply()
}

fun loadDeckUiMode(context: Context): DeckUiMode {
    return runCatching {
        DeckUiMode.valueOf(context.deckPrefs().getString(PREF_DECK_UI_MODE, null) ?: DeckUiMode.Classic.name)
    }.getOrDefault(DeckUiMode.Classic)
}

fun saveDeckUiMode(context: Context, mode: DeckUiMode) {
    context.deckPrefs().edit().putString(PREF_DECK_UI_MODE, mode.name).apply()
}

fun shouldShowClassicTutorial(context: Context): Boolean {
    return !context.deckPrefs().getBoolean(PREF_CLASSIC_TUTORIAL_SEEN, false)
}

fun saveClassicTutorialSeen(context: Context) {
    context.deckPrefs().edit().putBoolean(PREF_CLASSIC_TUTORIAL_SEEN, true).apply()
}

private fun Context.deckPrefs() = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

const val PREFS_NAME = "mobile_deck"
const val PREF_PAGES = "pages"
const val PREF_BUTTONS = "buttons"
const val PREF_COLUMNS = "columns"
const val PREF_ROWS = "rows"
const val PREF_SPACING = "spacing"
const val PREF_PAGE_SWIPE_AXIS = "page_swipe_axis"
const val PREF_PAGE_SWIPE_MODE = "page_swipe_mode"
const val PREF_MULTI_TOUCH_PAGE_SWIPE = "multi_touch_page_swipe"
const val PREF_PAGE_SWIPE_ANIMATION = "page_swipe_animation"
const val PREF_INFINITE_PAGE_SWIPE = "infinite_page_swipe"
const val PREF_BUTTON_VIBRATION_LEVEL = "button_vibration_level"
const val PREF_CLASSIC_SOLID_BUTTON_BACKGROUND = "classic_solid_button_background"
const val PREF_CLASSIC_DECK_BACKGROUND_TYPE = "classic_deck_background_type"
const val PREF_CLASSIC_DECK_BACKGROUND_COLOR = "classic_deck_background_color"
const val PREF_CLASSIC_DECK_BACKGROUND_IMAGE_URI = "classic_deck_background_image_uri"
const val PREF_DECK_UI_MODE = "deck_ui_mode"
const val PREF_CLASSIC_TUTORIAL_SEEN = "classic_tutorial_seen"
const val PREF_CONSOLE_LAYOUT = "console_layout"
const val PREF_CONSOLE_PANEL_CONNECTION = "console_panel_connection"
const val PREF_CONSOLE_PANEL_MESSAGE = "console_panel_message"
const val PREF_CONSOLE_PANEL_CLOCK = "console_panel_clock"
const val PREF_CONSOLE_PANEL_DATE = "console_panel_date"
const val APP_WIDGET_HOST_ID = 4201
const val INVALID_APP_WIDGET_ID = -1
const val APP_ICON_URI_PREFIX = "app-icon:"
const val MAX_PAGES = 5
const val MIN_COLUMNS = 4
const val MAX_COLUMNS = 12
const val DEFAULT_COLUMNS = 6
const val MIN_ROWS = 2
const val MAX_ROWS = 6
const val DEFAULT_ROWS = 3
const val MAX_BUTTON_SPAN_COLUMNS = 3
const val MAX_BUTTON_SPAN_ROWS = 2
const val MIN_SPACING_DP = 2
const val MAX_SPACING_DP = 16
const val DEFAULT_SPACING_DP = 8
const val ICON_AUTO = "AUTO"
const val ICON_SETTINGS = "ICON_SETTINGS"
const val ICON_BLUETOOTH = "ICON_BLUETOOTH"
const val ICON_KEYBOARD = "ICON_KEYBOARD"
const val ICON_APPS = "ICON_APPS"
const val ICON_CODE = "ICON_CODE"
const val ICON_TEXT = "ICON_TEXT"
const val ICON_PLAY = "ICON_PLAY"
const val ICON_STOP = "ICON_STOP"
const val ICON_PREVIOUS = "ICON_PREVIOUS"
const val ICON_NEXT = "ICON_NEXT"
const val ICON_VOLUME_OFF = "ICON_VOLUME_OFF"
const val ICON_VOLUME_DOWN = "ICON_VOLUME_DOWN"
const val ICON_VOLUME_UP = "ICON_VOLUME_UP"
const val MEDIA_MUTE = "MUTE"
const val MEDIA_PLAY_PAUSE = "PLAY_PAUSE"
const val MEDIA_STOP = "STOP"
const val MEDIA_PREVIOUS = "PREVIOUS"
const val MEDIA_NEXT = "NEXT"
const val MEDIA_VOLUME_DOWN = "VOLUME_DOWN"
const val MEDIA_VOLUME_UP = "VOLUME_UP"
const val UTILITY_TIME = "TIME"
const val UTILITY_WEATHER = "WEATHER"


