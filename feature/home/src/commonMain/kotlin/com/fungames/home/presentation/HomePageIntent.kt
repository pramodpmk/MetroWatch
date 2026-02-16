package com.fungames.home.presentation

sealed interface HomePageIntent {

    data class ClickOnStation(val station: NearestStation) : HomePageIntent

    object ClickedOnLocation: HomePageIntent

    object ViewAllStations: HomePageIntent

    object FareCalculation : HomePageIntent

    object Timings : HomePageIntent

    object Settings: HomePageIntent

    object PlanTrip : HomePageIntent

}
