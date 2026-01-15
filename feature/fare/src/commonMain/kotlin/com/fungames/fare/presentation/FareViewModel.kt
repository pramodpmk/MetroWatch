package com.fungames.fare.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FareViewModel : ViewModel() {

    private val _timingTableState = MutableStateFlow<String>("")
    val timingTableState: StateFlow<String> = _timingTableState

    fun userIntent() {
        viewModelScope.launch {
            _timingTableState.value = "Latest"
        }
    }

}