package com.fungames.core.station.presentation.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.fungames.core.navigation.LocalNavigationResults
import com.fungames.core.navigation.result.NavigationKeys
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun StationPickerRoute(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry,
    onStationPickerResult: (() -> Unit)? = null,
    viewModel: StationPickerViewModel = koinViewModel()
) {
    val navigationResults = LocalNavigationResults.current
    val stationPickerState = viewModel.stationPickerState.collectAsState()
    // Try CompositionLocal first, then fallback to parameter (for flexibility)

    StationPickerScreen(
        stationPickerState = stationPickerState.value,
        onSearchQueryChange = { query -> viewModel.onSearchQueryChange(query) },
        navHostController = navHostController,
        onStationSelected = { station ->
            navigationResults.set(
                NavigationKeys.STATION_PICKER_RESULT,
                station.name
            )
            // TODO : Set the navigation result here
            //onStationPickerResult?.invoke(result)
            navHostController.popBackStack()
        }
    )
}

