package com.watermelonman.data.repository

import android.content.Context
import com.watermelonman.data.datastore.CitiesRepository
import java.io.IOException
import java.nio.charset.Charset
import kotlin.jvm.Throws

class CitiesRepositoryImpl(
    private val context: Context
) : CitiesRepository {
    @Throws(IOException::class)
    override fun getAllCitiesJson(): String {
        val inputStream = context.assets.open("cities.json")
        val buffer = ByteArray(inputStream.available())
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer, Charset.forName("UTF-8"))
    }
}