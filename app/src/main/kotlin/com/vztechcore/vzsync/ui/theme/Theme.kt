package com.vztechcore.vzsync.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.vztechcore.vzsync.domain.ThemeMode

// AMOLED: pure black bg, dark surface, standard surfaceVariant
private val AmoledColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onPrimary = DarkOnBackground,
    onSecondary = DarkOnBackground,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = DarkError,
)

// Standard dark: dark navy bg, slightly higher surface to keep cards readable
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    background = DarkSurface,
    surface = DarkSurfaceVariant,
    surfaceVariant = DarkSurfaceVariantHigh,
    onPrimary = DarkOnBackground,
    onSecondary = DarkOnBackground,
    onBackground = DarkOnBackground,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = DarkError,
)

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    secondary = LightSecondary,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = LightSurfaceVariant,
    onPrimary = LightOnBackground,
    onSecondary = LightOnBackground,
    onBackground = LightOnBackground,
    onSurface = LightOnSurface,
    onSurfaceVariant = LightOnSurfaceVariant,
    error = LightError,
)

@Composable
fun VzSyncTheme(
    themeMode: ThemeMode = ThemeMode.AMOLED,
    content: @Composable () -> Unit,
) {
    val isDarkTheme = when (themeMode) {
        ThemeMode.AMOLED, ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when (themeMode) {
        ThemeMode.AMOLED -> AmoledColorScheme
        ThemeMode.DARK -> DarkColorScheme
        ThemeMode.LIGHT -> LightColorScheme
        ThemeMode.SYSTEM ->
            if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = VzTypography,
        content = content,
    )
}