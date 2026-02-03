package com.fungames.core.station.presentation.list

import com.fungames.core.ui.components.DisplayText
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
import com.fungames.core.station.domain.Station
import com.fungames.core.ui.components.AppScaffold
import com.fungames.core.ui.components.BrandToolBar
import com.fungames.core.ui.theme.BrandBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationListScreen(
    stationListState: StationListUi,
    navHostController: NavHostController,
    onSearchQueryChange: (String) -> Unit = {},
    onStationClick: (Station) -> Unit = {}
) {
    AppScaffold(
        toolBar = {
            BrandToolBar(
                title = "Stations",
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
                value = stationListState.searchQuery,
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

            Box(modifier = Modifier.fillMaxSize()) {
                if (stationListState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                } else if (stationListState.isError) {
                    DisplayText(
                        text = stationListState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(androidx.compose.ui.Alignment.Center).padding(16.dp)
                    )
                } else if (stationListState.filteredList.isEmpty()) {
                    DisplayText(
                        text = if (stationListState.searchQuery.isBlank()) {
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
                        items(stationListState.filteredList) { station ->
                            StationItem(
                                station = station,
                                onClick = { onStationClick(station) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StationItem(
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
                text = "Code: ${station.code}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
