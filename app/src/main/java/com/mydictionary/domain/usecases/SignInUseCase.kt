package com.mydictionary.domain.usecases

import com.mydictionary.domain.entity.User
import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.usecases.base.SingleUseCaseWithParameter
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignInUseCase @Inject constructor(val userRepository: UserRepository) :
    SingleUseCaseWithParameter<String?, User> {

    override fun execute(parameter: String?): Single<User> =
        Single.just(parameter)
            .flatMap {
                userRepository.getUser()
                    .onErrorResumeNext { userRepository.signIn(parameter!!) }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}