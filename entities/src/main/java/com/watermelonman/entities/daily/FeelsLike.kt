package com.watermelonman.entities.daily

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeelsLike(
    val morn: Double,
    val day: Double,
    val eve: Double,
    val night: Double
): Parcelable
