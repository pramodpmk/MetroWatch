package com.fungames.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fungames.core.navigation.Route
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _homeState = MutableStateFlow<HomePageUi>(HomePageUi.initData())
    val homeState: StateFlow<HomePageUi> = _homeState
    private val _homeNavigationEffect = MutableSharedFlow<Route>()
    val homeNavigationEffect: SharedFlow<Route> = _homeNavigationEffect

    init {
        loadHomePage()
    }

    private fun loadHomePage() {
        viewModelScope.launch {
            val locationUseCase = flow {
                emit("Downtown Area")
            }
            val nearestStationUseCase = flow {
                emit(
                    NearestStation(
                        id = 1,
                        stationName = "Central Station",
                        stationCode = "CEN",
                        distanceToStation = "0.8 km away",
                        nextTrainTo = "",
                        nextTrainTime = "3 min",
                        line = "Blue Line",
                        platform = "Platform 2",
                        locationLatitude = 0,
                        locationLongitude = 0
                    )
                )
            }
            val stationListUseCase = flow {
                emit(
                    listOf(
                        NearestStation(
                            id = 1,
                            stationName = "Kadavanthra",
                            stationCode = "KAD",
                            distanceToStation = "100m",
                            nextTrainTo = "",
                            nextTrainTime = "",
                            locationLatitude = 0,
                            locationLongitude = 0
                        ),
                        NearestStation(
                            id = 1,
                            stationName = "Vyttila",
                            stationCode = "VYT",
                            distanceToStation = "100m",
                            nextTrainTo = "",
                            nextTrainTime = "",
                            locationLatitude = 0,
                            locationLongitude = 0
                        ),
                        NearestStation(
                            id = 1,
                            stationName = "Kakkanad",
                            stationCode = "KKD",
                            distanceToStation = "100m",
                            nextTrainTo = "",
                            nextTrainTime = "",
                            locationLatitude = 0,
                            locationLongitude = 0
                        )
                    )
                )
            }
            delay(2000L)
            combine(
                locationUseCase,
                nearestStationUseCase,
                stationListUseCase
            ) { location, nearest, stations ->
                _homeState.value.copy(
                    locationText = location,
                    nearestStation = nearest,
                    stationList = stations,
                    pageState = PageState.Success
                )
            }.collect { state ->
                _homeState.value = state
            }
        }
    }

    fun userIntent(homeIntent: HomePageIntent) {
        viewModelScope.launch {
            when (homeIntent) {
                is HomePageIntent.ClickOnStation -> {
                    // Fetch Station details and emit redirection effect
                    _homeNavigationEffect.emit(Route.StationDetail)
                }

                is HomePageIntent.ClickedOnLocation -> {
                    // Fetch location and emit redirection effect
                }

                is HomePageIntent.ViewAllStations -> {
                    // Emit redirection effect
                    _homeNavigationEffect.emit(Route.StationList)
                }

                is HomePageIntent.FareCalculation -> {
                    // Emit redirection effect
                    _homeNavigationEffect.emit(Route.FareCalculation)
                }

                is HomePageIntent.Timings -> {
                    // Emit redirection effect
                    _homeNavigationEffect.emit(Route.Timings)
                }

                is HomePageIntent.Settings -> {
                    // Emit redirection effect
                }
            }
        }
    }

}