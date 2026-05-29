package com.metrowatch.kochi.station.domain

import com.metrowatch.kochi.domain.DomainState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MetroRoutesUseCase(
    private val stationRepository: StationRepository
) {
    operator fun invoke(): Flow<DomainState<List<MetroRoute>>> = flow {
        emit(DomainState.Loading)
        try {
            val stations = stationRepository.stationList()
            val routes = stations.groupBy { it.lineId }
                .map { (lineId, stationList) ->
                    MetroRoute(
                        lineId = lineId,
                        stations = stationList.sortedBy { it.sequence }.map { it.nameEn }
                    )
                }
            emit(DomainState.Success(routes))
        } catch (e: Exception) {
            emit(DomainState.Error(e.message ?: "Unknown error"))
        }
    }
}
