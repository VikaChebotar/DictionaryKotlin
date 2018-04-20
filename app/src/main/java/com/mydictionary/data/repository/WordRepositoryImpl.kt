package com.mydictionary.data.repository

import com.mydictionary.data.oxfordapi.OxfordDictionaryStorage
import com.mydictionary.domain.entity.DetailWordInfo
import com.mydictionary.domain.entity.ShortWordInfo
import com.mydictionary.domain.repository.WordRepository
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepositoryImpl @Inject constructor(val oxfordStorage: OxfordDictionaryStorage) :
    WordRepository {

    override fun searchWord(searchPhrase: String) = oxfordStorage.searchTheWord(searchPhrase)

    override fun getWordInfo(wordName: String): Single<DetailWordInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getShortWordInfo(wordName: String): Single<ShortWordInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}