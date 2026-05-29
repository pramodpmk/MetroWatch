package com.metrowatch.kochi.station.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import com.metrowatch.kochi.ui.components.DisplayText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.toRoute
import coil3.compose.AsyncImage
import com.metrowatch.kochi.station.navigation.StationRoutes
import com.metrowatch.kochi.station.presentation.StationViewModel
import com.metrowatch.kochi.ui.components.AppScaffold
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

    AppScaffold(
        toolBar = {
            TopAppBar(
                title = { DisplayText(state.station?.name ?: "Station details") },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
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
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (state.station != null) {
                val station = state.station
                val uriHandler = LocalUriHandler.current
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        // Static Map Image Header
                        // Using a placeholder service for static maps since we don't have a real API key
                        val mapUrl = "https://maps.googleapis.com/maps/api/staticmap?center=${station.latitude},${station.longitude}&zoom=15&size=600x300&sensor=false"
                        AsyncImage(
                            model = mapUrl,
                            contentDescription = "Station map",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            DisplayText(
                                text = station.name,
                                style = MaterialTheme.typography.headlineMedium
                            )

                            DisplayText(
                                text = "ID: ${station.id}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    val url = "https://www.google.com/maps/dir/?api=1" +
                                        "&destination=${station.latitude},${station.longitude}" +
                                        "&travelmode=transit"
                                    uriHandler.openUri(url)
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Navigation,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                DisplayText("Navigate to station")
                            }

                            Spacer(modifier = Modifier.height(16.dp))

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
                                label = "Line id",
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
                                value = if (station.wheelchairAccessible) "Yes" else "No"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StationDetailItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            DisplayText(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            DisplayText(
                text = value.ifBlank { "Not available" },
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
