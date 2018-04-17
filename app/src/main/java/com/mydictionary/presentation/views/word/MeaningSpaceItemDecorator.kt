package com.mydictionary.presentation.views.word

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import android.view.View


/**
 * Created by Viktoria Chebotar on 25.02.18.
 */
class MeaningSpaceItemDecorator(val margin: Int, val bigMargin: Int? = null) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val viewType = parent.adapter.getItemViewType(position)
        val previousPosition = position - 1
        if (previousPosition > NO_POSITION) {
            val previousViewType = parent.adapter.getItemViewType(previousPosition)
            if (previousViewType != viewType && previousViewType == MeaningsAdapter.ViewType.EXAMPLE.ordinal) {
                outRect.set(0, bigMargin ?: margin, 0, 0)
                return
            } else if (previousViewType != viewType) {
                outRect.set(0, margin, 0, 0)
                return
            }
        }
        outRect.set(0, 0, 0, 0)
    }
}