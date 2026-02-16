package com.fungames.trip.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.navigation.NavHostController
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fungames.core.navigation.LocalNavigationResults
import com.fungames.core.navigation.Route
import com.fungames.core.navigation.result.NavigationKeys
import com.fungames.core.ui.components.AppScaffold
import com.fungames.core.ui.components.BrandToolBar
import com.fungames.core.ui.theme.BrandBlue
import com.fungames.core.ui.theme.LightBlueBg
import com.fungames.core.ui.theme.NearestStationLabelColor
import com.fungames.feature.timings.domain.TrainTiming
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun PlanTripScreen(
    navController: NavHostController,
    onNavigate: (Route) -> Unit,
    viewModel: TripViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigationResults = LocalNavigationResults.current

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
                title = "Plan Trip",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                TripCalculationCard(
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
                    onPlanTrip = { viewModel.planTrip() }
                )
            }

            if (uiState.isLoading) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    CircularProgressIndicator(color = BrandBlue)
                }
            }

            if (uiState.error != null) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = uiState.error!!, color = Color.Red)
                }
            }

            if (uiState.showDetails && uiState.tripDetails != null) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    TripDetailsSection(uiState.tripDetails!!)
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "TRAIN TIMINGS",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlue,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                }

                items(uiState.tripDetails!!.timings) { timing ->
                    TimingItem(timing)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun TripCalculationCard(
    uiState: TripUiState,
    onDepartureClick: () -> Unit,
    onArrivalClick: () -> Unit,
    onSwapStations: () -> Unit,
    onPlanTrip: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = LightBlueBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    shape = RoundedCornerShape(topStart = 24.dp, bottomEnd = 24.dp),
                    color = Color.White
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "PLAN YOUR TRIP",
                            color = BrandBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "DEPARTURE", fontSize = 10.sp, color = Color.Gray)
                    Text(text = "ARRIVAL", fontSize = 10.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = uiState.departureStation,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlue,
                        modifier = Modifier.weight(1f).clickable { onDepartureClick() }
                    )

                    IconButton(onClick = onSwapStations) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Swap",
                            tint = BrandBlue
                        )
                    }

                    Text(
                        text = uiState.arrivalStation,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandBlue,
                        modifier = Modifier.weight(1f).clickable { onArrivalClick() },
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onPlanTrip,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Text(text = "PLAN TRIP", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TripDetailsSection(details: com.fungames.trip.domain.TripDetails) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "TRIP DETAILS",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = BrandBlue,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailRow(icon = Icons.Default.LocationOn, label = "Distance", value = details.distance, iconColor = Color(0xFF4CAF50))
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))
                DetailRow(icon = Icons.Default.Payments, label = "Fare", value = details.fare, iconColor = Color(0xFFFF9800))
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))
                DetailRow(icon = Icons.Default.Subway, label = "Intermediate Stations", value = "${details.stationsCount}", iconColor = Color(0xFF2196F3))
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFEEEEEE))
                DetailRow(icon = Icons.Default.LinearScale, label = "Line", value = details.lineName, iconColor = Color(0xFF9C27B0))
            }
        }
    }
}

@Composable
fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, iconColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, modifier = Modifier.weight(1f), fontSize = 14.sp, color = Color.Gray)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

@Composable
fun TimingItem(timing: TrainTiming) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = timing.departureTime, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "Departure", fontSize = 12.sp, color = Color.Gray)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Default.TrendingFlat, contentDescription = null, tint = BrandBlue)
                Text(text = timing.duration, fontSize = 12.sp, color = BrandBlue)
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(text = timing.arrivalTime, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(text = "Arrival", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
