package com.metrowatch.kochi.station.presentation.parking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metrowatch.kochi.station.domain.ParkingInfo
import com.metrowatch.kochi.station.domain.ParkingInfoUseCase
import com.metrowatch.kochi.domain.DomainState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ParkingUiState(
    val parkingInfo: ParkingInfo? = null,
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
                        _uiState.value = _uiState.value.copy(
                            parkingInfo = state.data,
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
