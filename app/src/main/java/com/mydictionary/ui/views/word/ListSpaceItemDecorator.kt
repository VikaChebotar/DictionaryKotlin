package com.mydictionary.ui.views.word

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Viktoria Chebotar on 25.02.18.
 */
class ListSpaceItemDecorator(val margin: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildLayoutPosition(view)
        if (position < state.itemCount) {
            outRect.set(margin, margin, margin, 0);
        } else {
            outRect.set(margin, margin, margin, 0);
        }
    }
}