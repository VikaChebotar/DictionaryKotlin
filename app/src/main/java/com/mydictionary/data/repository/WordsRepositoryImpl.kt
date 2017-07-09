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

    override fun getTodayWordInfo(date: Date, listener: RepositoryListener<WordInfo>) {
        val wordOfTheDay = factory.localStorage.getWordOfTheDay();
        if ((wordOfTheDay != null) && (wordOfTheDay.date!!.isSameDay(Calendar.getInstance().time))) {
            factory.cloudStorage.getWordInfo(wordOfTheDay.word!!,
                    object : RepositoryListenerDelegate<WordInfo>(listener) {
                        override fun onSuccess(t: WordInfo) {
                            t.isFavorite = factory.localStorage.isWordFavorite(t.word)
                            super.onSuccess(t)
                        }
                    })
        } else {
            factory.cloudStorage.getRandomWord(object : RepositoryListenerDelegate<WordInfo>(listener) {
                override fun onSuccess(t: WordInfo) {
                    factory.localStorage.storeWordOfTheDay(t.word, Calendar.getInstance().time)
                    addWordToHistory(t.word)
                    t.isFavorite = factory.localStorage.isWordFavorite(t.word)
                    super.onSuccess(t)
                }
            })
        }
    }

    override fun getWordInfo(wordName: String, listener: RepositoryListener<WordInfo>) {
        factory.cloudStorage.getWordInfo(wordName,
                object : RepositoryListenerDelegate<WordInfo>(listener) {
                    override fun onSuccess(t: WordInfo) {
                        addWordToHistory(t.word)
                        t.isFavorite = factory.localStorage.isWordFavorite(t.word)
                        super.onSuccess(t)
                    }
                })
    }

    override fun getHistoryWords(limit: Int, listener: RepositoryListener<List<String>>) {
        factory.localStorage.getHistoryWords(limit, object : RepositoryListener<List<HistoryWord>> {
            override fun onSuccess(t: List<HistoryWord>) {
                listener.onSuccess(t.map { it.word })
            }

            override fun onError(error: String) {

            }
        });
    }

    override fun searchWord(searchPhrase: String, listener: RepositoryListener<List<String>>) {
        factory.cloudStorage.searchTheWord(searchPhrase, object : RepositoryListener<SearchResult> {
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
                                      listener: RepositoryListener<Boolean>) {
        val isFavoriteResult = factory.localStorage.setWordFavoriteState(wordName, isFavorite)
        listener.onSuccess(isFavoriteResult)
    }

    override fun getWordFavoriteState(wordName: String) =
            factory.localStorage.isWordFavorite(wordName)

}
