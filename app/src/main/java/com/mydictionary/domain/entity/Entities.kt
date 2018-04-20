package com.mydictionary.domain.entity

/**
 * User entity
 */
data class User(val id: String, val email: String)

/**
 * User word entity
 */
data class UserWord(
    val word: String,
    val accessTime: String,
    val favMeanings: List<String> = emptyList()
)

/**
 * Word list entity
 */
data class WordList(val listName: String, val category: String, val list: List<String>)

/**
 * Word Info entity (can be short and detail, data classes can't extend each other)
 */
data class DetailWordInfo(
    override val word: String,
    override val meanings: List<WordMeaning>,
    override val pronunciation: String?,
    val notes: List<String>?,
    val synonyms: List<String>?,
    val antonyms: List<String>?
) : WordInfo()

data class ShortWordInfo(
    override val word: String,
    override val meanings: List<WordMeaning>,
    override val pronunciation: String?
) : WordInfo()

data class WordMeaning(
    val definitionId: String,
    val definitions: List<String>?,
    val partOfSpeech: String,
    val examples: List<String>?
)

abstract class WordInfo {
    abstract val word: String
    abstract val meanings: List<WordMeaning>
    abstract val pronunciation: String?
}
