package com.fungames.core.data.api

import kotlinx.serialization.Serializable

@Serializable
data class VersionResponse(
    val version: String,
    val updatedAt: String? = null
)

@Serializable
data class ConfigResponse(
    val configuration: String,
    val version: String
)

@Serializable
data class ConfigurationDto(
    val stations: List<StationDto>,
    val distances: List<DistanceDto>,
    val fareRules: FareRulesDto,
    val timetables: Map<String, Map<String, TimetableDto>>,
    val alerts: List<AlertDto> = emptyList()
)

@Serializable
data class StationDto(
    val id: String,
    val name: NameDto,
    val mode: String,
    val lineId: String,
    val sequence: Int,
    val latitude: Double,
    val longitude: Double,
    val wheelchairAccessible: Boolean
)

@Serializable
data class NameDto(
    val en: String,
    val ml: String? = null,
    val hi: String? = null
)

@Serializable
data class DistanceDto(
    val from: String,
    val to: String,
    val km: Double,
    val isBidirectional: Boolean
)

@Serializable
data class FareRulesDto(
    val type: String,
    val currency: String,
    val slabs: List<FareSlabDto>
)

@Serializable
data class FareSlabDto(
    val minKm: Double,
    val maxKm: Double,
    val fare: Double
)

@Serializable
data class TimetableDto(
    val startTime: String,
    val endTime: String,
    val frequencyMinutes: Int
)

@Serializable
data class AlertDto(
    val id: String? = null,
    val title: String? = null,
    val message: String? = null
)
