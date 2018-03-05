package com.mydictionary.data.repository

import android.util.Log
import com.mydictionary.data.entity.UserWord
import com.mydictionary.data.pojo.SearchResult
import com.mydictionary.data.pojo.WordDetails

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

class WordsRepositoryImpl(val factory: WordsStorageFactory) : WordsRepository {
    private val TAG = WordsRepositoryImpl::class.java.canonicalName
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
//                    addWordToHistoryAndGet(t.word)
//                    t.isFavorite = factory.firebaseStorage.isWordFavorite(t.word)
//                    super.onSuccess(t)
//                }
//            })
//        }
//    }

    override fun getWordInfo(wordName: String, listener: RepositoryListener<WordDetails>) {
        factory.oxfordStorage.getWordInfo(wordName, object : RepositoryListener<WordDetails> {
            override fun onSuccess(t: WordDetails) {
                if (factory.firebaseStorage.getCurrentUser() != null) {
                    factory.firebaseStorage.addWordToHistoryAndGet(wordName, object : RepositoryListener<UserWord?> {
                        override fun onSuccess(userWord: UserWord?) {
                            t.meanings.forEach {
                                it.isFavourite = userWord?.value?.favSenses?.contains(it.definitionId) == true
                            }
                            listener.onSuccess(t)
                        }

                        override fun onError(error: String) {
                            listener.onSuccess(t)
                            Log.e(TAG, error)
                        }
                    })
                } else listener.onSuccess(t)
            }

            override fun onError(error: String) {
                listener.onError(error)
            }

        })
    }

    override fun getHistoryWords(listener: RepositoryListener<List<String>>) {
        if (factory.firebaseStorage.getCurrentUser() != null) {
            factory.firebaseStorage.getHistoryWords(listener)
        } else listener.onSuccess(emptyList())
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

//    override fun setWordFavoriteState(wordName: String, isFavorite: Boolean,
//                                      listener: RepositoryListener<Boolean>) {
//        val isFavoriteResult = factory.firebaseStorage.setWordFavoriteState(wordName, isFavorite)
//        listener.onSuccess(isFavoriteResult)
//    }
//
//    override fun getWordFavoriteState(wordName: String) =
//            factory.firebaseStorage.isWordFavorite(wordName)

    override fun setWordFavoriteState(word: WordDetails, favMeanings: List<String>, listener: RepositoryListener<WordDetails>) {
        factory.firebaseStorage.setWordFavoriteState(word.word, favMeanings, object : RepositoryListener<List<String>> {
            override fun onSuccess(t: List<String>) {
                word.meanings.forEach {
                    it.isFavourite = t.contains(it.definitionId)
                }
                listener.onSuccess(word)
            }

            override fun onError(error: String) {
                listener.onError(error)
            }

        })
    }

    override fun loginFirebaseUser(googleToken: String?, listener: RepositoryListener<String>) {
        factory.firebaseStorage.loginFirebaseUser(googleToken, listener)
    }

    override fun getCurrentUser() = factory.firebaseStorage.getCurrentUser()

    override fun logoutFirebaseUser() {
        factory.firebaseStorage.logoutFirebaseUser()
    }
}
