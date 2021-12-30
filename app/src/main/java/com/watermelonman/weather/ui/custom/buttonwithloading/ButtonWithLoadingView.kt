package com.watermelonman.weather.ui.custom.buttonwithloading

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.annotation.DimenRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import com.watermelonman.weather.R

class ButtonWithLoadingView
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttrs: Int = R.attr.buttonWithLoadingStyle,
    defStyleRes: Int = R.style.ButtonWithLoadingStyle
) : FrameLayout(context, attrs, defStyleAttrs, defStyleRes) {

    private var progressBar: ProgressBar? = null
    private var icon: AppCompatImageView? = null

    private val state: ButtonWithLoadingState

    init {
        val ta = context.theme.obtainStyledAttributes(
            attrs, R.styleable.ButtonWithLoadingView, defStyleAttrs, defStyleRes
        )
        try {
            state = initAttributes(ta)
        } finally {
            ta.recycle()
        }
        initView()
    }

    fun showProgress(show: Boolean) {
        state.showProgress = show
        updateProgressBar()
    }

    private fun initAttributes(ta: TypedArray): ButtonWithLoadingState {
        return with(ta) {
            ButtonWithLoadingState(
                getDrawable(R.styleable.ButtonWithLoadingView_bwl_icon)
            )
        }
    }

    private fun initView() {
        isClickable = true
        isFocusable = true
        icon = AppCompatImageView(context).apply {
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).also { lp ->
                lp.setMargins(getDimenPx(R.dimen.dp_8))
            }
            setImageDrawable(state.icon)
            imageTintList = ColorStateList.valueOf(Color.WHITE)
        }
        progressBar = ProgressBar(context).apply {
            layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).also { lp ->
                lp.setMargins(getDimenPx(R.dimen.dp_8))
            }
            indeterminateTintList = ColorStateList.valueOf(Color.WHITE)
            visibility = GONE
        }
        addView(progressBar)
        addView(icon)
    }

    private fun updateProgressBar() {
        icon?.isVisible = !state.showProgress
        progressBar?.isVisible = state.showProgress
    }

    private fun getDimenPx(@DimenRes resId: Int): Int =
        context.resources.getDimensionPixelSize(resId)
}