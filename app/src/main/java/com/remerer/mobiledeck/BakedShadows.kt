package com.remerer.mobiledeck

import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.CornerPathEffect
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.util.LruCache
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.VectorGroup
import androidx.compose.ui.graphics.vector.VectorNode
import androidx.compose.ui.graphics.vector.VectorPath
import androidx.compose.ui.platform.LocalInspectionMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.graphics.asAndroidPath
import kotlin.math.roundToInt

private const val MAX_ICON_SHADOW_CACHE_SIZE = 96
private const val SHADOW_BAKE_VERSION = "v20-compact-icon-shadow"
private const val ICON_VECTOR_ALPHA_DARK = 132
private const val ICON_VECTOR_ALPHA_LIGHT = 72
private const val ICON_BITMAP_ALPHA_DARK = 118
private const val ICON_BITMAP_ALPHA_LIGHT = 58
private const val ICON_SHADOW_BLUR_FACTOR = 0.085f
private const val ICON_SHADOW_SOURCE_SCALE = 1.2f
private const val ICON_SHADOW_STACK_COUNT_DARK = 2
private const val ICON_SHADOW_STACK_COUNT_LIGHT = 1
private const val ICON_SHADOW_MASK_CHECK_ALPHA = 0
private const val ICON_SHADOW_CONTINUOUS_RENDER_FOR_TEST = false
private const val ICON_SHADOW_RENDER_INTERVAL_MS = 300L
private val ICON_SHADOW_TEST_COLOR = AndroidColor.rgb(43, 64, 88)

private val iconShadowCache = LruCache<String, ImageBitmap>(MAX_ICON_SHADOW_CACHE_SIZE)

enum class BakedIconBodyStyle {
    SharpHairline,
    SharpThin,
    RoundThin,
    ThickThin,
    ThickMedium
}

enum class BakedIconTone {
    MatteSlate,
    MattePearl,
    BlueGray
}

@Composable
fun rememberBakedVectorIconShadow(
    key: String,
    imageVector: ImageVector,
    sizePx: Int,
    shadowSizePx: Int,
    enabled: Boolean,
    lightMode: Boolean = false
): ImageBitmap? {
    val safeSize = sizePx.coerceAtLeast(1)
    val safeShadowSize = shadowSizePx.coerceAtLeast(safeSize)
    val cacheKey = "vector:$key:$safeSize:$safeShadowSize:$lightMode:$SHADOW_BAKE_VERSION"
    if (LocalInspectionMode.current) {
        return remember(cacheKey, enabled, lightMode) {
            if (enabled) bakeVectorIconShadow(imageVector, safeSize, safeShadowSize, lightMode) else null
        }
    }
    var bitmap by remember(cacheKey, enabled) {
        mutableStateOf(if (enabled && !ICON_SHADOW_CONTINUOUS_RENDER_FOR_TEST) iconShadowCache[cacheKey] else null)
    }
    LaunchedEffect(cacheKey, enabled) {
        if (!enabled) {
            bitmap = null
            return@LaunchedEffect
        }
        if (ICON_SHADOW_CONTINUOUS_RENDER_FOR_TEST) {
            while (true) {
                bitmap = withContext(Dispatchers.Default) {
                    bakeVectorIconShadow(imageVector, safeSize, safeShadowSize, lightMode)
                }
                delay(ICON_SHADOW_RENDER_INTERVAL_MS)
            }
        }
        bitmap = iconShadowCache[cacheKey] ?: withContext(Dispatchers.Default) {
            bakeVectorIconShadow(imageVector, safeSize, safeShadowSize, lightMode)?.also { iconShadowCache.put(cacheKey, it) }
        }
    }
    return bitmap
}

