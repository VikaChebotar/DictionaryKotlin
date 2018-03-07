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

    override fun loginFirebaseUser(googleToken: String?, listener: RepositoryListener<String>) {
        factory.firebaseStorage.loginFirebaseUser(googleToken, listener)
    }

    override fun getCurrentUser() = factory.firebaseStorage.getCurrentUser()

    override fun logoutFirebaseUser() {
        factory.firebaseStorage.logoutFirebaseUser()
    }

    override fun getWordInfo(wordName: String, listener: RepositoryListener<WordDetails>) {
        val start = System.currentTimeMillis()
        factory.oxfordStorage.getFullWordInfo(wordName, object : RepositoryListener<WordDetails> {
            override fun onSuccess(t: WordDetails) {
                val getWordTime = System.currentTimeMillis()
                Log.e(TAG, "get word time: " + (getWordTime - start))
                if (factory.firebaseStorage.getCurrentUser() != null) {
                    factory.firebaseStorage.addWordToHistoryAndGet(wordName, object : RepositoryListener<UserWord?> {
                        override fun onSuccess(userWord: UserWord?) {
                            t.meanings.forEach {
                                it.isFavourite = userWord?.favSenses?.contains(it.definitionId) == true
                            }
                            listener.onSuccess(t)
                            Log.e(TAG, "get history time: " + (System.currentTimeMillis() - getWordTime))
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

    override fun getFavoriteWords(offset: Int, pageSize: Int, listener: RepositoryListener<List<WordDetails>>) {
        factory.firebaseStorage.getFavoriteWords(offset, pageSize, object : RepositoryListener<List<UserWord>> {
            override fun onSuccess(favWordsList: List<UserWord>) {
                val favWordNamesList = favWordsList.map { it.word }
                factory.oxfordStorage.getShortWordInfo(favWordNamesList, object : RepositoryListener<List<WordDetails>> {
                    override fun onSuccess(wordsList: List<WordDetails>) {
                        val finalList = favWordsList.map { favWord ->
                            val userWord = wordsList.find { it.word == favWord.word }
                            userWord?.meanings?.forEach {
                                it.isFavourite = favWord.favSenses.contains(it.definitionId) == true
                            }
                            userWord
                        }.filterNotNull()
                        listener.onSuccess(finalList)
                    }

                    override fun onError(error: String) {
                        listener.onError(error)
                    }
                })
            }

            override fun onError(error: String) {
                listener.onError(error)
            }
        })
    }

    override fun onAppForeground() {
        factory.firebaseStorage.onAppForeground()
    }

    override fun onAppBackground() {
        factory.firebaseStorage.onAppBackground()
    }
}
