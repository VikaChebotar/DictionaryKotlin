package com.mydictionary.data.network

import com.mydictionary.data.network.dto.WordDetailsResponse
import com.mydictionary.data.pojo.SearchResult
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

    @GET("entries/en/{word}")
    fun getWordInfo(@Path("word") word: String): Call<WordDetailsResponse>
//
//    @GET("words?random=true&frequencyMin=3&frequencyMax=7")
//    fun getRandomWord(): Call<WordInfo>
}
