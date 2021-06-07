package com.watermelonman.weather.ui.sharedviewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watermelonman.domain.interactor.SavedLocationsInteractor
import com.watermelonman.weather.utils.processResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SelectedLocationViewModel(
    private val savedLocationsInteractor: SavedLocationsInteractor
): ViewModel() {

    private val _actionUpdateSavedLocations by lazy { Channel<Boolean>(Channel.BUFFERED) }
    val actionUpdateSavedLocations = _actionUpdateSavedLocations.receiveAsFlow()

    private val _actionUpdateAllForecastData by lazy { Channel<Boolean>(Channel.BUFFERED) }
    val actionUpdateAllForecastData = _actionUpdateAllForecastData.receiveAsFlow()

    private val _actionOnUpdateError by lazy { Channel<Boolean>(Channel.BUFFERED) }
    val actionOnUpdateError = _actionOnUpdateError.receiveAsFlow()

    private val _actionOnUpdateSuccess by lazy { Channel<Boolean>(Channel.BUFFERED) }
    val actionOnUpdateSuccess = _actionOnUpdateSuccess.receiveAsFlow()

    var isAlreadyFetchingData = false

    fun emitSavedLocationsUpdates() {
        viewModelScope.launch(Dispatchers.IO) {
            savedLocationsInteractor.fillMissingPropertiesOfSelectedLocation()
                .processResult({ notifyAllOnSuccessObservers() }, { notifyOnErrorObservers() })
        }
    }

    fun updateAllForecastData() {
        viewModelScope.launch(Dispatchers.IO) {
            _actionUpdateAllForecastData.send(true)
        }
    }

    fun notifyOnSuccessObservers() {
        viewModelScope.launch(Dispatchers.IO) {
            isAlreadyFetchingData = true
            _actionUpdateSavedLocations.send(true)
            _actionUpdateAllForecastData.send(true)
        }
    }

    private suspend fun notifyAllOnSuccessObservers() {
        isAlreadyFetchingData = true
        _actionUpdateSavedLocations.send(true)
        _actionUpdateAllForecastData.send(true)
        _actionOnUpdateSuccess.send(true)
    }

    private suspend fun notifyOnErrorObservers() {
        _actionOnUpdateError.send(true)
    }
}