package com.mydictionary.data.wordlistrepo

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mydictionary.data.wordlistrepo.pojo.WordListDto
import com.mydictionary.domain.entity.WordList
import com.mydictionary.domain.repository.WordListRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.lang.ref.WeakReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordListRepositoryImpl @Inject constructor(val firebaseDatabase: FirebaseDatabase,
                                                 val mapper: WordListMapper) : WordListRepository {
    private var allWordListsCache = WeakReference<List<WordListDto>>(emptyList())

    override fun getAllWordLists(): Single<List<WordList>> =
            Single.create<List<WordListDto>> { emitter ->
                if (allWordListsCache.get()?.isNotEmpty() == true) emitter.onSuccess(allWordListsCache.get()!!)
                val query = getWordListReference()
                query.keepSynced(true)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        emitter.onError(p0?.toException() ?: Exception("error in getAllWordLists"))
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        allWordListsCache.clear()
                        val list = mutableListOf<WordListDto>()
                        p0?.children?.mapNotNullTo(list) { it.getValue<WordListDto>(WordListDto::class.java) }
                        allWordListsCache = WeakReference(list)
                        emitter.onSuccess(list)
                    }
                })
            }.map { mapper.mapListOfWordList(it) }


    override fun getWordList(wordListName: String): Single<WordList> =
            Single.create<WordListDto> { emitter ->
                if (allWordListsCache.get()?.isNotEmpty() == true) {
                    allWordListsCache.get()?.find { it.name == wordListName }?.let {
                        emitter.onSuccess(it)
                        return@create
                    }
                }
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
                    .map { mapper.mapWordList(it) }


    private fun getWordListReference() = firebaseDatabase.reference.child("wordlist")
}