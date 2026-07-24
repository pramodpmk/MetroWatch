package com.metrowatch.kochi.station.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import com.metrowatch.kochi.ui.components.DisplayText
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import com.metrowatch.kochi.station.domain.Station
import com.metrowatch.kochi.station.navigation.StationRoutes
import com.metrowatch.kochi.station.presentation.StationViewModel
import com.metrowatch.kochi.ui.components.StaticMapImage
import com.metrowatch.kochi.ui.theme.BrandBlue
import com.metrowatch.kochi.ui.theme.BrandWhite
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class, ExperimentalMaterial3Api::class)
@Composable
fun StationDetailRoute(
    navHostController: NavHostController,
    backStackEntry: NavBackStackEntry,
    viewModel: StationViewModel = koinViewModel()
) {
    val stationDetails = backStackEntry.toRoute<StationRoutes.StationDetails>()
    val stationId = stationDetails.stationId

    LaunchedEffect(stationId) {
        viewModel.loadStation(stationId)
    }

    val state = viewModel.stationDetailState.collectAsState().value

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            state.isError -> {
                DisplayText(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            }
            state.station != null -> {
                StationDetailContent(station = state.station)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FloatingCircleButton(
                icon = Icons.Default.ArrowBack,
                contentDescription = "Back",
                onClick = { navHostController.popBackStack() }
            )
        }
    }
}

@Composable
private fun StationDetailContent(station: Station) {
    val uriHandler = LocalUriHandler.current
    val isWaterMetro = station.mode.contains("water", ignoreCase = true)
    val modeLabel = station.mode.split("_").joinToString(" ") { part ->
        part.replaceFirstChar(Char::uppercase)
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            StaticMapImage(
                imageUrl = station.imageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
        }

        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-24).dp),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .align(Alignment.CenterHorizontally)
                            .background(
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                                RoundedCornerShape(2.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    Brush.linearGradient(
                                        listOf(BrandBlue, BrandBlue.copy(alpha = 0.7f))
                                    ),
                                    RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isWaterMetro) Icons.Default.DirectionsBoat else Icons.Default.Train,
                                contentDescription = null,
                                tint = BrandWhite,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            DisplayText(
                                text = station.name,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.headlineSmall
                            )
                            DisplayText(
                                text = "ID: ${station.id}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            StationModeChip(label = modeLabel)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val url = "https://www.google.com/maps/dir/?api=1" +
                                "&destination=${station.latitude},${station.longitude}" +
                                "&travelmode=transit"
                            uriHandler.openUri(url)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Navigation,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DisplayText("Navigate to station", color = BrandWhite)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    StationDetailItem(
                        icon = Icons.Default.Language,
                        label = "Malayalam name",
                        value = station.nameMl ?: ""
                    )

                    StationDetailItem(
                        icon = Icons.Default.Language,
                        label = "Hindi name",
                        value = station.nameHi ?: ""
                    )

                    StationDetailItem(
                        icon = Icons.Default.Route,
                        label = "Line ID",
                        value = station.lineId
                    )

                    StationDetailItem(
                        icon = Icons.Default.FormatListNumbered,
                        label = "Sequence",
                        value = station.sequence.toString()
                    )

                    StationDetailItem(
                        icon = Icons.Default.Accessible,
                        label = "Wheelchair accessible",
                        value = if (station.wheelchairAccessible) "Yes" else "No",
                        showDivider = false
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BrandBlue.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = BrandBlue,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        DisplayText(
                            text = "${station.name} is part of the $modeLabel network (Line ${station.lineId}).",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StationModeChip(label: String) {
    Row(
        modifier = Modifier
            .background(BrandBlue.copy(alpha = 0.12f), RoundedCornerShape(50)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DisplayText(
            text = label,
            color = BrandBlue,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun FloatingCircleButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color = Color.Black
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(44.dp)
            .background(BrandWhite, RoundedCornerShape(50))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}

@Composable
fun StationDetailItem(
    icon: ImageVector,
    label: String,
    value: String,
    showDivider: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(BrandBlue.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = BrandBlue,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            DisplayText(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = BrandBlue
            )
            DisplayText(
                text = value.ifBlank { "Not available" },
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
    if (showDivider) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}
