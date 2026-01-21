package com.fungames.core.station.presentation.list

import com.fungames.core.station.domain.Station

data class StationListUi(
    val list: List<Station>,
    val isLoading: Boolean,
    val isError: Boolean,
    val errorMessage: String,
) {
    companion object {
        fun initData() = StationListUi(
            list = emptyList(),
            isLoading = false,
            isError = false,
            errorMessage = ""
        )

    }
}