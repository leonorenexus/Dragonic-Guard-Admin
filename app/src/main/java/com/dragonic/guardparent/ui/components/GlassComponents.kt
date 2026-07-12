package com.dragonic.guardparent.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dragonic.guardparent.ui.theme.*

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderGlow: Color = PGlassBorder,
    content: @Composable ColumnScope.() -> Unit
) {
    val inf = rememberInfiniteTransition(label = "glow")
    val alpha by inf.animateFloat(0.3f, 0.7f,
        infiniteRepeatable(tween(2000, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "a")

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(
                listOf(Color(0x1A4FC3F7), Color(0x0D7C4DFF), Color(0x0A050810)),
                Offset.Zero, Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            ))
            .border(1.dp,
                Brush.linearGradient(listOf(
                    borderGlow.copy(alpha), PPurple.copy(alpha * 0.5f), borderGlow.copy(alpha * 0.3f)
                )),
                RoundedCornerShape(20.dp))
    ) {
        Column(Modifier.padding(16.dp), content = content)
    }
}

@Composable
fun GlowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    glowColor: Color = PCyan,
    enabled: Boolean = true
) {
    val inf = rememberInfiniteTransition(label = "g")
    val s by inf.animateFloat(0.8f, 1f,
        infiniteRepeatable(tween(1500, easing = EaseInOutSine), RepeatMode.Reverse), label = "s")

    Box(modifier = modifier
        .clip(RoundedCornerShape(14.dp))
        .background(Brush.horizontalGradient(listOf(
            glowColor.copy(if (enabled) 0.25f else 0.1f),
            PPurple.copy(if (enabled) 0.25f else 0.1f)
        )))
        .border(1.5.dp, Brush.horizontalGradient(listOf(glowColor.copy(s), PPurple.copy(s * 0.7f))),
            RoundedCornerShape(14.dp))
    ) {
        Button(onClick = onClick, enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, contentColor = glowColor,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = PWhiteDim.copy(0.3f)
            ), modifier = Modifier.fillMaxWidth()
        ) {
            Text(text, letterSpacing = 1.5.sp, style = MaterialTheme.typography.labelSmall.copy(fontSize = 13.sp))
        }
    }
}

@Composable
fun StatusDot(active: Boolean, modifier: Modifier = Modifier) {
    val inf = rememberInfiniteTransition(label = "dot")
    val a by inf.animateFloat(0.5f, 1f,
        infiniteRepeatable(tween(800), RepeatMode.Reverse), label = "da")
    val color = if (active) PGreen else PRed
    Box(modifier = modifier
        .size(10.dp)
        .clip(RoundedCornerShape(50))
        .background(color.copy(if (active) a else 0.4f)))
}

@Composable
fun SectionHeader(title: String, subtitle: String? = null) {
    Column {
        Text(title, style = MaterialTheme.typography.titleLarge, color = PWhite)
        if (subtitle != null)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = PCyan.copy(0.7f))
        Spacer(Modifier.height(4.dp))
        Box(Modifier.fillMaxWidth(0.3f).height(1.dp).background(
            Brush.horizontalGradient(listOf(PCyan, PPurple, Color.Transparent))
        ))
    }
}

@Composable
fun FeedbackSnackbar(message: String, onDismiss: () -> Unit) {
    LaunchedEffect(message) {
        kotlinx.coroutines.delay(2500)
        onDismiss()
    }
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(PNavy)
            .border(1.dp, PCyan.copy(0.4f), RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(message, color = PWhite, style = MaterialTheme.typography.bodyLarge)
    }
}
