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
        firebaseDatabase.setPersistenceEnabled(true)
    }

    fun getCurrentUser() = firebaseAuth.currentUser

    fun getHistoryWords(listener: RepositoryListener<List<String>>) {
        if (firebaseAuth.currentUser == null) {
            listener.onError(context.getString(R.string.sign_in_message))
            return
        }
        val query = getUserReference().
                orderByChild("accessTime").
                limitToLast(MAX_HISTORY_LIMIT)
        query.keepSynced(true)
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
        if (firebaseAuth.currentUser == null) {
            listener.onError(context.getString(R.string.sign_in_message))
            return
        }
        val userReferenceQuery = getUserReference()
        userReferenceQuery.keepSynced(true)
        userReferenceQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                listener.onError(p0?.toException()?.message ?: context.getString(R.string.default_error))
            }

            override fun onDataChange(userWords: DataSnapshot?) {
                val userWordsList = mutableListOf<UserWord>()
                userWords?.children?.mapNotNullTo(userWordsList) { it.getValue<UserWord>(UserWord::class.java) }
                val userSavedWord = userWordsList.find { it.word == word }
                if (userSavedWord != null) {
                    listener.onSuccess(userSavedWord)
                } else {
                    val userWord = UserWord(word)
                    userReferenceQuery.child(userWord.word).setValue(userWord)
                    listener.onSuccess(userWord)
                }
            }
        })
    }


    fun setWordFavoriteState(wordName: String, favMeanings: List<String>, listener: RepositoryListener<List<String>>) {
        if (firebaseAuth.currentUser == null) {
            listener.onError(context.getString(R.string.sign_in_message))
            return
        }
        val userWord = UserWord(wordName)
        userWord.favSenses = favMeanings
        val task = getUserReference().child(userWord.word).setValue(userWord)
        task.addOnSuccessListener { listener.onSuccess(favMeanings) }.
                addOnFailureListener { listener.onError(it.message ?: context.getString(R.string.default_error)) }
    }

    fun loginFirebaseUser(googleToken: String?, listener: RepositoryListener<String>) {
        if (firebaseAuth.currentUser != null) {
            listener.onError("User is already signedIn")
            return
        }
        val credential = GoogleAuthProvider.getCredential(googleToken, null)
        firebaseAuth.signInWithCredential(credential).
                addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val email = task.result.user.email
                        if (email != null) {
                            listener.onSuccess(email)
                        } else {
                            listener.onError(context.getString(R.string.login_error))
                        }
                    } else {
                        Log.e(TAG, "firebaseAuthWithGoogle:failure", task.getException())
                        listener.onError(task.exception?.message ?: context.getString(R.string.login_error))
                    }
                }
    }

    fun logoutFirebaseUser() {
        firebaseAuth.signOut();
    }

    private fun getUserReference() = firebaseDatabase.
            reference.
            child("users").
            child(firebaseAuth.currentUser?.uid)
}
