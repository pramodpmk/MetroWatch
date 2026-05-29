package com.metrowatch.kochi

import androidx.compose.ui.window.ComposeUIViewController
import com.metrowatch.kochi.di.initKoin

fun MainViewController() = ComposeUIViewController {
    initKoin()
    App()
}
