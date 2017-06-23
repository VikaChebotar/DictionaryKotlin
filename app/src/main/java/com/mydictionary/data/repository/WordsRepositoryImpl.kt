package com.mydictionary.data.repository

import com.mydictionary.commons.isSameDay
import com.mydictionary.data.entity.WordInfo
import java.util.*

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

class WordsRepositoryImpl(val factory: WordsStorageFactory) : WordsRepository {

    override fun getTodayWord(date: Date, listener: WordsRepository.WordSourceListener<WordInfo>) {
        val wordOfTheDay = factory.localStorage.getWordOfTheDay();
        if ((wordOfTheDay != null) && (wordOfTheDay.date!!.isSameDay(Calendar.getInstance().time))) {
            factory.cloudStorage.getWordInfo(wordOfTheDay.word!!, listener)
        } else {
            factory.cloudStorage.getRandomWord(object : WordsRepository.WordSourceListener<WordInfo> {
                override fun onSuccess(t: WordInfo) {
                    listener.onSuccess(t)
                    t.let { factory.localStorage.storeWordOfTheDay(t.word, Calendar.getInstance().time) }
                }

                override fun onError(error: String) {
                    listener.onError(error)
                }
            })
        }
    }

}
