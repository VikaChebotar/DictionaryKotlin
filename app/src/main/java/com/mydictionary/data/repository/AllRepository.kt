package com.mydictionary.data.repository

import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.domain.entity.PagedResult
import com.mydictionary.domain.entity.SortingOption
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

interface AllRepository {

    fun isSignedIn(): Single<Boolean>

//    fun getWordInfo(wordName: String): Single<WordDetails>

    fun getHistoryWords(): Flowable<List<String>>

    fun searchWord(searchPhrase: String): Single<List<String>>

    fun setWordFavoriteState(word: WordDetails, favMeanings: List<String>): Single<WordDetails>

    fun getFavoriteWordsInfo(
        offset: Int,
        pageSize: Int,
        sortingOption: SortingOption = SortingOption.BY_DATE
    ): Flowable<PagedResult<WordDetails>>

    fun getFavoriteWords(): Single<List<String>>

}
