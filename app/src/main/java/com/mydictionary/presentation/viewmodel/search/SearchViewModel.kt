package com.mydictionary.presentation.viewmodel.search

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.mydictionary.data.repository.AllRepository
import com.mydictionary.domain.usecases.SearchWordUseCase
import com.mydictionary.presentation.Data
import com.mydictionary.presentation.DataState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    val repository: AllRepository,
    val searchUseCase: SearchWordUseCase
) : ViewModel() {
    val compositeDisposable = CompositeDisposable()
    val searchResultList = MutableLiveData<Data<SearchResult>>()
    val searchPublisher = PublishProcessor.create<String>()

    init {
        loadHistoryWords(true)
        subscribeToSearchResult()
    }

    fun onSearchLetterEntered(phrase: String) {
        searchPublisher.onNext(phrase)
    }

    private fun subscribeToSearchResult() {
        compositeDisposable.add(
            searchUseCase.execute(searchPublisher).subscribe(
                { searchResultList.value = Data(DataState.SUCCESS, SearchResult(it), null) },
                { searchResultList.value = Data(DataState.ERROR, null, it.message) })
        )
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