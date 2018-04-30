package com.mydictionary.domain.usecases

import com.mydictionary.domain.FAV_WORD_PAGE_SIZE
import com.mydictionary.domain.entity.*
import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.repository.UserWordRepository
import com.mydictionary.domain.repository.WordRepository
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Viktoria Chebotar on 22.04.18.
 */
@Singleton
class ShowFavoriteWordsUseCase @Inject constructor(
    val userRepository: UserRepository,
    val userWordRepository: UserWordRepository,
    val wordRepository: WordRepository
) {

    suspend fun execute(channel: ReceiveChannel<Parameter>) =
        produce<Result<PagedResult<Response>>> {
            val userResult = userRepository.getUser()
            if (userResult is Result.Error) {
                send(userResult)
                return@produce
            }
            channel.consumeEach { parameter ->
                val result = userWordRepository.getUserWords(
                    parameter.offset,
                    FAV_WORD_PAGE_SIZE,
                    parameter.sortingOption,
                    true
                )
                when (result) {
                    is Result.Error -> send(result)
                    is Result.Success -> {
                        getWordInfos(result.data)
                    }
                }
            }
        }

    private suspend fun getWordInfos(pagedResult: PagedResult<UserWord>) {
        
    }

    //        return userRepository.getUser()
//            .flatMap {
//                userWordRepository.getUserWords(
//                    parameter.offset,
//                    FAV_WORD_PAGE_SIZE,
//                    parameter.sortingOption,
//                    true
//                )
//            }
//            .flatMap { pagedResult ->
//                Observable.just(pagedResult.list)
//                    .flatMapIterable { it -> it }
//                    .concatMap { userWord ->
//                        wordRepository.getShortWordInfo(userWord.word)
//                            .map { wordInfo ->
//                                Result(wordInfo, userWord)
//                            }
//                            .toObservable()
//                    }
//                    .toList()
//                    .map { PagedResult<Result>(it, pagedResult.totalSize) }
//            }
    data class Parameter(val offset: Int, val sortingOption: SortingOption)

    data class Response(val wordInfo: ShortWordInfo, val userWord: UserWord)
}
