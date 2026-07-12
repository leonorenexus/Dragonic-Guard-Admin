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
import com.dragonic.guardparent.ui.components.*
import com.dragonic.guardparent.ui.theme.*
import com.dragonic.guardparent.viewmodel.ParentViewModel

@Composable
fun DevicesScreen(vm: ParentViewModel) {
    val deviceIds by vm.deviceIds.collectAsState()
    val selectedId by vm.selectedDeviceId.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var deleteConfirm by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            SectionHeader(title = "Perangkat Anak", subtitle = "Kelola HP yang dipantau")
            IconButton(onClick = { showAddDialog = true }) {
                Box(
                    Modifier.clip(RoundedCornerShape(12.dp))
                        .background(PCyan.copy(0.15f))
                        .border(1.dp, PCyan.copy(0.4f), RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    Icon(Icons.Filled.Add, null, tint = PCyan, modifier = Modifier.size(20.dp))
                }
            }
        }

        if (deviceIds.isEmpty()) {
            GlassCard(Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                    Text("📡", style = MaterialTheme.typography.displayLarge)
                    Text("Belum ada perangkat", style = MaterialTheme.typography.titleMedium, color = PWhite)
                    Text("Tekan + untuk menambahkan HP anak",
                        style = MaterialTheme.typography.bodySmall, color = PWhiteDim.copy(0.5f))
                }
            }
        }

        deviceIds.forEach { id ->
            val isSelected = id == selectedId
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { vm.selectDevice(id) },
                borderGlow = if (isSelected) PCyan else PGlassBorder
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(48.dp).clip(RoundedCornerShape(14.dp))
                            .background(Brush.radialGradient(listOf(PCyan.copy(0.2f), PPurple.copy(0.1f)))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.PhoneAndroid, null, tint = PCyan, modifier = Modifier.size(24.dp))
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text("HP Anak", style = MaterialTheme.typography.titleMedium, color = PWhite)
                        Text(
                            id.take(20) + if (id.length > 20) "…" else "",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSelected) PCyan else PWhiteDim.copy(0.5f)
                        )
                    }
                    if (isSelected) {
                        Box(
                            Modifier.clip(RoundedCornerShape(50))
                                .background(PCyan.copy(0.15f))
                                .border(1.dp, PCyan.copy(0.4f), RoundedCornerShape(50))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) { Text("AKTIF", style = MaterialTheme.typography.labelSmall, color = PCyan) }
                    }
                    IconButton(onClick = { deleteConfirm = id }) {
                        Icon(Icons.Filled.Delete, null, tint = PRed.copy(0.6f))
                    }
                }
            }
        }

        // How to get device ID
        GlassCard(Modifier.fillMaxWidth()) {
            Text("📋  Cara Dapat ID Perangkat", style = MaterialTheme.typography.titleMedium, color = PCyan)
            Spacer(Modifier.height(8.dp))
            listOf(
                "1. Install DRAGONIC Guard di HP anak",
                "2. Buka app → tab Dashboard",
                "3. Salin ID Perangkat yang tampil",
                "4. Paste ID tersebut di sini"
            ).forEach {
                Text(it, style = MaterialTheme.typography.bodySmall, color = PWhiteDim)
                Spacer(Modifier.height(4.dp))
            }
        }
    }

    // Add device dialog
    if (showAddDialog) {
        var input by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = PDeepBlue,
            title = { Text("Tambah Perangkat Anak", color = PWhite, style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    Text("Masukkan ID perangkat dari app DRAGONIC Guard di HP anak",
                        color = PWhiteDim, style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        label = { Text("Device ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PCyan, unfocusedBorderColor = PGlassBorder,
                            focusedTextColor = PWhite, unfocusedTextColor = PWhite,
                            cursorColor = PCyan, focusedLabelColor = PCyan,
                            unfocusedLabelColor = PWhiteDim
                        )
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (input.isNotBlank()) { vm.addDevice(input); showAddDialog = false }
                }) { Text("Tambah", color = PCyan) }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Batal", color = PWhiteDim) }
            }
        )
    }

    // Delete confirm
    deleteConfirm?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteConfirm = null },
            containerColor = PDeepBlue,
            title = { Text("Hapus Perangkat?", color = PWhite, style = MaterialTheme.typography.titleLarge) },
            text = { Text("Perangkat ini akan dihapus dari daftar pantauan.",
                color = PWhiteDim, style = MaterialTheme.typography.bodyLarge) },
            confirmButton = {
                TextButton(onClick = { vm.removeDevice(id); deleteConfirm = null }) {
                    Text("Hapus", color = PRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirm = null }) { Text("Batal", color = PWhiteDim) }
            }
        )
    }
}
