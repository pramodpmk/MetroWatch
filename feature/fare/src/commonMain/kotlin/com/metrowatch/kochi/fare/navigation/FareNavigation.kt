package com.metrowatch.kochi.fare.navigation

import com.metrowatch.kochi.fare.presentation.CalculateFareRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.metrowatch.kochi.navigation.Route
import com.metrowatch.kochi.fare.presentation.WaterMetroFareRoute
import kotlinx.serialization.Serializable


sealed interface FareRoutes {
    @Serializable
    data object CalculateFare : FareRoutes
    @Serializable
    data object WaterMetroFareRoute : FareRoutes

}

fun NavGraphBuilder.fareGraph(
    navController: NavHostController,
    onNavigate: (Route) -> Unit
) {
    composable<FareRoutes.CalculateFare> {
        CalculateFareRoute(navController, onNavigate)
    }
    composable<FareRoutes.WaterMetroFareRoute> {
        WaterMetroFareRoute(navController, onNavigate)
    }
}
