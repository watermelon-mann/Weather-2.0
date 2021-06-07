package com.watermelonman.weather.ui.fragment.selectlocation.viewmodel

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.RequiresPermission
import androidx.lifecycle.viewModelScope
import com.watermelonman.domain.interactor.CitiesInteractor
import com.watermelonman.domain.interactor.CurrentLocationInteractor
import com.watermelonman.domain.interactor.PreferencesInteractor
import com.watermelonman.domain.interactor.SavedLocationsInteractor
import com.watermelonman.domain.utils.locationservices.LocationRequester
import com.watermelonman.entities.enums.StartDestinations
import com.watermelonman.entities.location.City
import com.watermelonman.entities.location.Location
import com.watermelonman.entities.result.CallException
import com.watermelonman.entities.result.State
import com.watermelonman.entities.utils.toGeoNameLocation
import com.watermelonman.entities.utils.toLocation
import com.watermelonman.weather.appbase.BaseViewModel
import com.watermelonman.weather.ui.fragment.selectlocation.view.SelectLocationFragmentDirections
import com.watermelonman.weather.utils.navigation.Command
import com.watermelonman.weather.utils.processResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SelectLocationViewModel(
    private val citiesInteractor: CitiesInteractor,
    private val savedLocationsInteractor: SavedLocationsInteractor,
    private val preferencesInteractor: PreferencesInteractor,
    private val currentLocationInteractor: CurrentLocationInteractor
): BaseViewModel() {

    private val _allCityNames by lazy { MutableStateFlow<State<List<City>>>(State.Loading()) }
    val allCityNames = _allCityNames.asStateFlow()

    private val _onLocationSelected by lazy { Channel<Boolean>(Channel.BUFFERED) }
    val onLocationSelected = _onLocationSelected.receiveAsFlow()

    private val _actionShowPermissionRequiredDialog by lazy { Channel<Boolean>(Channel.BUFFERED) }
    val actionShowPermissionRequiredDialog = _actionShowPermissionRequiredDialog.receiveAsFlow()

    private val _actionShowLocationUnavailableDialog by lazy { Channel<Boolean>(Channel.BUFFERED) }
    val actionShowLocationUnavailableDialog = _actionShowLocationUnavailableDialog.receiveAsFlow()

    private val _actionOpenAppSettings by lazy { Channel<Intent>(Channel.BUFFERED) }
    val actionOpenAppSettings = _actionOpenAppSettings.receiveAsFlow()

    private val _actionShowLocationRequestProgressBar by lazy { Channel<Boolean>(Channel.BUFFERED) }
    val actionShowLocationRequestProgressBar = _actionShowLocationRequestProgressBar.receiveAsFlow()

    private val _actionDismissSnackBars by lazy { Channel<Boolean>(Channel.BUFFERED) }
    val actionDismissSnackBars = _actionDismissSnackBars.receiveAsFlow()

    init {
        getAllCityNames()
    }

    fun getAllCityNames() {
        viewModelScope.launch(Dispatchers.IO) {
            val cities = citiesInteractor.getAllCities()
            cities.processResult(::onCityNamesSuccess, ::onCityNamesError)
        }
    }

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    fun selectLocationOfUser() {
        showLocationRequestProgressBar(true)
        viewModelScope.launch(Dispatchers.IO) {
            val locationRequest = LocationRequester(
                onResultSucceed = ::onLocationEstablished,
                onServiceUnAvailable = ::onLocationUnavailable
            )
            currentLocationInteractor.registerLocationRequests(locationRequest)
        }
    }

    fun selectCity(city: City) {
        val location = city.toGeoNameLocation()
        selectLocation(location)
    }

    fun goToHomePage() {
        viewModelScope.launch(Dispatchers.IO) {
            _actionDismissSnackBars.send(true)
        }
        changeStartDestinationToHomeFragment()
        val action = SelectLocationFragmentDirections.actionSelectLocationFragmentToHomeFragment()
        sendCommand(Command.NavCommand(action))
    }

    fun openMap() {
        viewModelScope.launch(Dispatchers.IO) {
            _actionDismissSnackBars.send(true)
        }
        val action = SelectLocationFragmentDirections.actionSelectLocationFragmentToMapFragment()
        sendCommand(Command.NavCommand(action))
    }

    fun finishApp() {
        sendCommand(Command.FinishAppCommand())
    }

    fun showPermissionIsRequiredDialog() {
        viewModelScope.launch(Dispatchers.IO) {
            _actionShowPermissionRequiredDialog.send(true)
        }
    }

    fun openAppSettings(packageName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _actionDismissSnackBars.send(true)
        }
        val uri = Uri.fromParts("package", packageName, null)
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = uri
        }
        viewModelScope.launch(Dispatchers.IO) {
            _actionOpenAppSettings.send(intent)
        }
    }

    private fun onLocationEstablished(location: android.location.Location) {
        selectLocationOfUser(location)
        currentLocationInteractor.unRegisterLocationRequests()
    }

    private fun onLocationUnavailable() {
        showLocationRequestProgressBar(false)
        showLocationIsUnAvailableDialog()
        currentLocationInteractor.unRegisterLocationRequests()
    }

    private suspend fun onCityNamesSuccess(cities: List<City>) {
        _allCityNames.emit(State.Success(cities))
    }

    private suspend fun onCityNamesError(error: CallException) {
        _allCityNames.emit(State.Error(error.errorCode))
    }

    private fun showLocationIsUnAvailableDialog() {
        viewModelScope.launch(Dispatchers.IO) {
            _actionShowLocationUnavailableDialog.send(true)
        }
    }

    private fun showLocationRequestProgressBar(isVisible: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            _actionShowLocationRequestProgressBar.send(isVisible)
        }
    }

    private fun changeStartDestinationToHomeFragment() {
        preferencesInteractor.setStartDestination(StartDestinations.HOME)
    }

    private fun selectLocationOfUser(location: android.location.Location) {
        val loc = location.toLocation()
        selectLocation(loc)
    }

    private fun selectLocation(location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            savedLocationsInteractor.selectLocation(location)
            _onLocationSelected.send(true)
        }
    }
}