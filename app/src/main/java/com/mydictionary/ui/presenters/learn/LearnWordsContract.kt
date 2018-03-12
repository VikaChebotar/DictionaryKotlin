package com.mydictionary.ui.presenters.learn

import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.ui.presenters.BasePresenter
import com.mydictionary.ui.presenters.BaseView

/**
 * Created by Viktoria_Chebotar on 3/12/2018.
 */
interface LearnWordsPresenter : BasePresenter<LearnWordsView> {
    fun onItemSelected(position:Int)
}

interface LearnWordsView : BaseView {
    fun showFavoriteWords(list: List<WordDetails>, needToReset: Boolean)
}