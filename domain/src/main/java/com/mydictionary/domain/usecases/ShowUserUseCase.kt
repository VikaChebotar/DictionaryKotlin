package com.mydictionary.domain.usecases

import com.mydictionary.domain.entity.User
import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.usecases.base.SingleUseCase
import io.reactivex.Scheduler
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class ShowUserUseCase @Inject constructor(
        val userRepository: UserRepository,
        @Named("executor_thread") val executorThread: Scheduler,
        @Named("postExecutionThread") val postExecutionThread: Scheduler
) : SingleUseCase<User> {

    override fun execute() = userRepository.getUser()
        .subscribeOn(executorThread)
        .observeOn(postExecutionThread)
}