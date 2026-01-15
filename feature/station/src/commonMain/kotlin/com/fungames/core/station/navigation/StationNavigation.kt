package com.fungames.core.station.navigation

import StationDetailRoute
import StationListRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable


sealed interface StationRoutes {
    @Serializable
    data object StationList : StationRoutes
    @Serializable
    data object StationDetails : StationRoutes

}

fun NavGraphBuilder.stationsGraph(navController: NavHostController) {
    composable<StationRoutes.StationList> {
        StationListRoute(navController)
    }
    composable<StationRoutes.StationDetails> {
        StationDetailRoute(navController)
    }
}
