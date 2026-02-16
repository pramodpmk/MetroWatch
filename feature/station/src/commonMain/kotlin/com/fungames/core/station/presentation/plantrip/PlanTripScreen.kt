package com.fungames.core.station.presentation.plantrip

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.fungames.core.navigation.LocalNavigationResults
import com.fungames.core.navigation.Route
import com.fungames.core.navigation.result.NavigationKeys
import com.fungames.core.station.domain.TripTiming
import com.fungames.core.ui.components.AppScaffold
import com.fungames.core.ui.components.BrandToolBar
import com.fungames.core.ui.components.DisplayText
import com.fungames.core.ui.theme.BrandBlue
import com.fungames.core.ui.theme.LightBlueBg
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                onPlanTrip = { viewModel.calculateTrip() }
            )

            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator(color = BrandBlue)
            }

            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(24.dp))
                DisplayText(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (uiState.showDetails) {
                Spacer(modifier = Modifier.height(24.dp))
                TripSummaryCard(uiState = uiState)

                Spacer(modifier = Modifier.height(24.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    DisplayText(
                        text = "AVAILABLE TRAINS",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = BrandBlue,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    uiState.timings.forEach { timing ->
                        TripTimingItem(timing)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TripCalculationCard(
    uiState: PlanTripUiState,
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
            Surface(
                shape = RoundedCornerShape(topStart = 24.dp, bottomEnd = 24.dp),
                color = Color.White
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "PLAN TRIP",
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
                    Text(text = "FROM", fontSize = 10.sp, color = Color.Gray)
                    Text(text = "TO", fontSize = 10.sp, color = Color.Gray)
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
                        textAlign = TextAlign.End
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
fun TripSummaryCard(uiState: PlanTripUiState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        DisplayText(
            text = "TRIP DETAILS",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            ),
            color = BrandBlue,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TripDetailItem(label = "Distance", value = uiState.distance)
                    TripDetailItem(label = "Fare", value = uiState.fare)
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFE0E0E0))
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TripDetailItem(label = "Intermediate Stations", value = "${uiState.numberOfStations}")
                    TripDetailItem(label = "Line", value = uiState.lineName)
                }
            }
        }
    }
}

@Composable
private fun TripDetailItem(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TripTimingItem(timing: TripTiming) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DisplayText(
                    text = "Train #${timing.trainNumber}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

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
                    DisplayText(text = "DEPARTURE", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
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
                    DisplayText(text = "ARRIVAL", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
            }
        }
    }
}
