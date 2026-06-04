package com.metrowatch.kochi.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metrowatch.kochi.navigation.Route
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class WaterMetroHomeViewModel : ViewModel() {
    private val _navigationEffect = MutableSharedFlow<Route>()
    val navigationEffect: SharedFlow<Route> = _navigationEffect

    fun onIntent(intent: WaterMetroHomePageIntent) {
        viewModelScope.launch {
            when (intent) {
                WaterMetroHomePageIntent.Stations -> _navigationEffect.emit(Route.WaterMetroStations)
                WaterMetroHomePageIntent.Routes -> _navigationEffect.emit(Route.WaterMetroRoutes)
                WaterMetroHomePageIntent.Timing -> _navigationEffect.emit(Route.WaterMetroTimings)
                WaterMetroHomePageIntent.Fare -> _navigationEffect.emit(Route.WaterMetroFare)
            }
        }
    }
}
