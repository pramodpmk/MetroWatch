package com.metrowatch.kochi.home.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.metrowatch.kochi.ui.components.AppScaffold
import com.metrowatch.kochi.ui.components.BrandToolBar
import com.metrowatch.kochi.ui.components.DisplayText

@Composable
fun WaterMetroHomeScreen(
    onIntent: (WaterMetroHomePageIntent) -> Unit
) {
    AppScaffold(
        toolBar = { },
        bottomBar = { }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            BrandToolBar(
                title = "Water Metro",
                navigationIcon = Icons.Default.DirectionsBoat
            )

            Spacer(Modifier.height(16.dp))
            DisplayText(
                text = "Water metro services",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            WaterMetroActionGrid(
                onActionClick = { label ->
                    when (label) {
                        "Stations" -> onIntent(WaterMetroHomePageIntent.Stations)
                        "Routes" -> onIntent(WaterMetroHomePageIntent.Routes)
                        "Timing" -> onIntent(WaterMetroHomePageIntent.Timing)
                        "Fare" -> onIntent(WaterMetroHomePageIntent.Fare)
                    }
                }
            )
        }
    }
}
