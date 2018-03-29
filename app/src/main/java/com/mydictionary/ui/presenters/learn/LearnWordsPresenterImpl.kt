package com.mydictionary.ui.presenters.learn

import android.util.Log
import com.mydictionary.R
import com.mydictionary.commons.Constants
import com.mydictionary.commons.Constants.Companion.FAV_WORD_PAGE_THRESHOLD
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.data.repository.RepositoryListener
import com.mydictionary.data.repository.WordsRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.processors.PublishProcessor
import java.util.*

/**
 * Created by Viktoria_Chebotar on 3/12/2018.
 */
class LearnWordsPresenterImpl(val repository: WordsRepository) : LearnWordsPresenter {
    val TAG = LearnWordsPresenterImpl::class.java.simpleName
    var wordsView: LearnWordsView? = null
    var favWordsOffset = 0
    val list = mutableListOf<WordDetails>()
    val paginator = PublishProcessor.create<Int>();
    var requestUnderWay = false
    var totalSize: Int = 0

    override fun onStart(view: LearnWordsView) {
        wordsView = view
        paginator.
                filter { !requestUnderWay }.
                onBackpressureDrop().
                doOnNext {
                    wordsView?.showProgress(it == 0)
                    requestUnderWay = true
                }.
                //subscribeOn(Schedulers.io()).
                concatMap { repository.getFavoriteWords(it, Constants.FAV_WORD_PAGE_SIZE) }.
                observeOn(AndroidSchedulers.mainThread()).
                doOnNext {
                    wordsView?.showProgress(false)
                    requestUnderWay = false
                }.
                subscribe({ pagedResult ->
                    totalSize = pagedResult.totalSize
                    if (favWordsOffset == 0) {
                        list.clear()
                        if (pagedResult.list.isNotEmpty()) showPositionText(0, totalSize)
                    }
                    list.addAll(pagedResult.list)
                    wordsView?.showFavoriteWords(list)
                    wordsView?.showProgress(false)
                    favWordsOffset += pagedResult.list.size
                }, {
                    wordsView?.showProgress(false)
                    requestUnderWay = false
                    Log.e(TAG, it.message ?: view.getContext().getString(R.string.default_error))
                    wordsView?.showError(it.message ?: view.getContext().getString(R.string.default_error))
                })
        loadFavoriteWords()
    }

    override fun onStop() {
        wordsView = null
    }

    override fun onItemSelected(position: Int) {
        if (position + FAV_WORD_PAGE_THRESHOLD >= favWordsOffset) {
            loadFavoriteWords()
        }
        showPositionText(position, totalSize)
    }

    private fun showPositionText(position: Int, totalSize: Int) {
        wordsView?.showPositionText(wordsView?.getContext()?.getString(R.string.fav_word_selected,
                position + 1, totalSize) ?: "")
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
                    wordsView?.showError(wordsView?.getContext()?.getString(R.string.delete_word_error) ?: "")
                }
            }

            override fun onError(error: String) {
                Log.e(TAG, "error: " + error)
                wordsView?.showError(error)
            }

        })
    }

    private fun loadFavoriteWords() {
        paginator.onNext(favWordsOffset)
        //paginator.onComplete()
    }

    override fun onUndoDeletionClicked(oldWordDetails: WordDetails, favMeanings: List<String>, position: Int) {
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

    override fun onShuffleClicked() {
        Collections.shuffle(list)
        wordsView?.showFavoriteWords(list)
    }
}