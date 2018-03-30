package com.mydictionary.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.mydictionary.R
import com.mydictionary.commons.Constants.Companion.MAX_HISTORY_LIMIT
import com.mydictionary.data.entity.UserWord
import com.mydictionary.data.pojo.SortingOption
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*


/**
 * Created by Viktoria_Chebotar on 6/12/2017.
 */
class InternalFirebaseStorage(val context: Context) {
    private val TAG = InternalFirebaseStorage::class.java.simpleName
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

    private val connectionListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val connected = snapshot.getValue(Boolean::class.java)!!
            Log.d(TAG, if (connected) "firebase is connected" else "firebase is not connected")
        }

        override fun onCancelled(error: DatabaseError) {
            Log.d(TAG, "firebase connection listener on cancelled")
        }
    };

    init {
        firebaseDatabase.setPersistenceEnabled(true)
    }

    fun isLoggedIn(): Single<Boolean> = Single.create { emitter -> emitter.onSuccess(firebaseAuth.currentUser != null) }

    fun loginFirebaseUser(googleToken: String?): Single<String> = Single.create { emitter ->
        if (firebaseAuth.currentUser != null) {
            emitter.onError(Exception("User is already signedIn"))
            return@create
        }
        val credential = GoogleAuthProvider.getCredential(googleToken, null)
        firebaseAuth.signInWithCredential(credential).
                addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val email = task.result.user.email
                        if (email != null) {
                            emitter.onSuccess(email)
                            registerUserWordsListener()
                        } else {
                            emitter.onError(Exception(context.getString(R.string.login_error)))
                        }
                    } else {
                        Log.e(TAG, "firebaseAuthWithGoogle:failure", task.getException())
                        emitter.onError(task.exception ?: Exception(context.getString(R.string.login_error)))
                    }
                }
    }

    fun logoutFirebaseUser(): Completable = Completable.create { emitter ->
        getUserReference().removeEventListener(valueEventListener)
        firebaseAuth.signOut()
        emitter.onComplete()
    }

    fun getHistoryWords(listener: RepositoryListener<List<String>>) {
        if (firebaseAuth.currentUser == null) {
            listener.onError(context.getString(R.string.sign_in_message))
            return
        }
        listener.onSuccess(userWordsList.reversed().take(MAX_HISTORY_LIMIT).map { it.word }.toList())
    }

    fun getFavoriteWords(offset: Int, pageSize: Int, sortingOption: SortingOption): Flowable<UserWord> = Flowable.create<UserWord>({ emitter ->
        if (firebaseAuth.currentUser == null) {
            emitter.onError(Exception(context.getString(R.string.sign_in_message)))
            return@create
        }
        var query: Query = getUserReference()
        when (sortingOption) {
            SortingOption.BY_DATE ->  query = query.orderByChild("accessTime")
            SortingOption.BY_NAME -> query = query.orderByChild("word")
            SortingOption.RANDOMLY -> {}
        }
        query.keepSynced(true)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                emitter.onError(p0?.toException() ?: Exception(context.getString(R.string.default_error)))
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val list = mutableListOf<UserWord>()
                p0?.children?.mapNotNullTo(list) { it.getValue<UserWord>(UserWord::class.java) }
                //  val favPageList = list.filter { it.favSenses.isNotEmpty() }.reversed().drop(offset).take(pageSize)
                when (sortingOption) {
                    SortingOption.BY_DATE -> list.reverse()
                    SortingOption.BY_NAME -> {
                    }
                    SortingOption.RANDOMLY -> Collections.shuffle(list)
                }
                list.forEach { emitter.onNext(it) }
                emitter.onComplete()
            }
        })
    }, BackpressureStrategy.DROP).
            observeOn(Schedulers.io()). //hack to return to background thread, because onDataChange is always called in UI thread
            filter { it.favSenses.isNotEmpty() }.
            skip(offset.toLong()).
            take(pageSize.toLong())

    fun getFavoriteWordsCount(): Single<Int> = Single.create({ emitter ->
        if (firebaseAuth.currentUser == null) {
            emitter.onError(Exception(context.getString(R.string.sign_in_message)))
            return@create
        }
        val query = getUserReference().orderByChild("accessTime")
        query.keepSynced(true)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                emitter.onError(p0?.toException() ?: Exception(context.getString(R.string.default_error)))
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val list = mutableListOf<UserWord>()
                p0?.children?.mapNotNullTo(list) { it.getValue<UserWord>(UserWord::class.java) }
                val totalSize = list.filter { it.favSenses.isNotEmpty() }.size
                emitter.onSuccess(totalSize)
            }
        })
    })
