package com.fungames.core.station.domain

interface StationRepository {

    suspend fun stationList(): List<Station>
}
