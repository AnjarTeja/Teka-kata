package com.example.tekakata.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tekakata.data.LevelData
import com.example.tekakata.utils.PreferencesManager

@Composable
fun LevelSelectScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLevel: (Int) -> Unit
) {
    val context = LocalContext.current
    val prefsManager = PreferencesManager(context)
    val highestLevel = prefsManager.getHighestLevel()
    val totalLevels = LevelData.levels.size

    val cardColors = listOf(
        listOf(Color(0xFFFF6B6B), Color(0xFFFF8E8E)),
        listOf(Color(0xFF4ECDC4), Color(0xFF6EE7DE)),
        listOf(Color(0xFF45B7D1), Color(0xFF67C8DB)),
        listOf(Color(0xFF96CEB4), Color(0xFFB0DEC4)),
        listOf(Color(0xFFFFD93D), Color(0xFFFFE66D)),
        listOf(Color(0xFF6C63FF), Color(0xFF8B83FF)),
        listOf(Color(0xFFFF922B), Color(0xFFFFAD5C)),
        listOf(Color(0xFFF06595), Color(0xFFFF85AB)),
    )

    val levelEmojis = listOf(
        "\uD83D\uDC31", "\uD83C\uDF4E", "\uD83C\uDFA8", "\u2744\uFE0F",
        "\uD83D\uDC1F", "\uD83D\uDE97", "\uD83D\uDC68\u200D\u2695\uFE0F",
        "\uD83C\uDFE0", "\uD83C\uDFD4\uFE0F", "\uD83C\uDF1F", "\uD83C\uDF5C",
        "\u26BD", "\uD83E\uDD85", "\uD83D\uDCDA", "\uD83D\uDE80"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = MainGradientColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
        ) {
            // TopBar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PrimaryColor)
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "\uD83D\uDDFA\uFE0F Pilih Level",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // Visual Progress Bar — jalan berbintang
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(12.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "\uD83C\uDF1F", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Progress: $highestLevel / $totalLevels",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // Jalan bintang
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..totalLevels) {
                            val isUnlocked = i <= highestLevel
                            val stars = prefsManager.getStarsForLevel(i)
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(20.dp)
                            ) {
                                Text(
                                    text = if (isUnlocked) "\u2B50" else "\u2606",
                                    fontSize = if (stars > 0) 16.sp else 14.sp,
                                    color = if (isUnlocked) Color(0xFFFFD43B) else Color.White.copy(alpha = 0.3f)
                                )
                            }
                            if (i < totalLevels) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(
                                            if (i < highestLevel) AccentGreen
                                            else Color.White.copy(alpha = 0.2f)
                                        )
                                )
                            }
                        }
                    }
                }
            }

            // Grid level
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LevelData.levels.forEachIndexed { index, level ->
                    val isUnlocked = level.id <= highestLevel
                    val colorPair = cardColors[index % cardColors.size]
                    val emoji = if (index < levelEmojis.size) levelEmojis[index] else "\u2B50"
                    val stars = prefsManager.getStarsForLevel(level.id)

                    Card(
                        onClick = {
                            if (isUnlocked) onNavigateToLevel(level.id)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(72.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isUnlocked) 6.dp else 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(colors = colorPair),
                                    RoundedCornerShape(18.dp)
                                )
                                .then(
                                    if (!isUnlocked) {
                                        Modifier.background(
                                            Color.Black.copy(alpha = 0.35f),
                                            RoundedCornerShape(18.dp)
                                        )
                                    } else Modifier
                                )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            if (isUnlocked) Color.White.copy(alpha = 0.3f)
                                            else Color.White.copy(alpha = 0.15f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isUnlocked) {
                                        Text(text = emoji, fontSize = 24.sp)
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Lock,
                                            contentDescription = "Terkunci",
                                            tint = Color.White.copy(alpha = 0.7f),
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.size(12.dp))

                                Column {
                                    Text(
                                        text = "Level ${level.id}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isUnlocked) Color.White else Color.White.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = level.tema,
                                        fontSize = 13.sp,
                                        color = if (isUnlocked) Color.White.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.6f)
                                    )
                                    if (isUnlocked && stars > 0) {
                                        Text(
                                            text = "\u2B50".repeat(stars),
                                            fontSize = 11.sp,
                                            color = Color(0xFFFFD43B)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                if (isUnlocked) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color.White.copy(alpha = 0.25f))
                                            .padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = "${level.kataKunci.size} kata",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
