package com.mydictionary.domain.usecases

import com.mydictionary.domain.FAV_WORD_PAGE_SIZE
import com.mydictionary.domain.entity.*
import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.repository.UserWordRepository
import com.mydictionary.domain.repository.WordRepository
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
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
                        val pagedResult = getWordInfos(result.data)
                        send(Result.Success(pagedResult))
                    }
                }
            }
        }

    private suspend fun getWordInfos(userWordPagedResult: PagedResult<UserWord>): PagedResult<Response> {
        val list = mutableListOf<Response>()
        val jobList = mutableListOf<Deferred<Result<ShortWordInfo>>>()
        val resultList = mutableListOf<Result<ShortWordInfo>>()
        userWordPagedResult.list.forEach { userWord ->
            jobList += async { wordRepository.getShortWordInfo(userWord.word) }
        }
        //execute in parallel
        jobList.forEach { resultList += it.await() }
        resultList.forEach { wordInfoResult ->
            when (wordInfoResult) {
                is Result.Error -> {
                }
                is Result.Success -> {
                    val userWord =
                        userWordPagedResult.list.find { it.word == wordInfoResult.data.word }
                    userWord?.let { list.add(Response(wordInfoResult.data, it)) }
                }
            }
        }
        return PagedResult(list, userWordPagedResult.totalSize)
    }

    data class Parameter(val offset: Int, val sortingOption: SortingOption)

    data class Response(val wordInfo: ShortWordInfo, val userWord: UserWord)
}
