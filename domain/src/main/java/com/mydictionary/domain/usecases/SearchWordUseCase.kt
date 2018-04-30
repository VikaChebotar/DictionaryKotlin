package com.mydictionary.domain.usecases

import com.mydictionary.domain.AUTOCOMPLETE_DELAY
import com.mydictionary.domain.MIN_WORD_LENGTH_TO_SEARCH
import com.mydictionary.domain.SEARCH_LIMIT
import com.mydictionary.domain.debounce
import com.mydictionary.domain.entity.Result
import com.mydictionary.domain.repository.WordRepository
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.filter
import kotlinx.coroutines.experimental.channels.produce
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchWordUseCase @Inject constructor(val wordRepository: WordRepository) {

    suspend fun execute(channel: ReceiveChannel<String>) = produce<Result<List<String>>> {
        channel
            .filter { !it.isEmpty() && it.length >= MIN_WORD_LENGTH_TO_SEARCH }
            .debounce(AUTOCOMPLETE_DELAY)
            .consumeEach {
                val result = wordRepository.searchWord(it, SEARCH_LIMIT)
                when (result) {
                    is Result.Success -> send(result)
                    is Result.Error -> send(Result.Success(emptyList()))
                }
            }
    }
}