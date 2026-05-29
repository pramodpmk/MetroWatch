package com.metrowatch.kochi.navigation

import androidx.compose.runtime.staticCompositionLocalOf

val LocalNavigationResults =
    staticCompositionLocalOf<NavigationResults> {
        error("NavigationResults not provided")
    }

