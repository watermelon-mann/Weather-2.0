package com.watermelonman.entities.allforecast

import com.watermelonman.entities.current.CurrentForecastData
import com.watermelonman.entities.daily.DailyData
import com.watermelonman.entities.hourly.HourlyData

data class AllForecastData(
    val current: CurrentForecastData,
    val hourly: List<HourlyData>,
    val daily: List<DailyData>
)
