package com.mydictionary.data.repository

import com.mydictionary.data.pojo.WordDetails

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

interface WordsRepository {

    interface WordSourceListener<T> {
        fun onSuccess(t: T)

        fun onError(error: String)
    }

//    fun getTodayWordInfo(date: Date, listener: WordSourceListener<WordDetails>)

    fun getWordInfo(wordName: String, listener: WordSourceListener<WordDetails>)

    fun getHistoryWords(listener: WordSourceListener<List<String>>)

    fun searchWord(searchPhrase: String, listener: WordSourceListener<List<String>>)
}
