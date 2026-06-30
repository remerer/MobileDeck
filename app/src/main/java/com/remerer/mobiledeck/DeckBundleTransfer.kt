package com.remerer.mobiledeck

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.util.Base64
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.drawable.toBitmap
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val BUNDLE_FORMAT = "mobiledeck.bundle"
private const val BUNDLE_VERSION = 2
private const val ASSET_URI_PREFIX = "asset:"

private data class DeckBundleScreen(
    val widthPx: Int,
    val heightPx: Int
) {
    val aspectRatio: Double
        get() = widthPx.toDouble() / heightPx.coerceAtLeast(1).toDouble()
}

data class DeckBundleSnapshot(
    val pages: List<DeckPageConfig>,
    val consoleLayouts: Map<Int, ConsoleLayoutConfig>,
    val columns: Int,
    val rows: Int,
    val spacing: Int,
    val pageSwipeAxis: PageSwipeAxis,
    val pageSwipeMode: PageSwipeMode,
    val pageSwipeAnimation: Boolean,
    val infinitePageSwipe: Boolean,
    val buttonVibrationLevel: ButtonVibrationLevel,
    val classicSolidButtonBackground: Boolean,
    val classicDeckBackground: ClassicDeckBackground,
    val deckUiMode: DeckUiMode,
    val classicFontSize: DeckFontSizeOption,
    val consoleFontSize: DeckFontSizeOption,
    val consolePanelOptions: ConsolePanelOptions
)

data class ImportedDeckBundle(
    val pages: List<DeckPageConfig>,
    val consoleLayouts: Map<Int, ConsoleLayoutConfig>,
    val columns: Int,
    val rows: Int,
    val spacing: Int,
    val pageSwipeAxis: PageSwipeAxis,
    val pageSwipeMode: PageSwipeMode,
    val pageSwipeAnimation: Boolean,
    val infinitePageSwipe: Boolean,
    val buttonVibrationLevel: ButtonVibrationLevel,
    val classicSolidButtonBackground: Boolean,
    val classicDeckBackground: ClassicDeckBackground,
    val deckUiMode: DeckUiMode,
    val classicFontSize: DeckFontSizeOption,
    val consoleFontSize: DeckFontSizeOption,
    val consolePanelOptions: ConsolePanelOptions
)

fun Context.createDeckBundleJson(snapshot: DeckBundleSnapshot): String {
    val assets = JSONArray()
    val assetByUri = linkedMapOf<String, String>()

    fun assetRefFor(uriString: String): String {
        if (uriString.isBlank()) return uriString
        assetByUri[uriString]?.let { return "$ASSET_URI_PREFIX$it" }
        val bytes = readBundleAssetBytes(uriString) ?: return ""
        val assetId = "asset_${assetByUri.size + 1}"
        val mimeType = bundleAssetMimeType(uriString)
        assetByUri[uriString] = assetId
        assets.put(
            JSONObject()
                .put("id", assetId)
                .put("sourceType", bundleAssetSourceType(uriString))
                .put("mimeType", mimeType)
                .put("data", Base64.encodeToString(bytes, Base64.NO_WRAP))
        )
        return "$ASSET_URI_PREFIX$assetId"
    }

    val pagesArray = JSONArray()
    snapshot.pages.forEach { page ->
        fun portableButtons(buttons: List<DeckButton>): JSONArray {
            val array = JSONArray()
            buttons.forEach { button ->
                val portable = button.copy(
                    iconImageUri = assetRefFor(button.iconImageUri),
                    appWidgetId = INVALID_APP_WIDGET_ID
                )
                array.put(
                    encodeDeckButton(portable)
                        .put("pcAvailability", pcAvailabilityForButton(button))
                )
            }
            return array
        }
        val buttons = portableButtons(page.buttonsForMode(snapshot.deckUiMode))
        pagesArray.put(
            JSONObject()
                .put("id", page.id)
                .put("name", page.name)
                .put("buttons", buttons)
                .put("classicButtons", portableButtons(page.classicButtons))
                .put("consoleButtons", portableButtons(page.consoleButtons))
        )
    }

    val background = snapshot.classicDeckBackground
    val portableBackground = if (background.type == ClassicDeckBackgroundType.Image) {
        background.copy(imageUri = assetRefFor(background.imageUri))
    } else {
        background
    }

    val consoleLayoutsRoot = JSONObject()
    snapshot.consoleLayouts.forEach { (pageId, layout) ->
        consoleLayoutsRoot.put(pageId.toString(), encodeConsoleLayout(layout))
    }

    val deviceId = mobileDeckDeviceId(this)
    val deviceName = mobileDeckDeviceName()
    val screen = currentDeckBundleScreen()
    val root = JSONObject()
        .put("format", BUNDLE_FORMAT)
        .put("version", BUNDLE_VERSION)
        .put("deviceId", deviceId)
        .put("deviceName", deviceName)
        .put(
            "metadata",
            JSONObject()
                .put("deviceId", deviceId)
                .put("deviceName", deviceName)
                .put("screen", encodeBundleScreen(screen))
        )
        .put("exportedAt", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US).format(Date()))
        .put("settings", encodeBundleSettings(snapshot, portableBackground, screen))
        .put("pages", pagesArray)
        .put("consoleLayouts", consoleLayoutsRoot)
        .put("assets", assets)
    return root.toString(2)
}

