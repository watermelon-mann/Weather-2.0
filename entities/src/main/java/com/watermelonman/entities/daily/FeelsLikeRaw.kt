package com.watermelonman.entities.daily


import com.google.gson.annotations.SerializedName

data class FeelsLikeRaw(
    @SerializedName("day")
    val day: Double?,
    @SerializedName("eve")
    val eve: Double?,
    @SerializedName("morn")
    val morn: Double?,
    @SerializedName("night")
    val night: Double?
)