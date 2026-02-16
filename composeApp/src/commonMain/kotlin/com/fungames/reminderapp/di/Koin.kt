package com.fungames.reminderapp.di

import com.fungames.core.data.di.dataModule
import com.fungames.core.station.di.stationModule
import com.fungames.fare.di.fareModule
import com.fungames.feature.settings.di.settingsModule
import com.fungames.feature.timings.di.timingsModule
import com.fungames.trip.di.tripModule
import com.fungames.home.di.homeModule
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
            tripModule,
            homeModule,
            settingsModule
        )
    }
}
