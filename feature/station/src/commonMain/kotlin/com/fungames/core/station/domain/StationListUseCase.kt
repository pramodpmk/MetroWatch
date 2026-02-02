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
        Station(
            id = 1,
            name = "Aluva",
            code = "ALW",
            latInf = 10.1086,
            lngInf = 76.3547,
            nextTrain = "10:30 AM",
            parkingAvailability = "Available",
            gates = "Gate A, Gate B",
            contactDetails = "0484-2620001",
            address = "Aluva, Kochi, Kerala 683101"
        ),
        Station(
            id = 2,
            name = "Ambattukavu",
            code = "AMBK",
            latInf = 10.0886,
            lngInf = 76.3347,
            nextTrain = "10:45 AM",
            parkingAvailability = "Limited",
            gates = "Gate 1",
            contactDetails = "0484-2620002",
            address = "Ambattukavu, Kochi, Kerala 683106"
        ),
        Station(
            id = 3,
            name = "Companypadi",
            code = "COMP",
            latInf = 10.0786,
            lngInf = 76.3247,
            nextTrain = "11:00 AM",
            parkingAvailability = "Not Available",
            gates = "Main Gate",
            contactDetails = "0484-2620003",
            address = "Companypadi, Kochi, Kerala 683106"
        ),
        Station(
            id = 4,
            name = "Appollo",
            code = "APL",
            latInf = 10.0686,
            lngInf = 76.3147,
            nextTrain = "11:15 AM",
            parkingAvailability = "Available",
            gates = "Gate A",
            contactDetails = "0484-2620004",
            address = "Appollo Tyres, Kochi, Kerala 683106"
        ),
        Station(
            id = 5,
            name = "North Kalamassery",
            code = "NKLM",
            latInf = 10.0586,
            lngInf = 76.3047,
            nextTrain = "11:30 AM",
            parkingAvailability = "Available",
            gates = "Gate 1, Gate 2",
            contactDetails = "0484-2620005",
            address = "North Kalamassery, Kochi, Kerala 683104"
        ),
        Station(
            id = 6,
            name = "CUSAT",
            code = "CUSAT",
            latInf = 10.0486,
            lngInf = 76.2947,
            nextTrain = "11:45 AM",
            parkingAvailability = "Available",
            gates = "Main Entrance",
            contactDetails = "0484-2620006",
            address = "CUSAT, Kalamassery, Kochi, Kerala 682022"
        ),
        Station(
            id = 7,
            name = "SOUTH KALAMASSERRY",
            code = "SKLM",
            latInf = 10.0386,
            lngInf = 76.2847,
            nextTrain = "12:00 PM",
            parkingAvailability = "Limited",
            gates = "Gate A",
            contactDetails = "0484-2620007",
            address = "South Kalamassery, Kochi, Kerala 682033"
        ),
        Station(
            id = 8,
            name = "Pathadipalam",
            code = "PTP",
            latInf = 10.0286,
            lngInf = 76.2747,
            nextTrain = "12:15 PM",
            parkingAvailability = "Available",
            gates = "Gate 1",
            contactDetails = "0484-2620008",
            address = "Pathadipalam, Kochi, Kerala 682024"
        ),
        Station(
            id = 9,
            name = "Toll",
            code = "TOLL",
            latInf = 10.0186,
            lngInf = 76.2647,
            nextTrain = "12:30 PM",
            parkingAvailability = "Not Available",
            gates = "Main Gate",
            contactDetails = "0484-2620009",
            address = "Edappally Toll, Kochi, Kerala 682024"
        ),
        Station(
            id = 10,
            name = "Lulu Mall",
            code = "LULU",
            latInf = 10.0086,
            lngInf = 76.2547,
            nextTrain = "12:45 PM",
            parkingAvailability = "Available",
            gates = "Mall Gate, Main Gate",
            contactDetails = "0484-2620010",
            address = "Edappally, Kochi, Kerala 682024"
        ),
        Station(
            id = 11,
            name = "High School",
            code = "HS",
            latInf = 9.9986,
            lngInf = 76.2447,
            nextTrain = "01:00 PM",
            parkingAvailability = "Available",
            gates = "Gate 1",
            contactDetails = "0484-2620011",
            address = "Edappally, Kochi, Kerala 682024"
        ),
        Station(
            id = 12,
            name = "Palarivattam",
            code = "PLV",
            latInf = 9.9886,
            lngInf = 76.2347,
            nextTrain = "01:15 PM",
            parkingAvailability = "Available",
            gates = "Gate A, Gate B",
            contactDetails = "0484-2620012",
            address = "Palarivattam, Kochi, Kerala 682025"
        ),
        Station(
            id = 13,
            name = "Kaloor",
            code = "KLR",
            latInf = 9.9786,
            lngInf = 76.2247,
            nextTrain = "01:30 PM",
            parkingAvailability = "Available",
            gates = "Gate 1, Gate 2",
            contactDetails = "0484-2620013",
            address = "Kaloor, Kochi, Kerala 682017"
        ),
        Station(
            id = 14,
            name = "Lissie",
            code = "LSI",
            latInf = 9.9686,
            lngInf = 76.2147,
            nextTrain = "01:45 PM",
            parkingAvailability = "Limited",
            gates = "Gate A",
            contactDetails = "0484-2620014",
            address = "Lissie Junction, Kochi, Kerala 682018"
        ),
        Station(
            id = 15,
            name = "North Railway Station",
            code = "NRS",
            latInf = 9.9586,
            lngInf = 76.2047,
            nextTrain = "02:00 PM",
            parkingAvailability = "Available",
            gates = "Main Gate",
            contactDetails = "0484-2620015",
            address = "Kacheripady, Kochi, Kerala 682018"
        ),
    )
}
