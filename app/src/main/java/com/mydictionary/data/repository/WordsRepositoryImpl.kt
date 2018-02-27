package com.mydictionary.data.repository

import com.mydictionary.data.entity.HistoryWord
import com.mydictionary.data.pojo.SearchResult
import com.mydictionary.data.pojo.WordDetails
import java.util.*

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

class WordsRepositoryImpl(val factory: WordsStorageFactory) : WordsRepository {

//    override fun getTodayWordDetails(date: Date, listener: RepositoryListener<WordDetails>) {
//        val wordOfTheDay = factory.firebaseStorage.getWordOfTheDay();
//        if ((wordOfTheDay != null) && (wordOfTheDay.date!!.isSameDay(Calendar.getInstance().time))) {
//            factory.oxfordStorage.getWordDetails(wordOfTheDay.word!!,
//                    object : RepositoryListenerDelegate<WordDetails>(listener) {
//                        override fun onSuccess(t: WordDetails) {
//                            t.isFavorite = factory.firebaseStorage.isWordFavorite(t.word)
//                            super.onSuccess(t)
//                        }
//                    })
//        } else {
//            factory.oxfordStorage.getRandomWord(object : RepositoryListenerDelegate<WordDetails>(listener) {
//                override fun onSuccess(t: WordDetails) {
//                    factory.firebaseStorage.storeWordOfTheDay(t.word, Calendar.getInstance().time)
//                    addWordToHistory(t.word)
//                    t.isFavorite = factory.firebaseStorage.isWordFavorite(t.word)
//                    super.onSuccess(t)
//                }
//            })
//        }
//    }

    override fun getWordInfo(wordName: String, listener: RepositoryListener<WordDetails>) {
        factory.oxfordStorage.getWordInfo(wordName,
                object : RepositoryListenerDelegate<WordDetails>(listener) {
                    override fun onSuccess(t: WordDetails) {
                        addWordToHistory(t.word)
                        // t.isFavorite = factory.firebaseStorage.isWordFavorite(t.word)
                        super.onSuccess(t)
                    }
                })
    }

    override fun getHistoryWords(limit: Int, listener: RepositoryListener<List<String>>) {
        factory.firebaseStorage.getHistoryWords(limit, object : RepositoryListener<List<HistoryWord>> {
            override fun onSuccess(t: List<HistoryWord>) {
                listener.onSuccess(t.map { it.word })
            }

            override fun onError(error: String) {

            }
        });
    }

    override fun searchWord(searchPhrase: String, listener: RepositoryListener<List<String>>) {
        factory.oxfordStorage.searchTheWord(searchPhrase, object : RepositoryListener<SearchResult> {
            override fun onSuccess(t: SearchResult) {
                listener.onSuccess(t.searchResults)
            }

            override fun onError(error: String) {
                listener.onError(error)
            }
        })
    }

    private fun addWordToHistory(wordName: String) {
//        var historyWord = factory.firebaseStorage.getWordFromHistory(wordName)
//        if (historyWord == null) {
//            historyWord = HistoryWord(wordName)
//        }
        val historyWord = HistoryWord(wordName)
        historyWord.accessTime = Calendar.getInstance().time
        factory.firebaseStorage.addWordToHistory(historyWord)
    }

//    override fun setWordFavoriteState(wordName: String, isFavorite: Boolean,
//                                      listener: RepositoryListener<Boolean>) {
//        val isFavoriteResult = factory.firebaseStorage.setWordFavoriteState(wordName, isFavorite)
//        listener.onSuccess(isFavoriteResult)
//    }
//
//    override fun getWordFavoriteState(wordName: String) =
//            factory.firebaseStorage.isWordFavorite(wordName)

}
