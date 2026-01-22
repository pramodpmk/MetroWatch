package com.fungames.core.station.presentation.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import com.fungames.core.navigation.LocalNavigationResultHandler
import com.fungames.core.navigation.NavigationResultHandler
import com.fungames.core.navigation.StationPickerResult
import com.fungames.core.station.domain.Station
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun StationPickerRoute(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry,
    onStationPickerResult: ((StationPickerResult) -> Unit)? = null,
    viewModel: StationPickerViewModel = koinViewModel()
) {
    val stationPickerState = viewModel.stationPickerState.collectAsState()
    // Try CompositionLocal first, then fallback to parameter (for flexibility)
    val localResultHandler = LocalNavigationResultHandler.current
    val resultHandler = localResultHandler ?: onStationPickerResult?.let { handler ->
        NavigationResultHandler { result ->
            if (result is StationPickerResult) {
                handler(result)
            }
        }
    }

    StationPickerScreen(
        stationPickerState = stationPickerState.value,
        onSearchQueryChange = { query -> viewModel.onSearchQueryChange(query) },
        navHostController = navHostController,
        onStationSelected = { station ->
            val result = StationPickerResult(
                id = station.id,
                name = station.name
            )
            // Return result to parent (CMP-safe, one-time event)
            resultHandler?.onResult(result)
            // Also call parameter callback if provided
            onStationPickerResult?.invoke(result)
            navHostController.popBackStack()
        }
    )
}

