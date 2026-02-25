package com.fungames.home.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fungames.core.ui.components.AppScaffold
import com.fungames.core.ui.components.DisplayText
import com.fungames.home.location.rememberLocationPermissionLauncher
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
        onLocation = { lat, lon -> onIntent(HomePageIntent.LocationGranted(lat, lon)) },
        onDenied = { onIntent(HomePageIntent.LocationDenied) }
    )

    AppScaffold(
        toolBar = { },
        bottomBar = { },
    ) { paddingValues ->
        if (homeState.value.pageState == PageState.Loading) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator()
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
                    onLocationClick = { launchLocation() }
                )

                if (homeState.value.nearestStationAvailable) {
                    NearestStationSection(
                        station = homeState.value.nearestStation,
                        onStationClick = { onIntent(HomePageIntent.ClickOnStation(homeState.value.nearestStation)) }
                    )
                }

                ActionGrid(
                    onActionClick = { label ->
                        when (label) {
                            "Stations" -> onIntent(HomePageIntent.ViewAllStations)
                            "Fare" -> onIntent(HomePageIntent.FareCalculation)
                            "Timing" -> onIntent(HomePageIntent.Timings)
                            "Settings" -> onIntent(HomePageIntent.Settings)
                            "Plan trip" -> onIntent(HomePageIntent.PlanTrip)
                            "Parking" -> onIntent(HomePageIntent.Parking)
                            "Contacts" -> onIntent(HomePageIntent.Contacts)
                        }
                    }
                )

                Spacer(Modifier.height(16.dp))
                DisplayText(
                    "Water metro", style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                WaterMetroActionGrid(
                    onActionClick = { label ->
                        when (label) {
                            "Stations" -> onIntent(HomePageIntent.WaterMetroStations)
                            "Routes" -> onIntent(HomePageIntent.WaterMetroRoutes)
                            "Fare" -> onIntent(HomePageIntent.FareCalculation)
                            "Timing" -> onIntent(HomePageIntent.Timings)
                        }
                    }
                )
            }
        }
    }
}

@Composable
@Preview
fun PreviewHome() {
    HomeScreen(MutableStateFlow(HomePageUi.initData().copy(pageState = PageState.Success))) {}
}
