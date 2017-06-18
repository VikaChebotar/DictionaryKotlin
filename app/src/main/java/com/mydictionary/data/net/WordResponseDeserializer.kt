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
                wordInfo.apply {
                    synonyms.addAll(it.synonyms);
                    antonyms.addAll(it.antonyms)
                    also.addAll(it.also)
                    derivation.addAll(it.derivation)
                    entails.addAll(it.entails)
                    examples.addAll(it.examples)
                    hasTypes.addAll(it.hasTypes)
                    inRegion.addAll(it.inRegion)
                    similarTo.addAll(it.similarTo)
                    typeOf.addAll(it.typeOf)
                    verbGroup.addAll(it.verbGroup)
                    if (it.definition != null && it.partOfSpeech != null) {
                        definitions.add(Definition(it.definition, it.partOfSpeech))
                    }
                }
            }
        }
        return wordInfo
    }
}
