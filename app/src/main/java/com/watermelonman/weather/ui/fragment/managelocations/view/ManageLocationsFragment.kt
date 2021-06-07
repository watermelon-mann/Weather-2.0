package com.watermelonman.weather.ui.fragment.managelocations.view

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.watermelonman.entities.location.LocationItem
import com.watermelonman.entities.result.State
import com.watermelonman.weather.appbase.BaseFragmentMVVM
import com.watermelonman.weather.databinding.FragmentManageLocationsBinding
import com.watermelonman.weather.ui.fragment.managelocations.utils.adapter.ManageLocationsAdapter
import com.watermelonman.weather.ui.fragment.managelocations.utils.adapter.SelectionMode
import com.watermelonman.weather.ui.fragment.managelocations.viewmodel.ManageLocationsViewModel
import com.watermelonman.weather.ui.fragment.selectlocation.utils.LaunchMode
import com.watermelonman.weather.ui.sharedviewmodels.SelectedLocationViewModel
import com.watermelonman.weather.utils.*
import com.watermelonman.weather.utils.viewbindingdelegate.viewBinding
import kotlinx.coroutines.delay
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ManageLocationsFragment: BaseFragmentMVVM<ManageLocationsViewModel, FragmentManageLocationsBinding>() {

    override val viewModel: ManageLocationsViewModel by viewModel()
    override val binding: FragmentManageLocationsBinding by viewBinding()
    private val selectedLocationViewModel: SelectedLocationViewModel by sharedViewModel()

    private lateinit var manageLocationsAdapter: ManageLocationsAdapter

    override fun initView(savedInstanceState: Bundle?) {
        initSavedLocationsRecyclerView()
        initSelectionMode()
        with(binding) {
            fabAddLocation.setOnClickListener {
                viewModel.goToSelectLocationFragment()
            }
        }
    }

    override fun observes() {
        collectWhenStarted(viewModel.savedLocations, ::handleSavedLocationsState)
        collectWhenStarted(selectedLocationViewModel.actionUpdateSavedLocations) {
            viewModel.getSavedLocations()
        }
        collectWhenStarted(viewModel.onLocationSelected) {
            selectedLocationViewModel.updateAllForecastData()
            navigateUp()
        }
        collectWhenStarted(viewModel.selectionMode,::setSelectionMode)
        collectWhenStarted(viewModel.actionSelectedLocationChanged) {
            selectedLocationViewModel.updateAllForecastData()
        }
    }

    private fun handleSavedLocationsState(state: State<List<LocationItem>>) {
        state.precessState(onSuccess = ::updateSavedLocations)
    }

    private fun updateSavedLocations(savedLocations: List<LocationItem>) {
        manageLocationsAdapter.submitList(savedLocations) {
            viewModel.configureSelectionMode()
        }
    }

    private fun initSavedLocationsRecyclerView() {
        manageLocationsAdapter = ManageLocationsAdapter().apply {
            setOnItemClickListener(viewModel::selectLocation)
            setOnLongItemClickListener(viewModel::setSelectionMode)
        }
        with(binding) {
            rvSavedLocations.adapter = manageLocationsAdapter
            rvSavedLocations.layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initSelectionMode() {
        with(binding) {
            imgSelectAll.setOnClickListener {
                manageLocationsAdapter.selectAll()
            }
            imgCancel.setOnClickListener {
                manageLocationsAdapter.deSelectAll()
                viewModel.setSelectionMode(SelectionMode.NOT_SELECTING)
            }
            imgDelete.setOnClickListener {
                deleteSelectedItems()
            }
        }
    }

    private fun deleteSelectedItems() {
        val selectedItems = manageLocationsAdapter.getSelectedItems()
        val areAllItemsSelected = manageLocationsAdapter.areAllItemsSelected()
        viewModel.deleteItems(selectedItems)
        if (areAllItemsSelected) {
            viewModel.resetStartDestination()
            viewModel.goToSelectLocationFragment(LaunchMode.INITIAL)
        }
    }

    private fun setSelectionMode(selectionMode: SelectionMode) {
        manageLocationsAdapter.selectionMode  = selectionMode
        with(binding.groupSelectionModeViews) {
            when(selectionMode) {
                SelectionMode.SELECTING -> fadeInIfInGone()
                SelectionMode.NOT_SELECTING -> fadeOutIfVisible()
            }
        }
    }
}