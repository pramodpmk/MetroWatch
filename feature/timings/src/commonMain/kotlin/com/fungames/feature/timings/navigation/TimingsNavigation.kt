package com.fungames.feature.timings.navigation

import com.fungames.feature.timings.presentation.detail.TrainTimingDetail
import com.fungames.feature.timings.presentation.TrainTimingScreen
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.fungames.core.navigation.Route
import com.fungames.feature.timings.presentation.WaterMetroTimingScreen
import kotlinx.serialization.Serializable


sealed interface TimingRoutes {
    @Serializable
    data object Timings : TimingRoutes
    @Serializable
    data object TimingDetail : TimingRoutes
    @Serializable
    data object WaterMetroTimings : TimingRoutes

}

fun NavGraphBuilder.timingsGraph(
    navController: NavHostController,
    onNavigate: (Route) -> Unit,
) {
    composable<TimingRoutes.Timings> {
        TrainTimingScreen(navController)
    }
    composable<TimingRoutes.WaterMetroTimings> {
        WaterMetroTimingScreen(navController)
    }
    composable<TimingRoutes.TimingDetail> { backStackEntry ->
        TrainTimingDetail(
            navController,
            onNavigate,
        )
    }
}
