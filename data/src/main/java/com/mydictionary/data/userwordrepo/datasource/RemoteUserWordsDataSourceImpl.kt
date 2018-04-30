package com.mydictionary.data.userwordrepo.datasource

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.mydictionary.data.await
import com.mydictionary.data.listenAsync
import com.mydictionary.data.userwordrepo.pojo.UserWordDto
import com.mydictionary.domain.entity.PagedResult
import com.mydictionary.domain.entity.SortingOption
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.channels.produce


class RemoteUserWordsDataSourceImpl(
        val firebaseDatabase: FirebaseDatabase,
        val firebaseAuth: FirebaseAuth,
        val context: Context
) : UserWordsDataSource {

    override suspend fun getUserWords(offset: Int, pageSize: Int, sortingOption: SortingOption,
                                      isFavorite: Boolean): PagedResult<UserWordDto> {
        val query = getUserReferenceQuery(sortingOption)
        query.keepSynced(true)
        val snapshot = query.await()
        var list = mutableListOf<UserWordDto>()
        snapshot.children?.mapNotNullTo(list) { it.getValue<UserWordDto>(UserWordDto::class.java) }
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
        return PagedResult(modifiedList, list.size)
    }

    override suspend fun getUserWord(wordName: String) = produce {
        val reference = getUserReferenceQuery().equalTo(wordName).limitToFirst(1)
        var userWordDto: UserWordDto? = null
        val snapshotChannel = reference.listenAsync()
        snapshotChannel.consumeEach {
            val list = mutableListOf<UserWordDto>()
            it.children?.mapNotNullTo(list) { it.getValue<UserWordDto>(UserWordDto::class.java) }
            if (list.isNotEmpty()) {
                if (list[0] != userWordDto) { //can happen when accessdate was
                    userWordDto = list[0]
                    send(userWordDto)
                }
            } else
            //"User don't have stored word "+ wordName
                send(null)
        }
    }

    override suspend fun addOrUpdateUserWord(userWord: UserWordDto) {
        getUserReference().child(userWord.word).setValue(userWord).await()
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