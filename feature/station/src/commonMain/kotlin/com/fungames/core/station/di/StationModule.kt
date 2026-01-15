package com.fungames.core.station.di

import com.fungames.core.station.presentation.StationViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val stationModule = module {
    viewModelOf(::StationViewModel)
}
