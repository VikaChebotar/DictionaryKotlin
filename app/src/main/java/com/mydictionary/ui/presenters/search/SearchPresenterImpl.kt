package com.mydictionary.ui.presenters.search

import com.mydictionary.commons.MIN_WORD_LENGTH_TO_SEARCH
import com.mydictionary.data.repository.WordsRepository
import com.mydictionary.ui.views.search.SearchEditText
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

/**
 * Created by Viktoria Chebotar on 25.06.17.
 */

class SearchPresenterImpl(val repository: WordsRepository) : SearchPresenter, SearchEditText.ContentChangedListener {
    val compositeDisposable = CompositeDisposable()
    var searchView: SearchView? = null

    override fun onStart(view: SearchView) {
        searchView = view;
        loadHistoryWords()

    }

    private fun loadHistoryWords() {
        val disposable = repository.getHistoryWords().
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread()).
                subscribe({ list -> searchView?.showHistoryWords(list) }, { e -> searchView?.showError(e.message ?: "") })
        compositeDisposable.add(disposable)
    }
//    repository.getHistoryWords(object : RepositoryListener<List<String>> {
//        override fun onSuccess(result: List<String>) {
//            searchView?.showHistoryWords(result)
//        }
//
//        override fun onError(error: String) {
//            searchView?.showError(error)
//        }
//    })


    override fun onStop() {
        searchView = null
        compositeDisposable.clear()
    }

    override fun onSearchLetterEntered(phrase: String) {
        if (phrase.length >= MIN_WORD_LENGTH_TO_SEARCH) {
           val disposable = repository.searchWord(phrase).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe({ searchView?.showSearchResult(it) },
                            { searchView?.showError(it.message ?: "") })
            compositeDisposable.add(disposable)
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
