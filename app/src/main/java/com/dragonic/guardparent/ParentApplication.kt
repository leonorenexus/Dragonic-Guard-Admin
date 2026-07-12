package com.dragonic.guardparent

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.FirebaseApp

class ParentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ALERT, "Guard Alert",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply { description = "Notifikasi dari HP anak" }
            )
        }
    }
    companion object { const val CHANNEL_ALERT = "parent_alert" }
}
