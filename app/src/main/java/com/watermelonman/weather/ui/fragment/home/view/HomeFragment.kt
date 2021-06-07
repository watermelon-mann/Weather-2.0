package com.watermelonman.weather.ui.fragment.home.view

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.watermelonman.entities.allforecast.AllForecastData
import com.watermelonman.entities.current.CurrentForecastData
import com.watermelonman.entities.daily.DailyData
import com.watermelonman.entities.hourly.HourlyData
import com.watermelonman.entities.result.State
import com.watermelonman.weather.R
import com.watermelonman.weather.appbase.BaseFragmentMVVM
import com.watermelonman.weather.databinding.FragmentHomeBinding
import com.watermelonman.weather.ui.fragment.home.utils.adapter.DailyForecastAdapter
import com.watermelonman.weather.ui.fragment.home.utils.adapter.HourlyForecastAdapter
import com.watermelonman.weather.ui.fragment.home.viewmodel.HomeViewModel
import com.watermelonman.weather.ui.sharedviewmodels.SelectedLocationViewModel
import com.watermelonman.weather.utils.*
import com.watermelonman.weather.utils.viewbindingdelegate.viewBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragmentMVVM<HomeViewModel, FragmentHomeBinding>() {

    override val viewModel: HomeViewModel by viewModel()
    override val binding: FragmentHomeBinding by viewBinding()
    private val selectedLocationViewModel: SelectedLocationViewModel by sharedViewModel()

    private lateinit var hourlySectionAnimation: Animation
    private lateinit var dailySectionAnimation: Animation
    private lateinit var detailsSectionAnimation: Animation
    private lateinit var hourlyForecastAdapter: HourlyForecastAdapter
    private lateinit var dailyForecastAdapter: DailyForecastAdapter

    override fun initView(savedInstanceState: Bundle?) {
        initAnimations()
        initSwipeRefreshLayout()
    }

    override fun observes() {
        collectWhenStarted(viewModel.time) {
            binding.txtTimeNowInLocation.text = it
        }
        collectWhenStarted(selectedLocationViewModel.actionUpdateAllForecastData) {
            viewModel.getAllWeatherDataOfSelectedLocation()
        }
        collectWhenStarted(viewModel.weatherForecast, ::handleForecastState)
    }

    override fun navigateUp() {
        viewModel.finishApp()
    }

    override fun onResume() {
        super.onResume()
        viewModel.enableTimeUpdates()
    }

    override fun onStop() {
        super.onStop()
        viewModel.disableTimeUpdates()
    }

    private fun handleForecastState(state: State<AllForecastData>) {
        if (selectedLocationViewModel.isAlreadyFetchingData) {
            selectedLocationViewModel.isAlreadyFetchingData = false
            return
        }
        state.precessState({
            onForecastSuccess(it)
        }, ::onForecastLoading) { onForecastError() }
    }

    private fun onForecastLoading() {
        showLoading(true)
        showErrorWindow(false)
    }

    private fun onForecastSuccess(data: AllForecastData) {
        showLoading(false)
        initCurrentForecastData(data.current)
        initHourlyForecastData(data.hourly)
        initDailyForecastData(data.daily)
        startAnimationsOnLayoutDrawn()
    }

    private fun onForecastError() {
        showLoading(false)
        showErrorWindow(true)
    }

    private fun initCurrentForecastData(forecast: CurrentForecastData) {
        with(binding) {
            txtLocation.text = forecast.locationName
            txtTemperature.text = forecast.temp.toString()
            txtDescription.text = forecast.description
            txtHumidity.text = getString(R.string.humidity_percent, forecast.humidity)
            txtRealFeel.text = getString(R.string.temp_degrees_celsius, forecast.tempFeelsLike)
            txtPressure.text = getString(R.string.pressure_h_pa, forecast.pressure)
            txtWind.text = getString(R.string.wind_speed, forecast.windSpeed)
            imgWindDirection.rotation = forecast.windDirection
            txtTimeNowInXLocation.text = getString(R.string.time_now_in_x_location, forecast.locationName)
            imgManageLocations.setOnClickListener { viewModel.openManageLocationsBottomSheet() }
        }
    }

    private fun initHourlyForecastData(hourlyData: List<HourlyData>) {
        initHourlyForecastAdapter(hourlyData)
        with(binding) {
            rvNext24Hours.adapter = hourlyForecastAdapter
            rvNext24Hours.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun initDailyForecastData(dailyData: List<DailyData>) {
        initDailyForecastAdapter(dailyData)
        with(binding) {
            rvNext7Days.adapter = dailyForecastAdapter
            rvNext7Days.layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initHourlyForecastAdapter(hourlyData: List<HourlyData>) {
        hourlyForecastAdapter = HourlyForecastAdapter().apply {
            updateForecast(hourlyData)
        }
    }

    private fun initDailyForecastAdapter(dailyData: List<DailyData>) {
        dailyForecastAdapter = DailyForecastAdapter().apply {
            updateForecast(dailyData)
            setOnViewForecastClickListener {
                viewModel.openSevenDayForecastBottomSheet(dailyData)
            }
        }
    }

    private fun showLoading(shouldShow: Boolean) {
        if (shouldShow) {
            binding.vLoading.fadeInIfInGone()
        } else {
            binding.vLoading.fadeOutIfVisible()
        }
    }

    private fun showErrorWindow(shouldShow: Boolean) {
        if (shouldShow) {
            binding.lAnErrorOccurred.fadeInIfInGone()
        }else {
            binding.lAnErrorOccurred.fadeOutIfVisible()
        }
    }

    private fun startAnimationsOnLayoutDrawn() {
        binding.lMainContainer.post {
            binding.lDataStatusMask.fadeOutIfVisible()
            startAnimations()
        }
    }

    private fun startAnimations() {
        if (!viewModel.shouldStartAnimations) return
        viewModel.shouldStartAnimations = false
        with(binding) {
            lNext24HoursContainer.startAnimation(hourlySectionAnimation)
            txtNext24Hours.startAnimation(hourlySectionAnimation)
            lNext7DaysContainer.startAnimation(dailySectionAnimation)
            txtNext7Days.startAnimation(dailySectionAnimation)
            lAdditionalInfoContainer.startAnimation(detailsSectionAnimation)
            txtAdditionalInfo.startAnimation(detailsSectionAnimation)
        }
    }

    private fun initAnimations() {
        hourlySectionAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_down_fade_in)
        dailySectionAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_down_fade_in)
            .apply { startOffset = SHORT_ANIM_DURATION }
        detailsSectionAnimation = AnimationUtils.loadAnimation(context, R.anim.slide_down_fade_in)
            .apply { startOffset = SHORT_ANIM_DURATION * 2 }
    }

    private fun initSwipeRefreshLayout() {
        binding.root.setOnRefreshListener {
            binding.lDataStatusMask.isVisible = true
            viewModel.shouldStartAnimations = true
            viewModel.getAllWeatherDataOfSelectedLocation()
            binding.root.isRefreshing = false
        }
    }
}