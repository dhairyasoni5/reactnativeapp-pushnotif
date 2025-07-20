package com.vedazdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import androidx.core.app.NotificationCompat
import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.modules.core.DeviceEventManagerModule
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class NotificationActionReceiver : BroadcastReceiver() {
    companion object {
        const val CALL_NOTIFICATION_ID = 1002
        const val CALL_IN_PROGRESS_NOTIFICATION_ID = 1003
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val callerName = intent.getStringExtra("CALLER_NAME") ?: "Unknown Caller"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (action) {
            "com.vedazdemo.ACTION_DECLINE" -> {
                Log.d("NotificationActionReceiver", "Decline action received, showing missed call notification: $callerName")
                
                // Dismiss the incoming call notification
                notificationManager.cancel(CALL_NOTIFICATION_ID)
                
                // Show missed call notification
                val channelId = "missed_call_channel"
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(channelId, "Missed Calls", NotificationManager.IMPORTANCE_HIGH)
                    channel.setShowBadge(true) // Show badge for missed calls
                    notificationManager.createNotificationChannel(channel)
                }
                val notification = NotificationCompat.Builder(context, channelId)
                    .setContentTitle("Missed Astro-Consultation Call")
                    .setContentText("You missed a call from $callerName")
                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                    .setAutoCancel(true)
                    .build()
                notificationManager.notify(1001, notification)
                
                // Send LocalBroadcastManager event for missed call
                val missedCallIntent = Intent("MISSED_CALL_EVENT")
                LocalBroadcastManager.getInstance(context).sendBroadcast(missedCallIntent)
                
                // Also send regular broadcast for backward compatibility
                val regularMissedCallIntent = Intent("com.vedazdemo.MISSED_CALL")
                context.sendBroadcast(regularMissedCallIntent)
            }
            
            "com.vedazdemo.ACTION_END_CALL" -> {
                Log.d("NotificationActionReceiver", "End call action received")
                
                // Cancel the call in progress notification
                notificationManager.cancel(CALL_IN_PROGRESS_NOTIFICATION_ID)
                
                // Send broadcast to React Native to handle navigation
                val endCallIntent = Intent("com.vedazdemo.END_CALL_ACTION")
                context.sendBroadcast(endCallIntent)
                
                // Also try to send event to React Native if app is running
                try {
                    val reactContext = context.applicationContext as? com.facebook.react.ReactApplication
                    reactContext?.reactNativeHost?.reactInstanceManager?.currentReactContext?.let { reactContext ->
                        reactContext
                            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                            .emit("END_CALL_ACTION", null)
                    }
                } catch (e: Exception) {
                    Log.d("NotificationActionReceiver", "React Native not running, using broadcast only: ${e.message}")
                }
            }
        }
    }
} 