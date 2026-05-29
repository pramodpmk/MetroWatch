package com.metrowatch.kochi.station.presentation.picker

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.metrowatch.kochi.station.domain.Station
import com.metrowatch.kochi.ui.components.AppScaffold
import com.metrowatch.kochi.ui.components.BrandToolBar
import com.metrowatch.kochi.ui.components.DisplayText
import com.metrowatch.kochi.ui.theme.BrandBlue
import com.metrowatch.kochi.ui.theme.BrandWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StationPickerScreen(
    stationPickerState: StationPickerUi,
    onSearchQueryChange: (String) -> Unit,
    navHostController: NavHostController,
    onStationSelected: (Station) -> Unit
) {
    var isSearchActive by remember { mutableStateOf(false) }

    AppScaffold(
        toolBar = {
            if (isSearchActive) {
                SearchToolBar(
                    query = stationPickerState.searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onClose = {
                        isSearchActive = false
                        onSearchQueryChange("")
                    }
                )
            } else {
                BrandToolBar(
                    title = "Select station",
                    navigationIcon = Icons.Default.ArrowBack,
                    onNavigationClick = { navHostController.popBackStack() },
                    trailingIcon = Icons.Default.Search,
                    onTrailingClick = { isSearchActive = true }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (stationPickerState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (stationPickerState.isError) {
                DisplayText(
                    text = stationPickerState.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else if (stationPickerState.filteredStations.isEmpty()) {
                DisplayText(
                    text = if (stationPickerState.searchQuery.isBlank()) {
                        "No stations available"
                    } else {
                        "No stations found"
                    },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
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
                            onClick = { onStationSelected(station) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchToolBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandBlue)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .background(BrandWhite.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = BrandWhite),
            cursorBrush = SolidColor(BrandWhite),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box {
                    if (query.isEmpty()) {
                        Text(
                            text = "Search stations...",
                            color = BrandWhite.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    innerTextField()
                }
            }
        )

        IconButton(onClick = onClose) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close search",
                tint = BrandWhite
            )
        }
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }
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

