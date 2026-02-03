package com.fungames.core.data.di

import com.fungames.core.data.db.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformDataModule: Module = module {
    single { getDatabaseBuilder(get()) }
}
