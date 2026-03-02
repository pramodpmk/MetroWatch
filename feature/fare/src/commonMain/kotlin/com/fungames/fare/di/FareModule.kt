package com.fungames.fare.di

import com.fungames.fare.data.FareRepositoryImpl
import com.fungames.fare.domain.CalculateFareUseCase
import com.fungames.fare.domain.FareRepository
import com.fungames.fare.presentation.FareViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val fareModule = module {
    factoryOf(::FareRepositoryImpl) bind FareRepository::class
    factoryOf(::CalculateFareUseCase)
    viewModelOf(::FareViewModel)
}
