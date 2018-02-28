package com.mydictionary.data.entity

import com.google.firebase.database.ServerValue

/**
 * Created by Viktoria Chebotar on 08.07.17.
 */
data class UserWord(var word: String = "", var value: UserWordValue = UserWordValue()) {
    data class UserWordValue(var accessTime: Any = ServerValue.TIMESTAMP, val favSenses: List<String> = mutableListOf())
}
