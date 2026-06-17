package com.example.tekakata.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Warna-warna cerah dan ceria untuk anak-anak
val PrimaryColor = Color(0xFF6C63FF)       // Ungu cerah (playful)
val SecondaryColor = Color(0xFFFF6B6B)      // Merah coral
val AccentGreen = Color(0xFF51CF66)         // Hijau segar
val AccentOrange = Color(0xFFFF922B)        // Oranye ceria
val AccentYellow = Color(0xFFFFD43B)        // Kuning cerah
val AccentCyan = Color(0xFF22B8CF)          // Cyan
val AccentPink = Color(0xFFF06595)          // Pink
val BackgroundColor = Color(0xFFF8F9FA)     // Abu-abu sangat muda

// Warna sel grid
val FoundCellColor = Color(0xFF51CF66)      // Hijau
val SelectedCellColor = Color(0xFF748FFC)   // Biru lembut
val HintCellColor = Color(0xFFFFD43B)       // Kuning cerah
val CellDefaultColor = Color(0xFFFFFFFF)
val CellBorderColor = Color(0xFFDEE2E6)

// Warna gradient untuk layar
val MainGradientColors = listOf(
    Color(0xFF6C63FF),
    Color(0xFF748FFC),
    Color(0xFFA5D8FF),
    Color(0xFFE7F5FF)
)

val GameGradientColors = listOf(
    Color(0xFFD1C4E9), // ungu lebih pekat
    Color(0xFFB3E5FC), // biru muda cerah
    Color(0xFFC8E6C9), // hijau mint
    Color(0xFFFFF9C4), // kuning pastel cerah
    Color(0xFFFFCCBC)  // peach/oranye lembut
)

// Gradient untuk header TopBar
val HeaderGradientColors = listOf(
    Color(0xFF1565C0), // biru tua
    Color(0xFF1E88E5), // biru medium
    Color(0xFF42A5F5)  // biru cerah
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
