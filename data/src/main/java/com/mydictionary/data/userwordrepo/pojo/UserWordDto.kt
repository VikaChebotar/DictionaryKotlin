package com.mydictionary.data.userwordrepo.pojo

import com.google.firebase.database.ServerValue

/**
 * Created by Viktoria Chebotar on 08.07.17.
 */
data class UserWordDto(var word: String = "", var favSenses: List<String> = listOf()) {
    var accessTime: Any = ServerValue.TIMESTAMP
    fun updateAccessTime() {
        accessTime = ServerValue.TIMESTAMP
    }
}
