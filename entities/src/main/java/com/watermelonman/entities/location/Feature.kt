package com.watermelonman.entities.location


import com.google.gson.annotations.SerializedName

data class Feature(
    @SerializedName("text")
    val text: String?
)