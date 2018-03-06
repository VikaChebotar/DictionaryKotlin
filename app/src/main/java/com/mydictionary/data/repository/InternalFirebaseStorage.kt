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
    private val firebaseAuth = FirebaseAuth.getInstance();
    private var firebaseDatabase = FirebaseDatabase.getInstance()
    private val userWordsList = mutableListOf<UserWord>()
    private val valueEventListener = object : ValueEventListener {
        override fun onCancelled(p0: DatabaseError?) {
            Log.e(TAG, "failed to get firebase updates: " + p0?.toException()?.message)
        }

        override fun onDataChange(userWords: DataSnapshot?) {
            userWordsList.clear()
            userWords?.children?.mapNotNullTo(userWordsList) { it.getValue<UserWord>(UserWord::class.java) }
        }
    }

    init {
        firebaseDatabase.setPersistenceEnabled(true)
        if (getCurrentUser() != null) {
            registerUserWordsListener()
            //TODO where to remove listener?
        }
    }

    fun getCurrentUser() = firebaseAuth.currentUser

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
                            registerUserWordsListener()
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
        getUserReference().removeEventListener(valueEventListener)
        firebaseAuth.signOut();
    }

    fun getHistoryWords(listener: RepositoryListener<List<String>>) {
        if (firebaseAuth.currentUser == null) {
            listener.onError(context.getString(R.string.sign_in_message))
            return
        }
        listener.onSuccess(userWordsList.reversed().take(MAX_HISTORY_LIMIT).map { it.word }.toList())
    }

    fun getFavoriteWords(offset: Int, pageSize: Int, listener: RepositoryListener<List<UserWord>>) {
        if (firebaseAuth.currentUser == null) {
            listener.onError(context.getString(R.string.sign_in_message))
            return
        }
        getUserReference().orderByChild("accessTime").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                listener.onError(p0?.toException()?.message ?: context.getString(R.string.default_error))
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val list = mutableListOf<UserWord>()
                p0?.children?.mapNotNullTo(list) { it.getValue<UserWord>(UserWord::class.java) }
                val favPageList = list.filter { it.favSenses.isNotEmpty() }.reversed().drop(offset).take(pageSize)
                listener.onSuccess(favPageList)
            }
        })

    }

    fun addWordToHistoryAndGet(word: String, listener: RepositoryListener<UserWord?>) {
        if (firebaseAuth.currentUser == null) {
            listener.onError(context.getString(R.string.sign_in_message))
            return
        }
        val userReferenceQuery = getUserReference()
        //userReferenceQuery.keepSynced(true)
        val userSavedWord = userWordsList.find { it.word == word }
        if (userSavedWord != null) {
            listener.onSuccess(userSavedWord)
            userSavedWord.updateAccessTime()
            userReferenceQuery.child(word).setValue(userSavedWord).addOnCompleteListener {
                if (!it.isSuccessful) {
                    Log.e(TAG, it.exception?.message ?: "error in addingWordToHistory update time")
                }
            }
        } else {
            val userWord = UserWord(word)
            userReferenceQuery.child(userWord.word).setValue(userWord).addOnCompleteListener {
                if (!it.isSuccessful) {
                    Log.e(TAG, it.exception?.message ?: "error in addingWordToHistory")
                }
            }
            listener.onSuccess(userWord)
        }
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

    private fun getUserReference() = firebaseDatabase.
            reference.
            child("users").
            child(firebaseAuth.currentUser?.uid)

    private fun registerUserWordsListener() {
        val query = getUserReference().orderByChild("accessTime")
        query.keepSynced(true)
        query.addValueEventListener(valueEventListener)
    }

}
