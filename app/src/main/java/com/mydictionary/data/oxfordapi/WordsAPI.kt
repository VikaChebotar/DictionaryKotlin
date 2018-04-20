package com.mydictionary.data.oxfordapi

import com.mydictionary.data.oxfordapi.dto.RelatedWordsResponse
import com.mydictionary.data.oxfordapi.dto.WordDetailsResponse
import com.mydictionary.data.pojo.SearchResult
import io.reactivex.Flowable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Viktoria_Chebotar on 6/2/2017.
 */

interface WordsAPI {
    @GET("https://api.datamuse.com/sug")
    fun searchTheWord(@Query("s") query: String, @Query("max") max: Int): Flowable<SearchResult>

    @GET("entries/en/{word}")
    fun getWordInfo(@Path("word") word: String): Single<WordDetailsResponse>

    @GET("entries/en/{word}/synonyms;antonyms")
    fun getRelatedWords(@Path("word") word: String): Single<RelatedWordsResponse>
//
//    @GET("words?random=true&frequencyMin=3&frequencyMax=7")
//    fun getRandomWord(): Call<WordInfo>
}
