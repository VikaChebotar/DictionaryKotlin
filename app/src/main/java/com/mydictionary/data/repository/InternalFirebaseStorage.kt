package com.mydictionary.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
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

    fun getCurrentUser() = firebaseAuth.currentUser

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
                } else {
                    listener.onSuccess(emptyList())
                }
            }
        })
    }

    fun addWordToHistoryAndGet(word: String, listener: RepositoryListener<UserWord?>) {
        val userReference = firebaseDatabase.reference.child("users")
        val query = firebaseDatabase.reference.child("users").
                child(firebaseAuth.currentUser?.uid).child(word)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                listener.onError(p0?.toException()?.message ?: context.getString(R.string.default_error))
            }

            override fun onDataChange(userWordValue: DataSnapshot?) {
                if (userWordValue?.exists() == true) {
                    val userFavWords = userWordValue.child("favSenses")
                    val userWord = UserWord(userWordValue.key)
                    userWord.value.favSenses = userFavWords.value as? List<String> ?: emptyList()
                    listener.onSuccess(userWord)
                } else {
                    val userWord = UserWord(word)
                    userReference.child(firebaseAuth.currentUser?.uid).child(userWord.word).setValue(userWord.value)
                    listener.onSuccess(userWord)
                }
            }
        })
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


    fun getWordFromHistory(wordName: String, listener: RepositoryListener<UserWord?>) {
        val query = firebaseDatabase.reference.child("users").
                child(firebaseAuth.currentUser?.uid).equalTo(wordName)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {

            }
        })
    }

//    fun isWordFavorite(wordName: String): Boolean {
//        val historyWord = realm.where(HistoryWord::class.java).equalTo("word", wordName).findFirst()
//        return historyWord?.isFavorite ?: false
//    }

    fun setWordFavoriteState(wordName: String, favMeanings: List<String>, listener: RepositoryListener<List<String>>) {
        val userReference = firebaseDatabase.reference.child("users")
        val userWord = UserWord(wordName)
        userWord.value.favSenses = favMeanings
        val task = userReference.child(firebaseAuth.currentUser?.uid).child(userWord.word).setValue(userWord.value)
        task.addOnSuccessListener { listener.onSuccess(favMeanings) }.
                addOnFailureListener { listener.onError(it.message ?: context.getString(R.string.default_error)) }
    }

    private fun loginAnonymously() {
        firebaseAuth.signInAnonymously().addOnCompleteListener {
            if (it.isSuccessful && firebaseAuth.currentUser != null) {
                Log.d(TAG, "signInAnonymously:success");
            } else {
                Log.e(TAG, "signInAnonymously:failure", it.exception);
            }
        }
    }

    fun linkGoogleAccountToFirebase(googleToken: String?, listener: RepositoryListener<String>) {
        if (firebaseAuth.currentUser?.isAnonymous != true)
            listener.onError("User is already signedIn")
        val credential = GoogleAuthProvider.getCredential(googleToken, null)
        firebaseAuth.currentUser?.linkWithCredential(credential)?.
                addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val email = task.result.user.email
                        if (email != null) {
                            listener.onSuccess(email)
                        } else {
                            listener.onError(context.getString(R.string.login_error))
                        }
                    } else {
                        Log.e(TAG, "linkGoogleAccountToFirebase:failure", task.getException())
                        listener.onError(task.exception?.message ?: context.getString(R.string.login_error))
                    }
                } ?: listener.onError(context.getString(R.string.login_error))
    }
}
