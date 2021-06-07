package com.watermelonman.data.local.db

import android.app.Application
import androidx.room.Database
import androidx.room.RoomDatabase
import com.watermelonman.data.local.dao.LocationDao
import com.watermelonman.entities.location.Location

@Database(
    entities = [Location::class],
    version = 1
)
abstract class Room: RoomDatabase() {

    abstract val locationDao: LocationDao

    companion object {
        fun getInstance(application: Application): Room {
            val instance: Room by lazy {
                androidx.room.Room
                    .databaseBuilder(
                        application,
                        Room::class.java,
                        "WeatherAppDatabase"
                    ).build()
            }
            return instance
        }
    }
}