package com.metrowatch.kochi.station.domain

data class ParkingInfo(
    val effectiveFrom: String,
    val applicableFor: String,
    val currency: String,
    val notes: List<String>,
    val hourlyRates: List<ParkingRate>,
    val passes: List<ParkingPass>
)

data class ParkingRate(
    val vehicleType: String,
    val isCommuter: Boolean,
    val firstTwoHours: Int,
    val everyExtraHour: Int,
    val note: String?
)

data class ParkingPass(
    val vehicleType: String,
    val isCommuter: Boolean,
    val passType: String,
    val rate: Int,
    val validityNote: String?
)
