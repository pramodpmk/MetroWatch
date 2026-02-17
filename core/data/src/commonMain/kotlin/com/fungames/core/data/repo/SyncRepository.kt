package com.fungames.core.data.repo

import com.fungames.core.data.api.ConfigApi
import com.fungames.core.data.api.ConfigurationDto
import com.fungames.core.data.db.AppDatabase
import com.fungames.core.data.db.ConfigVersionEntity
import com.fungames.core.data.db.ContactEntity
import com.fungames.core.data.db.DistanceEntity
import com.fungames.core.data.db.FareSlabEntity
import com.fungames.core.data.db.ParkingInfoEntity
import com.fungames.core.data.db.ParkingPassEntity
import com.fungames.core.data.db.ParkingRateEntity
import com.fungames.core.data.db.StationEntity
import com.fungames.core.data.db.TimetableEntity
import com.fungames.core.data.db.WaterMetroRouteEntity
import com.fungames.core.data.db.WaterMetroStationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class SyncRepository(
    private val database: AppDatabase,
    private val configApi: ConfigApi,
    private val json: Json
) {
    private val configDao = database.configDao()

    suspend fun syncConfig() = withContext(Dispatchers.IO) {
        try {
            val remoteVersionInfo = configApi.getVersion()
            val localVersionInfo = configDao.getConfigVersion()

            if (localVersionInfo?.version != remoteVersionInfo.version) {
                val configResponse = configApi.getConfig()
                val configurationDto = json.decodeFromString<ConfigurationDto>(configResponse.configuration)
                saveConfig(configurationDto, configResponse.version)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun saveConfig(dto: ConfigurationDto, version: String) {
        val stations = dto.stations.map {
            StationEntity(
                id = it.id,
                nameEn = it.name.en,
                nameMl = it.name.ml,
                nameHi = it.name.hi,
                latitude = it.latitude,
                longitude = it.longitude,
                lineId = it.lineId,
                sequence = it.sequence,
                mode = it.mode,
                wheelchairAccessible = it.wheelchairAccessible
            )
        }

        val distances = dto.distances.map {
            DistanceEntity(
                from = it.from,
                to = it.to,
                km = it.km,
                isBidirectional = it.isBidirectional
            )
        }

        val fareSlabs = dto.fareRules.slabs.map {
            FareSlabEntity(
                minKm = it.minKm,
                maxKm = it.maxKm,
                fare = it.fare
            )
        }

        val timetables = mutableListOf<TimetableEntity>()
        dto.timetables.forEach { (mode, dayTypes) ->
            dayTypes.forEach { (dayType, timetableDto) ->
                timetables.add(
                    TimetableEntity(
                        mode = mode,
                        dayType = dayType,
                        startTime = timetableDto.startTime,
                        endTime = timetableDto.endTime,
                        frequencyMinutes = timetableDto.frequencyMinutes
                    )
                )
            }
        }

        val waterMetroRoutes = mutableListOf<WaterMetroRouteEntity>()
        dto.waterMetro?.kochi_water_metro?.let { info ->
            info.operational.forEach {
                waterMetroRoutes.add(
                    WaterMetroRouteEntity(
                        id = it.id,
                        name = it.name,
                        stations = it.stations.joinToString(","),
                        duration = it.duration,
                        startDate = it.start_date,
                        status = "Operational",
                        isOperational = true
                    )
                )
            }
            info.proposed_routes.forEach {
                waterMetroRoutes.add(
                    WaterMetroRouteEntity(
                        id = it.id,
                        name = it.name,
                        stations = "",
                        duration = null,
                        startDate = null,
                        status = it.status,
                        isOperational = false
                    )
                )
            }
        }

        val waterMetroStations = dto.waterMetro?.kochi_water_metro?.operational_stations?.map {
            WaterMetroStationEntity(name = it)
        } ?: emptyList()

        val parkingRates = mutableListOf<ParkingRateEntity>()
        val parkingPasses = mutableListOf<ParkingPassEntity>()
        var parkingInfo: ParkingInfoEntity? = null

        dto.parking?.let { p ->
            parkingInfo = ParkingInfoEntity(
                effectiveFrom = p.effectiveFrom,
                applicableFor = p.applicableFor,
                currency = p.currency,
                notes = p.notes.joinToString("|")
            )

            // Rates
            listOf(
                "commuters" to p.hourlyRates.commuters,
                "nonCommuters" to p.hourlyRates.nonCommuters
            ).forEach { (type, rates) ->
                val isCommuter = type == "commuters"
                listOf(
                    "fourWheelers" to rates.firstTwoHours.fourWheelers to rates.everyExtraHour.fourWheelers,
                    "twoWheelers" to rates.firstTwoHours.twoWheelers to rates.everyExtraHour.twoWheelers,
                    "buses" to (rates.firstTwoHours.buses ?: 0) to (rates.everyExtraHour.buses ?: 0),
                    "tempo" to (rates.firstTwoHours.tempo ?: 0) to (rates.everyExtraHour.tempo ?: 0)
                ).forEach { pair ->
                    val vehicleType = pair.first.first
                    val firstTwo = pair.first.second
                    val extra = pair.second
                    parkingRates.add(
                        ParkingRateEntity(
                            vehicleType = vehicleType,
                            isCommuter = isCommuter,
                            firstTwoHours = firstTwo,
                            everyExtraHour = extra,
                            note = if (vehicleType == "tempo" || vehicleType == "buses") null else if (isCommuter) rates.everyExtraHour.note else null
                        )
                    )
                }
            }

            // Passes
            listOf(
                "commuters" to p.passes.commuters,
                "nonCommuters" to p.passes.nonCommuters
            ).forEach { (type, passType) ->
                val isCommuter = type == "commuters"
                passType.monthly?.let { m ->
                    m.fourWheelers?.let {
                        parkingPasses.add(ParkingPassEntity("fourWheelers", isCommuter, "monthly", it, m.validityNote))
                    }
                    m.twoWheelers?.let {
                        parkingPasses.add(ParkingPassEntity("twoWheelers", isCommuter, "monthly", it, m.validityNote))
                    }
                }
                passType.weekly?.let { w ->
                    w.fourWheelers?.let {
                        parkingPasses.add(ParkingPassEntity("fourWheelers", isCommuter, "weekly", it, w.validityNote))
                    }
                    w.twoWheelers?.let {
                        parkingPasses.add(ParkingPassEntity("twoWheelers", isCommuter, "weekly", it, w.validityNote))
                    }
                }
            }
        }

        val contacts = mutableListOf<ContactEntity>()
        dto.contacts?.ernakulam_phone_registry?.let { reg ->
            reg.emergency.forEach { contacts.add(ContactEntity(category = "Emergency", name = it.key, value = it.value)) }
            reg.government_offices.forEach { contacts.add(ContactEntity(category = "Government Offices", name = it.key, value = it.value)) }
            reg.police.forEach { contacts.add(ContactEntity(category = "Police", name = it.key, value = it.value)) }
            reg.utilities.forEach { contacts.add(ContactEntity(category = "Utilities", name = it.key, value = it.value)) }
            reg.hospitals.forEach { contacts.add(ContactEntity(category = "Hospitals", name = it.key, value = it.value)) }
            reg.transport.forEach { contacts.add(ContactEntity(category = "Transport", name = it.key, value = it.value)) }
            reg.websites.forEach { contacts.add(ContactEntity(category = "Websites", name = it.key, value = it.value)) }
        }

        configDao.updateConfig(
            version = ConfigVersionEntity(version = version),
            stations = stations,
            distances = distances,
            fareSlabs = fareSlabs,
            timetables = timetables,
            waterMetroRoutes = waterMetroRoutes,
            waterMetroStations = waterMetroStations,
            parkingRates = parkingRates,
            parkingPasses = parkingPasses,
            parkingInfo = parkingInfo,
            contacts = contacts
        )
    }
}
