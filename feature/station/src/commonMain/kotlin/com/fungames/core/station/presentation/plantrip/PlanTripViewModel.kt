package com.fungames.core.station.presentation.plantrip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fungames.core.station.domain.PlanTripUseCase
import com.fungames.core.station.domain.TripDetails
import com.fungames.core.station.domain.TripTiming
import com.fungames.domain.DomainState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlanTripUiState(
    val departureStation: String = "",
    val arrivalStation: String = "",
    val distance: String = "",
    val fare: String = "",
    val numberOfStations: Int = 0,
    val lineName: String = "",
    val timings: List<TripTiming> = emptyList(),
    val showDetails: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPickingDeparture: Boolean = true
)

class PlanTripViewModel(
    private val planTripUseCase: PlanTripUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlanTripUiState())
    val uiState: StateFlow<PlanTripUiState> = _uiState.asStateFlow()

    fun calculateTrip() {
        val currentState = _uiState.value
        viewModelScope.launch {
            planTripUseCase(currentState.departureStation, currentState.arrivalStation).collect { result ->
                when (result) {
                    is DomainState.Loading -> {
                        _uiState.update { it.copy(isLoading = true, showDetails = false, error = null) }
                    }
                    is DomainState.Success -> {
                        val details = result.data
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                distance = details.distance,
                                fare = details.fare,
                                numberOfStations = details.numberOfStations,
                                lineName = details.lineName,
                                timings = details.timings,
                                showDetails = true,
                                error = null
                            )
                        }
                    }
                    is DomainState.Error -> {
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
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