@Composable
fun rememberBakedBitmapIconShadow(
    key: String,
    imageBitmap: ImageBitmap?,
    sizePx: Int,
    shadowSizePx: Int,
    enabled: Boolean,
    lightMode: Boolean = false
): ImageBitmap? {
    val safeSize = sizePx.coerceAtLeast(1)
    val safeShadowSize = shadowSizePx.coerceAtLeast(safeSize)
    val cacheKey = "bitmap:$key:$safeSize:$safeShadowSize:$lightMode:$SHADOW_BAKE_VERSION"
    if (LocalInspectionMode.current) {
        return remember(cacheKey, imageBitmap, enabled, lightMode) {
            if (enabled && imageBitmap != null) bakeBitmapIconShadow(imageBitmap, safeSize, safeShadowSize, lightMode) else null
        }
    }
    var bitmap by remember(cacheKey, imageBitmap, enabled) {
        mutableStateOf(if (enabled && !ICON_SHADOW_CONTINUOUS_RENDER_FOR_TEST) iconShadowCache[cacheKey] else null)
    }
    LaunchedEffect(cacheKey, imageBitmap, enabled) {
        val source = imageBitmap
        if (!enabled || source == null) {
            bitmap = null
            return@LaunchedEffect
        }
        if (ICON_SHADOW_CONTINUOUS_RENDER_FOR_TEST) {
            while (true) {
                bitmap = withContext(Dispatchers.Default) {
                    bakeBitmapIconShadow(source, safeSize, safeShadowSize, lightMode)
                }
                delay(ICON_SHADOW_RENDER_INTERVAL_MS)
            }
        }
        bitmap = iconShadowCache[cacheKey] ?: withContext(Dispatchers.Default) {
            bakeBitmapIconShadow(source, safeSize, safeShadowSize, lightMode).also { iconShadowCache.put(cacheKey, it) }
        }
    }
    return bitmap
}

@Composable
fun rememberBakedVectorIconBody(
    key: String,
    imageVector: ImageVector,
    sizePx: Int,
    style: BakedIconBodyStyle,
    tone: BakedIconTone,
    enabled: Boolean
): ImageBitmap? {
    val safeSize = sizePx.coerceAtLeast(1)
    val cacheKey = "body:$key:$safeSize:$style:$tone:$SHADOW_BAKE_VERSION"
    if (LocalInspectionMode.current) {
        return remember(cacheKey, enabled, style, tone) {
            if (enabled) bakeVectorIconBody(imageVector, safeSize, style, tone) else null
        }
    }
    var bitmap by remember(cacheKey, enabled) {
        mutableStateOf(if (enabled) iconShadowCache[cacheKey] else null)
    }
    LaunchedEffect(cacheKey, enabled) {
        if (!enabled) {
            bitmap = null
            return@LaunchedEffect
        }
        bitmap = iconShadowCache[cacheKey] ?: withContext(Dispatchers.Default) {
            bakeVectorIconBody(imageVector, safeSize, style, tone).also { iconShadowCache.put(cacheKey, it) }
        }
    }
    return bitmap
}

