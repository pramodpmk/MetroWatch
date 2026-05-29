package com.metrowatch.kochi.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

actual fun platform() = "Android"

@Composable
actual fun rememberPlatformActions(): PlatformActions {
    val context = LocalContext.current
    return remember(context) {
        object : PlatformActions {
            override fun showToast(message: String) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

            override fun exitApp() {
                (context as? Activity)?.finish()
            }
        }
    }
}

@Composable
actual fun BackPressHandler(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled, onBack)
}

actual fun getTimeMillis(): Long = android.os.SystemClock.elapsedRealtime()

actual fun getLocalTime(): Pair<Int, Int> {
    val calendar = java.util.Calendar.getInstance()
    return calendar.get(java.util.Calendar.HOUR_OF_DAY) to calendar.get(java.util.Calendar.MINUTE)
}
