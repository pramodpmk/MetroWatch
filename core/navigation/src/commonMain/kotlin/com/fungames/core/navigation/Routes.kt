package com.fungames.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Timings : Route

    @Serializable
    data object AddReminder : Route

    @Serializable
    data object StationList : Route

    @Serializable
    data object StationDetail : Route

    @Serializable
    data object FareCalculation : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object StationPicker : Route

    @Serializable
    data object PlanTrip : Route

    @Serializable
    data object Splash : Route
}

@Serializable
sealed interface HomeDestination {

    /** Shell destination that hosts bottom navigation */
    @Serializable
    data object Tabs : HomeDestination
}