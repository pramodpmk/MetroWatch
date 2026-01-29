package com.fungames.core.ui.components

import com.fungames.core.ui.components.DisplayText
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AppScaffold(
    toolBar: @Composable () -> Unit,
    bottomBar: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    Scaffold(
        topBar = toolBar,
        bottomBar = bottomBar,
    ) {
        paddingValues ->
        content()
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