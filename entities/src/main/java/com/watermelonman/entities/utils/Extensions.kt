package com.watermelonman.entities.utils

import java.text.SimpleDateFormat
import java.util.*

inline fun<reified T> List<T>.subListCatching(from: Int, to: Int): List<T> {
    if (from < 0 || to < 0) throw IllegalStateException("Can't accept negative index for List<${T::class.java.simpleName}> substring")
    if (from > to) throw IllegalStateException("Specified start index is bigger than end index for List<${T::class.java.simpleName}> substring")
    if (from > size) throw IllegalStateException("Specified start index for List<${T::class.java.simpleName}> substring is bigger than list's size")
    return try {
        subList(from, to)
    } catch (e: IndexOutOfBoundsException) {
        subList(from, size)
    }
}

fun Long.convertTimeInSecondsToStringByPattern(pattern: String, timezoneOffset: Int): String {
    val timeInMillisWithOffset = convertTimeInSecondsToTimeInMillisWithTimezoneOffset(timezoneOffset)
    val date = Date(timeInMillisWithOffset)
    val formatter = simpleDateFormatWithNoTimezoneOffset(pattern)
    return formatter.format(date)
}

private fun Long.convertTimeInSecondsToTimeInMillisWithTimezoneOffset(timezoneOffset: Int): Long {
    return this.plus(timezoneOffset).times(1000)
}

private fun simpleDateFormatWithNoTimezoneOffset(pattern: String) =
    SimpleDateFormat(pattern, Locale.ROOT)
    .apply { timeZone = TimeZone.getTimeZone("GMT") }