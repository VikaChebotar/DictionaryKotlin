package com.mydictionary.ui.presenters

import com.mydictionary.data.entity.WordInfo
import com.mydictionary.data.repository.WordsRepository
import java.util.*

/**
 * Created by Viktoria Chebotar on 18.06.17.
 */

class HomePresenterImpl(val repository: WordsRepository) : HomePresenter {
    var homeView: HomeView? = null

    override fun onStart(view: HomeView) {
        this.homeView = view
        repository.getTodayWord(Calendar.getInstance().time,
                object : WordsRepository.WordSourceListener<WordInfo> {
                    override fun onSuccess(wordInfo: WordInfo) {
                        homeView?.showWordOfTheDay(wordInfo)
                    }

                    override fun onError(error: String) {
                        homeView?.showError(error)
                    }
                })
    }

    override fun onStop() {
        homeView = null
    }
}
