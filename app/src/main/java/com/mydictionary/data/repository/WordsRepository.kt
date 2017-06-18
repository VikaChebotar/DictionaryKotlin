package com.mydictionary.data.repository

import com.mydictionary.data.entity.WordInfo
import java.util.*

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

interface WordsRepository {

    interface WordSourceListener<T>{
        fun onSuccess(t:T)

        fun onError(error: String)
    }

    fun getTodayWord(date: Date, listener: WordSourceListener<WordInfo>);
}
