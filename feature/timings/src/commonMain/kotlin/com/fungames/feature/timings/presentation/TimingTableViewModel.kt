package com.fungames.feature.timings.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class TimingInfo(
    val title: String,
    val timing: String
)

class TimingTableViewModel : ViewModel() {

    private val _timingTableState = MutableStateFlow<List<TimingInfo>>(emptyList())
    val timingTableState: StateFlow<List<TimingInfo>> = _timingTableState

    init {
        _timingTableState.value = listOf(
            TimingInfo("Monday to Saturday", "06:00 AM - 10:00 PM"),
            TimingInfo("Sunday", "08:00 AM - 10:00 PM"),
            TimingInfo("Public Holidays", "08:00 AM - 10:00 PM")
        )
    }
}
