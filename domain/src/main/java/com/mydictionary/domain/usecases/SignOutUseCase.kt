package com.mydictionary.domain.usecases

import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.usecases.base.CompletableUseCase
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class SignOutUseCase @Inject constructor(val userRepository: UserRepository,
                                         @Named("executor_thread") val executorThread: Scheduler,
                                         @Named("ui_thread") val uiThread: Scheduler) :
    CompletableUseCase {

    override fun execute() =
        userRepository.signOut()
            .subscribeOn(executorThread)
            .observeOn(uiThread)
}