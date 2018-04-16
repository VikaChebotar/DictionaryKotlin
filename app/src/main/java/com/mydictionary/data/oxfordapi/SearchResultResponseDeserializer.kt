package com.mydictionary.data.oxfordapi

import com.mydictionary.data.pojo.SearchResult

/**
 * Created by Viktoria_Chebotar on 6/2/2017.
 */
class SearchResultResponseDeserializer : com.google.gson.JsonDeserializer<SearchResult> {
    override fun deserialize(json: com.google.gson.JsonElement?, typeOfT: java.lang.reflect.Type?, context: com.google.gson.JsonDeserializationContext?): com.mydictionary.data.pojo.SearchResult {
        val stringResults = json?.asJsonArray?.map { it.asJsonObject.get("word").asString } ?: listOf<String>()
        return com.mydictionary.data.pojo.SearchResult(stringResults);
    }
}