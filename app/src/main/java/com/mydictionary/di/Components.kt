package com.mydictionary.di

import com.mydictionary.presentation.DictionaryApp
import com.mydictionary.presentation.viewmodel.mywords.WordListViewModelFactory
import com.mydictionary.presentation.viewmodel.word.WordInfoViewModelFactory
import com.mydictionary.presentation.views.account.AccountActivity
import com.mydictionary.presentation.views.home.HomeActivity
import com.mydictionary.presentation.views.learn.LearnActivity
import com.mydictionary.presentation.views.search.SearchActivity
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(
    modules = [AppModule::class, NetworkModule::class, FirebaseModule::class,
        ViewModelModule::class, UseCasesModule::class, DataModule::class]
)
interface AppComponent {
    fun inject(app: DictionaryApp)
    fun inject(activity: SearchActivity)
    fun inject(vm: WordListViewModelFactory)
    fun inject(vm: WordInfoViewModelFactory)
    fun inject(activity: HomeActivity)
    fun inject(activity: AccountActivity)
    fun inject(activity: LearnActivity)
}