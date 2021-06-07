package com.watermelonman.data.repository

import com.watermelonman.data.datastore.SavedLocationsRepository
import com.watermelonman.data.local.dao.LocationDao
import com.watermelonman.entities.location.Location

class SavedLocationsRepositoryImpl(
    private val locationDao: LocationDao
): SavedLocationsRepository {

    override suspend fun saveLocation(location: Location) {
        locationDao.insert(location)
    }

    override suspend fun getSavedLocations(): List<Location> {
        return locationDao.getAllSavedLocations()
    }

    override suspend fun updateLocation(location: Location) {
        locationDao.update(location)
    }

    override suspend fun getSelectedLocation(): Location? {
        return locationDao.getSelectedLocation()
    }

    override suspend fun deleteLocation(location: Location) {
        locationDao.delete(location)
    }

    override suspend fun updateLocationSelectedStatus(isSelected: Boolean, location: Location) {
        val selectedStatus = if (isSelected) 1 else 0
        locationDao.updateSelectedStatus(selectedStatus, location.geoNameId)
    }

    override suspend fun getLocationById(id: Int): Location? {
        return locationDao.getLocationById(id)
    }

    override suspend fun updateLocationName(newName: String, location: Location) {
        locationDao.updateLocationName(newName, location.geoNameId)
    }

    override suspend fun deleteAll(locations: List<Location>) {
        locationDao.deleteAll(locations)
    }
}