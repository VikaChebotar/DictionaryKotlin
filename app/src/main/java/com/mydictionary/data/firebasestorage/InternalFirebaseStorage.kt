package com.mydictionary.data.firebasestorage

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.mydictionary.R
import com.mydictionary.commons.AuthorizationException
import com.mydictionary.commons.MAX_HISTORY_LIMIT
import com.mydictionary.data.firebasestorage.dto.UserWord
import com.mydictionary.data.firebasestorage.dto.WordList
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
    private val allWordLists = mutableListOf<WordList>()

    init {
        firebaseDatabase.setPersistenceEnabled(true)
    }

    fun isLoggedIn(): Single<Boolean> =
        Single.create { emitter -> emitter.onSuccess(firebaseAuth.currentUser != null) }

    fun loginFirebaseUser(googleToken: String?): Single<String> = Single.create { emitter ->
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
                emitter.onError(
                    task.exception
                            ?: Exception(context.getString(R.string.login_error))
                )
            }
        }
    }

    fun logoutFirebaseUser(): Completable = Completable.create { emitter ->
        firebaseAuth.signOut()
        emitter.onComplete()
    }

    fun getUserName(): Single<String> = Single.create<String> { emitter ->
        firebaseAuth.currentUser?.let { emitter.onSuccess(it.email!!) } ?: emitter.onError(
            AuthorizationException()
        )
    }

    fun getHistoryWords(): Flowable<List<String>> = Flowable.create<List<String>>({ emitter ->
        if (firebaseAuth.currentUser == null) {
            emitter.onError(AuthorizationException(context.getString(R.string.sign_in_message_favorites)))
            return@create
        }
        val query = getUserReference().orderByChild("accessTime").limitToLast(MAX_HISTORY_LIMIT)
        query.keepSynced(true)
        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                val list = mutableListOf<String>()
                p0?.children?.mapNotNullTo(list) {
                    it.getValue<UserWord>(UserWord::class.java)?.word
                }
                emitter.onNext(list.reversed())
            }
        })
    }, BackpressureStrategy.DROP)

    fun getFavoriteWords(
        offset: Int,
        pageSize: Int,
        sortingOption: SortingOption
    ): Flowable<UserWord> =
        Flowable.create<UserWord>({ emitter ->
            if (firebaseAuth.currentUser == null) {
                emitter.onError(AuthorizationException(context.getString(R.string.sign_in_message_favorites)))
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
                    emitter.onError(
                        p0?.toException()
                                ?: Exception(context.getString(R.string.default_error))
                    )
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
            observeOn(Schedulers.io()).filter { it.favSenses.isNotEmpty() }.skip(offset.toLong()).take(
            pageSize.toLong()
        )

    fun getFavoriteWordsCount(): Single<Int> = Single.create({ emitter ->
        if (firebaseAuth.currentUser == null) {
            if (!emitter.isDisposed)
                emitter.onError(AuthorizationException(context.getString(R.string.sign_in_message_favorites)))
            return@create
        }
        val query = getUserReference().orderByChild("accessTime")
        query.keepSynced(true)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                emitter.onError(
                    p0?.toException()
                            ?: Exception(context.getString(R.string.default_error))
                )
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val list = mutableListOf<UserWord>()
                p0?.children?.mapNotNullTo(list) { it.getValue<UserWord>(UserWord::class.java) }
                val totalSize = list.filter { it.favSenses.isNotEmpty() }.size
                emitter.onSuccess(totalSize)
            }
        })
    })

    fun addWordToHistoryAndGet(word: String): Single<UserWord> = Single.create { emitter ->
        if (firebaseAuth.currentUser == null) {
            emitter.onError(AuthorizationException())
        }

        val userReferenceQuery = getUserReference()
        userReferenceQuery.orderByChild("word").equalTo(word)
            .addListenerForSingleValueEvent(object : ValueEventListener {
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
                            emitter.onError(
                                it.exception ?: Exception("error in addingWordToHistory")
                            )
                        }
                    }
                    if (list.isNotEmpty()) {
                        userWord = list[0]
                        userWord.updateAccessTime()
                        userReferenceQuery.child(word).setValue(userWord)
                            .addOnCompleteListener(saveCompleteListener)
                    } else {
                        userReferenceQuery.child(userWord.word).setValue(userWord)
                            .addOnCompleteListener(saveCompleteListener)
                    }
                }
            })
    }

    fun setWordFavoriteState(wordName: String, favMeanings: List<String>): Single<UserWord> =
        Single.create<UserWord> { emitter ->
            if (firebaseAuth.currentUser == null) {
                emitter.onError(AuthorizationException(context.getString(R.string.sign_in_message_favorites)))
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

    fun getAllWordLists(): Single<List<WordList>> = Single.create<List<WordList>> { emitter ->
        if (allWordLists.isNotEmpty()) emitter.onSuccess(allWordLists)
        val query = getWordListReference()
        query.keepSynced(true)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                emitter.onError(p0?.toException() ?: Exception("error in getAllWordLists"))
            }

            override fun onDataChange(p0: DataSnapshot?) {
                allWordLists.clear()
                p0?.children?.mapNotNullTo(allWordLists) { it.getValue<WordList>(WordList::class.java) }
                emitter.onSuccess(allWordLists)
            }
        })
    }

    fun getWordList(wordListName: String): Single<List<String>> =
        Single.create<List<String>> { emitter ->
            if (allWordLists.isNotEmpty()) {
                allWordLists.find { it.name == wordListName }?.let {
                    emitter.onSuccess(it.list)
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
                    val wordList = mutableListOf<WordList>()
                    p0?.children?.mapNotNullTo(wordList) { it.getValue<WordList>(WordList::class.java) }
                    if (wordList.isNotEmpty()) {
                        emitter.onSuccess(wordList[0].list)
                    } else {
                        emitter.onError(Exception("No such list"))
                    }
                }
            })
        }. //hack to return to background thread, because onDataChange is always called in UI thread
            observeOn(Schedulers.io())

    private fun getUserReference() =
        firebaseDatabase.reference.child("users").child(firebaseAuth.currentUser?.uid)

    private fun getWordListReference() = firebaseDatabase.reference.child("wordlist")
}
