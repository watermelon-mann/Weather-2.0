package com.watermelonman.entities.location


import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("country")
    val country: String?,
    @SerializedName("geonameid")
    val geoNameId: Int,
    @SerializedName("name")
    val name: String?,
    @SerializedName("subcountry")
    val subCountry: String?
)