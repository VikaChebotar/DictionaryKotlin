package com.mydictionary.ui.presenters.home

import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.data.repository.WordsRepository

/**
 * Created by Viktoria Chebotar on 18.06.17.
 */

class HomePresenterImpl(val repository: WordsRepository) : HomePresenter {
    var homeView: HomeView? = null
    var todayWord: WordDetails? = null

    override fun onStart(view: HomeView) {
        this.homeView = view
        if (todayWord == null) {
       //    homeView?.showProgress(true)
//            repository.getTodayWordInfo(Calendar.getInstance().time,
//                    object : RepositoryListener<WordInfo> {
//                        override fun onSuccess(wordInfo: WordInfo) {
//                            todayWord = wordInfo;
//                            homeView?.showWordOfTheDay(wordInfo)
//                            homeView?.showProgress(false)
//                        }
//
//                        override fun onError(error: String) {
//                            homeView?.showProgress(false)
//                            homeView?.showError(error)
//                        }
//                    })
        } else {
           // homeView?.showWordOfTheDay(todayWord as WordDetails)
        }
    }

    override fun onMyWordsBtnClicked() {
        homeView?.startMyWordsActivity()
    }

    override fun onWordOfTheDayFavoriteBtnClicked() {
//        todayWord?.let {
//            repository.setWordFavoriteState(it.word, !it.isFavorite,
//                    object : RepositoryListener<Boolean> {
//                        override fun onSuccess(t: Boolean) {
//                            todayWord?.isFavorite = t
//                            homeView?.showWordOfTheDayFavoriteBtnState(t)
//                        }
//
//                        override fun onError(error: String) {
//                        }
//
//                    })
//        }
    }

    override fun onResume() {
//        todayWord?.let {
//            it.isFavorite = repository.getWordFavoriteState(it.word)
//            homeView?.showWordOfTheDayFavoriteBtnState(it.isFavorite)
//        }
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
