package com.mydictionary.domain.usecases

import com.mydictionary.domain.entity.DetailWordInfo
import com.mydictionary.domain.entity.UserWord
import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.repository.UserWordRepository
import com.mydictionary.domain.repository.WordRepository
import com.mydictionary.domain.usecases.base.UseCaseWithParameter
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShowWordInfoUseCase @Inject constructor(
        val wordRepository: WordRepository,
        val userWordRepository: UserWordRepository,
        val userRepository: UserRepository
) : UseCaseWithParameter<String, ShowWordInfoUseCase.Result> {

    override fun execute(parameter: String): Flowable<Result> =
            wordRepository.getWordInfo(parameter)
                    .toFlowable()
                    .map {
                        Result(it, null)
                    }
                    .flatMap { result ->
                        userRepository.getUser()
                                .toFlowable()
                                .flatMap {
                                    userWordRepository.getUserWord(parameter)
                                            .onErrorReturn { UserWord(parameter) }
                                            .flatMap {
                                                userWordRepository.addOrUpdateUserWord(it)
                                                        .andThen(Flowable.just(it))
                                            }
                                            .map {
                                                Result(result.wordInfo, it)
                                            }
                                }
                                .onErrorReturn { result }
                    }.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())


    data class Result(val wordInfo: DetailWordInfo, val userWord: UserWord?)
}