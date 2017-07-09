package com.mydictionary.data.repository

import com.mydictionary.data.entity.WordInfo
import java.util.*

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

interface WordsRepository {

    interface WordSourceListener<T> {
        fun onSuccess(t: T)

        fun onError(error: String)
    }

    fun getTodayWordInfo(date: Date, listener: WordSourceListener<WordInfo>)

    fun getWordInfo(wordName: String, listener: WordSourceListener<WordInfo>)

    fun getHistoryWords(limit: Int, listener: WordSourceListener<List<String>>)

    fun searchWord(searchPhrase: String, listener: WordSourceListener<List<String>>)

    fun addWordToHistory(wordName: String)

    fun setWordFavoriteState(wordName: String, isFavorite: Boolean, listener: WordSourceListener<Boolean>)

    fun getWordFavoriteState(wordName: String): Boolean
}
