package com.mydictionary.ui.views.learn

import android.content.Context
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent


/**
 * Recyclerview that handles clicks (not only on item, but also in spaces between them) and is scrollable at the same time
 */
class ClickableRecyclerView : RecyclerView {
    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            performClick()
            return true
        }
    }
    private val mDetector: GestureDetectorCompat = GestureDetectorCompat(context, gestureListener)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr)

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        return if (!mDetector.onTouchEvent(e)) {
            super.onTouchEvent(e)
        } else true
    }
}