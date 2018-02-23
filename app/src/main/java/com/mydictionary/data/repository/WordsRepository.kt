package com.mydictionary.data.repository

import com.mydictionary.data.pojo.WordDetails

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

interface WordsRepository {
//    fun getTodayWordInfo(date: Date, listener: RepositoryListener<WordDetails>)

    fun getWordInfo(wordName: String, listener: RepositoryListener<WordDetails>)

//    fun getHistoryWords(limit: Int, listener: RepositoryListener<List<String>>)

    fun searchWord(searchPhrase: String, listener: RepositoryListener<List<String>>)
//
//    fun addWordToHistory(wordName: String)
//
//    fun setWordFavoriteState(wordName: String, isFavorite: Boolean, listener: RepositoryListener<Boolean>)
//
//    fun getWordFavoriteState(wordName: String): Boolean
}

interface RepositoryListener<T> {
    fun onSuccess(t: T)

    fun onError(error: String)
}

open class RepositoryListenerDelegate<T>(listener: RepositoryListener<T>) :
        RepositoryListener<T> by listener