package com.mydictionary.data.repository

import com.mydictionary.data.pojo.PagedResult
import com.mydictionary.data.pojo.WordDetails
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

class WordsRepositoryImpl(val factory: WordsStorageFactory) : WordsRepository {
    private val TAG = WordsRepositoryImpl::class.java.canonicalName

    override fun loginFirebaseUser(googleToken: String?): Single<String> =
            factory.firebaseStorage.loginFirebaseUser(googleToken)


    override fun isSignedIn() = factory.firebaseStorage.isLoggedIn()

    override fun signOut(): Completable = factory.firebaseStorage.logoutFirebaseUser()

    override fun getWordInfo(wordName: String) = factory.oxfordStorage.getFullWordInfo(wordName).flatMap { wordDetails ->
        isSignedIn().flatMap { isSignedIn ->
            if (isSignedIn) {
                factory.firebaseStorage.addWordToHistoryAndGet(wordName).
                        map { userWord ->
                            wordDetails.meanings.forEach {
                                it.isFavourite = userWord.favSenses.contains(it.definitionId) == true
                            }
                            wordDetails
                        }
            } else {
                Single.just(wordDetails)
            }
        }
    }


    override fun getHistoryWords(listener: RepositoryListener<List<String>>) {
        factory.firebaseStorage.getHistoryWords(listener)
    }

    override fun searchWord(searchPhrase: String) = factory.oxfordStorage.searchTheWord(searchPhrase)

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

    override fun getFavoriteWords(offset: Int, pageSize: Int): Flowable<PagedResult<WordDetails>> =
            Single.zip(factory.firebaseStorage.getFavoriteWords(offset, pageSize).
                    concatMap { userWord ->
                        factory.oxfordStorage.getShortWordInfo(userWord.word).map {
                            it.meanings.forEach {
                                it.isFavourite = userWord.favSenses.contains(it.definitionId) == true
                            }
                            it
                        }.toFlowable()
                    }.
                    toList(), factory.firebaseStorage.getFavoriteWordsCount(), BiFunction<List<WordDetails>, Int, PagedResult<WordDetails>> { list, size ->
                PagedResult(list, size)
            }).toFlowable()

    override fun onAppForeground() {
        factory.firebaseStorage.onAppForeground()
    }

    override fun onAppBackground() {
        factory.firebaseStorage.onAppBackground()
    }
}
