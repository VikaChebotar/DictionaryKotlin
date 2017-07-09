package com.mydictionary.data.repository

import android.content.Context
import android.util.Log
import com.mydictionary.R
import com.mydictionary.commons.Constants
import com.mydictionary.commons.NoConnectivityException
import com.mydictionary.data.entity.SearchResult
import com.mydictionary.data.entity.WordInfo
import com.mydictionary.data.net.WordApiRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

class CloudStorage(val context: Context) {
    private val restApi = WordApiRetrofit.getInstance(context).wordsApi;

    fun getRandomWord(listener: RepositoryListener<WordInfo>) {
        val call = restApi.getRandomWord();
        call.enqueue(MyRetrofitCallback(listener, context))
    }

    fun getWordInfo(word: String, listener: RepositoryListener<WordInfo>) {
        val call = restApi.getWordInfo(word);
        call.enqueue(MyRetrofitCallback(listener, context))
    }

    fun searchTheWord(phrase: String, listener: RepositoryListener<SearchResult>) {
        val call = restApi.searchTheWord(phrase, Constants.SEARCH_LIMIT)
        call.enqueue(MyRetrofitCallback(listener, context))
    }

    private class MyRetrofitCallback<T>(val listener: RepositoryListener<T>,
                                        val context: Context) : Callback<T> {
        override fun onResponse(call: Call<T>?, response: Response<T>?) {
            if (response?.isSuccessful ?: false && response?.body() != null) {
                listener.onSuccess(response.body())
            } else {
                listener.onError(response?.errorBody().toString())
            }
        }

        override fun onFailure(call: Call<T>?, t: Throwable?) {
            val errorMes: String
            if (t is NoConnectivityException) {
                errorMes = context.getString(R.string.networkError)
            } else {
                errorMes = t?.message ?: context.getString(R.string.default_error)
            }
            listener.onError(errorMes)
            Log.e(CloudStorage::class.java.simpleName, errorMes)
        }


    }
}
