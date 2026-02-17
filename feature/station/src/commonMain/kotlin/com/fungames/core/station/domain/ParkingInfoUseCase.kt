package com.fungames.core.station.domain

import com.fungames.domain.BaseUseCase
import com.fungames.domain.DomainState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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
        }
    }
}
