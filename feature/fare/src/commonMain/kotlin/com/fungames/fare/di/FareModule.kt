package com.fungames.fare.di

import com.fungames.fare.domain.CalculateFareUseCase
import com.fungames.fare.presentation.FareViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val fareModule = module {
    factoryOf(::CalculateFareUseCase)
    viewModelOf(::FareViewModel)
}
