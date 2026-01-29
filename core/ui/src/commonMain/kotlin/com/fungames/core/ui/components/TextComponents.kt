package com.fungames.core.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

/**
 * Text view cmposable
 */
@Composable
fun DisplayText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Black,
    style: TextStyle = MaterialTheme.typography.bodyLarge
) {
    Text(
        text = text,
        color = color,
        modifier = modifier,
        style = style
    )
}