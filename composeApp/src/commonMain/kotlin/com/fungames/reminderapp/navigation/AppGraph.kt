package com.fungames.reminderapp.navigation

import HomeRoute
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.fungames.core.navigation.Route
import com.fungames.core.station.navigation.StationRoutes
import com.fungames.fare.navigation.FareRoutes
import com.fungames.feature.timings.navigation.TimingRoutes
import com.fungames.home.navigation.HomeRoutes

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

    composable<Route.StationDetail> {
        LaunchedEffect(Unit) {
            navController.navigate(StationRoutes.StationDetails) {
                popUpTo<Route.StationDetail> {
                    inclusive = true
                }
            }
        }
    }

    composable<Route.FareCalculation> {
        LaunchedEffect(Unit) {
            navController.navigate(FareRoutes.CalculateFare) {
                popUpTo<FareRoutes.CalculateFare> {
                    inclusive = true
                }
            }
        }
    }

    composable<Route.Home> {
        LaunchedEffect(Unit) {
            navController.navigate(HomeRoutes.HomePage) {
                popUpTo<HomeRoutes.HomePage> {
                    inclusive = true
                }
            }
        }
    }
}