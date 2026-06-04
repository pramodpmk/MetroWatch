package com.metrowatch.kochi.home.di

import com.metrowatch.kochi.home.presentation.HomeViewModel
import com.metrowatch.kochi.home.presentation.WaterMetroHomeViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val homeModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::WaterMetroHomeViewModel)
}
