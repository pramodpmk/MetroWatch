package com.fungames.reminderapp

import androidx.compose.ui.window.ComposeUIViewController
import com.fungames.reminderapp.data.DatabaseDriverFactory
import com.fungames.reminderapp.di.initKoin

fun MainViewController() = ComposeUIViewController {
    initKoin(DatabaseDriverFactory())
    App()
}
