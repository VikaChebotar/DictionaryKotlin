package com.mydictionary.data.wordlistrepo.datasource.remote

import com.mydictionary.data.wordlistrepo.pojo.WordListDto
import io.reactivex.Single

interface WordListDataSource {
    fun getAllWordLists(): Single<List<WordListDto>>
    fun getWordList(wordListName: String): Single<WordListDto>
}
