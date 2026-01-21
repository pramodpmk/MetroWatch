package com.fungames.core.station.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fungames.core.station.domain.StationListUseCase
import com.fungames.core.station.presentation.list.StationListUi
import com.fungames.domain.DomainState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StationViewModel(
    val stationListUseCase: StationListUseCase
) : ViewModel() {

    private val _stationListState = MutableStateFlow<StationListUi>(
        StationListUi.initData()
    )
    val stationListState: StateFlow<StationListUi> = _stationListState

    init {
        stationList()
    }

    private fun stationList() {
        viewModelScope.launch {
            stationListUseCase().collect { state ->
                when(state) {
                    is DomainState.Success -> {
                        _stationListState.value = stationListState.value.copy(
                            list = state.data,
                            isLoading = false,
                            isError = false
                        )
                    }
                    is DomainState.Error -> {
                        _stationListState.value = stationListState.value.copy(
                            isLoading = false,
                            isError = true
                        )
                    }
                    else -> {

                    }
                }
            }
        }
    }


    fun userIntent() {
        viewModelScope.launch {
            //_stationListState.value = "Latest"
        }
    }

}