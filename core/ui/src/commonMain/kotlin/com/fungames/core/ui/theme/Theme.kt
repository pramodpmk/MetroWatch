package com.fungames.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BrandBlue,
    onPrimary = BrandWhite,
    secondary = NearestStationLabelColor,
    onSecondary = BrandWhite,
    background = BrandWhite,
    surface = BrandWhite,
    onBackground = Color.Black,
    onSurface = Color.Black,
    surfaceVariant = LightBlueBg,
    onSurfaceVariant = Color.Black
)

@Composable
fun MetroTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
