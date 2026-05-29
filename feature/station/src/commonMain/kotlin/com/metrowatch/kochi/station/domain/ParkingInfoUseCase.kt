package com.metrowatch.kochi.station.domain

import com.metrowatch.kochi.domain.BaseUseCase
import com.metrowatch.kochi.domain.DomainState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class ParkingInfoUseCase(
    private val stationRepository: StationRepository
) : BaseUseCase<ParkingInfo?>() {

    override fun invoke(): Flow<DomainState<ParkingInfo?>> {
        return flow {
            emit(DomainState.Loading)
            try {
                val result = stationRepository.getParkingInfo()
                emit(DomainState.Success(result))
            } catch (e: Exception) {
                emit(DomainState.Error(message = e.message ?: "Unknown error", throwable = e))
            }
        }.flowOn(Dispatchers.IO)
    }
}
