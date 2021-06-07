package com.watermelonman.entities.location

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Location(
    val name: String,
    @PrimaryKey(autoGenerate = false)
    val geoNameId: Int = -1,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    var isSelected: Boolean = false
)