package com.fungames.core.data.db

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
