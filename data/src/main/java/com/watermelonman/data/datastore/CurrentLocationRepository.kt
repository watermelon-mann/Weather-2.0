package com.watermelonman.data.datastore

import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.Task

interface CurrentLocationRepository {
    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    suspend fun getLastLocation(): Task<Location>
    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    suspend fun requestLocation(locationRequest: LocationRequest, locationCallback: LocationCallback, looper: Looper): Task<Void>
    fun unRegisterLocationRequests()
}