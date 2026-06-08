package com.metrowatch.kochi.home.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.metrowatch.kochi.ui.AppLogger
import com.metrowatch.kochi.ui.components.AppScaffold
import com.metrowatch.kochi.home.location.rememberLocationPermissionLauncher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen(
    homeStateFlow: StateFlow<HomePageUi>,
    onIntent: (HomePageIntent) -> Unit
) {
    val homeState = homeStateFlow.collectAsState()

    val launchLocation = rememberLocationPermissionLauncher(
        onLocation = { lat, lon ->
            AppLogger.traceLog("onLocation")
            onIntent(HomePageIntent.LocationGranted(lat, lon))
        },
        onDenied = {
            AppLogger.traceLog("onDenied")
            onIntent(HomePageIntent.LocationDenied)
        }
    )

    val startLocationFetch = {
        AppLogger.traceLog("startLocationFetch")
        onIntent(HomePageIntent.LocationFetching)
        launchLocation()
    }

    LaunchedEffect(Unit) {
        startLocationFetch()
    }

    AppScaffold(
        toolBar = { },
        bottomBar = { },
    ) { paddingValues ->
        if (homeState.value.pageState == PageState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                HomeToolBar(
                    text = homeState.value.locationText,
                    onLocationClick = { startLocationFetch() }
                )

                Spacer(Modifier.height(12.dp))

                if (homeState.value.nearestStationAvailable) {
                    NearestStationSection(
                        station = homeState.value.nearestStation,
                        onStationClick = {
                            onIntent(HomePageIntent.ClickOnStation(homeState.value.nearestStation))
                        }
                    )
                    Spacer(Modifier.height(16.dp))
                }

                PlanJourneySection(
                    fromStation = if (homeState.value.nearestStationAvailable)
                        homeState.value.nearestStation.stationName else "",
                    onPlanJourney = { onIntent(HomePageIntent.PlanTrip) },
                    onPlanOnMap = { onIntent(HomePageIntent.PlanTrip) }
                )

                Spacer(Modifier.height(20.dp))

                QuickActionsSection(
                    onActionClick = { label ->
                        when (label) {
                            "Stations" -> onIntent(HomePageIntent.ViewAllStations)
                            "Routes" -> onIntent(HomePageIntent.MetroRoutes)
                            "Timings" -> onIntent(HomePageIntent.Timings)
                            "Fare" -> onIntent(HomePageIntent.FareCalculation)
                            "Parking" -> onIntent(HomePageIntent.Parking)
                        }
                    }
                )

                Spacer(Modifier.height(20.dp))

                WaterMetroSection(
                    onExplore = { onIntent(HomePageIntent.WaterMetroStations) }
                )

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
@Preview
fun PreviewHome() {
    HomeScreen(MutableStateFlow(HomePageUi.initData().copy(pageState = PageState.Success))) {}
}
