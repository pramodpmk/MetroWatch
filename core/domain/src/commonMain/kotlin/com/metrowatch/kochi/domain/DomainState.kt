package com.metrowatch.kochi.domain

sealed interface DomainState<out T> {
    
    data object Loading : DomainState<Nothing>
    
    data class Success<T>(val data: T) : DomainState<T>

    data class Error(
        val message: String, val throwable: Throwable? = null
    ) : DomainState<Nothing>
}
