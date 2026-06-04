package com.metrowatch.kochi.home.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.metrowatch.kochi.navigation.Route

@Composable
fun WaterMetroHomeRoute(
    onNavigate: (Route) -> Unit,
    viewModel: WaterMetroHomeViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.navigationEffect.collect { route ->
            onNavigate(route)
        }
    }
    WaterMetroHomeScreen(onIntent = viewModel::onIntent)
}
