package com.fungames.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fungames.core.ui.components.BrandToolBar
import com.fungames.core.ui.components.DisplayText
import com.fungames.core.ui.components.RowGrid
import com.fungames.core.ui.theme.BrandBlue
import com.fungames.core.ui.theme.LightBlueBg
import com.fungames.core.ui.theme.NearestStationLabelColor
import com.fungames.core.ui.theme.NextTrainGreen

@Composable
fun HomeToolBar(
    text: String,
    onLocationClick: () -> Unit
) {
    BrandToolBar(
        title = "MetroTime",
        navigationIcon = Icons.Default.DirectionsSubway
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { onLocationClick() },
            color = Color.White.copy(alpha = 0.15f)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                DisplayText(
                    text = text,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    DisplayText(
                        text = "Change",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NearestStationSection(
    station: NearestStation,
    onStationClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LightBlueBg)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                DisplayText(
                    text = "Nearest Station",
                    color = NearestStationLabelColor,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
                )
                DisplayText(
                    text = station.stationName,
                    color = Color.Black,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    DisplayText(
                        text = station.distanceToStation,
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(BrandBlue, CircleShape)
                    .clickable { onStationClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsSubway,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            InfoCard(
                label = "Line",
                value = station.line,
                valueColor = Color(0xFF7B1FA2),
                modifier = Modifier.weight(1f)
            )
            InfoCard(
                label = "Next Train",
                value = station.nextTrainTime,
                valueColor = NextTrainGreen,
                modifier = Modifier.weight(1f)
            )
            InfoCard(
                label = "Platform",
                value = station.platform,
                valueColor = Color.DarkGray,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun InfoCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DisplayText(text = label, color = Color.Gray, style = MaterialTheme.typography.labelSmall)
            DisplayText(
                text = value,
                color = valueColor,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun ActionGrid(
    onActionClick: (String) -> Unit
) {
    val actions = listOf(
        ActionItemData("Stations", Icons.Default.Map, Color(0xFF536DFE)),
        ActionItemData("Plan Trip", Icons.Default.Navigation, Color(0xFF00C853)),
        ActionItemData("Timing", Icons.Default.Schedule, Color(0xFFD500F9)),
        ActionItemData("Fare", Icons.Default.Calculate, Color(0xFFFF6D00)),
        ActionItemData("Places", Icons.Default.Place, Color(0xFFFF4081)),
        ActionItemData("Parking", Icons.Default.LocalParking, Color(0xFF00BFA5))
    )

    RowGrid(
        items = actions,
        columns = 3,
        horizontalSpacing = 16.dp,
        verticalSpacing = 16.dp,
        modifier = Modifier.padding(16.dp)
    ) { action ->
        ActionItem(
            action = action,
            modifier = Modifier.fillMaxWidth(),
            onClick = { onActionClick(action.label) }
        )
    }
}

@Composable
fun WaterMetroActionGrid(
    onActionClick: (String) -> Unit
) {
    val actions = listOf(
        ActionItemData("Stations", Icons.Default.Map, Color(0xFF536DFE)),
        ActionItemData("Plan Trip", Icons.Default.Navigation, Color(0xFF00C853)),
        ActionItemData("Timing", Icons.Default.Schedule, Color(0xFFD500F9)),
        ActionItemData("Fare", Icons.Default.Calculate, Color(0xFFFF6D00))
    )

    RowGrid(
        items = actions,
        columns = 3,
        horizontalSpacing = 16.dp,
        verticalSpacing = 16.dp,
        modifier = Modifier.padding(16.dp)
    ) { action ->
        ActionItem(
            action = action,
            modifier = Modifier.fillMaxWidth(),
            onClick = { onActionClick(action.label) }
        )
    }
}

data class ActionItemData(
    val label: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun ActionItem(
    action: ActionItemData,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(action.color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            DisplayText(
                text = action.label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}
