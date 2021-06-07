package com.watermelonman.data.remoteservice

import com.watermelonman.entities.current.ForecastCurrent
import com.watermelonman.entities.daily.ForecastDaily
import com.watermelonman.entities.hourly.ForecastHourly
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface WeatherAPI {

    companion object {
        const val BASE_URL = "http://api.openweathermap.org/"
        const val ENDPOINT_FIVE_DAY_HOURLY = "data/2.5/forecast"
        const val ENDPOINT_CURRENT = "data/2.5/weather"
        const val ENDPOINT_ONE_CALL_API = "data/2.5/onecall"
        const val ENDPOINT_HISTORICAL_WEATHER = "data/2.5/onecall/timemachine"
        const val ICON_URL = "http://openweathermap.org/img/wn/"
    }

    @GET(ENDPOINT_CURRENT)
    suspend fun getCurrentForecast(@QueryMap queries: Map<String, String>): Response<ForecastCurrent>

    @GET(ENDPOINT_ONE_CALL_API)
    suspend fun getSevenDayDailyForecast(@QueryMap queries: Map<String, String>): Response<ForecastDaily>

    @GET(ENDPOINT_ONE_CALL_API)
    suspend fun getHourlyForecast(@QueryMap queries: Map<String, String>): Response<ForecastHourly>
}