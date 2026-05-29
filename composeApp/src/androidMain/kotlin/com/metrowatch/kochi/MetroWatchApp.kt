package com.metrowatch.kochi

import android.app.Application
import com.metrowatch.kochi.di.initKoin
import org.koin.android.ext.koin.androidContext

class MetroWatchApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MetroWatchApp)
        }
    }
}
