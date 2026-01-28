package com.fungames.feature.timings.navigation

import TrainTimingDetail
import TrainTimingScreen
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.fungames.core.navigation.Route
import kotlinx.serialization.Serializable


sealed interface TimingRoutes {
    @Serializable
    data object Timings : TimingRoutes
    @Serializable
    data object TimingDetail : TimingRoutes

}

fun NavGraphBuilder.timingsGraph(
    navController: NavHostController,
    onNavigate: (Route) -> Unit,
) {
    composable<TimingRoutes.Timings> {
        TrainTimingScreen(navController)
    }
    composable<TimingRoutes.TimingDetail> { backStackEntry ->
        TrainTimingDetail(
            navController,
            onNavigate,
        )
    }
}
