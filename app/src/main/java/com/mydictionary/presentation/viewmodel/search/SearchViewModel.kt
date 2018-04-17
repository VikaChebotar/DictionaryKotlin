package com.mydictionary.presentation.viewmodel.search

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.mydictionary.commons.MIN_WORD_LENGTH_TO_SEARCH
import com.mydictionary.data.repository.WordsRepository
import com.mydictionary.presentation.Data
import com.mydictionary.presentation.DataState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SearchViewModel(val repository: WordsRepository) : ViewModel() {
    val compositeDisposable = CompositeDisposable()
    val searchResultList = MutableLiveData<Data<SearchResult>>()

    init {
        loadHistoryWords(true)
    }

    fun onSearchLetterEntered(phrase: String) {
        if (phrase.length >= MIN_WORD_LENGTH_TO_SEARCH) {
            val disposable = repository.searchWord(phrase)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    searchResultList.value = Data(DataState.SUCCESS, SearchResult(it), null)
                },
                    { searchResultList.value = Data(DataState.ERROR, null, it.message) })
            compositeDisposable.add(disposable)
        } else {
            onSearchCleared()
        }
    }

    fun onSearchCleared() {
        loadHistoryWords(false)
    }

    private fun loadHistoryWords(shouldAnimate: Boolean) {
        val disposable = repository.getHistoryWords()
            .onErrorReturn { emptyList() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                searchResultList.value =
                        Data(DataState.SUCCESS, SearchResult(list, true), null)
            }, { searchResultList.value = Data(DataState.ERROR, null, it.message) })
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}

data class SearchResult(
    val list: List<String>,
    val isHistory: Boolean = false
)