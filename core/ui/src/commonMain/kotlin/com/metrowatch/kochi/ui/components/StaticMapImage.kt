package com.metrowatch.kochi.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun StaticMapImage(
    imageUrl: String?,
    modifier: Modifier = Modifier.fillMaxWidth().height(200.dp),
    contentDescription: String = "Station location map"
) {
    println("StaticMapImage>>>url>>>$imageUrl")
    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}
