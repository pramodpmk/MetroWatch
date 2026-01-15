package com.fungames.fare.di

import com.fungames.fare.presentation.FareViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val fareModule = module {
    viewModelOf(::FareViewModel)
}
