package com.dragonic.guardparent.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dragonic.guardparent.ui.components.*
import com.dragonic.guardparent.ui.theme.*
import com.dragonic.guardparent.viewmodel.ParentViewModel
import kotlin.math.max

@Composable
fun UsageScreen(vm: ParentViewModel) {
    val usage by vm.todayUsage.collectAsState()
    val child by vm.childDevice.collectAsState()
    val total = max(1L, usage.sumOf { it.usageMinutes })

    Column(Modifier.fillMaxSize()) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
            SectionHeader(title = "Pemakaian Hari Ini", subtitle = child?.nickname ?: "HP Anak")
            Spacer(Modifier.height(16.dp))
            GlassCard(Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    UsageStat("Total", "${total}m", PCyan)
                    UsageStat("Apps", "${usage.size}", PPurple)
                    val h = total / 60; val m = total % 60
                    UsageStat("Durasi", "${h}j ${m}m", PAmber)
                }
            }
        }

        if (usage.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📊", fontSize = 48.sp)
                    Text("Belum ada data",
                        style = MaterialTheme.typography.titleMedium, color = PWhite)
                    Text("Data akan muncul saat HP anak aktif",
                        style = MaterialTheme.typography.bodySmall, color = PWhiteDim.copy(0.5f))
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(usage) { idx, record ->
                    val pct = record.usageMinutes.toFloat() / total
                    val color = when (idx) {
                        0 -> PCyan; 1 -> PPurple; 2 -> PAmber
                        else -> PWhiteDim.copy(0.5f)
                    }
                    GlassCard(Modifier.fillMaxWidth()) {
                        Row(Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)) {
                                Box(
                                    Modifier.size(36.dp).clip(RoundedCornerShape(10.dp))
                                        .background(color.copy(0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("${idx + 1}",
                                        style = MaterialTheme.typography.labelSmall, color = color)
                                }
                                Column {
                                    Text(record.appName,
                                        style = MaterialTheme.typography.bodyLarge, color = PWhite)
                                    Text("${(pct * 100).toInt()}% dari total",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PWhiteDim.copy(0.5f))
                                }
                            }
                            Text("${record.usageMinutes}m",
                                style = MaterialTheme.typography.titleMedium, color = color)
                        }
                        Spacer(Modifier.height(8.dp))
                        Box(Modifier.fillMaxWidth().height(4.dp)
                            .clip(RoundedCornerShape(2.dp)).background(PGlass)) {
                            Box(Modifier.fillMaxWidth(pct).height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Brush.horizontalGradient(
                                    listOf(color, color.copy(0.4f)))))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UsageStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = PWhiteDim)
    }
}
