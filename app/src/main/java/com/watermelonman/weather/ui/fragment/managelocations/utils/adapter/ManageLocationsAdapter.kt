
package com.watermelonman.weather.ui.fragment.managelocations.utils.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.watermelonman.entities.location.Location
import com.watermelonman.entities.location.LocationItem
import com.watermelonman.entities.utils.toLocation
import com.watermelonman.weather.R
import com.watermelonman.weather.appbase.recyclerview.BaseViewHolder
import com.watermelonman.weather.databinding.RvItemSavedLocationBinding
import com.watermelonman.weather.utils.SHORT_ANIM_DURATION
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ManageLocationsAdapter: ListAdapter<LocationItem, BaseViewHolder>(DiffUtil()) {

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private var onItemClickListener: (Location) -> Unit = {}
    private var onLongItemClickListener: (SelectionMode) -> Unit = {}
    var selectionMode = SelectionMode.NOT_SELECTING

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return ViewHolder(
            RvItemSavedLocationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(position)
    }

    fun setOnItemClickListener(listener: (Location) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnLongItemClickListener(listener: (SelectionMode) -> Unit) {
        onLongItemClickListener = listener
    }

    fun selectAll() {
        currentList.forEach {
            it.isChecked = true
        }
        notifyDataSetChanged()
    }

    fun deSelectAll() {
        currentList.forEach {
            it.isChecked = false
        }
        notifyDataSetChanged()
    }

    fun areAllItemsSelected(): Boolean {
        currentList.forEach {
            if (!it.isChecked) return false
        }
        return true
    }

    fun getSelectedItems(): List<LocationItem> {
        return currentList.filter { it.isChecked }
    }

    inner class ViewHolder(private val binding: RvItemSavedLocationBinding): BaseViewHolder(binding) {
        override fun bind(position: Int) {
            val item = getItem(position)
            with(binding) {
                txtLocation.text = item.name
                setCheckBoxCheckedState(item.isChecked)
                root.background =
                    if (item.isSelected) getDrawable(R.drawable.bg_rect_radius_12_main_blue_secondary_with_ripple)
                    else getDrawable(R.drawable.bg_rect_radius_12_main_blue_with_ripple)
                root.setOnLongClickListener {
                    val itemOfSelectedPosition = getItem(bindingAdapterPosition)
                    toggleCheckBoxCheckedState(itemOfSelectedPosition)
                    onLongItemClickListener(getSelectionMode(currentList))
                    true
                }
                root.setOnClickListener {
                    when(selectionMode) {
                        SelectionMode.NOT_SELECTING -> onItemClickListener(item.toLocation())
                        SelectionMode.SELECTING -> root.performLongClick()
                    }
                }
            }
        }

        private fun checkItem(item: LocationItem) {
            item.isChecked = true
            binding.checkboxIsSelected.visibility = View.VISIBLE
            binding.checkboxIsSelected.isChecked = item.isChecked
        }

        private fun unCheckItem(item: LocationItem) {
            item.isChecked = false
            binding.checkboxIsSelected.isChecked = item.isChecked
            CoroutineScope(Dispatchers.Main).launch {
                delay(SHORT_ANIM_DURATION / 2)
                binding.checkboxIsSelected.visibility = View.INVISIBLE
            }
        }

        private fun toggleCheckBoxCheckedState(item: LocationItem) {
            with(binding) {
                if (!checkboxIsSelected.isChecked) {
                    checkItem(item)
                } else {
                    unCheckItem(item)
                }
            }
        }

        private fun getSelectionMode(currentList: List<LocationItem>): SelectionMode {
            val isAnyItemChecked = currentList.map { it.isChecked }.contains(true)
            return if (isAnyItemChecked) SelectionMode.SELECTING
            else SelectionMode.NOT_SELECTING
        }

        private fun setCheckBoxCheckedState(isChecked: Boolean) {
            with(binding.checkboxIsSelected) {
                visibility = if (isChecked) View.VISIBLE else View.INVISIBLE
                this.isChecked = isChecked
            }
        }
    }
}
