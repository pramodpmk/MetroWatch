package com.fungames.core.station.presentation.watermetro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fungames.core.station.domain.WaterMetroStation
import com.fungames.core.station.domain.WaterMetroStationsUseCase
import com.fungames.domain.DomainState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class WaterMetroStationsUiState(
    val stations: List<WaterMetroStation> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = ""
)

class WaterMetroStationsViewModel(
    private val useCase: WaterMetroStationsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WaterMetroStationsUiState())
    val uiState: StateFlow<WaterMetroStationsUiState> = _uiState

    init {
        loadStations()
    }

    private fun loadStations() {
        viewModelScope.launch {
            useCase().collect { state ->
                when (state) {
                    is DomainState.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                    is DomainState.Success -> {
                        _uiState.value = _uiState.value.copy(
                            stations = state.data,
                            isLoading = false,
                            isError = false
                        )
                    }
                    is DomainState.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = state.message ?: "Failed to load stations"
                        )
                    }
                }
            }
        }
    }
}
