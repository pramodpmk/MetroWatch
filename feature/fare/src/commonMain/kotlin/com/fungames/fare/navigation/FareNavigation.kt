package com.fungames.fare.navigation

import com.fungames.fare.presentation.CalculateFareRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.fungames.core.navigation.Route
import com.fungames.fare.presentation.WaterMetroFareRoute
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
