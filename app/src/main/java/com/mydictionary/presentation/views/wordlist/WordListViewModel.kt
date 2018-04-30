package com.mydictionary.presentation.views.wordlist

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.util.Log
import com.mydictionary.domain.entity.Result
import com.mydictionary.domain.usecases.ShowWordListUseCase
import com.mydictionary.presentation.DictionaryApp
import com.mydictionary.presentation.views.Data
import com.mydictionary.presentation.views.DataState
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class WordListViewModel(
    private val showWordListUseCase: ShowWordListUseCase,
    val wordListName: String
) :
    ViewModel() {
    private var job: Job? = null
    val wordList = MutableLiveData<Data<List<String>>>()
    private var isReverseOrder = false

    init {
        Log.d("TAG", "word list view model")
        loadList()
    }

    private fun loadList() {
        job = launch(UI) {
            wordList.value = Data(DataState.LOADING, wordList.value?.data, null)
            val result = showWordListUseCase.execute(
                ShowWordListUseCase.Parameter(
                    wordListName,
                    isReverseOrder
                )
            )
            when (result) {
                is Result.Success -> wordList.value =
                        Data(DataState.SUCCESS, result.data.list, null)
                is Result.Error -> wordList.value =
                        Data(DataState.ERROR, null, result.exception.message)
            }
        }
    }

    fun sortMenu() {
        isReverseOrder = !isReverseOrder
        loadList()
    }

    override fun onCleared() {
        job?.cancel()
        super.onCleared()
    }
}

class WordListViewModelFactory(val wordListName: String) :
    ViewModelProvider.Factory {
    @Inject
    lateinit var showWordListUseCase: ShowWordListUseCase

    init {
        DictionaryApp.component.inject(this)
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            WordListViewModel::class.java -> WordListViewModel(
                showWordListUseCase,
                wordListName
            ) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class");
        }
    }

}