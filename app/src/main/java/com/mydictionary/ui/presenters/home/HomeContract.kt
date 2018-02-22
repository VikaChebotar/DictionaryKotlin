package com.mydictionary.ui.presenters.home

import com.mydictionary.data.pojo.WordInfo
import com.mydictionary.ui.presenters.BasePresenter
import com.mydictionary.ui.presenters.BaseView

/**
 * Created by Viktoria Chebotar on 18.06.17.
 */

interface HomePresenter : BasePresenter<HomeView> {
    fun onWordOfTheDayClicked()
}

interface HomeView : BaseView {
    fun showWordOfTheDay(word: WordInfo)
    fun startWordInfoActivity(word: WordInfo)
}

