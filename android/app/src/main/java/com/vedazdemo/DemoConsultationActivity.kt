package com.vedazdemo

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.PendingIntent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class DemoConsultationActivity : AppCompatActivity() {
    companion object {
        const val CALL_NOTIFICATION_ID = 1002
        const val CALL_IN_PROGRESS_NOTIFICATION_ID = 1003
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle locked screen scenario
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            
            // Request to dismiss keyguard if device is secured
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            if (keyguardManager.isKeyguardLocked) {
                try {
                    keyguardManager.requestDismissKeyguard(this, null)
                } catch (e: Exception) {
                    Log.e("DemoConsultationActivity", "Failed to dismiss keyguard: ${e.message}")
                }
            }
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }
        
        // Keep screen on during call
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // Get caller name from intent
        val callerName = intent.getStringExtra("caller_name") ?: "Astrologer"
        val isFullScreenCall = intent.getBooleanExtra("is_full_screen_call", false)
        
        // Cancel the call notification immediately when activity starts
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(CALL_NOTIFICATION_ID)
        
        // If this is a full-screen call (from locked screen), show call UI first
        if (isFullScreenCall) {
            // Show the call UI layout
            setContentView(R.layout.activity_incoming_call)
            
            // Set caller name
            findViewById<android.widget.TextView>(R.id.tvCallerName).text = callerName
            
            // Handle Accept button
            findViewById<android.widget.Button>(R.id.btnAccept).setOnClickListener {
                // Show call in progress notification and launch consultation
                showCallInProgressNotification(callerName)
                launchConsultationScreen(callerName)
            }
            
            // Handle Decline button
            findViewById<android.widget.Button>(R.id.btnDecline).setOnClickListener {
                // Show missed call notification and finish
                showMissedCallNotification(callerName)
                finish()
            }
        } else {
            // Direct launch to consultation (from notification Accept button)
            // Show call in progress notification
            showCallInProgressNotification(callerName)
            launchConsultationScreen(callerName)
        }
    }
    
    private fun showCallInProgressNotification(callerName: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "call_in_progress_channel"
        
        // Create notification channel for call in progress (API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Call in Progress", NotificationManager.IMPORTANCE_LOW)
            channel.setSound(null, null) // No sound for call in progress
            channel.setShowBadge(false) // Don't show badge for call in progress
            notificationManager.createNotificationChannel(channel)
        }
        
        // End call action
        val endCallIntent = Intent(this, NotificationActionReceiver::class.java).apply {
            action = "com.vedazdemo.ACTION_END_CALL"
        }
        val endCallPendingIntent = PendingIntent.getBroadcast(
            this, 1, endCallIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val callInProgressNotification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("Call in progress")
            .setContentText("Consultation with $callerName")
            .setOngoing(true) // Persistent notification
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "End Call", endCallPendingIntent)
            .build()
        
        notificationManager.notify(CALL_IN_PROGRESS_NOTIFICATION_ID, callInProgressNotification)
    }
    
    private fun launchConsultationScreen(callerName: String) {
        // Create intent to launch React Native app with deep link to ConsultationScreen
        val pkg = applicationContext.packageName
        val launchIntent = applicationContext.packageManager.getLaunchIntentForPackage(pkg)
        launchIntent?.let {
            it.putExtra("callerName", callerName)
            it.putExtra("deepLink", "consultation")
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            applicationContext.startActivity(it)
        }
        
        // Finish this activity
        finish()
    }
    
    private fun showMissedCallNotification(callerName: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "missed_call_channel"
        
        // Create notification channel for missed calls (API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Missed Calls", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Missed Astro-Consultation Call")
            .setContentText("You missed a call from $callerName")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(1001, notification)
        
        // Send LocalBroadcastManager event for missed call
        val missedCallIntent = Intent("MISSED_CALL_EVENT")
        LocalBroadcastManager.getInstance(this).sendBroadcast(missedCallIntent)
        
        // Also send regular broadcast for backward compatibility
        val regularMissedCallIntent = Intent("com.vedazdemo.MISSED_CALL")
        sendBroadcast(regularMissedCallIntent)
    }
} 