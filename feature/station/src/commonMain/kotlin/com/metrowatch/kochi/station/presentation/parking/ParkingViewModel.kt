package com.metrowatch.kochi.station.presentation.parking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metrowatch.kochi.station.domain.ParkingRate
import com.metrowatch.kochi.station.domain.ParkingInfoUseCase
import com.metrowatch.kochi.domain.DomainState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class VehicleRateGroup(
    val vehicleType: String,
    val commuter: ParkingRate?,
    val nonCommuter: ParkingRate?
)

data class ParkingUiState(
    val applicableFor: String = "",
    val effectiveFrom: String = "",
    val currency: String = "₹",
    val notes: List<String> = emptyList(),
    val vehicleGroups: List<VehicleRateGroup> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = ""
)

class ParkingViewModel(
    private val useCase: ParkingInfoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParkingUiState())
    val uiState: StateFlow<ParkingUiState> = _uiState

    init {
        loadParkingInfo()
    }

    private fun loadParkingInfo() {
        viewModelScope.launch {
            useCase().collect { state ->
                when (state) {
                    is DomainState.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                    is DomainState.Success -> {
                        val info = state.data
                        if (info == null) {
                            _uiState.value = _uiState.value.copy(isLoading = false)
                            return@collect
                        }
                        val groups = info.hourlyRates
                            .groupBy { it.vehicleType }
                            .map { (vehicleType, rates) ->
                                VehicleRateGroup(
                                    vehicleType = vehicleType,
                                    commuter = rates.find { it.isCommuter },
                                    nonCommuter = rates.find { !it.isCommuter }
                                )
                            }
                        _uiState.value = _uiState.value.copy(
                            applicableFor = info.applicableFor,
                            effectiveFrom = info.effectiveFrom,
                            currency = info.currency,
                            notes = info.notes,
                            vehicleGroups = groups,
                            isLoading = false,
                            isError = false
                        )
                    }
                    is DomainState.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = state.message ?: "Failed to load parking info"
                        )
                    }
                }
            }
        }
    }
}
