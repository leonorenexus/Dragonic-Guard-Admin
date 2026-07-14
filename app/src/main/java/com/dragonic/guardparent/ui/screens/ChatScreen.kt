package com.dragonic.guardparent.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dragonic.guardparent.ui.components.*
import com.dragonic.guardparent.ui.theme.*
import com.dragonic.guardparent.viewmodel.ParentViewModel

// Quick messages orang tua ke anak
private val QUICK_MESSAGES = listOf(
    "🍽️ Makan dulu!",
    "😴 Tidur sekarang!",
    "📚 Belajar dulu!",
    "🚿 Mandi dulu!",
    "📵 Taruh HP nya!",
    "🏠 Pulang sekarang!",
)

@Composable
fun ChatScreen(vm: ParentViewModel) {
    val messages by vm.messages.collectAsState()
    val deviceId by vm.selectedDeviceId.collectAsState()
    val feedback by vm.commandSent.collectAsState()
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto scroll ke pesan terbaru
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(0)
    }

    Box(Modifier.fillMaxSize()) {
        GalaxyBackground(Modifier.fillMaxSize())

        Column(Modifier.fillMaxSize()) {
            // Header
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(PBlack.copy(0.95f), PBlack.copy(0f))
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                SectionHeader(
                    title = "Pesan ke Anak",
                    subtitle = "Notifikasi langsung tampil di HP anak"
                )
            }

            if (deviceId == null) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("💬", fontSize = 48.sp)
                        Text(
                            "Tambahkan perangkat anak dulu",
                            style = MaterialTheme.typography.titleMedium,
                            color = PWhite
                        )
                    }
                }
            } else {
                // Quick messages
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QUICK_MESSAGES.forEach { msg ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(PPurple.copy(0.2f))
                                .border(1.dp, PPurple.copy(0.4f), RoundedCornerShape(50))
                                .clickable { vm.sendMessage(msg) }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                msg,
                                style = MaterialTheme.typography.bodySmall,
                                color = PWhite
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Messages list
                if (messages.isEmpty()) {
                    Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("📭", fontSize = 40.sp)
                            Text(
                                "Belum ada pesan terkirim",
                                style = MaterialTheme.typography.bodyLarge,
                                color = PWhiteDim
                            )
                            Text(
                                "Kirim pesan atau pilih template di atas",
                                style = MaterialTheme.typography.bodySmall,
                                color = PWhiteDim.copy(0.5f)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(
                            start = 16.dp, end = 16.dp,
                            top = 8.dp, bottom = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        reverseLayout = true
                    ) {
                        items(messages, key = { it.id }) { msg ->
                            ChatBubble(msg.text, msg.formattedTime)
                        }
                    }
                }

                // Input bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, PBlack.copy(0.98f))
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        placeholder = {
                            Text("Ketik pesan...", color = PWhiteDim.copy(0.4f))
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = PCyan,
                            unfocusedBorderColor = PGlassBorder,
                            focusedTextColor     = PWhite,
                            unfocusedTextColor   = PWhite,
                            cursorColor          = PCyan,
                            focusedContainerColor   = PGlass,
                            unfocusedContainerColor = PGlass
                        )
                    )
                    // Send button
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(PCyan.copy(0.4f), PPurple.copy(0.3f))
                                )
                            )
                            .border(1.dp, PCyan.copy(0.6f), CircleShape)
                            .clickable {
                                if (input.isNotBlank()) {
                                    vm.sendMessage(input)
                                    input = ""
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Send, null, tint = PCyan, modifier = Modifier.size(22.dp))
                    }
                }
            }
        }

        feedback?.let { msg ->
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp, start = 16.dp, end = 16.dp)
            ) {
                FeedbackSnackbar(msg) { vm.clearFeedback() }
            }
        }
    }
}

@Composable
fun ChatBubble(text: String, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Column(horizontalAlignment = Alignment.End) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 18.dp, topEnd = 18.dp,
                            bottomStart = 18.dp, bottomEnd = 4.dp
                        )
                    )
                    .background(
                        Brush.horizontalGradient(
                            listOf(PPurple.copy(0.6f), PCyan.copy(0.4f))
                        )
                    )
                    .border(
                        1.dp,
                        PCyan.copy(0.3f),
                        RoundedCornerShape(
                            topStart = 18.dp, topEnd = 18.dp,
                            bottomStart = 18.dp, bottomEnd = 4.dp
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .widthIn(max = 280.dp)
            ) {
                Text(
                    text,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                    color = PWhite
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                "Terkirim · $time",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = PWhiteDim.copy(0.4f)
            )
        }
    }
}

private val CircleShape = androidx.compose.foundation.shape.CircleShape
