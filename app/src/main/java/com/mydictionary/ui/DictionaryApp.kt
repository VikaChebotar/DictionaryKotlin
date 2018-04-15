package com.mydictionary.ui

import android.app.Application
import android.content.Context
import com.mydictionary.data.repository.WordsRepositoryImpl
import com.mydictionary.data.repository.WordsStorageFactory
import com.mydictionary.ui.presenters.ViewModelFactory
import io.reactivex.internal.functions.Functions
import io.reactivex.plugins.RxJavaPlugins


/**
 * Created by Viktoria_Chebotar on 6/12/2017.
 */

class DictionaryApp : Application() {
    val repository by lazy { WordsRepositoryImpl(WordsStorageFactory.getInstance(this)) }
    val viewModelFactory by lazy { ViewModelFactory(repository) }

    override fun onCreate() {
        super.onCreate()
        RxJavaPlugins.setErrorHandler(Functions.emptyConsumer())
    }

    companion object {
        fun getInstance(context: Context): DictionaryApp {
            return context.applicationContext as DictionaryApp
        }
    }
}
