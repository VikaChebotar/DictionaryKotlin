package com.mydictionary.ui.views.learn

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mydictionary.R
import com.mydictionary.commons.Constants.Companion.SELCTED_WORD_INFO_EXTRA
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.ui.views.word.MeaningSpaceItemDecorator
import com.mydictionary.ui.views.word.MeaningsAdapter
import kotlinx.android.synthetic.main.learn_card_item.*


/**
 * Created by Viktoria_Chebotar on 3/9/2018.
 */
private const val ROTATION_FRONT = 0f
private const val ROTATION_BACK = 180f
private const val ANIMATION_DURATION: Long = 200

class LearnCardItemFragment : Fragment() {
    private val meaningItemDecoration by lazy {
        MeaningSpaceItemDecorator(context?.resources?.getDimension(R.dimen.meanings_space_decorator)?.toInt() ?: 0,
                context?.resources?.getDimension(R.dimen.meanings_space_decorator_big)?.toInt() ?: 0)
    }
    private var isShowingFrontSide = true
    private var animatorSet: AnimatorSet? = null
    private var wordDetails: WordDetails? = null
    private var meaningsList: List<Any>? = null


    companion object {
        fun getInstance(details: WordDetails): LearnCardItemFragment {
            val fragment = LearnCardItemFragment()
            val extras = Bundle()
            extras.putParcelable(SELCTED_WORD_INFO_EXTRA, details)
            fragment.arguments = extras
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wordDetails = arguments?.getParcelable(SELCTED_WORD_INFO_EXTRA)
        meaningsList = wordDetails?.meanings?.filter { it.isFavourite }?.map { it.definitions.union(it.examples) }?.
                reduce { acc, set -> acc.union(set) }?.toList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.learn_card_item, container, false)
        view.setOnClickListener {
            rotate()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showFrontSide()
    }

    private fun rotate() {
        if (animatorSet?.isRunning == true) {
            animatorSet?.end()
        }
        val fromRotationY = if (isShowingFrontSide) ROTATION_FRONT else ROTATION_BACK
        val toRotationY = if (isShowingFrontSide) ROTATION_BACK else ROTATION_FRONT
        val middleRotationY = Math.abs(toRotationY - fromRotationY) / 2
        view?.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        val firstAnimator = ObjectAnimator.ofFloat(view, View.ROTATION_Y, fromRotationY, middleRotationY)
        val secondAnimator = ObjectAnimator.ofFloat(view, View.ROTATION_Y, middleRotationY, toRotationY)
        firstAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                isShowingFrontSide = !isShowingFrontSide
                frontSide.visibility = if (isShowingFrontSide) View.VISIBLE else View.GONE
                backSide.visibility = if (isShowingFrontSide) View.GONE else View.VISIBLE
                view?.scaleX = if (isShowingFrontSide) 1f else -1f
                if (isShowingFrontSide) showFrontSide() else showBackSide()
            }
        })
        secondAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                view?.setLayerType(View.LAYER_TYPE_NONE, null)
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

    private fun showFrontSide() {
        favWordName.text = wordDetails?.word
    }

    private fun showBackSide() {
        favMeaningsRecyclerView.layoutManager = LinearLayoutManager(context)
        favMeaningsRecyclerView.adapter = MeaningsAdapter(meaningsList ?: emptyList(), true)
        favMeaningsRecyclerView.removeItemDecoration(meaningItemDecoration)
        favMeaningsRecyclerView.addItemDecoration(meaningItemDecoration)
        favMeaningsRecyclerView.setOnClickListener { rotate() }
    }
}