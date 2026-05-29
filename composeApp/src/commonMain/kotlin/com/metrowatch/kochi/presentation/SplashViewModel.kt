package com.metrowatch.kochi.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.metrowatch.kochi.data.repo.SyncRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val syncRepository: SyncRepository
) : ViewModel() {

    private val _navigateToHome = MutableSharedFlow<Unit>()
    val navigateToHome: SharedFlow<Unit> = _navigateToHome

    init {
        syncAndNavigate()
    }

    private fun syncAndNavigate() {
        viewModelScope.launch {
            syncRepository.syncConfig()
            _navigateToHome.emit(Unit)
        }
    }
}