fun Context.importDeckBundleJson(raw: String): ImportedDeckBundle {
    val root = JSONObject(raw)
    require(root.optString("format") == BUNDLE_FORMAT) { "Unsupported MobileDeck bundle." }
    val assetUriByRef = importBundleAssets(root.optJSONArray("assets") ?: JSONArray())
    fun resolveAssetRef(value: String): String {
        if (!value.startsWith(ASSET_URI_PREFIX)) return value
        return assetUriByRef[value.removePrefix(ASSET_URI_PREFIX)] ?: ""
    }

    val settings = root.getJSONObject("settings")
    val pagesArray = root.getJSONArray("pages")
    val pages = List(pagesArray.length()) { pageIndex ->
        val pageObject = pagesArray.getJSONObject(pageIndex)
        val buttonsArray = pageObject.optJSONArray("buttons") ?: JSONArray()
        fun decodePortableButtons(array: JSONArray): List<DeckButton> {
            return List(array.length()) { buttonIndex ->
                val buttonObject = array.getJSONObject(buttonIndex)
                val imported = decodeDeckButton(buttonObject, buttonIndex)
                imported.copy(
                    iconImageUri = resolveAssetRef(imported.iconImageUri),
                    appWidgetId = INVALID_APP_WIDGET_ID
                )
            }
        }
        val buttons = decodePortableButtons(buttonsArray)
        val classicButtons = decodePortableButtons(pageObject.optJSONArray("classicButtons") ?: buttonsArray)
        val consoleButtons = decodePortableButtons(pageObject.optJSONArray("consoleButtons") ?: buttonsArray)
        DeckPageConfig(
            id = pageObject.getInt("id"),
            name = pageObject.optString("name", "Page ${pageIndex + 1}"),
            buttons = buttons,
            classicButtons = classicButtons,
            consoleButtons = consoleButtons
        )
    }

    val layoutsObject = root.optJSONObject("consoleLayouts") ?: JSONObject()
    val consoleLayouts = buildMap {
        val keys = layoutsObject.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            val pageId = key.toIntOrNull() ?: continue
            put(pageId, decodeConsoleLayout(layoutsObject.getJSONObject(key)))
        }
    }

    val classicBackground = decodeBundleClassicBackground(settings.optJSONObject("classicDeckBackground"))
        .let { background ->
            if (background.type == ClassicDeckBackgroundType.Image) {
                background.copy(imageUri = resolveAssetRef(background.imageUri))
            } else {
                background
            }
        }

    return ImportedDeckBundle(
        pages = pages,
        consoleLayouts = consoleLayouts,
        columns = settings.optInt("columns", DEFAULT_COLUMNS).coerceIn(MIN_COLUMNS, MAX_COLUMNS),
        rows = settings.optInt("rows", DEFAULT_ROWS).coerceIn(MIN_ROWS, MAX_ROWS),
        spacing = settings.optInt("spacing", DEFAULT_SPACING_DP).coerceIn(MIN_SPACING_DP, MAX_SPACING_DP),
        pageSwipeAxis = enumValue(settings.optString("pageSwipeAxis"), PageSwipeAxis.Horizontal),
        pageSwipeMode = enumValue(settings.optString("pageSwipeMode"), PageSwipeMode.SingleTouch),
        pageSwipeAnimation = settings.optBoolean("pageSwipeAnimation", true),
        infinitePageSwipe = settings.optBoolean("infinitePageSwipe", true),
        buttonVibrationLevel = enumValue(settings.optString("buttonVibrationLevel"), ButtonVibrationLevel.Strong),
        classicSolidButtonBackground = settings.optBoolean("classicSolidButtonBackground", true),
        classicDeckBackground = classicBackground,
        deckUiMode = enumValue(settings.optString("deckUiMode"), DeckUiMode.Classic),
        classicFontSize = enumValue(settings.optString("classicFontSize"), DeckFontSizeOption.System),
        consoleFontSize = enumValue(settings.optString("consoleFontSize"), DeckFontSizeOption.System),
        consolePanelOptions = decodeBundleConsolePanelOptions(settings.optJSONObject("consolePanelOptions"))
    )
}

