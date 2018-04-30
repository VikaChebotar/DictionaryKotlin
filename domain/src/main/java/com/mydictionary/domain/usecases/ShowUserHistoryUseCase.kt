package com.mydictionary.domain.usecases

import com.mydictionary.domain.MAX_HISTORY_LIMIT
import com.mydictionary.domain.entity.Result
import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.repository.UserWordRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShowUserHistoryUseCase @Inject constructor(
    val userRepository: UserRepository,
    val userWordRepository: UserWordRepository
) {

    suspend fun execute(): Result<List<String>> {
        val userResult = userRepository.getUser()
        return if (userResult is Result.Success) {
            val pagedResult = userWordRepository.getUserWords(0, MAX_HISTORY_LIMIT)
            if (pagedResult is Result.Success) {
                val data = pagedResult.data
                val list = data.list.map { it.word }
                Result.Success(list)
            } else Result.Error((pagedResult as Result.Error).exception)
        } else {
            Result.Success(emptyList())
        }
    }
}