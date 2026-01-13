package com.fungames.reminderapp.di

import com.fungames.reminderapp.data.DatabaseDriverFactory
import com.fungames.reminderapp.data.ReminderDataSource
import com.fungames.reminderapp.data.ReminderDataSourceImpl
import com.fungames.reminderapp.data.ReminderRepository
import com.fungames.reminderapp.data.ReminderRepositoryImpl
import com.fungames.reminderapp.db.AppDatabase
import com.fungames.reminderapp.presentation.add_reminder.AddReminderViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

fun appModule(databaseDriverFactory: DatabaseDriverFactory) = module {
    single { databaseDriverFactory }
    single { AppDatabase(get<DatabaseDriverFactory>().createDriver()) }
    single<ReminderDataSource> { ReminderDataSourceImpl(get()) }
    single<ReminderRepository> { ReminderRepositoryImpl(get()) }
    factory { AddReminderViewModel(get()) }
}
