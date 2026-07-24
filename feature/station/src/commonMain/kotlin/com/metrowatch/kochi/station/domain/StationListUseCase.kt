package com.metrowatch.kochi.station.domain

import com.metrowatch.kochi.domain.BaseUseCase
import com.metrowatch.kochi.domain.DomainState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class StationListUseCase(
    private val stationRepository: StationRepository
) : BaseUseCase<List<Station>>() {

    override fun invoke(): Flow<DomainState<List<Station>>> {
        return flow {
            emit(DomainState.Loading)
            try {
                var result = stationRepository.stationList()
                if (result.isEmpty()) {
                    val initialStations = getInitialStations()
                    stationRepository.saveStations(initialStations)
                    result = initialStations
                }
                emit(DomainState.Success(result))
            } catch (e: Exception) {
                emit(DomainState.Error(message = e.message ?: "Unknown error", throwable = e))
            }
        }.flowOn(Dispatchers.IO)
    }

    private fun getInitialStations() = listOf(
        Station(
            id = "ALW",
            nameEn = "Aluva",
            nameMl = "ആലുവ",
            nameHi = "आलुवा",
            latitude = 10.1086,
            longitude = 76.3547,
            lineId = "LINE1",
            sequence = 1,
            mode = "METRO",
            wheelchairAccessible = true,
            imageUrl = null
        ),
        Station(
            id = "AMBK",
            nameEn = "Ambattukavu",
            nameMl = "അമ്പാട്ടുകാവ്",
            nameHi = "अंबाट्टुकावू",
            latitude = 10.0886,
            longitude = 76.3347,
            lineId = "LINE1",
            sequence = 2,
            mode = "METRO",
            wheelchairAccessible = true,
            imageUrl = null
        ),
        Station(
            id = "COMP",
            nameEn = "Companypadi",
            nameMl = "കമ്പനിപ്പടി",
            nameHi = "कंपनीपडी",
            latitude = 10.0786,
            longitude = 76.3247,
            lineId = "LINE1",
            sequence = 3,
            mode = "METRO",
            wheelchairAccessible = true,
            imageUrl = null
        )
    )
}
