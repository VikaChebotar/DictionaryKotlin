package com.mydictionary.data.repository

import com.mydictionary.data.firebasestorage.InternalFirebaseStorage
import com.mydictionary.domain.entity.SortingOption
import com.mydictionary.domain.entity.UserWord
import com.mydictionary.domain.repository.UserWordRepository
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserWordsRepositoryImpl @Inject constructor(val firebaseStorage: InternalFirebaseStorage) :
    UserWordRepository {

    override fun getUserWords(
        offset: Int,
        pageSize: Int,
        sortingOption: SortingOption,
        isFavorite: Boolean
    ) = firebaseStorage.getUserWords(offset, pageSize, sortingOption, isFavorite)

    override fun getUserWord(wordName: String): Observable<UserWord> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addOrUpdateUserWord(userWord: UserWord): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}