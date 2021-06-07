package com.watermelonman.weather.utils.recyclerview

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ShowHideToTopButtonOnScrollListener(
    private val viewVisibilityManager: ViewVisibilityManager,
    private val layoutManager: LinearLayoutManager
): RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
        viewVisibilityManager.showHideViewBy { firstVisibleItemPosition != 0 }
    }

    fun attachTo(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(this)
    }
}