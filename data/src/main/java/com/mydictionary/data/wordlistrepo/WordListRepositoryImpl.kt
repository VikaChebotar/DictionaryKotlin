package com.mydictionary.data.wordlistrepo

import com.mydictionary.data.wordlistrepo.datasource.cache.WordListCache
import com.mydictionary.data.wordlistrepo.datasource.remote.WordListDataSource
import com.mydictionary.domain.entity.WordList
import com.mydictionary.domain.repository.WordListRepository
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordListRepositoryImpl @Inject constructor(
    val remoteWordListDataSource: WordListDataSource,
    val cache: WordListCache,
    val mapper: WordListMapper
) : WordListRepository {

    override fun getAllWordLists(): Single<List<WordList>> = Single
        .just("")
        .flatMap {
            if (cache.get()?.isNotEmpty() == true) Single.just(cache.get()!!)
            else remoteWordListDataSource.getAllWordLists()
        }
        .doOnEvent { t1, t2 ->
            t1?.let { cache.put(t1) }
        }
        .map { mapper.mapListOfWordList(it) }


    override fun getWordList(wordListName: String): Single<WordList> = Single.just("")
        .flatMap {
            if (cache.get()?.isNotEmpty() == true) {
                cache.get()?.find { it.name == wordListName }?.let {
                    Single.just(it)
                }
            } else remoteWordListDataSource.getWordList(wordListName)
        }
        .map { mapper.mapWordList(it) }
}