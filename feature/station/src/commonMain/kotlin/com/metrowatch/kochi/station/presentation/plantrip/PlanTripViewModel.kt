package com.metrowatch.kochi.station.presentation.plantrip

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metrowatch.kochi.station.domain.PlanTripUseCase
import com.metrowatch.kochi.station.domain.TripTiming
import com.metrowatch.kochi.domain.DomainState
import com.metrowatch.kochi.ui.getLocalTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlanTripUiState(
    val departureStation: String = "",
    val departureStationId: String = "",
    val arrivalStation: String = "",
    val arrivalStationId: String = "",
    val distance: String = "",
    val fare: String = "",
    val numberOfStations: Int = 0,
    val lineName: String = "",
    val timings: List<TripTiming> = emptyList(),
    val showDetails: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPickingDeparture: Boolean = true,
    val lastUpdatedTime: String = ""
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
                                error = null,
                                lastUpdatedTime = currentTimeString()
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
                departureStationId = it.arrivalStationId,
                arrivalStation = it.departureStation,
                arrivalStationId = it.departureStationId
            )
        }
    }

    fun setPickingDeparture(isDeparture: Boolean) {
        _uiState.update { it.copy(isPickingDeparture = isDeparture) }
    }

    fun updateStation(name: String, id: String = "") {
        _uiState.update { state ->
            if (state.isPickingDeparture) {
                state.copy(departureStation = name, departureStationId = id)
            } else {
                state.copy(arrivalStation = name, arrivalStationId = id)
            }
        }
    }

    private fun currentTimeString(): String {
        val (h24, min) = getLocalTime()
        val ampm = if (h24 < 12) "AM" else "PM"
        val h = when {
            h24 == 0 -> 12
            h24 > 12 -> h24 - 12
            else -> h24
        }
        return "${h.toString().padStart(2, '0')}:${min.toString().padStart(2, '0')} $ampm"
    }
}
