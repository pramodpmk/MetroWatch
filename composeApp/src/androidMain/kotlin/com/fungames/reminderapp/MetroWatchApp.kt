package com.fungames.reminderapp

import android.app.Application
import com.fungames.core.data.db.AppDatabase
import com.fungames.reminderapp.di.initKoin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class MetroWatchApp : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MetroWatchApp)
        }
        // Pre-warm Room DB and Ktor client off the main thread.
        // Koin singles are lazy — their first access normally blocks the main thread
        // during the first Compose frame (koinViewModel call), causing ~700ms jank.
        CoroutineScope(Dispatchers.IO).launch {
            get<AppDatabase>()
        }
    }
}
