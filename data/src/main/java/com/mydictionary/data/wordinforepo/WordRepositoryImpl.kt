package com.mydictionary.data.wordinforepo

import android.content.Context
import com.mydictionary.data.R
import com.mydictionary.data.wordinforepo.pojo.RelatedWordsResponse
import com.mydictionary.data.wordinforepo.pojo.WordDetailsResponse
import com.mydictionary.data.wordinforepo.restapi.WordsAPI
import com.mydictionary.domain.entity.DetailWordInfo
import com.mydictionary.domain.entity.ShortWordInfo
import com.mydictionary.domain.repository.WordRepository
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepositoryImpl @Inject constructor(val context: Context,
                                             val restApi: WordsAPI,
                                             val wordsCache: WordInfoCache,
                                             val mapper: WordInfoMapper) : WordRepository {

    override fun searchWord(searchPhrase: String, searchLimit: Int) =
            restApi.searchTheWord(searchPhrase, searchLimit).map { it.searchResults }

    override fun getWordInfo(word: String): Single<DetailWordInfo> =
            Single.just(word)
                    .flatMap {
                        val wordDetails = wordsCache.get(word)
                        val needToLoadDetails = wordDetails == null
                        val needToLoadRelatedWords = needToLoadDetails && wordDetails?.second == null
                        if (!needToLoadDetails) {
                            Single.just(wordDetails)
                        } else if (needToLoadDetails && needToLoadRelatedWords) {
                            Single.zip(
                                    restApi.getWordInfo(word),
                                    restApi.getRelatedWords(word)
                                            .onErrorReturn { RelatedWordsResponse() },
                                    mapper.zipTwoResponseResults())
                        } else Single.zip(
                                Single.just(wordDetails!!.first),
                                restApi.getRelatedWords(word).onErrorReturn { RelatedWordsResponse() },
                                mapper.zipTwoResponseResults())
                    }
                    .doOnSuccess { wordsCache.put(word, it) }
                    .map { it ->
                        mapper.map(it.first, it.second)
                    }
                    .flatMap {
                        if (it.meanings.isEmpty()
                                && it.notes?.isEmpty() == true
                                && it.synonyms?.isEmpty() == true
                                && it.antonyms?.isEmpty() == true)
                            Single.error(Exception(context.getString(R.string.word_not_found_error)))
                        else Single.just(it)
                    }

    override fun getShortWordInfo(word: String): Single<ShortWordInfo> =
            Single.just(word)
                    .flatMap {
                        val wordDetails = wordsCache.get(word)
                        if (wordDetails == null) {
                            restApi.getWordInfo(word)
                        } else Single.just(wordDetails.first)
                    }
                    .doOnSuccess { wordsCache.put(word, Pair<WordDetailsResponse, RelatedWordsResponse?>(it, null)) }
                    .map { mapper.map(it) }


}