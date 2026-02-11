package com.fungames.fare.domain

interface FareRepository {
    suspend fun getFareDetails(departureName: String, arrivalName: String): FareDetails?
}
