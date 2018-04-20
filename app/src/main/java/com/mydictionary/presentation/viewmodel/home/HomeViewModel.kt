package com.mydictionary.presentation.viewmodel.home

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.mydictionary.domain.usecases.ShowWordListsUseCase
import com.mydictionary.presentation.Data
import com.mydictionary.presentation.DataState
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by Viktoria Chebotar on 15.04.18.
 */

class HomeViewModel @Inject constructor(val showWordListsUseCase: ShowWordListsUseCase): ViewModel() {
    val wordList = MutableLiveData<Data<List<WordListItem>>>()
    private val compositeDisposable = CompositeDisposable()

    init {
        Log.d("TAG", "home view model")
        loadWordLists()
    }

    private fun loadWordLists() {
        compositeDisposable.add(showWordListsUseCase.execute()
                .doOnSubscribe { wordList.postValue(Data(DataState.LOADING, wordList.value?.data, null)) }
                .flatMap { Single.just(mapToPresentation(it)) }
                .subscribe(
                        {
                            wordList.postValue(Data(DataState.SUCCESS, it, null))
                        },
                        {
                            wordList.postValue(Data(DataState.ERROR, wordList.value?.data, it.message))
                        }))
    }

    override fun onCleared() {
        compositeDisposable.dispose()
        super.onCleared()
    }
}