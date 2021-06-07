package com.watermelonman.domain.interactor

import com.watermelonman.entities.location.City
import com.watermelonman.entities.result.Result

interface CitiesInteractor {
    fun getAllCities(): Result<ArrayList<City>>
}