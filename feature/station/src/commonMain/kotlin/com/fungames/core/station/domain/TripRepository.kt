package com.fungames.core.station.domain

interface TripRepository {
    suspend fun getTripDetails(departureName: String, arrivalName: String): TripDetails?
}
