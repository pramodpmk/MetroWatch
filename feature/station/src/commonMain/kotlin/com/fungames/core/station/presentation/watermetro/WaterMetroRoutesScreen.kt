package com.fungames.core.station.presentation.watermetro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBoat
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
fun WaterMetroRoutesRoute(
    navController: NavHostController,
    viewModel: WaterMetroRoutesViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    WaterMetroRoutesScreen(state, navController)
}

@Composable
fun WaterMetroRoutesScreen(
    state: WaterMetroRoutesUiState,
    navController: NavHostController
) {
    AppScaffold(
        toolBar = {
            BrandToolBar(
                title = "Water metro routes",
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
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.routes) { route ->
                        WaterMetroRouteItem(route = route)
                    }
                }
            }
        }
    }
}

@Composable
fun WaterMetroRouteItem(route: com.fungames.core.station.domain.WaterMetroRoute) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DisplayText(
                    text = route.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )

                StatusBadge(status = route.status ?: "Planned", isOperational = route.isOperational)
            }

            if (route.stations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                DisplayText(
                    text = "Stations: ${route.stations.joinToString(" → ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (route.duration != null || route.startDate != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    route.duration?.let {
                        DisplayText(
                            text = "Duration: $it",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    route.startDate?.let {
                        DisplayText(
                            text = "Started: $it",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String, isOperational: Boolean) {
    val backgroundColor = if (isOperational) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
    val textColor = if (isOperational) Color(0xFF2E7D32) else Color(0xFFEF6C00)

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(4.dp)
    ) {
        DisplayText(
            text = status,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
