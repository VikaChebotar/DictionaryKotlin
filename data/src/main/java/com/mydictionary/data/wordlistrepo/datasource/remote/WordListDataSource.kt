package com.mydictionary.data.wordlistrepo.datasource.remote

import com.mydictionary.data.wordlistrepo.pojo.WordListDto

interface WordListDataSource {
    suspend fun getAllWordLists(): List<WordListDto>
    suspend fun getWordList(wordListName: String): WordListDto
}
