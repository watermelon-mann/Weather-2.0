package com.watermelonman.entities.enums

enum class StartDestinations {
    SELECT_LOCATION,
    HOME
}

val  Int.ordinalToStartDestination: StartDestinations
get() = when(this) {
    0 -> StartDestinations.SELECT_LOCATION
    1 -> StartDestinations.HOME
    else -> throw IllegalStateException("Invalid ordinal input for ${StartDestinations::class}")
}