//    {
//        if (firebaseAuth.currentUser == null) {
//            listener.onError(context.getString(R.string.sign_in_message))
//            return
//        }
//        getUserReference().orderByChild("accessTime").addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onCancelled(p0: DatabaseError?) {
//                listener.onError(p0?.toException()?.message ?: context.getString(R.string.default_error))
//            }
//
//            override fun onDataChange(p0: DataSnapshot?) {
//                val list = mutableListOf<UserWord>()
//                p0?.children?.mapNotNullTo(list) { it.getValue<UserWord>(UserWord::class.java) }
//                val favPageList = list.filter { it.favSenses.isNotEmpty() }.reversed().drop(offset).take(pageSize)
//                listener.onSuccess(favPageList)
//            }
//        })
//    }

    fun addWordToHistoryAndGet(word: String): Single<UserWord> = Single.create { emitter ->
        if (firebaseAuth.currentUser == null) {
            emitter.onError(Exception(context.getString(R.string.sign_in_message)))
        }

        val userReferenceQuery = getUserReference()
        //userReferenceQuery.keepSynced(true)
        val userSavedWord = userWordsList.find { it.word == word }
        if (userSavedWord != null) {
            userSavedWord.updateAccessTime()
            userReferenceQuery.child(word).setValue(userSavedWord).addOnCompleteListener {
                if (it.isSuccessful) {
                    emitter.onSuccess(userSavedWord)
                } else {
                    emitter.onError(it.exception ?: Exception("error in addingWordToHistory update time"))
                }
            }
        } else {
            val userWord = UserWord(word)
            userReferenceQuery.child(userWord.word).setValue(userWord).addOnCompleteListener {
                if (it.isSuccessful) {
                    emitter.onSuccess(userWord)
                } else {
                    emitter.onError(it.exception ?: Exception("error in addingWordToHistory"))
                }
            }
        }
    }


    fun setWordFavoriteState(wordName: String, favMeanings: List<String>, listener: RepositoryListener<List<String>>) {
        if (firebaseAuth.currentUser == null) {
            listener.onError(context.getString(R.string.sign_in_message))
            return
        }

        val userWord = userWordsList.find { it.word == wordName } ?: UserWord(wordName)
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

    private fun unregisterUserWordsListener() {
        val query = getUserReference().orderByChild("accessTime")
        query.removeEventListener(valueEventListener)
    }

    private fun registerConnectionListener() {
        val firebaseConnectedRef = firebaseDatabase.getReference(".info/connected")
        firebaseConnectedRef.addValueEventListener(connectionListener)
    }

    private fun unregisterConnectionListener() {
        val firebaseConnectedRef = firebaseDatabase.getReference(".info/connected")
        firebaseConnectedRef.removeEventListener(connectionListener)
    }

    fun onAppForeground() {
        FirebaseDatabase.getInstance().goOnline()
        if (firebaseAuth.currentUser != null) {
            registerUserWordsListener()
        }
        registerConnectionListener()
    }

    fun onAppBackground() {
        if (firebaseAuth.currentUser != null) {
            unregisterUserWordsListener()
        }
        unregisterConnectionListener()
        FirebaseDatabase.getInstance().goOffline()
    }

}
