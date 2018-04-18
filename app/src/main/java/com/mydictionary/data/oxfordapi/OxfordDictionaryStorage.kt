package com.mydictionary.data.oxfordapi

import android.content.Context
import android.util.LruCache
import com.mydictionary.R
import com.mydictionary.commons.SEARCH_LIMIT
import com.mydictionary.data.oxfordapi.dto.RelatedWordsResponse
import com.mydictionary.data.oxfordapi.dto.WordDetailsResponse
import com.mydictionary.data.pojo.WordDetails
import com.mydictionary.data.pojo.WordResponseMapper
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import javax.inject.Inject

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

class OxfordDictionaryStorage @Inject constructor(
    val context: Context,
    val restApi: WordsAPI,
    val wordsCache: LruCache<String, WordDetails>,
    val mapper: WordResponseMapper
) {
    private val TAG = OxfordDictionaryStorage::class.java.simpleName

    fun getFullWordInfo(word: String): Single<WordDetails> =
        Single.just(word).flatMap {
            val wordDetails = wordsCache.get(word)
            val needToLoadDetails = wordDetails == null
            val needToLoadRelatedWords =
                !(!needToLoadDetails && (wordDetails.synonyms.isNotEmpty() || wordDetails.antonyms.isNotEmpty()))
            if (!needToLoadDetails) {
                Single.just(wordDetails)
            } else if (needToLoadDetails && needToLoadRelatedWords) {
                Single.zip(restApi.getWordInfo(word),
                    restApi.getRelatedWords(word).onErrorReturn { RelatedWordsResponse() },
                    BiFunction<WordDetailsResponse, RelatedWordsResponse, WordDetails>
                    { wordDetailsResponse, relatedWordsResponse ->
                        val wordDetails = mapper.fromDto(wordDetailsResponse)
                        mapper.setRelatedWords(wordDetails, relatedWordsResponse)
                        wordDetails
                    })
            } else Single.zip(Single.just(wordDetails),
                restApi.getRelatedWords(word).onErrorReturn { RelatedWordsResponse() },
                BiFunction<WordDetails, RelatedWordsResponse, WordDetails>
                { wordDetails, relatedWordsResponse ->
                    mapper.setRelatedWords(wordDetails, relatedWordsResponse)
                    wordDetails
                })
        }.flatMap {
            if (it.meanings.isEmpty() && it.notes.isEmpty() && it.synonyms.isEmpty() && it.antonyms.isEmpty())
                Single.error(Exception(context.getString(R.string.word_not_found_error)))
            else Single.just(it)
        }.doOnSuccess { wordsCache.put(it.word, it) }


    fun getShortWordInfo(word: String): Single<WordDetails> = Single.just(word).flatMap {
        val wordDetails = wordsCache.get(word)
        if (wordDetails == null) {
            restApi.getWordInfo(word).map { response -> mapper.fromDto(response) }
        } else Single.just(wordDetails)
    }.
        //  filter { it.meanings.isNotEmpty() }.
        //switchIfEmpty(Single.just(null)).
        doOnSuccess { wordsCache.put(it.word, it) }


    fun searchTheWord(phrase: String): Single<List<String>> =
        restApi.searchTheWord(phrase, SEARCH_LIMIT).map { it.searchResults }
}
