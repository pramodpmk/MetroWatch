package com.fungames.core.station.data

import com.fungames.core.data.db.StationDao
import com.fungames.core.data.db.StationEntity
import com.fungames.core.station.domain.Station
import com.fungames.core.station.domain.StationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StationRepositoryImpl(
    private val stationDao: StationDao
) : StationRepository {

    override suspend fun stationList(): List<Station> {
        return stationDao.getAllStationsList().map { it.toDomain() }
    }

    override fun getStations(): Flow<List<Station>> {
        return stationDao.getAllStations().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveStations(stations: List<Station>) {
        stationDao.insertStations(stations.map { it.toEntity() })
    }

    private fun StationEntity.toDomain() = Station(
        id = id,
        nameEn = nameEn,
        nameMl = nameMl,
        nameHi = nameHi,
        latitude = latitude,
        longitude = longitude,
        lineId = lineId,
        sequence = sequence,
        wheelchairAccessible = wheelchairAccessible
    )

    private fun Station.toEntity() = StationEntity(
        id = id,
        nameEn = nameEn,
        nameMl = nameMl,
        nameHi = nameHi,
        latitude = latitude,
        longitude = longitude,
        lineId = lineId,
        sequence = sequence,
        wheelchairAccessible = wheelchairAccessible
    )
}
