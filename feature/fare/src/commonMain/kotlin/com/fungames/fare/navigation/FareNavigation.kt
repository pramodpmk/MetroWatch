package com.fungames.fare.navigation

import CalculateFareRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable


sealed interface FareRoutes {
    @Serializable
    data object CalculateFare : FareRoutes

}

fun NavGraphBuilder.fareGraph(navController: NavHostController) {
    composable<FareRoutes.CalculateFare> {
        CalculateFareRoute(navController)
    }
}
