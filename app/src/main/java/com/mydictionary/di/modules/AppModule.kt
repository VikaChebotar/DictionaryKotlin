package com.mydictionary.di.modules

import com.mydictionary.presentation.DictionaryApp
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Named
import javax.inject.Singleton


@Module
class AppModule(private val app: DictionaryApp) {

    @Provides
    @Singleton
    fun providesApplication(): DictionaryApp = app

    @Provides
    @Named("executor_thread")
    fun provideExecutorThread(): Scheduler {
        return Schedulers.io()
    }

    @Provides
    @Named("postExecutionThread")
    fun providepostExecutionThread(): Scheduler {
        return AndroidSchedulers.mainThread()
    }
}