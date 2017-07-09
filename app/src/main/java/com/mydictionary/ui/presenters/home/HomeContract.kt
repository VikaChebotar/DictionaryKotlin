package com.mydictionary.ui.presenters.home

import com.mydictionary.data.entity.WordInfo
import com.mydictionary.ui.presenters.BasePresenter
import com.mydictionary.ui.presenters.BaseView

/**
 * Created by Viktoria Chebotar on 18.06.17.
 */

interface HomePresenter : BasePresenter<HomeView> {
    fun onWordOfTheDayClicked()
    fun onResume()
    fun onWordOfTheDayFavoriteBtnClicked()
    fun onMyWordsBtnClicked()
}

interface HomeView : BaseView {
    fun showWordOfTheDay(word: WordInfo)
    fun showWordOfTheDayFavoriteBtnState(isFavorite: Boolean)
    fun startWordInfoActivity(word: WordInfo)
    fun startMyWordsActivity()
}

