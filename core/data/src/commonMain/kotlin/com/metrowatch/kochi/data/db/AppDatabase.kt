package com.metrowatch.kochi.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ConfigVersionEntity::class,
        ContactEntity::class,
        DistanceEntity::class,
        FareSlabEntity::class,
        ParkingInfoEntity::class,
        ParkingPassEntity::class,
        ParkingRateEntity::class,
        StationEntity::class,
        TimetableEntity::class,
        WaterMetroRouteEntity::class,
        WaterMetroStationEntity::class
    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun configDao(): ConfigDao
    abstract fun stationDao(): StationDao
}

internal const val DB_FILE_NAME = "metro_watch.db"
