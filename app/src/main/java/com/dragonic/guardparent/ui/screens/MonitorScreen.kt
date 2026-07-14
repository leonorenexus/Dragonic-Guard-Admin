package com.dragonic.guardparent.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dragonic.guardparent.ui.components.*
import com.dragonic.guardparent.ui.theme.*
import com.dragonic.guardparent.viewmodel.ParentViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MonitorScreen(vm: ParentViewModel) {
    val child    by vm.childDevice.collectAsState()
    val feedback by vm.commandSent.collectAsState()
    val deviceId by vm.selectedDeviceId.collectAsState()

    val inf = rememberInfiniteTransition(label = "r")
    val rotation by inf.animateFloat(
        0f, 360f,
        infiniteRepeatable(tween(10000, easing = LinearEasing)),
        label = "rot"
    )

    Box(Modifier.fillMaxSize()) {
        // Galaxy background
        GalaxyBackground(Modifier.fillMaxSize())

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "DRAGONIC",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = PCyan
                    )
                    Text(
                        "Guard Parent",
                        style = MaterialTheme.typography.displayLarge,
                        color = PWhite
                    )
                    Text(
                        "v2.0.4",
                        style = MaterialTheme.typography.labelSmall,
                        color = PPurple
                    )
                }
                if (child != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        StatusDot(active = isOnline(child!!.lastSeen))
                        Text(
                            if (isOnline(child!!.lastSeen)) "Online" else "Offline",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isOnline(child!!.lastSeen)) PGreen else PRed
                        )
                    }
                }
            }

            // No device
            if (deviceId == null) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("📡", fontSize = 40.sp)
                            Text(
                                "Belum ada perangkat anak",
                                style = MaterialTheme.typography.titleMedium,
                                color = PWhite
                            )
                            Text(
                                "Tambahkan ID perangkat di tab Perangkat",
                                style = MaterialTheme.typography.bodySmall,
                                color = PWhiteDim.copy(0.5f)
                            )
                        }
                    }
                }
                return@Column
            }

            if (child == null) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = PCyan, modifier = Modifier.size(24.dp))
                        Text("Menghubungkan ke perangkat anak...", color = PWhiteDim)
                    }
                }
                return@Column
            }

            val c = child!!

            // Hero card with rotating rings
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF0D1B3E).copy(0.85f),
                                Color(0xFF050810).copy(0.9f)
                            )
                        )
                    )
                    .border(1.dp, PGlassBorder, RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Rings
                Box(
                    Modifier.size(170.dp).rotate(rotation)
                        .border(
                            2.dp,
                            Brush.sweepGradient(
                                listOf(PCyan.copy(0f), PCyan.copy(0.9f), PPurple.copy(0.5f), PCyan.copy(0f))
                            ),
                            CircleShape
                        )
                )
                Box(
                    Modifier.size(120.dp).rotate(-rotation * 0.7f)
                        .border(
                            1.dp,
                            Brush.sweepGradient(
                                listOf(PPurple.copy(0f), PPurple.copy(0.8f), PCyan.copy(0.3f), PPurple.copy(0f))
                            ),
                            CircleShape
                        )
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Filled.PhoneAndroid, null, tint = PCyan, modifier = Modifier.size(32.dp))
                    Text(c.nickname, style = MaterialTheme.typography.titleLarge, color = PWhite)
                    Text(
                        if (c.currentApp.isNotEmpty()) "📱 ${c.currentApp}" else "Layar mati",
                        style = MaterialTheme.typography.bodySmall,
                        color = PWhiteDim
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${c.todayScreenTimeMinutes}m screen time hari ini",
                        style = MaterialTheme.typography.labelSmall,
                        color = PCyan
                    )
                }
            }

            // Stats
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MiniStat(
                    "Baterai", "${c.batteryLevel}%",
                    if (c.batteryLevel > 20) PGreen else PRed,
                    Modifier.weight(1f)
                )
                MiniStat("Screen Time", "${c.todayScreenTimeMinutes}m", PCyan, Modifier.weight(1f))
                MiniStat(
                    "Status",
                    if (isOnline(c.lastSeen)) "Online" else "Offline",
                    if (isOnline(c.lastSeen)) PGreen else PRed,
                    Modifier.weight(1f)
                )
            }

            // Last seen
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Terakhir aktif", style = MaterialTheme.typography.bodySmall)
                        Text(
                            formatLastSeen(c.lastSeen),
                            style = MaterialTheme.typography.titleMedium,
                            color = PWhite
                        )
                    }
                    Icon(Icons.Filled.AccessTime, null, tint = PCyan, modifier = Modifier.size(22.dp))
                }
            }

            // Actions
            SectionHeader(title = "Kontrol Jarak Jauh")

            GlowButton(
                text = "🔒  KUNCI HP ANAK SEKARANG",
                onClick = { vm.lockDevice() },
                glowColor = PRed
            )

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlowButton(
                    text = "🔄 SINKRON",
                    onClick = { vm.syncRules() },
                    glowColor = PCyan,
                    modifier = Modifier.weight(1f)
                )
                GlowButton(
                    text = "📡 PING",
                    onClick = { vm.ping() },
                    glowColor = PPurple,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(8.dp))
        }

        // Feedback
        feedback?.let { msg ->
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                FeedbackSnackbar(msg) { vm.clearFeedback() }
            }
        }
    }
}

@Composable
fun MiniStat(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(0.1f))
            .border(1.dp, color.copy(0.3f), RoundedCornerShape(14.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            value,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = color
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color = PWhiteDim
        )
    }
}

private fun isOnline(lastSeen: Long) = System.currentTimeMillis() - lastSeen < 5 * 60 * 1000L

private fun formatLastSeen(ts: Long): String {
    if (ts == 0L) return "Belum pernah"
    val diff = System.currentTimeMillis() - ts
    return when {
        diff < 60_000       -> "Baru saja"
        diff < 3_600_000    -> "${diff / 60_000} menit lalu"
        else                -> SimpleDateFormat("HH:mm, dd MMM", Locale.getDefault()).format(Date(ts))
    }
}
