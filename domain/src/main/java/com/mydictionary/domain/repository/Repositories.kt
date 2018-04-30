package com.mydictionary.domain.repository

import com.mydictionary.domain.DEFAULT_PAGE_SIZE
import com.mydictionary.domain.entity.*
import kotlinx.coroutines.experimental.channels.ReceiveChannel

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
    suspend fun getUserWords(offset: Int = 0,
                             pageSize: Int = DEFAULT_PAGE_SIZE,
                             sortingOption: SortingOption = SortingOption.BY_DATE,
                             isFavorite: Boolean = false //when isFavorite==true returns only words with not empty fav meanings
    ): Result<PagedResult<UserWord>>

    suspend fun getUserWord(wordName: String): ReceiveChannel<Result<UserWord>> //each time object updates onNext will be called

    suspend fun addOrUpdateUserWord(userWord: UserWord): Result<Nothing?>
}

interface WordListRepository {
    suspend fun getAllWordLists(): Result<List<WordList>>
    suspend fun getWordList(wordListName: String): Result<WordList>
}