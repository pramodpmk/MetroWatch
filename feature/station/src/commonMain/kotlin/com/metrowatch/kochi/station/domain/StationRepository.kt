package com.metrowatch.kochi.station.domain

import kotlinx.coroutines.flow.Flow

interface StationRepository {
    suspend fun stationList(): List<Station>
    fun getStations(): Flow<List<Station>>
    suspend fun saveStations(stations: List<Station>)

    suspend fun getWaterMetroRoutes(): List<WaterMetroRoute>
    suspend fun getWaterMetroStations(): List<WaterMetroStation>
    suspend fun getParkingInfo(): ParkingInfo?
    suspend fun getContacts(): List<Contact>
}
