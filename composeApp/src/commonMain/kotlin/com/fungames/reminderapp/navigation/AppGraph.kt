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
        LaunchedEffect(Unit) {
            navController.navigate(TimingRoutes.Timings) {
                popUpTo<Route.Timings> {
                    inclusive = true
                }
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
                popUpTo<Route.FareCalculation> {
                    inclusive = true
                }
            }
        }
    }

    composable<Route.Home> {
        LaunchedEffect(Unit) {
            navController.navigate(HomeRoutes.HomePage) {
                popUpTo<Route.Home> {
                    inclusive = true
                }
            }
        }
    }

    composable<Route.PlanTrip> {
        LaunchedEffect(Unit) {
            navController.navigate(StationRoutes.PlanTrip) {
                popUpTo<Route.PlanTrip> {
                    inclusive = true
                }
            }
        }
    }

    composable<Route.StationPicker> {
        LaunchedEffect(Unit) {
            navController.navigate(StationRoutes.StationPicker) {
                popUpTo<Route.StationPicker> {
                    inclusive = true
                }
            }
        }
    }

    composable<Route.WaterMetroStations> {
        LaunchedEffect(Unit) {
            navController.navigate(StationRoutes.WaterMetroStations) {
                popUpTo<Route.WaterMetroStations> {
                    inclusive = true
                }
            }
        }
    }

    composable<Route.WaterMetroRoutes> {
        LaunchedEffect(Unit) {
            navController.navigate(StationRoutes.WaterMetroRoutes) {
                popUpTo<Route.WaterMetroRoutes> {
                    inclusive = true
                }
            }
        }
    }

    composable<Route.Parking> {
        LaunchedEffect(Unit) {
            navController.navigate(StationRoutes.Parking) {
                popUpTo<Route.Parking> {
                    inclusive = true
                }
            }
        }
    }

    composable<Route.Contacts> {
        LaunchedEffect(Unit) {
            navController.navigate(StationRoutes.Contacts) {
                popUpTo<Route.Contacts> {
                    inclusive = true
                }
            }
        }
    }
}