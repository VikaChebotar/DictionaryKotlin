package com.mydictionary.data.net

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.mydictionary.data.entity.Definition
import com.mydictionary.data.entity.WordInfo
import com.mydictionary.data.entity.WordInfoResult
import java.lang.reflect.Type

/**
 * Created by Viktoria_Chebotar on 6/2/2017.
 */

class WordResponseDeserializer : JsonDeserializer<WordInfo> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): WordInfo {
        val jsonObj = json!!.asJsonObject;
        val word = jsonObj.get("word").asString
        val pronunciationJson = jsonObj.get("pronunciation")
        val pronunciation = if (pronunciationJson.isJsonPrimitive) pronunciationJson.asString else
            pronunciationJson?.asJsonObject?.get("all")?.asString
        var wordInfo = WordInfo(word, pronunciation)
        val gson = Gson();
        val listType = object : TypeToken<List<WordInfoResult>>() {}.type
        val results = jsonObj.get("results")
        if (results != null) {
            val resultList: List<WordInfoResult> = gson.fromJson(results, listType)
            resultList.forEach {
                wordInfo.synonyms.addAll(it.synonyms);
                wordInfo.antonyms.addAll(it.antonyms)
                wordInfo.also.addAll(it.also)
                wordInfo.derivation.addAll(it.derivation)
                wordInfo.entails.addAll(it.entails)
                wordInfo.examples.addAll(it.examples)
                wordInfo.hasTypes.addAll(it.hasTypes)
                wordInfo.inRegion.addAll(it.inRegion)
                wordInfo.similarTo.addAll(it.similarTo)
                wordInfo.typeOf.addAll(it.typeOf)
                wordInfo.verbGroup.addAll(it.verbGroup)
                if (it.definition != null && it.partOfSpeech != null) {
                    wordInfo.definitions.add(Definition(it.definition, it.partOfSpeech))
                }
            }
        }
        return wordInfo
    }
}
