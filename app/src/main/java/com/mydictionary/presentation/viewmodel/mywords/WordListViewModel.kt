package com.mydictionary.presentation.viewmodel.mywords

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.mydictionary.data.repository.WordsRepository
import com.mydictionary.presentation.Data
import com.mydictionary.presentation.DataState
import com.mydictionary.presentation.DictionaryApp
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class WordListViewModel(private val repository: WordsRepository, val wordListName: String) :
    ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    val wordList = MutableLiveData<Data<List<String>>>()

    init {
        loadList()
    }

    private fun loadList() {
        repository.getWordList(wordListName)
            .doOnSubscribe {
                wordList.postValue(Data(DataState.LOADING, wordList.value?.data, null))
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                wordList.value = Data(DataState.SUCCESS, it, null)
            }, { throwable ->
                wordList.value = Data(DataState.ERROR, null, throwable.message)
            })
    }

    fun sortMenu() {
        val list = wordList.value?.data?.toMutableList()
        list?.reverse()
        wordList.value = Data(DataState.SUCCESS, list, null)
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}

class WordListViewModelFactory(val wordListName: String) :
    ViewModelProvider.Factory {
    @Inject
    lateinit var repository: WordsRepository

    init {
        DictionaryApp.component.inject(this)
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            WordListViewModel::class.java -> WordListViewModel(repository, wordListName) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class");
        }
    }

}