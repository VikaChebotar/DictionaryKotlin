package com.mydictionary.data.repository

import com.mydictionary.data.entity.WordInfo
import com.mydictionary.data.repository.WordsStorage.WordSourceListener
import java.util.*

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

interface WordsRepository {
    fun getTodayWord(date: Date, listener: WordSourceListener<WordInfo>);
}