private fun bakeVectorIconBody(
    imageVector: ImageVector,
    sizePx: Int,
    style: BakedIconBodyStyle,
    tone: BakedIconTone
): ImageBitmap {
    val bodyBitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bodyBitmap)
    val colors = bakedIconToneColors(tone)
    val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.style = Paint.Style.FILL
        shader = LinearGradient(
            0f,
            0f,
            0f,
            sizePx.toFloat(),
            intArrayOf(colors.top, colors.middle, colors.bottom),
            floatArrayOf(0f, 0.56f, 1f),
            Shader.TileMode.CLAMP
        )
    }
    val softFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.style = Paint.Style.FILL
        color = AndroidColor.argb(34, 236, 244, 252)
        pathEffect = when (style) {
            BakedIconBodyStyle.SharpHairline -> null
            BakedIconBodyStyle.SharpThin -> null
            BakedIconBodyStyle.RoundThin -> CornerPathEffect(imageVector.viewportWidth * 0.065f)
            BakedIconBodyStyle.ThickThin,
            BakedIconBodyStyle.ThickMedium -> CornerPathEffect(imageVector.viewportWidth * 0.04f)
        }
    }
    val strokeWidth = when (style) {
        BakedIconBodyStyle.SharpHairline -> imageVector.viewportWidth * 0.018f
        BakedIconBodyStyle.SharpThin -> imageVector.viewportWidth * 0.035f
        BakedIconBodyStyle.RoundThin -> imageVector.viewportWidth * 0.035f
        BakedIconBodyStyle.ThickThin -> imageVector.viewportWidth * 0.055f
        BakedIconBodyStyle.ThickMedium -> imageVector.viewportWidth * 0.09f
    }
    val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.style = Paint.Style.STROKE
        color = colors.stroke
        this.strokeWidth = strokeWidth
        val sharp = style == BakedIconBodyStyle.SharpHairline || style == BakedIconBodyStyle.SharpThin
        strokeJoin = if (sharp) Paint.Join.MITER else Paint.Join.ROUND
        strokeCap = if (sharp) Paint.Cap.SQUARE else Paint.Cap.ROUND
        pathEffect = when (style) {
            BakedIconBodyStyle.SharpHairline -> null
            BakedIconBodyStyle.SharpThin -> null
            BakedIconBodyStyle.RoundThin -> CornerPathEffect(imageVector.viewportWidth * 0.065f)
            BakedIconBodyStyle.ThickThin,
            BakedIconBodyStyle.ThickMedium -> CornerPathEffect(imageVector.viewportWidth * 0.035f)
        }
    }
    val highlightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.style = Paint.Style.STROKE
        color = AndroidColor.argb(118, 246, 250, 255)
        this.strokeWidth = imageVector.viewportWidth * 0.022f
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        pathEffect = CornerPathEffect(imageVector.viewportWidth * 0.035f)
    }
    val glossPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.style = Paint.Style.FILL
        shader = LinearGradient(
            0f,
            0f,
            0f,
            imageVector.viewportHeight * 0.58f,
            intArrayOf(
                AndroidColor.argb(64, 255, 255, 255),
                AndroidColor.argb(18, 255, 255, 255),
                AndroidColor.argb(0, 255, 255, 255)
            ),
            floatArrayOf(0f, 0.52f, 1f),
            Shader.TileMode.CLAMP
        )
    }
    val scale = sizePx / maxOf(imageVector.viewportWidth, imageVector.viewportHeight)
    val left = (sizePx - imageVector.viewportWidth * scale) / 2f
    val top = (sizePx - imageVector.viewportHeight * scale) / 2f
    canvas.save()
    canvas.translate(left, top)
    canvas.scale(scale, scale)
    if (strokeWidth > 0f) {
        drawVectorGroup(
            canvas = canvas,
            group = imageVector.root,
            paint = strokePaint,
            viewportWidth = imageVector.viewportWidth,
            viewportHeight = imageVector.viewportHeight
        )
    }
    drawVectorGroup(
        canvas = canvas,
        group = imageVector.root,
        paint = fillPaint,
        viewportWidth = imageVector.viewportWidth,
        viewportHeight = imageVector.viewportHeight
    )
    drawVectorGroup(
        canvas = canvas,
        group = imageVector.root,
        paint = softFillPaint,
        viewportWidth = imageVector.viewportWidth,
        viewportHeight = imageVector.viewportHeight
    )
    drawVectorGroup(
        canvas = canvas,
        group = imageVector.root,
        paint = glossPaint,
        viewportWidth = imageVector.viewportWidth,
        viewportHeight = imageVector.viewportHeight
    )
    canvas.translate(-imageVector.viewportWidth * 0.012f, -imageVector.viewportHeight * 0.012f)
    drawVectorGroup(
        canvas = canvas,
        group = imageVector.root,
        paint = highlightPaint,
        viewportWidth = imageVector.viewportWidth,
        viewportHeight = imageVector.viewportHeight
    )
    canvas.restore()
    val blurRadius = when (style) {
        BakedIconBodyStyle.SharpHairline -> 0f
        BakedIconBodyStyle.SharpThin -> 0f
        BakedIconBodyStyle.RoundThin -> sizePx * 0.006f
        BakedIconBodyStyle.ThickThin -> sizePx * 0.006f
        BakedIconBodyStyle.ThickMedium -> sizePx * 0.008f
    }
    if (blurRadius <= 0f) {
        return bodyBitmap.asImageBitmap()
    }
    val blurredAlphaPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        maskFilter = BlurMaskFilter(blurRadius, BlurMaskFilter.Blur.NORMAL)
    }
    val offset = IntArray(2)
    val blurredAlpha = bodyBitmap.extractAlpha(blurredAlphaPaint, offset)
    val result = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val resultCanvas = Canvas(result)
    val blurPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.argb(42, 120, 140, 164)
    }
    resultCanvas.drawBitmap(blurredAlpha, offset[0].toFloat(), offset[1].toFloat(), blurPaint)
    resultCanvas.drawBitmap(bodyBitmap, 0f, 0f, Paint(Paint.ANTI_ALIAS_FLAG).apply { alpha = 232 })
    blurredAlpha.recycle()
    bodyBitmap.recycle()
    return result.asImageBitmap()
}

