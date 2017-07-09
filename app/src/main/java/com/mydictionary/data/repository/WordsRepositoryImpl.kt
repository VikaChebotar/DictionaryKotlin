package com.mydictionary.data.repository

import com.mydictionary.commons.isSameDay
import com.mydictionary.data.entity.HistoryWord
import com.mydictionary.data.entity.SearchResult
import com.mydictionary.data.entity.WordInfo
import java.util.*

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

class WordsRepositoryImpl(val factory: WordsStorageFactory) : WordsRepository {

    override fun getTodayWordInfo(date: Date, listener: WordsRepository.WordSourceListener<WordInfo>) {
        val wordOfTheDay = factory.localStorage.getWordOfTheDay();
        if ((wordOfTheDay != null) && (wordOfTheDay.date!!.isSameDay(Calendar.getInstance().time))) {
            factory.cloudStorage.getWordInfo(wordOfTheDay.word!!, object : WordsRepository.WordSourceListener<WordInfo> {
                override fun onSuccess(t: WordInfo) {
                    t.isFavorite = factory.localStorage.isWordFavorite(t.word)
                    listener.onSuccess(t)
                }

                override fun onError(error: String) {
                    listener.onError(error)
                }
            })
        } else {
            factory.cloudStorage.getRandomWord(object : WordsRepository.WordSourceListener<WordInfo> {
                override fun onSuccess(t: WordInfo) {
                    factory.localStorage.storeWordOfTheDay(t.word, Calendar.getInstance().time)
                    addWordToHistory(t.word)
                    t.isFavorite = factory.localStorage.isWordFavorite(t.word)
                    listener.onSuccess(t)
                }

                override fun onError(error: String) {
                    listener.onError(error)
                }
            })
        }
    }

    override fun getWordInfo(wordName: String, listener: WordsRepository.WordSourceListener<WordInfo>) {
        factory.cloudStorage.getWordInfo(wordName, object : WordsRepository.WordSourceListener<WordInfo> {
            override fun onSuccess(t: WordInfo) {
                addWordToHistory(t.word)
                t.isFavorite = factory.localStorage.isWordFavorite(t.word)
                listener.onSuccess(t)
            }

            override fun onError(error: String) {
                listener.onError(error)
            }
        })
    }

    override fun getHistoryWords(limit: Int, listener: WordsRepository.WordSourceListener<List<String>>) {
        factory.localStorage.getHistoryWords(limit, object : WordsRepository.WordSourceListener<List<HistoryWord>> {
            override fun onSuccess(t: List<HistoryWord>) {
                listener.onSuccess(t.map { it.word })
            }

            override fun onError(error: String) {

            }
        });
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

    override fun addWordToHistory(wordName: String) {
        var historyWord = factory.localStorage.getWordFromHistory(wordName)
        if (historyWord == null) {
            historyWord = HistoryWord(wordName)
        }
        historyWord.accessTime = Calendar.getInstance().time
        factory.localStorage.addWordToHistory(historyWord)
    }

    override fun setWordFavoriteState(wordName: String, isFavorite: Boolean,
                                      listener: WordsRepository.WordSourceListener<Boolean>) {
        val isFavoriteResult = factory.localStorage.setWordFavoriteState(wordName, isFavorite)
        listener.onSuccess(isFavoriteResult)
    }

    override fun getWordFavoriteState(wordName: String) =
            factory.localStorage.isWordFavorite(wordName)

}
