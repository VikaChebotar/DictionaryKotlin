package com.mydictionary.domain.repository

import com.mydictionary.domain.entity.*
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface UserRepository {
    fun getUser(): Single<User>
    fun signIn(token: String): Single<User>
    fun signOut(): Completable
}

interface WordRepository {
    fun searchWord(searchPhrase: String, searchLimit: Int): Single<List<String>>
    fun getWordInfo(wordName: String): Single<DetailWordInfo>
    fun getShortWordInfo(wordName: String): Single<ShortWordInfo>
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