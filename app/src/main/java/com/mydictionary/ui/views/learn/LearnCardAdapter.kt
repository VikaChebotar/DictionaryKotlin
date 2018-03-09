package com.mydictionary.ui.views.learn

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import com.mydictionary.R
import com.mydictionary.commons.inflate
import com.mydictionary.data.pojo.WordDetails


/**
 * Created by Viktoria_Chebotar on 3/9/2018.
 */
const val CAMERA_DISTANCE = 5

class LearnCardAdapter(val activity: Activity) : RecyclerView.Adapter<LearnCardAdapter.WordCardHolder>() {
    private val wordsList = mutableListOf<WordDetails>()
    private val screenWidth by lazy {
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        metrics.widthPixels
    }

    fun setTickets(wordsList: List<WordDetails>) {
        this.wordsList.clear()
        this.wordsList.addAll(wordsList)
        notifyDataSetChanged()
    }

    fun addTickets(wordsList: List<WordDetails>) {
        this.wordsList.addAll(wordsList)
        notifyDataSetChanged()
    }

    fun clearTickets() {
        this.wordsList.clear()
        notifyDataSetChanged()
    }

    fun getWordsList(): List<WordDetails> {
        return wordsList
    }

    override fun onBindViewHolder(holder: WordCardHolder?, position: Int) {
        holder?.bindView(wordsList[position], position)
    }

    override fun onBindViewHolder(holder: WordCardHolder?, position: Int, payloads: MutableList<Any>?) {
        if (payloads == null || payloads.isEmpty()) {
            val word = wordsList.get(position)
            holder?.bindView(word, position)
        }
    }

    override fun onViewRecycled(holder: WordCardHolder) {
        super.onViewRecycled(holder)
        holder.isShowingFrontSide = true
        holder.itemView.scaleX = 1f
        holder.itemView.rotationY = 0f
    }

    override fun getItemCount() = wordsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordCardHolder {
        val view = parent.inflate(R.layout.learn_card_item)
        view.setCameraDistance(view.getCameraDistance() * CAMERA_DISTANCE)
        val params = view.getLayoutParams() as RecyclerView.LayoutParams
        params.width = (screenWidth * 0.7).toInt()
        view.layoutParams = params
        return WordCardHolder(view)
    }

    inner class WordCardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var isShowingFrontSide = true

        fun bindView(wordDetails: WordDetails, position: Int) {
            if (isShowingFrontSide) {
                bindFrontSide(wordDetails, position)
            } else {
                bindBackSide(wordDetails, position)
            }
            itemView.scaleX = (if (isShowingFrontSide) 1 else -1).toFloat()
            itemView.setOnClickListener { rotateTicket(wordDetails, position) }
        }

        fun bindFrontSide(wordDetails: WordDetails, position: Int) {

        }

        fun bindBackSide(wordDetails: WordDetails, position: Int) {

        }

        protected fun rotateTicket(wordDetails: WordDetails, position: Int) {
            val layoutParams = itemView.layoutParams
            layoutParams.width = itemView.width
            layoutParams.height = itemView.height
            itemView.layoutParams = layoutParams
            notifyItemChanged(position, LearnCardItemAnimator.WordItemHolderInfo(wordDetails, position))
        }
    }
}