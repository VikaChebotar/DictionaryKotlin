package com.mydictionary.presentation

import android.app.Application
import com.mydictionary.di.AppComponent
import com.mydictionary.di.DaggerAppComponent
import com.mydictionary.di.modules.AppModule
import io.reactivex.internal.functions.Functions
import io.reactivex.plugins.RxJavaPlugins


/**
 * Created by Viktoria_Chebotar on 6/12/2017.
 */

class DictionaryApp : Application() {
    override fun onCreate() {
        super.onCreate()
        RxJavaPlugins.setErrorHandler(Functions.emptyConsumer())
        initDagger()
    }

    private fun initDagger() {
        component = DaggerAppComponent
            .builder()
            .appModule(AppModule(this))
            .build()
    }

    companion object {
        lateinit var component: AppComponent
    }
}
