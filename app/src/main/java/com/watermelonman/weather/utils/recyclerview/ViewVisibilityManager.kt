package com.watermelonman.weather.utils.recyclerview

import android.view.View
import android.view.ViewGroup
import androidx.transition.Transition
import androidx.transition.TransitionManager

abstract class ViewVisibilityManager(view: View, parent: ViewGroup? = null) {

    protected val root: ViewGroup = parent ?: (view.parent as? ViewGroup) ?: throw IllegalStateException(
        "Specified view don't have a parent. Consider to specify it manually"
    )

    abstract val transition: Transition
    protected abstract fun showButton()
    protected abstract fun hideButton()

    open fun showHideViewBy(predicate: () -> Boolean) {
        if (predicate()) {
            showButton()
        } else {
            hideButton()
        }
    }

    protected fun beginDelayedSlideTransition() {
        TransitionManager.beginDelayedTransition(root, transition)
    }
}