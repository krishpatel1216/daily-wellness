package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object LedPresets {
    val presets = listOf(
        Pair("Sunrise Gold", Color(0xFFF59E0B)),
        Pair("Zen Emerald", Color(0xFF10B981)),
        Pair("Calm Cyan", Color(0xFF0EA5E9)),
        Pair("Cosmic Violet", Color(0xFFA855F7))
    )
}

@Composable
fun LedGlowFrame(
    isActive: Boolean,
    presetIndex: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val selectedColor = LedPresets.presets.getOrNull(presetIndex)?.second ?: Color(0xFFF59E0B)

    val infiniteTransition = rememberInfiniteTransition(label = "ledPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.90f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (isActive) {
                    Modifier.drawBehind {
                        val glowColor = selectedColor.copy(alpha = pulseAlpha * 0.45f)
                        val edgeWidth = 36.dp.toPx()

                        // Top edge glow
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(glowColor, Color.Transparent),
                                startY = 0f,
                                endY = edgeWidth
                            )
                        )
                        // Bottom edge glow
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, glowColor),
                                startY = size.height - edgeWidth,
                                endY = size.height
                            )
                        )
                        // Left edge glow
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(glowColor, Color.Transparent),
                                startX = 0f,
                                endX = edgeWidth
                            )
                        )
                        // Right edge glow
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, glowColor),
                                startX = size.width - edgeWidth,
                                endX = size.width
                            )
                        )
                    }
                } else Modifier
            )
    ) {
        content()
    }
}
