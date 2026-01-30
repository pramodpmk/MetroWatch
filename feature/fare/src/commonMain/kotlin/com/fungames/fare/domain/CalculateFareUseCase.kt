package com.fungames.fare.domain

import com.fungames.domain.DomainState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CalculateFareUseCase {
    operator fun invoke(departure: String, arrival: String): Flow<DomainState<FareDetails>> = flow {
        emit(DomainState.Loading)
        // Mock API call with delay
        delay(2000)
        emit(DomainState.Success(FareDetails(distance = "10.5 km", fare = "₹45.00")))
    }
}
