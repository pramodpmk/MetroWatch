package com.fungames.feature.timings.di

import com.fungames.feature.timings.data.TimingsRepositoryImpl
import com.fungames.feature.timings.domain.CalculateTimingsUseCase
import com.fungames.feature.timings.domain.TimingsRepository
import com.fungames.feature.timings.presentation.TimingTableViewModel
import com.fungames.feature.timings.presentation.detail.TimingDetailViewModel
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
