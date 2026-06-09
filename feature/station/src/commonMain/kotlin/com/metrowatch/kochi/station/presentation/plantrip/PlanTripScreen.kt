package com.metrowatch.kochi.station.presentation.plantrip

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.metrowatch.kochi.navigation.LocalNavigationResults
import com.metrowatch.kochi.navigation.Route
import com.metrowatch.kochi.navigation.result.NavigationKeys
import com.metrowatch.kochi.station.domain.TripTiming
import com.metrowatch.kochi.ui.components.DisplayText
import com.metrowatch.kochi.ui.theme.BrandBlue
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun PlanTripScreen(
    navController: NavHostController,
    onNavigate: (Route) -> Unit,
    viewModel: PlanTripViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigationResults = LocalNavigationResults.current

    LaunchedEffect(Unit) {
        val fromName = navigationResults.consume<String>(NavigationKeys.PLAN_TRIP_FROM_NAME)
            ?: return@LaunchedEffect
        val fromId = navigationResults.consume<String>(NavigationKeys.PLAN_TRIP_FROM_ID) ?: ""
        val toName = navigationResults.consume<String>(NavigationKeys.PLAN_TRIP_TO_NAME)
            ?: return@LaunchedEffect
        val toId = navigationResults.consume<String>(NavigationKeys.PLAN_TRIP_TO_ID) ?: ""
        viewModel.prefillAndCalculate(fromName, fromId, toName, toId)
    }

    LaunchedEffect(navigationResults.version) {
        val stationName = navigationResults.consume<String>(NavigationKeys.STATION_PICKER_RESULT)
        val stationId = navigationResults.consume<String>(NavigationKeys.STATION_PICKER_ID_RESULT)
        if (stationName != null) {
            viewModel.updateStation(stationName, stationId ?: "")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            navigationResults.clearResult(NavigationKeys.STATION_PICKER_RESULT)
            navigationResults.clearResult(NavigationKeys.STATION_PICKER_ID_RESULT)
        }
    }

    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val blueAreaHeight = statusBarHeight + 120.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(blueAreaHeight)
                            .background(BrandBlue)
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(WindowInsets.statusBars)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 4.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                            Column {
                                Text(
                                    text = "Plan trip",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = "Find the best route between stations",
                                    color = Color.White.copy(alpha = 0.8f),
                                    fontSize = 12.sp
                                )
                            }
                        }

                        TripInputCard(
                            uiState = uiState,
                            onDepartureClick = {
                                viewModel.setPickingDeparture(true)
                                onNavigate(Route.StationPicker)
                            },
                            onArrivalClick = {
                                viewModel.setPickingDeparture(false)
                                onNavigate(Route.StationPicker)
                            },
                            onSwapStations = { viewModel.swapStations() },
                            onPlanTrip = { viewModel.calculateTrip() }
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
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
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            if (uiState.showDetails) {
                item {
                    TripSummarySection(
                        uiState = uiState,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                item {
                    AvailableTrainsHeader(
                        lastUpdatedTime = uiState.lastUpdatedTime,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
                items(uiState.timings, key = { it.trainNumber }) { timing ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        TripTimingCard(timing = timing)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(0.dp)) }
        }
    }
}

@Composable
private fun TripInputCard(
    uiState: PlanTripUiState,
    onDepartureClick: () -> Unit,
    onArrivalClick: () -> Unit,
    onSwapStations: () -> Unit,
    onPlanTrip: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(BrandBlue.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Your journey",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onDepartureClick() }
                ) {
                    Text(text = "From", fontSize = 11.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (uiState.departureStation.isEmpty()) "Select" else uiState.departureStation,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.departureStation.isEmpty()) Color.LightGray else Color.Black,
                        maxLines = 1
                    )
                    if (uiState.departureStationId.isNotEmpty()) {
                        Text(
                            text = uiState.departureStationId.uppercase(),
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(BrandBlue, CircleShape)
                        .clickable { onSwapStations() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Swap stations",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onArrivalClick() },
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "To",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (uiState.arrivalStation.isEmpty()) "Select" else uiState.arrivalStation,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.arrivalStation.isEmpty()) Color.LightGray else Color.Black,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (uiState.arrivalStationId.isNotEmpty()) {
                        Text(
                            text = uiState.arrivalStationId.uppercase(),
                            fontSize = 11.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onPlanTrip,
                enabled = uiState.departureStation.isNotEmpty() && uiState.arrivalStation.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
            ) {
                Text(
                    text = "Plan trip",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun TripSummarySection(uiState: PlanTripUiState, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Trip summary",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.Black
            )
            Text(
                text = "View route >",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = BrandBlue
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricChip(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.LocationOn,
                value = uiState.distance,
                label = "Distance"
            )
            MetricChip(
                modifier = Modifier.weight(1f),
                iconText = "₹",
                value = uiState.fare,
                label = "Fare"
            )
            MetricChip(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Train,
                value = "${uiState.numberOfStations}",
                label = "Stations"
            )
            LineChip(
                modifier = Modifier.weight(1f),
                lineName = uiState.lineName
            )
        }
    }
}

@Composable
private fun MetricChip(
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconText: String? = null,
    value: String,
    label: String
) {
    Column(
        modifier = modifier
            .background(BrandBlue.copy(alpha = 0.06f), RoundedCornerShape(12.dp))
            .padding(horizontal = 6.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(BrandBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            } else if (iconText != null) {
                Text(
                    text = iconText,
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LineChip(
    modifier: Modifier = Modifier,
    lineName: String
) {
    Column(
        modifier = modifier
            .background(BrandBlue.copy(alpha = 0.06f), RoundedCornerShape(12.dp))
            .padding(horizontal = 6.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(0xFFFF6B35), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = lineName.take(2).uppercase(),
                color = Color.White,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = lineName,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Text(
            text = "Line",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AvailableTrainsHeader(lastUpdatedTime: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Available trains",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = Color.Black
        )
        if (lastUpdatedTime.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = "Last updated: $lastUpdatedTime",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TripTimingCard(timing: TripTiming) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(BrandBlue.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Train,
                            contentDescription = null,
                            tint = BrandBlue,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Metro Train",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(BrandBlue.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = timing.duration,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = BrandBlue
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = timing.departureTime,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Departure",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = timing.arrivalTime,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Arrival",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
