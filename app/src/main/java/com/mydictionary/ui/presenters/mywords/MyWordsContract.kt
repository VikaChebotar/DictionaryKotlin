package com.mydictionary.ui.presenters.mywords

import com.mydictionary.ui.presenters.BasePresenter
import com.mydictionary.ui.presenters.BaseView

/**
 * Created by Viktoria Chebotar on 09.07.17.
 */
interface WordsPresenter : BasePresenter<WordsView> {
}

interface WordsView : BaseView {
    fun showWords(words: List<String>)
    fun startWordInfoActivity(word: String)
    fun getWordListName():String
}