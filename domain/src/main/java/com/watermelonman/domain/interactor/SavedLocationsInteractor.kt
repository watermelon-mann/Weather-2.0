package com.watermelonman.domain.interactor

import com.watermelonman.entities.location.Location
import com.watermelonman.entities.result.Result

interface SavedLocationsInteractor {
    suspend fun selectLocation(location: Location)
    suspend fun getSavedLocations(): List<Location>
    suspend fun getSelectedLocation(): Location?
    suspend fun fillMissingPropertiesOfSelectedLocation(): Result<Boolean>
    suspend fun deleteAll(locations: List<Location>)
    suspend fun selectFirstIfExists()
}