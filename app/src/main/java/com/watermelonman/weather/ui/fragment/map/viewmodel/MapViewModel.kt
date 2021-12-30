package com.watermelonman.weather.ui.fragment.map.viewmodel

import android.app.Application
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.provider.Settings
import androidx.annotation.RequiresPermission
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.watermelonman.domain.interactor.CurrentLocationInteractor
import com.watermelonman.domain.interactor.PreferencesInteractor
import com.watermelonman.domain.interactor.SavedLocationsInteractor
import com.watermelonman.domain.utils.locationservices.LocationRequester
import com.watermelonman.entities.enums.StartDestinations
import com.watermelonman.entities.result.ErrorCode
import com.watermelonman.entities.result.State
import com.watermelonman.entities.utils.latLngLocation
import com.watermelonman.weather.BuildConfig
import com.watermelonman.weather.R
import com.watermelonman.weather.appbase.BaseViewModel
import com.watermelonman.weather.ui.fragment.map.view.MapFragmentDirections
import com.watermelonman.weather.utils.navigation.Command
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MapViewModel(
    application: Application,
    private val currentLocationInteractor: CurrentLocationInteractor,
    private val savedLocationsInteractor: SavedLocationsInteractor,
    private val preferencesInteractor: PreferencesInteractor
): BaseViewModel() {

    init {
        MapsInitializer.initialize(application)
        Places.initialize(application, BuildConfig.MAPS_API_KEY)
    }

    private val _selectedLocation by lazy { MutableStateFlow<State<LatLng>>(State.Loading()) }
    val selectedLocation = _selectedLocation.asStateFlow()

    private var selectedLocationOnMapState: State<LatLng> = State.Loading()

    private val _onDone = Channel<Boolean>(Channel.BUFFERED)
    val onDone = _onDone.receiveAsFlow()

    private val _userLocationLoading by lazy { MutableStateFlow(false) }
    val userLocationLoading = _userLocationLoading.asStateFlow()

    private val _actionShowPermissionRequiredDialog by lazy { Channel<Boolean>(Channel.BUFFERED) }
    val actionShowPermissionRequiredDialog = _actionShowPermissionRequiredDialog.receiveAsFlow()

    private val _actionOpenAppSettings by lazy { Channel<Intent>(Channel.BUFFERED) }
    val actionOpenAppSettings = _actionOpenAppSettings.receiveAsFlow()

    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    fun locateUser() {
        viewModelScope.launch(Dispatchers.IO) {
            _userLocationLoading.emit(true)
            val locationRequester = LocationRequester(
                onResultSucceed = ::sendUserLocation,
                onServiceUnAvailable = ::sendUserLocationError
            )
            currentLocationInteractor.registerLocationRequests(locationRequester)
        }
    }

    private suspend fun sendUserLocation(location: Location) {
        _userLocationLoading.emit(false)
        currentLocationInteractor.unRegisterLocationRequests()
        val state = State.Success(LatLng(location.latitude, location.longitude))
        selectedLocationOnMapState = state
        _selectedLocation.emit(state)
    }

    private suspend fun sendUserLocationError() {
        _userLocationLoading.emit(false)
        currentLocationInteractor.unRegisterLocationRequests()
        val state = State.Error<LatLng>(ErrorCode.RESPONSE_UNSUCCESSFUL)
        selectedLocationOnMapState = state
        _selectedLocation.emit(state)
    }

    fun selectLocation(latLng: LatLng) {
        viewModelScope.launch(Dispatchers.IO) {
            updateSelectedLocationOnMap(latLng)
            _selectedLocation.emit(selectedLocationOnMapState)
        }
    }

    fun updateSelectedLocationOnMap(latLng: LatLng) {
        selectedLocationOnMapState = State.Success(latLng)
    }

    fun selectMarkedLocation() {
        viewModelScope.launch(Dispatchers.IO) {
            (selectedLocationOnMapState as? State.Success)?.let { selectedLocation ->
                val location =
                    latLngLocation(selectedLocation.data.latitude, selectedLocation.data.longitude)
                savedLocationsInteractor.selectLocation(location)
                savedLocationsInteractor.fillMissingPropertiesOfSelectedLocation()
                _onDone.send(true)

            } ?: sendCommand(Command.NavigateUpCommand())
        }
    }

    fun navigateUpToManageLocations() {
        sendCommand(Command.NavigateUpCommand(R.id.manageLocationsFragment))
    }

    fun goToHomePage() {
        changeStartDestinationToHomeFragment()
        val action = MapFragmentDirections.actionMapFragmentToHomeFragment()
        sendCommand(Command.NavCommand(action))
    }

    fun showPermissionIsRequiredDialog() {
        viewModelScope.launch(Dispatchers.IO) {
            _actionShowPermissionRequiredDialog.send(true)
        }
    }
    fun openAppSettings(packageName: String) {
        val uri = Uri.fromParts("package", packageName, null)
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = uri
        }
        viewModelScope.launch(Dispatchers.IO) {
            _actionOpenAppSettings.send(intent)
        }
    }

    private fun changeStartDestinationToHomeFragment() {
        preferencesInteractor.setStartDestination(StartDestinations.HOME)
    }
}