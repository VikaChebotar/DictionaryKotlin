package com.mydictionary.data.entity

/**
 * Created by Viktoria_Chebotar on 6/1/2017.
 */
data class WordInfo(val word: String, val pronunciation: String?) {
    var definitions = mutableListOf<Definition>()
    var examples = mutableListOf<String>()
    var synonyms = mutableListOf<String>()
    var antonyms = mutableListOf<String>()
    var also = mutableListOf<String>()
    var derivation = mutableListOf<String>()
    var typeOf = mutableListOf<String>()
    var hasTypes = mutableListOf<String>()
    var verbGroup = mutableListOf<String>()
    var inRegion = mutableListOf<String>()
    var entails = mutableListOf<String>()
    var similarTo = mutableListOf<String>()
}

data class Definition(val definition: String, val partOfSpeech: String)

class WordInfoResult {
    val definition: String? = null
    val partOfSpeech: String? = null
    val examples: List<String> = listOf()
    val synonyms: List<String> = listOf()
    val antonyms: List<String> = listOf()
    val also: List<String> = listOf()
    val derivation: List<String> = listOf()
    val typeOf: List<String> = listOf()
    val hasTypes: List<String> = listOf()
    val verbGroup: List<String> = listOf()
    val inRegion: List<String> = listOf()
    val entails: List<String> = listOf()
    val similarTo: List<String> = listOf()
}