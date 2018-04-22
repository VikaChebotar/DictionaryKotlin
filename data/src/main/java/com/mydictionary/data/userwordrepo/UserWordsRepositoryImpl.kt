package com.mydictionary.data.userwordrepo

import android.content.Context
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mydictionary.data.R
import com.mydictionary.data.userwordrepo.pojo.UserWordDto
import com.mydictionary.domain.entity.PagedResult
import com.mydictionary.domain.entity.SortingOption
import com.mydictionary.domain.entity.UserWord
import com.mydictionary.domain.repository.UserWordRepository
import io.reactivex.*
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserWordsRepositoryImpl @Inject constructor(val firebaseDatabase: FirebaseDatabase,
                                                  val firebaseAuth: FirebaseAuth,
                                                  val context: Context,
                                                  val mapper: UserWordMapper) :
        UserWordRepository {

    override fun getUserWords(offset: Int, pageSize: Int,
                              sortingOption: SortingOption, isFavorite: Boolean)
            : Single<PagedResult<UserWord>> =
            Single.create({ emitter: SingleEmitter<PagedResult<UserWord>> ->
                val query = getUserReferenceQuery(sortingOption)
                query.keepSynced(true)
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError?) {
                        emitter.onError(p0?.toException()
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
                                .map { it -> mapper.mapUserWord(it) }
                        emitter.onSuccess(PagedResult(modifiedList, list.size))
                    }
                })
            })
                    //hack to return to background thread, because onDataChange is always called in UI thread
                    .observeOn(Schedulers.io())

    override fun getUserWord(word: String): Flowable<UserWord> =
            Flowable.create<UserWordDto>({ emitter ->
                val reference = getUserReferenceQuery().equalTo(word).limitToFirst(1)
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
                    .map { mapper.mapUserWord(it) }


    override fun addOrUpdateUserWord(userWord: UserWord) =
            Completable.create { emitter ->
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

    private fun getUserReferenceQuery(sortingOption: SortingOption = SortingOption.BY_NAME): Query {
        var query: Query = getUserReference()
        when (sortingOption) {
            SortingOption.BY_DATE -> query = query.orderByChild("accessTime")
            SortingOption.BY_NAME -> query = query.orderByChild("word")
            SortingOption.RANDOMLY -> {
            }
        }
        return query
    }

    private fun getUserReference() =
            firebaseDatabase.reference.child("users").child(firebaseAuth.currentUser?.uid)

}