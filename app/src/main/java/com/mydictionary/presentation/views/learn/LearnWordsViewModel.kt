package com.mydictionary.presentation.views.learn

import android.annotation.SuppressLint
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.os.Parcelable
import android.util.Log
import com.mydictionary.domain.FAV_WORD_PAGE_THRESHOLD
import com.mydictionary.domain.entity.*
import com.mydictionary.domain.usecases.AddMeaningToFavoritesUseCase
import com.mydictionary.domain.usecases.RemoveMeaningFromFavoritesUseCase
import com.mydictionary.domain.usecases.ShowFavoriteWordsUseCase
import com.mydictionary.presentation.utils.NonNullMutableLiveData
import com.mydictionary.presentation.views.Data
import com.mydictionary.presentation.views.DataState
import com.mydictionary.presentation.views.word.Definition
import com.mydictionary.presentation.views.word.Example
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

/**
 * Created by Viktoria Chebotar on 15.04.18.
 */
class LearnWordsViewModel @Inject constructor(
    val showFavoriteWordsUseCase: ShowFavoriteWordsUseCase,
    val addMeaningToFavoritesUseCase: AddMeaningToFavoritesUseCase,
    val removeMeaningFromFavoritesUseCase: RemoveMeaningFromFavoritesUseCase
) : ViewModel() {
    private val pagingLiveData = MediatorLiveData<Pair<Int, SortingOption>>()
    private val pagingChannel = Channel<ShowFavoriteWordsUseCase.Parameter>()
    private var loadNextPageJob: Job? = null
    private var deleteWordJob: Job? = null
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
        loadNextPageJob = launch(UI) {
            showFavoriteWordsUseCase.execute(pagingChannel).consumeEach { result ->
                when (result) {
                    is Result.Success -> {
                        val mappedList =
                            result.data.list.map { it.wordInfo.mapToPresentation(it.userWord) }
                        val pagedResult = PagedResult(mappedList, result.data.totalSize)
                        totalSize.value = pagedResult.totalSize
                        val oldList = list.value?.data?.toMutableList() ?: mutableListOf()
                        if (favWordsOffset == 0) {
                            oldList.clear()
                        }
                        oldList.addAll(pagedResult.list)
                        list.value = Data(
                            DataState.SUCCESS,
                            oldList,
                            null
                        )
                        favWordsOffset += pagedResult.list.size
                    }
                    is Result.Error -> {
                        list.value = Data(
                            DataState.ERROR,
                            list.value?.data,
                            result.exception.message
                        )
                    }
                }
            }
        }
    }

    private fun loadNextPage() {
        launch(UI) {
            if (list.value?.dataState != DataState.LOADING) {
                list.value = Data(DataState.LOADING, list.value?.data, null)
                pagingChannel.send(
                    ShowFavoriteWordsUseCase.Parameter(
                        favWordsOffset,
                        sortingType.value
                    )
                )
            }
        }
    }

    fun onItemSelected(position: Int) {
        currentSelectedPosition.value = position
    }

    fun onSortSelected(sortingOption: SortingOption) {
        if (sortingOption != sortingType.value || sortingOption == SortingOption.RANDOMLY)
            sortingType.value = sortingOption
    }

    fun onItemDeleteClicked(wordDetails: UserWordInfoPresentation) {
        deleteWordJob = launch(UI) {
            val oldFavMeanings = wordDetails.meanings.map { it.definitionId }
            val result = removeMeaningFromFavoritesUseCase.execute(
                RemoveMeaningFromFavoritesUseCase.Parameter(wordDetails.word, oldFavMeanings)
            )
            when (result) {
                is Result.Success -> {
                    val oldList = list.value?.data?.toMutableList() ?: mutableListOf()
                    val position = oldList.indexOf(wordDetails)
                    oldList.remove(wordDetails)
                    totalSize.value = totalSize.value?.minus(1)
                    list.value = Data(DataState.SUCCESS, oldList, null)
                    val wordInfo = DeletedWordInfo(wordDetails, oldFavMeanings, position)
                    deletedWordInfo.value = Data(DataState.SUCCESS, wordInfo)
                }
                is Result.Error -> {
                    deletedWordInfo.value = Data(DataState.ERROR, null, result.exception.message)
                }
            }
        }
    }

    fun onUndoDeletionClicked(deletedWordInfo: DeletedWordInfo) {
        deleteWordJob = launch(UI) {
            val parameter = AddMeaningToFavoritesUseCase.Parameter(
                deletedWordInfo.wordDetails.word,
                deletedWordInfo.oldFavMeanings
            )
            val result = addMeaningToFavoritesUseCase.execute(parameter)
            when (result) {
                is Result.Success -> {
                    val oldList = list.value?.data?.toMutableList() ?: mutableListOf()
                    oldList.add(deletedWordInfo.oldPosition, deletedWordInfo.wordDetails)
                    totalSize.value = totalSize.value?.plus(1)
                    list.value = Data(DataState.SUCCESS, oldList, null)
                }
                is Result.Error -> {
                    Log.e(LearnWordsViewModel::class.java.name, result.exception.message)
                }
            }
        }
    }

    override fun onCleared() {
        loadNextPageJob?.cancel()
        deleteWordJob?.cancel()
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
data class UserWordInfoPresentation(
    val word: String,
    var meanings: List<UserWordMeaningPresentation>
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class UserWordMeaningPresentation(
    val definitionId: String,
    val definitions: List<Definition>,
    var partOfSpeech: String,
    val examples: List<Example>
) : Parcelable

fun WordMeaning.mapToPresentation() =
    UserWordMeaningPresentation(
        definitionId,
        definitions?.map { Definition(it) }
                ?: emptyList(), partOfSpeech,
        examples?.map { Example(it) }
                ?: emptyList()
    )

fun WordInfo.mapToPresentation(userWord: UserWord): UserWordInfoPresentation {
    val meanings =
        meanings.filter {
            userWord.favMeanings.contains(it.definitionId)
        }.map {
            it.mapToPresentation()
        }
    return UserWordInfoPresentation(
        word,
        meanings
    )
}
