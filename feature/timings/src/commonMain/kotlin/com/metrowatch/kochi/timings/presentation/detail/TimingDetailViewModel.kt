package com.metrowatch.kochi.timings.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metrowatch.kochi.domain.DomainState
import com.metrowatch.kochi.timings.domain.CalculateTimingsUseCase
import com.metrowatch.kochi.timings.domain.TrainTiming
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class TimingDetailUiState(
    val fromStation: String = "",
    val toStation: String = "",
    val timings: List<TrainTiming> = emptyList(),
    val isPickingFrom: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val showDetails: Boolean = false
)

class TimingDetailViewModel(
    private val calculateTimingsUseCase: CalculateTimingsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(TimingDetailUiState())
    val uiState: StateFlow<TimingDetailUiState> = _uiState.asStateFlow()

    fun calculateTimings() {
        calculateTimingsUseCase(uiState.value.fromStation, uiState.value.toStation)
            .onEach { state ->
                when (state) {
                    is DomainState.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                    is DomainState.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                timings = state.data,
                                showDetails = true
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
            }.launchIn(viewModelScope)
    }

    fun swapStations() {
        _uiState.update {
            it.copy(
                fromStation = it.toStation,
                toStation = it.fromStation,
                showDetails = false // Clear results on swap
            )
        }
    }

    fun setPickingFrom(isFrom: Boolean) {
        _uiState.update { it.copy(isPickingFrom = isFrom) }
    }

    fun updateStation(name: String) {
        _uiState.update { state ->
            if (state.isPickingFrom) {
                state.copy(fromStation = name, showDetails = false)
            } else {
                state.copy(toStation = name, showDetails = false)
            }
        }
    }
}
