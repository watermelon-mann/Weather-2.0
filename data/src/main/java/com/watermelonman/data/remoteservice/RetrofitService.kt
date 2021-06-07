package com.watermelonman.data.remoteservice

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitService {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofitBuilder = Retrofit.Builder()
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())

    private inline fun <reified T> Retrofit.Builder.create(baseUrl: String): T  {
        return baseUrl(baseUrl)
            .build()
            .create(T::class.java)
    }

    val weatherApi: WeatherAPI = retrofitBuilder.create(WeatherAPI.BASE_URL)
}