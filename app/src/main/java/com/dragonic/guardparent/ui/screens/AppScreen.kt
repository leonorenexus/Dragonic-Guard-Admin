package com.dragonic.guardparent.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.dragonic.guardparent.model.AppRule
import com.dragonic.guardparent.ui.components.*
import com.dragonic.guardparent.ui.theme.*
import com.dragonic.guardparent.viewmodel.ParentViewModel

@Composable
fun AppsScreen(vm: ParentViewModel) {
    val rules by vm.rules.collectAsState()
    val feedback by vm.commandSent.collectAsState()
    val deviceId by vm.selectedDeviceId.collectAsState()
    var search by remember { mutableStateOf("") }
    var limitDialog by remember { mutableStateOf<AppRule?>(null) }

    // Snapshot stabil — hindari recomposition bug
    val stableRules = remember(rules) { rules.toList() }

    val filtered = remember(stableRules, search) {
        if (search.isBlank()) stableRules
        else stableRules.filter {
            it.appName.contains(search, ignoreCase = true) ||
            it.packageName.contains(search, ignoreCase = true)
        }
    }

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            // Header
            Column(Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                SectionHeader(
                    title = "Kontrol Aplikasi",
                    subtitle = "Kelola akses app HP anak dari sini"
                )
                Spacer(Modifier.height(14.dp))

                if (deviceId == null) {
                    GlassCard(Modifier.fillMaxWidth()) {
                        Text(
                            "Tambahkan perangkat anak terlebih dahulu di tab Perangkat",
                            color = PWhiteDim,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    return@Column
                }

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    placeholder = { Text("Cari aplikasi...", color = PWhiteDim.copy(0.4f)) },
                    leadingIcon = { Icon(Icons.Filled.Search, null, tint = PCyan) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PCyan,
                        unfocusedBorderColor = PGlassBorder,
                        focusedTextColor = PWhite,
                        unfocusedTextColor = PWhite,
                        cursorColor = PCyan,
                        focusedContainerColor = PGlass,
                        unfocusedContainerColor = PGlass
                    )
                )
            }

            // Content
            when {
                deviceId == null -> { /* handled above */ }

                stableRules.isEmpty() -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("📱", style = MaterialTheme.typography.displayLarge)
                            Text(
                                "Belum ada aturan app",
                                style = MaterialTheme.typography.titleMedium,
                                color = PWhite
                            )
                            Text(
                                "Aturan muncul setelah app anak terhubung ke Firebase",
                                style = MaterialTheme.typography.bodySmall,
                                color = PWhiteDim.copy(0.5f)
                            )
                        }
                    }
                }

                filtered.isEmpty() -> {
                    Box(
                        Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Tidak ada hasil untuk \"$search\"",
                            style = MaterialTheme.typography.bodyLarge,
                            color = PWhiteDim
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            start = 20.dp,
                            end = 20.dp,
                            top = 0.dp,
                            bottom = 80.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = filtered,
                            key = { it.packageName }
                        ) { rule ->
                            ParentAppItem(
                                rule = rule,
                                onToggleBlock = { vm.setAppBlocked(rule, it) },
                                onSetLimit = { limitDialog = rule }
                            )
                        }
                    }
                }
            }
        }

        // Feedback snackbar di bawah
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

    // Limit dialog
    limitDialog?.let { rule ->
        var input by remember(rule.packageName) {
            mutableStateOf(rule.dailyLimitMinutes.toString())
        }
        AlertDialog(
            onDismissRequest = { limitDialog = null },
            containerColor = PDeepBlue,
            title = {
                Text(
                    "Batas Waktu Harian",
                    color = PWhite,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Column {
                    Text(rule.appName, color = PCyan, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it.filter { c -> c.isDigit() } },
                        label = { Text("Menit per hari (0 = bebas)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PCyan,
                            unfocusedBorderColor = PGlassBorder,
                            focusedTextColor = PWhite,
                            unfocusedTextColor = PWhite,
                            cursorColor = PCyan,
                            focusedLabelColor = PCyan,
                            unfocusedLabelColor = PWhiteDim
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.setDailyLimit(rule, input.toIntOrNull() ?: 0)
                    limitDialog = null
                }) { Text("Simpan", color = PCyan) }
            },
            dismissButton = {
                TextButton(onClick = { limitDialog = null }) {
                    Text("Batal", color = PWhiteDim)
                }
            }
        )
    }
}

@Composable
fun ParentAppItem(
    rule: AppRule,
    onToggleBlock: (Boolean) -> Unit,
    onSetLimit: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderGlow = if (rule.isBlocked) PRed.copy(0.4f) else PGlassBorder
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.radialGradient(listOf(PCyan.copy(0.2f), PPurple.copy(0.1f)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    rule.appName.firstOrNull()?.uppercase() ?: "?",
                    style = MaterialTheme.typography.titleLarge,
                    color = PCyan
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(rule.appName, style = MaterialTheme.typography.bodyLarge, color = PWhite)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (rule.isBlocked) {
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(50))
                                .background(PRed.copy(0.15f))
                                .border(1.dp, PRed.copy(0.4f), RoundedCornerShape(50))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                "DIBLOKIR",
                                style = MaterialTheme.typography.labelSmall,
                                color = PRed
                            )
                        }
                    }
                    if (rule.dailyLimitMinutes > 0) {
                        Text(
                            "${rule.dailyLimitMinutes}m/hari",
                            style = MaterialTheme.typography.labelSmall,
                            color = PAmber
                        )
                    }
                }
            }
            Switch(
                checked = rule.isBlocked,
                onCheckedChange = onToggleBlock,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = PRed,
                    checkedTrackColor = PRed.copy(0.3f),
                    uncheckedThumbColor = PWhiteDim,
                    uncheckedTrackColor = PGlass
                )
            )
            IconButton(onClick = onSetLimit) {
                Icon(
                    Icons.Filled.Timer, null,
                    tint = if (rule.dailyLimitMinutes > 0) PAmber else PWhiteDim.copy(0.4f)
                )
            }
        }
    }
}
