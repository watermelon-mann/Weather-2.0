package com.watermelonman.weather.ui.fragment.managelocations.utils.adapter

import androidx.recyclerview.widget.DiffUtil
import com.watermelonman.entities.location.LocationItem


class DiffUtil: DiffUtil.ItemCallback<LocationItem>() {
    override fun areItemsTheSame(oldItem: LocationItem, newItem: LocationItem): Boolean {
        return oldItem.geoNameId == newItem.geoNameId
    }

    override fun areContentsTheSame(oldItem: LocationItem, newItem: LocationItem): Boolean {
        return oldItem == newItem
    }
}