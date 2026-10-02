package com.scorpions.agriscan.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Tumhare Color.kt se colors use kar rahe hain
private val DarkColorScheme = darkColorScheme(
    primary = Amber500,              // Gold
    onPrimary = Charcoal900,         // Dark text on gold
    secondary = AgriGreen700,        // Emerald green
    onSecondary = Cream50,           // Cream text
    background = Color(0xFF0A2A1A),  // Deep forest green
    onBackground = Cream50,          // Cream text
    surface = AgriGreen900,          // Dark green surface
    onSurface = Cream50,             // Cream text
    error = Red600,
    onError = Cream50,
    tertiary = Emerald600,
    onTertiary = Cream50
)

@Composable
fun AgriScanTheme(
    darkTheme: Boolean = true,   // Force dark theme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AgriScanTypography,
        content = content
    )
}