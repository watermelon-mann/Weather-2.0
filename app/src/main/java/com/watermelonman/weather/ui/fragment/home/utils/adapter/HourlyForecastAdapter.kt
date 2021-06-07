package com.watermelonman.weather.ui.fragment.home.utils.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.watermelonman.domain.utils.convertIconCodenameToIconUrl
import com.watermelonman.entities.enums.IconSize
import com.watermelonman.entities.hourly.HourlyData
import com.watermelonman.weather.R
import com.watermelonman.weather.appbase.recyclerview.BaseViewHolder
import com.watermelonman.weather.databinding.RvItemHourlyBinding

class HourlyForecastAdapter: RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder>() {

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private val weatherDataList: MutableList<HourlyData> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            RvItemHourlyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return weatherDataList.size
    }

    fun updateForecast(hourlyData: List<HourlyData>) {
        weatherDataList.clear()
        weatherDataList.addAll(hourlyData)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: RvItemHourlyBinding) : BaseViewHolder(binding) {
        override fun bind(position: Int) {
            val item = weatherDataList[position]
            with(binding) {
                txtTemperature.text = getString(R.string.temp_degrees_celsius, item.temp)
                txtWindSpeed.text = getString(R.string.wind_speed, item.windSpeed)
                imgWindDirection.rotation = item.windDegree
                txtTime.text = item.time
                imgIcon.load(item.imageCodename.convertIconCodenameToIconUrl(IconSize.X_2))
            }
        }
    }
}