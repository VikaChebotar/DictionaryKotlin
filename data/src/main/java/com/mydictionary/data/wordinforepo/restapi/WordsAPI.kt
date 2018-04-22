package com.mydictionary.data.wordinforepo.restapi

import com.mydictionary.data.wordinforepo.pojo.RelatedWordsResponse
import com.mydictionary.data.wordinforepo.pojo.SearchResult
import com.mydictionary.data.wordinforepo.pojo.WordDetailsResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Viktoria_Chebotar on 6/2/2017.
 */

interface WordsAPI {
    @GET("https://api.datamuse.com/sug")
    fun searchTheWord(@Query("s") query: String, @Query("max") max: Int): Single<SearchResult>

    @GET("entries/en/{word}")
    fun getWordInfo(@Path("word") word: String): Single<WordDetailsResponse>

    @GET("entries/en/{word}/synonyms;antonyms")
    fun getRelatedWords(@Path("word") word: String): Single<RelatedWordsResponse>
}
