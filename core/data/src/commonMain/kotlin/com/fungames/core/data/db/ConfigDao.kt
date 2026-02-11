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

    @Transaction
    suspend fun updateConfig(
        version: ConfigVersionEntity,
        stations: List<StationEntity>,
        distances: List<DistanceEntity>,
        fareSlabs: List<FareSlabEntity>,
        timetables: List<TimetableEntity>
    ) {
        deleteAllStations()
        insertStations(stations)

        deleteAllDistances()
        insertDistances(distances)

        deleteAllFareSlabs()
        insertFareSlabs(fareSlabs)

        deleteAllTimetables()
        insertTimetables(timetables)

        insertConfigVersion(version)
    }
}
