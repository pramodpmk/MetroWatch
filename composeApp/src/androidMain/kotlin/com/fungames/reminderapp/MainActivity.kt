package com.fungames.reminderapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.fungames.reminderapp.data.DatabaseDriverFactory
import com.fungames.reminderapp.di.initKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initKoin(DatabaseDriverFactory(this))
        setContent {
            App()
        }
    }
}
