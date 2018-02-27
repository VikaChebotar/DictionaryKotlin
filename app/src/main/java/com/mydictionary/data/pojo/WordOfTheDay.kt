package com.mydictionary.data.pojo

import java.util.*

/**
 * Created by Viktoria_Chebotar on 6/12/2017.
 */

open class WordOfTheDay() {
    constructor(word: String, date: Date) : this() {
        this.word = word
        this.date = date
    }

    var word: String? = null
    var date: Date? = null
}