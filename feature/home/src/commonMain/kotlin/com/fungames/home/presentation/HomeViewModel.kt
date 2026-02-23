package com.fungames.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fungames.core.data.db.StationDao
import com.fungames.core.navigation.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

class HomeViewModel(
    private val stationDao: StationDao
) : ViewModel() {

    private val _homeState = MutableStateFlow<HomePageUi>(HomePageUi.initData())
    val homeState: StateFlow<HomePageUi> = _homeState
    private val _homeNavigationEffect = MutableSharedFlow<Route>()
    val homeNavigationEffect: SharedFlow<Route> = _homeNavigationEffect

    init {
        loadHomePage()
    }

    private fun loadHomePage() {
        _homeState.value = HomePageUi.initData().copy(
            locationText = "Tap 'Change' to find nearest station",
            nearestStationAvailable = false,
            pageState = PageState.Success
        )
    }

    fun userIntent(homeIntent: HomePageIntent) {
        viewModelScope.launch {
            when (homeIntent) {
                is HomePageIntent.ClickOnStation -> {
                    _homeNavigationEffect.emit(Route.StationDetail)
                }

                is HomePageIntent.ClickedOnLocation -> {
                    // Handled directly from the composable via rememberLocationPermissionLauncher
                }

                is HomePageIntent.LocationGranted -> {
                    findNearestStation(homeIntent.lat, homeIntent.lon)
                }

                is HomePageIntent.LocationDenied -> {
                    _homeState.value = _homeState.value.copy(
                        locationText = "Location access denied",
                        nearestStationAvailable = false
                    )
                }

                is HomePageIntent.ViewAllStations -> {
                    _homeNavigationEffect.emit(Route.StationList)
                }

                is HomePageIntent.FareCalculation -> {
                    _homeNavigationEffect.emit(Route.FareCalculation)
                }

                is HomePageIntent.Timings -> {
                    _homeNavigationEffect.emit(Route.Timings)
                }

                is HomePageIntent.Settings -> {}

                is HomePageIntent.PlanTrip -> {
                    _homeNavigationEffect.emit(Route.PlanTrip)
                }

                is HomePageIntent.WaterMetroStations -> {
                    _homeNavigationEffect.emit(Route.WaterMetroStations)
                }

                is HomePageIntent.WaterMetroRoutes -> {
                    _homeNavigationEffect.emit(Route.WaterMetroRoutes)
                }

                is HomePageIntent.Parking -> {
                    _homeNavigationEffect.emit(Route.Parking)
                }

                is HomePageIntent.Contacts -> {
                    _homeNavigationEffect.emit(Route.Contacts)
                }
            }
        }
    }

    private fun findNearestStation(lat: Double, lon: Double) {
        viewModelScope.launch {
            _homeState.value = _homeState.value.copy(
                locationText = "Searching...",
                nearestStationAvailable = false
            )

            val stations = withContext(Dispatchers.IO) { stationDao.getAllStationsList() }

            if (stations.isEmpty()) {
                _homeState.value = _homeState.value.copy(
                    locationText = "No stations available",
                    nearestStationAvailable = false
                )
                return@launch
            }

            val nearest = stations.minByOrNull { haversineDistance(lat, lon, it.latitude, it.longitude) }

            if (nearest != null) {
                val distanceKm = haversineDistance(lat, lon, nearest.latitude, nearest.longitude)
                val distanceText = if (distanceKm < 1.0) {
                    "${(distanceKm * 1000).roundToInt()} m away"
                } else {
                    val tenths = (distanceKm * 10).roundToInt()
                    "${tenths / 10}.${tenths % 10} km away"
                }

                _homeState.value = _homeState.value.copy(
                    locationText = nearest.nameEn,
                    locationLatitude = lat,
                    locationLongitude = lon,
                    nearestStation = NearestStation(
                        id = nearest.id.hashCode(),
                        stationName = nearest.nameEn,
                        stationCode = nearest.id,
                        distanceToStation = distanceText,
                        nextTrainTo = "",
                        nextTrainTime = "",
                        line = nearest.lineId,
                        platform = "",
                        locationLatitude = nearest.latitude.toLong(),
                        locationLongitude = nearest.longitude.toLong()
                    ),
                    nearestStationAvailable = true
                )
            } else {
                _homeState.value = _homeState.value.copy(
                    locationText = "No station found nearby",
                    nearestStationAvailable = false
                )
            }
        }
    }

    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = (lat2 - lat1) * PI / 180.0
        val dLon = (lon2 - lon1) * PI / 180.0
        val a = sin(dLat / 2).pow(2.0) +
                cos(lat1 * PI / 180.0) * cos(lat2 * PI / 180.0) * sin(dLon / 2).pow(2.0)
        val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))
        return r * c
    }
}
