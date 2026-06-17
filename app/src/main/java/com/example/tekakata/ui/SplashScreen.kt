package com.example.tekakata.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val SplashGradientColors = listOf(
    Color(0xFFB3E5FC),
    Color(0xFFE1F5FE),
    Color(0xFFFFFFFF)
)

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var showSubtitle by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showSubtitle = true
        delay(3200)
        onSplashFinished()
    }

    val subtitleAlpha by animateFloatAsState(
        targetValue = if (showSubtitle) 1f else 0f,
        animationSpec = tween(500),
        label = "subtitleAlpha"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val bounceScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounceScale"
    )
    val wobbleRotation by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wobbleRotation"
    )
    val sparkleScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(1300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkleScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = SplashGradientColors)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 80.dp, end = 36.dp)
                .scale(sparkleScale)
        ) {
            Text(text = "\u2B50", fontSize = 22.sp)
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 60.dp, start = 30.dp)
                .scale(sparkleScale)
        ) {
            Text(text = "\uD83C\uDF1F", fontSize = 20.sp)
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = 40.dp)
                .scale(sparkleScale)
        ) {
            Text(text = "\u2728", fontSize = 24.sp)
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 90.dp, start = 35.dp)
                .scale(sparkleScale)
        ) {
            Text(text = "\uD83D\uDCAB", fontSize = 16.sp)
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "\uD83D\uDD0D",
                fontSize = 80.sp,
                modifier = Modifier
                    .scale(bounceScale)
                    .rotate(wobbleRotation)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "TEKA KATA",
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1565C0),
                letterSpacing = 6.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Si Pencari Kata",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1565C0).copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(subtitleAlpha)
            )
        }
    }
}
