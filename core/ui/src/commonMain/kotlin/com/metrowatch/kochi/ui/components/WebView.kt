package com.metrowatch.kochi.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun WebView(url: String, modifier: Modifier = Modifier)
