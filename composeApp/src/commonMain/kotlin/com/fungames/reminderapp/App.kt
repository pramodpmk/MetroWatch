package com.fungames.reminderapp

import androidx.compose.material3.ExperimentalMaterial3Api
import com.fungames.core.ui.theme.MetroTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.fungames.core.navigation.LocalNavigationResults
import com.fungames.core.navigation.NavigationResults
import com.fungames.fare.navigation.FareRoutes
import com.fungames.feature.timings.navigation.TimingRoutes
import com.fungames.home.navigation.HomeRoutes
import com.fungames.reminderapp.presentation.RootNavHost
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
