package com.watermelonman.weather.utils.recyclerview

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.transition.Slide

class ViewSlidingVisibilityManager(
    gravity: Gravity,
    private val view: View,
    parent: ViewGroup? = null
): ViewVisibilityManager(view, parent) {

    override val transition = Slide().apply {
        slideEdge = gravity.slideEdge
    }

    fun setOnButtonClickListener(listener: () -> Unit) {
        view.setOnClickListener { listener() }
    }

    override fun showButton() {
        if (view.isVisible) return
        beginDelayedSlideTransition()
        view.isVisible = true
    }

    override fun hideButton() {
        if (!view.isVisible) return
        beginDelayedSlideTransition()
        view.isVisible = false
    }

    enum class Gravity(val slideEdge: Int) {
        TOP(android.view.Gravity.TOP),
        BOTTOM(android.view.Gravity.BOTTOM),
        START(android.view.Gravity.START),
        END(android.view.Gravity.END)
    }
}