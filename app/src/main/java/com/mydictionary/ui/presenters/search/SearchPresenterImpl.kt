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

class SearchPresenterImpl(val repository: WordsRepository) : SearchPresenter,
    SearchEditText.ContentChangedListener {
    val compositeDisposable = CompositeDisposable()
    var searchView: SearchView? = null

    override fun onStart(view: SearchView) {
        searchView = view;
        loadHistoryWords(true)

    }

    private fun loadHistoryWords(shouldAnimate: Boolean) {
        val disposable = repository.getHistoryWords()
            .onErrorReturn { emptyList()}
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                searchView?.showHistoryWords(list, shouldAnimate)
            }, { e -> searchView?.showError(e.message ?: "") })
        compositeDisposable.add(disposable)
    }


    override fun onStop() {
        searchView = null
        compositeDisposable.clear()
    }

    override fun onSearchLetterEntered(phrase: String) {
        if (phrase.length >= MIN_WORD_LENGTH_TO_SEARCH) {
            val disposable = repository.searchWord(phrase).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ searchView?.showSearchResult(it) },
                    { searchView?.showError(it.message ?: "") })
            compositeDisposable.add(disposable)
        } else {
            onSearchCleared()
        }
    }

    override fun onSearchCleared() {
        loadHistoryWords(false)
    }

    override fun onSearchClosed() {
        searchView?.finishView()
    }


}
