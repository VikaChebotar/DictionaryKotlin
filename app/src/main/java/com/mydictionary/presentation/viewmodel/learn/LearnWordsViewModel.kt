package com.mydictionary.presentation.viewmodel.learn

import android.annotation.SuppressLint
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.os.Parcelable
import android.util.Log
import com.mydictionary.presentation.utils.NonNullMutableLiveData
import com.mydictionary.domain.FAV_WORD_PAGE_THRESHOLD
import com.mydictionary.domain.entity.PagedResult
import com.mydictionary.domain.entity.SortingOption
import com.mydictionary.domain.usecases.AddMeaningToFavoritesUseCase
import com.mydictionary.domain.usecases.RemoveMeaningFromFavoritesUseCase
import com.mydictionary.domain.usecases.ShowFavoriteWordsUseCase
import com.mydictionary.presentation.viewmodel.Data
import com.mydictionary.presentation.viewmodel.DataState
import com.mydictionary.presentation.viewmodel.word.Definition
import com.mydictionary.presentation.viewmodel.word.Example
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

/**
 * Created by Viktoria Chebotar on 15.04.18.
 */
class LearnWordsViewModel @Inject constructor(val showFavoriteWordsUseCase: ShowFavoriteWordsUseCase,
                                              val addMeaningToFavoritesUseCase: AddMeaningToFavoritesUseCase,
                                              val removeMeaningFromFavoritesUseCase: RemoveMeaningFromFavoritesUseCase)
    : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private val paginator = PublishProcessor.create<ShowFavoriteWordsUseCase.Parameter>()
    private val pagingLiveData = MediatorLiveData<Pair<Int, SortingOption>>()

    val list = MutableLiveData<Data<List<UserWordInfoPresentation>>>()
    val deletedWordInfo = MutableLiveData<Data<DeletedWordInfo>>()
    val totalSize = MutableLiveData<Int>()
    val currentSelectedPosition = NonNullMutableLiveData(0)
    val sortingType = NonNullMutableLiveData(SortingOption.BY_DATE)

    private var favWordsOffset = 0


    private val pagingLiveDataObserver = Observer<Pair<Int, SortingOption>> {
        if (currentSelectedPosition.value + FAV_WORD_PAGE_THRESHOLD >= favWordsOffset
                && currentSelectedPosition.value < totalSize.value ?: Int.MAX_VALUE
        ) {
            loadNextPage()
        }
    }

    init {
        pagingLiveData.addSource(
                currentSelectedPosition,
                { pagingLiveData.value = Pair(currentSelectedPosition.value, sortingType.value) })
        pagingLiveData.addSource(sortingType, { sortingOption ->
            favWordsOffset = 0
            pagingLiveData.value = Pair(currentSelectedPosition.value, sortingType.value)
        })
        pagingLiveData.observeForever(pagingLiveDataObserver)
        subscribeToNextPageResult()
        loadNextPage()
    }

    private fun subscribeToNextPageResult() {
        val disposable = paginator.filter { list.value?.dataState != DataState.LOADING }
                .onBackpressureDrop()
                .doOnNext {
                    list.value = Data(DataState.LOADING, list.value?.data, null)
                }
                .concatMap {
                    showFavoriteWordsUseCase.execute(it)
                }
                .map {
                    val mappedList =
                            it.list.map {
                                with(it) {
                                    val meanings =
                                            wordInfo.meanings.filter {
                                                userWord.favMeanings.contains(it.definitionId)
                                            }.map {
                                                UserWordMeaningPresentation(
                                                        it.definitionId,
                                                        it.definitions?.map { Definition(it) }
                                                                ?: emptyList(), it.partOfSpeech,
                                                        it.examples?.map { Example(it) }
                                                                ?: emptyList()
                                                )
                                            }
                                    UserWordInfoPresentation(wordInfo.word, meanings)
                                }
                            }
                    PagedResult<UserWordInfoPresentation>(mappedList, it.totalSize)
                }
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

    private fun loadNextPage() {
        paginator.onNext(ShowFavoriteWordsUseCase.Parameter(favWordsOffset, sortingType.value))
    }

    fun onItemSelected(position: Int) {
        currentSelectedPosition.value = position
    }

    fun onSortSelected(sortingOption: SortingOption) {
        if (sortingOption != sortingType.value || sortingOption == SortingOption.RANDOMLY)
            sortingType.value = sortingOption
    }

    fun onItemDeleteClicked(wordDetails: UserWordInfoPresentation) {
        val oldFavMeanings = wordDetails.meanings.map { it.definitionId }
        val disposable =
                removeMeaningFromFavoritesUseCase.execute(RemoveMeaningFromFavoritesUseCase.Parameter(
                        wordDetails.word, oldFavMeanings
                ))
                        .subscribe({
                            val oldList = list.value?.data?.toMutableList() ?: mutableListOf()
                            val position = oldList.indexOf(wordDetails)
                            oldList.remove(wordDetails)
                            totalSize.value = totalSize.value?.minus(1)
                            list.value = Data(DataState.SUCCESS, oldList, null)
                            deletedWordInfo.value = Data(
                                    DataState.SUCCESS,
                                    DeletedWordInfo(wordDetails, oldFavMeanings, position)
                            )
                        }, { exception ->
                            deletedWordInfo.value = Data(DataState.ERROR, null, exception.message)
                        })
        compositeDisposable.add(disposable)
    }

    fun onUndoDeletionClicked(deletedWordInfo: DeletedWordInfo) {
        val disposable = addMeaningToFavoritesUseCase.execute(AddMeaningToFavoritesUseCase.Parameter(
                deletedWordInfo.wordDetails.word,
                deletedWordInfo.oldFavMeanings))
                .subscribe({
                    val oldList = list.value?.data?.toMutableList() ?: mutableListOf()
                    oldList.add(deletedWordInfo.oldPosition, deletedWordInfo.wordDetails)
                    totalSize.value = totalSize.value?.plus(1)
                    list.value = Data(DataState.SUCCESS, oldList, null)
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
        val wordDetails: UserWordInfoPresentation,
        val oldFavMeanings: List<String>,
        val oldPosition: Int
)

@SuppressLint("ParcelCreator")
@Parcelize
data class UserWordInfoPresentation(val word: String,
                                    var meanings: List<UserWordMeaningPresentation>) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class UserWordMeaningPresentation(
        val definitionId: String,
        val definitions: List<Definition>,
        var partOfSpeech: String,
        val examples: List<Example>
) : Parcelable
