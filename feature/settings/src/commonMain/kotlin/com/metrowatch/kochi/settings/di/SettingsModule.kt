package com.metrowatch.kochi.settings.di

import com.metrowatch.kochi.settings.presentation.SettingsViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val settingsModule = module {
    viewModelOf(::SettingsViewModel)
}
