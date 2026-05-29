package com.metrowatch.kochi.data.di

import com.metrowatch.kochi.data.api.ConfigApi
import com.metrowatch.kochi.data.db.AppDatabase
import com.metrowatch.kochi.data.db.getDatabase
import com.metrowatch.kochi.data.repo.SyncRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

expect val platformDataModule: Module

val dataModule = module {
    includes(platformDataModule)

    single {
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }

    single {
        HttpClient {
            install(ContentNegotiation) {
                json(get<Json>())
            }
            install(Logging) {
                level = LogLevel.ALL
                logger = object : Logger {
                    override fun log(message: String) {
                        println("KTOR: $message")
                    }
                }
            }
        }
    }

    single { ConfigApi(get()) }
    single { getDatabase(get()) }
    single { get<AppDatabase>().configDao() }
    single { get<AppDatabase>().stationDao() }

    singleOf(::SyncRepository)
}
