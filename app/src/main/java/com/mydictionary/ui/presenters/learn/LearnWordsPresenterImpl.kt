package com.mydictionary.ui.presenters.learn

import android.content.Context
import android.util.Log
import com.mydictionary.R
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
    val list = mutableListOf<WordDetails>()
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

    override fun onItemDeleteClicked(wordDetails: WordDetails) {
        val favMeanings = emptyList<String>()
        val oldFavMeanings = wordDetails.meanings.filter { it.isFavourite }.map { it.definitionId }
        repository.setWordFavoriteState(wordDetails, favMeanings, object : RepositoryListener<WordDetails> {
            override fun onSuccess(t: WordDetails) {
                if (t.meanings.none { it.isFavourite }) {
                    val position = list.indexOf(wordDetails)
                    list.remove(wordDetails)
                    wordsView?.showFavoriteWords(list)
                    wordsView?.showWordDeletedMessage(wordDetails, oldFavMeanings, position)
                } else {
                    wordsView?.showError(context.getString(R.string.delete_word_error))
                }
            }

            override fun onError(error: String) {
                Log.e(TAG, "error: " + error)
                wordsView?.showError(error)
            }

        })
    }

    private fun loadFavoriteWords() {
        repository.getCurrentUser()?.let {
            wordsView?.showProgress(favWordsOffset == 0)
            repository.getFavoriteWords(favWordsOffset, Constants.FAV_WORD_PAGE_SIZE, object : RepositoryListener<List<WordDetails>> {
                override fun onSuccess(t: List<WordDetails>) {
                    super.onSuccess(t)
                    if (favWordsOffset == 0) {
                        list.clear()
                    }
                    list.addAll(t)
                    wordsView?.showFavoriteWords(list)
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

    override fun onUndoDeletionClicked(oldWordDetails: WordDetails, favMeanings:List<String>, position: Int) {
        repository.setWordFavoriteState(oldWordDetails, favMeanings, object : RepositoryListener<WordDetails> {
            override fun onSuccess(t: WordDetails) {
                super.onSuccess(t)
                if (t.meanings.any { it.isFavourite }) {
                    list.add(position, t)
                    wordsView?.showFavoriteWords(list)
                } else {
                    //TODO
                }
            }

            override fun onError(error: String) {
                super.onError(error)
                //TODO
            }
        })
    }

}