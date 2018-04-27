package com.mydictionary.data.wordinforepo

import android.content.Context
import com.mydictionary.data.R
import com.mydictionary.data.await
import com.mydictionary.data.wordinforepo.datasource.cache.WordInfoCache
import com.mydictionary.data.wordinforepo.datasource.remote.RemoteWordsDataSource
import com.mydictionary.data.wordinforepo.datasource.remote.WordInfoMapper
import com.mydictionary.data.wordinforepo.pojo.RelatedWordsResponse
import com.mydictionary.data.wordinforepo.pojo.WordDetailsResponse
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

    override suspend fun getShortWordInfo(wordName: String): Result<ShortWordInfo> {
        val wordDetails = wordsCache.get(wordName)
        val wordDetailResponse: WordDetailsResponse = if (wordDetails == null) {
            try {
                val response = dataSource.getWordInfo(wordName).await()
                wordsCache.put(
                    wordName,
                    Pair<WordDetailsResponse, RelatedWordsResponse?>(response, null)
                )
                response
            } catch (e: Exception) {
                return Result.Error(e)
            }
        } else wordDetails.first
        return Result.Success(mapper.mapToShortWordInfo(wordDetailResponse))
    }

    override suspend fun getWordInfo(wordName: String): Result<DetailWordInfo> {
        val errorResult = Result.Error(Exception(context.getString(R.string.word_not_found_error)))
        val wordDetails = wordsCache.get(wordName)
        val needToLoadDetails = wordDetails == null
        val needToLoadRelatedWords = needToLoadDetails && wordDetails?.second == null
        val pair = if (!needToLoadDetails) {
            wordDetails!!
        } else if (needToLoadDetails && needToLoadRelatedWords) {
            try {
                Pair<WordDetailsResponse, RelatedWordsResponse?>(
                    dataSource.getWordInfo(wordName).await(),
                    awaitForRelatedWordsCall(wordName)
                )
            } catch (e: Exception) {
                return errorResult
            }
        } else {
            Pair<WordDetailsResponse, RelatedWordsResponse?>(
                wordDetails!!.first,
                awaitForRelatedWordsCall(wordName)
            )
        }
        wordsCache.put(wordName, pair)
        val detailWordInfo = mapper.mapToDetailWordInfo(pair.first, pair.second)
        return if (detailWordInfo.isEmpty())
            errorResult
        else Result.Success(detailWordInfo)
    }

    private suspend fun awaitForRelatedWordsCall(wordName: String): RelatedWordsResponse {
        return try {
            dataSource.getRelatedWords(wordName).await()
        } catch (e: Exception) {
            RelatedWordsResponse()
        }
    }

    private fun DetailWordInfo.isEmpty() = meanings.isEmpty()
            && notes?.isEmpty() == true
            && synonyms?.isEmpty() == true
            && antonyms?.isEmpty() == true

}