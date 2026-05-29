package com.metrowatch.kochi.station.presentation.metroroutes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.metrowatch.kochi.station.domain.MetroRoute
import com.metrowatch.kochi.ui.components.AppScaffold
import com.metrowatch.kochi.ui.components.BrandToolBar
import com.metrowatch.kochi.ui.components.DisplayText
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MetroRoutesRoute(
    navController: NavHostController,
    viewModel: MetroRoutesViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    MetroRoutesScreen(state, navController)
}

@Composable
fun MetroRoutesScreen(
    state: MetroRoutesUiState,
    navController: NavHostController
) {
    AppScaffold(
        toolBar = {
            BrandToolBar(
                title = "Metro routes",
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
                        MetroRouteItem(route = route)
                    }
                }
            }
        }
    }
}

@Composable
fun MetroRouteItem(route: MetroRoute) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            DisplayText(
                text = route.lineId,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )

            if (route.stations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                DisplayText(
                    text = "Stations: ${route.stations.joinToString(" → ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
