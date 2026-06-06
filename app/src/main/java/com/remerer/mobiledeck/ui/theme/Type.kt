package com.remerer.mobiledeck.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

fun Typography.scaled(scale: Float): Typography {
    val safeScale = scale.coerceIn(0.85f, 1.20f)
    if (safeScale == 1f) return this
    return copy(
        displayLarge = displayLarge.scaled(safeScale),
        displayMedium = displayMedium.scaled(safeScale),
        displaySmall = displaySmall.scaled(safeScale),
        headlineLarge = headlineLarge.scaled(safeScale),
        headlineMedium = headlineMedium.scaled(safeScale),
        headlineSmall = headlineSmall.scaled(safeScale),
        titleLarge = titleLarge.scaled(safeScale),
        titleMedium = titleMedium.scaled(safeScale),
        titleSmall = titleSmall.scaled(safeScale),
        bodyLarge = bodyLarge.scaled(safeScale),
        bodyMedium = bodyMedium.scaled(safeScale),
        bodySmall = bodySmall.scaled(safeScale),
        labelLarge = labelLarge.scaled(safeScale),
        labelMedium = labelMedium.scaled(safeScale),
        labelSmall = labelSmall.scaled(safeScale)
    )
}

private fun TextStyle.scaled(scale: Float): TextStyle {
    return copy(
        fontSize = fontSize.scaled(scale),
        lineHeight = lineHeight.scaled(scale)
    )
}

private fun TextUnit.scaled(scale: Float): TextUnit {
    return if (this == TextUnit.Unspecified) this else (value * scale).sp
}
