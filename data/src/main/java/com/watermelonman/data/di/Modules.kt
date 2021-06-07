package com.watermelonman.data.di

import com.watermelonman.data.datastore.*
import com.watermelonman.data.local.dao.LocationDao
import com.watermelonman.data.local.db.Room
import com.watermelonman.data.remoteservice.RetrofitService
import com.watermelonman.data.repository.*
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val apiModule = module {
    single { RetrofitService.weatherApi }
}

val databaseModule = module {
    fun provideLocationDao(room: Room): LocationDao = room.locationDao

    single { Room.getInstance(androidApplication()) }
    single { provideLocationDao(get()) }
}

val repositoryModule = module {
    factory<WeatherForecastRepository> { WeatherForecastRepositoryImpl(get()) }
    factory<CurrentLocationRepository> { CurrentLocationRepositoryImpl(androidContext()) }
    factory<CitiesRepository> { CitiesRepositoryImpl(androidContext()) }
    factory<PreferencesRepository> { PreferencesRepositoryImpl(androidContext()) }
    factory<SavedLocationsRepository> { SavedLocationsRepositoryImpl(get()) }
}