package com.fungames.home.navigation

import HomeRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.fungames.core.navigation.Route
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI


sealed interface HomeRoutes {
    @Serializable
    data object HomePage : HomeRoutes

}

@OptIn(KoinExperimentalAPI::class)
fun NavGraphBuilder.homeGraph(
    navController: NavHostController,
    onNavigate: (Route) -> Unit
    ) {
    composable<HomeRoutes.HomePage> {
        HomeRoute(
            navController,
            onNavigate,
            koinViewModel()
        )
    }
}
