package com.mydictionary.ui.views.home

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.mydictionary.R
import com.mydictionary.data.entity.WordInfo
import kotlinx.android.synthetic.main.random_word_card.view.*


/**
 * Created by Viktoria Chebotar on 18.06.17.
 */

class WordOfTheDayCard : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.random_word_card, this)
    }

    fun bind(wordInfo: WordInfo) {
        wordOfTheDay.setText(wordInfo.word)

        wordPronounce.visibility = if (wordInfo.pronunciation.isNullOrEmpty()) View.GONE else View.VISIBLE
        wordPronounce.text = context.getString(R.string.prononcuation, wordInfo.pronunciation ?: "")

        definition.text = wordInfo.definitions.getOrNull(0)?.definition ?: ""

        partOfSpeech.text = wordInfo.definitions.getOrNull(0)?.partOfSpeech ?: ""

        example.text = wordInfo.examples.getOrNull(0) ?: ""
        example.visibility = if (example.text.isEmpty()) View.GONE else View.VISIBLE

//        speakWord.setOnClickListener { TODO("not implemented")}
//        likeWord.setOnClickListener { TODO("not implemented") }
    }

}
