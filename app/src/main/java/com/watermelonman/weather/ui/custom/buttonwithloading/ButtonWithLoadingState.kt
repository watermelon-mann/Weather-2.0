package com.watermelonman.weather.ui.custom.buttonwithloading

import android.graphics.drawable.Drawable

data class ButtonWithLoadingState(
    val icon: Drawable?,
    var showProgress: Boolean = false
)