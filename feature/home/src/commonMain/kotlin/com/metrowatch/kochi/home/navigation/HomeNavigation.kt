package com.metrowatch.kochi.home.navigation

import HomeRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.metrowatch.kochi.home.presentation.WaterMetroHomeRoute
import com.metrowatch.kochi.navigation.Route
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI


sealed interface HomeRoutes {
    @Serializable
    data object HomePage : HomeRoutes

    @Serializable
    data object WaterMetroHomePage : HomeRoutes
}

@OptIn(KoinExperimentalAPI::class)
fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    onNavigate: (Route) -> Unit
    ) {
    composable<HomeRoutes.HomePage> {
        HomeRoute(
            navController,
            onNavigate,
            koinViewModel()
        )
    }
}

@OptIn(KoinExperimentalAPI::class)
fun NavGraphBuilder.waterMetroHomeGraph(
    onNavigate: (Route) -> Unit
) {
    composable<HomeRoutes.WaterMetroHomePage> {
        WaterMetroHomeRoute(
            onNavigate = onNavigate,
            viewModel = koinViewModel()
        )
    }
}
