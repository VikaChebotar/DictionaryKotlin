package com.mydictionary.domain.usecases

import com.mydictionary.domain.entity.UserWord
import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.repository.UserWordRepository
import com.mydictionary.domain.usecases.base.CompletableUseCaseWithParameter
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Viktoria Chebotar on 21.04.18.
 */
@Singleton
class AddMeaningToFavoritesUseCase @Inject constructor(
        val userWordRepository: UserWordRepository,
        val userRepository: UserRepository,
        @Named("executor_thread") val executorThread: Scheduler,
        @Named("postExecutionThread") val postExecutionThread: Scheduler
) : CompletableUseCaseWithParameter<AddMeaningToFavoritesUseCase.Parameter> {

    override fun execute(parameter: Parameter) =
            userRepository.getUser()
                    .flatMap {
                        userWordRepository
                                .getUserWord(parameter.word)
                                .take(1)
                                .single(UserWord(parameter.word))
                    }.flatMapCompletable {
                        val meanings = it.favMeanings.toMutableSet()
                        meanings.addAll(parameter.favMeaningIds)
                        userWordRepository.addOrUpdateUserWord(UserWord(it.word, meanings.toList()))
                    }.subscribeOn(executorThread)
                    .observeOn(postExecutionThread)

    data class Parameter(val word: String, val favMeaningIds: List<String>)
}
