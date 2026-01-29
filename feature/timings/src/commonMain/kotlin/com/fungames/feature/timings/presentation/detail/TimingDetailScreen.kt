package com.fungames.feature.timings.presentation.detail

import com.fungames.core.ui.components.DisplayText
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fungames.core.navigation.LocalNavigationResults
import com.fungames.core.navigation.Route
import com.fungames.core.navigation.result.NavigationKeys
import com.fungames.feature.timings.domain.TrainTiming
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3Api::class)
@Composable
fun TrainTimingDetail(
    navHostController: NavHostController,
    onNavigate: (Route) -> Unit,
    viewModel: TimingDetailViewModel = koinViewModel()
) {
    val navigationResults = LocalNavigationResults.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(navigationResults.version) {
        navigationResults
            .consume<String>(NavigationKeys.STATION_PICKER_RESULT)
            ?.let { stationName ->
                viewModel.updateStation(stationName)
            }
    }

    DisposableEffect(Unit) {
        onDispose {
            navigationResults.clearResult(NavigationKeys.STATION_PICKER_RESULT)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { DisplayText("Train Timings") },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            StationPickerCard(
                fromStation = uiState.fromStation,
                toStation = uiState.toStation,
                onFromClick = {
                    viewModel.setPickingFrom(true)
                    onNavigate(Route.StationPicker)
                },
                onToClick = {
                    viewModel.setPickingFrom(false)
                    onNavigate(Route.StationPicker)
                },
                onSwapClick = { viewModel.swapStations() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            DisplayText(
                text = "Available Trains",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(16.dp))

            uiState.timings.forEach { timing ->
                TrainTimingItem(timing)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun StationPickerCard(
    fromStation: String,
    toStation: String,
    onFromClick: () -> Unit,
    onToClick: () -> Unit,
    onSwapClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                StationSelector(
                    label = "From",
                    stationName = fromStation,
                    onClick = onFromClick
                )
                Spacer(modifier = Modifier.height(16.dp))
                StationSelector(
                    label = "To",
                    stationName = toStation,
                    onClick = onToClick
                )
            }

            TextButton(
                onClick = onSwapClick,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                DisplayText(
                    text = "Swap",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun StationSelector(
    label: String,
    stationName: String,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.clickable(onClick = onClick)) {
        DisplayText(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        DisplayText(
            text = stationName,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
        )
        HorizontalDivider(
            modifier = Modifier.padding(top = 4.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
fun TrainTimingItem(timing: TrainTiming) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DisplayText(
                    text = timing.trainName,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                DisplayText(
                    text = "#${timing.trainNumber}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    DisplayText(text = timing.departureTime, style = MaterialTheme.typography.bodyLarge)
                    DisplayText(text = "Departure", style = MaterialTheme.typography.labelSmall)
                }

                DisplayText(
                    text = timing.duration,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Column(horizontalAlignment = Alignment.End) {
                    DisplayText(text = timing.arrivalTime, style = MaterialTheme.typography.bodyLarge)
                    DisplayText(text = "Arrival", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
