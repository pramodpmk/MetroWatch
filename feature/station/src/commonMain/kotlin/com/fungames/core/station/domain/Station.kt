package com.fungames.core.station.domain

data class Station(
    val id: Int,
    val name: String,
    val code: String,
    val latInf: Double,
    val lngInf: Double,
    val nextTrain: String = "",
    val parkingAvailability: String = "",
    val gates: String = "",
    val contactDetails: String = "",
    val address: String = ""
)
