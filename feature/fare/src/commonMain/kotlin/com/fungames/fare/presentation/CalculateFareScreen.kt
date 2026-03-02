package com.fungames.fare.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.LocationOn
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
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun FareCalculatorScreen(
    navController: NavHostController,
    onNavigate: (Route) -> Unit,
    viewModel: FareViewModel = koinViewModel()
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
                title = "Fare calculator",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FareCalculationCard(
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
                onCalculateFare = { viewModel.calculateFare() }
            )

            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator(color = BrandBlue)
            }

            if (uiState.showDetails) {
                Spacer(modifier = Modifier.height(24.dp))
                FareDetailsSection(uiState = uiState)
            }
        }
    }
}

@Composable
fun FareCalculationCard(
    uiState: FareUiState,
    onDepartureClick: () -> Unit,
    onArrivalClick: () -> Unit,
    onSwapStations: () -> Unit,
    onCalculateFare: () -> Unit
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
                    modifier = Modifier
                        .padding(top = 0.dp),
                    shape = RoundedCornerShape(topStart = 24.dp, bottomEnd = 24.dp),
                    color = Color.White
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Calculate fare",
                            color = BrandBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Departure", fontSize = 10.sp, color = Color.Gray)
                    Text(text = "Arrival", fontSize = 10.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (uiState.departureStation.isEmpty()) "Select station" else uiState.departureStation,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.departureStation.isEmpty()) Color.Gray else BrandBlue,
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
                        text = if (uiState.arrivalStation.isEmpty()) "Select station" else uiState.arrivalStation,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.arrivalStation.isEmpty()) Color.Gray else BrandBlue,
                        modifier = Modifier.weight(1f).clickable { onArrivalClick() },
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onCalculateFare,
                    enabled = uiState.departureStation.isNotEmpty() && uiState.arrivalStation.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Text(text = "Calculate fare", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FareDetailsSection(uiState: FareUiState) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Fare & journey details",
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50), // Green
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Distance", fontSize = 12.sp, color = Color.Gray)
                        Text(text = uiState.distance, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp)
                            .background(color = Color(0xFFE0E0E0))
                            .padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = uiState.fare,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(text = "Total fare", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = Color(0xFFE0E0E0))
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Enjoy a smooth and efficient ride with Mumbai Metro!",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
