package com.watermelonman.domain.interactor

import androidx.annotation.RequiresPermission
import com.watermelonman.domain.utils.locationservices.LocationRequester

interface CurrentLocationInteractor {
    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    suspend fun registerLocationRequests(locationRequester: LocationRequester)
    fun unRegisterLocationRequests()
}