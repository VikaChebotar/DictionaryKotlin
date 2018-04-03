package com.mydictionary.ui

import android.app.Application
import android.content.Context
import com.mydictionary.data.repository.WordsRepository
import com.mydictionary.data.repository.WordsRepositoryImpl
import com.mydictionary.data.repository.WordsStorageFactory

/**
 * Created by Viktoria_Chebotar on 6/12/2017.
 */

class DictionaryApp : Application() {
    lateinit var repository: WordsRepository;

    override fun onCreate() {
        super.onCreate()
        repository = WordsRepositoryImpl(WordsStorageFactory.getInstance(this))
    }

    companion object {
        fun getInstance(context: Context): DictionaryApp {
            return context.applicationContext as DictionaryApp
        }
    }
}
