package com.mydictionary.data.userwordrepo.datasource

import com.mydictionary.data.userwordrepo.pojo.UserWordDto
import com.mydictionary.domain.DEFAULT_PAGE_SIZE
import com.mydictionary.domain.entity.PagedResult
import com.mydictionary.domain.entity.SortingOption
import kotlinx.coroutines.experimental.channels.ReceiveChannel

interface UserWordsDataSource {
    suspend fun getUserWords(
        offset: Int = 0,
        pageSize: Int = DEFAULT_PAGE_SIZE,
        sortingOption: SortingOption = SortingOption.BY_DATE,
        isFavorite: Boolean = false //when isFavorite==true returns only words with not empty fav meanings
    ): PagedResult<UserWordDto>

    suspend fun getUserWord(wordName: String): ReceiveChannel<UserWordDto?> //each time object updates onNext will be called

    suspend fun addOrUpdateUserWord(userWord: UserWordDto)
}
