package com.mydictionary.ui.views.learn

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.support.v4.util.ArrayMap
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.view.View
import com.mydictionary.data.pojo.WordDetails


/**
 * Created by Viktoria_Chebotar on 3/9/2018.
 */
class LearnCardItemAnimator : DefaultItemAnimator() {
    private val animatorMap = ArrayMap<LearnCardAdapter.WordCardHolder, AnimatorSet>()

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun recordPreLayoutInformation(state: RecyclerView.State,
                                            viewHolder: RecyclerView.ViewHolder,
                                            changeFlags: Int, payloads: List<Any>): ItemHolderInfo {
        if (changeFlags == FLAG_CHANGED) {
            for (payload in payloads) {
                if (payload is WordItemHolderInfo) {
                    return payload
                }
            }
        }
        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads)
    }

    override fun animateChange(oldHolder: RecyclerView.ViewHolder, newHolder: RecyclerView.ViewHolder, preInfo: ItemHolderInfo, postInfo: ItemHolderInfo): Boolean {
        val wordHolder = newHolder as LearnCardAdapter.WordCardHolder
        if (!animatorMap.containsKey(wordHolder)) {
            val fromRotationY = wordHolder.itemView.getRotationY()
            val middleRotationY = fromRotationY - ROTATION_DEGREE
            val toRotationY = middleRotationY - ROTATION_DEGREE
            wordHolder.itemView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            val oldViewRotateAnimator = ObjectAnimator.ofFloat(wordHolder.itemView, View.ROTATION_Y, fromRotationY, middleRotationY)
            val newViewRotateAnimator = ObjectAnimator.ofFloat(wordHolder.itemView, View.ROTATION_Y, middleRotationY, toRotationY)
            oldViewRotateAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    val itemHolderInfo = preInfo as WordItemHolderInfo
                    if (wordHolder.isShowingFrontSide) {
                        wordHolder.isShowingFrontSide = false
                        wordHolder.bindBackSide(itemHolderInfo.word, itemHolderInfo.position)
                    } else {
                        wordHolder.isShowingFrontSide = true
                        wordHolder.bindFrontSide(itemHolderInfo.word, itemHolderInfo.position)
                    }
                }
            })
            newViewRotateAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    animatorMap.remove(wordHolder)
                    dispatchAnimationFinished(wordHolder)
                    wordHolder.itemView.setLayerType(View.LAYER_TYPE_NONE, null)
                }
            })
            val animatorSet = AnimatorSet()

            animatorSet.play(newViewRotateAnimator).after(oldViewRotateAnimator)
            oldViewRotateAnimator.setDuration(ANIMATION_DURATION.toLong())
            newViewRotateAnimator.setDuration(ANIMATION_DURATION.toLong())

            animatorSet.start()
            animatorMap.put(wordHolder, animatorSet)
        }
        return false
    }

    private fun cancelAnimation(viewHolder: RecyclerView.ViewHolder) {
        if (animatorMap.containsKey(viewHolder)) {
            val animators = animatorMap.get(viewHolder)?.childAnimations.orEmpty()
            for (animator in animators) {
                animator.end()
            }
        }
    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {
        super.endAnimation(item)
        cancelAnimation(item)
    }

    override fun endAnimations() {
        super.endAnimations()
        for (animator in animatorMap.values) {
            val childAnimators = animator.getChildAnimations()
            for (childAnimation in childAnimators) {
                childAnimation.end()
            }
        }
    }

    class WordItemHolderInfo(var word: WordDetails, position: Int) : ItemHolderInfo() {
        var position = -1

        init {
            this.position = position
        }
    }

    companion object {
        private val ROTATION_DEGREE = 90
        private val ANIMATION_DURATION = 300
    }
}