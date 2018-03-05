package com.mydictionary.data.repository

import android.content.Context
import android.util.Log
import com.mydictionary.R
import com.mydictionary.commons.Constants
import com.mydictionary.commons.NoConnectivityException
import com.mydictionary.data.network.WordApiRetrofit
import com.mydictionary.data.pojo.Mapper
import com.mydictionary.data.pojo.SearchResult
import com.mydictionary.data.pojo.WordDetails
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

class OxfordDictionaryStorage(val context: Context) {
    private val restApi = WordApiRetrofit.getInstance(context).wordsApi;

    //  fun getRandomWord(listener: RepositoryListener<WordInfo>) {
    //   val call = restApi.getRandomWord();
    //  call.enqueue(MyRetrofitCallback(listener, context))
    //}

    fun getWordInfo(word: String, listener: RepositoryListener<WordDetails>) {
        val wordDetailsRequest = async { executeCallAsync(restApi.getWordInfo(word), listener) }
        val relatedWordsRequest = async { executeCallAsync(restApi.getRelatedWords(word), listener) }
        launch(UI) {
            val wordDetailsResponse = wordDetailsRequest.await()
            val relatedWordsResponse = relatedWordsRequest.await()
            if (wordDetailsResponse?.isSuccessful == true && wordDetailsResponse.body() != null) {
                val wordDetails = Mapper.fromDto(wordDetailsResponse.body(), relatedWordsResponse?.body())
                if (wordDetails.meanings.isEmpty() && wordDetails.notes.isEmpty() && wordDetails.synonyms.isEmpty() && wordDetails.antonyms.isEmpty()) {
                    listener.onError(this@OxfordDictionaryStorage.context.getString(R.string.word_not_found_error))
                } else {
                    listener.onSuccess(wordDetails)
                }
            } else {
                var message = this@OxfordDictionaryStorage.context.getString(R.string.default_error)
                if (wordDetailsResponse?.isSuccessful == false) {
                    message = wordDetailsResponse.raw()?.message() ?: message
                }
                listener.onError(message)
            }
        }
    }

    private fun <T> executeCallAsync(call: Call<T>, listener: RepositoryListener<WordDetails>): Response<T>? {
        return try {
            call.execute()
        } catch (e: Exception) {
            onFailure(e, listener)
            null
        }
    }


    fun searchTheWord(phrase: String, listener: RepositoryListener<SearchResult>) {
        val call = restApi.searchTheWord(phrase, Constants.SEARCH_LIMIT)
        call.enqueue(MyRetrofitCallback(listener, context))
    }

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
