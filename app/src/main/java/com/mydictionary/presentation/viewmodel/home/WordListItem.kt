package com.mydictionary.presentation.viewmodel.home

import com.mydictionary.data.firebasestorage.dto.WordList

sealed class WordListItem {
    data class Category(val category: String) : WordListItem()
    data class WordList(val name: String, val category: String, val list: List<String>) : WordListItem()
}

fun mapToPresentation(wordList: WordList) =
        WordListItem.WordList(wordList.name, wordList.type, wordList.list)

fun mapToPresentation(lists: List<WordList>) =
        lists
                .groupBy { it.type }
                .entries
                .flatMap {
                    listOf(WordListItem.Category(it.key),
                            *it.value.map { mapToPresentation(it) }.toTypedArray()
                    )
                }
