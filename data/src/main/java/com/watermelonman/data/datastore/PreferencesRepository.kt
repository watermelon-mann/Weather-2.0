package com.watermelonman.data.datastore

import androidx.annotation.IdRes
import com.watermelonman.entities.enums.StartDestinations

interface PreferencesRepository {
    fun getStartDestination(defValue: StartDestinations): StartDestinations
    fun saveStartDestination(startDestinations: StartDestinations)
}