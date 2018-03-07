package com.mydictionary.ui

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
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
        registerActivityLifecycleCallbacks(AppLifecycleTracker())
    }

    companion object {

        fun getInstance(context: Context): DictionaryApp {
            return context.applicationContext as DictionaryApp
        }
    }

    inner class AppLifecycleTracker : Application.ActivityLifecycleCallbacks {
        private var numStarted = 0

        override fun onActivityStarted(activity: Activity?) {
            if (numStarted == 0) {
                repository.onAppForeground()
            }
            numStarted++
        }

        override fun onActivityStopped(activity: Activity?) {
            numStarted--
            if (numStarted == 0) {
                repository.onAppBackground()
            }
        }

        override fun onActivityPaused(activity: Activity?) {

        }

        override fun onActivityResumed(activity: Activity?) {

        }

        override fun onActivityDestroyed(activity: Activity?) {

        }

        override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {

        }

        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        }
    }
}
