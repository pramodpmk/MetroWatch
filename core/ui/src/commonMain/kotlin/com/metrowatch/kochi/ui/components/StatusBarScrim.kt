package com.metrowatch.kochi.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Transparent-to-black gradient drawn behind the status bar so its icons stay legible
 * over any screen content. Rendered once above the root NavHost rather than per-screen.
 */
@Composable
fun StatusBarScrim(modifier: Modifier = Modifier) {
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(statusBarHeight + 32.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.35f), Color.Transparent)
                )
            )
    )
}
