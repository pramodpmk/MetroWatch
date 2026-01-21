package com.fungames.core.station.di

import com.fungames.core.station.domain.StationListUseCase
import com.fungames.core.station.presentation.StationViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val stationModule = module {
    viewModelOf(::StationViewModel)
    singleOf(::StationListUseCase)
}
