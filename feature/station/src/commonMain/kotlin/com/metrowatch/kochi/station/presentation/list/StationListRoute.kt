package com.metrowatch.kochi.station.presentation.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import com.metrowatch.kochi.station.navigation.StationRoutes
import com.metrowatch.kochi.station.presentation.StationViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun StationListRoute(
    navHostController: NavHostController,
    viewModel: StationViewModel = koinViewModel()
) {

    val stationListState = viewModel.stationListState.collectAsState()

    StationListScreen(
        stationListState = stationListState.value,
        navHostController = navHostController,
        onSearchQueryChange = { query ->
            viewModel.onSearchQueryChange(query)
        },
        onStationClick = { station ->
            navHostController.navigate(StationRoutes.StationDetails(station.id))
        }
    )
}
