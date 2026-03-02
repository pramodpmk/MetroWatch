package com.fungames.core.station.presentation.contact

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ContactsRoute(
    navHostController: NavHostController,
    viewModel: ContactsViewModel = koinViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    ContactsScreen(state, navHostController)
}