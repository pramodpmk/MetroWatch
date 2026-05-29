package com.metrowatch.kochi.station.presentation.contact

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metrowatch.kochi.station.domain.Contact
import com.metrowatch.kochi.station.domain.ContactsUseCase
import com.metrowatch.kochi.domain.DomainState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ContactsUiState(
    val contacts: List<Contact> = emptyList(),
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String = ""
)

class ContactsViewModel(
    private val contactsUseCase: ContactsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactsUiState())
    val uiState: StateFlow<ContactsUiState> = _uiState

    init {
        loadContacts()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            contactsUseCase().collect { state ->
                when (state) {
                    is DomainState.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                    is DomainState.Success -> {
                        _uiState.value = _uiState.value.copy(
                            contacts = state.data,
                            isLoading = false,
                            isError = false
                        )
                    }
                    is DomainState.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isError = true,
                            errorMessage = state.message ?: "Failed to load contacts"
                        )
                    }
                }
            }
        }
    }
}
