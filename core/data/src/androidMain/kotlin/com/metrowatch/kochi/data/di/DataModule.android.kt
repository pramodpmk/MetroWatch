package com.metrowatch.kochi.data.di

import com.metrowatch.kochi.data.db.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformDataModule: Module = module {
    single { getDatabaseBuilder(get()) }
}
