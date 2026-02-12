package com.fungames.core.station.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.fungames.core.station.presentation.contact.ContactsRoute
import com.fungames.core.station.presentation.detail.StationDetailRoute
import com.fungames.core.station.presentation.list.StationListRoute
import com.fungames.core.station.presentation.picker.StationPickerRoute
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
}
