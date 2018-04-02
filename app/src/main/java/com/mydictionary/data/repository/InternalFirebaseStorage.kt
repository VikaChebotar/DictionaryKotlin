package com.mydictionary.data.repository

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.mydictionary.R
import com.mydictionary.commons.MAX_HISTORY_LIMIT
import com.mydictionary.data.entity.UserWord
import com.mydictionary.data.pojo.SortingOption
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.Single.create
import io.reactivex.schedulers.Schedulers
import java.util.*


/**
 * Created by Viktoria_Chebotar on 6/12/2017.
 */
class InternalFirebaseStorage(val context: Context) {
    private val TAG = InternalFirebaseStorage::class.java.simpleName
    private val firebaseAuth = FirebaseAuth.getInstance();
    private var firebaseDatabase = FirebaseDatabase.getInstance()


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

    fun isLoggedIn(): Single<Boolean> = create { emitter -> emitter.onSuccess(firebaseAuth.currentUser != null) }

    fun loginFirebaseUser(googleToken: String?): Single<String> = create { emitter ->
        if (firebaseAuth.currentUser != null) {
            emitter.onError(Exception("User is already signedIn"))
            return@create
        }
        val credential = GoogleAuthProvider.getCredential(googleToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val email = task.result.user.email
                if (email != null) {
                    emitter.onSuccess(email)
                } else {
                    emitter.onError(Exception(context.getString(R.string.login_error)))
                }
            } else {
                Log.e(TAG, "firebaseAuthWithGoogle:failure", task.getException())
                emitter.onError(task.exception
                        ?: Exception(context.getString(R.string.login_error)))
            }
        }
    }

    fun logoutFirebaseUser(): Completable = Completable.create { emitter ->
        firebaseAuth.signOut()
        emitter.onComplete()
    }

    fun getHistoryWords(): Flowable<List<String>> = Flowable.create<List<String>>({ emitter ->
        if (firebaseAuth.currentUser == null) {
            emitter.onError(Exception(context.getString(R.string.sign_in_message)))
            return@create
        }
        val query = getUserReference().orderByChild("accessTime").limitToLast(MAX_HISTORY_LIMIT)
        query.keepSynced(true)
        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                val list = mutableListOf<String>()
                p0?.children?.mapNotNullTo(list) { it.getValue<UserWord>(UserWord::class.java)?.word }
                emitter.onNext(list.reversed())
            }
        })
    }, BackpressureStrategy.DROP)

    fun getFavoriteWords(offset: Int, pageSize: Int, sortingOption: SortingOption): Flowable<UserWord> =
            Flowable.create<UserWord>({ emitter ->
                if (firebaseAuth.currentUser == null) {
                    emitter.onError(Exception(context.getString(R.string.sign_in_message)))
                    return@create
                }
                var query: Query = getUserReference()
                when (sortingOption) {
                    SortingOption.BY_DATE -> query = query.orderByChild("accessTime")
                    SortingOption.BY_NAME -> query = query.orderByChild("word")
                    SortingOption.RANDOMLY -> {
                    }
                }
                query.keepSynced(true)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        emitter.onError(p0?.toException()
                                ?: Exception(context.getString(R.string.default_error)))
                    }

                    override fun onDataChange(p0: DataSnapshot?) {
                        val list = mutableListOf<UserWord>()
                        p0?.children?.mapNotNullTo(list) { it.getValue<UserWord>(UserWord::class.java) }
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
                    //hack to return to background thread, because onDataChange is always called in UI thread
                    observeOn(Schedulers.io()).
                    filter { it.favSenses.isNotEmpty() }.skip(offset.toLong()).take(pageSize.toLong())

    fun getFavoriteWordsCount(): Single<Int> = create({ emitter ->
        if (firebaseAuth.currentUser == null) {
            emitter.onError(Exception(context.getString(R.string.sign_in_message)))
            return@create
        }
        val query = getUserReference().orderByChild("accessTime")
        query.keepSynced(true)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                emitter.onError(p0?.toException()
                        ?: Exception(context.getString(R.string.default_error)))
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val list = mutableListOf<UserWord>()
                p0?.children?.mapNotNullTo(list) { it.getValue<UserWord>(UserWord::class.java) }
                val totalSize = list.filter { it.favSenses.isNotEmpty() }.size
                emitter.onSuccess(totalSize)
            }
        })
    })

    fun addWordToHistoryAndGet(word: String): Single<UserWord> = create { emitter ->
        if (firebaseAuth.currentUser == null) {
            emitter.onError(Exception(context.getString(R.string.sign_in_message)))
        }

        val userReferenceQuery = getUserReference()
        userReferenceQuery.orderByChild("word").equalTo(word).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                emitter.onError(p0?.toException() ?: Exception("error in addingWordToHistory"))
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val list = mutableListOf<UserWord>()
                p0?.children?.mapNotNullTo(list) { it.getValue<UserWord>(UserWord::class.java) }
                var userWord = UserWord(word)

                val saveCompleteListener = OnCompleteListener<Void> { it ->
                    if (it.isSuccessful) {
                        emitter.onSuccess(userWord)
                    } else {
                        emitter.onError(it.exception ?: Exception("error in addingWordToHistory"))
                    }
                }
                if (list.isNotEmpty()) {
                    userWord = list[0]
                    userWord.updateAccessTime()
                    userReferenceQuery.child(word).setValue(userWord).addOnCompleteListener(saveCompleteListener)
                } else {
                    userReferenceQuery.child(userWord.word).setValue(userWord).addOnCompleteListener(saveCompleteListener)
                }
            }
        })
    }

    fun setWordFavoriteState(wordName: String, favMeanings: List<String>): Single<UserWord> = create<UserWord> { emitter ->
        if (firebaseAuth.currentUser == null) {
            emitter.onError(Exception(context.getString(R.string.sign_in_message)))
            return@create
        }
        val userWord = UserWord(wordName)
        userWord.favSenses = favMeanings
        val task = getUserReference().child(wordName).setValue(userWord)
        task.addOnSuccessListener {
            emitter.onSuccess(userWord)
        }.addOnFailureListener { e ->
            emitter.onError(e)
        }
    }

    private fun getUserReference() = firebaseDatabase.reference.child("users").child(firebaseAuth.currentUser?.uid)


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
        registerConnectionListener()
    }

    fun onAppBackground() {
        unregisterConnectionListener()
        FirebaseDatabase.getInstance().goOffline()
    }

}
