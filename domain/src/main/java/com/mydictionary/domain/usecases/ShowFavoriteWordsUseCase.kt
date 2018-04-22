package com.mydictionary.domain.usecases

import com.mydictionary.domain.FAV_WORD_PAGE_SIZE
import com.mydictionary.domain.entity.PagedResult
import com.mydictionary.domain.entity.ShortWordInfo
import com.mydictionary.domain.entity.SortingOption
import com.mydictionary.domain.entity.UserWord
import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.repository.UserWordRepository
import com.mydictionary.domain.repository.WordRepository
import com.mydictionary.domain.usecases.base.UseCaseWithParameter
import io.reactivex.Flowable
import io.reactivex.Scheduler
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
        @Named("ui_thread") val uiThread: Scheduler
) : UseCaseWithParameter<ShowFavoriteWordsUseCase.Parameter,
        PagedResult<ShowFavoriteWordsUseCase.Result>> {

    override fun execute(parameter: ShowFavoriteWordsUseCase.Parameter): Flowable<PagedResult<Result>> {
        return userRepository.getUser().toFlowable()
                .flatMap {
                    userWordRepository.getUserWords(parameter.offset, FAV_WORD_PAGE_SIZE, parameter.sortingOption, true)
                            .toFlowable()
                }
                .concatMap { pagedResult ->
                    Flowable.just(pagedResult.list)
                            .concatMapIterable { pagedResult ->
                                pagedResult
                            }
                            .concatMap { userWord ->
                                wordRepository.getShortWordInfo(userWord.word)
                                        .map { wordInfo ->
                                            Result(wordInfo, userWord)
                                        }
                                        .toFlowable()
                            }
                            .toList()
                            .map { PagedResult<Result>(it, pagedResult.totalSize) }
                            .toFlowable()
                }
                .subscribeOn(executorThread)
                .observeOn(uiThread)
    }

    data class Parameter(val offset: Int, val sortingOption: SortingOption)

    data class Result(val wordInfo: ShortWordInfo, val userWord: UserWord)
}
