package com.watermelonman.data.local.dao

import androidx.room.*
import com.watermelonman.entities.location.Location

@Dao
interface LocationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: Location)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(locations: List<Location>)

    @Update
    suspend fun update(location: Location)

    @Delete
    suspend fun delete(location: Location)

    @Delete
    suspend fun deleteAll(locations: List<Location>)

    @Query("SELECT * FROM location")
    suspend fun getAllSavedLocations(): List<Location>

    @Query("SELECT * FROM location WHERE isSelected = 1")
    suspend fun getSelectedLocation(): Location?

    @Query("UPDATE Location SET isSelected=:isSelected WHERE geoNameId =:id")
    suspend fun updateSelectedStatus(isSelected: Int, id: Int)

    @Query("SELECT * FROM location WHERE geoNameId =:id")
    suspend fun getLocationById(id: Int): Location?

    @Query("UPDATE Location SET name =:name WHERE geoNameId =:id")
    suspend fun updateLocationName(name: String, id: Int)
}