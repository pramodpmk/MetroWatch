package com.fungames.fare.presentation

import com.fungames.fare.domain.GeneralFare

data class GeneralFareUiState(
    val fareList: List<GeneralFare> = emptyList(),
    val isLoading: Boolean = false
)
