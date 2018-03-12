package com.mydictionary.ui.views.learn

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mydictionary.R
import kotlinx.android.synthetic.main.learn_card_item.*


/**
 * Created by Viktoria_Chebotar on 3/9/2018.
 */
private const val ROTATION_FRONT = 0f
private const val ROTATION_BACK = 180f
private const val ANIMATION_DURATION: Long = 300

class LearnCardItemFragment : Fragment() {
    private var isShowingFrontSide = true
    private var animatorSet: AnimatorSet? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.learn_card_item, container, false)
        view.setOnClickListener {
            rotate(view)
        }
        return view
    }

    private fun rotate(view: View) {
        if (animatorSet?.isRunning == true) {
            animatorSet?.end()
        }
        val fromRotationY = if (isShowingFrontSide) ROTATION_FRONT else ROTATION_BACK
        val toRotationY = if (isShowingFrontSide) ROTATION_BACK else ROTATION_FRONT
        val middleRotationY = Math.abs(toRotationY - fromRotationY) / 2
        view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        val firstAnimator = ObjectAnimator.ofFloat(view, View.ROTATION_Y, fromRotationY, middleRotationY)
        val secondAnimator = ObjectAnimator.ofFloat(view, View.ROTATION_Y, middleRotationY, toRotationY)
        firstAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                isShowingFrontSide = !isShowingFrontSide
                frontSide.visibility = if (isShowingFrontSide) View.VISIBLE else View.GONE
                backSide.visibility = if (isShowingFrontSide) View.GONE else View.VISIBLE
                view.scaleX = if (isShowingFrontSide) 1f else -1f
            }
        })
        secondAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                view.setLayerType(View.LAYER_TYPE_NONE, null)
            }
        })
        animatorSet = AnimatorSet()
        animatorSet?.play(secondAnimator)?.after(firstAnimator)
        firstAnimator.duration = ANIMATION_DURATION
        secondAnimator.duration = ANIMATION_DURATION
        animatorSet?.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        animatorSet?.end()
    }
}