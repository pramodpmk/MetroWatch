package com.metrowatch.kochi.presentation

import com.metrowatch.kochi.ui.components.DisplayText
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.metrowatch.kochi.navigation.BottomTab
import com.metrowatch.kochi.navigation.HomeDestination
import com.metrowatch.kochi.navigation.NavigationResults
import com.metrowatch.kochi.station.navigation.stationsGraph
import com.metrowatch.kochi.fare.navigation.FareRoutes
import com.metrowatch.kochi.fare.navigation.fareGraph
import com.metrowatch.kochi.settings.navigation.SettingsRoutes
import com.metrowatch.kochi.settings.navigation.settingsGraph
import com.metrowatch.kochi.timings.navigation.TimingRoutes
import com.metrowatch.kochi.timings.navigation.timingsGraph
import com.metrowatch.kochi.home.navigation.HomeRoutes
import com.metrowatch.kochi.home.navigation.homeGraph
import com.metrowatch.kochi.home.navigation.waterMetroHomeGraph
import com.metrowatch.kochi.navigation.appGraph
import androidx.navigation.toRoute
import com.metrowatch.kochi.navigation.Route
import com.metrowatch.kochi.station.navigation.StationRoutes
import com.metrowatch.kochi.ui.BackPressHandler
import com.metrowatch.kochi.ui.getTimeMillis
import com.metrowatch.kochi.ui.rememberPlatformActions
import com.metrowatch.kochi.ui.components.WebViewScreen
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun TabHost(
    modifier: Modifier = Modifier,
    padding: PaddingValues,
    appNavHostController: NavHostController,
    tab: BottomTab
) {
    val homeController = rememberNavController()
    val waterMetroController = rememberNavController()
    val settingsController = rememberNavController()
    val contactsController = rememberNavController()
    val navControllers = remember {
        linkedMapOf<BottomTab, NavHostController>(
            BottomTab.HOME to homeController,
            BottomTab.WATER_METRO to waterMetroController,
            BottomTab.CONTACTS to contactsController,
            BottomTab.SETTINGS to settingsController,
        )
    }

    val navController = navControllers.getValue(tab)

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = when (tab) {
            BottomTab.HOME -> HomeRoutes.HomePage
            BottomTab.WATER_METRO -> HomeRoutes.WaterMetroHomePage
            BottomTab.CONTACTS -> StationRoutes.Contacts
            BottomTab.SETTINGS -> SettingsRoutes.Settings
        }
    ) { // Screens directly tied to bottom navigation goes here
        stationsGraph(navController)
        homeGraph(navController) {
            appNavHostController.navigate(it)
        }
        waterMetroHomeGraph {
            appNavHostController.navigate(it)
        }
        settingsGraph(navController) {
            appNavHostController.navigate(it)
        }
    }
}

@Composable
fun RootNavHost() {
    val navController = rememberNavController()
    // Navigation-scoped result callback registry (CMP-safe, no global state)
    val resultCallbacks = remember {
        mutableMapOf<String, (Any) -> Unit>()
    }

    NavHost(
        navController = navController,
        startDestination = Route.Splash
    ) {
        composable<Route.Splash> {
            SplashScreen(
                viewModel = koinViewModel(),
                onSyncComplete = {
                    navController.navigate(HomeDestination.Tabs) {
                        popUpTo<Route.Splash> {
                            inclusive = true
                        }
                    }
                }
            )
        }
        // All screens displayed over (without) bottom navigation goes here
        composable<HomeDestination.Tabs> {
            HomeScaffold(navController)
        }
        stationsGraph(navController)
        homeGraph(navController) {
            navController.navigate(it)
        }
        timingsGraph(
            navController,
            onNavigate = { target ->
                navController.navigate(target)
            }
        )
        fareGraph(
            navController,
            onNavigate = { target ->
                navController.navigate(target)
            }
        )
        settingsGraph(navController) { target ->
            navController.navigate(target)
        }
        composable<Route.WebView> { backStackEntry ->
            val webView = backStackEntry.toRoute<Route.WebView>()
            WebViewScreen(
                title = webView.title,
                url = webView.url,
                onBack = { navController.popBackStack() }
            )
        }
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

    val platformActions = rememberPlatformActions()
    var lastBackPressTime by remember { mutableStateOf(0L) }

    BackPressHandler(enabled = true) {
        if (selectedTab != BottomTab.HOME) {
            selectedTab = BottomTab.HOME
        } else {
            val currentTime = getTimeMillis()
            if (currentTime - lastBackPressTime < 2000) {
                platformActions.exitApp()
            } else {
                lastBackPressTime = currentTime
                platformActions.showToast("Press back again to quit")
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            when (tab) {
                                BottomTab.HOME -> Icon(Icons.Outlined.Home, null)
                                BottomTab.WATER_METRO -> Icon(Icons.Default.DirectionsBoat, null)
                                BottomTab.CONTACTS -> Icon(Icons.Outlined.AccountBox, null)
                                BottomTab.SETTINGS -> Icon(Icons.Outlined.Settings, null)
                            }
                        },
                        label = { DisplayText(tab.label) }
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        TabHost(
            modifier = Modifier.padding(padding),
            padding = padding,
            appNavHostController = appNavController,
            tab = selectedTab
        )
    }
}
