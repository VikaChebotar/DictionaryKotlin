package com.mydictionary.domain.usecases

import com.mydictionary.domain.FAV_WORD_PAGE_SIZE
import com.mydictionary.domain.entity.PagedResult
import com.mydictionary.domain.entity.ShortWordInfo
import com.mydictionary.domain.entity.SortingOption
import com.mydictionary.domain.entity.UserWord
import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.repository.UserWordRepository
import com.mydictionary.domain.repository.WordRepository
import com.mydictionary.domain.usecases.base.SingleUseCaseWithParameter
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * Created by Viktoria Chebotar on 22.04.18.
 */
@Singleton
class ShowFavoriteWordsUseCase @Inject constructor(
    val userRepository: UserRepository,
    val userWordRepository: UserWordRepository,
    val wordRepository: WordRepository,
    @Named("executor_thread") val executorThread: Scheduler,
    @Named("postExecutionThread") val postExecutionThread: Scheduler
) : SingleUseCaseWithParameter<ShowFavoriteWordsUseCase.Parameter,
        PagedResult<ShowFavoriteWordsUseCase.Result>> {

    override fun execute(parameter: ShowFavoriteWordsUseCase.Parameter): Single<PagedResult<Result>> {
        return userRepository.getUser()
            .flatMap {
                userWordRepository.getUserWords(
                    parameter.offset,
                    FAV_WORD_PAGE_SIZE,
                    parameter.sortingOption,
                    true
                )
            }
            .flatMap { pagedResult ->
                Observable.just(pagedResult.list)
                    .flatMapIterable { it -> it }
                    .concatMap { userWord ->
                        wordRepository.getShortWordInfo(userWord.word)
                            .map { wordInfo ->
                                Result(wordInfo, userWord)
                            }
                            .toObservable()
                    }
                    .toList()
                    .map { PagedResult<Result>(it, pagedResult.totalSize) }
            }
            .subscribeOn(executorThread)
            .observeOn(postExecutionThread)
    }

    data class Parameter(val offset: Int, val sortingOption: SortingOption)

    data class Result(val wordInfo: ShortWordInfo, val userWord: UserWord)
}
