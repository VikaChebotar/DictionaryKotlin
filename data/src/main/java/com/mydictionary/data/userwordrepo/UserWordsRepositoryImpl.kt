package com.mydictionary.data.userwordrepo

import com.mydictionary.data.userwordrepo.datasource.UserWordsDataSource
import com.mydictionary.data.userwordrepo.pojo.UserWordDto
import com.mydictionary.domain.entity.PagedResult
import com.mydictionary.domain.entity.SortingOption
import com.mydictionary.domain.entity.UserWord
import com.mydictionary.domain.repository.UserWordRepository
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserWordsRepositoryImpl @Inject constructor(
    val dataSource: UserWordsDataSource,
    val mapper: UserWordMapper
) :
    UserWordRepository {

    override fun getUserWords(
        offset: Int, pageSize: Int,
        sortingOption: SortingOption, isFavorite: Boolean
    ): Single<PagedResult<UserWord>> = dataSource
        .getUserWords(offset, pageSize, sortingOption, isFavorite)
        .map {
            val mappedList = it.list.map { it -> mapper.mapUserWord(it) }
            PagedResult(mappedList, it.totalSize)
        }

    override fun getUserWord(word: String): Flowable<UserWord> =
        dataSource
            .getUserWord(word)
            .map { mapper.mapUserWord(it) }


    override fun addOrUpdateUserWord(userWord: UserWord) =
        Single.just(userWord)
            .map { UserWordDto(userWord.word, userWord.favMeanings) }
            .flatMapCompletable { dataSource.addOrUpdateUserWord(it) }

}