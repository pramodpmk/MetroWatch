package com.fungames.feature.settings.di

import com.fungames.feature.settings.presentation.SettingsViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val settingsModule = module {
    viewModelOf(::SettingsViewModel)
}
