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
    private val _waterMetroTimingState = MutableStateFlow<List<TimingInfo>>(emptyList())
    val waterMetroTimingState: StateFlow<List<TimingInfo>> = _waterMetroTimingState

    init {
        _timingTableState.value = listOf(
            TimingInfo("Monday to Saturday", "06:00 am - 10:00 pm"),
            TimingInfo("Sunday", "08:00 am - 10:00 pm"),
            TimingInfo("Public holidays", "08:00 am - 10:00 pm")
        )
        _waterMetroTimingState.value = listOf(
            TimingInfo("Kakkanad to Vyttila", "08:05 am - 07:30 pm"),
            TimingInfo("Vyttila to Kakkanad", "07:35 am - 07:00 pm"),
            TimingInfo("High Court to Fort Kochi", "08:00 am - 08:30 pm"),
            TimingInfo("Fort Kochi to High Court", "08:25 am - 08:55 pm"),
        )
    }
}
