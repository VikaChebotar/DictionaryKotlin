package com.mydictionary.data.entity

import com.google.firebase.database.ServerValue

/**
 * Created by Viktoria Chebotar on 08.07.17.
 */
data class UserWord(var word: String = "", var accessTime: Any = ServerValue.TIMESTAMP, var favSenses: List<String> = listOf()) {
    fun updateAccessTime() {
        accessTime = ServerValue.TIMESTAMP
    }
}
