package com.mydictionary.presentation.views

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Viktoria_Chebotar on 3/7/2018.
 */
class SpaceItemDecorator(val margin: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        outRect.set(margin, margin, margin, if (position < state.itemCount - 1) 0 else margin);
    }
}