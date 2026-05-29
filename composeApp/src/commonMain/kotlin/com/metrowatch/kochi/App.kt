package com.metrowatch.kochi

import androidx.compose.material3.ExperimentalMaterial3Api
import com.metrowatch.kochi.ui.theme.MetroTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.metrowatch.kochi.navigation.LocalNavigationResults
import com.metrowatch.kochi.navigation.NavigationResults
import com.metrowatch.kochi.fare.navigation.FareRoutes
import com.metrowatch.kochi.timings.navigation.TimingRoutes
import com.metrowatch.kochi.home.navigation.HomeRoutes
import com.metrowatch.kochi.presentation.RootNavHost
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Serializable
sealed interface Screen {
    @Serializable
    data object AddReminder : Screen
}

val TopLevelRoutes = setOf(
    HomeRoutes.HomePage::class,
    TimingRoutes.Timings::class,
    FareRoutes.CalculateFare::class
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {

    val navigationResults = remember { NavigationResults() }
    MetroTheme {

        CompositionLocalProvider(
            LocalNavigationResults provides navigationResults
        ) {
            RootNavHost()
        }
    }
}
