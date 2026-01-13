package com.fungames.reminderapp.data

import com.fungames.reminderapp.db.Reminders

interface ReminderDataSource {
    suspend fun getAllReminders(): List<Reminders>
    suspend fun insertReminder(title: String, description: String?, remindAt: Long)
    suspend fun deleteReminder(id: Long)
}

class ReminderDataSourceImpl(private val database: com.fungames.reminderapp.db.AppDatabase) : ReminderDataSource {
    private val queries = database.remindersQueries

    override suspend fun getAllReminders(): List<Reminders> {
        return queries.selectAll().executeAsList()
    }

    override suspend fun insertReminder(title: String, description: String?, remindAt: Long) {
        queries.insert(title, description, remindAt)
    }

    override suspend fun deleteReminder(id: Long) {
        queries.delete(id)
    }
}
