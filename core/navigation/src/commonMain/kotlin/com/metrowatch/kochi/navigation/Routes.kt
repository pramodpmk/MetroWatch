package com.metrowatch.kochi.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object HomeGraph : Route

    @Serializable
    data object StationGraph : Route

    @Serializable
    data object TimingsGraph : Route

    @Serializable
    data object FareGraph : Route

    @Serializable
    data object SettingsGraph : Route

    @Serializable
    data class StationDetail(val stationId: String) : Route

    @Serializable
    data object StationList : Route

    @Serializable
    data object FareCalculation : Route

    @Serializable
    data object Timings : Route

    @Serializable
    data object WaterMetroTimings : Route

    @Serializable
    data object PlanTrip : Route

    @Serializable
    data object WaterMetroStations : Route

    @Serializable
    data object WaterMetroRoutes : Route

    @Serializable
    data object Parking : Route

    @Serializable
    data object MetroRoutes : Route

    @Serializable
    data object WaterMetroFare : Route

    @Serializable
    data object StationPicker : Route

    @Serializable
    data object Splash : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object Contacts : Route

    @Serializable
    data class WebView(val title: String, val url: String) : Route
}

@Serializable
sealed interface HomeDestination : Route {
    @Serializable
    data object Home : HomeDestination

    @Serializable
    data object Tabs : HomeDestination
}
