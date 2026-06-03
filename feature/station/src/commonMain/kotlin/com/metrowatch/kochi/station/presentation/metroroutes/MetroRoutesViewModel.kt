package com.metrowatch.kochi.station.presentation.metroroutes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metrowatch.kochi.station.domain.MetroRoute
import com.metrowatch.kochi.station.domain.MetroRoutesUseCase
import com.metrowatch.kochi.domain.DomainState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MetroRoutesUiState(
    val isLoading: Boolean = false,
    val routes: List<MetroRoute> = emptyList(),
    val selectedRouteIndex: Int = 0,
    val isError: Boolean = false,
    val errorMessage: String = ""
) {
    val selectedRoute: MetroRoute? get() = routes.getOrNull(selectedRouteIndex)
}

class MetroRoutesViewModel(
    private val metroRoutesUseCase: MetroRoutesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MetroRoutesUiState())
    val uiState: StateFlow<MetroRoutesUiState> = _uiState

    init {
        loadRoutes()
    }

    fun onRouteSelected(index: Int) {
        _uiState.update { it.copy(selectedRouteIndex = index) }
    }

    private fun loadRoutes() {
        viewModelScope.launch {
            metroRoutesUseCase().collect { state ->
                when (state) {
                    is DomainState.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is DomainState.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                routes = state.data,
                                isError = false
                            )
                        }
                    }
                    is DomainState.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isError = true,
                                errorMessage = state.message
                            )
                        }
                    }
                }
            }
        }
    }
}
