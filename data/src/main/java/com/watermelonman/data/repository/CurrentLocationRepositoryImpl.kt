package com.watermelonman.data.repository

import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.watermelonman.data.datastore.CurrentLocationRepository

class CurrentLocationRepositoryImpl(
    private val context: Context
): CurrentLocationRepository {

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    private var locationCallback: LocationCallback? = null

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    override suspend fun getLastLocation(): Task<Location> {
        return fusedLocationClient.lastLocation
    }

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    override suspend fun requestLocation(
        locationRequest: LocationRequest,
        locationCallback: LocationCallback,
        looper: Looper
    ): Task<Void> {
        this.locationCallback = locationCallback
        return fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, looper)
    }

    override fun unRegisterLocationRequests() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }

}