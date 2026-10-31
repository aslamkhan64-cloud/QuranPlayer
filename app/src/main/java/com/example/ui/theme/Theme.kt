package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CelestialBlue,
    onPrimary = SpaceBlack,
    primaryContainer = SpaceMidnight,
    onPrimaryContainer = TextPrimary,
    secondary = GoldAccent,
    onSecondary = SpaceBlack,
    secondaryContainer = GoldWarm.copy(alpha = 0.2f),
    onSecondaryContainer = GoldLight,
    tertiary = CelestialCyan,
    background = SpaceBlack,
    onBackground = TextPrimary,
    surface = SpaceSurface,
    onSurface = TextPrimary,
    surfaceVariant = SpaceCardBg,
    onSurfaceVariant = TextSecondary,
    outline = SpaceCardBorder,
    outlineVariant = SpaceCardBorder.copy(alpha = 0.5f)
)

@Composable
fun QuranPlayerTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = SpaceBlack.toArgb()
            window.navigationBarColor = SpaceBlack.toArgb()
            val windowInsetsController = WindowCompat.getInsetsController(window, view)
            windowInsetsController.isAppearanceLightStatusBars = false
            windowInsetsController.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
