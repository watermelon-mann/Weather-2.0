package com.watermelonman.domain.utils.locationservices

import android.location.Location

class LocationRequester(
    val onResultSucceed: suspend (location: Location) -> Unit,
    val onServiceUnAvailable: suspend () -> Unit = {},
    val onServiceAvailable: suspend () -> Unit = {}
)