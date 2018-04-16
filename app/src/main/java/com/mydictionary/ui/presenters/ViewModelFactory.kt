package com.mydictionary.ui.presenters

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.mydictionary.data.repository.WordsRepository
import com.mydictionary.ui.presenters.home.HomeViewModel
import com.mydictionary.ui.presenters.learn.LearnWordsViewModel
import com.mydictionary.ui.presenters.search.SearchViewModel

/**
 * Created by Viktoria Chebotar on 15.04.18.
 */
class ViewModelFactory(private val repository: WordsRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            HomeViewModel::class.java -> HomeViewModel(repository) as T
            LearnWordsViewModel::class.java -> LearnWordsViewModel(repository) as T
            SearchViewModel::class.java -> SearchViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class");
        }
    }

}