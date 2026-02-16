package com.fungames.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

actual fun platform() = "iOS"

@Composable
actual fun rememberPlatformActions(): PlatformActions {
    return remember {
        object : PlatformActions {
            override fun showToast(message: String) {
                // No-op on iOS
            }

            override fun exitApp() {
                // No-op on iOS
            }
        }
    }
}

@Composable
actual fun BackPressHandler(enabled: Boolean, onBack: () -> Unit) {
    // No-op on iOS
}

actual fun getTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000).toLong()
