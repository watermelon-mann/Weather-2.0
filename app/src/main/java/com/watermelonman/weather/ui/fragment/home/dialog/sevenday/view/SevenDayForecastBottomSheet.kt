package com.watermelonman.weather.ui.fragment.home.dialog.sevenday.view

import android.os.Bundle
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.watermelonman.weather.appbase.BaseBottomSheetFragment
import com.watermelonman.weather.databinding.BottomSheetSevenDayForecastBinding
import com.watermelonman.weather.ui.fragment.home.dialog.sevenday.utils.adapter.SevenDayForecastAdapter
import com.watermelonman.weather.ui.fragment.home.dialog.sevenday.viewmodel.SevenDayForecastViewModel
import com.watermelonman.weather.utils.makeFullScreen
import com.watermelonman.weather.utils.viewbindingdelegate.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SevenDayForecastBottomSheet:
    BaseBottomSheetFragment<SevenDayForecastViewModel, BottomSheetSevenDayForecastBinding>() {

    override val viewModel: SevenDayForecastViewModel by viewModel()
    override val binding: BottomSheetSevenDayForecastBinding by viewBinding()
    private val args: SevenDayForecastBottomSheetArgs by navArgs()

    override fun initView(savedInstanceState: Bundle?) {
        val adapter = SevenDayForecastAdapter()
            .apply { updateForecast(args.sevenDayForecast.toList()) }
        with(binding) {
            rvSevenDayForecast.adapter = adapter
            rvSevenDayForecast.layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.makeFullScreen(binding.root, this, ViewGroup.LayoutParams.MATCH_PARENT)
    }

}