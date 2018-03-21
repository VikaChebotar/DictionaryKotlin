package com.mydictionary.data.repository

import com.mydictionary.data.pojo.WordDetails
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

interface WordsRepository {
    fun loginFirebaseUser(googleToken: String?): Single<String>

    fun isSignedIn(): Single<Boolean>

    fun signOut(): Completable

    fun getWordInfo(wordName: String): Single<WordDetails>

    fun getHistoryWords(listener: RepositoryListener<List<String>>)

    fun searchWord(searchPhrase: String): Single<List<String>>

    fun setWordFavoriteState(word: WordDetails, favMeanings: List<String>, listener: RepositoryListener<WordDetails>)

    fun getFavoriteWords(offset: Int, pageSize: Int): Flowable<List<WordDetails>>

    fun onAppForeground()

    fun onAppBackground()
}

interface RepositoryListener<T> {
    fun onSuccess(t: T) {}

    fun onError(error: String) {}
}