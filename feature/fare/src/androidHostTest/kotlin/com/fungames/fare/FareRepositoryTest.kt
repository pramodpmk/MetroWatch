package com.fungames.fare

import com.fungames.core.data.db.*
import com.fungames.fare.data.FareRepositoryImpl
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlinx.coroutines.flow.Flow

class FareRepositoryTest {

    private val mockStationDao = object : StationDao {
        override fun getAllStations(): Flow<List<StationEntity>> = throw NotImplementedError()
        override suspend fun getAllStationsList(): List<StationEntity> = throw NotImplementedError()
        override suspend fun getStationIdByName(name: String): String? {
            return when (name) {
                "Station A" -> "idA"
                "Station B" -> "idB"
                else -> null
            }
        }
        override suspend fun getStationByName(name: String): StationEntity? {
            return when (name) {
                "Station A" -> StationEntity("idA", "Station A", null, null, 0.0, 0.0, "L1", 1, "metro", true)
                "Station B" -> StationEntity("idB", "Station B", null, null, 0.0, 0.0, "L1", 2, "metro", true)
                else -> null
            }
        }
        override suspend fun getStationsByLine(lineId: String): List<StationEntity> {
            return listOf(
                StationEntity("idA", "Station A", null, null, 0.0, 0.0, "L1", 1, "metro", true),
                StationEntity("idB", "Station B", null, null, 0.0, 0.0, "L1", 2, "metro", true)
            )
        }
        override suspend fun insertStations(stations: List<StationEntity>) {}
        override suspend fun deleteAllStations() {}
    }

    private val mockConfigDao = object : ConfigDao {
        override suspend fun getConfigVersion(): ConfigVersionEntity? = throw NotImplementedError()
        override suspend fun getDistance(fromId: String, toId: String): Double? {
            return if ((fromId == "idA" && toId == "idB") || (fromId == "idB" && toId == "idA")) {
                10.5
            } else null
        }
        override suspend fun getFareSlabs(): List<FareSlabEntity> {
            return listOf(
                FareSlabEntity(minKm = 0.0, maxKm = 5.0, fare = 20.0),
                FareSlabEntity(minKm = 5.1, maxKm = 10.0, fare = 30.0),
                FareSlabEntity(minKm = 10.1, maxKm = 15.0, fare = 40.0)
            )
        }
        override suspend fun insertConfigVersion(version: ConfigVersionEntity) {}
        override suspend fun insertDistances(distances: List<DistanceEntity>) {}
        override suspend fun deleteAllDistances() {}
        override suspend fun insertFareSlabs(slabs: List<FareSlabEntity>) {}
        override suspend fun deleteAllFareSlabs() {}
        override suspend fun getTimetableByMode(mode: String): TimetableEntity? = null
        override suspend fun getTimetablesByMode(mode: String): List<TimetableEntity> = emptyList()
        override suspend fun insertTimetables(timetables: List<TimetableEntity>) {}
        override suspend fun deleteAllTimetables() {}
        override suspend fun insertStations(stations: List<StationEntity>) {}
        override suspend fun deleteAllStations() {}
        override suspend fun insertWaterMetroRoutes(routes: List<WaterMetroRouteEntity>) {}
        override suspend fun deleteAllWaterMetroRoutes() {}
        override suspend fun getWaterMetroRoutes(): List<WaterMetroRouteEntity> = emptyList()
        override suspend fun insertWaterMetroStations(stations: List<WaterMetroStationEntity>) {}
        override suspend fun deleteAllWaterMetroStations() {}
        override suspend fun getWaterMetroStations(): List<WaterMetroStationEntity> = emptyList()
        override suspend fun insertParkingRates(rates: List<ParkingRateEntity>) {}
        override suspend fun deleteAllParkingRates() {}
        override suspend fun getParkingRates(): List<ParkingRateEntity> = emptyList()
        override suspend fun insertParkingPasses(passes: List<ParkingPassEntity>) {}
        override suspend fun deleteAllParkingPasses() {}
        override suspend fun getParkingPasses(): List<ParkingPassEntity> = emptyList()
        override suspend fun insertParkingInfo(info: ParkingInfoEntity) {}
        override suspend fun deleteAllParkingInfo() {}
        override suspend fun getParkingInfo(): ParkingInfoEntity? = null
        override suspend fun insertContacts(contacts: List<ContactEntity>) {}
        override suspend fun deleteAllContacts() {}
        override suspend fun getContacts(): List<ContactEntity> = emptyList()
        override suspend fun updateConfig(
            version: ConfigVersionEntity,
            stations: List<StationEntity>,
            distances: List<DistanceEntity>,
            fareSlabs: List<FareSlabEntity>,
            timetables: List<TimetableEntity>,
            waterMetroRoutes: List<WaterMetroRouteEntity>,
            waterMetroStations: List<WaterMetroStationEntity>,
            parkingRates: List<ParkingRateEntity>,
            parkingPasses: List<ParkingPassEntity>,
            parkingInfo: ParkingInfoEntity?,
            contacts: List<ContactEntity>
        ) {}
    }

    @Test
    fun testFareCalculation() = runBlocking {
        val repository = FareRepositoryImpl(mockStationDao, mockConfigDao)
        val details = repository.getFareDetails("Station A", "Station B")

        assertNotNull(details)
        assertEquals("10.5 km", details.distance)
        assertEquals("₹40.0", details.fare)
    }
}
