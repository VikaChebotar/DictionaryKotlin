package com.mydictionary.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mydictionary.data.entity.HistoryWord


/**
 * Created by Viktoria_Chebotar on 6/12/2017.
 */
class InternalFirebaseStorage {
    private val TAG = InternalFirebaseStorage::class.java.canonicalName
    // private val realm: Realm = Realm.getDefaultInstance()
    private val firebaseAuth = FirebaseAuth.getInstance();
    private var firebaseDatabase = FirebaseDatabase.getInstance()

    init {
        if (firebaseAuth.currentUser == null) {
            loginAnonymously()
        } else{
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
    fun getHistoryWords(limit: Int, listener: RepositoryListener<List<HistoryWord>>) {
//        val results = realm.where(HistoryWord::class.java).findAllSortedAsync("accessTime", Sort.DESCENDING)
//        results.addChangeListener { t -> listener.onSuccess(t.subList(0, Math.min(limit, t.size))) }
    }

    fun addWordToHistory(word: HistoryWord) {
        firebaseDatabase.reference.child("history").setValue(word)
//        realm.executeTransactionAsync { realm ->
//            if (realm.where(HistoryWord::class.java).count() >= Constants.MAX_HISTORY_LIMIT) {
//                val oldestWord = realm.where(HistoryWord::class.java).
//                        equalTo("isFavorite", false).
//                        findAllSorted("accessTime", Sort.ASCENDING).first()
//                oldestWord?.deleteFromRealm()
//            }
//            realm?.copyToRealmOrUpdate(word)
//        }
    }

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
