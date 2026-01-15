package com.fungames.feature.timings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fungames.core.navigation.Route
import com.fungames.feature.timings.navigation.TimingRoutes
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TimingTableViewModel : ViewModel() {

    private val _timingTableState = MutableStateFlow<String>("")
    val timingTableState: StateFlow<String> = _timingTableState
    private val _stationRoutingEffect = MutableSharedFlow<Int>()
    val stationRoutingEffect: SharedFlow<Int> = _stationRoutingEffect


    fun userIntent() {
        viewModelScope.launch {
            //_timingTableState.value = "Latest"
            _stationRoutingEffect.emit(1)
        }
    }

}