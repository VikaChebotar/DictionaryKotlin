package com.mydictionary.data.entity

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

/**
 * Created by Viktoria_Chebotar on 6/12/2017.
 */

open class WordOfTheDay() : RealmObject() {
    constructor(word: String, date: Date) : this() {
        this.word = word
        this.date = date
    }

    @PrimaryKey @Required var word: String? = null
    @Required var date: Date? = null
}