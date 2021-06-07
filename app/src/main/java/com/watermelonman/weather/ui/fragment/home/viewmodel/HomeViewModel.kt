package com.watermelonman.weather.ui.fragment.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.watermelonman.domain.interactor.WeatherForecastInteractor
import com.watermelonman.entities.allforecast.AllForecastData
import com.watermelonman.entities.daily.DailyData
import com.watermelonman.entities.result.CallException
import com.watermelonman.entities.result.State
import com.watermelonman.entities.utils.convertTimeInSecondsToStringByPattern
import com.watermelonman.weather.appbase.BaseViewModel
import com.watermelonman.weather.ui.fragment.home.view.HomeFragmentDirections
import com.watermelonman.weather.utils.navigation.Command
import com.watermelonman.weather.utils.processResult
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(
    private val weatherForecastInteractor: WeatherForecastInteractor
): BaseViewModel() {

    private val _weatherForecast by lazy { MutableStateFlow<State<AllForecastData>>(State.Loading()) }
    val weatherForecast = _weatherForecast.asStateFlow()

    private val _time by lazy { MutableStateFlow("") }
    val time = _time.asStateFlow()

    private val weatherSuccessOrNull: AllForecastData?
    get() = (_weatherForecast.value as? State.Success)?.data

    private var getAllWeatherJob: Job? = null
    private var areTimeUpdatesEnabled = true
    var shouldStartAnimations = true

    init {
        getAllWeatherDataOfSelectedLocation()
    }

    fun finishApp() {
        sendCommand(Command.FinishAppCommand())
    }

    fun openSevenDayForecastBottomSheet(sevenDayForecast: List<DailyData>) {
        val action = HomeFragmentDirections
            .actionHomeFragmentToSevenDayForecastBottomSheet(sevenDayForecast.toTypedArray())
        sendCommand(Command.NavCommand(action))
    }

    fun openManageLocationsBottomSheet() {
        val action = HomeFragmentDirections.actionHomeFragmentToManageLocationsBottomSheet()
        sendCommand(Command.NavCommand(action))
    }

    fun getAllWeatherDataOfSelectedLocation() {
        getAllWeatherJob?.cancel()
        getAllWeatherJob = viewModelScope.launch(Dispatchers.IO) {
            _weatherForecast.emit(State.Loading())
            val result = weatherForecastInteractor.getAllWeatherDataForSelectedLocation()
            result.processResult(::onWeatherForecastSuccess, ::onWeatherForecastError)
        }
    }

    fun enableTimeUpdates() {
        if (areTimeUpdatesEnabled) return
        areTimeUpdatesEnabled = true
        val timezoneOffset = weatherSuccessOrNull?.current?.timezoneOffset
        viewModelScope.launch(Dispatchers.IO) {
            timezoneOffset?.let { launchTimeUpdater(it) }
        }
    }

    fun disableTimeUpdates() {
        areTimeUpdatesEnabled = false
    }

    private suspend fun onWeatherForecastSuccess(forecastData: AllForecastData) {
        updateTime(forecastData.current.timezoneOffset)
        startUpdatingTime(forecastData.current.timezoneOffset)
        _weatherForecast.emit(State.Success(forecastData))
    }

    private suspend fun onWeatherForecastError(error: CallException) {
        _weatherForecast.emit(State.Error(error.errorCode))
    }

    private fun startUpdatingTime(timezoneOffset: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            launchTimeUpdater(timezoneOffset)
        }
    }

    private suspend fun launchTimeUpdater(timezoneOffset: Int) {
        withContext(Dispatchers.Default) {
            if (!areTimeUpdatesEnabled) return@withContext
            updateTime(timezoneOffset)
            delay(60_000L)
            launchTimeUpdater(timezoneOffset)
        }
    }

    private suspend fun updateTime(timezoneOffset: Int) {
        val currentTimeSeconds = System.currentTimeMillis().div(1000)
        val time = currentTimeSeconds.convertTimeInSecondsToStringByPattern("HH:mm", timezoneOffset)
        _time.emit(time)
    }
}