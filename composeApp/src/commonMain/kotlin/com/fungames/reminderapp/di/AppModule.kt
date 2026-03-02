package com.fungames.reminderapp.di

import com.fungames.reminderapp.presentation.SplashViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    // Add app-level dependencies here
    viewModelOf(::SplashViewModel)
}
