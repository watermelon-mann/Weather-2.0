package com.watermelonman.domain.usecase

import com.watermelonman.data.datastore.SavedLocationsRepository
import com.watermelonman.data.datastore.WeatherForecastRepository
import com.watermelonman.domain.interactor.WeatherForecastInteractor
import com.watermelonman.entities.allforecast.AllForecastData
import com.watermelonman.entities.current.CurrentForecastData
import com.watermelonman.entities.daily.DailyData
import com.watermelonman.entities.enums.Units
import com.watermelonman.entities.hourly.HourlyData
import com.watermelonman.entities.location.Location
import com.watermelonman.entities.result.CallException
import com.watermelonman.entities.result.ErrorCode
import com.watermelonman.entities.result.Result
import com.watermelonman.entities.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

class WeatherForecastUseCase(
    private val weatherForecastRepository: WeatherForecastRepository,
    private val savedLocationsRepository: SavedLocationsRepository
): WeatherForecastInteractor {

    override suspend fun getCurrentForecast(location: Location, units: Units): Result<CurrentForecastData> {
        return withContext(Dispatchers.IO) {
            return@withContext when(val result = weatherForecastRepository.getCurrentForecast(location, units)) {
                is Result.Error -> Result.Error(result.error)
                is Result.Success -> result.toCurrentForecastDataResult()
            }
        }
    }

    override suspend fun getHourlyForecast(location: Location, units: Units): Result<List<HourlyData>> {
        return withContext(Dispatchers.IO) {
            return@withContext when(val result = weatherForecastRepository.getHourlyForecast(location, units)) {
                is Result.Error -> Result.Error(result.error)
                is Result.Success -> result.toHourlyDataListResult()
            }
        }
    }

    override suspend fun getDailyForecast(location: Location, units: Units): Result<List<DailyData>> {
        return withContext(Dispatchers.IO) {
            return@withContext when(val result =  weatherForecastRepository.getDailyForecast(location, units)) {
                is Result.Error -> Result.Error(result.error)
                is Result.Success -> result.toDailyDataListResult()
            }
        }
    }

    override suspend fun getAllWeatherDataForSelectedLocation(): Result<AllForecastData> {
        return withContext(Dispatchers.IO) {
            val location = savedLocationsRepository.getSelectedLocation()
                ?: return@withContext Result.Error(
                CallException(ErrorCode.NULL_DATA, "No Selected Location Found")
            )
            val currentResult = getCurrentForecast(location)
            if (currentResult is Result.Error) return@withContext Result.Error(currentResult.error)
            currentResult as Result.Success
            val newLocation = currentResult.data.toLocation(location)
            val hourly = async { getHourlyForecast(newLocation) }
            val daily = async { getDailyForecast(newLocation) }
            updateSelectedLocation(newLocation, location)
            val hourlyResult = hourly.await()
            val dailyResult = daily.await()
            if (hourlyResult is Result.Error) return@withContext Result.Error(hourlyResult.error)
            if (dailyResult is Result.Error) return@withContext Result.Error(dailyResult.error)
            hourlyResult as Result.Success
            dailyResult as Result.Success
            val next24hours = hourlyResult.data.subListCatching(0, 24)
            return@withContext Result.Success(
                AllForecastData(currentResult.data, next24hours, dailyResult.data)
            )
        }
    }
//
//    override suspend fun getCurrentForecastForSelectedLocation(): Result<CurrentForecastData> {
//        return withContext(Dispatchers.IO) {
//            val location = savedLocationsRepository.getSelectedLocation()
//                ?: return@withContext Result.Error(
//                    CallException(ErrorCode.NULL_DATA, "No Selected Location Found")
//                )
//            val currentResult = getCurrentForecast(location)
//            if (currentResult is Result.Error) return@withContext Result.Error(currentResult.error)
//            currentResult as Result.Success
//            val newLocation = currentResult.data.toLocation(location)
//            updateSelectedLocation(newLocation, location)
//            currentResult
//        }
//    }

    private suspend fun updateSelectedLocation(newLocation: Location, oldLocation: Location) {
        savedLocationsRepository.deleteLocation(oldLocation)
        savedLocationsRepository.saveLocation(newLocation)
        savedLocationsRepository.updateLocationSelectedStatus(true, newLocation)
    }
}