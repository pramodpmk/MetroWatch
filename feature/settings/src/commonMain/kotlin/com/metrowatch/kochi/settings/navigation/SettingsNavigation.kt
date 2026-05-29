package com.metrowatch.kochi.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.metrowatch.kochi.navigation.Route
import com.metrowatch.kochi.settings.presentation.SettingsScreen
import kotlinx.serialization.Serializable

sealed interface SettingsRoutes {
    @Serializable
    data object Settings : SettingsRoutes
}

fun NavGraphBuilder.settingsGraph(
    navController: NavHostController,
    onNavigate: (Route) -> Unit = {}
) {
    composable<SettingsRoutes.Settings> {
        SettingsScreen(onNavigate = onNavigate)
    }
}
