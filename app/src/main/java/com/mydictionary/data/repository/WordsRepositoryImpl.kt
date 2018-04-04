package com.mydictionary.data.repository

import android.util.Log
import com.mydictionary.data.firebasestorage.dto.UserWord
import com.mydictionary.data.pojo.PagedResult
import com.mydictionary.data.pojo.SortingOption
import com.mydictionary.data.pojo.WordDetails
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

class WordsRepositoryImpl(val factory: WordsStorageFactory) : WordsRepository {
    private val TAG = WordsRepositoryImpl::class.java.simpleName

    override fun loginFirebaseUser(googleToken: String?): Single<String> =
            factory.firebaseStorage.loginFirebaseUser(googleToken)


    override fun isSignedIn() = factory.firebaseStorage.isLoggedIn()

    override fun signOut(): Completable = factory.firebaseStorage.logoutFirebaseUser()

    override fun getWordInfo(wordName: String) =
            factory.oxfordStorage.getFullWordInfo(wordName)
                    .flatMap { wordDetails ->
                        Log.e(TAG, "getWordInfo:" + Thread.currentThread().toString())
                        isSignedIn().flatMap { isSignedIn ->
                            if (isSignedIn) {
                                factory.firebaseStorage.addWordToHistoryAndGet(wordName)
                                        .map { userWord ->
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


    override fun getHistoryWords() = factory.firebaseStorage.getHistoryWords()

    override fun searchWord(searchPhrase: String) = factory.oxfordStorage.searchTheWord(searchPhrase)

    override fun setWordFavoriteState(word: WordDetails, favMeanings: List<String>): Single<WordDetails> =
            factory.firebaseStorage.setWordFavoriteState(word.word, favMeanings).map { userWord: UserWord ->
                word.meanings.forEach {
                    it.isFavourite = userWord.favSenses.contains(it.definitionId)
                }
                word
            }

    override fun getFavoriteWordsInfo(offset: Int, pageSize: Int, sortingOption: SortingOption): Flowable<PagedResult<WordDetails>> =
            Single.zip(factory.firebaseStorage.getFavoriteWords(offset, pageSize, sortingOption).concatMap { userWord ->
                factory.oxfordStorage.getShortWordInfo(userWord.word)
                        .map {
                            it.meanings.forEach {
                                it.isFavourite = userWord.favSenses.contains(it.definitionId) == true
                            }
                            it
                        }
                        .toFlowable()
            }.toList(), factory.firebaseStorage.getFavoriteWordsCount(),
                    BiFunction<List<WordDetails>, Int, PagedResult<WordDetails>> { list, size ->
                        PagedResult(list, size)
                    }).toFlowable()

    override fun getFavoriteWords(): Single<List<String>> =
            factory.firebaseStorage.getFavoriteWords(0, Int.MAX_VALUE, SortingOption.BY_DATE).
                    map { it.word }.toList()

    override fun getAllWordLists() = factory.firebaseStorage.getAllWordLists()

    override fun getWordList(wordListName: String) = factory.firebaseStorage.getWordList(wordListName)
}
