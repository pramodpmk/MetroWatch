package com.metrowatch.kochi.fare.domain

interface FareRepository {
    suspend fun getFareDetails(departureName: String, arrivalName: String): FareDetails?
}