private data class BakedIconToneColors(
    val top: Int,
    val middle: Int,
    val bottom: Int,
    val stroke: Int
)

private fun bakedIconToneColors(tone: BakedIconTone): BakedIconToneColors {
    return when (tone) {
        BakedIconTone.MatteSlate -> BakedIconToneColors(
            top = AndroidColor.rgb(150, 169, 190),
            middle = AndroidColor.rgb(92, 112, 136),
            bottom = AndroidColor.rgb(62, 80, 103),
            stroke = AndroidColor.rgb(72, 92, 116)
        )
        BakedIconTone.MattePearl -> BakedIconToneColors(
            top = AndroidColor.rgb(246, 250, 255),
            middle = AndroidColor.rgb(190, 204, 220),
            bottom = AndroidColor.rgb(138, 158, 180),
            stroke = AndroidColor.rgb(142, 160, 184)
        )
        BakedIconTone.BlueGray -> BakedIconToneColors(
            top = AndroidColor.rgb(156, 181, 204),
            middle = AndroidColor.rgb(94, 120, 148),
            bottom = AndroidColor.rgb(58, 80, 108),
            stroke = AndroidColor.rgb(68, 92, 120)
        )
    }
}

private fun bakeVectorIconShadow(imageVector: ImageVector, sizePx: Int, canvasSizePx: Int, lightMode: Boolean): ImageBitmap? {
    val mask = Bitmap.createBitmap(canvasSizePx, canvasSizePx, Bitmap.Config.ARGB_8888)
    val maskCanvas = Canvas(mask)
    val maskPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.WHITE
        style = Paint.Style.FILL
    }
    val blurPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        maskFilter = BlurMaskFilter(sizePx * ICON_SHADOW_BLUR_FACTOR, BlurMaskFilter.Blur.NORMAL)
    }
    val maskCheckPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ICON_SHADOW_TEST_COLOR.withAlpha(ICON_SHADOW_MASK_CHECK_ALPHA)
        style = Paint.Style.FILL
    }
    val drawSize = iconShadowSourceSize(sizePx, canvasSizePx)
    val inset = (canvasSizePx - drawSize) / 2f
    maskCanvas.save()
    maskCanvas.translate(inset, inset)
    maskCanvas.scale(drawSize / imageVector.viewportWidth, drawSize / imageVector.viewportHeight)
    drawVectorGroup(
        canvas = maskCanvas,
        group = imageVector.root,
        paint = maskPaint,
        viewportWidth = imageVector.viewportWidth,
        viewportHeight = imageVector.viewportHeight
    )
    maskCanvas.restore()
    val offset = IntArray(2)
    val blurredMask = mask.extractAlpha(blurPaint, offset)
    val bitmap = Bitmap.createBitmap(canvasSizePx, canvasSizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ICON_SHADOW_TEST_COLOR.withAlpha(if (lightMode) ICON_VECTOR_ALPHA_LIGHT else ICON_VECTOR_ALPHA_DARK)
    }
    repeat(if (lightMode) ICON_SHADOW_STACK_COUNT_LIGHT else ICON_SHADOW_STACK_COUNT_DARK) {
        canvas.drawBitmap(blurredMask, offset[0].toFloat(), offset[1].toFloat(), shadowPaint)
    }
    blurredMask.recycle()
    mask.recycle()
    if (ICON_SHADOW_MASK_CHECK_ALPHA > 0) {
        canvas.save()
        canvas.translate(inset, inset)
        canvas.scale(drawSize / imageVector.viewportWidth, drawSize / imageVector.viewportHeight)
        drawVectorGroup(
            canvas = canvas,
            group = imageVector.root,
            paint = maskCheckPaint,
            viewportWidth = imageVector.viewportWidth,
            viewportHeight = imageVector.viewportHeight
        )
        canvas.restore()
    }
    return bitmap.asImageBitmap()
}

