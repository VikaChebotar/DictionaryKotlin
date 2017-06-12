package com.mydictionary.data.repository

import com.mydictionary.data.entity.WordInfo
import com.mydictionary.data.repository.WordsStorage.WordSourceListener
import java.util.*

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

class WordsRepositoryImpl(val factory: WordsStorageFactory) : WordsRepository {

    override fun getTodayWord(date: Date, listener: WordSourceListener<WordInfo>) {
        factory.cloudStorage.getRandomWord(listener)
    }

}
