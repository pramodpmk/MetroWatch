package com.metrowatch.kochi.di

import com.metrowatch.kochi.data.di.dataModule
import com.metrowatch.kochi.station.di.stationModule
import com.metrowatch.kochi.fare.di.fareModule
import com.metrowatch.kochi.settings.di.settingsModule
import com.metrowatch.kochi.timings.di.timingsModule
import com.metrowatch.kochi.home.di.homeModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) {
    startKoin {
        appDeclaration()
        modules(
            appModule,
            dataModule,
            timingsModule,
            stationModule,
            fareModule,
            homeModule,
            settingsModule
        )
    }
}
