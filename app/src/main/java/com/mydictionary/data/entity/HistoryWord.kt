package com.mydictionary.data.entity

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Viktoria Chebotar on 08.07.17.
 */
open class HistoryWord(@PrimaryKey var word: String = "",
                       var isFavorite: Boolean = false,
                       var accessTime: Date? = null) : RealmObject()
