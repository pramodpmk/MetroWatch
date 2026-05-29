package com.metrowatch.kochi.station.domain

data class MetroRoute(
    val lineId: String,
    val stations: List<String>
)
