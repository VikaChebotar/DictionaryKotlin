package com.mydictionary.data.net

import android.content.Context
import android.net.ConnectivityManager
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mydictionary.R
import com.mydictionary.data.commons.Constants
import com.mydictionary.data.commons.NoConnectivityException
import com.mydictionary.data.commons.isOnline
import com.mydictionary.data.entity.SearchResult
import com.mydictionary.data.entity.WordInfo
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Viktoria_Chebotar on 6/2/2017.
 */
class WordApiRetrofit private constructor(context: Context) {
    val wordsApi: WordsAPI

    companion object {
        private var instance: WordApiRetrofit? = null
        fun getInstance(context: Context): WordApiRetrofit {
            if (instance == null)
                instance = WordApiRetrofit(context)

            return instance!!
        }
    }

    init {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val searchResultListType = object : TypeToken<SearchResult>() {}.type
        val gson = GsonBuilder().
                registerTypeAdapter(searchResultListType, SearchResultResponseDeserializer()).
                registerTypeAdapter(WordInfo::class.java, WordResponseDeserializer()).
                create()

        val client = OkHttpClient.Builder().
                addInterceptor(loggingInterceptor).
                addInterceptor(HeaderInterceptor(context)).
                addInterceptor(ConnectivityInterceptor(context)).build();

        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.WORDS_API_ENDPOINT)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        wordsApi = retrofit.create(WordsAPI::class.java)
    }

    inner class HeaderInterceptor(val context: Context) : Interceptor {
        override fun intercept(chain: Interceptor.Chain?): Response {
            val request = chain!!.request().
                    newBuilder().
                    addHeader(context.getString(R.string.api_key_name), context.getString(R.string.wordnik_api_key)).
                    build()
            return chain.proceed(request)
        }
    }

    inner class ConnectivityInterceptor(val context: Context) : Interceptor {
        override fun intercept(chain: Interceptor.Chain?): Response {
            if (!(context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).isOnline()) {
                throw NoConnectivityException()
            }

            val builder = chain!!.request().newBuilder()
            return chain.proceed(builder.build())
        }
    }
}

