package com.fungames.home.presentation

import DisplayText
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

@Composable
fun HomeBottomBar() {
    Row {
        DisplayText(text = "Home", modifier = Modifier.weight(1f))
        DisplayText(text = "Timing", modifier = Modifier.weight(1f))
        DisplayText(text = "Fare", modifier = Modifier.weight(1f))
        DisplayText(text = "Settings", modifier = Modifier.weight(1f))
    }
}
