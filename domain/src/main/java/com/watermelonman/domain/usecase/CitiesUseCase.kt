package com.watermelonman.domain.usecase

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.watermelonman.data.datastore.CitiesRepository
import com.watermelonman.domain.interactor.CitiesInteractor
import com.watermelonman.entities.location.Cities
import com.watermelonman.entities.location.City
import com.watermelonman.entities.result.CallException
import com.watermelonman.entities.result.ErrorCode
import com.watermelonman.entities.result.Result


class CitiesUseCase(
    private val citiesRepository: CitiesRepository
): CitiesInteractor {

    private val gson = Gson()

    override fun getAllCities(): Result<ArrayList<City>> {
        return try {
            val citiesJson = citiesRepository.getAllCitiesJson()
            val typeToken = object : TypeToken<Cities>(){}.type
            val cities = gson.fromJson<Cities>(citiesJson, typeToken)
                .apply { sortBy { it.country } }
            Result.Success(cities)
        }catch (e: Exception) {
            Result.Error(CallException(ErrorCode.DATA_CONVERSION, "Could not parse the data of Cities"))
        }
    }
}