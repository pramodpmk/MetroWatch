package com.fungames.trip.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.fungames.core.navigation.Route
import com.fungames.trip.presentation.PlanTripScreen
import kotlinx.serialization.Serializable

sealed interface TripRoutes {
    @Serializable
    data object PlanTrip : TripRoutes
}

fun NavGraphBuilder.tripGraph(
    navController: NavHostController,
    onNavigate: (Route) -> Unit
) {
    composable<TripRoutes.PlanTrip> {
        PlanTripScreen(
            navController = navController,
            onNavigate = onNavigate
        )
    }
}
