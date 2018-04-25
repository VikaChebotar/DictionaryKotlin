package com.mydictionary.domain.usecases

import com.mydictionary.domain.entity.User
import com.mydictionary.domain.repository.UserRepository
import com.mydictionary.domain.usecases.base.SuspendUseCaseWithResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShowUserUseCase @Inject constructor(
    val userRepository: UserRepository
) : SuspendUseCaseWithResult<User> {

    override suspend fun execute() = userRepository.getUser()

}