package com.metrowatch.kochi.fare.domain

import com.metrowatch.kochi.domain.DomainState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.math.roundToInt

class CalculateFareUseCase(
    private val fareRepository: FareRepository
) {
    companion object {
        private const val METRO_AVG_SPEED_KMH = 35.0
    }

    operator fun invoke(departure: String, arrival: String): Flow<DomainState<FareDetails>> = flow {
        emit(DomainState.Loading)
        try {
            val departureStation = fareRepository.getStationByName(departure)
                ?: run {
                    emit(DomainState.Error("Station '$departure' not found"))
                    return@flow
                }
            val arrivalStation = fareRepository.getStationByName(arrival)
                ?: run {
                    emit(DomainState.Error("Station '$arrival' not found"))
                    return@flow
                }

            if (departureStation.lineId != arrivalStation.lineId) {
                emit(DomainState.Error("Stations are on different lines"))
                return@flow
            }

            val allStationsOnLine = fareRepository.getStationsByLine(departureStation.lineId)
            val startSeq = minOf(departureStation.sequence, arrivalStation.sequence)
            val endSeq = maxOf(departureStation.sequence, arrivalStation.sequence)
            val stationsInRange = allStationsOnLine.filter { it.sequence in startSeq..endSeq }

            var distanceKm = 0.0
            for (i in 0 until stationsInRange.size - 1) {
                distanceKm += fareRepository.getDistance(stationsInRange[i].id, stationsInRange[i + 1].id) ?: 0.0
            }

            val slabs = fareRepository.getFareSlabs()
            val matchingSlab = slabs.find { distanceKm >= it.minKm && distanceKm <= it.maxKm }
                ?: slabs.find { it.maxKm == 0.0 && distanceKm >= it.minKm }
                ?: slabs.lastOrNull()

            if (matchingSlab == null) {
                emit(DomainState.Error("No fare available for this route"))
                return@flow
            }

            val estimatedTimeMin = (distanceKm / METRO_AVG_SPEED_KMH * 60).roundToInt()
            val distanceNumber = ((distanceKm * 10).roundToInt()) / 10.0
            emit(DomainState.Success(
                FareDetails(
                    distance = "$distanceNumber km",
                    fare = "₹${matchingSlab.fare.toInt()}.00",
                    departureCode = departureStation.id,
                    arrivalCode = arrivalStation.id,
                    stops = stationsInRange.size,
                    estimatedTimeMin = estimatedTimeMin,
                    lineId = departureStation.lineId
                )
            ))
        } catch (e: Exception) {
            emit(DomainState.Error(e.message ?: "An unknown error occurred", e))
        }
    }.flowOn(Dispatchers.IO)
}
