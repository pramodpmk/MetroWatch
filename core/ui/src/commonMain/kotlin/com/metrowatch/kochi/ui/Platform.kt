package com.metrowatch.kochi.ui

import androidx.compose.runtime.Composable

expect fun platform(): String

interface PlatformActions {
    fun showToast(message: String)
    fun exitApp()
}

@Composable
expect fun rememberPlatformActions(): PlatformActions

@Composable
expect fun BackPressHandler(enabled: Boolean = true, onBack: () -> Unit)

expect fun getTimeMillis(): Long

expect fun getLocalTime(): Pair<Int, Int>
