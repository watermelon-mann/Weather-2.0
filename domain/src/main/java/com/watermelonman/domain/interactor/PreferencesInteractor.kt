package com.watermelonman.domain.interactor

import androidx.annotation.IdRes
import com.watermelonman.entities.enums.StartDestinations

interface PreferencesInteractor {
    fun getStartDestination(defValue: StartDestinations): StartDestinations
    fun setStartDestination(startDestination: StartDestinations)
}