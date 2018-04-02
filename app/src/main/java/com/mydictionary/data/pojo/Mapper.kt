package com.mydictionary.data.pojo

import com.mydictionary.commons.TOP_RELATED_WORDS_LIMIT
import com.mydictionary.data.network.dto.RelatedWordsResponse
import com.mydictionary.data.network.dto.WordDetailsResponse

/**
 * Created by Viktoria_Chebotar on 2/22/2018.
 */

class Mapper {
    companion object {
        fun fromDto(wordDetailsResponse: WordDetailsResponse?): WordDetails {
            val result = wordDetailsResponse?.results?.elementAtOrNull(0)
            val word = WordDetails(result?.word ?: "")
            val allEntries = mutableListOf<WordDetailsResponse.Entry>()
            val pronunciations = mutableSetOf<String>()
            val notesList = mutableSetOf<Note>()
            val meanings = mutableListOf<WordMeaning>()
            result?.lexicalEntries?.forEach { lexicalEntry ->
                allEntries.addAll(lexicalEntry.entries?.onEach { it.lexicalCategory = lexicalEntry.lexicalCategory }.orEmpty())
                pronunciations.addAll(lexicalEntry.pronunciations?.map { it.phoneticSpelling }?.filterNotNull()?.asIterable() ?: emptySet())
            }
            word.pronunciation = pronunciations.elementAtOrNull(0)
            allEntries.sortBy { it.homographNumber }
            allEntries.forEach { entry ->
                entry.notes?.forEach { notesList.add(Note(it.text)) }
                entry.senses?.filter { it.definitions?.isNotEmpty() == true }?.forEach {
                    val meaning = WordMeaning(it.id)
                    meaning.partOfSpeech = entry.lexicalCategory
                    it.definitions?.map { Definition(it) }?.forEach { meaning.definitions.add(it) }
                    it.examples?.map { it.text }?.filterNotNull()?.forEach { meaning.examples.add(Example(it)) }
                    meanings.add(meaning)
                }
            }
            word.notes = notesList.toList()
            word.meanings = meanings
            return word
        }

        fun setRelatedWords(wordDetails: WordDetails, relatedWordsResponse: RelatedWordsResponse?) {
            if (relatedWordsResponse == null) return
            val synonyms = mutableSetOf<String>()
            val antonyms = mutableSetOf<String>()
            val relatedWordEntries = relatedWordsResponse.results?.flatMap { it.lexicalEntries.orEmpty() }?.
                    flatMap { it.entries.orEmpty() }
            relatedWordEntries?.toMutableList()?.sortBy { it.homographNumber }
            relatedWordEntries?.flatMap { it.senses.orEmpty() }?.forEach {
                synonyms.addAll(it.synonyms?.map { it.text.orEmpty() }.orEmpty())
                antonyms.addAll(it.antonyms?.map { it.text.orEmpty() }.orEmpty())
            }
            wordDetails.synonyms = synonyms.take(TOP_RELATED_WORDS_LIMIT)
            wordDetails.antonyms = antonyms.take(TOP_RELATED_WORDS_LIMIT)
        }
    }
}
