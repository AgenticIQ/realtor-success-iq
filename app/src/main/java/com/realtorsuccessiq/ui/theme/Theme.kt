package com.realtorsuccessiq.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF22C55E),
    onPrimary = Color(0xFF052E16),
    primaryContainer = Color(0xFF064E3B),
    onPrimaryContainer = Color(0xFFD1FAE5),

    secondary = Color(0xFF94A3B8),
    onSecondary = Color(0xFF0B1220),

    tertiary = ColorPalette.SuccessMinimalAccent,
    onTertiary = Color(0xFF0B1220),

    background = ColorPalette.SuccessMinimalSecondary,
    onBackground = Color(0xFFE2E8F0),

    surface = Color(0xFF0F172A),
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF111827),
    onSurfaceVariant = Color(0xFFCBD5E1),

    outline = Color(0xFF334155)
)

private val LightColorScheme = lightColorScheme(
    primary = ColorPalette.SuccessMinimalPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF052E16),

    secondary = ColorPalette.SuccessMinimalSecondary,
    onSecondary = Color.White,

    tertiary = ColorPalette.SuccessMinimalAccent,
    onTertiary = Color(0xFF0B1220),

    background = ColorPalette.SuccessMinimalBackground,
    onBackground = ColorPalette.SuccessMinimalSecondary,

    surface = Color.White,
    onSurface = ColorPalette.SuccessMinimalSecondary,
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF0F172A),

    outline = Color(0xFF94A3B8)
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
            DarkColorScheme.copy(primary = customPrimary ?: themePreset.primary)
        }
        else -> {
            LightColorScheme.copy(
                primary = customPrimary ?: themePreset.primary,
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

