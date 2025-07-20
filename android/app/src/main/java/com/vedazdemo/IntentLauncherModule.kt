package com.vedazdemo

import android.app.Activity
import android.content.Intent
import com.facebook.react.bridge.*
import com.facebook.react.module.annotations.ReactModule

@ReactModule(name = IntentLauncherModule.NAME)
class IntentLauncherModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    companion object {
        const val NAME = "IntentLauncher"
    }

    override fun getName(): String = NAME

    @ReactMethod
    fun getInitialIntent(promise: Promise) {
        val activity: Activity? = currentActivity
        val intent: Intent? = activity?.intent
        val extras = Arguments.createMap()
        intent?.extras?.keySet()?.forEach { key ->
            val value = intent.extras?.get(key)
            when (value) {
                is String -> extras.putString(key, value)
                is Int -> extras.putInt(key, value)
                is Boolean -> extras.putBoolean(key, value)
                is Double -> extras.putDouble(key, value)
                // Add more types if needed
            }
        }
        val result = Arguments.createMap()
        result.putMap("extras", extras)
        promise.resolve(result)
    }
} 