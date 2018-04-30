package com.mydictionary.data.userwordrepo

import android.util.Log
import com.mydictionary.data.userwordrepo.datasource.UserWordsDataSource
import com.mydictionary.data.userwordrepo.pojo.UserWordDto
import com.mydictionary.domain.entity.PagedResult
import com.mydictionary.domain.entity.Result
import com.mydictionary.domain.entity.SortingOption
import com.mydictionary.domain.entity.UserWord
import com.mydictionary.domain.repository.UserWordRepository
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserWordsRepositoryImpl @Inject constructor(
    val dataSource: UserWordsDataSource,
    val mapper: UserWordMapper
) : UserWordRepository {

    override suspend fun getUserWords(
        offset: Int, pageSize: Int, sortingOption: SortingOption,
        isFavorite: Boolean
    )
            : Result<PagedResult<UserWord>> {
        return try {
            val pagedResult = dataSource.getUserWords(offset, pageSize, sortingOption, isFavorite)
            val mappedList = pagedResult.list.map { it -> mapper.mapUserWord(it) }
            Result.Success(PagedResult(mappedList, pagedResult.totalSize))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getUserWord(wordName: String) = produce<Result<UserWord?>> {
        val userWordChannel = dataSource.getUserWord(wordName)
        userWordChannel.consumeEach {
            it?.let {
                val userWord = mapper.mapUserWord(it)
                send(Result.Success(userWord))
                Log.e("TAG", "send in repo")
            } ?: send(Result.Success(null))
        }
    }

    override suspend fun addOrUpdateUserWord(userWord: UserWord): Result<Nothing?> {
        val userWordDto = UserWordDto(userWord.word, userWord.favMeanings)
        return try {
            dataSource.addOrUpdateUserWord(userWordDto)
            Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}