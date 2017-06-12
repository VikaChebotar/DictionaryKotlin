package com.mydictionary.data.repository

import android.content.Context
import android.util.Log
import com.mydictionary.R
import com.mydictionary.data.commons.NoConnectivityException
import com.mydictionary.data.entity.WordInfo
import com.mydictionary.data.net.WordApiRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by Viktoria_Chebotar on 6/7/2017.
 */

class CloudStorage(val context: Context) : WordsStorage {
    val restApi = WordApiRetrofit.getInstance(context).wordsApi;

    override fun getRandomWord(listener: WordsStorage.WordSourceListener<WordInfo>) {
        val call = restApi.getRandomWord();
        call.enqueue(object : Callback<WordInfo> {
            override fun onResponse(call: Call<WordInfo>?, response: Response<WordInfo>?) {
                if (response?.isSuccessful ?: false) {
                    listener.onSuccess(response?.body())
                } else {
                    listener.onError(response?.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<WordInfo>?, t: Throwable?) {
                val errorMes: String?
                if (t is NoConnectivityException) {
                    errorMes = context.getString(R.string.networkError)
                } else {
                    errorMes = t?.message
                }
                listener.onError(errorMes)
                Log.e(CloudStorage::class.java.simpleName, "getRandomWord: "+errorMes)
            }

        })
    }


}
