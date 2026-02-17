package com.fungames.core.station.domain

import com.fungames.domain.DomainState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class PlanTripUseCase(
    private val tripRepository: TripRepository
) {
    operator fun invoke(departure: String, arrival: String): Flow<DomainState<TripDetails>> = flow {
        emit(DomainState.Loading)
        try {
            val tripDetails = tripRepository.getTripDetails(departure, arrival)
            if (tripDetails != null) {
                emit(DomainState.Success(tripDetails))
            } else {
                emit(DomainState.Error("Could not calculate trip details for the selected stations"))
            }
        } catch (e: Exception) {
            emit(DomainState.Error(e.message ?: "An unknown error occurred", e))
        }
    }.flowOn(Dispatchers.IO)
}
