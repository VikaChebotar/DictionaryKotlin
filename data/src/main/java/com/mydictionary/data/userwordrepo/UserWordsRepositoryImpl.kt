package com.mydictionary.data.userwordrepo

import com.mydictionary.data.userwordrepo.datasource.UserWordsDataSource
import com.mydictionary.data.userwordrepo.pojo.UserWordDto
import com.mydictionary.domain.entity.*
import com.mydictionary.domain.repository.UserWordRepository
import kotlinx.coroutines.experimental.channels.produce
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserWordsRepositoryImpl @Inject constructor(
        val dataSource: UserWordsDataSource,
        val mapper: UserWordMapper
) : UserWordRepository {

    override suspend fun getUserWords(offset: Int, pageSize: Int, sortingOption: SortingOption,
                                      isFavorite: Boolean)
            : Result<PagedResult<UserWord>> {
        return try {
            val pagedResult = dataSource.getUserWords(offset, pageSize, sortingOption, isFavorite)
            val mappedList = pagedResult.list.map { it -> mapper.mapUserWord(it) }
            Result.Success(PagedResult(mappedList, pagedResult.totalSize))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getUserWord(wordName: String) = produce<Result<WordInfo>> {

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

//    override fun getUserWord(word: String): Observable<UserWord> =
//        dataSource
//            .getUserWord(word)
//            .map { mapper.mapUserWord(it) }
//
//
//    override fun addOrUpdateUserWord(userWord: UserWord) =
//        Single.just(userWord)
//            .map { UserWordDto(userWord.word, userWord.favMeanings) }
//            .flatMapCompletable { dataSource.addOrUpdateUserWord(it) }

}