package com.namma.santhe.ledger.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Santhe Green Palette
val SantheGreen = Color(0xFF2E7D32)
val SantheGreenLight = Color(0xFF4CAF50)
val SantheGreenDark = Color(0xFF1B5E20)
val SantheAmber = Color(0xFFFF8F00)
val SantheAmberLight = Color(0xFFFFB300)
val SantheRed = Color(0xFFD32F2F)
val SantheBackground = Color(0xFFF5F5F5)
val SantheSurface = Color(0xFFFFFFFF)
val SantheOnSurface = Color(0xFF212121)

private val LightColorScheme = lightColorScheme(
    primary = SantheGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA5D6A7),
    onPrimaryContainer = SantheGreenDark,
    secondary = SantheAmber,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFFE082),
    onSecondaryContainer = Color(0xFF4E3B00),
    error = SantheRed,
    onError = Color.White,
    background = SantheBackground,
    onBackground = SantheOnSurface,
    surface = SantheSurface,
    onSurface = SantheOnSurface,
    surfaceVariant = Color(0xFFE8F5E9),
    outline = Color(0xFF9E9E9E)
)

private val DarkColorScheme = darkColorScheme(
    primary = SantheGreenLight,
    onPrimary = Color.Black,
    primaryContainer = SantheGreenDark,
    onPrimaryContainer = Color(0xFFA5D6A7),
    secondary = SantheAmberLight,
    onSecondary = Color.Black,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0)
)

@Composable
fun NammaSantheTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}