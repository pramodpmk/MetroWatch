package com.metrowatch.kochi.station.presentation.picker

import com.metrowatch.kochi.station.domain.Station

data class StationPickerUi(
    val allStations: List<Station>,
    val filteredStations: List<Station>,
    val searchQuery: String,
    val isLoading: Boolean,
    val isError: Boolean,
    val errorMessage: String,
) {
    companion object {
        fun initData() = StationPickerUi(
            allStations = emptyList(),
            filteredStations = emptyList(),
            searchQuery = "",
            isLoading = false,
            isError = false,
            errorMessage = ""
        )
    }
}

