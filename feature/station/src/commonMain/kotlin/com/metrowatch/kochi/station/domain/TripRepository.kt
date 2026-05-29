package com.metrowatch.kochi.station.domain

interface TripRepository {
    suspend fun getTripDetails(departureName: String, arrivalName: String): TripDetails?
}
