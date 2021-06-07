package com.watermelonman.entities.hourly

data class HourlyData(
    val dt: Long,
    val imageCodename: String,
    val temp: Double,
    val windSpeed: Double,
    val windDegree: Float,
    val time: String
)