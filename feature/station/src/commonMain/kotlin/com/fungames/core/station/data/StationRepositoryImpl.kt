package com.fungames.core.station.data

import com.fungames.core.data.db.ConfigDao
import com.fungames.core.data.db.StationDao
import com.fungames.core.data.db.StationEntity
import com.fungames.core.station.domain.Contact
import com.fungames.core.station.domain.ParkingInfo
import com.fungames.core.station.domain.ParkingPass
import com.fungames.core.station.domain.ParkingRate
import com.fungames.core.station.domain.Station
import com.fungames.core.station.domain.StationRepository
import com.fungames.core.station.domain.WaterMetroRoute
import com.fungames.core.station.domain.WaterMetroStation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StationRepositoryImpl(
    private val stationDao: StationDao,
    private val configDao: ConfigDao
) : StationRepository {

    override suspend fun stationList(): List<Station> {
        return stationDao.getAllStationsList().map { it.toDomain() }
    }

    override fun getStations(): Flow<List<Station>> {
        return stationDao.getAllStations().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun saveStations(stations: List<Station>) {
        stationDao.insertStations(stations.map { it.toEntity() })
    }

    override suspend fun getWaterMetroRoutes(): List<WaterMetroRoute> {
        return configDao.getWaterMetroRoutes().map {
            WaterMetroRoute(
                id = it.id,
                name = it.name,
                stations = it.stations.split(",").filter { s -> s.isNotEmpty() },
                duration = it.duration,
                startDate = it.startDate,
                status = it.status,
                isOperational = it.isOperational
            )
        }
    }

    override suspend fun getWaterMetroStations(): List<WaterMetroStation> {
        return configDao.getWaterMetroStations().map {
            WaterMetroStation(name = it.name)
        }
    }

    override suspend fun getParkingInfo(): ParkingInfo? {
        val info = configDao.getParkingInfo() ?: return null
        val rates = configDao.getParkingRates().map {
            ParkingRate(
                vehicleType = it.vehicleType,
                isCommuter = it.isCommuter,
                firstTwoHours = it.firstTwoHours,
                everyExtraHour = it.everyExtraHour,
                note = it.note
            )
        }
        val passes = configDao.getParkingPasses().map {
            ParkingPass(
                vehicleType = it.vehicleType,
                isCommuter = it.isCommuter,
                passType = it.passType,
                rate = it.rate,
                validityNote = it.validityNote
            )
        }
        return ParkingInfo(
            effectiveFrom = info.effectiveFrom,
            applicableFor = info.applicableFor,
            currency = info.currency,
            notes = info.notes.split("|"),
            hourlyRates = rates,
            passes = passes
        )
    }

    override suspend fun getContacts(): List<Contact> {
        return configDao.getContacts().map {
            Contact(
                id = it.id,
                category = it.category,
                name = it.name,
                value = it.value
            )
        }
    }

    private fun StationEntity.toDomain() = Station(
        id = id,
        nameEn = nameEn,
        nameMl = nameMl,
        nameHi = nameHi,
        latitude = latitude,
        longitude = longitude,
        lineId = lineId,
        sequence = sequence,
        mode = mode,
        wheelchairAccessible = wheelchairAccessible
    )

    private fun Station.toEntity() = StationEntity(
        id = id,
        nameEn = nameEn,
        nameMl = nameMl,
        nameHi = nameHi,
        latitude = latitude,
        longitude = longitude,
        lineId = lineId,
        sequence = sequence,
        mode = mode,
        wheelchairAccessible = wheelchairAccessible
    )
}
