package com.watermelonman.weather.ui.fragment.map.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.snackbar.Snackbar
import com.watermelonman.entities.result.State
import com.watermelonman.weather.R
import com.watermelonman.weather.appbase.BaseFragmentMVVM
import com.watermelonman.weather.databinding.FragmentMapBinding
import com.watermelonman.weather.ui.fragment.map.viewmodel.MapViewModel
import com.watermelonman.weather.ui.fragment.selectlocation.dialog.LocationPermissionIsRequiredDialog
import com.watermelonman.weather.ui.sharedviewmodels.SelectedLocationViewModel
import com.watermelonman.weather.utils.*
import com.watermelonman.weather.utils.maps.MapLifecycleObserver
import com.watermelonman.weather.utils.viewbindingdelegate.viewBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapFragment: BaseFragmentMVVM<MapViewModel, FragmentMapBinding>() {

    override val viewModel: MapViewModel by viewModel()
    override val binding: FragmentMapBinding by viewBinding()
    private val selectedLocationViewModel: SelectedLocationViewModel by sharedViewModel()

    private lateinit var map: GoogleMap
    private lateinit var placesClient: PlacesClient
    private var locationPermissionLauncher: ActivityResultLauncher<String>? = null
    private var marker: Marker? = null
    private var snackBar: Snackbar? = null

    override fun initView(savedInstanceState: Bundle?) {
        initLocationPermissionLauncher()
        with(binding.mapView) {
            onCreate(savedInstanceState)
            MapLifecycleObserver.bindTo(viewLifecycleOwner, this)
            getMapAsync(onMapReadyCallback)
            placesClient = Places.createClient(requireContext())
        }
        with(binding) {
            imgDone.setOnClickListener {
                viewModel.selectMarkedLocation()
            }
            imgLocateUser.setOnClickListener {
                snackBar?.dismiss()
                locateUser()
            }
        }
    }

    override fun observes() {
        collectWhenStarted(viewModel.onDone) {
            viewModel.navigateUpToManageLocations()
            selectedLocationViewModel.notifyOnSuccessObservers()
        }
        collectWhenStarted(viewModel.userLocationLoading, ::shouldShowLoading)
        collectWhenStarted(viewModel.actionShowPermissionRequiredDialog) {
            showPermissionIsRequiredDialog()
        }
        collectWhenStarted(viewModel.actionOpenAppSettings, ::startActivity)
    }

    override fun navigateUp() {
        super.navigateUp()
        snackBar?.dismiss()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    @SuppressLint("MissingPermission")//Again, for stupid compiler.
    private fun initLocationPermissionLauncher() {
        locationPermissionLauncher = permissionRequestLauncher { isGranted->
            if (isGranted) {
                viewModel.locateUser()
            }else {
                binding.vLoading.fadeOutIfVisible()
                val shouldShowPermissionRequiredDialog = !ActivityCompat
                    .shouldShowRequestPermissionRationale(requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                if(shouldShowPermissionRequiredDialog) {
                    viewModel.showPermissionIsRequiredDialog()
                }
            }
        }
    }

    private val onMapReadyCallback: (GoogleMap) -> Unit = { googleMap->
        map = googleMap
        collectWhenStarted(viewModel.selectedLocation, ::handleSelectedLocationState)
        initSearchBar()
        map.setOnMapClickListener { latLng ->
            marker?.remove()
            marker = map.addMarker(simpleMarkerPosition(latLng))
            viewModel.updateSelectedLocationOnMap(latLng)
        }
    }

    private fun locateUser() {
        locationPermissionLauncher?.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun initSearchBar() {
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocompleteFragment)
                as AutocompleteSupportFragment
        autocompleteFragment.setPlaceFields(listOf(Place.Field.LAT_LNG))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                viewModel.selectLocation(place.latLng!!)
            }

            override fun onError(status: Status) {
                if (status.isCanceled) return
                binding.root.showShortSnackBar(R.string.on_place_error_message)
            }
        })
    }

    private fun handleSelectedLocationState(state: State<LatLng>) {
        state.precessState(::onLocationStateSuccess, onError = { onLocationStateError() })
    }

    private fun onLocationStateSuccess(latLng: LatLng) {
        marker?.remove()
        marker = map.addMarker(simpleMarkerPosition(latLng))
        map.moveCamera(cameraPosition(latLng))
    }


    private fun onLocationStateError() {
        snackBar = binding.root.showIndefiniteSnackBar(R.string.on_locate_error_message, R.string.try_again) {
            locateUser()
        }
    }

    private fun shouldShowLoading(shouldShow: Boolean) {
        if (shouldShow) {
            binding.vLoading.fadeInIfInGone()
        } else {
            binding.vLoading.fadeOutIfVisible()
        }
    }

    private fun showPermissionIsRequiredDialog() {
        LocationPermissionIsRequiredDialog(requireContext()).apply {
            setPositiveButtonClickListener { _, _ ->
                viewModel.openAppSettings(requireActivity().packageName)
            }
            show()
        }
    }
}