package com.mydictionary.data.userwordrepo.datasource

import com.mydictionary.data.userwordrepo.pojo.UserWordDto
import com.mydictionary.domain.entity.PagedResult
import com.mydictionary.domain.entity.SortingOption
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface UserWordsDataSource {
    fun getUserWords(
        offset: Int = 0,
        pageSize: Int = Int.MAX_VALUE, //default value - get all
        sortingOption: SortingOption = SortingOption.BY_DATE,
        isFavorite: Boolean = false //when isFavorite==true returns only words with not empty fav meanings
    ): Single<PagedResult<UserWordDto>>

    fun getUserWord(wordName: String): Observable<UserWordDto> //each time object updates onNext will be called

    fun addOrUpdateUserWord(userWord: UserWordDto): Completable
}
