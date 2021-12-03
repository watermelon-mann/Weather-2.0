package com.watermelonman.data.local.dao

import androidx.room.*
import com.watermelonman.entities.location.Location

@Dao
interface LocationDao {

    //TODO: Find out Y suspend functions cause build error
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(location: Location)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(locations: List<Location>)

    @Update
    fun update(location: Location)

    @Delete
    fun delete(location: Location)

    @Delete
    fun deleteAll(locations: List<Location>)

    @Query("SELECT * FROM location")
    fun getAllSavedLocations(): List<Location>

    @Query("SELECT * FROM location WHERE isSelected = 1")
    fun getSelectedLocation(): Location?

    @Query("UPDATE Location SET isSelected=:isSelected WHERE geoNameId =:id")
    fun updateSelectedStatus(isSelected: Int, id: Int)

    @Query("SELECT * FROM location WHERE geoNameId =:id")
    fun getLocationById(id: Int): Location?

    @Query("UPDATE Location SET name =:name WHERE geoNameId =:id")
    fun updateLocationName(name: String, id: Int)
}