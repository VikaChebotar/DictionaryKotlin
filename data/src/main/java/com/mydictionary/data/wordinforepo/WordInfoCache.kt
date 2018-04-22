package com.mydictionary.data.wordinforepo

import android.util.LruCache
import com.mydictionary.data.wordinforepo.pojo.RelatedWordsResponse
import com.mydictionary.data.wordinforepo.pojo.WordDetailsResponse

/**
 * Created by Viktoria Chebotar on 22.04.18.
 */
interface WordInfoCache {
    fun put(word: String, wordInfo: Pair<WordDetailsResponse, RelatedWordsResponse?>)
    fun get(word: String): Pair<WordDetailsResponse, RelatedWordsResponse?>?
}

class WordInfoCacheImpl : WordInfoCache {
    private val lruCache = LruCache<String,
            Pair<WordDetailsResponse, RelatedWordsResponse?>>(getCacheMemorySize())

    override fun put(word: String, pair: Pair<WordDetailsResponse, RelatedWordsResponse?>) {
        lruCache.put(word, pair)
    }

    override fun get(word: String) = lruCache.get(word)

}