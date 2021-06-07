package com.watermelonman.entities.utils

import com.watermelonman.entities.current.CurrentForecastData
import com.watermelonman.entities.current.ForecastCurrent
import com.watermelonman.entities.daily.Daily
import com.watermelonman.entities.daily.DailyData
import com.watermelonman.entities.daily.FeelsLike
import com.watermelonman.entities.daily.ForecastDaily
import com.watermelonman.entities.hourly.ForecastHourly
import com.watermelonman.entities.hourly.Hourly
import com.watermelonman.entities.hourly.HourlyData
import com.watermelonman.entities.location.City
import com.watermelonman.entities.location.Location
import com.watermelonman.entities.location.LocationItem
import com.watermelonman.entities.result.CallException
import com.watermelonman.entities.result.ErrorCode
import com.watermelonman.entities.result.Result

fun android.location.Location.toLocation(geoNameIdi: Int = -1, isSelected: Boolean = false): Location {
    return Location(
        name = "",
        geoNameId = geoNameIdi,
        lat = latitude,
        lon = longitude,
        isSelected = isSelected
    )
}

fun City.toGeoNameLocation(isSelected: Boolean = false): Location {
    return Location(
        name = name ?: "",
        geoNameId = geoNameId,
        isSelected = isSelected
    )
}

fun CurrentForecastData.toLocation(oldLocation: Location): Location {
    return Location(
        name = locationName,
        geoNameId = geoNameId,
        lat = lat,
        lon = lon,
        isSelected = oldLocation.isSelected
    )
}

fun latLngLocation(lat: Double, lon: Double): Location {
    return Location(
        name = "",
        geoNameId = -1,
        lat, lon,
        isSelected = false
    )
}

fun Result<ForecastCurrent>.toCurrentForecastDataResult(): Result<CurrentForecastData> {
    if (this is Result.Error) return Result.Error(this.error)
    this as Result.Success
    val isThereNullInRequiredData = checkIfNullByDisjunction(
        data.name,
        data.coord?.lat,
        data.coord?.lon,
        data.id,
        data.main?.temp,
        data.weather?.get(0)?.description,
        data.main?.humidity,
        data.main?.feelsLike,
        data.main?.pressure,
        data.wind?.speed,
        data.wind?.deg,
        data.timezone
    )
    return if (isThereNullInRequiredData) {
        Result.Error(CallException(
            ErrorCode.NULL_DATA,
            "There are null values in required data of CurrentForecastData"))
    }
    else Result.Success(
        CurrentForecastData(
            data.name!!,
            data.coord?.lat!!,
            data.coord.lon!!,
            data.id!!,
            data.main?.temp!!,
            data.weather?.get(0)?.description!!,
            data.main.humidity!!,
            data.main.feelsLike!!,
            data.main.pressure!!,
            data.wind?.speed!!,
            data.wind.deg?.toFloat()!!,
            data.timezone!!
        )
    )
}


fun Result<ForecastHourly>.toHourlyDataListResult(): Result<List<HourlyData>> {
    if (this is Result.Error) return Result.Error(this.error)
    this as Result.Success
    val hourlyDataList: List<HourlyData> =  data.hourly?.map {
        val hourlyDataResult = it.toHourlyDataResult(data)
        if (hourlyDataResult is Result.Error) return Result.Error(hourlyDataResult.error)
        else {
            (hourlyDataResult as Result.Success)
            return@map hourlyDataResult.data
        }
    } ?: return Result.Error(CallException(ErrorCode.NULL_DATA, "HourlyData list is null"))
    return Result.Success(hourlyDataList)
}

fun Hourly.toHourlyDataResult(forecast: ForecastHourly): Result<HourlyData> {
    val isThereNullInRequiredData = checkIfNullByDisjunction(
        weather?.get(0)?.icon,
        forecast.timezoneOffset,
        dt,
        weather?.get(0)?.icon,
        temp,
        windSpeed,
        windDeg?.toFloat()
    )
    if (isThereNullInRequiredData) return Result.Error(CallException(
        ErrorCode.NULL_DATA,
        "There are null values in required data of HourlyData"))
    val iconCodename = weather?.get(0)?.icon!!
    val time = dt!!.convertTimeInSecondsToStringByPattern("HH:mm", forecast.timezoneOffset!!)
    return Result.Success(
        HourlyData(
            dt = dt,
            imageCodename = iconCodename,
            temp = temp!!,
            windSpeed = windSpeed!!,
            windDegree = windDeg?.toFloat()!!,
            time = time
        )
    )
}

fun Result<ForecastDaily>.toDailyDataListResult(): Result<List<DailyData>> {
    if (this is Result.Error) return Result.Error(this.error)
    this as Result.Success
    val dailyDataList: List<DailyData> = data.daily?.map {
        val dailyDataResult = it.toDailyDataResult(data)
        if (dailyDataResult is Result.Error) return Result.Error(dailyDataResult.error)
        else {
            dailyDataResult as Result.Success
            return@map dailyDataResult.data
        }
    } ?: return Result.Error(CallException(ErrorCode.NULL_DATA, "DailyData list is null"))
    return Result.Success(dailyDataList)
}

fun Daily.toDailyDataResult(forecast: ForecastDaily): Result<DailyData> {
    val isThereNullInRequiredData = checkIfNullByDisjunction(
        dt,
        weather?.get(0)?.icon,
        weather?.get(0)?.main,
        forecast.timezoneOffset,
        temp?.day?.toInt(),
        temp?.night?.toInt(),
        feelsLike?.morn?.toInt(),
        feelsLike?.day?.toInt(),
        feelsLike?.eve?.toInt(),
        feelsLike?.night?.toInt(),
        sunrise,
        sunset,
        uvi,
        windDeg,
        windSpeed,
        humidity,
        pressure
    )
    return if (isThereNullInRequiredData) {
        Result.Error(
            CallException(
                ErrorCode.NULL_DATA, "There are null values in required data Of DailyData"
            )
        )
    } else {
        val iconCodename = weather?.get(0)?.icon!!
        val description = weather[0].description!!
        val weekday = dt!!.convertTimeInSecondsToStringByPattern("EEE", forecast.timezoneOffset!!)
        val sunrise = this.sunrise!!.convertTimeInSecondsToStringByPattern("HH:mm", forecast.timezoneOffset)
        val sunset = this.sunset!!.convertTimeInSecondsToStringByPattern("HH:mm", forecast.timezoneOffset)
        val date = dt.convertTimeInSecondsToStringByPattern("MM/dd", forecast.timezoneOffset)
        val feelsLike = FeelsLike(
            this.feelsLike?.morn!!,
            this.feelsLike.day!!,
            this.feelsLike.eve!!,
            this.feelsLike.night!!
        )
        Result.Success(
            DailyData(
                dt = dt,
                imageCodename = iconCodename,
                description = description,
                weekday = weekday,
                date = date,
                dayTemp = temp?.day?.toInt()!!,
                nightTemp = temp.night?.toInt()!!,
                feelsLike = feelsLike,
                sunrise = sunrise,
                sunset = sunset,
                uvi = uvi!!,
                humidity = humidity!!,
                pressure = pressure!!,
                windDirection = windDeg?.toFloat()!!,
                windSpeed = windSpeed!!
            )
        )
    }
}

fun Location.toLocationItem(): LocationItem {
    return LocationItem(name, geoNameId, lat, lon, isSelected)
}

fun LocationItem.toLocation(): Location {
    return Location(name, geoNameId, lat, lon, isSelected)
}