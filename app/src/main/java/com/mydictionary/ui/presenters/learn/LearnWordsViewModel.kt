package com.mydictionary.ui.presenters.learn

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.mydictionary.commons.FAV_WORD_PAGE_SIZE
import com.mydictionary.commons.FAV_WORD_PAGE_THRESHOLD
import com.mydictionary.data.pojo.SortingOption
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.data.repository.WordsRepository
import com.mydictionary.ui.Data
import com.mydictionary.ui.DataState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor

/**
 * Created by Viktoria Chebotar on 15.04.18.
 */
class LearnWordsViewModel(val repository: WordsRepository) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private val paginator = PublishProcessor.create<Int>()

    val list = MutableLiveData<Data<List<WordDetails>>>()
    val totalSize = MutableLiveData<Int>()
    val currentSelectedPosition = MutableLiveData<Int>()

    private var favWordsOffset = 0
    private var sortingType = SortingOption.BY_DATE

    init {
        createRequest()
        currentSelectedPosition.value = 0
        loadFavoriteWords()
    }

    private fun createRequest() {
        val disposable =
                paginator.filter { list.value?.dataState != DataState.LOADING }
                        .onBackpressureDrop()
                        .doOnNext {
                            list.value = Data(DataState.LOADING, list.value?.data, null)
                        }
                        .concatMap { repository.getFavoriteWordsInfo(it, FAV_WORD_PAGE_SIZE, sortingType) }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ pagedResult ->
                            totalSize.postValue(pagedResult.totalSize)
                            val oldList = list.value?.data?.toMutableList()
                                    ?: mutableListOf<WordDetails>()
                            if (favWordsOffset == 0) {
                                oldList.clear()
                            }
                            oldList.addAll(pagedResult.list)
                            list.value = Data(DataState.SUCCESS, oldList, null)
                            favWordsOffset += pagedResult.list.size
                        }, {
                            list.value = Data(DataState.ERROR, list.value?.data, it.message)
                        })
        compositeDisposable.add(disposable)
    }

    private fun loadFavoriteWords() {
        if (!compositeDisposable.isDisposed && currentSelectedPosition.value ?: 0 < totalSize.value ?: Int.MAX_VALUE)
            paginator.onNext(favWordsOffset)
    }

    fun onItemSelected(position: Int) {
        currentSelectedPosition.value = position
        if (position + FAV_WORD_PAGE_THRESHOLD >= favWordsOffset) {
            loadFavoriteWords()
        }
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}