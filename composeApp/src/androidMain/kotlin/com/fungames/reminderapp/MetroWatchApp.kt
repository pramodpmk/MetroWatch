package com.fungames.reminderapp

import android.app.Application
import com.fungames.reminderapp.di.initKoin
import org.koin.android.ext.koin.androidContext

class MetroWatchApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MetroWatchApp)
        }
    }
}
