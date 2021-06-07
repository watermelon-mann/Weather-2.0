package com.watermelonman.domain.interactor

import com.watermelonman.entities.allforecast.AllForecastData
import com.watermelonman.entities.location.Location
import com.watermelonman.entities.current.CurrentForecastData
import com.watermelonman.entities.result.Result
import com.watermelonman.entities.daily.DailyData
import com.watermelonman.entities.enums.Units
import com.watermelonman.entities.hourly.HourlyData

interface WeatherForecastInteractor {
    suspend fun getCurrentForecast(location: Location, units: Units = Units.METRIC): Result<CurrentForecastData>
    suspend fun getHourlyForecast(location: Location, units: Units = Units.METRIC): Result<List<HourlyData>>
    suspend fun getDailyForecast(location: Location, units: Units = Units.METRIC): Result<List<DailyData>>
    suspend fun getAllWeatherDataForSelectedLocation(): Result<AllForecastData>
}