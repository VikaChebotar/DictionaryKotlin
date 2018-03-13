package com.mydictionary.data.pojo

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Viktoria_Chebotar on 2/22/2018.
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class WordDetails(val word: String) : Parcelable {
    var pronunciation: String? = null
    var notes = listOf<Note>()
    var meanings = listOf<WordMeaning>()
    var synonyms = listOf<String>()
    var antonyms = listOf<String>()
}

@SuppressLint("ParcelCreator")
@Parcelize
data class WordMeaning(val definitionId: String) : Parcelable {
    val definitions = mutableListOf<Definition>()
    var partOfSpeech: String? = null
    val examples = mutableListOf<Example>()
    var isFavourite = false
}

//wrapper classes used to distinguish view types in adapter
@SuppressLint("ParcelCreator")
@Parcelize
data class Note(val text: String)  : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Definition(val text: String) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Example(val text: String):Parcelable
