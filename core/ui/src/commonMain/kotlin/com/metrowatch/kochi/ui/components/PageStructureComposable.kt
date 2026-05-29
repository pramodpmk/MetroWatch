package com.metrowatch.kochi.ui.components

import com.metrowatch.kochi.ui.components.DisplayText
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppScaffold(
    toolBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = toolBar,
        bottomBar = bottomBar,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        content(paddingValues)
    }
}

@Composable
fun HomeBottomBar() {
    Row {
        DisplayText(text = "Home", modifier = Modifier.weight(1f))
        DisplayText(text = "Timing", modifier = Modifier.weight(1f))
        DisplayText(text = "Fare", modifier = Modifier.weight(1f))
        DisplayText(text = "Settings", modifier = Modifier.weight(1f))
    }
}