package com.mydictionary.domain.usecases

import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.usecases.base.CompletableUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignOutUseCase @Inject constructor(val userRepository: UserRepository) :
    CompletableUseCase {

    override fun execute() =
        userRepository.signOut()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}