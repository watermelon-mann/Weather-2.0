package com.watermelonman.data.repository

import com.watermelonman.data.datastore.WeatherForecastRepository
import com.watermelonman.data.remoteservice.WeatherAPI
import com.watermelonman.data.utils.safeApiCall
import com.watermelonman.entities.current.ForecastCurrent
import com.watermelonman.entities.daily.ForecastDaily
import com.watermelonman.entities.enums.Units
import com.watermelonman.entities.hourly.ForecastHourly
import com.watermelonman.entities.location.Location
import com.watermelonman.entities.result.Result

class WeatherForecastRepositoryImpl(
    private val weatherAPI: WeatherAPI
): WeatherForecastRepository {

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }

    override suspend fun getCurrentForecast(location: Location, units: Units): Result<ForecastCurrent> {
        println("api ket = ${getApiKey()}")
        val queries = mutableMapOf(
            "units" to units.type,
            "appid" to getApiKey()
        )
        if (location.geoNameId < 0) {
            queries["lat"] = location.lat.toString()
            queries["lon"] = location.lon.toString()
        } else {
            queries["id"] = location.geoNameId.toString()
        }
        return safeApiCall { weatherAPI.getCurrentForecast(queries) }
    }

    override suspend fun getHourlyForecast(location: Location, units: Units): Result<ForecastHourly> {
        val queries = mutableMapOf(
            "units" to units.type,
            "appid" to getApiKey(),
            "exclude" to "alerts,daily,minutely,current",
            "lat" to location.lat.toInt().toString(),
            "lon" to location.lon.toInt().toString()
        )
        return safeApiCall { weatherAPI.getHourlyForecast(queries) }
    }

    override suspend fun getDailyForecast(location: Location, units: Units): Result<ForecastDaily> {
        val queries = mutableMapOf(
            "units" to units.type,
            "appid" to getApiKey(),
            "exclude" to "alerts,hourly,minutely,current",
            "lat" to location.lat.toInt().toString(),
            "lon" to location.lon.toInt().toString()
        )
        return safeApiCall { weatherAPI.getSevenDayDailyForecast(queries) }
    }

    private external fun getApiKey(): String
}