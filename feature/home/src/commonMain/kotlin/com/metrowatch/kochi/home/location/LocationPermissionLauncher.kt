package com.metrowatch.kochi.home.location

import androidx.compose.runtime.Composable

@Composable
expect fun rememberLocationPermissionLauncher(
    onLocation: (lat: Double, lon: Double) -> Unit,
    onDenied: () -> Unit
): () -> Unit
