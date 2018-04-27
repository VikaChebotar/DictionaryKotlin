package com.mydictionary.data.wordlistrepo

import com.mydictionary.data.wordlistrepo.datasource.cache.WordListCache
import com.mydictionary.data.wordlistrepo.datasource.remote.WordListDataSource
import com.mydictionary.domain.entity.Result
import com.mydictionary.domain.entity.WordList
import com.mydictionary.domain.repository.WordListRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordListRepositoryImpl @Inject constructor(
    val remoteWordListDataSource: WordListDataSource,
    val cache: WordListCache,
    val mapper: WordListMapper
) : WordListRepository {

    override suspend fun getAllWordLists(): Result<List<WordList>> {
        val list = if (cache.get()?.isNotEmpty() == true) {
            cache.get()!!
        } else {
            try {
                val list = remoteWordListDataSource.getAllWordLists()
                cache.put(list)
                list
            } catch (e: Exception) {
                return Result.Error(e)
            }
        }
        return Result.Success(mapper.mapListOfWordList(list))
    }

    override suspend fun getWordList(wordListName: String): Result<WordList> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

//
//    override fun getWordList(wordListName: String): Single<WordList> = Single.just("")
//        .flatMap {
//            if (cache.get()?.isNotEmpty() == true) {
//                cache.get()?.find { it.name == wordListName }?.let {
//                    Single.just(it)
//                }
//            } else remoteWordListDataSource.getWordList(wordListName)
//        }
//        .map { mapper.mapWordList(it) }
}