package com.mydictionary.domain.usecases

import com.mydictionary.domain.entity.DetailWordInfo
import com.mydictionary.domain.entity.Result
import com.mydictionary.domain.entity.UserWord
import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.repository.UserWordRepository
import com.mydictionary.domain.repository.WordRepository
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShowWordInfoUseCase @Inject constructor(
    val wordRepository: WordRepository,
    val userWordRepository: UserWordRepository,
    val userRepository: UserRepository
) {

    suspend fun execute(parameter: String): ReceiveChannel<Result<Response>> =
        produce {
            val detailWordInfoResult = wordRepository.getWordInfo(parameter)
            if (detailWordInfoResult is Result.Success) {
                val userResult = userRepository.getUser()
                if (userResult is Result.Success) {
                    getUserWord(parameter).consumeEach {
                        send(Result.Success(Response(detailWordInfoResult.data, it)))
                    }
                } else send(Result.Success(Response(detailWordInfoResult.data, null)))
            } else send(Result.Error((detailWordInfoResult as Result.Error).exception))
        }

    private suspend fun getUserWord(parameter: String): ReceiveChannel<UserWord?> = produce {
        val userWordChannel = userWordRepository.getUserWord(parameter)
        userWordChannel.consumeEach {
            if (it is Result.Success && it.data == null) {
                val newUserWord = UserWord(parameter)
                val addNewWordResult = userWordRepository.addOrUpdateUserWord(newUserWord)
                if (addNewWordResult is Result.Success) {
                    send(newUserWord)
                } else {
                    send(null)
                }
            } else if (it is Result.Success) {
                send(it.data)
            } else send(null)
        }
    }

    data class Response(val wordInfo: DetailWordInfo, val userWord: UserWord?)
}