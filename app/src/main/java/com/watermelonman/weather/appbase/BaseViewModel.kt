package com.watermelonman.weather.appbase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.watermelonman.weather.utils.navigation.ViewCommand
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    private val _command by lazy { Channel<ViewCommand>(Channel.BUFFERED) }
    val command = _command.receiveAsFlow()

    protected fun sendCommand(command: ViewCommand) {
        viewModelScope.launch { _command.send(command) }
    }

}