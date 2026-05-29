package com.metrowatch.kochi.data.api

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
    val alerts: List<AlertDto> = emptyList(),
    val parking: ParkingDto? = null,
    val waterMetro: WaterMetroDto? = null,
    val contacts: ContactsDto? = null
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

@Serializable
data class ParkingDto(
    val effectiveFrom: String,
    val applicableFor: String,
    val currency: String,
    val hourlyRates: HourlyRatesDto,
    val passes: PassesDto,
    val notes: List<String>
)

@Serializable
data class HourlyRatesDto(
    val commuters: RatesDto,
    val nonCommuters: RatesDto
)

@Serializable
data class RatesDto(
    val firstTwoHours: VehicleRatesDto,
    val everyExtraHour: VehicleRatesDto
)

@Serializable
data class VehicleRatesDto(
    val fourWheelers: Int,
    val twoWheelers: Int,
    val buses: Int? = null,
    val tempo: Int? = null,
    val note: String? = null
)

@Serializable
data class PassesDto(
    val commuters: PassTypeDto,
    val nonCommuters: PassTypeDto
)

@Serializable
data class PassTypeDto(
    val monthly: VehiclePassDto? = null,
    val weekly: VehiclePassDto? = null
)

@Serializable
data class VehiclePassDto(
    val fourWheelers: Int? = null,
    val twoWheelers: Int? = null,
    val validityNote: String? = null
)

@Serializable
data class WaterMetroDto(
    val kochi_water_metro: WaterMetroInfoDto
)

@Serializable
data class WaterMetroInfoDto(
    val total_proposed_routes: Int,
    val operational_routes: Int,
    val operational: List<WaterMetroOperationalRouteDto>,
    val proposed_routes: List<WaterMetroProposedRouteDto>,
    val operational_stations: List<String>,
    val official_website: String,
    val app: String
)

@Serializable
data class WaterMetroOperationalRouteDto(
    val id: Int,
    val name: String,
    val stations: List<String>,
    val duration: String,
    val start_date: String
)

@Serializable
data class WaterMetroProposedRouteDto(
    val id: Int,
    val name: String,
    val status: String
)

@Serializable
data class ContactsDto(
    val ernakulam_phone_registry: PhoneRegistryDto
)

@Serializable
data class PhoneRegistryDto(
    val last_updated: String,
    val source: String,
    val emergency: Map<String, String>,
    val government_offices: Map<String, String>,
    val police: Map<String, String>,
    val utilities: Map<String, String>,
    val hospitals: Map<String, String>,
    val transport: Map<String, String>,
    val websites: Map<String, String>
)
