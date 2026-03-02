package com.fungames.core.station.presentation.plantrip

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.fungames.core.navigation.Route
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun PlanTripRoute(
    navHostController: NavHostController,
    onNavigate: (Route) -> Unit,
    viewModel: PlanTripViewModel = koinViewModel()
) {
    PlanTripScreen(navHostController, onNavigate, viewModel)
}
