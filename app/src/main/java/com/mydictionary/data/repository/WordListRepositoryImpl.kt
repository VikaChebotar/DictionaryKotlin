package com.mydictionary.data.repository

import com.mydictionary.data.firebasestorage.InternalFirebaseStorage
import com.mydictionary.domain.entity.WordList
import com.mydictionary.domain.repository.WordListRepository
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordListRepositoryImpl  @Inject constructor(val firebaseStorage: InternalFirebaseStorage): WordListRepository{
    override fun getAllWordLists(): Single<List<WordList>> =
        firebaseStorage.getAllWordLists()


    override fun getWordList(wordListName: String): Single<WordList>  = firebaseStorage.getWordList(wordListName)

}