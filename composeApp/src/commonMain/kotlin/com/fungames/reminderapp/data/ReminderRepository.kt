package com.fungames.reminderapp.data

import com.fungames.reminderapp.db.Reminders

interface ReminderRepository {
    suspend fun getAllReminders(): List<Reminders>
    suspend fun insertReminder(title: String, description: String?, remindAt: Long)
    suspend fun deleteReminder(id: Long)
}

class ReminderRepositoryImpl(private val dataSource: ReminderDataSource) : ReminderRepository {
    override suspend fun getAllReminders(): List<Reminders> {
        return dataSource.getAllReminders()
    }

    override suspend fun insertReminder(title: String, description: String?, remindAt: Long) {
        dataSource.insertReminder(title, description, remindAt)
    }

    override suspend fun deleteReminder(id: Long) {
        dataSource.deleteReminder(id)
    }
}
