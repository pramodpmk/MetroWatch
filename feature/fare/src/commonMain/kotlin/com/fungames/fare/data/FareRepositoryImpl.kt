package com.fungames.fare.data

import com.fungames.core.data.db.ConfigDao
import com.fungames.core.data.db.StationDao
import com.fungames.fare.domain.FareDetails
import com.fungames.fare.domain.FareRepository

class FareRepositoryImpl(
    private val stationDao: StationDao,
    private val configDao: ConfigDao
) : FareRepository {
    override suspend fun getFareDetails(departureName: String, arrivalName: String): FareDetails? {
        val departureId = stationDao.getStationIdByName(departureName) ?: return null
        val arrivalId = stationDao.getStationIdByName(arrivalName) ?: return null

        val distanceKm = configDao.getDistance(departureId, arrivalId) ?: return null
        val slabs = configDao.getFareSlabs()

        val matchingSlab = slabs.find { distanceKm >= it.minKm && distanceKm <= it.maxKm }
            ?: slabs.find { it.maxKm == 0.0 && distanceKm >= it.minKm } // Handle cases with no upper bound
            ?: slabs.lastOrNull()

        return matchingSlab?.let {
            FareDetails(
                distance = "${distanceKm} km",
                fare = "₹${it.fare}"
            )
        }
    }
}
