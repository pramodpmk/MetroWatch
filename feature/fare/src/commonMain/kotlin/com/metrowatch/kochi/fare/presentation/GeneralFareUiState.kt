package com.metrowatch.kochi.fare.presentation

import com.metrowatch.kochi.fare.domain.GeneralFare

data class GeneralFareUiState(
    val fareList: List<GeneralFare> = emptyList(),
    val isLoading: Boolean = false
)
