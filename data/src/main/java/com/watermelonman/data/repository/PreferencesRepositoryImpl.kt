package com.watermelonman.data.repository

import android.content.Context
import com.watermelonman.data.datastore.PreferencesRepository
import com.watermelonman.entities.enums.StartDestinations
import com.watermelonman.entities.enums.ordinalToStartDestination

class PreferencesRepositoryImpl(
    context: Context
): PreferencesRepository {

    private val name = "pref"
    private val keyStartDestination = "keyStartDestination"

    private val sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)

    override fun getStartDestination(defValue: StartDestinations): StartDestinations {
        val savedStartDestinations =
            sharedPreferences.getInt(keyStartDestination, StartDestinations.SELECT_LOCATION.ordinal)
        return savedStartDestinations.ordinalToStartDestination
    }

    override fun saveStartDestination(startDestinations: StartDestinations) {
        sharedPreferences.edit().putInt(keyStartDestination, startDestinations.ordinal).apply()
    }
}