package com.watermelonman.entities.current

data class CurrentForecastData(
    val locationName: String,
    val lat: Double,
    val lon: Double,
    val geoNameId: Int,
    val temp: Double,
    val description: String,
    val humidity: Int,
    val tempFeelsLike: Double,
    val pressure: Int,
    val windSpeed: Double,
    val windDirection: Float,
    val timezoneOffset: Int
)