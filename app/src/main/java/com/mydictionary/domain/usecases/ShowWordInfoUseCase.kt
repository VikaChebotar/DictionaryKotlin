package com.mydictionary.domain.usecases

import com.mydictionary.domain.entity.DetailWordInfo
import com.mydictionary.domain.entity.UserWord
import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.repository.UserWordRepository
import com.mydictionary.domain.repository.WordRepository
import com.mydictionary.domain.usecases.base.SingleUseCaseWithParameter
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShowWordInfoUseCase @Inject constructor(
    val wordRepository: WordRepository,
    val userWordRepository: UserWordRepository,
    val userRepository: UserRepository
) :
    SingleUseCaseWithParameter<String, ShowWordInfoUseCase.Result> {
    override fun execute(parameter: String): Single<Result> =
        wordRepository.getWordInfo(parameter)
            .map { Result(it, null) }
            .flatMap { result ->
                userRepository.getUser().flatMap {
                    userWordRepository.getUserWord(parameter).singleOrError()
                        .doOnEvent { t1, t2 ->
                            val userWord = t1?.let { t1 } ?: UserWord(parameter)
                            userWordRepository.addOrUpdateUserWord(userWord)
                        }
                        .map {
                            Result(result.wordInfo, it)
                        }
                }.onErrorResumeNext { Single.just(result) }
            }
    

    data class Result(val wordInfo: DetailWordInfo, val userWord: UserWord?)
}