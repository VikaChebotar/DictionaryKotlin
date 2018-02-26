package com.mydictionary.data.network.dto

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by Viktoria_Chebotar on 2/22/2018.
 */

class WordDetailsResponse {
    @SerializedName("results")
    @Expose
    var results: List<Result>? = null

    class Result {
        @SerializedName("lexicalEntries")
        @Expose
        var lexicalEntries: List<LexicalEntry>? = null
        @SerializedName("word")
        @Expose
        var word: String? = null
    }

    class LexicalEntry {
        @SerializedName("entries")
        @Expose
        var entries: List<Entry>? = null
        @SerializedName("lexicalCategory")
        @Expose
        var lexicalCategory: String? = null
        @SerializedName("pronunciations")
        @Expose
        var pronunciations: List<Pronunciation>? = null
    }

    class Entry {

        @SerializedName("homographNumber")
        @Expose
        var homographNumber: String? = null
        @SerializedName("notes")
        @Expose
        var notes: List<Note>? = null
        @SerializedName("senses")
        @Expose
        var senses: List<Sense>? = null
        var lexicalCategory: String? = null //not in json, added later for convenience
    }

    class Note {

        @SerializedName("text")
        @Expose
        var text: String = ""
        @SerializedName("type")
        @Expose
        var type: String? = null

    }

    class Pronunciation {
        @SerializedName("phoneticSpelling")
        @Expose
        var phoneticSpelling: String? = null

    }

    class Sense(@SerializedName("id")
                @Expose val id: String) {
        @SerializedName("definitions")
        @Expose
        var definitions: List<String>? = null
        @SerializedName("examples")
        @Expose
        var examples: List<Example>? = null
        @SerializedName("subsenses")
        @Expose
        var subsenses: List<Sense>? = null
        @SerializedName("notes")
        @Expose
        var notes: List<Note>? = null
    }

    class Example {
        @SerializedName("text")
        @Expose
        var text: String? = null
    }

}