package com.mydictionary.data.repository

import com.mydictionary.data.pojo.SearchResult
import com.mydictionary.data.pojo.WordDetails

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

class WordsRepositoryImpl(val factory: WordsStorageFactory) : WordsRepository {

//    override fun getTodayWordInfo(date: Date, listener: WordsRepository.WordSourceListener<WordDetails>) {
//        val wordOfTheDay = factory.localStorage.getWordOfTheDay();
//        if ((wordOfTheDay != null) && (wordOfTheDay.date!!.isSameDay(Calendar.getInstance().time))) {
//            factory.cloudStorage.getWordInfo(wordOfTheDay.word!!, listener)
//        } else {
//            factory.cloudStorage.getRandomWord(object : WordsRepository.WordSourceListener<WordDetails> {
//                override fun onSuccess(t: WordDetails) {
//                    listener.onSuccess(t)
//                    factory.localStorage.storeWordOfTheDay(t.word, Calendar.getInstance().time)
//                }
//
//                override fun onError(error: String) {
//                    listener.onError(error)
//                }
//            })
//        }
//    }

    override fun getWordInfo(wordName: String, listener: WordsRepository.WordSourceListener<WordDetails>) {
        factory.cloudStorage.getWordInfo(wordName, object : WordsRepository.WordSourceListener<WordDetails> {
            override fun onSuccess(t: WordDetails) {
                listener.onSuccess(t)
                //todo add word to history or update access time
            }

            override fun onError(error: String) {
                listener.onError(error)
            }
        })
    }

    override fun getHistoryWords(listener: WordsRepository.WordSourceListener<List<String>>) {
        val result = factory.localStorage.getHistoryWords();
        listener.onSuccess(result)
        //todo implement properly
    }

    override fun searchWord(searchPhrase: String, listener: WordsRepository.WordSourceListener<List<String>>) {
        factory.cloudStorage.searchTheWord(searchPhrase, object : WordsRepository.WordSourceListener<SearchResult> {
            override fun onSuccess(t: SearchResult) {
                listener.onSuccess(t.searchResults)
            }

            override fun onError(error: String) {
                listener.onError(error)
            }
        })
    }
}
