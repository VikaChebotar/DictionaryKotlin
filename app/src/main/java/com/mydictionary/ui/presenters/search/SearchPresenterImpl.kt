package com.mydictionary.ui.presenters.search

import com.mydictionary.commons.Constants
import com.mydictionary.data.repository.RepositoryListener
import com.mydictionary.data.repository.WordsRepository
import com.mydictionary.ui.views.search.SearchEditText
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Viktoria Chebotar on 25.06.17.
 */

class SearchPresenterImpl(val repository: WordsRepository) : SearchPresenter, SearchEditText.ContentChangedListener {

    var searchView: SearchView? = null

    override fun onStart(view: SearchView) {
        searchView = view;
        loadHistoryWords()

    }

    private fun loadHistoryWords() {
        repository.getHistoryWords(object : RepositoryListener<List<String>> {
            override fun onSuccess(result: List<String>) {
                searchView?.showHistoryWords(result)
            }

            override fun onError(error: String) {
                searchView?.showError(error)
            }
        })
    }


    override fun onStop() {
        searchView = null
    }

    override fun onSearchLetterEntered(phrase: String) {
        if (phrase.length >= Constants.MIN_WORD_LENGTH_TO_SEARCH) {
            repository.searchWord(phrase).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe({ searchView?.showSearchResult(it) },
                            { searchView?.showError(it.message ?: "") })
        } else {
            onSearchCleared()
        }
    }

    override fun onSearchCleared() {
        loadHistoryWords()
    }

    override fun onSearchClosed() {
        searchView?.finishView()
    }


}
