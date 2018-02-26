package com.mydictionary.data.network.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Viktoria_Chebotar on 2/26/2018.
 */
class RelatedWordsResponse{
    @SerializedName("results")
    @Expose
    var results: List<Result>? = null

    class RelatedWord {
        @SerializedName("id")
        @Expose
        var id: String? = null
        @SerializedName("language")
        @Expose
        var language: String? = null
        @SerializedName("text")
        @Expose
        var text: String? = null
    }

    class Entry {
        @SerializedName("homographNumber")
        @Expose
        var homographNumber: String? = null
        @SerializedName("senses")
        @Expose
        var senses: List<Sense>? = null
    }

    class LexicalEntry {
        @SerializedName("entries")
        @Expose
        var entries: List<Entry>? = null
    }

     class Result {
        @SerializedName("id")
        @Expose
        var id: String? = null
        @SerializedName("lexicalEntries")
        @Expose
        var lexicalEntries: List<LexicalEntry>? = null
    }

    class Sense {
        @SerializedName("antonyms")
        @Expose
        var antonyms: List<RelatedWord>? = null
        @SerializedName("id")
        @Expose
        var id: String? = null
        @SerializedName("synonyms")
        @Expose
        var synonyms: List<RelatedWord>? = null
        @SerializedName("registers")
        @Expose
        var registers: List<String>? = null
    }

}