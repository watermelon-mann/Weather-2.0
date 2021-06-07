package com.watermelonman.entities.hourly


import com.google.gson.annotations.SerializedName

data class Hourly(
    @SerializedName("clouds")
    val clouds: Int?,
    @SerializedName("dew_point")
    val dewPoint: Double?,
    @SerializedName("dt")
    val dt: Long?,
    @SerializedName("feels_like")
    val feelsLike: Double?,
    @SerializedName("humidity")
    val humidity: Int?,
    @SerializedName("pop")
    val pop: Double?,
    @SerializedName("pressure")
    val pressure: Int?,
    @SerializedName("rain")
    val rain: Rain?,
    @SerializedName("temp")
    val temp: Double?,
    @SerializedName("uvi")
    val uvi: Double?,
    @SerializedName("visibility")
    val visibility: Int?,
    @SerializedName("weather")
    val weather: List<Weather>?,
    @SerializedName("wind_deg")
    val windDeg: Int?,
    @SerializedName("wind_gust")
    val windGust: Double?,
    @SerializedName("wind_speed")
    val windSpeed: Double?
)