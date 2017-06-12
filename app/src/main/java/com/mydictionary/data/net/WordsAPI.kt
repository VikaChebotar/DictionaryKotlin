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
    @retrofit2.http.GET("https://api.datamuse.com/sug")
    fun searchTheWord(@retrofit2.http.Query("s") query: String, @retrofit2.http.Query("max") max: Int): retrofit2.Call<SearchResult>

    @retrofit2.http.GET("words/{word}")
    fun getWordInfo(@retrofit2.http.Path("word") word: String): retrofit2.Call<WordInfo>

    @retrofit2.http.GET("words?random=true&frequencyMin=1&frequencyMax=4.5")
    fun getRandomWord(): retrofit2.Call<WordInfo>
}
