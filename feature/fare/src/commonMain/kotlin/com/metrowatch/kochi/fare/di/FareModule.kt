package com.metrowatch.kochi.fare.di

import com.metrowatch.kochi.fare.data.FareRepositoryImpl
import com.metrowatch.kochi.fare.domain.CalculateFareUseCase
import com.metrowatch.kochi.fare.domain.FareRepository
import com.metrowatch.kochi.fare.presentation.FareViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val fareModule = module {
    factoryOf(::FareRepositoryImpl) bind FareRepository::class
    factoryOf(::CalculateFareUseCase)
    viewModelOf(::FareViewModel)
}
