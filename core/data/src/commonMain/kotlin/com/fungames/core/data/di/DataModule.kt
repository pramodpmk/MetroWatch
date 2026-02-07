package com.fungames.core.data.di

import com.fungames.core.data.api.ConfigApi
import com.fungames.core.data.db.AppDatabase
import com.fungames.core.data.db.getDatabase
import com.fungames.core.data.repo.SyncRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformDataModule: Module

val dataModule = module {
    includes(platformDataModule)

    single {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    single {
        HttpClient {
            install(ContentNegotiation) {
                json(get())
            }
        }
    }

    single { ConfigApi(get()) }

    single<AppDatabase> { getDatabase(get()) }
    single { get<AppDatabase>().stationDao() }
    single { get<AppDatabase>().configDao() }

    single { SyncRepository(get(), get(), get()) }
}
