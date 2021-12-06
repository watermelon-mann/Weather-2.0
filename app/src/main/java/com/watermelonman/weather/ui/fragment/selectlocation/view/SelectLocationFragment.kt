package com.watermelonman.weather.ui.fragment.selectlocation.view

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import com.google.android.material.snackbar.Snackbar
import com.watermelonman.entities.location.City
import com.watermelonman.entities.result.State
import com.watermelonman.weather.R
import com.watermelonman.weather.appbase.BaseFragmentMVVM
import com.watermelonman.weather.databinding.FragmentSelectLocationBinding
import com.watermelonman.weather.ui.fragment.selectlocation.dialog.LocationPermissionIsRequiredDialog
import com.watermelonman.weather.ui.fragment.selectlocation.dialog.LocationServiceUnavailableDialog
import com.watermelonman.weather.ui.fragment.selectlocation.utils.LaunchMode
import com.watermelonman.weather.ui.fragment.selectlocation.utils.adapter.CityNamesAdapter
import com.watermelonman.weather.ui.fragment.selectlocation.viewmodel.SelectLocationViewModel
import com.watermelonman.weather.ui.sharedviewmodels.SelectedLocationViewModel
import com.watermelonman.weather.utils.*
import com.watermelonman.weather.utils.recyclerview.ShowHideToTopButtonOnScrollListener
import com.watermelonman.weather.utils.recyclerview.ViewSlidingVisibilityManager
import com.watermelonman.weather.utils.recyclerview.ViewSlidingVisibilityManager.Gravity
import com.watermelonman.weather.utils.viewbindingdelegate.viewBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectLocationFragment: BaseFragmentMVVM<SelectLocationViewModel, FragmentSelectLocationBinding>() {

    override val viewModel: SelectLocationViewModel by viewModel()
    override val binding: FragmentSelectLocationBinding by viewBinding()
    private val selectedLocationViewModel: SelectedLocationViewModel by sharedViewModel()
    private val args: SelectLocationFragmentArgs by navArgs()

    private lateinit var citiesAdapter: CityNamesAdapter
    private lateinit var citiesLayoutManager: LinearLayoutManager
    private lateinit var toTopButtonOnScrollListener: ShowHideToTopButtonOnScrollListener
    private var locationPermissionLauncher: ActivityResultLauncher<String>? = null
    private var snackBar: Snackbar? = null

    override fun initView(savedInstanceState: Bundle?) {
        initPermissionRequestLauncher()
        initCitiesRecyclerView()
        with(binding) {
            btnYourLocation.setOnClickListener {
                selectLocationOfTheUser()
            }
            btnChooseOnMap.setOnClickListener {
                viewModel.openMap()
            }
        }
    }

    override fun observes() {
        collectWhenStarted(viewModel.allCityNames, ::handleCitiesResult)
        collectWhenStarted(viewModel.onLocationSelected) {
            if (args.launchMode == LaunchMode.INITIAL) {
                viewModel.goToHomePage()
            } else {
                selectedLocationViewModel.emitSavedLocationsUpdates()
            }
        }
        collectWhenStarted(viewModel.actionShowPermissionRequiredDialog) {
            showPermissionIsRequiredDialog()
        }
        collectWhenStarted(viewModel.actionShowLocationUnavailableDialog) {
            showLocationIsUnavailableDialog()
        }
        collectWhenStarted(viewModel.actionOpenAppSettings, ::startActivity)
        collectWhenStarted(viewModel.actionShowLocationRequestProgressBar) {
            showRequestingLocationProgressBar(it)
            shouldEnableUserInteractions(!it)
        }
        collectWhenStarted(viewModel.actionDismissSnackBars) {
            snackBar?.dismiss()
        }
        collectWhenStarted(selectedLocationViewModel.actionOnUpdateError) {
            showErrorSnackBar()
        }
        collectWhenStarted(selectedLocationViewModel.actionOnUpdateSuccess) {
            navigateUp()
        }
    }

    override fun navigateUp() {
        snackBar?.dismiss()
        when(args.launchMode) {
            LaunchMode.NOT_INITIAL -> super.navigateUp()
            else -> viewModel.finishApp()
        }
    }

    private fun showErrorSnackBar() {
        binding.root.showShortSnackBar(R.string.select_location_error_message)
    }

    private fun handleCitiesResult(state: State<List<City>>) {
        state.precessState(::onCitiesListSuccess, onError = { onCitiesListError() })
    }

    private fun onCitiesListSuccess(cities: List<City>) {
        citiesAdapter.setInitialList(cities)
    }

    private fun onCitiesListError() {
        snackBar = binding.root.showIndefiniteSnackBar(R.string.on_cities_load_error_message, R.string.try_again) {
            viewModel.getAllCityNames()
        }
    }

    private fun initPermissionRequestLauncher() {
        @SuppressLint("MissingPermission")//Just added for stupid compiler
        locationPermissionLauncher = permissionRequestLauncher { isGranted->
            if (isGranted) {
                viewModel.selectLocationOfUser()
            }else {
                val shouldShowPermissionRequiredDialog = !ActivityCompat
                    .shouldShowRequestPermissionRationale(requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                if(shouldShowPermissionRequiredDialog) {
                    viewModel.showPermissionIsRequiredDialog()
                }
            }
        }
    }

    private fun initCitiesRecyclerView() {
        initCitiesAdapter()
        initToTopButton()
        binding.rvCities.apply {
            adapter = citiesAdapter
            layoutManager = citiesLayoutManager
            toTopButtonOnScrollListener.attachTo(this)
        }
    }

    private fun initToTopButton() {
        val viewVisibilityManager = ViewSlidingVisibilityManager(Gravity.TOP, binding.imgToTop)
        viewVisibilityManager.setOnButtonClickListener { binding.rvCities.smoothScrollToPosition(0) }
        toTopButtonOnScrollListener = ShowHideToTopButtonOnScrollListener(viewVisibilityManager, citiesLayoutManager)
    }

    private fun initCitiesAdapter() {
        citiesAdapter = CityNamesAdapter().apply {
            setSearchBar(binding.edtSearchBar)
            setOnLocationClickListener(::selectCity)
        }
        citiesLayoutManager = LinearLayoutManager(context)
    }

    private fun selectLocationOfTheUser() {
        selectLocationOfUserWithPermissionRequest()
    }

    private fun selectCity(city: City) {
        binding.root.hideKeyboard()
        viewModel.selectCity(city)
    }
    
    private fun selectLocationOfUserWithPermissionRequest() {
        locationPermissionLauncher?.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun showLocationIsUnavailableDialog() {
        LocationServiceUnavailableDialog(requireContext()).show()
    }

    private fun showPermissionIsRequiredDialog() {
        LocationPermissionIsRequiredDialog(requireContext()).apply {
            setPositiveButtonClickListener { _, _ ->
                viewModel.openAppSettings(requireActivity().packageName)
            }
            show()
        }
    }

    private fun showRequestingLocationProgressBar(isVisible: Boolean) {
        TransitionManager.beginDelayedTransition(binding.root)
        binding.lRequestingLocationProgressBar.isVisible = isVisible
    }

    private fun shouldEnableUserInteractions(shouldEnable: Boolean) {
        with(binding) {
            citiesAdapter.enableItemClicks(shouldEnable)
            edtSearchBar.isEnabled = shouldEnable
            btnChooseOnMap.isEnabled = shouldEnable
            btnYourLocation.isEnabled = shouldEnable
        }
    }

}