package com.metrowatch.kochi.station.presentation.metroroutes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.metrowatch.kochi.station.domain.MetroRoute
import com.metrowatch.kochi.ui.components.AppScaffold
import com.metrowatch.kochi.ui.components.BrandToolBar
import com.metrowatch.kochi.ui.components.DisplayText
import com.metrowatch.kochi.ui.theme.BrandBlue
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun MetroRoutesRoute(
    navController: NavHostController,
    viewModel: MetroRoutesViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    MetroRoutesScreen(
        state = state,
        navController = navController,
        onRouteSelected = viewModel::onRouteSelected
    )
}

@Composable
fun MetroRoutesScreen(
    state: MetroRoutesUiState,
    navController: NavHostController,
    onRouteSelected: (Int) -> Unit
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F8F8))
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = BrandBlue
                )
                state.isError -> DisplayText(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
                state.routes.isEmpty() -> DisplayText(
                    text = "No routes available",
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> {
                    val route = state.selectedRoute ?: return@Box
                    val lineColor = lineColor(route.lineId)

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            RouteTabSelector(
                                routes = state.routes,
                                selectedIndex = state.selectedRouteIndex,
                                onRouteSelected = onRouteSelected
                            )
                        }

                        item {
                            RouteHeroCard(
                                route = route,
                                lineColor = lineColor,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }

                        item {
                            StationsHeader(
                                route = route,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            )
                        }

                        itemsIndexed(route.stations) { index, station ->
                            StationTimelineItem(
                                index = index,
                                station = station,
                                total = route.stations.size,
                                lineColor = lineColor,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }

                        item {
                            ViewFullRouteMap(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteTabSelector(
    routes: List<MetroRoute>,
    selectedIndex: Int,
    onRouteSelected: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(routes) { index, route ->
            val isSelected = index == selectedIndex
            val color = lineColor(route.lineId)

            Card(
                modifier = Modifier
                    .width(88.dp)
                    .clickable { onRouteSelected(index) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Color.White else Color(0xFFF5F5F5)
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (isSelected) 4.dp else 0.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (isSelected) color else color.copy(alpha = 0.15f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = route.lineId,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp
                            ),
                            color = if (isSelected) Color.White else color
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = lineName(route.lineId),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        maxLines = 2
                    )
                }
            }
        }
    }
}

@Composable
private fun RouteHeroCard(
    route: MetroRoute,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                lineColor.copy(alpha = 0.08f),
                                lineColor.copy(alpha = 0.18f)
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(lineColor, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = route.lineId,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = lineName(route.lineId),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatChip(
                            icon = Icons.Default.Route,
                            value = "${route.stations.size}",
                            label = "Total Stations",
                            lineColor = lineColor
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .background(lineColor.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Train,
                        contentDescription = null,
                        tint = lineColor,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    lineColor: Color
) {
    Row(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = lineColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun StationsHeader(route: MetroRoute, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Stations (${route.stations.size})",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        if (route.stations.size >= 2) {
            Text(
                text = "${route.stations.first()} → ${route.stations.last()}",
                style = MaterialTheme.typography.labelSmall,
                color = BrandBlue
            )
        }
    }
}

@Composable
private fun StationTimelineItem(
    index: Int,
    station: String,
    total: Int,
    lineColor: Color,
    modifier: Modifier = Modifier
) {
    val isFirst = index == 0
    val isLast = index == total - 1
    val isTerminal = isFirst || isLast
    val numberText = (index + 1).toString().padStart(2, '0')

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { }
            .background(Color.White)
            .padding(end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(if (isFirst) 12.dp else 16.dp)
                    .background(if (isFirst) Color.Transparent else lineColor.copy(alpha = 0.35f))
            )

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        if (isTerminal) lineColor else Color.White,
                        CircleShape
                    )
                    .then(
                        if (!isTerminal) Modifier.border(1.5.dp, lineColor.copy(alpha = 0.5f), CircleShape)
                        else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = numberText,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    ),
                    color = if (isTerminal) Color.White else lineColor
                )
            }

            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(if (isLast) 12.dp else 16.dp)
                    .background(if (isLast) Color.Transparent else lineColor.copy(alpha = 0.35f))
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = station,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = if (isTerminal) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (isTerminal) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .background(lineColor.copy(alpha = 0.12f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Terminal Station",
                            style = MaterialTheme.typography.labelSmall,
                            color = lineColor
                        )
                    }
                }
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }

    if (!isLast) {
        HorizontalDivider(
            modifier = Modifier.padding(start = 48.dp),
            color = Color(0xFFF0F0F0),
            thickness = 0.5.dp
        )
    }
}

@Composable
private fun ViewFullRouteMap(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(BrandBlue.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "View full route map",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "See all stations on map",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private fun lineColor(lineId: String): Color = when (lineId.uppercase()) {
    "R1" -> Color(0xFF039076)
    "R2" -> Color(0xFF1565C0)
    "R3" -> Color(0xFFF9A825)
    else -> Color(0xFF039076)
}

private fun lineName(lineId: String): String = when (lineId.uppercase()) {
    "R1" -> "Green Line"
    "R2" -> "Blue Line"
    "R3" -> "Yellow Line"
    else -> lineId
}
