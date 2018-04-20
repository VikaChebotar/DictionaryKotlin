package com.mydictionary.data.repository

import com.mydictionary.data.firebasestorage.InternalFirebaseStorage
import com.mydictionary.data.firebasestorage.dto.UserWordDto
import com.mydictionary.data.oxfordapi.OxfordDictionaryStorage
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.domain.entity.PagedResult
import com.mydictionary.domain.entity.SortingOption
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */
@Singleton
class AllRepositoryImpl
@Inject constructor(
    val firebaseStorage: InternalFirebaseStorage,
    val oxfordStorage: OxfordDictionaryStorage
) : AllRepository {
    private val TAG = AllRepositoryImpl::class.java.simpleName

    override fun isSignedIn() = firebaseStorage.isLoggedIn()

    override fun getWordInfo(wordName: String) =
        oxfordStorage
            .getFullWordInfo(wordName)
            .flatMap { wordDetails ->
                isSignedIn().flatMap { isSignedIn ->
                    if (isSignedIn) {
                        firebaseStorage.addWordToHistoryAndGet(wordName)
                            .map { userWord ->
                                wordDetails.meanings.forEach {
                                    it.isFavourite = userWord.favSenses.contains(it.definitionId) ==
                                            true
                                }
                                wordDetails
                            }
                    } else {
                        Single.just(wordDetails)
                    }
                }
            }


    override fun getHistoryWords() = firebaseStorage.getHistoryWords()

    override fun searchWord(searchPhrase: String) =
        oxfordStorage.searchTheWord(searchPhrase).singleOrError()

    override fun setWordFavoriteState(
        word: WordDetails,
        favMeanings: List<String>
    ): Single<WordDetails> =
        firebaseStorage.setWordFavoriteState(
            word.word,
            favMeanings
        ).map { userWord: UserWordDto ->
            word.meanings.forEach {
                it.isFavourite = userWord.favSenses.contains(it.definitionId)
            }
            word
        }

    override fun getFavoriteWordsInfo(
        offset: Int,
        pageSize: Int,
        sortingOption: SortingOption
    ): Flowable<PagedResult<WordDetails>> =
        Single.zip(firebaseStorage.getFavoriteWords(
            offset,
            pageSize,
            sortingOption
        )
            .concatMap { userWord ->
                oxfordStorage.getShortWordInfo(userWord.word)
                    .map {
                        it.meanings.forEach {
                            it.isFavourite = userWord.favSenses.contains(it.definitionId) == true
                        }
                        it
                    }
                    .toFlowable()
            }.toList(), firebaseStorage.getFavoriteWordsCount(),
            BiFunction<List<WordDetails>, Int, PagedResult<WordDetails>> { list, size ->
                PagedResult(list, size)
            }).toFlowable()

    override fun getFavoriteWords(): Single<List<String>> =
        firebaseStorage.getFavoriteWords(
            0,
            Int.MAX_VALUE,
            SortingOption.BY_DATE
        ).map { it.word }.toList()

}
