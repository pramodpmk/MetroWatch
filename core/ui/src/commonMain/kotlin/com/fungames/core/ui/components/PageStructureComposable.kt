package com.fungames.core.ui.components

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

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

