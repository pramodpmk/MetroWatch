package com.fungames.reminderapp

import TrainTimingScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.fungames.reminderapp.presentation.add_reminder.AddReminderScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

sealed class Screen {
    data object Home : Screen()
    data object AddReminder : Screen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }

        when (currentScreen) {
            is Screen.Home -> {
                Scaffold(
                    topBar = { TopAppBar(title = { Text("Reminder App") }) },
                    floatingActionButton = {
                        FloatingActionButton(onClick = { currentScreen = Screen.AddReminder }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Reminder")
                        }
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier.fillMaxSize().padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        TrainTimingScreen()
                    }
                }
            }

            is Screen.AddReminder -> {
                AddReminderScreen(onReminderSaved = { currentScreen = Screen.Home })
            }
        }
    }
}
