package com.watermelonman.weather.ui.activity.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.watermelonman.data.utils.ConnectionStateManager
import com.watermelonman.entities.enums.StartDestinations
import com.watermelonman.weather.R
import com.watermelonman.weather.ui.activity.viewmodel.MainActivityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ConnectionStateManager.bindToActivity(this)
        setTheme(R.style.Theme_Weather)
        setContentView(R.layout.activity_main)
        setUpNavGraph(savedInstanceState)
    }

    private fun setUpNavGraph(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) return
        (supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment)
            .navController.apply {
                graph.startDestination = getStartDestination()
                graph = graph
        }
    }

    private fun getStartDestination(): Int {
        return when(viewModel.getStartDestination(StartDestinations.SELECT_LOCATION)) {
            StartDestinations.SELECT_LOCATION -> R.id.selectLocationFragment
            StartDestinations.HOME -> R.id.homeFragment
        }
    }
}