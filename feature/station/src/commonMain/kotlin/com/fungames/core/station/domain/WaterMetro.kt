package com.fungames.core.station.domain

data class WaterMetroRoute(
    val id: Int,
    val name: String,
    val stations: List<String>,
    val duration: String?,
    val startDate: String?,
    val status: String?,
    val isOperational: Boolean
)

data class WaterMetroStation(
    val name: String
)
