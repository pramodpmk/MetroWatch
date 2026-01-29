package com.fungames.home.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.fungames.core.ui.components.AppScaffold
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
        bottomBar = { }, // TODO : Bottom bar to include n app module
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
                            "Station List" -> onIntent(HomePageIntent.ViewAllStations)
                            "Fare Calculator" -> onIntent(HomePageIntent.FareCalculation)
                            "Timing Table" -> onIntent(HomePageIntent.Timings)
                            "Settings" -> onIntent(HomePageIntent.Settings)
                            // Add more mappings as intents are added
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