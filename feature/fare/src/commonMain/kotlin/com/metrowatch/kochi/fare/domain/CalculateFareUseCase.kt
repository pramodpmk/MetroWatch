package com.metrowatch.kochi.fare.domain

import com.metrowatch.kochi.domain.DomainState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class CalculateFareUseCase(
    private val fareRepository: FareRepository
) {
    operator fun invoke(departure: String, arrival: String): Flow<DomainState<FareDetails>> = flow {
        emit(DomainState.Loading)
        try {
            val fareDetails = fareRepository.getFareDetails(departure, arrival)
            if (fareDetails != null) {
                emit(DomainState.Success(fareDetails))
            } else {
                emit(DomainState.Error("Could not calculate fare for the selected stations"))
            }
        } catch (e: Exception) {
            emit(DomainState.Error(e.message ?: "An unknown error occurred", e))
        }
    }.flowOn(Dispatchers.IO)
}
