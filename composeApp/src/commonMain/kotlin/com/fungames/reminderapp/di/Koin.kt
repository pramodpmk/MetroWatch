package com.fungames.reminderapp.di

import com.fungames.reminderapp.data.DatabaseDriverFactory
import org.koin.core.context.startKoin

fun initKoin(databaseDriverFactory: DatabaseDriverFactory) {
    startKoin {
        modules(appModule(databaseDriverFactory))
    }
}
