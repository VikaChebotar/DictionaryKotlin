package com.mydictionary.data.repository

import com.mydictionary.data.entity.WordOfTheDay
import io.realm.Realm
import java.util.*

/**
 * Created by Viktoria_Chebotar on 6/12/2017.
 */
class LocalStorage {
    //TODO where to close?
    private val realm: Realm = Realm.getDefaultInstance()

    fun getWordOfTheDay(): WordOfTheDay? {
        return realm.where(WordOfTheDay::class.java).findFirst();
    }

    fun storeWordOfTheDay(word: String, date: Date) {
        realm.executeTransactionAsync { realm: Realm? ->
            run {
                realm?.delete(WordOfTheDay::class.java)
                realm?.copyToRealm(WordOfTheDay(word, date))
            }
        }
    }

    fun getHistoryWords(): List<String> {
        return listOf("1", "22", "333")
    }
}
