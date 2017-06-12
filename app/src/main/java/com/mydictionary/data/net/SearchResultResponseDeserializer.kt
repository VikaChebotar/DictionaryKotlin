package com.mydictionary.data.net

import com.mydictionary.data.entity.SearchResult

/**
 * Created by Viktoria_Chebotar on 6/2/2017.
 */
class SearchResultResponseDeserializer : com.google.gson.JsonDeserializer<SearchResult> {
    override fun deserialize(json: com.google.gson.JsonElement?, typeOfT: java.lang.reflect.Type?, context: com.google.gson.JsonDeserializationContext?): com.mydictionary.data.entity.SearchResult {
        val stringResults = json?.asJsonArray?.map { it.asJsonObject.get("word").asString } ?: listOf<String>()
        return com.mydictionary.data.entity.SearchResult(stringResults);
    }
}