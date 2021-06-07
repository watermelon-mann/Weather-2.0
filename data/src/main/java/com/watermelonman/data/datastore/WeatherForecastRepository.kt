package com.watermelonman.data.datastore

import com.watermelonman.entities.result.Result
import com.watermelonman.entities.hourly.ForecastHourly
import com.watermelonman.entities.current.ForecastCurrent
import com.watermelonman.entities.daily.ForecastDaily
import com.watermelonman.entities.enums.Units
import com.watermelonman.entities.location.Location

interface WeatherForecastRepository {
    suspend fun getCurrentForecast(location: Location, units: Units): Result<ForecastCurrent>
    suspend fun getHourlyForecast(location: Location, units: Units): Result<ForecastHourly>
    suspend fun getDailyForecast(location: Location, units: Units): Result<ForecastDaily>
}