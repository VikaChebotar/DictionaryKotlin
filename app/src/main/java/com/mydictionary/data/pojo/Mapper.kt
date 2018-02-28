package com.mydictionary.data.pojo

import com.mydictionary.commons.Constants.Companion.TOP_RELATED_WORDS_LIMIT
import com.mydictionary.data.network.dto.RelatedWordsResponse
import com.mydictionary.data.network.dto.WordDetailsResponse

/**
 * Created by Viktoria_Chebotar on 2/22/2018.
 */

class Mapper {
    companion object {
        fun fromDto(wordDetailsResponse: WordDetailsResponse?, relatedWordsResponse: RelatedWordsResponse?): WordDetails {
            val result = wordDetailsResponse?.results?.elementAtOrNull(0)
            val word = WordDetails(result?.word ?: "")
            val allEntries = mutableListOf<WordDetailsResponse.Entry>()
            val pronunciations = mutableSetOf<String>()
            val notesList = mutableSetOf<Note>()
            val meanings = mutableListOf<WordMeaning>()
            val synonyms = mutableSetOf<String>()
            val antonyms = mutableSetOf<String>()
            result?.lexicalEntries?.forEach { lexicalEntry ->
                allEntries.addAll(lexicalEntry.entries?.onEach { it.lexicalCategory = lexicalEntry.lexicalCategory }.orEmpty())
                pronunciations.addAll(lexicalEntry.pronunciations?.map { it.phoneticSpelling }?.filterNotNull()?.asIterable() ?: emptySet())
            }
            word.pronunciation = pronunciations.elementAtOrNull(0)
            allEntries.sortBy { it.homographNumber }
            allEntries.forEach { entry ->
                entry.notes?.forEach { notesList.add(Note(it.text, it.type)) }
                entry.senses?.filter { it.definitions?.isNotEmpty() == true }?.forEach {
                    val meaning = WordMeaning(it.id)
                    meaning.partOfSpeech = entry.lexicalCategory
                    meaning.definitions.addAll(it.definitions.orEmpty())
                    it.examples?.map { it.text }?.filterNotNull()?.forEach { meaning.examples.add(it) }
                    meanings.add(meaning)
                }
            }
            val relatedWordEntries = relatedWordsResponse?.results?.flatMap { it.lexicalEntries.orEmpty() }?.
                    flatMap { it.entries.orEmpty() }
            relatedWordEntries?.toMutableList()?.sortBy { it.homographNumber }
            relatedWordEntries?.flatMap { it.senses.orEmpty() }?.forEach {
                synonyms.addAll(it.synonyms?.map { it.text.orEmpty() }.orEmpty())
                antonyms.addAll(it.antonyms?.map { it.text.orEmpty() }.orEmpty())
            }
            word.synonyms = synonyms.take(TOP_RELATED_WORDS_LIMIT)
            word.antonyms = antonyms.take(TOP_RELATED_WORDS_LIMIT)
            word.notes = notesList.toList()
            word.meanings = meanings
            return word
        }
    }
}