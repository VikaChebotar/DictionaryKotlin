package com.mydictionary.ui.presenters.search

import com.mydictionary.ui.presenters.BasePresenter
import com.mydictionary.ui.presenters.BaseView

/**
 * Created by Viktoria Chebotar on 25.06.17.
 */
interface SearchPresenter : BasePresenter<SearchView> {

}

interface SearchView : BaseView {
    fun showHistoryWords(list: List<String>)
    fun showSearchResult(list: List<String>)
    fun finishView()
}