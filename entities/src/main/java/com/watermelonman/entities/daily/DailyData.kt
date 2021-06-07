package com.watermelonman.entities.daily

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DailyData(
    val dt: Long,
    val imageCodename: String,
    val description: String,
    val weekday: String,
    val date: String,
    val dayTemp: Int,
    val nightTemp: Int,
    val feelsLike: FeelsLike,
    val sunrise: String,
    val sunset: String,
    val uvi: Double,
    val humidity: Int,
    val pressure: Int,
    val windDirection: Float,
    val windSpeed: Double
): Parcelable