package com.watermelonman.data.datastore

import java.io.IOException
import kotlin.jvm.Throws

interface CitiesRepository {
    @Throws(IOException::class)
    fun getAllCitiesJson(): String
}