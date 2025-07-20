package com.vedazdemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {
    companion object {
        const val CALL_NOTIFICATION_ID = 1002
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCMService", "New FCM token: $token")
        // Send token to server if needed
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d("FCMService", "onMessageReceived called. Data: ${remoteMessage.data}")

        val data = remoteMessage.data
        val callType = data["callType"] ?: "ASTRO_CONSULTATION"
        val astrologerName = data["callerName"] ?: "Astrologer"

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "call_channel"
        val vibrationPattern = longArrayOf(0, 400, 200, 400, 200, 400)
        val ringtoneUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.incoming_call)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Incoming Calls", NotificationManager.IMPORTANCE_HIGH)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            channel.enableVibration(true)
            channel.vibrationPattern = vibrationPattern
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            channel.setSound(ringtoneUri, audioAttributes)
            notificationManager.createNotificationChannel(channel)
        }

        // Full-screen intent for DemoConsultationActivity (shows when locked)
        val fullScreenIntent = Intent(this, DemoConsultationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("caller_name", astrologerName)
            putExtra("is_full_screen_call", true)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Accept action: Direct launch to DemoConsultationActivity
        val acceptIntent = Intent(this, DemoConsultationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("caller_name", astrologerName)
        }
        val acceptPendingIntent = PendingIntent.getActivity(
            this, 1, acceptIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Decline action: broadcast
        val declineIntent = Intent(this, NotificationActionReceiver::class.java).apply {
            action = "com.vedazdemo.ACTION_DECLINE"
            putExtra("CALLER_NAME", astrologerName)
        }
        val declinePendingIntent = PendingIntent.getBroadcast(
            this, 2, declineIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("Incoming Astro-Consultation Call")
            .setContentText("Call from $astrologerName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setAutoCancel(true)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setVibrate(vibrationPattern)
            .setSound(ringtoneUri)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Decline", declinePendingIntent)
            .addAction(android.R.drawable.ic_menu_call, "Accept", acceptPendingIntent)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .build()

        notificationManager.notify(CALL_NOTIFICATION_ID, notification)
    }
} 