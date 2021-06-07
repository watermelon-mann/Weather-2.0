package com.watermelonman.data.datastore

import com.watermelonman.entities.location.Location

interface SavedLocationsRepository {
    suspend fun saveLocation(location: Location)
    suspend fun getSavedLocations(): List<Location>
    suspend fun updateLocation(location: Location)
    suspend fun getSelectedLocation(): Location?
    suspend fun deleteLocation(location: Location)
    suspend fun deleteAll(locations: List<Location>)
    suspend fun updateLocationSelectedStatus(isSelected: Boolean, location: Location)
    suspend fun getLocationById(id: Int): Location?
    suspend fun updateLocationName(newName: String, location: Location)
}