package com.fungames.feature.timings.domain

import com.fungames.domain.DomainState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class CalculateTimingsUseCase(
    private val timingsRepository: TimingsRepository
) {
    operator fun invoke(departure: String, arrival: String): Flow<DomainState<List<TrainTiming>>> = flow {
        emit(DomainState.Loading)
        try {
            val timings = timingsRepository.getTimings(departure, arrival)
            if (timings.isNotEmpty()) {
                emit(DomainState.Success(timings))
            } else {
                emit(DomainState.Error("Could not calculate timings for the selected stations"))
            }
        } catch (e: Exception) {
            emit(DomainState.Error(e.message ?: "An unknown error occurred", e))
        }
    }.flowOn(Dispatchers.IO)
}
