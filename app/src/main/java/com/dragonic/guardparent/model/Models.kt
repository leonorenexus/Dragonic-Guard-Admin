package com.dragonic.guardparent.model

data class ChildDevice(
    val deviceId: String = "",
    val nickname: String = "HP Anak",
    val fcmToken: String = "",
    val lastSeen: Long = 0L,
    val batteryLevel: Int = 0,
    val isCharging: Boolean = false,
    val currentApp: String = "",
    val todayScreenTimeMinutes: Long = 0L
)

data class AppRule(
    val packageName: String = "",
    val appName: String = "",
    val isBlocked: Boolean = false,
    val dailyLimitMinutes: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)

data class AppUsageRecord(
    val packageName: String = "",
    val appName: String = "",
    val usageMinutes: Long = 0L,
    val date: String = ""
)

enum class CommandType {
    LOCK_DEVICE, UNLOCK_DEVICE, BLOCK_APP, UNBLOCK_APP, SYNC_RULES, PING
}

data class RemoteCommand(
    val type: String = CommandType.PING.name,
    val payload: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
