package com.watermelonman.domain.usecase

import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.LocationRequest
import com.watermelonman.data.datastore.CurrentLocationRepository
import com.watermelonman.domain.interactor.CurrentLocationInteractor
import com.watermelonman.domain.utils.locationservices.LocationCallback
import com.watermelonman.domain.utils.locationservices.LocationRequester
import kotlinx.coroutines.*

class CurrentLocationUseCase(
    private val currentLocationRepository: CurrentLocationRepository
): CurrentLocationInteractor {

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    override suspend fun registerLocationRequests(locationRequester: LocationRequester) {
        currentLocationRepository.getLastLocation().addOnSuccessListener {
            it?.let{
                CoroutineScope(Dispatchers.IO).launch {
                    locationRequester.onResultSucceed(it)
                }
            }
            CoroutineScope(Dispatchers.IO).launch {
                requestLocationIfLastNotFound(it, locationRequester)
            }
        }
    }

    override fun unRegisterLocationRequests() {
        currentLocationRepository.unRegisterLocationRequests()
    }

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    private suspend fun requestLocationIfLastNotFound(it: Location?, locationRequester: LocationRequester) {
        if (it != null) return
        val locationRequest = LocationRequest.create().apply {
            interval = 700
            maxWaitTime = 3000
            fastestInterval = 300
            numUpdates = 1
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val locationCallback = LocationCallback(locationRequester)
        currentLocationRepository.requestLocation(locationRequest, locationCallback, Looper.getMainLooper())
    }
}
