package com.mydictionary.ui.presenters.learn

import android.content.Context
import android.util.Log
import com.mydictionary.commons.Constants
import com.mydictionary.commons.Constants.Companion.FAV_WORD_PAGE_THRESHOLD
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.data.repository.RepositoryListener
import com.mydictionary.data.repository.WordsRepository

/**
 * Created by Viktoria_Chebotar on 3/12/2018.
 */
class LearnWordsPresenterImpl(val repository: WordsRepository, val context: Context) : LearnWordsPresenter {
    val TAG = LearnWordsPresenterImpl::class.java.simpleName
    var wordsView: LearnWordsView? = null
    var favWordsOffset = 0

    override fun onStart(view: LearnWordsView) {
        wordsView = view
        loadFavoriteWords()
    }

    override fun onStop() {

    }

    override fun onItemSelected(position: Int) {
        if (position + FAV_WORD_PAGE_THRESHOLD >= favWordsOffset) {
            loadFavoriteWords()
        }
    }

    private fun loadFavoriteWords() {
        repository.getCurrentUser()?.let {
            wordsView?.showProgress(favWordsOffset == 0)
            repository.getFavoriteWords(favWordsOffset, Constants.FAV_WORD_PAGE_SIZE, object : RepositoryListener<List<WordDetails>> {
                override fun onSuccess(t: List<WordDetails>) {
                    super.onSuccess(t)
                    wordsView?.showFavoriteWords(t, favWordsOffset == 0)
                    wordsView?.showProgress(false)
                    favWordsOffset += t.size
                }

                override fun onError(error: String) {
                    super.onError(error)
                    wordsView?.showProgress(false)
                    Log.e(TAG, error)
                }
            })
        }
    }
}