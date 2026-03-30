package com.fungames.feature.timings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.fungames.core.ui.components.AppScaffold
import com.fungames.core.ui.components.BrandToolBar
import com.fungames.core.ui.components.DisplayText
import com.fungames.core.ui.theme.BrandBlue
import com.fungames.core.ui.theme.LightBlueBg
import com.fungames.feature.timings.navigation.TimingRoutes
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun TrainTimingScreen(
    navHostController: NavHostController,
    viewModel: TimingTableViewModel = koinViewModel()
) {
    val timings = viewModel.timingTableState.collectAsState()

    AppScaffold(
        toolBar = {
            TimingToolBar(onBackClick = { navHostController.popBackStack() })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F8F8))
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                SearchTimingCard(onClick = {
                    navHostController.navigate(TimingRoutes.TimingDetail)
                })
            }

            item {
                DisplayText(
                    text = "General timings",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            items(timings.value) { timing ->
                TimingCard(timing)
            }
        }
    }
}

@Composable
fun TimingToolBar(
    onBackClick: () -> Unit,
    title: String = "Metro Timing"
    ) {
    BrandToolBar(
        title = title,
        navigationIcon = Icons.Default.ArrowBack,
        onNavigationClick = onBackClick
    )
}

@Composable
fun SearchTimingCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(LightBlueBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DirectionsSubway,
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                DisplayText(
                    text = "Timing between stations",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                DisplayText(
                    text = "Find metro schedule for specific stations",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Icon(
                imageVector = Icons.Default.NavigateNext,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Composable
fun TimingCard(timing: TimingInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = null,
                tint = LightBlueBg.copy(alpha = 0.5f),
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                DisplayText(
                    text = timing.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                DisplayText(
                    text = timing.timing,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = BrandBlue
                    )
                )
            }
        }
    }
}
