package com.mydictionary.data.wordlistrepo.datasource.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mydictionary.data.wordlistrepo.pojo.WordListDto
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers


class RemoteWordListDataSourceImpl(val firebaseDatabase: FirebaseDatabase) :
    WordListDataSource {

    override fun getAllWordLists() = Single.create<List<WordListDto>> { emitter ->
        val query = getWordListReference()
        query.keepSynced(true)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                emitter.onError(p0?.toException() ?: Exception("error in getAllWordLists"))
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val list = mutableListOf<WordListDto>()
                p0?.children?.mapNotNullTo(list) { it.getValue<WordListDto>(WordListDto::class.java) }
                emitter.onSuccess(list)
            }
        })
    }

    override fun getWordList(wordListName: String): Single<WordListDto> =
        Single.create<WordListDto> { emitter ->
            val query = getWordListReference().orderByChild("name").equalTo(wordListName)
            query.keepSynced(true)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    emitter.onError(p0?.toException() ?: Exception("error in getAllWordLists"))
                }

                override fun onDataChange(p0: DataSnapshot?) {
                    val wordList = mutableListOf<WordListDto>()
                    p0?.children?.mapNotNullTo(wordList) { it.getValue<WordListDto>(WordListDto::class.java) }
                    if (wordList.isNotEmpty()) {
                        emitter.onSuccess(wordList[0])
                    } else {
                        emitter.onError(Exception("No such list"))
                    }
                }
            })
        }.
            //hack to return to background thread, because onDataChange is always called in UI thread
            observeOn(Schedulers.io())

    private fun getWordListReference() = firebaseDatabase.reference.child("wordlist")
}