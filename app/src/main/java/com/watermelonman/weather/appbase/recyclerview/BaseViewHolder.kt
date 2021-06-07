package com.watermelonman.weather.appbase.recyclerview

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseViewHolder(private val binding: ViewBinding): RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(position: Int)
    fun getString(@StringRes resId: Int, vararg args: Any?): String {
        return binding.root.context.getString(resId, *args)
    }
    fun getDrawable(@DrawableRes resId: Int): Drawable? {
        return ContextCompat.getDrawable(binding.root.context, resId)
    }
}