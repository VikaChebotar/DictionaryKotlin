package com.mydictionary.presentation.views.learn

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.view.menu.MenuBuilder
import android.support.v7.view.menu.MenuPopupHelper
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mydictionary.R
import com.mydictionary.presentation.utils.SELCTED_WORD_INFO_EXTRA
import com.mydictionary.presentation.viewmodel.learn.UserWordInfoPresentation
import com.mydictionary.presentation.views.word.MeaningSpaceItemDecorator
import com.mydictionary.presentation.views.word.MeaningsAdapter
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
    private var wordDetails: UserWordInfoPresentation? = null
    private var meaningsList: List<Any>? = null
    private var listener: OnCardItemListener? = null

    interface OnCardItemListener {
        fun onDetailsClicked(word: UserWordInfoPresentation)
        fun onDeleteClicked(word: UserWordInfoPresentation)
    }

    companion object {
        fun getInstance(details: UserWordInfoPresentation): LearnCardItemFragment {
            val fragment = LearnCardItemFragment()
            val extras = Bundle()
            extras.putParcelable(SELCTED_WORD_INFO_EXTRA, details)
            fragment.arguments = extras
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (activity is OnCardItemListener) {
            listener = activity as OnCardItemListener
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wordDetails = arguments?.getParcelable(SELCTED_WORD_INFO_EXTRA)
        meaningsList = wordDetails?.meanings?.map { it.definitions.union(it.examples) }?.
                reduce { acc, set -> acc.union(set) }?.toList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.learn_card_item, container, false)
        view.setOnClickListener { rotate() }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showFrontSide()
        setPopupMenuClickListener()
    }

    @SuppressLint("RestrictedApi")
    private fun setPopupMenuClickListener() {
        if (context == null) return
        val popup = PopupMenu(context!!, menuWordBtn)
        popup.menuInflater.inflate(R.menu.my_word_item_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.action_delete -> wordDetails?.let { listener?.onDeleteClicked(it) }
                else -> wordDetails?.let { listener?.onDetailsClicked(it) }
            }
            true
        }
        val menuHelper = MenuPopupHelper(context!!, popup.menu as MenuBuilder, menuWordBtn)
        menuHelper.setForceShowIcon(true)
        menuHelper.gravity = Gravity.END

        menuWordBtn.setOnClickListener {
            menuHelper.show()
        }
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