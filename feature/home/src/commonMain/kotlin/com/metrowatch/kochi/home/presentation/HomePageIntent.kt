package com.metrowatch.kochi.home.presentation

sealed interface HomePageIntent {

    data class ClickOnStation(val station: NearestStation) : HomePageIntent

    object ClickedOnLocation : HomePageIntent

    object LocationFetching : HomePageIntent

    data class LocationGranted(val lat: Double, val lon: Double) : HomePageIntent

    object LocationDenied : HomePageIntent

    object ViewAllStations : HomePageIntent

    object FareCalculation : HomePageIntent

    object Timings : HomePageIntent

    object Settings: HomePageIntent

    object PlanTrip : HomePageIntent

    object WaterMetroStations : HomePageIntent

    object WaterMetroRoutes : HomePageIntent

    object Parking : HomePageIntent

    object MetroRoutes : HomePageIntent

    object WaterMetroTiming : HomePageIntent

    object WaterMetroFare : HomePageIntent

    object SelectFromStation : HomePageIntent

    object SelectToStation : HomePageIntent

    data class StationPickedForTrip(val name: String, val id: String) : HomePageIntent
}
