package com.vedazdemo

import android.content.Intent
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class EndCallModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    override fun getName(): String {
        return "EndCallModule"
    }

    @ReactMethod
    fun endCall() {
        // Send broadcast to NotificationActionReceiver
        val intent = Intent("com.vedazdemo.ACTION_END_CALL")
        reactApplicationContext.sendBroadcast(intent)
    }
} 