package com.metrowatch.kochi.home.presentation

sealed interface WaterMetroHomePageIntent {
    object Stations : WaterMetroHomePageIntent
    object Routes : WaterMetroHomePageIntent
    object Timing : WaterMetroHomePageIntent
    object Fare : WaterMetroHomePageIntent
}
