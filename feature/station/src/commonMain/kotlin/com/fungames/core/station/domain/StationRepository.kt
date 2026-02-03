package com.fungames.core.station.domain

import kotlinx.coroutines.flow.Flow

interface StationRepository {
    suspend fun stationList(): List<Station>
    fun getStations(): Flow<List<Station>>
    suspend fun saveStations(stations: List<Station>)
}
