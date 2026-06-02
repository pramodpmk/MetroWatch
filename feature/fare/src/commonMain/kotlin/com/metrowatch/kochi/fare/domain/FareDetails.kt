package com.metrowatch.kochi.fare.domain

data class FareDetails(
    val distance: String,
    val fare: String,
    val departureCode: String,
    val arrivalCode: String,
    val stops: Int,
    val estimatedTimeMin: Int,
    val lineId: String
)
