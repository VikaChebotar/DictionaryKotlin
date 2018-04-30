package com.mydictionary.domain.usecases

import com.mydictionary.domain.entity.Result
import com.mydictionary.domain.entity.WordList
import com.mydictionary.domain.repository.WordListRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShowWordListsUseCase @Inject constructor(
    private val wordListRepository: WordListRepository
) {

    suspend fun execute(): Result<List<WordList>> =
        wordListRepository.getAllWordLists()
}