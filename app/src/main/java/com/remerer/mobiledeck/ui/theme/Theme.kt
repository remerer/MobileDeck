package com.remerer.mobiledeck.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class MobileDeckThemeStyle {
    Classic,
    Console
}

private val ClassicDarkColorScheme = darkColorScheme(
    primary = Color(0xFF8CC7FF),
    onPrimary = Color(0xFF002E52),
    primaryContainer = Color(0xFF0B4A82),
    onPrimaryContainer = Color(0xFFD3E8FF),
    secondary = Color(0xFFC5C7DD),
    tertiary = Color(0xFFE7B7D1),
    background = Color(0xFF101418),
    onBackground = Color(0xFFE1E3E6),
    surface = Color(0xFF151A20),
    onSurface = Color(0xFFE1E3E6),
    surfaceVariant = Color(0xFF26313B),
    onSurfaceVariant = Color(0xFFC0CAD4),
    outline = Color(0xFF8A949E),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val ClassicLightColorScheme = lightColorScheme(
    primary = Color(0xFF0B63C7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4FF),
    onPrimaryContainer = Color(0xFF001B3F),
    secondary = Color(0xFF566170),
    tertiary = Color(0xFF875278),
    background = Color(0xFFF7F9FC),
    onBackground = Color(0xFF171C22),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF171C22),
    surfaceVariant = Color(0xFFE1E7EF),
    onSurfaceVariant = Color(0xFF404852),
    outline = Color(0xFF707984),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

private val ConsoleDarkColorScheme = darkColorScheme(
    primary = Color(0xFF3FD2FF),
    onPrimary = Color(0xFF002B3A),
    primaryContainer = Color(0xFF00506F),
    onPrimaryContainer = Color(0xFFC3EEFF),
    secondary = Color(0xFFB7C9D6),
    tertiary = Color(0xFF87D5E7),
    background = Color(0xFF050A10),
    onBackground = Color(0xFFE1E7EE),
    surface = Color(0xFF0C151D),
    onSurface = Color(0xFFE1E7EE),
    surfaceVariant = Color(0xFF172531),
    onSurfaceVariant = Color(0xFFB9C8D4),
    outline = Color(0xFF82919C),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

private val ConsoleLightColorScheme = lightColorScheme(
    primary = Color(0xFF0076A8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC4E7FF),
    onPrimaryContainer = Color(0xFF001E2D),
    secondary = Color(0xFF50606C),
    tertiary = Color(0xFF1C6978),
    background = Color(0xFFF2F8FC),
    onBackground = Color(0xFF131C22),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF131C22),
    surfaceVariant = Color(0xFFDCE9F1),
    onSurfaceVariant = Color(0xFF3C4A53),
    outline = Color(0xFF6C7A84),
    error = Color(0xFFBA1A1A),
    onError = Color.White
)

@Composable
fun MobileDeckTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    style: MobileDeckThemeStyle = MobileDeckThemeStyle.Classic,
    fontSizeScale: Float = 1f,
    content: @Composable () -> Unit
) {
    val colorScheme = when (style) {
        MobileDeckThemeStyle.Classic -> if (darkTheme) ClassicDarkColorScheme else ClassicLightColorScheme
        MobileDeckThemeStyle.Console -> if (darkTheme) ConsoleDarkColorScheme else ConsoleLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography.scaled(fontSizeScale),
        content = content
    )
}
