package com.metrowatch.kochi.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStations(stations: List<StationEntity>)

    @Query("SELECT * FROM stations")
    fun getAllStations(): Flow<List<StationEntity>>

    @Query("SELECT * FROM stations")
    suspend fun getAllStationsList(): List<StationEntity>

    @Query("SELECT * FROM stations WHERE id = :id")
    suspend fun getStationById(id: String): StationEntity?

    @Query("SELECT * FROM stations WHERE nameEn = :name OR nameMl = :name OR nameHi = :name LIMIT 1")
    suspend fun getStationByName(name: String): StationEntity?

    @Query("SELECT * FROM stations WHERE lineId = :lineId")
    suspend fun getStationsByLine(lineId: String): List<StationEntity>
}
