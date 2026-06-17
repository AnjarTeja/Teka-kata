package com.example.tekakata.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val PrimaryColor = Color(0xFF6C63FF)
val SecondaryColor = Color(0xFFFF6B6B)
val AccentGreen = Color(0xFF51CF66)
val AccentOrange = Color(0xFFFF922B)
val AccentYellow = Color(0xFFFFD43B)
val AccentCyan = Color(0xFF22B8CF)
val AccentPink = Color(0xFFF06595)
val BackgroundColor = Color(0xFFF8F9FA)

val FoundCellColor = Color(0xFF51CF66)
val SelectedCellColor = Color(0xFF748FFC)
val HintCellColor = Color(0xFFFFD43B)
val CellDefaultColor = Color(0xFFFFFFFF)
val CellBorderColor = Color(0xFFDEE2E6)

val StarColor = Color(0xFFFFD43B)
val StarEmptyColor = Color(0xFFE0E0E0)
val ConfettiColors = listOf(
    Color(0xFFFF6B6B), Color(0xFF4ECDC4), Color(0xFFFFD93D),
    Color(0xFF6C63FF), Color(0xFFFF922B), Color(0xFF51CF66),
    Color(0xFFF06595), Color(0xFF45B7D1)
)

val MainGradientColors = listOf(
    Color(0xFF6C63FF),
    Color(0xFF748FFC),
    Color(0xFFA5D8FF),
    Color(0xFFE7F5FF)
)

val GameGradientColors = listOf(
    Color(0xFFD1C4E9),
    Color(0xFFB3E5FC),
    Color(0xFFC8E6C9),
    Color(0xFFFFF9C4),
    Color(0xFFFFCCBC)
)

val HeaderGradientColors = listOf(
    Color(0xFF1565C0),
    Color(0xFF1E88E5),
    Color(0xFF42A5F5)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    secondary = SecondaryColor,
    background = BackgroundColor,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF212529),
    onSurface = Color(0xFF212529)
)

@Composable
fun TekaKataTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(),
        content = content
    )
}
