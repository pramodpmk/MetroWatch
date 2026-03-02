package com.fungames.core.station.presentation.metroroutes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fungames.core.station.domain.MetroRoute
import com.fungames.core.station.domain.MetroRoutesUseCase
import com.fungames.domain.DomainState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class MetroRoutesUiState(
    val isLoading: Boolean = false,
    val routes: List<MetroRoute> = emptyList(),
    val isError: Boolean = false,
    val errorMessage: String = ""
)

class MetroRoutesViewModel(
    private val metroRoutesUseCase: MetroRoutesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MetroRoutesUiState())
    val uiState: StateFlow<MetroRoutesUiState> = _uiState

    init {
        loadRoutes()
    }

    private fun loadRoutes() {
        viewModelScope.launch {
            metroRoutesUseCase().collect { state ->
                when (state) {
                    is DomainState.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                    is DomainState.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            routes = state.data,
                            isError = false
                        )
                    }
                    is DomainState.Error -> {
                        _uiState.value = _uiState.value.copy(
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
