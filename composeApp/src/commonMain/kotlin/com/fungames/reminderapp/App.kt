package com.fungames.reminderapp

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
    MaterialTheme {

        RootNavHost()
        /*val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = HomeRoutes.HomePage
        ) {

            appGraph(navController = navController)

            // Feature: Timings
            timingsGraph(navController)

            stationsGraph(navController)

            fareGraph(navController)

            homeGraph(navController)

            // Feature: Reminders (local to app module for now)
            composable<Screen.AddReminder> {
                AddReminderScreen(
                    onReminderSaved = { navController.popBackStack() }
                )
            }

        }*/

    }
}
