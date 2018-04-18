package com.mydictionary.presentation.viewmodel.learn

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.mydictionary.commons.FAV_WORD_PAGE_SIZE
import com.mydictionary.commons.FAV_WORD_PAGE_THRESHOLD
import com.mydictionary.commons.NonNullMutableLiveData
import com.mydictionary.data.pojo.SortingOption
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.data.repository.WordsRepository
import com.mydictionary.presentation.Data
import com.mydictionary.presentation.DataState
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by Viktoria Chebotar on 15.04.18.
 */
class LearnWordsViewModel @Inject constructor(val repository: WordsRepository): ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private val paginator = PublishProcessor.create<Int>()
    private val pagingLiveData = MediatorLiveData<Pair<Int, SortingOption>>()

    val list = MutableLiveData<Data<List<WordDetails>>>()
    val deletedWordInfo = MutableLiveData<Data<DeletedWordInfo>>()
    val totalSize = MutableLiveData<Int>()
    val currentSelectedPosition = NonNullMutableLiveData(0)
    val sortingType = NonNullMutableLiveData(SortingOption.BY_DATE)

    private var favWordsOffset = 0


   private val pagingLiveDataObserver = Observer<Pair<Int, SortingOption>> {
        if (currentSelectedPosition.value + FAV_WORD_PAGE_THRESHOLD >= favWordsOffset
            && currentSelectedPosition.value < totalSize.value ?: Int.MAX_VALUE
        ) {
            loadFavoriteWords()
        }
    }

    init {
        createRequest()
        loadFavoriteWords()
        pagingLiveData.addSource(
            currentSelectedPosition,
            { pagingLiveData.value = Pair(currentSelectedPosition.value, sortingType.value) })
        pagingLiveData.addSource(sortingType, { sortingOption ->
            favWordsOffset = 0
            pagingLiveData.value = Pair(currentSelectedPosition.value, sortingType.value)
        })
        pagingLiveData.observeForever(pagingLiveDataObserver)
    }

    private fun createRequest() {
        val disposable =
            paginator.filter { list.value?.dataState != DataState.LOADING }
                .onBackpressureDrop()
                .doOnNext {
                    list.value = Data(DataState.LOADING, list.value?.data, null)
                }
                .concatMap {
                    repository.getFavoriteWordsInfo(it, FAV_WORD_PAGE_SIZE, sortingType.value)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ pagedResult ->
                    totalSize.postValue(pagedResult.totalSize)
                    val oldList = list.value?.data?.toMutableList() ?: mutableListOf()
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
        if (!compositeDisposable.isDisposed)
            paginator.onNext(favWordsOffset)
    }

    fun onItemSelected(position: Int) {
        currentSelectedPosition.value = position
    }

    fun onSortSelected(sortingOption: SortingOption) {
        if (sortingOption != sortingType.value || sortingOption == SortingOption.RANDOMLY)
            sortingType.value = sortingOption
    }

    fun onItemDeleteClicked(wordDetails: WordDetails) {
        val favMeanings = emptyList<String>()
        val oldFavMeanings = wordDetails.meanings.filter { it.isFavourite }.map { it.definitionId }
        val disposable =
            repository.setWordFavoriteState(wordDetails, favMeanings)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ wordDetails ->
                    if (wordDetails.meanings.none { it.isFavourite }) {
                        val oldList = list.value?.data?.toMutableList() ?: mutableListOf()
                        val position = oldList.indexOf(wordDetails)
                        oldList.remove(wordDetails)
                        totalSize.value = totalSize.value?.minus(1)
                        list.value = Data(DataState.SUCCESS, oldList, null)
                        deletedWordInfo.value = Data(
                            DataState.SUCCESS,
                            DeletedWordInfo(wordDetails, oldFavMeanings, position)
                        )
                    } else {
                        deletedWordInfo.value = Data(DataState.ERROR)
                    }
                }, { exception ->
                    deletedWordInfo.value = Data(DataState.ERROR, null, exception.message)
                })
        compositeDisposable.add(disposable)
    }

    fun onUndoDeletionClicked(deletedWordInfo: DeletedWordInfo) {
        val disposable = repository.setWordFavoriteState(
            deletedWordInfo.wordDetails,
            deletedWordInfo.oldFavMeanings
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ t ->
                if (t.meanings.any { it.isFavourite }) {
                    val oldList = list.value?.data?.toMutableList() ?: mutableListOf()
                    oldList.add(deletedWordInfo.oldPosition, t)
                    totalSize.value = totalSize.value?.plus(1)
                    list.value = Data(DataState.SUCCESS, oldList, null)
                } else {
                    Log.e(LearnWordsViewModel::class.java.name, "Error undoing deletion")
                }
            }, {
                Log.e(LearnWordsViewModel::class.java.name, it.message)
            })
        compositeDisposable.add(disposable)
    }

    override fun onCleared() {
        compositeDisposable.clear()
        pagingLiveData.removeSource(currentSelectedPosition)
        pagingLiveData.removeSource(sortingType)
        pagingLiveData.removeObserver(pagingLiveDataObserver)
        super.onCleared()
    }
}

data class DeletedWordInfo(
    val wordDetails: WordDetails,
    val oldFavMeanings: List<String>,
    val oldPosition: Int
)