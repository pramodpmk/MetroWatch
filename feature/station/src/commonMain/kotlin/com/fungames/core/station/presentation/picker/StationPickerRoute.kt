package com.fungames.core.station.presentation.picker

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.fungames.core.station.domain.Station
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun StationPickerRoute(
    navHostController: NavHostController,
    viewModel: StationPickerViewModel = koinViewModel()
) {
    val stationPickerState = viewModel.stationPickerState.collectAsState()

    StationPickerScreen(
        stationPickerState = stationPickerState.value,
        onSearchQueryChange = { query -> viewModel.onSearchQueryChange(query) },
        navHostController = navHostController,
        onStationSelected = { stationSelected ->

        }
    )
}

