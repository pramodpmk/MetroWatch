package com.metrowatch.kochi.presentation

import com.metrowatch.kochi.ui.components.DisplayText
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DirectionsBoat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.DirectionsBoat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.metrowatch.kochi.ui.theme.BrandBlue
import com.metrowatch.kochi.ui.theme.BrandGray
import com.metrowatch.kochi.ui.theme.BrandWhite
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
import com.metrowatch.kochi.settings.presentation.SettingsScreen
import com.metrowatch.kochi.timings.navigation.TimingRoutes
import com.metrowatch.kochi.timings.navigation.timingsGraph
import com.metrowatch.kochi.home.presentation.WaterMetroHomeRoute
import com.metrowatch.kochi.navigation.appGraph
import androidx.navigation.toRoute
import com.metrowatch.kochi.navigation.Route
import com.metrowatch.kochi.station.presentation.contact.ContactsRoute
import com.metrowatch.kochi.ui.BackPressHandler
import com.metrowatch.kochi.ui.getTimeMillis
import com.metrowatch.kochi.ui.rememberPlatformActions
import com.metrowatch.kochi.ui.components.WebViewScreen
import org.koin.compose.viewmodel.koinViewModel
import HomeRoute


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
            NavigationBar(
                modifier = Modifier.clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                containerColor = BrandWhite,
                tonalElevation = 8.dp
            ) {
                tabs.forEach { tab ->
                    val selected = selectedTab == tab
                    NavigationBarItem(
                        selected = selected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = when (tab) {
                                    BottomTab.HOME -> if (selected) Icons.Filled.Home else Icons.Outlined.Home
                                    BottomTab.WATER_METRO -> if (selected) Icons.Filled.DirectionsBoat else Icons.Outlined.DirectionsBoat
                                    BottomTab.CONTACTS -> if (selected) Icons.Filled.AccountBox else Icons.Outlined.AccountBox
                                    BottomTab.SETTINGS -> if (selected) Icons.Filled.Settings else Icons.Outlined.Settings
                                },
                                contentDescription = null
                            )
                        },
                        label = {
                            DisplayText(
                                text = tab.label,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BrandWhite,
                            selectedTextColor = BrandBlue,
                            indicatorColor = BrandBlue,
                            unselectedIconColor = BrandGray,
                            unselectedTextColor = BrandGray
                        )
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (selectedTab) {
                BottomTab.HOME -> HomeRoute(
                    navHostController = appNavController,
                    onNavigate = { appNavController.navigate(it) },
                    viewModel = koinViewModel()
                )
                BottomTab.WATER_METRO -> WaterMetroHomeRoute(
                    onNavigate = { appNavController.navigate(it) },
                    viewModel = koinViewModel()
                )
                BottomTab.CONTACTS -> ContactsRoute(appNavController)
                BottomTab.SETTINGS -> SettingsScreen(onNavigate = { appNavController.navigate(it) })
            }
        }
    }
}
