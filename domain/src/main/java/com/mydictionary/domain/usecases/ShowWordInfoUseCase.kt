package com.mydictionary.domain.usecases

import com.mydictionary.domain.entity.DetailWordInfo
import com.mydictionary.domain.entity.UserWord
import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.repository.UserWordRepository
import com.mydictionary.domain.repository.WordRepository
import com.mydictionary.domain.usecases.base.UseCaseWithParameter
import io.reactivex.Observable
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ShowWordInfoUseCase @Inject constructor(
        val wordRepository: WordRepository,
        val userWordRepository: UserWordRepository,
        val userRepository: UserRepository,
        @Named("executor_thread") val executorThread: Scheduler,
        @Named("postExecutionThread") val postExecutionThread: Scheduler
) : UseCaseWithParameter<String, ShowWordInfoUseCase.Result> {

    override fun execute(parameter: String): Observable<Result> =
            wordRepository.getWordInfo(parameter)
                    .toObservable()
                    .map {
                        Result(it, null)
                    }
                    .flatMap { result ->
                        userRepository.getUser()
                                .toObservable()
                                .flatMap {
                                    userWordRepository.getUserWord(parameter)
                                            .onErrorReturn { UserWord(parameter) }
                                            .flatMap {
                                                userWordRepository.addOrUpdateUserWord(it)
                                                        .andThen(Observable.just(it))
                                            }
                                            .map {
                                                Result(result.wordInfo, it)
                                            }
                                }
                                .onErrorReturn { result }
                    }.subscribeOn(executorThread)
                    .observeOn(postExecutionThread)


    data class Result(val wordInfo: DetailWordInfo, val userWord: UserWord?)
}