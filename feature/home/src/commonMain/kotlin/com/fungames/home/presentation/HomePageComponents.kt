package com.fungames.home.presentation

import com.fungames.core.ui.components.DisplayText
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomeToolBar(
    text: String
) {
    Row {
        DisplayText(text = text)
    }
}
