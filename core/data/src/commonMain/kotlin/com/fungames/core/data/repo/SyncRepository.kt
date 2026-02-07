package com.fungames.core.data.repo

import com.fungames.core.data.api.ConfigApi
import com.fungames.core.data.api.ConfigurationDto
import com.fungames.core.data.db.AppDatabase
import com.fungames.core.data.db.ConfigVersionEntity
import com.fungames.core.data.db.DistanceEntity
import com.fungames.core.data.db.FareSlabEntity
import com.fungames.core.data.db.StationEntity
import com.fungames.core.data.db.TimetableEntity
import kotlinx.serialization.json.Json

class SyncRepository(
    private val database: AppDatabase,
    private val configApi: ConfigApi,
    private val json: Json
) {
    private val configDao = database.configDao()

    suspend fun syncConfig() {
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

        configDao.updateConfig(
            version = ConfigVersionEntity(version = version),
            stations = stations,
            distances = distances,
            fareSlabs = fareSlabs,
            timetables = timetables
        )
    }
}
