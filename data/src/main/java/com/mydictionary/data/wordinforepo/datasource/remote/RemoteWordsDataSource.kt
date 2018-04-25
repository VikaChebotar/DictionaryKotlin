package com.mydictionary.data.wordinforepo.datasource.remote

import com.mydictionary.data.wordinforepo.pojo.RelatedWordsResponse
import com.mydictionary.data.wordinforepo.pojo.SearchResult
import com.mydictionary.data.wordinforepo.pojo.WordDetailsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Viktoria_Chebotar on 6/2/2017.
 */

interface RemoteWordsDataSource {
    @GET("https://api.datamuse.com/sug")
    fun searchTheWord(@Query("s") query: String, @Query("max") max: Int): Call<SearchResult>

    @GET("entries/en/{word}")
    fun getWordInfo(@Path("word") word: String): Call<WordDetailsResponse>

    @GET("entries/en/{word}/synonyms;antonyms")
    fun getRelatedWords(@Path("word") word: String): Call<RelatedWordsResponse>
}
