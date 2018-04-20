package com.mydictionary.presentation.viewmodel.home

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.mydictionary.data.repository.AllRepository
import com.mydictionary.presentation.Data
import com.mydictionary.presentation.DataState
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Viktoria Chebotar on 15.04.18.
 */

class HomeViewModel @Inject constructor(val repository: AllRepository): ViewModel() {
    val wordList = MutableLiveData<Data<List<WordListItem>>>()
    private val compositeDisposable = CompositeDisposable()

    init {
        loadWordLists()
    }

    private fun loadWordLists() {
        compositeDisposable.add(repository.getAllWordLists()
                .doOnSubscribe { wordList.postValue(Data(DataState.LOADING, wordList.value?.data, null)) }
                .flatMap { Single.just(mapToPresentation(it)) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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