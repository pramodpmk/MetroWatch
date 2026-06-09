package com.metrowatch.kochi.fare.data

import com.metrowatch.kochi.data.db.ConfigDao
import com.metrowatch.kochi.data.db.FareSlabEntity
import com.metrowatch.kochi.data.db.StationDao
import com.metrowatch.kochi.data.db.StationEntity
import com.metrowatch.kochi.fare.domain.FareRepository

class FareRepositoryImpl(
    private val stationDao: StationDao,
    private val configDao: ConfigDao
) : FareRepository {

    override suspend fun getStationByName(name: String): StationEntity? =
        stationDao.getStationByName(name)

    override suspend fun getStationsByLine(lineId: String): List<StationEntity> =
        stationDao.getStationsByLine(lineId).sortedBy { it.sequence }

    override suspend fun getDistance(fromId: String, toId: String): Double? =
        configDao.getDistance(fromId, toId)

    override suspend fun getFareSlabs(): List<FareSlabEntity> =
        configDao.getFareSlabs()
}
