package com.mydictionary.data.repository

import android.content.Context

/**
 * Created by Viktoria_Chebotar on 6/12/2017.
 */

class WordsStorageFactory private constructor(context: Context) {
    val oxfordStorage = OxfordDictionaryStorage(context)
    val firebaseStorage = InternalFirebaseStorage()

    companion object {
        private var instance: WordsStorageFactory? = null
        fun getInstance(context: Context): WordsStorageFactory {
            if (instance == null)
                instance = WordsStorageFactory(context)

            return instance!!
        }
    }

}
