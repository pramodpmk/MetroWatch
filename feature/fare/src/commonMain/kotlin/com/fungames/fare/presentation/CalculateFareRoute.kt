package com.fungames.fare.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fungames.core.navigation.Route
import com.fungames.fare.presentation.FareViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun CalculateFareRoute(
    navHostController: NavHostController,
    onNavigate: (Route) -> Unit,
    viewModel: FareViewModel = koinViewModel()
) {
    FareCalculatorScreen(navHostController, onNavigate, viewModel)
}