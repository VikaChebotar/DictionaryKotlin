package com.mydictionary.domain.repository

import com.mydictionary.domain.entity.*
import io.reactivex.Completable
import io.reactivex.Observable
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
    fun getUserWords(): Single<PagedResult<UserWord>>

    fun getUserWord(wordName: String): Observable<UserWord> //each time object updates onNext will be called

    fun addOrUpdateUserWord(userWord: UserWord): Completable
}

interface WordListRepository {
    suspend fun getAllWordLists(): Result<List<WordList>>
    suspend fun getWordList(wordListName: String): Result<WordList>
}