package com.fungames.core.station.domain

import com.fungames.domain.BaseUseCase
import com.fungames.domain.DomainState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class WaterMetroRoutesUseCase(
    private val stationRepository: StationRepository
) : BaseUseCase<List<WaterMetroRoute>>() {

    override fun invoke(): Flow<DomainState<List<WaterMetroRoute>>> {
        return flow {
            emit(DomainState.Loading)
            try {
                val result = stationRepository.getWaterMetroRoutes()
                emit(DomainState.Success(result))
            } catch (e: Exception) {
                emit(DomainState.Error(message = e.message ?: "Unknown error", throwable = e))
            }
        }.flowOn(Dispatchers.IO)
    }
}
