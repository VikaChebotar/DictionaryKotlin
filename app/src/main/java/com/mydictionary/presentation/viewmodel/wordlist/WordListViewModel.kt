package com.mydictionary.presentation.viewmodel.wordlist

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.util.Log
import com.mydictionary.domain.usecases.ShowWordListUseCase
import com.mydictionary.presentation.viewmodel.Data
import com.mydictionary.presentation.viewmodel.DataState
import com.mydictionary.presentation.DictionaryApp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class WordListViewModel(
    private val showWordListUseCase: ShowWordListUseCase,
    val wordListName: String
) :
    ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    val wordList = MutableLiveData<Data<List<String>>>()
    private var isReverseOrder = false

    init {
        Log.d("TAG", "word list view model")
        loadList()
    }

    private fun loadList() {
        compositeDisposable.add(showWordListUseCase
            .execute(ShowWordListUseCase.Parameter(wordListName, isReverseOrder))
            .doOnSubscribe {
                wordList.postValue(Data(DataState.LOADING, wordList.value?.data, null))
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                wordList.value = Data(DataState.SUCCESS, it.list, null)
            }, { throwable ->
                wordList.value = Data(DataState.ERROR, null, throwable.message)
            })
        )
    }

    fun sortMenu() {
        isReverseOrder = !isReverseOrder
        loadList()
    }

    override fun onCleared() {
        compositeDisposable.dispose()
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
            WordListViewModel::class.java -> WordListViewModel(showWordListUseCase, wordListName) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class");
        }
    }

}