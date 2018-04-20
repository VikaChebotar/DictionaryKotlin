package com.mydictionary.domain.usecases

import com.mydictionary.commons.MAX_HISTORY_LIMIT
import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.repository.UserWordRepository
import com.mydictionary.domain.usecases.base.SingleUseCase
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShowUserHistoryUseCase @Inject constructor(
    val userRepository: UserRepository,
    val userWordRepository: UserWordRepository
) :
    SingleUseCase<List<String>> {

    override fun execute(): Single<List<String>> {
        return userRepository.getUser()
            .flatMap {
                userWordRepository.getUserWords(0, MAX_HISTORY_LIMIT)
                    .map {
                        it.list.map { it.word }
                    }
            }
            .onErrorReturn { emptyList() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}