package com.fungames.reminderapp.presentation.add_reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fungames.reminderapp.data.ReminderRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddReminderViewModel(private val reminderRepository: ReminderRepository) : ViewModel() {

    private val _state = MutableStateFlow(AddReminderState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AddReminderEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: AddReminderEvent) {
        when (event) {
            is AddReminderEvent.OnTitleChange -> _state.update { it.copy(title = event.title) }
            is AddReminderEvent.OnDescriptionChange -> _state.update { it.copy(description = event.description) }
            is AddReminderEvent.OnDateChange -> _state.update { it.copy(date = event.date) }
            is AddReminderEvent.OnTimeChange -> _state.update { it.copy(time = event.time) }
            AddReminderEvent.OnCreateReminderClick -> createReminder()
        }
    }

    private fun createReminder() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val state = _state.value
            if (state.title.isBlank() || state.date.isBlank() || state.time.isBlank()) {
                _state.update { it.copy(isLoading = false, error = "Title, date, and time cannot be empty.") }
                return@launch
            }

            // This is a simple and naive way to parse date and time. 
            // We will replace it with a proper date/time library later.
            val remindAt = try {
                val (year, month, day) = state.date.split("-").map { it.toInt() }
                val (hour, minute) = state.time.split(":").map { it.toInt() }
                // A more robust solution would use a proper date-time library
                // to handle time zones and formatting.
                //kotlinx.datetime.LocalDateTime(year, month, day, hour, minute, 0, 0)
                 //   .toInstant(kotlinx.datetime.TimeZone.currentSystemDefault()).toEpochMilliseconds()
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Invalid date or time format.") }
                return@launch
            }

            try {
                //reminderRepository.insertReminder(state.title, state.description, remindAt)
                _state.update { it.copy(isLoading = false, isReminderSaved = true) }
                _effect.emit(AddReminderEffect.ReminderSaved)
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}