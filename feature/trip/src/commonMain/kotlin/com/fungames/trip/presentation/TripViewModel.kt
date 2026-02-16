package com.fungames.trip.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fungames.domain.DomainState
import com.fungames.trip.domain.GetTripDetailsUseCase
import com.fungames.trip.domain.TripDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TripUiState(
    val departureStation: String = "Select Station",
    val arrivalStation: String = "Select Station",
    val isLoading: Boolean = false,
    val showDetails: Boolean = false,
    val tripDetails: TripDetails? = null,
    val error: String? = null,
    val isPickingDeparture: Boolean = true
)

class TripViewModel(
    private val getTripDetailsUseCase: GetTripDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripUiState())
    val uiState: StateFlow<TripUiState> = _uiState.asStateFlow()

    fun setPickingDeparture(isDeparture: Boolean) {
        _uiState.update { it.copy(isPickingDeparture = isDeparture) }
    }

    fun updateStation(stationName: String) {
        _uiState.update {
            if (it.isPickingDeparture) {
                it.copy(departureStation = stationName, showDetails = false)
            } else {
                it.copy(arrivalStation = stationName, showDetails = false)
            }
        }
    }

    fun swapStations() {
        _uiState.update {
            it.copy(
                departureStation = it.arrivalStation,
                arrivalStation = it.departureStation,
                showDetails = false
            )
        }
    }

    fun planTrip() {
        val departure = _uiState.value.departureStation
        val arrival = _uiState.value.arrivalStation

        if (departure == "Select Station" || arrival == "Select Station") {
            _uiState.update { it.copy(error = "Please select both stations") }
            return
        }

        if (departure == arrival) {
            _uiState.update { it.copy(error = "Departure and arrival stations cannot be the same") }
            return
        }

        viewModelScope.launch {
            getTripDetailsUseCase(departure, arrival).collect { state ->
                when (state) {
                    is DomainState.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null, showDetails = false) }
                    }
                    is DomainState.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                showDetails = true,
                                tripDetails = state.data,
                                error = null
                            )
                        }
                    }
                    is DomainState.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = state.message,
                                showDetails = false
                            )
                        }
                    }
                }
            }
        }
    }
}
