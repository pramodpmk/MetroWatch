package com.fungames.core.data.di

import com.fungames.core.data.db.AppDatabase
import com.fungames.core.data.db.getDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformDataModule: Module

val dataModule = module {
    includes(platformDataModule)
    single<AppDatabase> { getDatabase(get()) }
    single { get<AppDatabase>().stationDao() }
}
