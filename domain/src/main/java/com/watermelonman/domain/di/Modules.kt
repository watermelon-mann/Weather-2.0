package com.watermelonman.domain.di

import com.watermelonman.domain.interactor.*
import com.watermelonman.domain.usecase.*
import org.koin.dsl.module

val interactorsModule = module {
    factory<WeatherForecastInteractor> { WeatherForecastUseCase(get(), get()) }
    factory<CurrentLocationInteractor> { CurrentLocationUseCase(get()) }
    factory<CitiesInteractor> { CitiesUseCase(get()) }
    factory<SavedLocationsInteractor> { SavedLocationsUseCase(get(), get()) }
    factory<PreferencesInteractor> { PreferencesUseCase(get()) }
}