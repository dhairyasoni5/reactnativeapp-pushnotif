package com.vedazdemo

import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate
import android.content.Intent
import com.facebook.react.modules.core.DeviceEventManagerModule
import android.util.Log

class MainActivity : ReactActivity() {

  /**
   * Returns the name of the main component registered from JavaScript.
   */
  override fun getMainComponentName(): String = "VedazDemo"

  /**
   * Returns the instance of the [ReactActivityDelegate].
   */
  override fun createReactActivityDelegate(): ReactActivityDelegate =
      DefaultReactActivityDelegate(this, mainComponentName, fabricEnabled)

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)
    setIntent(intent)
    Log.d("MainActivity", "onNewIntent called with extras: ${intent?.extras}")
    // Notify JS of new intent
    val reactInstanceManager = (application as? com.facebook.react.ReactApplication)?.reactNativeHost?.reactInstanceManager
    val currentReactContext = reactInstanceManager?.currentReactContext
    currentReactContext?.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      ?.emit("onNewIntent", null)
  }
}
