package com.mydictionary.data.repository

import android.content.Context
import android.util.Log
import android.util.LruCache
import com.mydictionary.R
import com.mydictionary.commons.Constants
import com.mydictionary.commons.NoConnectivityException
import com.mydictionary.commons.Utils
import com.mydictionary.data.network.WordApiRetrofit
import com.mydictionary.data.network.dto.RelatedWordsResponse
import com.mydictionary.data.network.dto.WordDetailsResponse
import com.mydictionary.data.pojo.Mapper
import com.mydictionary.data.pojo.WordDetails
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

class OxfordDictionaryStorage(val context: Context) {
    private val restApi = WordApiRetrofit.getInstance(context).wordsApi;
    private val wordsCache = LruCache<String, WordDetails>(Utils.getCacheMemorySize())

    fun getFullWordInfo(word: String): Single<WordDetails> =
            Single.just(word).
                    flatMap {
                        val wordDetails = wordsCache.get(word)
                        val needToLoadDetails = wordDetails == null
                        val needToLoadRelatedWords = !(!needToLoadDetails && (wordDetails.synonyms.isNotEmpty() || wordDetails.antonyms.isNotEmpty()))
                        if (!needToLoadDetails) {
                            Single.just(wordDetails)
                        } else if (needToLoadDetails && needToLoadRelatedWords) {
                            Single.zip(restApi.getWordInfo(word), restApi.getRelatedWords(word),
                                    BiFunction<WordDetailsResponse, RelatedWordsResponse, WordDetails>
                                    { wordDetailsResponse, relatedWordsResponse ->
                                        val wordDetails = Mapper.fromDto(wordDetailsResponse)
                                        Mapper.setRelatedWords(wordDetails, relatedWordsResponse)
                                        wordDetails
                                    })
                        } else Single.zip(Single.just(wordDetails), restApi.getRelatedWords(word),
                                BiFunction<WordDetails, RelatedWordsResponse, WordDetails>
                                { wordDetails, relatedWordsResponse ->
                                    Mapper.setRelatedWords(wordDetails, relatedWordsResponse)
                                    wordDetails
                                })
                    }.
                    flatMap {
                        if (it.meanings.isEmpty() && it.notes.isEmpty() && it.synonyms.isEmpty() && it.antonyms.isEmpty())
                            Single.error(Exception(context.getString(R.string.word_not_found_error)))
                        else Single.just(it)
                    }.
                    doOnSuccess { wordsCache.put(it.word, it) }


    fun getShortWordInfo(word: String): Single<WordDetails>
            = Single.just(word).
            flatMap {
                val wordDetails = wordsCache.get(word)
                if (wordDetails == null) {
                    restApi.getWordInfo(word).map { response -> Mapper.fromDto(response) }
                } else Single.just(wordDetails)
            }.
            filter { it.meanings.isNotEmpty() }.
            switchIfEmpty(Single.just(null)).doOnSuccess { wordsCache.put(it.word, it) }



    fun searchTheWord(phrase: String): Single<List<String>> = restApi.searchTheWord(phrase, Constants.SEARCH_LIMIT).map { it.searchResults }

    private inner class MyRetrofitCallback<T>(val listener: RepositoryListener<T>,
                                              val context: Context) : Callback<T> {
        override fun onResponse(call: Call<T>?, response: Response<T>?) {
            if (response?.isSuccessful == true && response.body() != null) {
                listener.onSuccess(response.body() as T)
            } else {
                listener.onError(response?.raw()?.message() ?: context.getString(R.string.default_error))
            }
        }

        override fun onFailure(call: Call<T>?, t: Throwable?) {
            onFailure(t, listener)
        }
    }

    private fun <T> onFailure(t: Throwable?, listener: RepositoryListener<T>) {
        val errorMes: String
        if (t is NoConnectivityException) {
            errorMes = context.getString(R.string.networkError)
        } else {
            errorMes = t?.message ?: context.getString(R.string.default_error)
        }
        listener.onError(errorMes)
        Log.e(OxfordDictionaryStorage::class.java.simpleName, errorMes)
    }

}
