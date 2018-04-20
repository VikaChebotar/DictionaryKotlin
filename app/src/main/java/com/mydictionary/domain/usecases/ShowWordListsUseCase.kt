package com.mydictionary.domain.usecases

import com.mydictionary.domain.entity.WordList
import com.mydictionary.domain.repository.WordListRepository
import com.mydictionary.domain.usecases.base.SingleUseCase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShowWordListsUseCase @Inject constructor(val wordListRepository: WordListRepository) :
    SingleUseCase<List<WordList>> {

    override fun execute(): Single<List<WordList>> =
        wordListRepository.getAllWordLists()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}