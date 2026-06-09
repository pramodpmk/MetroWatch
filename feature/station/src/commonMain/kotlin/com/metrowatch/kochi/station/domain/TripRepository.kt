package com.metrowatch.kochi.station.domain

import com.metrowatch.kochi.data.db.FareSlabEntity
import com.metrowatch.kochi.data.db.StationEntity
import com.metrowatch.kochi.data.db.TimetableEntity

interface TripRepository {
    suspend fun getStationByName(name: String): StationEntity?
    suspend fun getStationsByLine(lineId: String): List<StationEntity>
    suspend fun getDistance(fromId: String, toId: String): Double?
    suspend fun getFareSlabs(): List<FareSlabEntity>
    suspend fun getTimetablesByMode(mode: String): List<TimetableEntity>
}
