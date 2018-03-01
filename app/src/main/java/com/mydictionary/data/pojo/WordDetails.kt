package com.mydictionary.data.pojo

/**
 * Created by Viktoria_Chebotar on 2/22/2018.
 */

data class WordDetails(val word: String) {
    var pronunciation: String? = null
    var notes = listOf<Note>()
    var meanings = listOf<WordMeaning>()
    var synonyms = listOf<String>()
    var antonyms = listOf<String>()

}

data class WordMeaning(val definitionId: String) {
    val definitions = mutableListOf<String>()
    var partOfSpeech: String? = null
    val examples = mutableListOf<String>()
    var isFavourite = false
}

data class Note(val text: String, val type: String?)