package com.watermelonman.weather.ui.fragment.selectlocation.utils.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.watermelonman.entities.location.City
import com.watermelonman.weather.appbase.recyclerview.BaseViewHolder
import com.watermelonman.weather.databinding.RvItemLocationBinding
import kotlinx.coroutines.*


class CityNamesAdapter: RecyclerView.Adapter<BaseViewHolder>() {

    init {
        stateRestorationPolicy = StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private val initialList: MutableList<City> = ArrayList()
    private val filteredList: MutableList<City> = ArrayList()
    private var searchJob: Job? = null
    private var isItemClicksEnabled = true

    private var onLocationClickListener: (city: City) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return LocationViewHolder(
            RvItemLocationBinding.inflate(inflater, parent, false)
        )
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    fun enableItemClicks(isEnable: Boolean) {
        isItemClicksEnabled = isEnable
    }

    fun setOnLocationClickListener(listener: (city: City) -> Unit) {
        onLocationClickListener = listener
    }

    fun setSearchBar(editText: AppCompatEditText) {
        editText.doAfterTextChanged {
            searchJob?.cancel()
            searchJob = CoroutineScope(Dispatchers.Default).launch {
                val text: String = it?.toString() ?: ""
                val filteredList = filterBy(text)
                withContext(Dispatchers.Main) {
                    updateItems(filteredList)
                }
            }
        }
    }

    fun setInitialList(list: Collection<City>) {
        initialList.clear()
        initialList.addAll(list)
        updateItems(list)
    }

    private fun updateItems(items: Collection<City>) {
        filteredList.apply {
            clear()
            addAll(items)
        }
        notifyDataSetChanged()
    }

    private fun filterBy(text: String): List<City> {
        if (text.isBlank() || text.isEmpty()) return initialList
        return initialList.filter {
            (it.name?.contains(text, true) == true
            || (it.country?.contains(text, true)) == true)
        }
    }

    inner class LocationViewHolder(private val binding: RvItemLocationBinding): BaseViewHolder(binding) {
        override fun bind(position: Int) {
            val item = filteredList[position]
            with(binding) {
                txtName.text = item.name
                val country = "${item.country},"
                txtCountry.text = country
                root.setOnClickListener {
                    if (!isItemClicksEnabled) return@setOnClickListener
                    onLocationClickListener(item)
                }
            }
        }
    }
}