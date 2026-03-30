package com.fungames.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fungames.core.data.db.ConfigDao
import com.fungames.core.data.db.StationDao
import com.fungames.core.navigation.Route
import com.fungames.core.ui.getLocalTime
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
    private val stationDao: StationDao,
    private val configDao: ConfigDao
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
                    _homeNavigationEffect.emit(Route.StationDetail(homeIntent.station.stationId))
                }

                is HomePageIntent.ClickedOnLocation -> {
                    // Handled directly from the composable via rememberLocationPermissionLauncher
                }

                is HomePageIntent.LocationFetching -> {
                    _homeState.value = _homeState.value.copy(
                        locationText = "Fetching location...",
                        nearestStationAvailable = false
                    )
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

                is HomePageIntent.WaterMetroTiming -> {
                    _homeNavigationEffect.emit(Route.WaterMetroTimings)
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

                is HomePageIntent.MetroRoutes -> {
                    _homeNavigationEffect.emit(Route.MetroRoutes)
                }

                is HomePageIntent.WaterMetroFare -> {
                    _homeNavigationEffect.emit(Route.WaterMetroFare)
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

                val currentTime = getLocalTime()
                val currentMinutes = currentTime.first * 60 + currentTime.second

                val timetable = withContext(Dispatchers.IO) { configDao.getTimetableByMode(nearest.mode) }
                var nextTrainTimeStr = ""
                if (timetable != null) {
                    val startMinutes = parseTimeToMinutes(timetable.startTime)
                    val endMinutes = parseTimeToMinutes(timetable.endTime)
                    val frequency = timetable.frequencyMinutes

                    if (currentMinutes < startMinutes) {
                        nextTrainTimeStr = timetable.startTime
                    } else if (currentMinutes >= endMinutes) {
                        nextTrainTimeStr = "Tomorrow"
                    } else {
                        val minutesSinceStart = currentMinutes - startMinutes
                        val nextIn = frequency - (minutesSinceStart % frequency)
                        nextTrainTimeStr = formatMinutesToTime(currentMinutes + nextIn)
                    }
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
                        nextTrainTime = nextTrainTimeStr,
                        line = nearest.lineId,
                        stationId = nearest.id,
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

    private fun parseTimeToMinutes(timeStr: String): Int {
        val normalized = timeStr.lowercase().replace("am", " am").replace("pm", " pm").trim()
        val parts = normalized.split(Regex("\\s+"))
        if (parts.size < 2) return 0
        val timeParts = parts[0].split(":")
        if (timeParts.size < 2) return 0
        var hour = timeParts[0].toIntOrNull() ?: 0
        val minute = timeParts[1].toIntOrNull() ?: 0
        val ampm = parts[1]

        if (ampm == "pm" && hour < 12) hour += 12
        if (ampm == "am" && hour == 12) hour = 0
        return hour * 60 + minute
    }

    private fun formatMinutesToTime(minutes: Int): String {
        val hour = (minutes / 60) % 24
        val min = minutes % 60
        val ampm = if (hour < 12) "am" else "pm"
        val h = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        return "${h.toString().padStart(2, '0')}:${min.toString().padStart(2, '0')} $ampm"
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
