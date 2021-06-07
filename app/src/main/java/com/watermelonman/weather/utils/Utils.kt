package com.watermelonman.weather.utils

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

fun cameraPosition(latLng: LatLng, zoom: Float = 12f) =
    CameraUpdateFactory.newCameraPosition(
        CameraPosition.fromLatLngZoom(latLng, zoom)
    )

fun simpleMarkerPosition(latLng: LatLng, title: String? = null): MarkerOptions {
    val options = MarkerOptions().position(latLng)
    title?.let { options.title(it) }
    return options
}
