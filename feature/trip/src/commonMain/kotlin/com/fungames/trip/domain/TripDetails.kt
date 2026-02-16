package com.fungames.trip.domain

import com.fungames.feature.timings.domain.TrainTiming

data class TripDetails(
    val distance: String,
    val fare: String,
    val stationsCount: Int,
    val lineName: String,
    val timings: List<TrainTiming>
)
