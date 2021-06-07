package com.watermelonman.domain.usecase

import com.watermelonman.data.datastore.PreferencesRepository
import com.watermelonman.domain.interactor.PreferencesInteractor
import com.watermelonman.entities.enums.StartDestinations

class PreferencesUseCase(
    private val preferencesRepository: PreferencesRepository
): PreferencesInteractor {
    override fun getStartDestination(defValue: StartDestinations): StartDestinations {
        return preferencesRepository.getStartDestination(defValue)
    }

    override fun setStartDestination(startDestination: StartDestinations) {
        preferencesRepository.saveStartDestination(startDestination)
    }
}