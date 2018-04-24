package com.mydictionary.data.wordlistrepo

import com.mydictionary.data.wordlistrepo.datasource.cache.WordListCache
import com.mydictionary.data.wordlistrepo.datasource.remote.WordListDataSource
import com.mydictionary.data.wordlistrepo.pojo.WordListDto
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
        .create<List<WordListDto>> { emitter ->
            if (cache.get()?.isNotEmpty() == true) emitter.onSuccess(cache.get()!!)
            else remoteWordListDataSource.getAllWordLists()
        }
        .doOnEvent { t1, t2 ->
            t1?.let { cache.put(t1) }
        }
        .map { mapper.mapListOfWordList(it) }


    override fun getWordList(wordListName: String): Single<WordList> = Single
        .create<WordListDto> { emitter ->
            if (cache.get()?.isNotEmpty() == true) {
                cache.get()?.find { it.name == wordListName }?.let {
                    emitter.onSuccess(it)
                }
            } else remoteWordListDataSource.getWordList(wordListName)
        }
        .map { mapper.mapWordList(it) }
}