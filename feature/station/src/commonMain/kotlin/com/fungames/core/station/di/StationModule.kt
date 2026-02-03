package com.fungames.core.station.di

import com.fungames.core.station.data.StationRepositoryImpl
import com.fungames.core.station.domain.StationListUseCase
import com.fungames.core.station.domain.StationRepository
import com.fungames.core.station.presentation.StationViewModel
import com.fungames.core.station.presentation.picker.StationPickerViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val stationModule = module {
    singleOf(::StationRepositoryImpl) bind StationRepository::class
    singleOf(::StationListUseCase)
    viewModelOf(::StationViewModel)
    viewModelOf(::StationPickerViewModel)
}
