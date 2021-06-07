package com.watermelonman.weather.di

import com.watermelonman.weather.ui.activity.viewmodel.MainActivityViewModel
import com.watermelonman.weather.ui.fragment.managelocations.viewmodel.ManageLocationsViewModel
import com.watermelonman.weather.ui.fragment.home.dialog.sevenday.viewmodel.SevenDayForecastViewModel
import com.watermelonman.weather.ui.fragment.home.viewmodel.HomeViewModel
import com.watermelonman.weather.ui.fragment.map.viewmodel.MapViewModel
import com.watermelonman.weather.ui.fragment.selectlocation.viewmodel.SelectLocationViewModel
import com.watermelonman.weather.ui.sharedviewmodels.SelectedLocationViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainActivityViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { MapViewModel(androidApplication(), get(), get()) }
    viewModel { SelectLocationViewModel(get(), get(), get(), get()) }
    viewModel { SevenDayForecastViewModel() }
    viewModel { ManageLocationsViewModel(get(), get()) }
    viewModel { SelectedLocationViewModel(get()) }
}