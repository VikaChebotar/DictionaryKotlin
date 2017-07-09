package com.mydictionary.ui.presenters.search

import com.mydictionary.commons.Constants
import com.mydictionary.data.repository.RepositoryListener
import com.mydictionary.data.repository.WordsRepository
import com.mydictionary.ui.views.search.SearchEditText

/**
 * Created by Viktoria Chebotar on 25.06.17.
 */

class SearchPresenterImpl(val repository: WordsRepository) : SearchPresenter, SearchEditText.ContentChangedListener {

    var searchView: SearchView? = null
    var historyWords: List<String>? = null

    override fun onStart(view: SearchView) {
        searchView = view;
        loadHistoryWords()

    }

    private fun loadHistoryWords() {
        repository.getHistoryWords(Constants.HISTORY_SEARCH_LIMIT, object : RepositoryListener<List<String>> {
            override fun onSuccess(result: List<String>) {
                historyWords = result
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
            repository.searchWord(phrase, object : RepositoryListener<List<String>> {
                override fun onSuccess(result: List<String>) {
                    searchView?.showSearchResult(result)
                }

                override fun onError(error: String) {
                    searchView?.showError(error)
                }

            })
        } else {
            onSearchCleared()
        }
    }

    override fun onSearchCleared() {
        if (historyWords == null) {
            loadHistoryWords()
        } else {
            searchView?.showHistoryWords(historyWords as List<String>)
        }
    }

    override fun onSearchClosed() {
        searchView?.finishView()
    }


}
