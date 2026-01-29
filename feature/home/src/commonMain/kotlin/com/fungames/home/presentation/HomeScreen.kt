package com.fungames.home.presentation

import com.fungames.core.ui.components.DisplayText
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fungames.core.ui.components.AppScaffold
import com.fungames.core.ui.components.RowGrid
import kotlinx.coroutines.flow.StateFlow
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen(homeStateFlow: StateFlow<HomePageUi>) {

    val homeState = homeStateFlow.collectAsState()

    AppScaffold(
        toolBar = { HomeToolBar(homeState.value.locationText) },
        bottomBar = {  }, // TODO : Bottom bar to include n app module
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(Modifier.height(60.dp))
            DisplayText("Home Page")
            Spacer(Modifier.height(160.dp))
            RowGrid(
                items = homeState.value.stationList,
                columns = 2,
                horizontalSpacing = 8.dp,
                verticalSpacing = 8.dp
            ) { item ->
                Box(modifier = Modifier.background(Color.Blue)) {
                    DisplayText(
                        item.stationName,
                        Modifier.padding(16.dp)
                    )
                }
            }
            Spacer(Modifier.height(160.dp))
        }
    }



}

@Composable
@Preview
fun PreviewHome() {
    val viewModel = HomeViewModel()
    HomeScreen(viewModel.homeState)
}