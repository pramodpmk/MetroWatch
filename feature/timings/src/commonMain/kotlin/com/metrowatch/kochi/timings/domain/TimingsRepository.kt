package com.metrowatch.kochi.timings.domain

import com.metrowatch.kochi.data.db.StationEntity
import com.metrowatch.kochi.data.db.TimetableEntity

interface TimingsRepository {
    suspend fun getStationByName(name: String): StationEntity?
    suspend fun getStationsByLine(lineId: String): List<StationEntity>
    suspend fun getDistance(fromId: String, toId: String): Double?
    suspend fun getTimetablesByMode(mode: String): List<TimetableEntity>
}
