package com.metrowatch.kochi.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "distances", primaryKeys = ["from", "to"])
data class DistanceEntity(
    val from: String,
    val to: String,
    val km: Double,
    val isBidirectional: Boolean
)

@Entity(tableName = "fare_slabs")
data class FareSlabEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val minKm: Double,
    val maxKm: Double,
    val fare: Double
)

@Entity(tableName = "timetables", primaryKeys = ["mode", "dayType"])
data class TimetableEntity(
    val mode: String,
    val dayType: String,
    val startTime: String,
    val endTime: String,
    val frequencyMinutes: Int
)

@Entity(tableName = "config_version")
data class ConfigVersionEntity(
    @PrimaryKey val id: Int = 0,
    val version: String,
    val updatedAt: String? = null
)

@Entity(tableName = "water_metro_routes")
data class WaterMetroRouteEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val stations: String, // Comma separated
    val duration: String?,
    val startDate: String?,
    val status: String?,
    val isOperational: Boolean
)

@Entity(tableName = "water_metro_stations")
data class WaterMetroStationEntity(
    @PrimaryKey val name: String
)

@Entity(tableName = "parking_rates", primaryKeys = ["vehicleType", "isCommuter"])
data class ParkingRateEntity(
    val vehicleType: String, // fourWheelers, twoWheelers, buses, tempo
    val isCommuter: Boolean,
    val firstTwoHours: Int,
    val everyExtraHour: Int,
    val note: String? = null
)

@Entity(tableName = "parking_passes", primaryKeys = ["vehicleType", "isCommuter", "passType"])
data class ParkingPassEntity(
    val vehicleType: String,
    val isCommuter: Boolean,
    val passType: String, // monthly, weekly
    val rate: Int,
    val validityNote: String? = null
)

@Entity(tableName = "parking_info")
data class ParkingInfoEntity(
    @PrimaryKey val id: Int = 0,
    val effectiveFrom: String,
    val applicableFor: String,
    val currency: String,
    val notes: String // Comma separated
)

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val name: String,
    val value: String
)
