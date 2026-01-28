package com.fungames.fare.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FareUiState(
    val departureStation: String = "SEEPZ",
    val arrivalStation: String = "CSMI Airport-T1",
    val distance: String = "6.26km",
    val fare: String = "₹30.00",
    val showDetails: Boolean = false
)

class FareViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FareUiState())
    val uiState: StateFlow<FareUiState> = _uiState.asStateFlow()

    private val _timingTableState = MutableStateFlow<String>("")
    val timingTableState: StateFlow<String> = _timingTableState

    fun userIntent() {
        viewModelScope.launch {
            _timingTableState.value = "Latest"
        }
    }

    fun calculateFare() {
        _uiState.update { it.copy(showDetails = true) }
    }

    fun swapStations() {
        _uiState.update {
            it.copy(
                departureStation = it.arrivalStation,
                arrivalStation = it.departureStation
            )
        }
    }

}
