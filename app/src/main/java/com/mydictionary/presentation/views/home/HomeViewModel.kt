package com.mydictionary.presentation.views.home

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.mydictionary.domain.entity.Result
import com.mydictionary.domain.usecases.ShowWordListsUseCase
import com.mydictionary.presentation.views.Data
import com.mydictionary.presentation.views.DataState
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

/**
 * Created by Viktoria Chebotar on 15.04.18.
 */

class HomeViewModel @Inject constructor(private val showWordListsUseCase: ShowWordListsUseCase) :
    ViewModel() {
    val wordList = MutableLiveData<Data<List<WordListItem>>>()
    private var job: Job? = null

    init {
        loadWordLists()
    }

    private fun loadWordLists() {
        job = launch(UI) {
            wordList.value = Data(DataState.LOADING, wordList.value?.data, null)
            val result = showWordListsUseCase.execute()
            when (result) {
                is Result.Success -> {
                    val presentationData = mapToPresentation(result.data)
                    wordList.value = Data(DataState.SUCCESS, presentationData, null)
                }
                is Result.Error -> wordList.value =
                        Data(DataState.ERROR, wordList.value?.data, result.exception.message)
            }
        }
    }

    override fun onCleared() {
        job?.cancel()
        super.onCleared()
    }
}