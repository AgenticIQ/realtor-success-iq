package com.realtorsuccessiq.ui.theme

import androidx.compose.ui.graphics.Color

object ColorPalette {
    // Success Minimal (default)
    val SuccessMinimalPrimary = Color(0xFF16A34A) // Success Green (soft)
    val SuccessMinimalSecondary = Color(0xFF0B1220) // Deep Navy
    val SuccessMinimalAccent = Color(0xFFD4AF37) // Gold
    val SuccessMinimalBackground = Color(0xFFF0FDF4) // Very soft green-tinted background
    
    // Trust Blue
    val TrustBluePrimary = Color(0xFF2563EB)
    val TrustBlueSecondary = Color(0xFF1E3A8A)
    val TrustBlueAccent = Color(0xFF60A5FA)
    val TrustBlueBackground = Color(0xFFF0F9FF)
    
    // Modern Charcoal
    val ModernCharcoalPrimary = Color(0xFF1F2937)
    val ModernCharcoalSecondary = Color(0xFF111827)
    val ModernCharcoalAccent = Color(0xFF6B7280)
    val ModernCharcoalBackground = Color(0xFFF9FAFB)
    
    // Coastal
    val CoastalPrimary = Color(0xFF0D9488) // Teal
    val CoastalSecondary = Color(0xFF0F766E)
    val CoastalAccent = Color(0xFF5EEAD4)
    val CoastalBackground = Color(0xFFF0FDFA)
}

data class ThemePreset(
    val name: String,
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val background: Color
)

object ThemePresets {
    val SuccessMinimal = ThemePreset(
        name = "Success Minimal",
        primary = ColorPalette.SuccessMinimalPrimary,
        secondary = ColorPalette.SuccessMinimalSecondary,
        accent = ColorPalette.SuccessMinimalAccent,
        background = ColorPalette.SuccessMinimalBackground
    )
    
    val TrustBlue = ThemePreset(
        name = "Trust Blue",
        primary = ColorPalette.TrustBluePrimary,
        secondary = ColorPalette.TrustBlueSecondary,
        accent = ColorPalette.TrustBlueAccent,
        background = ColorPalette.TrustBlueBackground
    )
    
    val ModernCharcoal = ThemePreset(
        name = "Modern Charcoal",
        primary = ColorPalette.ModernCharcoalPrimary,
        secondary = ColorPalette.ModernCharcoalSecondary,
        accent = ColorPalette.ModernCharcoalAccent,
        background = ColorPalette.ModernCharcoalBackground
    )
    
    val Coastal = ThemePreset(
        name = "Coastal",
        primary = ColorPalette.CoastalPrimary,
        secondary = ColorPalette.CoastalSecondary,
        accent = ColorPalette.CoastalAccent,
        background = ColorPalette.CoastalBackground
    )
    
    val all = listOf(SuccessMinimal, TrustBlue, ModernCharcoal, Coastal)
}