private fun encodeBundleSettings(
    snapshot: DeckBundleSnapshot,
    background: ClassicDeckBackground,
    screen: DeckBundleScreen
): JSONObject {
    return JSONObject()
        .put("columns", snapshot.columns)
        .put("rows", snapshot.rows)
        .put("spacing", snapshot.spacing)
        .put("screenWidth", screen.widthPx)
        .put("screenHeight", screen.heightPx)
        .put("deviceAspectRatio", screen.aspectRatio)
        .put("pageSwipeAxis", snapshot.pageSwipeAxis.name)
        .put("pageSwipeMode", snapshot.pageSwipeMode.name)
        .put("pageSwipeAnimation", snapshot.pageSwipeAnimation)
        .put("infinitePageSwipe", snapshot.infinitePageSwipe)
        .put("buttonVibrationLevel", snapshot.buttonVibrationLevel.name)
        .put("classicSolidButtonBackground", snapshot.classicSolidButtonBackground)
        .put("classicDeckBackground", encodeBundleClassicBackground(background))
        .put("deckUiMode", snapshot.deckUiMode.name)
        .put("classicFontSize", snapshot.classicFontSize.name)
        .put("consoleFontSize", snapshot.consoleFontSize.name)
        .put("consolePanelOptions", encodeBundleConsolePanelOptions(snapshot.consolePanelOptions))
}

private fun Context.currentDeckBundleScreen(): DeckBundleScreen {
    val activityScreen = findActivity()?.currentVisibleScreen()
    val fallbackMetrics = resources.displayMetrics
    val width = activityScreen?.widthPx ?: fallbackMetrics.widthPixels
    val height = activityScreen?.heightPx ?: fallbackMetrics.heightPixels
    return normalizedLandscapeScreen(width, height)
}

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}

private fun Activity.currentVisibleScreen(): DeckBundleScreen? {
    val rect = Rect()
    window?.decorView?.getWindowVisibleDisplayFrame(rect)
    return if (rect.width() > 0 && rect.height() > 0) {
        DeckBundleScreen(rect.width(), rect.height())
    } else {
        null
    }
}

private fun normalizedLandscapeScreen(widthPx: Int, heightPx: Int): DeckBundleScreen {
    val safeWidth = widthPx.coerceAtLeast(1)
    val safeHeight = heightPx.coerceAtLeast(1)
    return DeckBundleScreen(
        widthPx = maxOf(safeWidth, safeHeight),
        heightPx = minOf(safeWidth, safeHeight)
    )
}

private fun encodeBundleScreen(screen: DeckBundleScreen): JSONObject {
    return JSONObject()
        .put("widthPx", screen.widthPx)
        .put("heightPx", screen.heightPx)
        .put("aspectRatio", screen.aspectRatio)
}

private fun encodeBundleClassicBackground(background: ClassicDeckBackground): JSONObject {
    return JSONObject()
        .put("type", background.type.name)
        .put("color", background.color.toArgb())
        .put("imageUri", background.imageUri)
}

private fun decodeBundleClassicBackground(item: JSONObject?): ClassicDeckBackground {
    if (item == null) return ClassicDeckBackground()
    return ClassicDeckBackground(
        type = enumValue(item.optString("type"), ClassicDeckBackgroundType.Default),
        color = androidx.compose.ui.graphics.Color(item.optInt("color", ClassicDeckBackground().color.toArgb())),
        imageUri = item.optString("imageUri")
    )
}

private fun encodeBundleConsolePanelOptions(options: ConsolePanelOptions): JSONObject {
    return JSONObject()
        .put("showConnection", options.showConnection)
        .put("showMessage", options.showMessage)
        .put("showClock", options.showClock)
        .put("showDate", options.showDate)
}

