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
    val definitions = mutableListOf<Definition>()
    var partOfSpeech: String? = null
    val examples = mutableListOf<Example>()
    var isFavourite = false
}

//wrapper classes used to distinguish view types in adapter
data class Note(val text: String)

data class Definition(val text: String)

data class Example(val text: String)