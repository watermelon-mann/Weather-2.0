package com.watermelonman.weather.utils.navigation

import androidx.navigation.NavDirections

sealed class Command {
    class FinishAppCommand: ViewCommand
    class NavigateUpCommand(val destinationId: Int? = null, val popupInclusive: Boolean = false): ViewCommand
    class NavCommand(val navDirections: NavDirections): ViewCommand
}