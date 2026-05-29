package com.metrowatch.kochi.timings.di

import com.metrowatch.kochi.timings.data.TimingsRepositoryImpl
import com.metrowatch.kochi.timings.domain.CalculateTimingsUseCase
import com.metrowatch.kochi.timings.domain.TimingsRepository
import com.metrowatch.kochi.timings.presentation.TimingTableViewModel
import com.metrowatch.kochi.timings.presentation.detail.TimingDetailViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val timingsModule = module {
    factoryOf(::TimingsRepositoryImpl) bind TimingsRepository::class
    factoryOf(::CalculateTimingsUseCase)
    viewModelOf(::TimingTableViewModel)
    viewModelOf(::TimingDetailViewModel)
}
