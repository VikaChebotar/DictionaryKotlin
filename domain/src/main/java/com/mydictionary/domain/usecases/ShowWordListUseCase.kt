package com.mydictionary.domain.usecases

import com.mydictionary.domain.entity.Result
import com.mydictionary.domain.entity.WordList
import com.mydictionary.domain.repository.WordListRepository
import javax.inject.Inject


class ShowWordListUseCase @Inject constructor(val wordListRepository: WordListRepository) {
    private var wordList: WordList? = null

    suspend fun execute(parameter: Parameter): Result<WordList> {
        val result = wordList?.let {
            Result.Success(wordList!!)
        } ?: wordListRepository.getWordList(parameter.listName)
        return if (result is Result.Success) {
            val sortedList = result.data.list.toMutableList()
            if (parameter.isReverseOrder)
                sortedList.reverse()
            Result.Success(WordList(result.data.listName, result.data.category, sortedList))
        } else result
    }

    data class Parameter(val listName: String, val isReverseOrder: Boolean)
}