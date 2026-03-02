package com.fungames.core.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ConfigDao {
    @Query("SELECT * FROM config_version WHERE id = 0")
    suspend fun getConfigVersion(): ConfigVersionEntity?

    @Query("SELECT km FROM distances WHERE (`from` = :fromId AND `to` = :toId) OR (`from` = :toId AND `to` = :fromId) LIMIT 1")
    suspend fun getDistance(fromId: String, toId: String): Double?

    @Query("SELECT * FROM fare_slabs ORDER BY minKm ASC")
    suspend fun getFareSlabs(): List<FareSlabEntity>

    @Query("SELECT * FROM timetables WHERE mode = :mode LIMIT 1")
    suspend fun getTimetableByMode(mode: String): TimetableEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfigVersion(version: ConfigVersionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDistances(distances: List<DistanceEntity>)

    @Query("DELETE FROM distances")
    suspend fun deleteAllDistances()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFareSlabs(slabs: List<FareSlabEntity>)

    @Query("DELETE FROM fare_slabs")
    suspend fun deleteAllFareSlabs()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetables(timetables: List<TimetableEntity>)

    @Query("DELETE FROM timetables")
    suspend fun deleteAllTimetables()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStations(stations: List<StationEntity>)

    @Query("DELETE FROM stations")
    suspend fun deleteAllStations()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterMetroRoutes(routes: List<WaterMetroRouteEntity>)

    @Query("DELETE FROM water_metro_routes")
    suspend fun deleteAllWaterMetroRoutes()

    @Query("SELECT * FROM water_metro_routes")
    suspend fun getWaterMetroRoutes(): List<WaterMetroRouteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterMetroStations(stations: List<WaterMetroStationEntity>)

    @Query("DELETE FROM water_metro_stations")
    suspend fun deleteAllWaterMetroStations()

    @Query("SELECT * FROM water_metro_stations")
    suspend fun getWaterMetroStations(): List<WaterMetroStationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParkingRates(rates: List<ParkingRateEntity>)

    @Query("DELETE FROM parking_rates")
    suspend fun deleteAllParkingRates()

    @Query("SELECT * FROM parking_rates")
    suspend fun getParkingRates(): List<ParkingRateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParkingPasses(passes: List<ParkingPassEntity>)

    @Query("DELETE FROM parking_passes")
    suspend fun deleteAllParkingPasses()

    @Query("SELECT * FROM parking_passes")
    suspend fun getParkingPasses(): List<ParkingPassEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParkingInfo(info: ParkingInfoEntity)

    @Query("DELETE FROM parking_info")
    suspend fun deleteAllParkingInfo()

    @Query("SELECT * FROM parking_info LIMIT 1")
    suspend fun getParkingInfo(): ParkingInfoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<ContactEntity>)

    @Query("DELETE FROM contacts")
    suspend fun deleteAllContacts()

    @Query("SELECT * FROM contacts")
    suspend fun getContacts(): List<ContactEntity>

    @Transaction
    suspend fun updateConfig(
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
    ) {
        deleteAllStations()
        insertStations(stations)

        deleteAllDistances()
        insertDistances(distances)

        deleteAllFareSlabs()
        insertFareSlabs(fareSlabs)

        deleteAllTimetables()
        insertTimetables(timetables)

        deleteAllWaterMetroRoutes()
        insertWaterMetroRoutes(waterMetroRoutes)

        deleteAllWaterMetroStations()
        insertWaterMetroStations(waterMetroStations)

        deleteAllParkingRates()
        insertParkingRates(parkingRates)

        deleteAllParkingPasses()
        insertParkingPasses(parkingPasses)

        deleteAllParkingInfo()
        parkingInfo?.let { insertParkingInfo(it) }

        deleteAllContacts()
        insertContacts(contacts)

        insertConfigVersion(version)
    }
}
