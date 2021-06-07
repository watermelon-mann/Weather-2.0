package com.watermelonman.weather.utils.maps

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.maps.MapView

class MapLifecycleObserver
private constructor(
    private val owner: LifecycleOwner,
    private val mapView: MapView
) {

    init {
        enable()
    }

    private fun enable() {
        owner.lifecycle.addObserver(object : DefaultLifecycleObserver {

            override fun onStart(owner: LifecycleOwner) {
                super.onStart(owner)
                mapView.onStart()
            }

            override fun onResume(owner: LifecycleOwner) {
                super.onResume(owner)
                mapView.onResume()
            }

            override fun onPause(owner: LifecycleOwner) {
                super.onPause(owner)
                mapView.onPause()
            }

            override fun onStop(owner: LifecycleOwner) {
                super.onStop(owner)
                mapView.onStop()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                mapView.onDestroy()
                owner.lifecycle.removeObserver(this)
            }
        })
    }

    companion object {
        fun bindTo(owner: LifecycleOwner, mapView: MapView) {
            MapLifecycleObserver(owner, mapView)
        }
    }
}