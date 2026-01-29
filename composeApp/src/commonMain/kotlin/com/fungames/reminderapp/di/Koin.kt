package com.fungames.reminderapp.di

import com.fungames.core.station.di.stationModule
import com.fungames.fare.di.fareModule
import com.fungames.feature.settings.di.settingsModule
import com.fungames.feature.timings.di.timingsModule
import com.fungames.home.di.homeModule
import com.fungames.reminderapp.data.DatabaseDriverFactory
import org.koin.core.context.startKoin

fun initKoin(databaseDriverFactory: DatabaseDriverFactory) {
    startKoin {
        modules(
            appModule(databaseDriverFactory),
            timingsModule,
            stationModule,
            fareModule,
            homeModule,
            settingsModule
        )
    }
}
