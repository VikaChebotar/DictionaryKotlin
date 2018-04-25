package com.mydictionary.domain.repository

import com.mydictionary.domain.entity.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface UserRepository {
    suspend fun getUser(): Result<User>
    suspend fun signIn(token: String): Result<User>
    suspend fun signOut()
}

interface WordRepository {
    suspend fun searchWord(searchPhrase: String, searchLimit: Int): Result<List<String>>
    suspend fun getWordInfo(wordName: String): Result<DetailWordInfo>
    suspend fun getShortWordInfo(wordName: String): Result<ShortWordInfo>
}

interface UserWordRepository {
    fun getUserWords(
        offset: Int = 0,
        pageSize: Int = Int.MAX_VALUE, //default value - get all
        sortingOption: SortingOption = SortingOption.BY_DATE,
        isFavorite: Boolean = false //when isFavorite==true returns only words with not empty fav meanings
    ): Single<PagedResult<UserWord>>

    fun getUserWord(wordName: String): Flowable<UserWord> //each time object updates onNext will be called

    fun addOrUpdateUserWord(userWord: UserWord): Completable
}

interface WordListRepository {
    fun getAllWordLists(): Single<List<WordList>>
    fun getWordList(wordListName: String): Single<WordList>
}