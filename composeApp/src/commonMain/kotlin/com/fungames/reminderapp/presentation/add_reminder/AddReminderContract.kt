package com.fungames.reminderapp.presentation.add_reminder

import com.fungames.reminderapp.db.Reminders

data class AddReminderState(
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = "",
    val isLoading: Boolean = false,
    val isReminderSaved: Boolean = false,
    val error: String? = null
)

sealed class AddReminderEvent {
    data class OnTitleChange(val title: String) : AddReminderEvent()
    data class OnDescriptionChange(val description: String) : AddReminderEvent()
    data class OnDateChange(val date: String) : AddReminderEvent()
    data class OnTimeChange(val time: String) : AddReminderEvent()
    data object OnCreateReminderClick : AddReminderEvent()
}

sealed class AddReminderEffect {
    data object ReminderSaved : AddReminderEffect()
}