private fun iconShadowSourceSize(sizePx: Int, canvasSizePx: Int): Float {
    val preferred = sizePx * ICON_SHADOW_SOURCE_SCALE
    return preferred.coerceAtMost(canvasSizePx.toFloat()).coerceAtLeast(sizePx.toFloat())
}

private fun drawVectorGroup(
    canvas: Canvas,
    group: VectorGroup,
    paint: Paint,
    viewportWidth: Float,
    viewportHeight: Float
) {
    canvas.save()
    canvas.translate(group.translationX, group.translationY)
    if (group.rotation != 0f || group.scaleX != 1f || group.scaleY != 1f) {
        val matrix = Matrix().apply {
            postTranslate(-group.pivotX, -group.pivotY)
            postScale(group.scaleX, group.scaleY)
            postRotate(group.rotation)
            postTranslate(group.pivotX, group.pivotY)
        }
        canvas.concat(matrix)
    }
    for (node: VectorNode in group) {
        when (node) {
            is VectorGroup -> drawVectorGroup(canvas, node, paint, viewportWidth, viewportHeight)
            is VectorPath -> {
                val hasFill = node.fill.isVisibleBrush(node.fillAlpha)
                val hasStroke = node.stroke.isVisibleBrush(node.strokeAlpha) && node.strokeLineWidth > 0f
                if (hasFill || hasStroke) {
                    val path = PathParser()
                        .addPathNodes(node.pathData)
                        .toPath()
                        .asAndroidPath()
                    if (!pathFillsViewport(path, viewportWidth, viewportHeight)) {
                        canvas.drawPath(path, paint)
                    }
                }
            }
        }
    }
    canvas.restore()
}

private fun androidx.compose.ui.graphics.Brush?.isVisibleBrush(alpha: Float): Boolean {
    if (this == null || alpha <= 0f) return false
    val solidColor = this as? SolidColor
    return solidColor == null || solidColor.value.alpha > 0f
}

private fun pathFillsViewport(path: android.graphics.Path, viewportWidth: Float, viewportHeight: Float): Boolean {
    val bounds = RectF()
    path.computeBounds(bounds, true)
    val tolerance = 0.01f
    return bounds.left <= tolerance &&
        bounds.top <= tolerance &&
        bounds.right >= viewportWidth - tolerance &&
        bounds.bottom >= viewportHeight - tolerance
}

private fun bakeBitmapIconShadow(imageBitmap: ImageBitmap, sizePx: Int, canvasSizePx: Int, lightMode: Boolean): ImageBitmap {
    val source = imageBitmap.asAndroidBitmap()
    val innerSize = iconShadowSourceSize(sizePx, canvasSizePx).roundToInt().coerceAtLeast(1)
    val scaled = Bitmap.createScaledBitmap(source, innerSize, innerSize, true)
    val blurPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        maskFilter = BlurMaskFilter(sizePx * ICON_SHADOW_BLUR_FACTOR, BlurMaskFilter.Blur.NORMAL)
    }
    val offset = IntArray(2)
    val alpha = scaled.extractAlpha(blurPaint, offset)
    val bitmap = Bitmap.createBitmap(canvasSizePx, canvasSizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    val left = (canvasSizePx - innerSize) / 2f + offset[0]
    val top = (canvasSizePx - innerSize) / 2f + offset[1]
    val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ICON_SHADOW_TEST_COLOR.withAlpha(if (lightMode) ICON_BITMAP_ALPHA_LIGHT else ICON_BITMAP_ALPHA_DARK)
    }
    repeat(if (lightMode) ICON_SHADOW_STACK_COUNT_LIGHT else ICON_SHADOW_STACK_COUNT_DARK) {
        canvas.drawBitmap(alpha, left, top, shadowPaint)
    }
    if (scaled !== source) scaled.recycle()
    alpha.recycle()
    return bitmap.asImageBitmap()
}

private fun Int.withAlpha(alpha: Int): Int {
    return (alpha.coerceIn(0, 255) shl 24) or (this and 0x00FFFFFF)
}
