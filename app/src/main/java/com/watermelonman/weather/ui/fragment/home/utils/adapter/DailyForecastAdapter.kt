package com.watermelonman.weather.ui.fragment.home.utils.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.watermelonman.domain.utils.convertIconCodenameToIconUrl
import com.watermelonman.entities.daily.DailyData
import com.watermelonman.entities.daily.FeelsLike
import com.watermelonman.weather.R
import com.watermelonman.weather.appbase.recyclerview.BaseViewHolder
import com.watermelonman.weather.databinding.RvItemDailyBinding
import com.watermelonman.weather.databinding.RvItemViewSevenDayForecastBinding
import com.watermelonman.entities.enums.IconSize

class DailyForecastAdapter: RecyclerView.Adapter<BaseViewHolder>() {

    private val dailyForecastData: MutableList<DailyData> = ArrayList()
    private var onViewSevenDayForecastClickListener = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == R.layout.rv_item_view_seven_day_forecast) {
            ViewSevenDayForecastViewHolder(
                RvItemViewSevenDayForecastBinding.inflate(inflater, parent, false)
            )
        } else {
            DailyForecastViewHolder(
                RvItemDailyBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return dailyForecastData.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLastPosition(position)) {
            R.layout.rv_item_view_seven_day_forecast
        } else {
            R.layout.rv_item_daily
        }
    }

    fun updateForecast(items: List<DailyData>) {
        dailyForecastData.clear()
        items.subList(0,4).let { dailyForecastData.addAll(it) }
        dailyForecastData.add(emptyItem())
        notifyDataSetChanged()
    }

    fun setOnViewForecastClickListener(listener: () -> Unit) {
        onViewSevenDayForecastClickListener = listener
    }

    private fun isLastPosition(position: Int) = position == dailyForecastData.size - 1
    private fun emptyItem() = DailyData(0, "", "", "","", 0,0, FeelsLike(0.0,0.0,0.0,0.0),"", "", 0.0, 0, 0, 0.0f, 0.0)

    inner class DailyForecastViewHolder(private val binding: RvItemDailyBinding): BaseViewHolder(binding) {
        override fun bind(position: Int) {
            val item = dailyForecastData[position]
            val weekday = when(position) {
                0 -> getString(R.string.today)
                1 -> getString(R.string.tomorrow)
                else -> item.weekday
            }
            with(binding) {
                imgIconDaily.load(item.imageCodename.convertIconCodenameToIconUrl(IconSize.X_2))
                txtDayAndDescription.text = getString(R.string.forecast_day_and_desc, weekday, item.description)
                txtDayNightTemperature.text = getString(R.string.day_night_temperature, item.dayTemp, item.nightTemp)
            }
        }
    }

    inner class ViewSevenDayForecastViewHolder(private val binding: RvItemViewSevenDayForecastBinding): BaseViewHolder(binding) {
        override fun bind(position: Int) {
            with(binding) {
                btnViewSevenDayForecast.setOnClickListener {
                    onViewSevenDayForecastClickListener()
                }
            }
        }
    }
}