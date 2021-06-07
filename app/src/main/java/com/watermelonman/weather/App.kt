package com.watermelonman.weather

import android.app.Application
import com.watermelonman.data.di.apiModule
import com.watermelonman.data.di.databaseModule
import com.watermelonman.data.di.repositoryModule
import com.watermelonman.domain.di.interactorsModule
import com.watermelonman.weather.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(listOf(
                apiModule,
                databaseModule,
                repositoryModule,
                interactorsModule,
                viewModelModule
            ))
        }
    }
}