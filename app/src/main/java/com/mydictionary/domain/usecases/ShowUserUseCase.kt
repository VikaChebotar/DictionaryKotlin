package com.mydictionary.domain.usecases

import com.mydictionary.domain.entity.User
import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.usecases.base.SingleUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShowUserUseCase @Inject constructor(
    val userRepository: UserRepository
) : SingleUseCase<User> {

    override fun execute() = userRepository.getUser()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
}