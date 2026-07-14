package com.dragonic.guardparent.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.*
import kotlin.random.Random

data class Star(
    val x: Float,
    val y: Float,
    val radius: Float,
    val alpha: Float,
    val speed: Float,
    val color: Color
)

data class Nebula(
    val x: Float,
    val y: Float,
    val radius: Float,
    val color: Color,
    val alpha: Float
)

@Composable
fun GalaxyBackground(modifier: Modifier = Modifier) {
    val stars = remember {
        List(180) {
            Star(
                x      = Random.nextFloat(),
                y      = Random.nextFloat(),
                radius = Random.nextFloat() * 2.5f + 0.5f,
                alpha  = Random.nextFloat() * 0.8f + 0.2f,
                speed  = Random.nextFloat() * 0.008f + 0.002f,
                color  = listOf(
                    Color(0xFF4FC3F7), // cyan
                    Color(0xFFE8F4FD), // white
                    Color(0xFF7C4DFF), // purple
                    Color(0xFFB39DDB), // light purple
                    Color(0xFF80DEEA)  // teal
                ).random()
            )
        }
    }

    val nebulae = remember {
        listOf(
            Nebula(0.15f, 0.2f,  0.35f, Color(0xFF1A237E), 0.25f),
            Nebula(0.8f,  0.15f, 0.28f, Color(0xFF4A148C), 0.2f),
            Nebula(0.5f,  0.6f,  0.4f,  Color(0xFF006064), 0.15f),
            Nebula(0.2f,  0.75f, 0.25f, Color(0xFF1B5E20), 0.12f),
            Nebula(0.85f, 0.7f,  0.3f,  Color(0xFF4A148C), 0.18f),
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "galaxy")

    // Twinkle animation
    val twinkle by infiniteTransition.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(3000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "twinkle"
    )

    // Slow drift
    val drift by infiniteTransition.animateFloat(
        0f, 1f,
        infiniteRepeatable(tween(20000, easing = LinearEasing)),
        label = "drift"
    )

    // Nebula pulse
    val nebulaPulse by infiniteTransition.animateFloat(
        0.8f, 1.2f,
        infiniteRepeatable(tween(5000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "nebula"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Deep space background
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF0A0F1E),
                    Color(0xFF050810),
                    Color(0xFF000005)
                ),
                center = Offset(size.width * 0.5f, size.height * 0.3f),
                radius = size.width * 1.2f
            )
        )

        // Draw nebulae
        nebulae.forEach { nebula ->
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        nebula.color.copy(alpha = nebula.alpha * nebulaPulse),
                        nebula.color.copy(alpha = nebula.alpha * 0.3f * nebulaPulse),
                        Color.Transparent
                    ),
                    center = Offset(size.width * nebula.x, size.height * nebula.y),
                    radius = size.width * nebula.radius * nebulaPulse
                ),
                radius = size.width * nebula.radius * nebulaPulse,
                center = Offset(size.width * nebula.x, size.height * nebula.y)
            )
        }

        // Draw stars with twinkle + drift
        stars.forEach { star ->
            val driftedY = ((star.y + drift * star.speed) % 1f)
            val twinkleAlpha = star.alpha * (0.5f + 0.5f * sin(
                (twinkle + star.x + star.y) * PI.toFloat() * 2f
            ))

            // Star glow
            if (star.radius > 1.5f) {
                drawCircle(
                    color = star.color.copy(alpha = twinkleAlpha * 0.3f),
                    radius = star.radius * 3f,
                    center = Offset(size.width * star.x, size.height * driftedY)
                )
            }

            // Star core
            drawCircle(
                color = star.color.copy(alpha = twinkleAlpha),
                radius = star.radius,
                center = Offset(size.width * star.x, size.height * driftedY)
            )
        }

        // Shooting stars
        drawShootingStar(drift, size.width, size.height)

        // Bottom vignette
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFF050810).copy(alpha = 0.6f)
                )
            )
        )
    }
}

private fun DrawScope.drawShootingStar(drift: Float, w: Float, h: Float) {
    val progress = (drift * 3f) % 1f
    if (progress < 0.3f) {
        val t = progress / 0.3f
        val startX = w * 0.1f + w * 0.6f * t
        val startY = h * 0.05f + h * 0.15f * t
        val length = 80f * (1f - t)
        val alpha = (1f - t) * 0.8f

        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(Color.Transparent, Color(0xFF4FC3F7).copy(alpha = alpha)),
                start  = Offset(startX - length, startY - length * 0.3f),
                end    = Offset(startX, startY)
            ),
            start       = Offset(startX - length, startY - length * 0.3f),
            end         = Offset(startX, startY),
            strokeWidth = 1.5f
        )
    }
}