private fun pcAvailabilityForButton(button: DeckButton): JSONObject {
    val reason = when {
        button.appWidgetId != INVALID_APP_WIDGET_ID -> "android_widget"
        button.actionType == DeckActionType.Utility -> "android_utility"
        buttonAppAction(button) == DeckActionType.Settings -> "android_settings"
        buttonAppAction(button) == DeckActionType.BluetoothStatus -> "android_bluetooth"
        buttonAppAction(button) == DeckActionType.PreviousPage -> "android_page_navigation"
        buttonAppAction(button) == DeckActionType.NextPage -> "android_page_navigation"
        button.actionType == DeckActionType.AppCommand -> "android_app_command"
        else -> null
    }
    return JSONObject()
        .put("available", reason == null)
        .put("reason", reason ?: "")
}

private fun decodeBundleConsolePanelOptions(item: JSONObject?): ConsolePanelOptions {
    if (item == null) return ConsolePanelOptions()
    return ConsolePanelOptions(
        showConnection = item.optBoolean("showConnection", true),
        showMessage = item.optBoolean("showMessage", true),
        showClock = item.optBoolean("showClock", true),
        showDate = item.optBoolean("showDate", true)
    )
}

private inline fun <reified T : Enum<T>> enumValue(value: String, fallback: T): T {
    return runCatching { enumValueOf<T>(value) }.getOrDefault(fallback)
}

private fun Context.readBundleAssetBytes(uriString: String): ByteArray? {
    if (uriString.isBlank()) return null
    return if (uriString.startsWith(APP_ICON_URI_PREFIX)) {
        val packageName = uriString.removePrefix(APP_ICON_URI_PREFIX)
        runCatching {
            val bitmap = packageManager.getApplicationIcon(packageName).toBitmap()
            ByteArrayOutputStream().use { output ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                output.toByteArray()
            }
        }.getOrNull()
    } else {
        readUriAssetBytes(uriString) ?: readFileAssetBytes(uriString)
    }
}

private fun Context.bundleAssetMimeType(uriString: String): String {
    if (uriString.startsWith(APP_ICON_URI_PREFIX)) return "image/png"
    return runCatching { contentResolver.getType(Uri.parse(uriString)) }.getOrNull()
        ?: when (uriString.substringAfterLast('.', "").lowercase(Locale.US)) {
            "gif" -> "image/gif"
            "jpg", "jpeg" -> "image/jpeg"
            "webp" -> "image/webp"
            "mp4" -> "video/mp4"
            "webm" -> "video/webm"
            else -> "image/png"
        }
}

private fun bundleAssetSourceType(uriString: String): String {
    if (uriString.startsWith(APP_ICON_URI_PREFIX)) return "app-icon"
    return Uri.parse(uriString).scheme?.lowercase(Locale.US)?.takeIf { it.isNotBlank() } ?: "file"
}

private fun Context.readUriAssetBytes(uriString: String): ByteArray? {
    val uri = runCatching { Uri.parse(uriString) }.getOrNull() ?: return null
    return runCatching {
        contentResolver.openInputStream(uri)?.use { it.readBytes() }
    }.getOrNull()
}

private fun readFileAssetBytes(uriString: String): ByteArray? {
    return runCatching {
        val uri = Uri.parse(uriString)
        val candidate = when (uri.scheme?.lowercase(Locale.US)) {
            "file" -> uri.path
            null, "" -> uriString
            else -> null
        } ?: return@runCatching null
        File(candidate).takeIf { it.isFile }?.readBytes()
    }.getOrNull()
}

private fun Context.importBundleAssets(assets: JSONArray): Map<String, String> {
    val targetDir = File(filesDir, "imported_bundle_assets").apply { mkdirs() }
    return buildMap {
        repeat(assets.length()) { index ->
            val item = assets.getJSONObject(index)
            val id = item.getString("id")
            val mimeType = item.optString("mimeType", "image/png")
            val extension = when {
                mimeType.equals("image/gif", ignoreCase = true) -> "gif"
                mimeType.equals("image/jpeg", ignoreCase = true) -> "jpg"
                mimeType.equals("image/webp", ignoreCase = true) -> "webp"
                mimeType.equals("video/mp4", ignoreCase = true) -> "mp4"
                mimeType.equals("video/webm", ignoreCase = true) -> "webm"
                else -> "png"
            }
            val bytes = Base64.decode(item.getString("data"), Base64.DEFAULT)
            val file = File(targetDir, "${System.currentTimeMillis()}_${id}.${extension}")
            file.outputStream().use { it.write(bytes) }
            put(id, Uri.fromFile(file).toString())
        }
    }
}
