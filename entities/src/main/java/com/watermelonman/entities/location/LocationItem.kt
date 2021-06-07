package com.watermelonman.entities.location


data class LocationItem(
    val name: String,
    val geoNameId: Int = -1,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    var isSelected: Boolean = false,
    var isChecked: Boolean = false
)
