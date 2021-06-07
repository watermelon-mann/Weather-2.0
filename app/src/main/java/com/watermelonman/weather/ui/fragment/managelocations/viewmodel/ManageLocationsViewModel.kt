package com.watermelonman.weather.ui.fragment.managelocations.viewmodel

import androidx.lifecycle.viewModelScope
import com.watermelonman.domain.interactor.PreferencesInteractor
import com.watermelonman.domain.interactor.SavedLocationsInteractor
import com.watermelonman.entities.enums.StartDestinations
import com.watermelonman.entities.location.Location
import com.watermelonman.entities.location.LocationItem
import com.watermelonman.entities.result.State
import com.watermelonman.entities.utils.toLocation
import com.watermelonman.entities.utils.toLocationItem
import com.watermelonman.weather.appbase.BaseViewModel
import com.watermelonman.weather.ui.fragment.managelocations.utils.adapter.SelectionMode
import com.watermelonman.weather.ui.fragment.managelocations.view.ManageLocationsFragmentDirections
import com.watermelonman.weather.ui.fragment.selectlocation.utils.LaunchMode
import com.watermelonman.weather.utils.SHORT_ANIM_DURATION
import com.watermelonman.weather.utils.navigation.Command
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ManageLocationsViewModel(
    private val savedLocationsInteractor: SavedLocationsInteractor,
    private val preferencesInteractor: PreferencesInteractor
): BaseViewModel() {

    private val _savedLocations by lazy { MutableStateFlow<State<List<LocationItem>>>(State.Loading()) }
    val savedLocations = _savedLocations.asStateFlow()

    private val _selectionMode by lazy { MutableStateFlow(SelectionMode.NOT_SELECTING) }
    val selectionMode = _selectionMode.asStateFlow()

    private val _onLocationSelected by lazy { Channel<Boolean>(Channel.BUFFERED) }
    val onLocationSelected = _onLocationSelected.receiveAsFlow()

    private val _actionSelectedLocationChanged by lazy { Channel<Boolean>(Channel.BUFFERED) }
    val actionSelectedLocationChanged = _actionSelectedLocationChanged.receiveAsFlow()

    private var isClickedOnDelete = false

    init {
        getSavedLocations()
    }

    fun getSavedLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            val savedLocations = savedLocationsInteractor.getSavedLocations()
                .map { it.toLocationItem() }
            _savedLocations.emit(State.Success(savedLocations))
        }
    }

    fun goToSelectLocationFragment(launchMode: LaunchMode = LaunchMode.NOT_INITIAL) {
        val action = ManageLocationsFragmentDirections
            .actionManageLocationsFragmentToSelectLocationFragment(launchMode)
        sendCommand(Command.NavCommand(action))
    }

    fun selectLocation(location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            savedLocationsInteractor.selectLocation(location)
            _onLocationSelected.send(true)
        }
    }

    fun setSelectionMode(selectionMode: SelectionMode) =
        viewModelScope.launch(Dispatchers.IO) {
            _selectionMode.emit(selectionMode)
        }


    fun deleteItems(items: List<LocationItem>) {
        isClickedOnDelete = true
        viewModelScope.launch(Dispatchers.IO) {
            var isSelectedLocationDeleted = false
            val locations = items.map {
                if (it.isSelected) {
                    isSelectedLocationDeleted = true
                }
                it.toLocation()
            }
            savedLocationsInteractor.deleteAll(locations)
            if (isSelectedLocationDeleted) {
                savedLocationsInteractor.selectFirstIfExists()
                _actionSelectedLocationChanged.send(true)
            }
            getSavedLocations()
        }
    }

    fun resetStartDestination() {
        preferencesInteractor.setStartDestination(StartDestinations.SELECT_LOCATION)
    }

    fun configureSelectionMode() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(SHORT_ANIM_DURATION)
            if (isClickedOnDelete) {
                setSelectionMode(SelectionMode.NOT_SELECTING)
            }
            isClickedOnDelete = false
        }
    }

}