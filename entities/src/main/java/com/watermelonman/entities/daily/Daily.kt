package com.watermelonman.entities.daily


import com.google.gson.annotations.SerializedName

data class Daily(
    @SerializedName("clouds")
    val clouds: Int?,
    @SerializedName("dew_point")
    val dewPoint: Double?,
    @SerializedName("dt")
    val dt: Long?,
    @SerializedName("feels_like")
    val feelsLike: FeelsLikeRaw?,
    @SerializedName("humidity")
    val humidity: Int?,
    @SerializedName("moon_phase")
    val moonPhase: Double?,
    @SerializedName("moonrise")
    val moonrise: Long?,
    @SerializedName("moonset")
    val moonset: Long?,
    @SerializedName("pop")
    val pop: Double?,
    @SerializedName("pressure")
    val pressure: Int?,
    @SerializedName("rain")
    val rain: Double?,
    @SerializedName("sunrise")
    val sunrise: Long?,
    @SerializedName("sunset")
    val sunset: Long?,
    @SerializedName("temp")
    val temp: Temp?,
    @SerializedName("uvi")
    val uvi: Double?,
    @SerializedName("weather")
    val weather: List<Weather>?,
    @SerializedName("wind_deg")
    val windDeg: Int?,
    @SerializedName("wind_gust")
    val windGust: Double?,
    @SerializedName("wind_speed")
    val windSpeed: Double?
)