package com.dragonic.guardparent.repository

import com.dragonic.guardparent.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class ParentRepository {

    private val firestore = FirebaseFirestore.getInstance()

    // ── Watch child device status (real-time) ───────────────────────────────

    fun watchChildDevice(deviceId: String): Flow<ChildDevice> = callbackFlow {
        val reg: ListenerRegistration = firestore
            .collection("devices")
            .document(deviceId)
            .addSnapshotListener { snap, _ ->
                if (snap != null && snap.exists()) {
                    val device = ChildDevice(
                        deviceId       = deviceId,
                        nickname       = snap.getString("nickname") ?: "HP Anak",
                        fcmToken       = snap.getString("fcmToken") ?: "",
                        lastSeen       = snap.getLong("lastSeen") ?: 0L,
                        batteryLevel   = (snap.getLong("batteryLevel") ?: 0L).toInt(),
                        isCharging     = snap.getBoolean("isCharging") ?: false,
                        currentApp     = snap.getString("currentApp") ?: "",
                        todayScreenTimeMinutes = snap.getLong("todayScreenTimeMinutes") ?: 0L
                    )
                    trySend(device)
                }
            }
        awaitClose { reg.remove() }
    }

    // ── Watch app rules (real-time) ──────────────────────────────────────────

    fun watchRules(deviceId: String): Flow<List<AppRule>> = callbackFlow {
        val reg = firestore
            .collection("devices")
            .document(deviceId)
            .collection("rules")
            .addSnapshotListener { snap, _ ->
                val rules = snap?.documents?.mapNotNull { doc ->
                    try {
                        AppRule(
                            packageName        = doc.getString("packageName") ?: return@mapNotNull null,
                            appName            = doc.getString("appName") ?: "",
                            isBlocked          = doc.getBoolean("isBlocked") ?: false,
                            dailyLimitMinutes  = (doc.getLong("dailyLimitMinutes") ?: 0L).toInt(),
                            updatedAt          = doc.getLong("updatedAt") ?: 0L
                        )
                    } catch (_: Exception) { null }
                } ?: emptyList()
                trySend(rules)
            }
        awaitClose { reg.remove() }
    }

    // ── Watch today's usage ──────────────────────────────────────────────────

    fun watchTodayUsage(deviceId: String): Flow<List<AppUsageRecord>> = callbackFlow {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val reg = firestore
            .collection("devices")
            .document(deviceId)
            .collection("usage")
            .whereEqualTo("date", today)
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { doc ->
                    try {
                        AppUsageRecord(
                            packageName    = doc.getString("packageName") ?: "",
                            appName        = doc.getString("appName") ?: "",
                            usageMinutes   = doc.getLong("usageMinutes") ?: 0L,
                            date           = today
                        )
                    } catch (_: Exception) { null }
                } ?: emptyList()
                trySend(list.sortedByDescending { it.usageMinutes })
            }
        awaitClose { reg.remove() }
    }

    // ── Send remote command ──────────────────────────────────────────────────

    suspend fun sendCommand(deviceId: String, command: RemoteCommand) {
        firestore.collection("devices")
            .document(deviceId)
            .collection("commands")
            .add(command)
            .await()
    }

    // ── Block / unblock app ──────────────────────────────────────────────────

    suspend fun setAppBlocked(deviceId: String, rule: AppRule, blocked: Boolean) {
        val updated = rule.copy(isBlocked = blocked, updatedAt = System.currentTimeMillis())
        firestore.collection("devices")
            .document(deviceId)
            .collection("rules")
            .document(rule.packageName)
            .set(updated)
            .await()
        sendCommand(deviceId, RemoteCommand(type = if (blocked) "BLOCK_APP" else "UNBLOCK_APP",
            payload = rule.packageName))
    }

    // ── Set daily limit ──────────────────────────────────────────────────────

    suspend fun setDailyLimit(deviceId: String, rule: AppRule, minutes: Int) {
        val updated = rule.copy(dailyLimitMinutes = minutes, updatedAt = System.currentTimeMillis())
        firestore.collection("devices")
            .document(deviceId)
            .collection("rules")
            .document(rule.packageName)
            .set(updated)
            .await()
        sendCommand(deviceId, RemoteCommand(type = "SYNC_RULES"))
    }

    // ── Save device ID to prefs ──────────────────────────────────────────────

    fun getSavedDeviceIds(context: android.content.Context): List<String> {
        val prefs = context.getSharedPreferences("parent_prefs", android.content.Context.MODE_PRIVATE)
        val raw = prefs.getString("device_ids", "") ?: ""
        return if (raw.isEmpty()) emptyList() else raw.split(",").filter { it.isNotBlank() }
    }

    fun saveDeviceId(context: android.content.Context, deviceId: String) {
        val prefs = context.getSharedPreferences("parent_prefs", android.content.Context.MODE_PRIVATE)
        val existing = getSavedDeviceIds(context).toMutableList()
        if (deviceId !in existing) {
            existing.add(deviceId)
            prefs.edit().putString("device_ids", existing.joinToString(",")).apply()
        }
    }

    fun removeDeviceId(context: android.content.Context, deviceId: String) {
        val prefs = context.getSharedPreferences("parent_prefs", android.content.Context.MODE_PRIVATE)
        val existing = getSavedDeviceIds(context).toMutableList()
        existing.remove(deviceId)
        prefs.edit().putString("device_ids", existing.joinToString(",")).apply()
    }
}
