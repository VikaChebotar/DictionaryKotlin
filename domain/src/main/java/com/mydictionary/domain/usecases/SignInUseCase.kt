package com.mydictionary.domain.usecases

import com.mydictionary.domain.entity.User
import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.usecases.base.SingleUseCaseWithParameter
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class SignInUseCase @Inject constructor(
    val userRepository: UserRepository,
    @Named("executor_thread") val executorThread: Scheduler,
    @Named("postExecutionThread") val postExecutionThread: Scheduler
) :
    SingleUseCaseWithParameter<String?, User> {

    override fun execute(parameter: String?): Single<User> =
        userRepository.getUser()
            .onErrorResumeNext { userRepository.signIn(parameter!!) }
            .subscribeOn(executorThread)
            .observeOn(postExecutionThread)
}