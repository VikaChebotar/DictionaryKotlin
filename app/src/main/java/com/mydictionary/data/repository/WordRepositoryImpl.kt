package com.mydictionary.data.repository

import com.mydictionary.data.oxfordapi.OxfordDictionaryStorage
import com.mydictionary.domain.repository.WordRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepositoryImpl @Inject constructor(val oxfordStorage: OxfordDictionaryStorage) :
        WordRepository {

    override fun searchWord(searchPhrase: String, searchLimit: Int) =
            oxfordStorage.searchTheWord(searchPhrase, searchLimit)

    override fun getWordInfo(wordName: String) =
            oxfordStorage.getFullWordInfo(wordName)

    override fun getShortWordInfo(wordName: String) = oxfordStorage.getShortWordInfo(wordName)

}