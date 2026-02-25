package com.fungames.core.station.presentation.parking

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fungames.core.ui.components.AppScaffold
import com.fungames.core.ui.components.BrandToolBar
import com.fungames.core.ui.components.DisplayText
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ParkingRoute(
    navController: NavHostController,
    viewModel: ParkingViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    ParkingScreen(state, navController)
}

@Composable
fun ParkingScreen(
    state: ParkingUiState,
    navController: NavHostController
) {
    AppScaffold(
        toolBar = {
            BrandToolBar(
                title = "Parking rates",
                navigationIcon = Icons.Default.ArrowBack,
                onNavigationClick = { navController.popBackStack() }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.isError) {
                DisplayText(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            } else if (state.parkingInfo == null) {
                DisplayText(
                    text = "No parking information available",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                val info = state.parkingInfo
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        ParkingHeader(info)
                    }

                    item {
                        DisplayText(
                            text = "Hourly rates",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    items(info.hourlyRates) { rate ->
                        ParkingRateItem(rate, info.currency)
                    }

                    item {
                        DisplayText(
                            text = "Passes",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    items(info.passes) { pass ->
                        ParkingPassItem(pass, info.currency)
                    }

                    if (info.notes.isNotEmpty()) {
                        item {
                            DisplayText(
                                text = "Important notes",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            info.notes.forEach { note ->
                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Icon(Icons.Default.Info, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    DisplayText(note, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ParkingHeader(info: com.fungames.core.station.domain.ParkingInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            DisplayText(
                text = "Applicable for: ${info.applicableFor}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            DisplayText(
                text = "Effective from: ${info.effectiveFrom}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ParkingRateItem(rate: com.fungames.core.station.domain.ParkingRate, currency: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                DisplayText(
                    text = rate.vehicleType.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Badge(containerColor = if (rate.isCommuter) Color(0xFFE8F5E9) else Color(0xFFE3F2FD)) {
                    DisplayText(
                        text = if (rate.isCommuter) "Commuter" else "Non-Commuter",
                        color = if (rate.isCommuter) Color(0xFF2E7D32) else Color(0xFF1976D2),
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            DisplayText("First 2 hours: $currency ${rate.firstTwoHours}")
            DisplayText("Every extra hour: $currency ${rate.everyExtraHour}")
            rate.note?.let {
                Spacer(modifier = Modifier.height(4.dp))
                DisplayText(it, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}

@Composable
fun ParkingPassItem(pass: com.fungames.core.station.domain.ParkingPass, currency: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                DisplayText(
                    text = "${pass.passType.replaceFirstChar { it.uppercase() }} Pass",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                DisplayText(
                    text = "$currency ${pass.rate}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            DisplayText("Vehicle: ${pass.vehicleType.replaceFirstChar { it.uppercase() }}")
            DisplayText("User: ${if (pass.isCommuter) "Commuter" else "Non-Commuter"}")
            pass.validityNote?.let {
                Spacer(modifier = Modifier.height(4.dp))
                DisplayText(it, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}
