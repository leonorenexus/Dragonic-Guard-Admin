package com.dragonic.guardparent.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

data class ChatMessage(
    val id: String = "",
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val formattedTime: String = SimpleDateFormat("HH:mm", Locale.getDefault())
        .format(Date(System.currentTimeMillis()))
)

class ChatRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun listenMessages(deviceId: String): Flow<List<ChatMessage>> = callbackFlow {
        val reg = firestore
            .collection("devices")
            .document(deviceId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snap, _ ->
                val msgs = snap?.documents?.mapNotNull { doc ->
                    try {
                        val ts = doc.getLong("timestamp") ?: return@mapNotNull null
                        ChatMessage(
                            id            = doc.id,
                            text          = doc.getString("text") ?: "",
                            timestamp     = ts,
                            formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault())
                                .format(Date(ts))
                        )
                    } catch (_: Exception) { null }
                } ?: emptyList()
                trySend(msgs)
            }
        awaitClose { reg.remove() }
    }

    suspend fun sendMessage(deviceId: String, text: String) {
        val now = System.currentTimeMillis()
        firestore.collection("devices")
            .document(deviceId)
            .collection("messages")
            .add(mapOf(
                "text"      to text,
                "timestamp" to now,
                "from"      to "parent"
            )).await()

        // Juga kirim sebagai command notifikasi ke HP anak
        firestore.collection("devices")
            .document(deviceId)
            .collection("commands")
            .add(mapOf(
                "type"      to "PARENT_MESSAGE",
                "payload"   to text,
                "timestamp" to now
            )).await()
    }

    suspend fun deleteOldMessages(deviceId: String) {
        try {
            val cutoff = System.currentTimeMillis() - (7 * 24 * 3600 * 1000L) // 7 hari
            val old = firestore.collection("devices")
                .document(deviceId)
                .collection("messages")
                .whereLessThan("timestamp", cutoff)
                .get().await()
            old.documents.forEach { it.reference.delete() }
        } catch (_: Exception) {}
    }
}
