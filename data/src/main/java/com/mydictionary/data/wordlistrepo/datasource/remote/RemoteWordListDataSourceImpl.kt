package com.mydictionary.data.wordlistrepo.datasource.remote

import com.google.firebase.database.FirebaseDatabase
import com.mydictionary.data.await
import com.mydictionary.data.wordlistrepo.pojo.WordListDto


class RemoteWordListDataSourceImpl(val firebaseDatabase: FirebaseDatabase) :
    WordListDataSource {

    override suspend fun getAllWordLists(): List<WordListDto> {
        val query = getWordListReference()
        query.keepSynced(true)
        val list = mutableListOf<WordListDto>()
        val dataSnapshot = query.await()
        dataSnapshot.children?.mapNotNullTo(list) { it.getValue<WordListDto>(WordListDto::class.java) }
        return list
    }

    override suspend fun getWordList(wordListName: String): WordListDto {
        val query = getWordListReference().orderByChild("name").equalTo(wordListName)
        query.keepSynced(true)
        val dataSnapshot = query.await()
        val wordList = mutableListOf<WordListDto>()
        dataSnapshot.children?.mapNotNullTo(wordList) { it.getValue<WordListDto>(WordListDto::class.java) }
        return wordList[0]
    }

    private fun getWordListReference() = firebaseDatabase.reference.child("wordlist")
}