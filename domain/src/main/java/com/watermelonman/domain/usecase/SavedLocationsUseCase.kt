package com.watermelonman.domain.usecase

import com.watermelonman.data.datastore.SavedLocationsRepository
import com.watermelonman.data.datastore.WeatherForecastRepository
import com.watermelonman.domain.interactor.SavedLocationsInteractor
import com.watermelonman.entities.enums.Units
import com.watermelonman.entities.location.Location
import com.watermelonman.entities.result.CallException
import com.watermelonman.entities.result.ErrorCode
import com.watermelonman.entities.result.Result
import com.watermelonman.entities.utils.toCurrentForecastDataResult
import com.watermelonman.entities.utils.toLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SavedLocationsUseCase(
    private val savedLocationsRepository: SavedLocationsRepository,
    private val weatherForecastRepository: WeatherForecastRepository
): SavedLocationsInteractor {

    override suspend fun selectLocation(location: Location) {
        val currentSelectedLocation = savedLocationsRepository.getSelectedLocation()
        if (currentSelectedLocation?.geoNameId == location.geoNameId) return
        val locationInDb = savedLocationsRepository.getLocationById(location.geoNameId)
        val doesLocationExistInDatabase = locationInDb?.geoNameId == location.geoNameId
        currentSelectedLocation?.let {
            savedLocationsRepository.updateLocationSelectedStatus(false, it)
        }
        if (!doesLocationExistInDatabase) {
            savedLocationsRepository.saveLocation(location)
        }
        savedLocationsRepository.updateLocationSelectedStatus(true, location)
    }

    override suspend fun getSavedLocations(): List<Location> {
        return savedLocationsRepository.getSavedLocations()
    }

    override suspend fun getSelectedLocation(): Location? {
        return savedLocationsRepository.getSelectedLocation()
    }

    override suspend fun fillMissingPropertiesOfSelectedLocation(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            val location = getSelectedLocation()
                ?: return@withContext Result.Error(CallException(ErrorCode.NULL_DATA, "No Selected Location Found"))
            val currentResult =
                when(val result = weatherForecastRepository.getCurrentForecast(location, Units.METRIC)) {
            is Result.Error -> Result.Error(result.error)
            is Result.Success -> result.toCurrentForecastDataResult()
        }
            if (currentResult is Result.Error) return@withContext Result.Error(currentResult.error)
            currentResult as Result.Success
            val newLocation = currentResult.data.toLocation(location)
            savedLocationsRepository.deleteLocation(location)
            savedLocationsRepository.saveLocation(newLocation)
            savedLocationsRepository.updateLocationSelectedStatus(true, newLocation)
            Result.Success(true)
        }
    }

    override suspend fun deleteAll(locations: List<Location>) {
        savedLocationsRepository.deleteAll(locations)
    }

    override suspend fun selectFirstIfExists() {
        val savedLocations = getSavedLocations()
            .takeIf { it.isNotEmpty() }
        savedLocations?.let { selectLocation(savedLocations.first()) }
    }
}