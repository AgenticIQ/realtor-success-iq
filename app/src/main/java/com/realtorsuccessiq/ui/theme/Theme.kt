package com.realtorsuccessiq.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ColorPalette.SuccessMinimalPrimary,
    secondary = ColorPalette.SuccessMinimalSecondary,
    tertiary = ColorPalette.SuccessMinimalAccent
)

private val LightColorScheme = lightColorScheme(
    primary = ColorPalette.SuccessMinimalPrimary,
    secondary = ColorPalette.SuccessMinimalSecondary,
    tertiary = ColorPalette.SuccessMinimalAccent,
    background = ColorPalette.SuccessMinimalBackground
)

@Composable
fun RealtorSuccessTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    themePreset: ThemePreset = ThemePresets.SuccessMinimal,
    customPrimary: Color? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> {
            darkColorScheme(
                primary = customPrimary ?: themePreset.primary,
                secondary = themePreset.secondary,
                tertiary = themePreset.accent
            )
        }
        else -> {
            lightColorScheme(
                primary = customPrimary ?: themePreset.primary,
                secondary = themePreset.secondary,
                tertiary = themePreset.accent,
                background = themePreset.background
            )
        }
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

