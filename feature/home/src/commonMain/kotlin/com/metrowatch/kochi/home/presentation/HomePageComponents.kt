package com.metrowatch.kochi.home.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.metrowatch.kochi.ui.components.BrandToolBar
import com.metrowatch.kochi.ui.components.DisplayText
import com.metrowatch.kochi.ui.components.RowGrid
import com.metrowatch.kochi.ui.getLocalTime
import com.metrowatch.kochi.ui.theme.BrandBlue
import com.metrowatch.kochi.ui.theme.NextTrainGreen

private fun greeting(hour: Int) = when {
    hour < 12 -> "Good Morning!"
    hour < 17 -> "Good Afternoon!"
    else -> "Good Evening!"
}

@Composable
fun HomeToolBar(
    text: String,
    onLocationClick: () -> Unit
) {
    BrandToolBar(
        title = "MetroTime",
        navigationIcon = Icons.Default.DirectionsSubway,
        trailingIcon = Icons.Default.Notifications,
        onTrailingClick = {}
    ) {
        val (hour, _) = remember { getLocalTime() }

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                DisplayText(
                    text = greeting(hour),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(Modifier.height(10.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onLocationClick() },
                    color = Color.White.copy(alpha = 0.18f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        DisplayText(
                            text = if (text.isEmpty()) "Fetching location…" else text,
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            DisplayText(
                                text = "Change",
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.DirectionsSubway,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.22f),
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(80.dp)
            )
        }

        Spacer(Modifier.height(6.dp))
    }
}

private fun parseTrainTime(nextTrainTime: String): Pair<String, String> = when {
    nextTrainTime.contains("min") -> Pair(nextTrainTime.replace("min", "").trim(), "min")
    nextTrainTime == "Tomorrow" -> Pair("--", "Tomorrow")
    else -> Pair(nextTrainTime, "")
}

@Composable
fun NearestStationSection(
    station: NearestStation,
    onStationClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onStationClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DirectionsSubway,
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    DisplayText(
                        text = "Next train from",
                        color = Color.Gray,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(Modifier.height(4.dp))

                DisplayText(
                    text = station.stationName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (station.line.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE91E63), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            DisplayText(
                                text = station.line,
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                    DisplayText(
                        text = station.nextTrainTo,
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                DisplayText(
                    text = "Arriving in",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall
                )
                val (timeValue, timeUnit) = remember(station.nextTrainTime) {
                    parseTrainTime(station.nextTrainTime)
                }
                DisplayText(
                    text = timeValue,
                    color = NextTrainGreen,
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.ExtraBold)
                )
                if (timeUnit.isNotEmpty()) {
                    DisplayText(
                        text = timeUnit,
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (station.stationId.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        DisplayText(
                            text = "Platform ${station.stationId}",
                            color = Color.DarkGray,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlanJourneySection(
    fromStation: String,
    toStation: String,
    onSelectFrom: () -> Unit,
    onSelectTo: () -> Unit,
    onPlanJourney: () -> Unit,
    onPlanOnMap: () -> Unit
) {
    val canPlan = fromStation.isNotEmpty() && toStation.isNotEmpty()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DisplayText(
                    text = "Where are you going?",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = onPlanOnMap,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        tint = BrandBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    DisplayText(
                        text = "Plan on Map",
                        color = BrandBlue,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // From field
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelectFrom() }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(BrandBlue, CircleShape)
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            DisplayText(
                                text = "From",
                                color = Color.Gray,
                                style = MaterialTheme.typography.labelSmall
                            )
                            DisplayText(
                                text = if (fromStation.isEmpty()) "Select departure station" else fromStation,
                                color = if (fromStation.isEmpty()) Color.Gray else Color.Black,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(Modifier.height(8.dp))

                    // To field
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onSelectTo() }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    if (toStation.isEmpty()) Color(0xFFBDBDBD) else BrandBlue,
                                    CircleShape
                                )
                        )
                        Spacer(Modifier.width(8.dp))
                        Column {
                            DisplayText(
                                text = "To",
                                color = Color.Gray,
                                style = MaterialTheme.typography.labelSmall
                            )
                            DisplayText(
                                text = if (toStation.isEmpty()) "Search destination station" else toStation,
                                color = if (toStation.isEmpty()) Color.Gray else Color.Black,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }

                Spacer(Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFF5F5F5), CircleShape)
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onPlanJourney,
                enabled = canPlan,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
            ) {
                DisplayText(
                    text = "Plan Journey",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun QuickActionsSection(onActionClick: (String) -> Unit) {
    val actions = listOf(
        QuickActionData("Stations", "Explore all", Icons.Default.Map, Color(0xFF536DFE)),
        QuickActionData("Routes", "Metro & Water", Icons.Default.Route, Color(0xFF00C853)),
        QuickActionData("Timings", "First & Last train", Icons.Default.Schedule, Color(0xFFD500F9)),
        QuickActionData("Fare", "Calculate fare", Icons.Default.Calculate, Color(0xFFFF6D00)),
        QuickActionData("Parking", "Find parking", Icons.Default.LocalParking, Color(0xFF00BFA5)),
        QuickActionData("Service updates", "Latest info", Icons.Default.Info, Color(0xFF2196F3))
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DisplayText(
                text = "Quick actions",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            TextButton(
                onClick = { onActionClick("Stations") },
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)
            ) {
                DisplayText(
                    text = "View all",
                    color = BrandBlue,
                    style = MaterialTheme.typography.labelSmall
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        RowGrid(
            items = actions,
            columns = 3,
            horizontalSpacing = 10.dp,
            verticalSpacing = 10.dp
        ) { action ->
            QuickActionItem(action = action, onClick = { onActionClick(action.label) })
        }
    }
}

data class QuickActionData(
    val label: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun QuickActionItem(action: QuickActionData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(action.color.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = null,
                    tint = action.color,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            DisplayText(
                text = action.label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
            )
            DisplayText(
                text = action.subtitle,
                color = Color.Gray,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)
            )
        }
    }
}

@Composable
fun WaterMetroActionGrid(onActionClick: (String) -> Unit) {
    val actions = listOf(
        QuickActionData("Stations", "Explore", Icons.Default.Map, Color(0xFF536DFE)),
        QuickActionData("Routes", "View routes", Icons.Default.DirectionsBoat, Color(0xFF00C853)),
        QuickActionData("Timing", "Schedules", Icons.Default.Schedule, Color(0xFFD500F9)),
        QuickActionData("Fare", "Calculate", Icons.Default.Calculate, Color(0xFFFF6D00))
    )

    RowGrid(
        items = actions,
        columns = 3,
        horizontalSpacing = 10.dp,
        verticalSpacing = 10.dp,
        modifier = Modifier.padding(16.dp)
    ) { action ->
        QuickActionItem(action = action, onClick = { onActionClick(action.label) })
    }
}

@Composable
fun WaterMetroSection(onExplore: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                DisplayText(
                    text = "Water Metro",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(4.dp))
                DisplayText(
                    text = "Scenic rides across Kochi",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = onExplore,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color.White),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) {
                    DisplayText(
                        text = "Explore Water Metro",
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.DirectionsBoat,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.28f),
                modifier = Modifier.size(88.dp)
            )
        }
    }
}
