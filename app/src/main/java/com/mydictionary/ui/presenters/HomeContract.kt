package com.mydictionary.ui.presenters

import com.mydictionary.data.entity.WordInfo

/**
 * Created by Viktoria Chebotar on 18.06.17.
 */

interface HomePresenter {
    fun onStart(view: HomeView)
    fun onStop()

}

interface HomeView {
    fun showWordOfTheDay(word: WordInfo)
    fun showProgress(progress: Boolean)
    fun showError(message: String)
}

