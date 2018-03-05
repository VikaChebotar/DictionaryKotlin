package com.mydictionary.data.repository

import com.google.firebase.auth.FirebaseUser
import com.mydictionary.data.pojo.WordDetails

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

interface WordsRepository {
    fun loginFirebaseUser(googleToken: String?, listener: RepositoryListener<String>)

    fun getCurrentUser(): FirebaseUser?

    fun logoutFirebaseUser()

    fun getWordInfo(wordName: String, listener: RepositoryListener<WordDetails>)

    fun getHistoryWords(listener: RepositoryListener<List<String>>)

    fun searchWord(searchPhrase: String, listener: RepositoryListener<List<String>>)

    fun setWordFavoriteState(word: WordDetails, favMeanings: List<String>, listener: RepositoryListener<WordDetails>)

    fun getFavoriteWords(offset: Int, pageSize: Int, listener: RepositoryListener<List<WordDetails>>)
}

interface RepositoryListener<T> {
    fun onSuccess(t: T) {}

    fun onError(error: String) {}
}

open class RepositoryListenerDelegate<T>(listener: RepositoryListener<T>) :
        RepositoryListener<T> by listener