package com.metrowatch.kochi.station.data

import com.metrowatch.kochi.data.db.ConfigDao
import com.metrowatch.kochi.data.db.StationDao
import com.metrowatch.kochi.data.db.StationEntity
import com.metrowatch.kochi.station.domain.Contact
import com.metrowatch.kochi.station.domain.ParkingInfo
import com.metrowatch.kochi.station.domain.ParkingPass
import com.metrowatch.kochi.station.domain.ParkingRate
import com.metrowatch.kochi.station.domain.Station
import com.metrowatch.kochi.data.toSentenceCase
import com.metrowatch.kochi.station.domain.StationRepository
import com.metrowatch.kochi.station.domain.WaterMetroRoute
import com.metrowatch.kochi.station.domain.WaterMetroStation
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
                displayName = it.name.toSentenceCase(),
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
        wheelchairAccessible = wheelchairAccessible,
        imageUrl = imageUrl
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
        wheelchairAccessible = wheelchairAccessible,
        imageUrl = imageUrl
    )
}
