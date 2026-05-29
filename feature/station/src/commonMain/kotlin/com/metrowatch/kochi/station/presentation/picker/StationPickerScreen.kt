package com.metrowatch.kochi.station.presentation.picker

import com.metrowatch.kochi.ui.components.DisplayText
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.metrowatch.kochi.station.domain.Station
import com.metrowatch.kochi.ui.components.AppScaffold
import com.metrowatch.kochi.ui.components.BrandToolBar
import com.metrowatch.kochi.ui.theme.BrandBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationPickerScreen(
    stationPickerState: StationPickerUi,
    onSearchQueryChange: (String) -> Unit,
    navHostController: NavHostController,
    onStationSelected: (Station) -> Unit
) {
    AppScaffold(
        toolBar = {
            BrandToolBar(
                title = "Select station",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = { navHostController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Bar
            OutlinedTextField(
                value = stationPickerState.searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { DisplayText("Search stations...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = BrandBlue,
                    focusedLeadingIconColor = BrandBlue
                )
            )

            // Station List
            Box(modifier = Modifier.fillMaxSize()) {
                if (stationPickerState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(androidx.compose.ui.Alignment.Center)
                    )
                } else if (stationPickerState.isError) {
                    DisplayText(
                        text = stationPickerState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(androidx.compose.ui.Alignment.Center)
                            .padding(16.dp)
                    )
                } else if (stationPickerState.filteredStations.isEmpty()) {
                    DisplayText(
                        text = if (stationPickerState.searchQuery.isBlank()) {
                            "No stations available"
                        } else {
                            "No stations found"
                        },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(androidx.compose.ui.Alignment.Center)
                            .padding(16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(stationPickerState.filteredStations) { station ->
                            StationPickerItem(
                                station = station,
                                onClick = {
                                    onStationSelected(station)
                                    // Navigation back is handled in StationPickerRoute
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StationPickerItem(
    station: Station,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            DisplayText(
                text = station.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            DisplayText(
                text = "ID: ${station.id}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

