package com.metrowatch.kochi.station.presentation.picker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metrowatch.kochi.station.domain.StationListUseCase
import com.metrowatch.kochi.domain.DomainState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StationPickerViewModel(
    private val stationListUseCase: StationListUseCase
) : ViewModel() {

    private val _stationPickerState = MutableStateFlow<StationPickerUi>(
        StationPickerUi.initData()
    )
    val stationPickerState: StateFlow<StationPickerUi> = _stationPickerState

    init {
        loadStations()
    }

    private fun loadStations() {
        viewModelScope.launch {
            _stationPickerState.update { it.copy(isLoading = true) }
            stationListUseCase().collect { state ->
                when (state) {
                    is DomainState.Success -> {
                        _stationPickerState.update {
                            it.copy(
                                allStations = state.data,
                                filteredStations = state.data,
                                isLoading = false,
                                isError = false
                            )
                        }
                    }
                    is DomainState.Error -> {
                        _stationPickerState.update {
                            it.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = "Failed to load stations"
                            )
                        }
                    }
                    is DomainState.Loading -> {
                        _stationPickerState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        viewModelScope.launch {
            val currentState = _stationPickerState.value
            val filtered = withContext(Dispatchers.Default) {
                if (query.isBlank()) {
                    currentState.allStations
                } else {
                    currentState.allStations.filter { station ->
                        station.name.contains(query, ignoreCase = true) ||
                                (station.nameMl?.contains(query, ignoreCase = true) ?: false) ||
                                (station.nameHi?.contains(query, ignoreCase = true) ?: false)
                    }
                }
            }
            _stationPickerState.update {
                it.copy(
                    searchQuery = query,
                    filteredStations = filtered
                )
            }
        }
    }
}

