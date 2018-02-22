package com.mydictionary.data.pojo

import com.mydictionary.data.network.dto.WordDetailsResponse

/**
 * Created by Viktoria_Chebotar on 2/22/2018.
 */

class Mapper {
    companion object {
        fun fromDto(dto: WordDetailsResponse): WordDetails {
            val result = dto.results?.elementAtOrNull(0)
            val word = WordDetails(result?.word ?: "")
            val allEntries = mutableListOf<WordDetailsResponse.Entry>()
            result?.lexicalEntries?.forEach { lexicalEntry ->
                allEntries.addAll(lexicalEntry.entries?.onEach { it.lexicalCategory = lexicalEntry.lexicalCategory }.orEmpty())
            }
            allEntries.sortBy { it.homographNumber }
            allEntries.forEach { entry ->
                entry.senses?.forEach {
                    val meaning = WordMeaning(it.id)
                    meaning.partOfSpeech = entry.lexicalCategory
                    meaning.definitions.addAll(it.definitions.orEmpty())
                    it.subsenses?.map { it.definitions }?.filterNotNull()?.forEach { meaning.definitions.addAll(it) }
                    it.examples?.map { it.text }?.filterNotNull()?.forEach { meaning.examples.add(it) }
                    it.subsenses?.map { it.examples }?.forEach {
                        it?.map { it.text }?.filterNotNull()?.forEach { meaning.examples.add(it) }
                    }
                    word.meanings.add(meaning)
                }
            }
            return word
        }
    }
}
