package com.mydictionary.ui.presenters.home

import com.mydictionary.data.entity.WordInfo
import com.mydictionary.data.repository.WordsRepository
import java.util.*

/**
 * Created by Viktoria Chebotar on 18.06.17.
 */

class HomePresenterImpl(val repository: WordsRepository) : HomePresenter {
    var homeView: HomeView? = null
    var todayWord: WordInfo? = null

    override fun onStart(view: HomeView) {
        this.homeView = view
        if (todayWord == null) {
            homeView?.showProgress(true)
            repository.getTodayWord(Calendar.getInstance().time,
                    object : WordsRepository.WordSourceListener<WordInfo> {
                        override fun onSuccess(wordInfo: WordInfo) {
                            todayWord = wordInfo;
                            homeView?.showWordOfTheDay(wordInfo)
                            homeView?.showProgress(false)
                        }

                        override fun onError(error: String) {
                            homeView?.showProgress(false)
                            homeView?.showError(error)
                        }
                    })
        } else {
            homeView?.showWordOfTheDay(todayWord as WordInfo)
        }
    }

    override fun onStop() {
        homeView = null
    }

    override fun onWordOfTheDayClicked() {
        if (todayWord != null) {
            homeView?.startWordInfoActivity(todayWord!!)
        }
    }
}
