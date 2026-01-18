package com.fungames.reminderapp.presentation

import DisplayText
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fungames.core.navigation.BottomTab
import com.fungames.core.navigation.HomeDestination
import com.fungames.core.navigation.Route
import com.fungames.core.station.navigation.stationsGraph
import com.fungames.fare.navigation.FareRoutes
import com.fungames.fare.navigation.fareGraph
import com.fungames.feature.timings.navigation.TimingRoutes
import com.fungames.feature.timings.navigation.timingsGraph
import com.fungames.home.navigation.HomeRoutes
import com.fungames.home.navigation.homeGraph
import com.fungames.reminderapp.navigation.appGraph

@Composable
fun TabHost(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    appNavHostController: NavHostController,
    tab: BottomTab
) {
    val homeController = rememberNavController()
    val timingController = rememberNavController()
    val fareController = rememberNavController()
    val settingsController = rememberNavController()
    val navControllers = remember {
        linkedMapOf<BottomTab, NavHostController>(
            BottomTab.HOME to homeController,
            BottomTab.TIMINGS to timingController,
            BottomTab.FARE to fareController,
            BottomTab.SETTINGS to settingsController
        )
    }

    val navController = navControllers.getValue(tab)

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = when (tab) {
            BottomTab.TIMINGS -> TimingRoutes.Timings
            BottomTab.HOME -> HomeRoutes.HomePage
                BottomTab.FARE -> FareRoutes.CalculateFare
            BottomTab.SETTINGS -> FareRoutes.CalculateFare
        }
    ) { // Screens directly tied to bottom navigation goes here
        timingsGraph(navController, { target ->
            appNavHostController.navigate(target)
        })
        homeGraph(navController)
        fareGraph(navController)
    }
}

@Composable
fun RootNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = HomeDestination.Tabs
    ) {
        // All screens displayed over (without) bottom navigation goes here
        composable<HomeDestination.Tabs> {
            HomeScaffold(navController)
        }
        stationsGraph(navController)
        homeGraph(navController)
        timingsGraph(navController, { target ->
            navController.navigate(target)
        })
        fareGraph(navController)
        appGraph(navController)
    }
}

@Composable
fun HomeScaffold(
    appNavController: NavHostController
) {
    val tabs = remember { BottomTab.entries.toList() }
    var selectedTab by rememberSaveable {
        mutableStateOf(BottomTab.HOME)
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(Icons.Outlined.AccountBox, null) },
                        label = { DisplayText(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        TabHost(
            modifier = Modifier.padding(padding),
            padding = padding,
            appNavHostController = appNavController,
            tab = selectedTab
        )
    }
}
