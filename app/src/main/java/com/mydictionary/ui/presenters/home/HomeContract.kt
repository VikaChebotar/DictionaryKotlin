package com.mydictionary.ui.presenters.home

import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.ui.presenters.BasePresenter
import com.mydictionary.ui.presenters.BaseView

/**
 * Created by Viktoria Chebotar on 18.06.17.
 */

interface HomePresenter : BasePresenter<HomeView> {
    fun onMyWordsBtnClicked()
}

interface HomeView : BaseView {
    fun startWordInfoActivity(word: WordDetails)
    fun startMyWordsActivity()
    fun showWordLists(list: List<WordListItem>)
}

