package com.mydictionary.domain.usecases

import com.mydictionary.domain.entity.WordList
import com.mydictionary.domain.repository.WordListRepository
import com.mydictionary.domain.usecases.base.SingleUseCaseWithParameter
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class ShowWordListUseCase @Inject constructor(val wordListRepository: WordListRepository) :
    SingleUseCaseWithParameter<ShowWordListUseCase.Parameter, WordList> {
    private var wordList: WordList? = null

    override fun execute(parameter: Parameter): Single<WordList> =
        Single.just(parameter)
            .flatMap {
                wordList?.let {
                    Single.just(wordList!!)
                } ?: wordListRepository.getWordList(parameter.listName)
            }
            .map {
                val sortedList = it.list.toMutableList()
                if (parameter.isReverseOrder)
                    sortedList.reverse()
                WordList(it.listName, it.category, sortedList)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())


    data class Parameter(val listName: String, val isReverseOrder: Boolean)
}