package com.mydictionary.presentation.viewmodel.search

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.mydictionary.domain.MIN_WORD_LENGTH_TO_SEARCH
import com.mydictionary.domain.usecases.SearchWordUseCase
import com.mydictionary.domain.usecases.ShowUserHistoryUseCase
import com.mydictionary.presentation.viewmodel.Data
import com.mydictionary.presentation.viewmodel.DataState
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    val searchUseCase: SearchWordUseCase,
    val userHistoryUseCase: ShowUserHistoryUseCase
) : ViewModel() {
    val searchResultList = MutableLiveData<Data<SearchResult>>()

    private val compositeDisposable = CompositeDisposable()
    private val searchPublisher = PublishProcessor.create<String>()
    private var historyListResult = listOf<String>()

    init {
        Log.d("TAG", "search view model")
        loadHistoryWords()
        subscribeToSearchResult()
    }

    fun onSearchLetterEntered(phrase: String) {
        if (phrase.length < MIN_WORD_LENGTH_TO_SEARCH) {
            onSearchCleared()
        } else {
            searchPublisher.onNext(phrase)
        }
    }

    private fun subscribeToSearchResult() {
        compositeDisposable.add(
            searchUseCase.execute(searchPublisher).subscribe(
                { searchResultList.value = Data(DataState.SUCCESS, SearchResult(it), null) },
                { searchResultList.value = Data(DataState.ERROR, null, it.message) })
        )
    }

    fun onSearchCleared() {
        loadHistoryWords()
    }

    private fun loadHistoryWords() {
        val disposable = Single.just(historyListResult)
            .flatMap {
                if (it.isEmpty()) {
                    userHistoryUseCase.execute()
                } else Single.just(it)
            }
            .subscribe({ list ->
                historyListResult = list
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