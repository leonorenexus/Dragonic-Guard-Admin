package com.dragonic.guardparent.service

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.dragonic.guardparent.ParentApplication.Companion.CHANNEL_ALERT
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class ParentFCMService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.data["title"] ?: message.notification?.title ?: "DRAGONIC Guard"
        val body  = message.data["body"]  ?: message.notification?.body  ?: "Aktivitas baru terdeteksi"
        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        val notif = NotificationCompat.Builder(this, CHANNEL_ALERT)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notif)
    }
}
