package com.mydictionary.data.wordlistrepo.datasource.cache

import com.mydictionary.data.wordlistrepo.pojo.WordListDto
import java.lang.ref.WeakReference

interface WordListCache {
    fun put(list: List<WordListDto>)
    fun get(): List<WordListDto>?
}

class WordListCacheImpl : WordListCache {
    private var allWordListsCache = WeakReference<List<WordListDto>>(emptyList())

    override fun put(list: List<WordListDto>) {
        allWordListsCache = WeakReference(list)
    }

    override fun get() = allWordListsCache.get()

}