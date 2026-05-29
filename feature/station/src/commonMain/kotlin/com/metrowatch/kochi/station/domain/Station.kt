package com.metrowatch.kochi.station.domain

data class Station(
    val id: String,
    val nameEn: String,
    val nameMl: String?,
    val nameHi: String?,
    val latitude: Double,
    val longitude: Double,
    val lineId: String,
    val sequence: Int,
    val mode: String,
    val wheelchairAccessible: Boolean
) {
    // Helper property for UI compatibility
    val name: String get() = nameEn
}
