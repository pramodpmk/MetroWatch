package com.metrowatch.kochi.station.presentation.watermetro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metrowatch.kochi.station.domain.WaterMetroRoute
import com.metrowatch.kochi.station.domain.WaterMetroRoutesUseCase
import com.metrowatch.kochi.domain.DomainState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class WaterMetroRoutesUiState(
    val routes: List<WaterMetroRoute> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = ""
)

class WaterMetroRoutesViewModel(
    private val useCase: WaterMetroRoutesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WaterMetroRoutesUiState())
    val uiState: StateFlow<WaterMetroRoutesUiState> = _uiState

    init {
        loadRoutes()
    }

    private fun loadRoutes() {
        viewModelScope.launch {
            useCase().collect { state ->
                when (state) {
                    is DomainState.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                    is DomainState.Success -> {
                        _uiState.value = _uiState.value.copy(
                            routes = state.data,
                            isLoading = false,
                            isError = false
                        )
                    }
                    is DomainState.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = state.message ?: "Failed to load routes"
                        )
                    }
                }
            }
        }
    }
}
