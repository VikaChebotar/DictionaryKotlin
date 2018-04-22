package com.mydictionary.domain.usecases

import com.mydictionary.domain.MAX_HISTORY_LIMIT
import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.repository.UserWordRepository
import com.mydictionary.domain.usecases.base.SingleUseCase
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ShowUserHistoryUseCase @Inject constructor(
        val userRepository: UserRepository,
        val userWordRepository: UserWordRepository,
        @Named("executor_thread") val executorThread: Scheduler,
        @Named("ui_thread") val uiThread: Scheduler
) :
    SingleUseCase<List<String>> {

    override fun execute(): Single<List<String>> {
        return userRepository.getUser()
            .flatMap {
                userWordRepository.getUserWords(0, MAX_HISTORY_LIMIT)
                    .map {
                        it.list.map { it.word }
                    }
            }
            .onErrorReturn { emptyList() }
            .subscribeOn(executorThread)
            .observeOn(uiThread)
    }
}