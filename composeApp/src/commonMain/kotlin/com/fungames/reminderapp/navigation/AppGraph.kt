package com.fungames.reminderapp.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.fungames.core.navigation.Route
import com.fungames.core.station.navigation.StationRoutes
import com.fungames.feature.timings.navigation.TimingRoutes

fun NavGraphBuilder.appGraph(
    navController: NavHostController
) {
    composable<Route.Timings> {
        navController.navigate(TimingRoutes.Timings) {
            popUpTo<Route.Timings> {
                inclusive = true
            }
        }
    }

    composable<Route.StationList> {
        LaunchedEffect(Unit) {
            navController.navigate(StationRoutes.StationList) {
                popUpTo<Route.StationList> {
                    inclusive = true
                }
            }
        }
    }
}