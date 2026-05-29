package com.metrowatch.kochi.timings.domain

interface TimingsRepository {
    suspend fun getTimings(departureName: String, arrivalName: String): List<TrainTiming>
}
