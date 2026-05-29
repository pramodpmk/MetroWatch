package com.metrowatch.kochi.station.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.metrowatch.kochi.navigation.Route
import com.metrowatch.kochi.station.presentation.contact.ContactsRoute
import com.metrowatch.kochi.station.presentation.parking.ParkingRoute
import com.metrowatch.kochi.station.presentation.watermetro.WaterMetroStationsRoute
import com.metrowatch.kochi.station.presentation.watermetro.WaterMetroRoutesRoute
import com.metrowatch.kochi.station.presentation.metroroutes.MetroRoutesRoute
import com.metrowatch.kochi.station.presentation.detail.StationDetailRoute
import com.metrowatch.kochi.station.presentation.list.StationListRoute
import com.metrowatch.kochi.station.presentation.picker.StationPickerRoute
import com.metrowatch.kochi.station.presentation.plantrip.PlanTripRoute
import kotlinx.serialization.Serializable


sealed interface StationRoutes {
    @Serializable
    data object StationList : StationRoutes
    @Serializable
    data class StationDetails(val stationId: String) : StationRoutes
    @Serializable
    data object StationPicker : StationRoutes
    @Serializable
    data object Contacts : StationRoutes
    @Serializable
    data object PlanTrip : StationRoutes
    @Serializable
    data object WaterMetroStations : StationRoutes
    @Serializable
    data object WaterMetroRoutes : StationRoutes
    @Serializable
    data object MetroRoutes : StationRoutes
    @Serializable
    data object Parking : StationRoutes

}

fun NavGraphBuilder.stationsGraph(
    navController: NavHostController,
    onStationPickerResult: (() -> Unit)? = null
) {
    composable<StationRoutes.StationList> {
        StationListRoute(navController)
    }
    composable<StationRoutes.StationDetails> { backStackEntry ->
        StationDetailRoute(navController, backStackEntry)
    }
    composable<StationRoutes.StationPicker> { backStackEntry ->
        StationPickerRoute(
            navHostController = navController,
            backStackEntry = backStackEntry,
            onStationPickerResult = onStationPickerResult
        )
    }
    composable<StationRoutes.Contacts> {
        ContactsRoute(navController)
    }
    composable<StationRoutes.PlanTrip> {
        PlanTripRoute(
            navHostController = navController,
            onNavigate = { route ->
                when (route) {
                    is Route.StationPicker -> navController.navigate(StationRoutes.StationPicker)
                    else -> {}
                }
            }
        )
    }
    composable<StationRoutes.WaterMetroStations> {
        WaterMetroStationsRoute(navController)
    }
    composable<StationRoutes.WaterMetroRoutes> {
        WaterMetroRoutesRoute(navController)
    }
    composable<StationRoutes.MetroRoutes> {
        MetroRoutesRoute(navController)
    }
    composable<StationRoutes.Parking> {
        ParkingRoute(navController)
    }
}
