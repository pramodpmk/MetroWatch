package com.metrowatch.kochi.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stations")
data class StationEntity(
    @PrimaryKey val id: String,

    val nameEn: String,
    val nameMl: String?,
    val nameHi: String?,

    val latitude: Double,
    val longitude: Double,

    val lineId: String,
    val sequence: Int,

    val mode: String,

    val wheelchairAccessible: Boolean
)
