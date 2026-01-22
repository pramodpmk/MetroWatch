package com.fungames.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry

/**
 * CMP-safe navigation result handler.
 * Provides a navigation-scoped mechanism to pass results from child screens to parent screens.
 * 
 * Usage:
 * 1. Parent screen: Wrap content with NavigationResultHandler and use rememberNavigationResult()
 * 2. Child screen: Use LocalNavigationResultHandler.current to return result
 */
data class NavigationResultHandler(
    val onResult: (Any) -> Unit
)

val LocalNavigationResultHandler = compositionLocalOf<NavigationResultHandler?> { null }

/**
 * Provides navigation result handling capability to child composables.
 * This is scoped to the navigation entry and will be cleared when the entry is removed.
 */
@Composable
fun NavigationResultHandler(
    backStackEntry: NavBackStackEntry,
    onResult: (Any) -> Unit,
    content: @Composable () -> Unit
) {
    val handler = remember(backStackEntry.id) {
        NavigationResultHandler(onResult)
    }
    
    CompositionLocalProvider(LocalNavigationResultHandler provides handler) {
        content()
    }
}

/**
 * Remembers a navigation result handler for the current navigation entry.
 * Returns a function that can be used to handle results from child screens.
 */
@Composable
fun rememberNavigationResult(
    backStackEntry: NavBackStackEntry,
    onResult: (Any) -> Unit
): NavigationResultHandler {
    return remember(backStackEntry.id) {
        NavigationResultHandler(onResult)
    }
}

