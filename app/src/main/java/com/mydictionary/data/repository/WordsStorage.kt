package com.mydictionary.data.repository

import com.mydictionary.data.entity.WordInfo

/**
 * Created by Viktoria_Chebotar on 6/9/2017.
 */

interface WordsStorage {

    interface WordSourceListener<T>{
        fun onSuccess(t:T?)

        fun onError(error: String?)
    }

    fun getRandomWord(listener: WordSourceListener<WordInfo>);
}
