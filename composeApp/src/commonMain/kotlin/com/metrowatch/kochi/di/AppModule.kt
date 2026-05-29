package com.metrowatch.kochi.di

import com.metrowatch.kochi.presentation.SplashViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    // Add app-level dependencies here
    viewModelOf(::SplashViewModel)
}
