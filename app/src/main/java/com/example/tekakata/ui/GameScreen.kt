package com.example.tekakata.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tekakata.utils.PreferencesManager
import kotlin.math.sqrt

// Helper: hitung kolom sel dari posisi X pointer, return null jika tidak dekat pusat sel manapun
private fun getCellCol(
    x: Float, stridePx: Float, cellSizePx: Float, radius: Float, gridSize: Int
): Int? {
    val rawCol = (x / stridePx).toInt().coerceIn(0, gridSize - 1)
    val cellCenterX = rawCol * stridePx + cellSizePx / 2f
    val dx = x - cellCenterX
    return if (dx * dx <= radius * radius) rawCol else null
}

// Helper: hitung baris sel dari posisi Y pointer, return null jika tidak dekat pusat sel manapun
private fun getCellRow(
    y: Float, stridePx: Float, cellSizePx: Float, radius: Float, gridSize: Int
): Int? {
    val rawRow = (y / stridePx).toInt().coerceIn(0, gridSize - 1)
    val cellCenterY = rawRow * stridePx + cellSizePx / 2f
    val dy = y - cellCenterY
    return if (dy * dy <= radius * radius) rawRow else null
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GameScreen(
    levelId: Int,
    viewModel: GameViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToLevel: (Int) -> Unit,
    onNavigateToMain: () -> Unit
) {
    val context = LocalContext.current
    val prefsManager = PreferencesManager(context)

    val grid by viewModel.grid.collectAsState()
    val foundWords by viewModel.foundWords.collectAsState()
    val foundCells by viewModel.foundCells.collectAsState()
    val selectedCells by viewModel.selectedCells.collectAsState()
    val isLevelComplete by viewModel.isLevelComplete.collectAsState()
    val hintIndices by viewModel.hintIndices.collectAsState()
    val currentLevel by viewModel.currentLevel.collectAsState()
    val hintCount by viewModel.hintCount.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }

    LaunchedEffect(levelId) {
        viewModel.initLevel(levelId)
    }

    val density = LocalDensity.current
    val gridSize = if (grid.isNotEmpty()) grid.size else 10

    // Spasi antar sel agar drag miring lebih mudah
    val gapDp = 4

    // Hitung ukuran sel dinamis dari lebar layar — sebesar mungkin, tetap muat
    val screenWidthDp = LocalConfiguration.current.screenWidthDp
    val gridOuterPadding = 20 // padding kiri-kanan total
    val gridInnerPadding = 12 // padding dalam container grid
    val availableDp = screenWidthDp - gridOuterPadding - gridInnerPadding
    val cellSizeDp = ((availableDp - gapDp * (gridSize - 1)) / gridSize).coerceIn(30, 52)
    val cellSizePx = with(density) { cellSizeDp.dp.toPx() }
    val gapPx = with(density) { gapDp.dp.toPx() }
    val stridePx = cellSizePx + gapPx

    // Radius pendaftaran sel — 45% dari ukuran sel
    val registrationRadius = cellSizePx * 0.45f
    // Jarak interpolasi — cek setiap setengah sel agar drag cepat tidak melewatkan sel
    val interpolationStep = stridePx * 0.4f

    // Emoji dekorasi berdasarkan tema level
    val temaEmoji = when {
        currentLevel?.tema?.contains("Hewan Darat") == true -> "\uD83D\uDC31\uD83D\uDC36\uD83D\uDC30\uD83D\uDC18\uD83D\uDC2F"
        currentLevel?.tema?.contains("Buah") == true -> "\uD83C\uDF4E\uD83C\uDF4A\uD83C\uDF47\uD83C\uDF4C\uD83C\uDF49"
        currentLevel?.tema?.contains("Warna") == true -> "\uD83C\uDFA8\uD83C\uDF08\uD83D\uDCA5\u2B50\uD83C\uDF1F"
        currentLevel?.tema?.contains("Dingin") == true -> "\u2744\uFE0F\u2603\uFE0F\uD83E\uDDCA\uD83C\uDF28\uFE0F\uD83E\uDDE3"
        currentLevel?.tema?.contains("Hewan Laut") == true -> "\uD83D\uDC1F\uD83D\uDC20\uD83D\uDC19\uD83E\uDD9E\uD83C\uDF1F"
        currentLevel?.tema?.contains("Kendaraan") == true -> "\uD83D\uDE97\u2708\uFE0F\uD83D\uDEB2\uD83D\uDE82\u26F5"
        currentLevel?.tema?.contains("Profesi") == true -> "\uD83D\uDC68\u200D\u2695\uFE0F\uD83D\uDC68\u200D\uD83C\uDFEB\uD83D\uDC6E\uD83D\uDC68\u200D\uD83C\uDF73\uD83E\uDDD1\u200D\uD83C\uDF3E"
        currentLevel?.tema?.contains("Rumah") == true -> "\uD83C\uDFE0\uD83E\uDE91\uD83D\uDCA1\uD83D\uDEAA\uD83E\uDE9F"
        currentLevel?.tema?.contains("Alam") == true -> "\uD83C\uDFD4\uFE0F\uD83C\uDF33\uD83C\uDF3B\uD83C\uDF27\uFE0F\uD83C\uDF0A"
        currentLevel?.tema?.contains("Campuran") == true -> "\u2B50\uD83D\uDE80\uD83E\uDD81\u2600\uFE0F\uD83C\uDF19\u2604\uFE0F"
        else -> "\uD83C\uDF1F\u2B50\uD83C\uDF89\uD83C\uDF8A\uD83C\uDFC6"
    }
    val cellColorA = listOf(
        Color(0xFFEDE7F6), // ungu muda
        Color(0xFFE8EAF6), // indigo muda
        Color(0xFFE3F2FD), // biru muda
        Color(0xFFE0F7FA), // cyan muda
        Color(0xFFE8F5E9), // hijau muda
        Color(0xFFF1F8E9), // lime muda
        Color(0xFFFFF8E1), // kuning muda
        Color(0xFFFCE4EC), // pink muda
    )
    val cellColorB = listOf(
        Color(0xFFD1C4E9), // ungu
        Color(0xFFC5CAE9), // indigo
        Color(0xFFBBDEFB), // biru
        Color(0xFFB2EBF2), // cyan
        Color(0xFFC8E6C9), // hijau
        Color(0xFFDCEDC8), // lime
        Color(0xFFFFECB3), // kuning
        Color(0xFFF8BBD0), // pink
    )

    val totalWords = currentLevel?.kataKunci?.size ?: 0
    val foundCount = foundWords.size

    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = GameGradientColors))
    ) {
        // TopBar — gradient warna-warni cerah
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(colors = HeaderGradientColors)
                )
                .padding(top = statusBarPadding)
                .padding(horizontal = 8.dp, vertical = 10.dp)
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
                Column(modifier = Modifier.padding(start = 4.dp)) {
                    Text(
                        text = "\uD83C\uDFAE Level $levelId",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Text(
                        text = currentLevel?.tema ?: "",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "\u2B50 $foundCount / $totalWords",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
        }

        // Grid Area — FIXED, centered, dengan daftar kata + grid menyatu
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            // Dekorasi emoji melayang di background — lebih banyak dan bervariasi
            Text(
                text = "\uD83C\uDF1F", fontSize = 40.sp,
                modifier = Modifier.align(Alignment.TopStart).padding(start = 6.dp, top = 2.dp).alpha(0.35f)
            )
            Text(
                text = "\u2B50", fontSize = 24.sp,
                modifier = Modifier.align(Alignment.TopStart).padding(start = 50.dp, top = 20.dp).alpha(0.2f)
            )
            Text(
                text = "\uD83C\uDF08", fontSize = 34.sp,
                modifier = Modifier.align(Alignment.TopEnd).padding(end = 6.dp, top = 2.dp).alpha(0.3f)
            )
            Text(
                text = "\u2601\uFE0F", fontSize = 26.sp,
                modifier = Modifier.align(Alignment.TopEnd).padding(end = 50.dp, top = 14.dp).alpha(0.18f)
            )
            Text(
                text = "\uD83C\uDF89", fontSize = 36.sp,
                modifier = Modifier.align(Alignment.BottomStart).padding(start = 8.dp, bottom = 4.dp).alpha(0.28f)
            )
            Text(
                text = "\uD83C\uDF3A", fontSize = 22.sp,
                modifier = Modifier.align(Alignment.BottomStart).padding(start = 55.dp, bottom = 16.dp).alpha(0.2f)
            )
            Text(
                text = "\uD83C\uDFC6", fontSize = 34.sp,
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 6.dp, bottom = 2.dp).alpha(0.3f)
            )
            Text(
                text = "\uD83C\uDF1F", fontSize = 20.sp,
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 48.dp, bottom = 20.dp).alpha(0.18f)
            )
            // Dekorasi tengah samping
            Text(
                text = "\uD83C\uDF88", fontSize = 28.sp,
                modifier = Modifier.align(Alignment.CenterStart).padding(start = 4.dp).alpha(0.2f)
            )
            Text(
                text = "\uD83C\uDF81", fontSize = 28.sp,
                modifier = Modifier.align(Alignment.CenterEnd).padding(end = 4.dp).alpha(0.2f)
            )

            // Konten utama: daftar kata + grid dalam satu Column ter-center
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // === Daftar Kata — strip banner menempel di atas grid ===
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    PrimaryColor.copy(alpha = 0.9f),
                                    Color(0xFF9C27B0).copy(alpha = 0.85f),
                                    PrimaryColor.copy(alpha = 0.9f)
                                )
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "\uD83D\uDD0E",
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "CARI KATA",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = temaEmoji,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        // Chip kata-kata
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            currentLevel?.kataKunci?.forEach { word ->
                                val isFound = word in foundWords

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isFound)
                                                Brush.linearGradient(listOf(Color(0xFF43A047), Color(0xFF2E7D32)))
                                            else
                                                Brush.linearGradient(listOf(Color.White.copy(alpha = 0.25f), Color.White.copy(alpha = 0.15f)))
                                        )
                                        .then(
                                            if (!isFound) Modifier.background(
                                                Color.White.copy(alpha = 0.05f),
                                                RoundedCornerShape(8.dp)
                                            ) else Modifier
                                        )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (isFound) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFFC8E6C9),
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                        }
                                        Text(
                                            text = word,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isFound) Color.White else Color.White.copy(alpha = 0.95f),
                                            textDecoration = if (isFound) TextDecoration.LineThrough else TextDecoration.None
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // === Grid — menempel langsung di bawah banner daftar kata ===
                if (grid.isNotEmpty()) {
                    val gridPadding = 6
                    val totalGridDp = cellSizeDp * gridSize + gapDp * (gridSize - 1) + gridPadding * 2

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .shadow(16.dp, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                            .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFE8EAF6), Color(0xFFEDE7F6))
                                )
                            )
                            .size(totalGridDp.dp)
                            .padding(gridPadding.dp)
                            .pointerInput(Unit) {
                                awaitPointerEventScope {
                                    var lastX = -1f
                                    var lastY = -1f

                                    while (true) {
                                        val event = awaitPointerEvent()
                                        val pointer = event.changes.firstOrNull() ?: continue

                                        val x = pointer.position.x
                                        val y = pointer.position.y

                                        if (pointer.pressed && !pointer.previousPressed) {
                                            lastX = x
                                            lastY = y
                                            val col = getCellCol(x, stridePx, cellSizePx, registrationRadius, gridSize)
                                            val row = getCellRow(y, stridePx, cellSizePx, registrationRadius, gridSize)
                                            if (col != null && row != null) {
                                                viewModel.startSelection(row, col)
                                            }
                                        } else if (pointer.pressed && pointer.previousPressed) {
                                            val dx = x - lastX
                                            val dy = y - lastY
                                            val dist = sqrt(dx * dx + dy * dy)
                                            val steps = (dist / interpolationStep).toInt().coerceAtLeast(1)

                                            for (i in 1..steps) {
                                                val t = i.toFloat() / steps
                                                val ix = lastX + dx * t
                                                val iy = lastY + dy * t
                                                val col = getCellCol(ix, stridePx, cellSizePx, registrationRadius, gridSize)
                                                val row = getCellRow(iy, stridePx, cellSizePx, registrationRadius, gridSize)
                                                if (col != null && row != null) {
                                                    viewModel.updateSelection(row, col)
                                                }
                                            }
                                            lastX = x
                                            lastY = y
                                        }

                                        if (!pointer.pressed && pointer.previousPressed) {
                                            viewModel.endSelection()
                                            lastX = -1f
                                            lastY = -1f
                                        }
                                    }
                                }
                            }
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(gapDp.dp)
                        ) {
                            for (row in grid.indices) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(gapDp.dp)
                                ) {
                                    for (col in grid[row].indices) {
                                        val cellPair = Pair(row, col)
                                        val isFound = cellPair in foundCells
                                        val isSelected = cellPair in selectedCells
                                        val isHint = cellPair == hintIndices

                                        val defaultBrush = if ((row + col) % 2 == 0) {
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    cellColorA[row % cellColorA.size],
                                                    cellColorA[(row + 1) % cellColorA.size]
                                                )
                                            )
                                        } else {
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    cellColorB[row % cellColorB.size],
                                                    cellColorB[(row + 1) % cellColorB.size]
                                                )
                                            )
                                        }

                                        val cellBg = when {
                                            isFound -> Brush.linearGradient(
                                                colors = listOf(Color(0xFF66BB6A), Color(0xFF43A047))
                                            )
                                            isSelected -> Brush.linearGradient(
                                                colors = listOf(Color(0xFF7C4DFF), Color(0xFF651FFF))
                                            )
                                            isHint -> Brush.linearGradient(
                                                colors = listOf(Color(0xFFFFCA28), Color(0xFFFFB300))
                                            )
                                            else -> defaultBrush
                                        }

                                        val textColor = when {
                                            isFound || isSelected -> Color.White
                                            isHint -> Color(0xFF4A148C)
                                            else -> Color(0xFF1A237E)
                                        }

                                        val scale by animateFloatAsState(
                                            targetValue = when {
                                                isHint -> 1.15f
                                                isSelected -> 1.05f
                                                else -> 1f
                                            },
                                            label = "cellScale"
                                        )

                                        Box(
                                            modifier = Modifier
                                                .size(cellSizeDp.dp)
                                                .scale(scale)
                                                .shadow(
                                                    elevation = if (isSelected || isFound) 6.dp else 2.dp,
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(cellBg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = grid[row][col].toString(),
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = textColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Bar — sedikit ke atas, tidak nempel bawah
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = navBarPadding)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val hintsLeft = 3 - hintCount
            val hintDisabled = hintCount >= 3

            Button(
                onClick = { viewModel.giveHint() },
                enabled = !hintDisabled,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentOrange,
                    disabledContainerColor = Color(0xFFBDBDBD)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Petunjuk",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (hintDisabled) "HABIS" else "PETUNJUK ($hintsLeft)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Button(
                onClick = { showResetDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Jawaban",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("ULANG", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }

    // Dialog konfirmasi Reset — ramah anak
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = Color.Transparent,
            shape = RoundedCornerShape(24.dp),
            title = null,
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2))
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Emoji besar dan lucu
                        Text(text = "\uD83E\uDD14\u2753", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Mulai Lagi?",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFE65100)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Wah, semua kata yang sudah\nketemu bakal hilang lho! \uD83D\uDE2E",
                            fontSize = 14.sp,
                            color = Color(0xFF5D4037),
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Mau coba cari dari awal lagi? \uD83D\uDCAA",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100),
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Tombol-tombol lucu
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Tombol Batal
                            Button(
                                onClick = { showResetDialog = false },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90CAF9))
                            ) {
                                Text(
                                    text = "\uD83D\uDE45 BATAL",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }

                            // Tombol Reset
                            Button(
                                onClick = {
                                    viewModel.resetAllAnswers()
                                    showResetDialog = false
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7043))
                            ) {
                                Text(
                                    text = "\uD83D\uDE80 YA!",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }

    // Dialog Level Selesai — meriah dan menarik bagi anak-anak
    if (isLevelComplete && currentLevel != null) {
        AlertDialog(
            onDismissRequest = {},
            containerColor = Color.Transparent,
            shape = RoundedCornerShape(28.dp),
            title = null,
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(28.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFEDE7F6), // ungu muda
                                    Color(0xFFE8F5E9), // hijau muda
                                    Color(0xFFFFF9C4)  // kuning cerah
                                )
                            )
                        )
                        .padding(24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Emoji perayaan besar
                        Text(text = "\uD83C\uDFC6", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "\uD83C\uDF89\uD83C\uDF8A\uD83C\uDF89\uD83C\uDF8A\uD83C\uDF89", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Judul besar
                        Text(
                            text = "HEBAT SEKALI! \uD83C\uDF1F",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF4A148C)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Level Selesai!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6A1B9A)
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Info level dalam Card mini
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.7f))
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "\uD83D\uDCCB Level $levelId",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1565C0)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Tema: ${currentLevel!!.tema}",
                                    fontSize = 14.sp,
                                    color = Color(0xFF37474F)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "\u2B50 ${foundWords.size} kata ditemukan",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Kamu juara! \uD83D\uDCAA\uD83D\uDE0E",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Tombol aksi
                        if (levelId < 10) {
                            Button(
                                onClick = {
                                    prefsManager.saveHighestLevel(levelId + 1)
                                    onNavigateToLevel(levelId + 1)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
                            ) {
                                Text(
                                    text = "\uD83D\uDE80 LANJUT KE LEVEL ${levelId + 1}",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(onClick = { onNavigateToMain() }) {
                                Text(
                                    text = "\uD83C\uDFE0 Kembali ke Menu",
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryColor,
                                    fontSize = 13.sp
                                )
                            }
                        } else {
                            Button(
                                onClick = { onNavigateToMain() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                            ) {
                                Text(
                                    text = "\uD83C\uDFE0 KEMBALI KE MENU",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}
