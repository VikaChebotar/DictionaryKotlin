package com.mydictionary.data.wordinforepo.datasource.remote

import com.mydictionary.data.wordinforepo.pojo.RelatedWordsResponse
import com.mydictionary.data.wordinforepo.pojo.WordDetailsResponse
import com.mydictionary.domain.TOP_RELATED_WORDS_LIMIT
import com.mydictionary.domain.entity.DetailWordInfo
import com.mydictionary.domain.entity.ShortWordInfo
import com.mydictionary.domain.entity.WordMeaning
import io.reactivex.functions.BiFunction

/**
 * Created by Viktoria_Chebotar on 2/22/2018.
 */

object WordInfoMapper {
    fun mapToShortWordInfo(wordDetailsResponse: WordDetailsResponse): ShortWordInfo {
        with(wordDetailsResponse) {
            val result = results?.elementAtOrNull(0)

            val allEntries = mutableListOf<WordDetailsResponse.Entry>()
            val pronunciations = mutableSetOf<String>()
            // val notesList = mutableSetOf<String>()
            val meanings = mutableListOf<WordMeaning>()
            result?.lexicalEntries?.forEach { lexicalEntry ->
                allEntries.addAll(lexicalEntry.entries?.onEach {
                    it.lexicalCategory = lexicalEntry.lexicalCategory
                }.orEmpty())
                pronunciations.addAll(
                    lexicalEntry.pronunciations?.map { it.phoneticSpelling }?.filterNotNull()?.asIterable()
                            ?: emptySet()
                )
            }
            val pronunciation = pronunciations.elementAtOrNull(0)
            allEntries.sortBy { it.homographNumber }
            allEntries.forEach { entry ->
                //   entry.notes?.forEach { notesList.add(it.text) }
                entry.senses?.filter { it.definitions?.isNotEmpty() == true }?.forEach {
                    val examples = mutableListOf<String>()
                    it.examples?.map { it.text }?.filterNotNull()
                        ?.forEach { examples.add(it) }
                    val meaning = WordMeaning(
                        it.id, it.definitions, entry.lexicalCategory
                                ?: "", examples
                    )
                    meanings.add(meaning)
                }
            }
            return ShortWordInfo(result?.word ?: "", meanings, pronunciation)
        }
    }

    fun mapToDetailWordInfo(
        wordDetailsResponse: WordDetailsResponse,
        relatedWordsResponse: RelatedWordsResponse?
    )
            : DetailWordInfo {
        val shortWordInfo = mapToShortWordInfo(wordDetailsResponse)
        val notes = mutableSetOf<String>()
        val synonyms = mutableSetOf<String>()
        val antonyms = mutableSetOf<String>()
        val result = wordDetailsResponse?.results?.elementAtOrNull(0)
        val allEntries = mutableListOf<WordDetailsResponse.Entry>()
        result?.lexicalEntries?.forEach { lexicalEntry ->
            allEntries.addAll(lexicalEntry.entries?.onEach {
                it.lexicalCategory = lexicalEntry.lexicalCategory
            }.orEmpty())
        }
        allEntries.forEach { entry ->
            entry.notes?.forEach { notes.add(it.text) }
        }
        val relatedWordEntries =
            relatedWordsResponse?.results?.flatMap { it.lexicalEntries.orEmpty() }
                ?.flatMap { it.entries.orEmpty() }
        relatedWordEntries?.toMutableList()?.sortBy { it.homographNumber }
        relatedWordEntries?.flatMap { it.senses.orEmpty() }?.forEach {
            synonyms.addAll(it.synonyms?.map { it.text.orEmpty() }.orEmpty())
            antonyms.addAll(it.antonyms?.map { it.text.orEmpty() }.orEmpty())
        }
        return DetailWordInfo(
            shortWordInfo.word, shortWordInfo.meanings,
            shortWordInfo.pronunciation, notes.toList(),
            synonyms.take(TOP_RELATED_WORDS_LIMIT), antonyms.take(TOP_RELATED_WORDS_LIMIT)
        )
    }

    fun zipTwoResponseResults() =
        BiFunction<WordDetailsResponse, RelatedWordsResponse, Pair<WordDetailsResponse, RelatedWordsResponse?>>
        { wordDetailsResponse, relatedWordsResponse ->
            Pair<WordDetailsResponse, RelatedWordsResponse?>(
                wordDetailsResponse,
                relatedWordsResponse
            )
        }
}
