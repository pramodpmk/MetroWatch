package com.fungames.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [StationEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stationDao(): StationDao
}
