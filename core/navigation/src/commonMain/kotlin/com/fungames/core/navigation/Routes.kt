package com.fungames.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Timings : Route

    @Serializable
    data object AddReminder : Route

    @Serializable
    data object StationList : Route
}
