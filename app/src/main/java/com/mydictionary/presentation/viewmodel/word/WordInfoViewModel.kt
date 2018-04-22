package com.mydictionary.presentation.viewmodel.word

import android.app.Application
import android.arch.lifecycle.*
import android.content.res.Resources
import android.util.Log
import com.mydictionary.R
import com.mydictionary.domain.usecases.AddMeaningToFavoritesUseCase
import com.mydictionary.domain.usecases.RemoveMeaningFromFavoritesUseCase
import com.mydictionary.domain.usecases.ShowWordInfoUseCase
import com.mydictionary.presentation.viewmodel.Data
import com.mydictionary.presentation.viewmodel.DataState
import com.mydictionary.presentation.DictionaryApp
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class WordInfoViewModel(
        val showWordUseCase: ShowWordInfoUseCase,
        val addToFavoritesUseCase: AddMeaningToFavoritesUseCase,
        val removeFromFavoritesUseCase: RemoveMeaningFromFavoritesUseCase,
        private val wordName: String,
        app: Application
) : AndroidViewModel(app) {
    private val wordInfo = MutableLiveData<Data<ShowWordInfoUseCase.Result>>()
    private val compositeDisposable = CompositeDisposable()
    val wordPresentationDetails: LiveData<Data<WordPresentationDetails>> =
            Transformations.map(wordInfo,
                    {
                        val presentationDetails =
                                mapToPresentation(it.data, getApplication<DictionaryApp>().resources)
                        Data(it.dataState, presentationDetails, it.message)
                    })

    init {
        Log.d("TAG", "word info view model")
        loadWordInfo()
    }

    private fun loadWordInfo() {
        compositeDisposable.add(
                Flowable.just(wordName)
                        .doOnSubscribe {
                            wordInfo.postValue(Data(DataState.LOADING, wordInfo.value?.data, null))
                        }
                        .flatMap { showWordUseCase.execute(wordName) }
                        .subscribe({ it ->
                            wordInfo.value = Data(DataState.SUCCESS, it, null)
                        }, { e ->
                            wordInfo.value = Data(DataState.ERROR, wordInfo.value?.data, e.message)
                        })
        )
    }

    fun onFavoriteClicked(item: WordMeaning) {
        Single.just(item.isFavourite)
                .flatMapCompletable {
                    if (it) {
                        removeFromFavoritesUseCase.execute(RemoveMeaningFromFavoritesUseCase.Parameter(
                                wordName, listOf(item.definitionId)
                        ))
                    } else
                        addToFavoritesUseCase
                                .execute(AddMeaningToFavoritesUseCase.Parameter(wordName,
                                        listOf(item.definitionId)))
                }.subscribe({}, {
                    wordInfo.value = Data(DataState.ERROR, wordInfo.value?.data, it.message)
                })
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}

private fun mapToPresentation(
        wordDetailsResult: ShowWordInfoUseCase.Result?,
        resources: Resources
): WordPresentationDetails? {
    return wordDetailsResult?.let {
        val wordCardsList = mutableListOf<Any>()
        wordCardsList.add(resources.getString(R.string.definitions) ?: "")
        val wordInfo = it.wordInfo
        val userWord = it.userWord
        with(wordInfo) {
            val meanings = meanings.map {
                com.mydictionary.presentation.viewmodel.word.WordMeaning(
                        it.definitionId, it.definitions?.map { Definition(it) } ?: emptyList(),
                        it.partOfSpeech, it.examples?.map { Example(it) } ?: emptyList(),
                        userWord?.favMeanings?.contains(it.definitionId) == true
                )
            }
            wordCardsList.addAll(meanings)
            if (synonyms?.isNotEmpty() == true) {
                wordCardsList.add(resources.getString(R.string.synonyms))
                wordCardsList.add(synonyms!!)
            }
            if (antonyms?.isNotEmpty() == true) {
                wordCardsList.add(resources.getString(R.string.antonyms))
                wordCardsList.add(antonyms!!)
            }
            if (notes?.isNotEmpty() == true) {
                wordCardsList.add(resources.getString(R.string.notes))
                wordCardsList.addAll(notes!!.map { Note(it) })
            }
            WordPresentationDetails(pronunciation, wordCardsList)
        }
    }
}

data class WordPresentationDetails(val pronunciation: String? = null, val contentList: List<Any>)

class WordInfoViewModelFactory(
        private val wordName: String
) :
        ViewModelProvider.Factory {
    @Inject
    lateinit var useCase: ShowWordInfoUseCase
    @Inject
    lateinit var addFavUseCase: AddMeaningToFavoritesUseCase
    @Inject
    lateinit var removeFavUseCase: RemoveMeaningFromFavoritesUseCase
    @Inject
    lateinit var app: DictionaryApp

    init {
        DictionaryApp.component.inject(this)
    }

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            WordInfoViewModel::class.java -> WordInfoViewModel(useCase, addFavUseCase,
                    removeFavUseCase, wordName, app) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class");
        }
    }
}
