package com.fungames.fare.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.fungames.core.navigation.Route
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WaterMetroFareRoute(
    navHostController: NavHostController,
    onNavigate: (Route) -> Unit,
    viewModel: FareViewModel = koinViewModel()
) {
    WaterMetroTimingScreen(navHostController, onNavigate, viewModel)
}