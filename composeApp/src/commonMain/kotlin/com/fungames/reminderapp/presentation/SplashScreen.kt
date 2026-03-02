package com.fungames.reminderapp.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fungames.core.ui.components.DisplayText
import com.fungames.core.ui.theme.BrandBlue
import com.fungames.core.ui.theme.BrandWhite

@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onSyncComplete: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.navigateToHome.collect {
            onSyncComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBlue),
        contentAlignment = Alignment.Center
    ) {
        DisplayText(
            text = "Metro app",
            color = BrandWhite,
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
