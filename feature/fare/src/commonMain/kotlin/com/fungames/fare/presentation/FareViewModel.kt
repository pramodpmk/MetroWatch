package com.fungames.fare.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fungames.domain.DomainState
import com.fungames.fare.domain.CalculateFareUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FareUiState(
    val departureStation: String = "",
    val arrivalStation: String = "",
    val distance: String = "",
    val fare: String = "",
    val showDetails: Boolean = false,
    val isLoading: Boolean = false,
    val isPickingDeparture: Boolean = true
)

class FareViewModel(
    private val calculateFareUseCase: CalculateFareUseCase
) : ViewModel() {

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
        val currentState = _uiState.value
        viewModelScope.launch {
            calculateFareUseCase(currentState.departureStation, currentState.arrivalStation).collect { result ->
                when (result) {
                    is DomainState.Loading -> {
                        _uiState.update { it.copy(isLoading = true, showDetails = false) }
                    }
                    is DomainState.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                distance = result.data.distance,
                                fare = result.data.fare,
                                showDetails = true
                            )
                        }
                    }
                    is DomainState.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        // Handle error if needed
                    }
                }
            }
        }
    }

    fun swapStations() {
        _uiState.update {
            it.copy(
                departureStation = it.arrivalStation,
                arrivalStation = it.departureStation
            )
        }
    }

    fun setPickingDeparture(isDeparture: Boolean) {
        _uiState.update { it.copy(isPickingDeparture = isDeparture) }
    }

    fun updateStation(name: String) {
        _uiState.update { state ->
            if (state.isPickingDeparture) {
                state.copy(departureStation = name)
            } else {
                state.copy(arrivalStation = name)
            }
        }
    }
}
