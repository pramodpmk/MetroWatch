package com.fungames.core.station.domain

import com.fungames.domain.BaseUseCase
import com.fungames.domain.DomainState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StationListUseCase : BaseUseCase<List<Station>>() {

    override fun invoke(): Flow<DomainState<List<Station>>> {
        return flow {
            emit(DomainState.Loading)
            val result = stationList()
            emit(DomainState.Success(result))

        }
    }

    private fun stationList() = listOf(
        Station(id = 1, name = "Aluva", code = "ALW", latInf = 1.0, lngInf = 2.0),
        Station(id = 2, name = "Ambattukavu", code = "ALW", latInf = 1.0, lngInf = 2.0),
        Station(id = 3, name = "Companypadi", code = "ALW", latInf = 1.0, lngInf = 2.0),
        Station(id = 4, name = "Appollo", code = "ALW", latInf = 1.0, lngInf = 2.0),
        Station(id = 5, name = "North Kalamassery", code = "ALW", latInf = 1.0, lngInf = 2.0),
        Station(id = 6, name = "CUSAT", code = "ALW", latInf = 1.0, lngInf = 2.0),
        Station(id = 7, name = "SOUTH KALAMASSERRY", code = "ALW", latInf = 1.0, lngInf = 2.0),
        Station(id = 8, name = "Pathadipalam", code = "ALW", latInf = 1.0, lngInf = 2.0),
        Station(id = 9, name = "Toll", code = "ALW", latInf = 1.0, lngInf = 2.0),
        Station(id = 10, name = "Lulu Mall", code = "ALW", latInf = 1.0, lngInf = 2.0),
        Station(id = 11, name = "High School", code = "ALW", latInf = 1.0, lngInf = 2.0),
        Station(id = 12, name = "Palarivattam", code = "ALW", latInf = 1.0, lngInf = 2.0),
        Station(id = 13, name = "Kaloor", code = "ALW", latInf = 1.0, lngInf = 2.0),
        Station(id = 14, name = "Lissie", code = "ALW", latInf = 1.0, lngInf = 2.0),
        Station(id = 15, name = "North Railway Station", code = "ALW", latInf = 1.0, lngInf = 2.0),
    )
}