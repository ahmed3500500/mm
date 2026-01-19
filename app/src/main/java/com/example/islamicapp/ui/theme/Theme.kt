package com.example.islamicapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val DarkColorScheme = darkColorScheme(
    primary = IslamicGold,
    secondary = EmeraldGreen,
    background = DarkGreen,
    surface = SurfaceGreen,
    onPrimary = DarkGreen,
    onSecondary = IslamicGold,
    onBackground = IslamicGold,
    onSurface = IslamicGold
)

private val LightColorScheme = lightColorScheme(
    primary = IslamicGold,
    secondary = EmeraldGreen,
    background = DarkGreen,
    surface = SurfaceGreen,
    onPrimary = DarkGreen,
    onSecondary = IslamicGold,
    onBackground = IslamicGold,
    onSurface = IslamicGold
)

@Composable
fun IslamicAppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColorScheme else LightColorScheme
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = colors,
            typography = Typography,
            content = content
        )
    }
}
