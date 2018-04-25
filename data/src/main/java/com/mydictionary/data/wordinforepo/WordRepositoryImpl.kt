package com.mydictionary.data.wordinforepo

import android.content.Context
import com.mydictionary.data.await
import com.mydictionary.data.wordinforepo.datasource.cache.WordInfoCache
import com.mydictionary.data.wordinforepo.datasource.remote.RemoteWordsDataSource
import com.mydictionary.data.wordinforepo.datasource.remote.WordInfoMapper
import com.mydictionary.domain.entity.DetailWordInfo
import com.mydictionary.domain.entity.Result
import com.mydictionary.domain.entity.ShortWordInfo
import com.mydictionary.domain.repository.WordRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepositoryImpl @Inject constructor(
    val context: Context,
    val dataSource: RemoteWordsDataSource,
    val wordsCache: WordInfoCache,
    val mapper: WordInfoMapper
) : WordRepository {

    override suspend fun searchWord(searchPhrase: String, searchLimit: Int): Result<List<String>> {
        return try {
            val searchResult =
                dataSource.searchTheWord(searchPhrase, searchLimit).await().searchResults
            Result.Success(searchResult)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getWordInfo(wordName: String): Result<DetailWordInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getShortWordInfo(wordName: String): Result<ShortWordInfo> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

//    override fun searchWord(searchPhrase: String, searchLimit: Int) =
//        dataSource.searchTheWord(searchPhrase, searchLimit).map { it.searchResults }
//
//    override fun getWordInfo(word: String): Single<DetailWordInfo> =
//        Single.just(word)
//            .flatMap {
//                val wordDetails = wordsCache.get(word)
//                val needToLoadDetails = wordDetails == null
//                val needToLoadRelatedWords = needToLoadDetails && wordDetails?.second == null
//                if (!needToLoadDetails) {
//                    Single.just(wordDetails)
//                } else if (needToLoadDetails && needToLoadRelatedWords) {
//                    Single.zip(
//                        dataSource.getWordInfo(word),
//                        dataSource.getRelatedWords(word)
//                            .onErrorReturn { RelatedWordsResponse() },
//                        mapper.zipTwoResponseResults()
//                    )
//                } else Single.zip(
//                    Single.just(wordDetails!!.first),
//                    dataSource.getRelatedWords(word).onErrorReturn { RelatedWordsResponse() },
//                    mapper.zipTwoResponseResults()
//                )
//            }
//            .doOnSuccess { it?.let { wordsCache.put(word, it) } }
//            .map { it ->
//                mapper.map(it.first, it.second)
//            }
//            .flatMap {
//                if (it.meanings.isEmpty()
//                    && it.notes?.isEmpty() == true
//                    && it.synonyms?.isEmpty() == true
//                    && it.antonyms?.isEmpty() == true
//                )
//                    Single.error(Exception(context.getString(R.string.word_not_found_error)))
//                else Single.just(it)
//            }
//
//    override fun getShortWordInfo(word: String): Single<ShortWordInfo> =
//        Single.just(word)
//            .flatMap {
//                val wordDetails = wordsCache.get(word)
//                if (wordDetails == null) {
//                    dataSource.getWordInfo(word)
//                } else Single.just(wordDetails.first)
//            }
//            .doOnSuccess {
//                wordsCache.put(
//                    word,
//                    Pair<WordDetailsResponse, RelatedWordsResponse?>(it, null)
//                )
//            }
//            .map { mapper.map(it) }


}