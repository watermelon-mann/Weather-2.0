package com.watermelonman.weather.ui.fragment.home.dialog.sevenday.utils.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import coil.load
import com.watermelonman.domain.utils.convertIconCodenameToIconUrl
import com.watermelonman.entities.daily.DailyData
import com.watermelonman.entities.enums.IconSize
import com.watermelonman.weather.R
import com.watermelonman.weather.appbase.recyclerview.BaseViewHolder
import com.watermelonman.weather.databinding.RvItemDailyFullBinding

class SevenDayForecastAdapter: RecyclerView.Adapter<BaseViewHolder>() {

    private val dailyDataList: MutableList<DailyData> = ArrayList()
    private var expandedPosition = -1
    private var previousExpandedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return ViewHolder(
            RvItemDailyFullBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return dailyDataList.size
    }

    fun updateForecast(items: List<DailyData>) {
        dailyDataList.clear()
        dailyDataList.addAll(items)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val binding: RvItemDailyFullBinding) : BaseViewHolder(binding) {
        override fun bind(position: Int) {
            val item = dailyDataList[position]
            val weekday = when (position) {
                0 -> getString(R.string.today)
                1 -> getString(R.string.tomorrow)
                else -> item.weekday
            }
            with(binding) {
                groupAdditionalInfo.isVisible = expandedPosition == position
                root.background = if (expandedPosition == position) {
                    getDrawable(R.drawable.bg_rect_radius_12_bottom_sheet_blue_secondary_with_ripple)
                } else {
                    getDrawable(R.drawable.bg_rect_radius_12_bottom_sheet_blue_with_ripple)
                }
                txtDate.text = item.date
                imgIconDaily.load(item.imageCodename.convertIconCodenameToIconUrl(IconSize.X_4))
                txtDayAndDescription.text =
                    getString(R.string.forecast_day_and_desc, weekday, item.description)
                txtDayNightTemperature.text =
                    getString(R.string.day_night_temperature, item.dayTemp, item.nightTemp)
                txtRealFeelMorn.text = getString(R.string.temp_degrees_celsius, item.feelsLike.morn)
                txtRealFeelDay.text = getString(R.string.temp_degrees_celsius, item.feelsLike.day)
                txtRealFeelEven.text = getString(R.string.temp_degrees_celsius, item.feelsLike.eve)
                txtRealFeelNight.text =
                    getString(R.string.temp_degrees_celsius, item.feelsLike.night)
                txtUvIndex.text = item.uvi.toString()
                txtHumidity.text = getString(R.string.humidity_percent, item.humidity)
                txtPressure.text = getString(R.string.pressure_h_pa, item.pressure)
                txtWindSpeed.text = getString(R.string.wind_speed, item.windSpeed)
                imgWindDirection.rotation = item.windDirection
                txtSunrise.text = getString(R.string.sunrise_at, item.sunrise)
                txtSunset.text = getString(R.string.sunrise_at, item.sunset)
                root.setOnClickListener {
                    previousExpandedPosition = expandedPosition
                    expandedPosition = if (expandedPosition != position) position else -1
                    TransitionManager.beginDelayedTransition(root)
                    notifyItemChanged(position)
                    if (previousExpandedPosition != -1 && previousExpandedPosition != expandedPosition) {
                        notifyItemChanged(previousExpandedPosition)
                    }
                }
            }
        }
    }
}