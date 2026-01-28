package com.fungames.feature.timings.presentation.detail

import androidx.lifecycle.ViewModel
import com.fungames.feature.timings.domain.TrainTiming
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TimingDetailUiState(
    val fromStation: String = "Station A",
    val toStation: String = "Station B",
    val timings: List<TrainTiming> = emptyList(),
    val isPickingFrom: Boolean = true
)

class TimingDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TimingDetailUiState())
    val uiState: StateFlow<TimingDetailUiState> = _uiState.asStateFlow()

    init {
        loadTimings()
    }

    private fun loadTimings() {
        // Dummy data for now
        val dummyTimings = listOf(
            TrainTiming("12345", "Express A", "08:00 AM", "10:00 AM", "2h 00m"),
            TrainTiming("67890", "Superfast B", "12:00 PM", "02:30 PM", "2h 30m"),
            TrainTiming("11223", "Passenger C", "04:00 PM", "07:00 PM", "3h 00m"),
            TrainTiming("44556", "Night Express D", "10:00 PM", "12:00 AM", "2h 00m")
        )
        _uiState.update { it.copy(timings = dummyTimings) }
    }

    fun swapStations() {
        _uiState.update {
            it.copy(
                fromStation = it.toStation,
                toStation = it.fromStation
            )
        }
    }

    fun setPickingFrom(isFrom: Boolean) {
        _uiState.update { it.copy(isPickingFrom = isFrom) }
    }

    fun updateStation(name: String) {
        _uiState.update { state ->
            if (state.isPickingFrom) {
                state.copy(fromStation = name)
            } else {
                state.copy(toStation = name)
            }
        }
    }
}
