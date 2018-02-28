package com.mydictionary.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mydictionary.R
import com.mydictionary.commons.Constants.Companion.MAX_HISTORY_LIMIT
import com.mydictionary.data.entity.UserWord


/**
 * Created by Viktoria_Chebotar on 6/12/2017.
 */
class InternalFirebaseStorage(val context: Context) {
    private val TAG = InternalFirebaseStorage::class.java.canonicalName
    // private val realm: Realm = Realm.getDefaultInstance()
    private val firebaseAuth = FirebaseAuth.getInstance();
    private var firebaseDatabase = FirebaseDatabase.getInstance()

    init {
        if (firebaseAuth.currentUser == null) {
            loginAnonymously()
        } else {
            Log.d(TAG, "already signedIn");
        }
    }

    //    fun getWordOfTheDay(): WordOfTheDay? {
//        return realm.where(WordOfTheDay::class.java).findFirst();
//    }
//
//    fun storeWordOfTheDay(word: String, date: Date) {
//        realm.executeTransactionAsync { realm: Realm? ->
//            run {
//                realm?.delete(WordOfTheDay::class.java)
//                realm?.copyToRealm(WordOfTheDay(word, date))
//            }
//        }
//    }
//
    fun getHistoryWords(listener: RepositoryListener<List<String>>) {
        val query = firebaseDatabase.reference.child("users").
                child(firebaseAuth.currentUser?.uid).orderByChild("accessTime").limitToLast(MAX_HISTORY_LIMIT)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError?) {
                listener.onError(error?.toException()?.message ?: context.getString(R.string.default_error))
            }

            override fun onDataChange(data: DataSnapshot?) {
                if (data?.value != null) {
                    listener.onSuccess(data.children.reversed().map { it.key }.toList())
                }
            }
        })
    }

    fun addWordToHistory(word: String) {
        val userReference = firebaseDatabase.reference.child("users")
        val userWord = UserWord(word)
        val task = userReference.child(firebaseAuth.currentUser?.uid).child(userWord.word).setValue(userWord.value)
        task.addOnSuccessListener { Log.d(TAG, "success") }.addOnFailureListener { Log.d(TAG, it.message) }
    }
//        realm.executeTransactionAsync { realm ->
//            if (realm.where(HistoryWord::class.java).count() >= Constants.MAX_HISTORY_LIMIT) {
//                val oldestWord = realm.where(HistoryWord::class.java).
//                        equalTo("isFavorite", false).
//                        findAllSorted("accessTime", Sort.ASCENDING).first()
//                oldestWord?.deleteFromRealm()
//            }
//            realm?.copyToRealmOrUpdate(word)
//        }


    //
//    fun getWordFromHistory(wordName: String): HistoryWord? {
//        val realmWord = realm.where(HistoryWord::class.java).equalTo("word", wordName).findFirst()
//        return if (realmWord != null) realm.copyFromRealm(realmWord) else null
//    }
//
//    fun isWordFavorite(wordName: String): Boolean {
//        val historyWord = realm.where(HistoryWord::class.java).equalTo("word", wordName).findFirst()
//        return historyWord?.isFavorite ?: false
//    }
//
//    fun setWordFavoriteState(wordName: String, isFavorite: Boolean): Boolean {
//        val historyWord = realm.where(HistoryWord::class.java).equalTo("word", wordName).findFirst()
//        realm.executeTransaction {
//            historyWord?.isFavorite = isFavorite
//        }
//        return historyWord?.isFavorite ?: false
//    }
    private fun loginAnonymously() {
        firebaseAuth.signInAnonymously().addOnCompleteListener {
            if (it.isSuccessful && firebaseAuth.currentUser != null) {
                Log.d(TAG, "signInAnonymously:success");
            } else {
                Log.e(TAG, "signInAnonymously:failure", it.exception);
            }
        }
    }
}
