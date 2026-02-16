package com.fungames.trip.domain

import com.fungames.core.data.db.ConfigDao
import com.fungames.core.data.db.StationDao
import com.fungames.fare.domain.FareRepository
import com.fungames.feature.timings.domain.TimingsRepository
import com.fungames.domain.DomainState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetTripDetailsUseCase(
    private val stationDao: StationDao,
    private val fareRepository: FareRepository,
    private val timingsRepository: TimingsRepository
) {
    operator fun invoke(departure: String, arrival: String): Flow<DomainState<TripDetails>> = flow {
        emit(DomainState.Loading)
        try {
            val departureStation = stationDao.getStationByName(departure)
            val arrivalStation = stationDao.getStationByName(arrival)

            if (departureStation == null || arrivalStation == null) {
                emit(DomainState.Error("Invalid stations"))
                return@flow
            }

            val fareDetails = fareRepository.getFareDetails(departure, arrival)
            val timings = timingsRepository.getTimings(departure, arrival)

            val stationsCount = if (departureStation.lineId == arrivalStation.lineId) {
                val allStationsOnLine = stationDao.getStationsByLine(departureStation.lineId)
                    .sortedBy { it.sequence }
                val startSeq = minOf(departureStation.sequence, arrivalStation.sequence)
                val endSeq = maxOf(departureStation.sequence, arrivalStation.sequence)
                val count = allStationsOnLine.filter { it.sequence in startSeq..endSeq }.size
                if (count >= 2) count - 2 else 0
            } else {
                0 // For now, only same line supported as per FareRepositoryImpl
            }

            val lineName = departureStation.lineId.ifEmpty { "Blue Line" }

            if (fareDetails != null) {
                emit(DomainState.Success(
                    TripDetails(
                        distance = fareDetails.distance,
                        fare = fareDetails.fare,
                        stationsCount = stationsCount,
                        lineName = lineName,
                        timings = timings
                    )
                ))
            } else {
                emit(DomainState.Error("Could not plan trip for the selected stations"))
            }
        } catch (e: Exception) {
            emit(DomainState.Error(e.message ?: "An unknown error occurred", e))
        }
    }
}
