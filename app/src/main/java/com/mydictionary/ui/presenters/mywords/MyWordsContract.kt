package com.mydictionary.ui.presenters.mywords

import com.mydictionary.data.entity.HistoryWord
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.ui.presenters.BasePresenter
import com.mydictionary.ui.presenters.BaseView

/**
 * Created by Viktoria Chebotar on 09.07.17.
 */
interface MyWordsPresenter : BasePresenter<MyWordsView> {
    fun onFavoriteBtnClicked(position: Int, isFavorite: Boolean)
    fun onRemoveWord(position: Int)
    fun onWordClicked(position: Int)
}

interface MyWordsView : BaseView {
    fun showWords(words: List<HistoryWord>, isFavorite: Boolean)
    fun startWordInfoActivity(word: WordDetails)
}