package com.metrowatch.kochi.timings.presentation.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.metrowatch.kochi.navigation.LocalNavigationResults
import com.metrowatch.kochi.navigation.Route
import com.metrowatch.kochi.navigation.result.NavigationKeys
import com.metrowatch.kochi.ui.components.AppScaffold
import com.metrowatch.kochi.ui.components.BrandToolBar
import com.metrowatch.kochi.ui.components.DisplayText
import com.metrowatch.kochi.ui.theme.BrandBlue
import com.metrowatch.kochi.ui.theme.LightBlueBg
import com.metrowatch.kochi.timings.domain.TrainTiming
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
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

    AppScaffold(
        toolBar = {
            BrandToolBar(
                title = "Train timings",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = { navHostController.popBackStack() }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                TimingCalculationCard(
                    uiState = uiState,
                    onFromClick = {
                        viewModel.setPickingFrom(true)
                        onNavigate(Route.StationPicker)
                    },
                    onToClick = {
                        viewModel.setPickingFrom(false)
                        onNavigate(Route.StationPicker)
                    },
                    onSwapStations = { viewModel.swapStations() },
                    onCalculateTimings = { viewModel.calculateTimings() }
                )
            }

            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BrandBlue)
                    }
                }
            }

            if (uiState.error != null) {
                item {
                    DisplayText(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 24.dp)
                    )
                }
            }

            if (uiState.showDetails) {
                item {
                    DisplayText(
                        text = "Available trains",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = BrandBlue,
                        modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
                    )
                }

                items(uiState.timings, key = { it.trainNumber }) { timing ->
                    TrainTimingItem(timing)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun TimingCalculationCard(
    uiState: TimingDetailUiState,
    onFromClick: () -> Unit,
    onToClick: () -> Unit,
    onSwapStations: () -> Unit,
    onCalculateTimings: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LightBlueBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Surface(
                shape = RoundedCornerShape(topStart = 24.dp, bottomEnd = 24.dp),
                color = Color.White
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Find timings",
                        color = BrandBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "From", fontSize = 10.sp, color = Color.Gray)
                    Text(text = "To", fontSize = 10.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (uiState.fromStation.isEmpty()) "Select station" else uiState.fromStation,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.fromStation.isEmpty()) Color.Gray else BrandBlue,
                        modifier = Modifier.weight(1f).clickable { onFromClick() }
                    )

                    IconButton(onClick = onSwapStations) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Swap",
                            tint = BrandBlue
                        )
                    }

                    Text(
                        text = if (uiState.toStation.isEmpty()) "Select station" else uiState.toStation,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.toStation.isEmpty()) Color.Gray else BrandBlue,
                        modifier = Modifier.weight(1f).clickable { onToClick() },
                        textAlign = TextAlign.End
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onCalculateTimings,
                    enabled = uiState.fromStation.isNotEmpty() && uiState.toStation.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Text(text = "Calculate timings", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TrainTimingItem(timing: TrainTiming) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    DisplayText(
                        text = timing.trainName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    DisplayText(
                        text = "Train #${timing.trainNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = LightBlueBg
                ) {
                    DisplayText(
                        text = timing.duration,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = BrandBlue,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFE0E0E0))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    DisplayText(
                        text = timing.departureTime,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    DisplayText(text = "Departure", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }

                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = null,
                    tint = Color(0xFFE0E0E0),
                    modifier = Modifier.size(24.dp)
                )

                Column(horizontalAlignment = Alignment.End) {
                    DisplayText(
                        text = timing.arrivalTime,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    DisplayText(text = "Arrival", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
        }
    }
}
