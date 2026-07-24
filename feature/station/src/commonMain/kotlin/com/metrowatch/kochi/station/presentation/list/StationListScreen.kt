package com.metrowatch.kochi.station.presentation.list

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.metrowatch.kochi.station.domain.Station
import com.metrowatch.kochi.ui.components.DisplayText
import com.metrowatch.kochi.ui.theme.BrandBlue
import com.metrowatch.kochi.ui.theme.BrandWhite
import org.jetbrains.compose.resources.painterResource
import reminderapp.feature.station.generated.resources.Res
import reminderapp.feature.station.generated.resources.img_metro_train

private val TitleRowHeight = 56.dp

@Composable
fun StationListScreen(
    stationListState: StationListUi,
    navHostController: NavHostController,
    onSearchQueryChange: (String) -> Unit = {},
    onStationClick: (Station) -> Unit = {}
) {
    val lazyListState = rememberLazyListState()
    val isAtTop by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 &&
                    lazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        StationListHeader(
            searchQuery = stationListState.searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onBackClick = { navHostController.popBackStack() },
            isExpanded = isAtTop
        )

        if (!stationListState.isLoading && !stationListState.isError) {
            StationRouteInfoRow(stations = stationListState.list)
        }

        Box(modifier = Modifier.weight(1f)) {
            when {
                stationListState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                stationListState.isError -> {
                    DisplayText(
                        text = stationListState.errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                stationListState.filteredList.isEmpty() -> {
                    DisplayText(
                        text = if (stationListState.searchQuery.isBlank()) "No stations available"
                        else "No stations found for \"${stationListState.searchQuery}\"",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                else -> {
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            top = 4.dp,
                            bottom = 4.dp + WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding()
                        )
                    ) {
                        itemsIndexed(stationListState.filteredList) { index, station ->
                            StationTimelineItem(
                                station = station,
                                isFirst = index == 0,
                                isLast = index == stationListState.filteredList.size - 1,
                                onClick = { onStationClick(station) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StationListHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    isExpanded: Boolean
) {
    val titleHeight by animateDpAsState(
        targetValue = if (isExpanded) TitleRowHeight else 0.dp,
        animationSpec = tween(durationMillis = 220),
        label = "titleHeight"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(BrandBlue)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(bottom = 12.dp)
        ) {
            // Collapsible: back button + title + train image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(titleHeight)
                    .clipToBounds()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(TitleRowHeight)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = BrandWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Stations",
                        color = BrandWhite,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Always visible: search bar
            AlwaysVisibleSearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange
            )
        }
    }
}

@Composable
private fun AlwaysVisibleSearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 16.dp, end = 16.dp)
            .background(BrandWhite.copy(alpha = 0.18f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = BrandWhite.copy(alpha = 0.85f),
            modifier = Modifier.size(18.dp)
        )
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp),
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = BrandWhite),
            cursorBrush = SolidColor(BrandWhite),
            singleLine = true,
            decorationBox = { innerTextField ->
                Box {
                    if (query.isEmpty()) {
                        Text(
                            text = "Search station",
                            color = BrandWhite.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    innerTextField()
                }
            }
        )
        if (query.isNotEmpty()) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Clear",
                tint = BrandWhite.copy(alpha = 0.85f),
                modifier = Modifier.size(18.dp).clickable { onQueryChange("") }
            )
        }
    }
}

@Composable
private fun StationRouteInfoRow(stations: List<Station>) {
    if (stations.isEmpty()) return
    val sorted = stations.sortedBy { it.sequence }
    val first = sorted.first().nameEn
    val last = sorted.last().nameEn

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${stations.size} Stations",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "From $first to $last",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = BrandBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }

    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun StationTimelineItem(
    station: Station,
    isFirst: Boolean,
    isLast: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StationTimeline(isFirst = isFirst, isLast = isLast)

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(40.dp)
                .background(BrandBlue.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Train,
                contentDescription = null,
                tint = BrandBlue,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = station.nameEn,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isFirst || isLast) "Terminal Station" else "Station",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .border(1.dp, BrandBlue, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = station.id.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = BrandBlue
                )
            }
        }

        Text(
            text = "${station.sequence}",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 6.dp)
        )

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun StationTimeline(isFirst: Boolean, isLast: Boolean) {
    val lineColor = BrandBlue
    val white = BrandWhite

    Column(
        modifier = Modifier
            .width(40.dp)
            .height(84.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isFirst) {
            Canvas(modifier = Modifier.width(2.dp).weight(1f)) {
                drawLine(
                    color = lineColor,
                    start = Offset(size.width / 2, 0f),
                    end = Offset(size.width / 2, size.height),
                    strokeWidth = size.width
                )
            }
            Canvas(modifier = Modifier.size(14.dp)) {
                drawCircle(color = lineColor, radius = size.minDimension / 2)
            }
        } else if (isLast) {
            Canvas(modifier = Modifier.width(2.dp).weight(1f)) {
                drawLine(
                    color = lineColor,
                    start = Offset(size.width / 2, 0f),
                    end = Offset(size.width / 2, size.height),
                    strokeWidth = size.width
                )
            }
            Canvas(modifier = Modifier.size(14.dp)) {
                drawCircle(color = lineColor, radius = size.minDimension / 2)
            }
            Canvas(modifier = Modifier.width(2.dp).height(8.dp)) {
                drawLine(
                    color = lineColor,
                    start = Offset(size.width / 2, 0f),
                    end = Offset(size.width / 2, size.height),
                    strokeWidth = size.width
                )
            }
        } else {
            Canvas(modifier = Modifier.width(2.dp).weight(1f)) {
                drawLine(
                    color = lineColor,
                    start = Offset(size.width / 2, 0f),
                    end = Offset(size.width / 2, size.height),
                    strokeWidth = size.width
                )
            }
            Canvas(modifier = Modifier.size(14.dp)) {
                drawCircle(color = white, radius = size.minDimension / 2)
                drawCircle(
                    color = lineColor,
                    radius = size.minDimension / 2,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            Canvas(modifier = Modifier.width(2.dp).weight(1f)) {
                drawLine(
                    color = lineColor,
                    start = Offset(size.width / 2, 0f),
                    end = Offset(size.width / 2, size.height),
                    strokeWidth = size.width
                )
            }
        }
    }
}
