package com.metrowatch.kochi.domain

import kotlinx.coroutines.flow.Flow

abstract class BaseUseCase<T> {

    abstract operator fun invoke(): Flow<DomainState<T>>

    suspend fun apiCall(
        callTarget: suspend () -> Unit,
    ) {

    }
}