package com.fungames.feature.timings.di

import com.fungames.feature.timings.presentation.TimingTableViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val timingsModule = module {
    viewModelOf(::TimingTableViewModel)
}
