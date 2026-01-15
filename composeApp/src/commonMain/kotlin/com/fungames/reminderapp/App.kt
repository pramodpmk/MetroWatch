package com.fungames.reminderapp

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fungames.core.station.navigation.stationsGraph
import com.fungames.feature.timings.navigation.TimingRoutes
import com.fungames.feature.timings.navigation.timingsGraph
import com.fungames.reminderapp.navigation.appGraph
import com.fungames.reminderapp.presentation.add_reminder.AddReminderScreen
import kotlinx.serialization.Serializable
import org.jetbrains.compose.ui.tooling.preview.Preview

@Serializable
sealed interface Screen {
    @Serializable
    data object AddReminder : Screen
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = TimingRoutes.Timings
        ) {

            appGraph(navController = navController)

            // Feature: Timings
            timingsGraph(navController)

            stationsGraph(navController)

            // Feature: Reminders (local to app module for now)
            composable<Screen.AddReminder> {
                AddReminderScreen(
                    onReminderSaved = { navController.popBackStack() }
                )
            }

        }

    }
}
