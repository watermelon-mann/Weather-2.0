package com.watermelonman.weather.ui.activity.viewmodel

import androidx.lifecycle.ViewModel
import com.watermelonman.domain.interactor.PreferencesInteractor
import com.watermelonman.entities.enums.StartDestinations

class MainActivityViewModel(
    private val preferencesInteractor: PreferencesInteractor
): ViewModel() {


    fun getStartDestination(defaultValue: StartDestinations): StartDestinations {
        return preferencesInteractor.getStartDestination(defaultValue)
    }
}