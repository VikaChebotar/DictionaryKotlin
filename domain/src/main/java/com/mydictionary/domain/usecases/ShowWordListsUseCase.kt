package com.mydictionary.domain.usecases

import com.mydictionary.domain.entity.WordList
import com.mydictionary.domain.repository.WordListRepository
import com.mydictionary.domain.usecases.base.SingleUseCase
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ShowWordListsUseCase @Inject constructor(val wordListRepository: WordListRepository,
                                               @Named("executor_thread") val executorThread: Scheduler,
                                               @Named("ui_thread") val uiThread: Scheduler) :
    SingleUseCase<List<WordList>> {

    override fun execute(): Single<List<WordList>> =
        wordListRepository.getAllWordLists()
            .subscribeOn(executorThread)
            .observeOn(uiThread)
}