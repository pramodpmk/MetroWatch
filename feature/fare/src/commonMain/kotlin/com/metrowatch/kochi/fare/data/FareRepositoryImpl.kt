package com.metrowatch.kochi.fare.data

import com.metrowatch.kochi.data.db.ConfigDao
import com.metrowatch.kochi.data.db.StationDao
import com.metrowatch.kochi.fare.domain.FareDetails
import com.metrowatch.kochi.fare.domain.FareRepository

class FareRepositoryImpl(
    private val stationDao: StationDao,
    private val configDao: ConfigDao
) : FareRepository {
    override suspend fun getFareDetails(departureName: String, arrivalName: String): FareDetails? {
        val departureStation = stationDao.getStationByName(departureName) ?: return null
        val arrivalStation = stationDao.getStationByName(arrivalName) ?: return null

        if (departureStation.lineId != arrivalStation.lineId) {
            return null
        }

        val allStationsOnLine = stationDao.getStationsByLine(departureStation.lineId)
            .sortedBy { it.sequence }

        val startSeq = minOf(departureStation.sequence, arrivalStation.sequence)
        val endSeq = maxOf(departureStation.sequence, arrivalStation.sequence)

        val stationsInRange = allStationsOnLine.filter { it.sequence in startSeq..endSeq }

        var distanceKm = 0.0
        for (i in 0 until stationsInRange.size - 1) {
            val s1 = stationsInRange[i]
            val s2 = stationsInRange[i+1]
            val stepDistance = configDao.getDistance(s1.id, s2.id) ?: return null
            distanceKm += stepDistance
        }

        val slabs = configDao.getFareSlabs()

        val matchingSlab = slabs.find { distanceKm >= it.minKm && distanceKm <= it.maxKm }
            ?: slabs.find { it.maxKm == 0.0 && distanceKm >= it.minKm } // Handle cases with no upper bound
            ?: slabs.lastOrNull()

        return matchingSlab?.let {
            FareDetails(
                distance = "$distanceKm km",
                fare = "₹${it.fare}"
            )
        }
    }
}
