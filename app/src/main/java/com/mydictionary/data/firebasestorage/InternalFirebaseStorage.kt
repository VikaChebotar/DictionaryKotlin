package com.mydictionary.data.firebasestorage

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*
import com.mydictionary.R
import com.mydictionary.commons.AuthorizationException
import com.mydictionary.data.firebasestorage.dto.UserWordDto
import com.mydictionary.data.firebasestorage.dto.WordListDto
import com.mydictionary.domain.entity.*
import io.reactivex.*
import io.reactivex.schedulers.Schedulers


/**
 * Created by Viktoria_Chebotar on 6/12/2017.
 */
class InternalFirebaseStorage(
        val context: Context,
        val firebaseAuth: FirebaseAuth,
        val firebaseDatabase: FirebaseDatabase
) {
    private val TAG = InternalFirebaseStorage::class.java.simpleName
    private val allWordLists = mutableListOf<WordListDto>()

    init {
        firebaseDatabase.setPersistenceEnabled(true)
    }

    fun loginFirebaseUser(googleToken: String): Single<User> =
            Single.create<FirebaseUser> { emitter ->
                //        if (firebaseAuth.currentUser != null) {
//            emitter.onError(Exception("User is already signedIn"))
//            return@create
//        }
                val credential = GoogleAuthProvider.getCredential(googleToken, null)
                firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result.user != null) {
                        emitter.onSuccess(task.result.user!!)
                    } else {
                        Log.e(TAG, "firebaseAuthWithGoogle:failure", task.getException())
                        emitter.onError(
                                task.exception
                                        ?: Exception(context.getString(R.string.login_error))
                        )
                    }
                }
            }
                    .map { User(it.uid, it.email!!) }

    fun logoutFirebaseUser(): Completable = Completable.create { emitter ->
        firebaseAuth.signOut()
        emitter.onComplete()
    }

    fun getUserName(): Single<String> = Single.create<String> { emitter ->
        firebaseAuth.currentUser?.let { emitter.onSuccess(it.email!!) } ?: emitter.onError(
                AuthorizationException()
        )
    }

    fun getUser(): Single<User> = Single.create<User> { emitter ->
        firebaseAuth.currentUser?.let { emitter.onSuccess(User(it.uid, it.email!!)) }
                ?: emitter.onError(AuthorizationException())
    }

    fun getUserWord(word: String) = Flowable.create<UserWordDto>({ emitter ->
        val reference = getUserReference().orderByChild("word").equalTo(word).limitToFirst(1)
        var userWordDto: UserWordDto? = null
        val valueListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                emitter.onError(p0?.toException() ?: Exception())
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val list = mutableListOf<UserWordDto>()
                p0?.children?.mapNotNullTo(list) { it.getValue<UserWordDto>(UserWordDto::class.java) }
                if (list.isNotEmpty()) {
                    if (list[0] != userWordDto) { //can happen when accessdate was
                        userWordDto = list[0]
                        emitter.onNext(userWordDto!!)
                    }
                } else emitter.onError(Exception("User don't have stored word " + word))
            }
        }
        reference.addValueEventListener(valueListener)
        emitter.setCancellable { reference.removeEventListener(valueListener) }
    }, BackpressureStrategy.DROP)
            .map { UserWord(it.word, it.favSenses) }

    fun addOrUpdateUserWord(userWord: UserWord) = Completable.create { emitter ->
        val saveCompleteListener = OnCompleteListener<Void> { it ->
            if (it.isSuccessful) {
                emitter.onComplete()
            } else {
                emitter.onError(it.exception
                        ?: Exception("error in addingWordToHistory"))
            }
        }
        val userWordDto = UserWordDto(userWord.word, userWord.favMeanings)
        getUserReference().child(userWord.word).setValue(userWordDto)
                .addOnCompleteListener(saveCompleteListener)
    }


    fun getAllWordLists(): Single<List<WordList>> = Single.create<List<WordListDto>> { emitter ->
        if (allWordLists.isNotEmpty()) emitter.onSuccess(allWordLists)
        val query = getWordListReference()
        query.keepSynced(true)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                emitter.onError(p0?.toException() ?: Exception("error in getAllWordLists"))
            }

            override fun onDataChange(p0: DataSnapshot?) {
                allWordLists.clear()
                p0?.children?.mapNotNullTo(allWordLists) { it.getValue<WordListDto>(WordListDto::class.java) }

                emitter.onSuccess(allWordLists)
            }
        })
    }.map { it -> it.map { WordList(it.name, it.type, it.list) } }

    fun getWordList(wordListName: String): Single<WordList> =
            Single.create<WordListDto> { emitter ->
                if (allWordLists.isNotEmpty()) {
                    allWordLists.find { it.name == wordListName }?.let {
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
                    .map { it -> WordList(it.name, it.type, it.list) }

    private fun getUserReference() =
            firebaseDatabase.reference.child("users").child(firebaseAuth.currentUser?.uid)

    private fun getWordListReference() = firebaseDatabase.reference.child("wordlist")

    fun getUserWords(
            offset: Int,
            pageSize: Int,
            sortingOption: SortingOption,
            isFavorite: Boolean
    ) = Single.create({ emitter: SingleEmitter<PagedResult<UserWord>> ->
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
                var list = mutableListOf<UserWordDto>()
                p0?.children?.mapNotNullTo(list) { it.getValue<UserWordDto>(UserWordDto::class.java) }
                when (sortingOption) {
                    SortingOption.BY_DATE -> list.reverse()
                    SortingOption.BY_NAME -> {
                    }
                    SortingOption.RANDOMLY -> list.shuffle()
                }
                list = list.toMutableList()
                        .filter { if (isFavorite) it.favSenses.isNotEmpty() else true }
                        .toMutableList()
                val modifiedList = list
                        .drop(offset)
                        .take(pageSize)
                        .map { it ->
                            com.mydictionary.domain.entity.UserWord(
                                    it.word,
                                    it.favSenses
                            )
                        }
                emitter.onSuccess(PagedResult(modifiedList, list.size))
            }
        })
    })
            //hack to return to background thread, because onDataChange is always called in UI thread
            .observeOn(Schedulers.io())
}
