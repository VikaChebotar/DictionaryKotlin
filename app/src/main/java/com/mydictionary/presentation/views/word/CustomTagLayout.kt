package com.mydictionary.presentation.views.word

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.mydictionary.R

/**
 * Created by Viktoria Chebotar on 02.07.17.
 */
class CustomTagLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ViewGroup(context, attrs, defStyleAttr) {
    private var itemMargin: Int = 0

    init {
        itemMargin = context.resources.getDimensionPixelSize(R.dimen.very_small_margin)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var curWidth: Int
        var curHeight: Int
        var curLeft: Int
        var curTop: Int
        var maxHeight = 0

        //get the available size of child view
        val childLeft = this.paddingLeft
        val childTop = this.paddingTop
        val childRight = this.measuredWidth - this.paddingRight
        val childBottom = this.measuredHeight - this.paddingBottom
        val childWidth = childRight - childLeft
        val childHeight = childBottom - childTop

        curLeft = childLeft
        curTop = childTop

        for (i in 0 until childCount) {
            val child = getChildAt(i)

            if (child.visibility == View.GONE) return

            //Get the maximum size of the child
            child.measure(View.MeasureSpec.makeMeasureSpec(childWidth, View.MeasureSpec.AT_MOST),
                    View.MeasureSpec.makeMeasureSpec(childHeight, View.MeasureSpec.AT_MOST))
            curWidth = child.measuredWidth
            curHeight = child.measuredHeight
            //wrap is reach to the end
            if (curLeft + curWidth >= childRight) {
                curLeft = childLeft
                maxHeight = curHeight + itemMargin
                curTop += maxHeight
            }
            //do the layout
            child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight)
            //store the max height
            curLeft += curWidth + itemMargin
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Measurement will ultimately be computing these values.
        var maxHeight = 0
        val maxWidth = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        var childState = 0
        var mLeftWidth = 0
        var rowCount = 0

        // Iterate through all children, measuring them and computing our dimensions
        // from their size.
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility == View.GONE) continue

            // Measure the child.
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
            if (mLeftWidth + child.measuredWidth >= maxWidth) {
                rowCount++
                mLeftWidth = child.measuredWidth + itemMargin
                maxHeight += child.measuredHeight
            } else {
                mLeftWidth += child.measuredWidth + itemMargin
                maxHeight = Math.max(maxHeight, child.measuredHeight)
            }
            childState = View.combineMeasuredStates(childState, child.measuredState)
        }

        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, suggestedMinimumHeight) + itemMargin * rowCount + itemMargin/2

        // Report our final dimensions.
        setMeasuredDimension(View.resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                View.resolveSizeAndState(maxHeight, heightMeasureSpec, childState shl View.MEASURED_HEIGHT_STATE_SHIFT))
    }
}
