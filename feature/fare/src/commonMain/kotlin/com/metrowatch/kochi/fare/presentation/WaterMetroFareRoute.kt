package com.metrowatch.kochi.fare.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.metrowatch.kochi.navigation.Route
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WaterMetroFareRoute(
    navHostController: NavHostController,
    onNavigate: (Route) -> Unit,
    viewModel: FareViewModel = koinViewModel()
) {
    WaterMetroTimingScreen(navHostController, onNavigate, viewModel)
}