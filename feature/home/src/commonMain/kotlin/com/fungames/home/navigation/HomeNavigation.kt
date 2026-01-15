package com.fungames.home.navigation

import HomeRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable


sealed interface HomeRoutes {
    @Serializable
    data object HomePage : HomeRoutes

}

fun NavGraphBuilder.homeGraph(navController: NavHostController) {
    composable<HomeRoutes.HomePage> {
        HomeRoute(navController)
    }
}
