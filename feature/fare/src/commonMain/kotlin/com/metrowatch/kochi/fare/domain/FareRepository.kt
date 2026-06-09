package com.metrowatch.kochi.fare.domain

import com.metrowatch.kochi.data.db.FareSlabEntity
import com.metrowatch.kochi.data.db.StationEntity

interface FareRepository {
    suspend fun getStationByName(name: String): StationEntity?
    suspend fun getStationsByLine(lineId: String): List<StationEntity>
    suspend fun getDistance(fromId: String, toId: String): Double?
    suspend fun getFareSlabs(): List<FareSlabEntity>
}
