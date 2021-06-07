package com.watermelonman.entities.daily


import com.google.gson.annotations.SerializedName

data class ForecastDaily(
    @SerializedName("daily")
    val daily: List<Daily>?,
    @SerializedName("lat")
    val lat: Int?,
    @SerializedName("lon")
    val lon: Int?,
    @SerializedName("timezone")
    val timezone: String?,
    @SerializedName("timezone_offset")
    val timezoneOffset: Int?
)