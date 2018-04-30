package com.mydictionary.presentation.views.search

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.mydictionary.domain.MIN_WORD_LENGTH_TO_SEARCH
import com.mydictionary.domain.entity.Result
import com.mydictionary.domain.usecases.SearchWordUseCase
import com.mydictionary.domain.usecases.ShowUserHistoryUseCase
import com.mydictionary.presentation.views.Data
import com.mydictionary.presentation.views.DataState
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    val searchUseCase: SearchWordUseCase,
    val userHistoryUseCase: ShowUserHistoryUseCase
) : ViewModel() {
    val searchResultList = MutableLiveData<Data<SearchResult>>()

    private var historyListResult = listOf<String>()
    private var searchJob: Job? = null
    private var historyJob: Job? = null
    private val searchQueryChannel = Channel<String>()

    init {
        Log.d("TAG", "loadSearchResult view model")
        loadHistoryWords()
        subscribeToSearchResult()
    }

    fun onSearchLetterEntered(phrase: String) {
        if (phrase.length < MIN_WORD_LENGTH_TO_SEARCH) {
            onSearchCleared()
        } else {
            loadSearchResult(phrase)
        }
    }

    private fun loadSearchResult(query: String) {
        searchQueryChannel.offer(query)
    }

    private fun subscribeToSearchResult() {
        searchJob = launch(UI) {
            searchUseCase.execute(searchQueryChannel).consumeEach { result ->
                when (result) {
                    is Result.Success ->
                        searchResultList.postValue(
                            Data(DataState.SUCCESS, SearchResult(result.data), null)
                        )
                    is Result.Error -> searchResultList.postValue(
                        Data(DataState.ERROR, null, result.exception.message)
                    )
                }
            }
        }
    }

    fun onSearchCleared() {
        loadHistoryWords()
    }

    private fun loadHistoryWords() {
        historyJob = launch(UI) {
            if (historyListResult.isEmpty()) {
                val result = userHistoryUseCase.execute()
                when (result) {
                    is Result.Success -> {
                        historyListResult = result.data
                        searchResultList.value =
                                Data(DataState.SUCCESS, SearchResult(historyListResult, true), null)
                    }
                    is Result.Error -> searchResultList.value =
                            Data(DataState.ERROR, null, result.exception.message)
                }
            } else {
                searchResultList.value =
                        Data(DataState.SUCCESS, SearchResult(historyListResult, true), null)
            }
        }
    }

    override fun onCleared() {
        searchJob?.cancel()
        historyJob?.cancel()
        super.onCleared()
    }
}

data class SearchResult(
    val list: List<String>,
    val isHistory: Boolean = false
)