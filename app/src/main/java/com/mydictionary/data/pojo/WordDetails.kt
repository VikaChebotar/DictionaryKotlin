package com.mydictionary.data.pojo

/**
 * Created by Viktoria_Chebotar on 2/22/2018.
 */

data class WordDetails(val word: String) {
    var pronunciation: String? = null
    var notes: String? = null
    var meanings = mutableListOf<WordMeaning>()
    var synonyms = mutableListOf<String>()
    var antonyms = mutableListOf<String>()

}

data class WordMeaning(val definitionId: String) {
    val definitions = mutableListOf<String>()
    var partOfSpeech: String? = null
    val examples = mutableListOf<String>()
}