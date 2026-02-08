package com.fungames.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        StationEntity::class,
        DistanceEntity::class,
        FareSlabEntity::class,
        TimetableEntity::class,
        ConfigVersionEntity::class
    ],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stationDao(): StationDao
    abstract fun configDao(): ConfigDao
}
