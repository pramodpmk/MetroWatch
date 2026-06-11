package com.metrowatch.kochi.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.metrowatch.kochi.ui.theme.BrandWhite
import org.jetbrains.compose.resources.painterResource
import reminderapp.composeapp.generated.resources.Res
import reminderapp.composeapp.generated.resources.metromate_logo

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
            .background(BrandWhite),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.metromate_logo),
            contentDescription = "metro-mate-logo",
            modifier = Modifier.size(192.dp)
        )
    }
}
