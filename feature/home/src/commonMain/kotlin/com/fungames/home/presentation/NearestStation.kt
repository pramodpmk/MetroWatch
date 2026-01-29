package com.fungames.home.presentation

data class NearestStation(
    val id: Int,
    val stationName: String,
    val stationCode: String,
    val distanceToStation: String,
    val nextTrainTo: String,
    val nextTrainTime: String,
    val line: String = "",
    val platform: String = "",
    val locationLatitude: Long,
    val locationLongitude: Long
)
