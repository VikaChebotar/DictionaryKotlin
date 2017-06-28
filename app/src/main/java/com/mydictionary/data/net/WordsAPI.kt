package com.mydictionary.data.net

import com.mydictionary.data.entity.SearchResult
import com.mydictionary.data.entity.WordInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Viktoria_Chebotar on 6/2/2017.
 */

interface WordsAPI {
    @GET("https://api.datamuse.com/sug")
    fun searchTheWord(@Query("s") query: String, @Query("max") max: Int): Call<SearchResult>

    @GET("words/{word}")
    fun getWordInfo(@Path("word") word: String): Call<WordInfo>

    @GET("words?random=true&frequencyMin=3&frequencyMax=7")
    fun getRandomWord(): Call<WordInfo>
}
