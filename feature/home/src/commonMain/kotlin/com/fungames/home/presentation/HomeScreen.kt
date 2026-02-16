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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.fungames.core.ui.components.AppScaffold
import com.fungames.core.ui.components.DisplayText
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen(
    homeStateFlow: StateFlow<HomePageUi>,
    onIntent: (HomePageIntent) -> Unit
) {

    val homeState = homeStateFlow.collectAsState()

    AppScaffold(
        toolBar = { },
        bottomBar = { }, // Bottom bar to included in app module
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
                    onLocationClick = { onIntent(HomePageIntent.ClickedOnLocation) }
                )

                NearestStationSection(
                    station = homeState.value.nearestStation,
                    onStationClick = { onIntent(HomePageIntent.ClickOnStation(homeState.value.nearestStation)) }
                )

                ActionGrid(
                    onActionClick = { label ->
                        when (label) {
                            "Stations" -> onIntent(HomePageIntent.ViewAllStations)
                            "Fare" -> onIntent(HomePageIntent.FareCalculation)
                            "Timing" -> onIntent(HomePageIntent.Timings)
                            "Settings" -> onIntent(HomePageIntent.Settings)
                            "Plan Trip" -> onIntent(HomePageIntent.PlanTrip)
                        }
                    }
                )

                Spacer(Modifier.height(16.dp))
                DisplayText(
                    "Water Metro", style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                WaterMetroActionGrid(
                    onActionClick = { label ->
                        when (label) {
                            "Stations" -> onIntent(HomePageIntent.ViewAllStations)
                            "Fare" -> onIntent(HomePageIntent.FareCalculation)
                            "Timing" -> onIntent(HomePageIntent.Timings)
                            "Settings" -> onIntent(HomePageIntent.Settings)
                            "Plan Trip" -> onIntent(HomePageIntent.PlanTrip)
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
    val viewModel = HomeViewModel()
    HomeScreen(viewModel.homeState) {}
}