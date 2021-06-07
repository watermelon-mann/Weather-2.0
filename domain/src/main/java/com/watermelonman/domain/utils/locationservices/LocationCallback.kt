package com.watermelonman.domain.utils.locationservices

import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationCallback(
    private val locationRequester: LocationRequester
): LocationCallback() {

    override fun onLocationResult(p0: LocationResult) {
        super.onLocationResult(p0)
        CoroutineScope(Dispatchers.IO).launch {
            locationRequester.onResultSucceed(p0.lastLocation)
        }
    }

    override fun onLocationAvailability(p0: LocationAvailability) {
        super.onLocationAvailability(p0)
        CoroutineScope(Dispatchers.IO).launch {
            if (p0.isLocationAvailable) {
                locationRequester.onServiceAvailable()
            }else {
                locationRequester.onServiceUnAvailable()
            }
        }
    }
}