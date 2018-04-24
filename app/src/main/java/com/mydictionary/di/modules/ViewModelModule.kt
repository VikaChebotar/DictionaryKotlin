package com.mydictionary.di.modules

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.mydictionary.presentation.viewmodel.ViewModelFactory
import com.mydictionary.presentation.viewmodel.account.AccountViewModel
import com.mydictionary.presentation.viewmodel.home.HomeViewModel
import com.mydictionary.presentation.viewmodel.learn.LearnWordsViewModel
import com.mydictionary.presentation.viewmodel.search.SearchViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    internal abstract fun accountViewModel(viewModel: AccountViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    internal abstract fun homeViewModel(viewModel: HomeViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(LearnWordsViewModel::class)
    internal abstract fun learnViewModel(viewModel: LearnWordsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    internal abstract fun searchViewModel(viewModel: SearchViewModel): ViewModel
}