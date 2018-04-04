package com.mydictionary.ui.presenters.home

sealed class WordListItem {
    data class ListCategory(val type: String): WordListItem()
    data class WordList(val name: String, val type: String, val list: List<String>):WordListItem()
}