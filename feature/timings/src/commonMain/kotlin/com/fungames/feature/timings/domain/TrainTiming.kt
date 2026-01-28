package com.fungames.feature.timings.domain

data class TrainTiming(
    val trainNumber: String,
    val trainName: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String
)
