package com.metrowatch.kochi.fare.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsSubway
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.*
import androidx.navigation.NavHostController
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.metrowatch.kochi.navigation.LocalNavigationResults
import com.metrowatch.kochi.navigation.Route
import com.metrowatch.kochi.navigation.result.NavigationKeys
import com.metrowatch.kochi.ui.theme.BrandBlue
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

    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    // Blue background height = status bar + toolbar row (~72dp) + card overlap (~48dp)
    val blueAreaHeight = statusBarHeight + 120.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header section: blue background behind toolbar + card overlap
            Box(modifier = Modifier.fillMaxWidth()) {
                // Fixed-height blue backdrop (shorter than toolbar+card combined)
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(blueAreaHeight)
                        .background(BrandBlue)
                )

                // Content column: sits on top of the blue backdrop, extends below it
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    // Toolbar row
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
                        // Title and subtitle start at the same x — both in this Column
                        Column {
                            Text(
                                text = "Fare calculator",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Text(
                                text = "Check fare between any metro stations",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Card starts here — within the blue area, extends below it
                    YourJourneyCard(
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
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandBlue)
                }
            }

            if (uiState.showDetails) {
                Spacer(modifier = Modifier.height(8.dp))
                FareJourneyDetailsSection(uiState = uiState)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun YourJourneyCard(
    uiState: FareUiState,
    onDepartureClick: () -> Unit,
    onArrivalClick: () -> Unit,
    onSwapStations: () -> Unit,
    onCalculateFare: () -> Unit
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
                    Text(
                        text = uiState.departureStationCode,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
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
                    Text(
                        text = uiState.arrivalStationCode,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onCalculateFare,
                enabled = uiState.departureStation.isNotEmpty() && uiState.arrivalStation.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
            ) {
                Text(
                    text = "Calculate fare",
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
fun FareJourneyDetailsSection(uiState: FareUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Fare & journey details",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.Black
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "View route map",
                    color = BrandBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    FareDetailColumn(
                        iconContent = {
                            Text(
                                text = "₹",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        },
                        label = "Total fare",
                        value = uiState.fare,
                        valueSize = 22,
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .height(56.dp)
                            .width(1.dp)
                            .background(Color(0xFFE0E0E0))
                    )

                    FareDetailColumn(
                        iconContent = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = "Distance",
                        value = uiState.distance,
                        modifier = Modifier.weight(1f),
                        alignEnd = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFE0E0E0))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    FareDetailColumn(
                        iconContent = {
                            Icon(
                                imageVector = Icons.Default.Train,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = "Stations",
                        value = uiState.stops,
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .height(56.dp)
                            .width(1.dp)
                            .background(Color(0xFFE0E0E0))
                    )

                    FareDetailColumn(
                        iconContent = {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        label = "Estimated time",
                        value = uiState.estimatedTime,
                        subLabel = "(Approx.)",
                        modifier = Modifier.weight(1f),
                        alignEnd = true
                    )
                }

                if (uiState.lineId.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFE0E0E0))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(BrandBlue, RoundedCornerShape(6.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = uiState.lineId,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.lineId,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BrandBlue.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DirectionsSubway,
                            contentDescription = null,
                            tint = BrandBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Smooth & affordable travel!",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = Color.Black
                            )
                            Text(
                                text = "Thank you for choosing Kochi Metro!",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Fares are based on standard ticket rates. Actual fare may vary for smart card users.",
            fontSize = 11.sp,
            color = Color.Gray,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun FareDetailColumn(
    iconContent: @Composable () -> Unit,
    label: String,
    value: String,
    subLabel: String = "",
    valueSize: Int = 16,
    modifier: Modifier = Modifier,
    alignEnd: Boolean = false
) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalAlignment = if (alignEnd) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(BrandBlue, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            iconContent()
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, fontSize = 11.sp, color = Color.Gray)
        Text(
            text = value,
            fontSize = valueSize.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        if (subLabel.isNotEmpty()) {
            Text(text = subLabel, fontSize = 11.sp, color = Color.Gray)
        }
    }
}
