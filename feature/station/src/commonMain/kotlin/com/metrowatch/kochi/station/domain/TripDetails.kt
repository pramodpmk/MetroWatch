package com.metrowatch.kochi.station.domain

data class TripDetails(
    val distance: String,
    val fare: String,
    val numberOfStations: Int,
    val lineName: String,
    val timings: List<TripTiming>
)

data class TripTiming(
    val trainNumber: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String
)
