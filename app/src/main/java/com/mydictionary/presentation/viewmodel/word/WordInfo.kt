package com.mydictionary.presentation.viewmodel.word

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Viktoria Chebotar on 21.04.18.
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class WordInfo(val word: String,
                    var pronunciation: String? = null,
                    var notes: List<Note>,
                    var meanings: List<WordMeaning>,
                    var synonyms: List<String>,
                    var antonyms: List<String>) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class WordMeaning(val definitionId: String,
                       val definitions: List<Definition> = emptyList(),
                       val partOfSpeech: String? = null,
                       val examples: List<Example> = emptyList(),
                       var isFavourite: Boolean = false) : Parcelable

//wrapper classes used to distinguish view types in adapter
@SuppressLint("ParcelCreator")
@Parcelize
data class Note(val text: String) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Definition(val text: String) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Example(val text: String) : Parcelable