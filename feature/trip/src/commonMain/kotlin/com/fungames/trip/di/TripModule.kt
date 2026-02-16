package com.fungames.trip.di

import com.fungames.trip.domain.GetTripDetailsUseCase
import com.fungames.trip.presentation.TripViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val tripModule = module {
    factoryOf(::GetTripDetailsUseCase)
    viewModelOf(::TripViewModel)
}
