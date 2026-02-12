package com.fungames.feature.timings.domain

interface TimingsRepository {
    suspend fun getTimings(departureName: String, arrivalName: String): List<TrainTiming>
}
